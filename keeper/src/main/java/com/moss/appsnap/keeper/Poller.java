/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of appsnap.
 *
 * appsnap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * appsnap is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appsnap; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.appsnap.keeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.AuthorizationToken;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installs.Command;
import com.moss.appsnap.api.installs.CommandId;
import com.moss.appsnap.api.installs.CommandVisitor;
import com.moss.appsnap.api.installs.KeeperCommands;
import com.moss.appsnap.api.installs.KeeperId;
import com.moss.appsnap.api.installs.commands.InstallCommand;
import com.moss.appsnap.api.installs.commands.UninstallCommand;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.KeeperConfig;
import com.moss.appsnap.keeper.data.ResolvedJavaLaunchSpec;

public class Poller implements Runnable {
	private final Log log = LogFactory.getLog(getClass());
	private final Guts guts;
	private final HandyTool tool;
	private final boolean doRestarts;
	
	public Poller(Guts guts, boolean doRestarts) {
		super();
		this.guts = guts;
		this.tool = new HandyTool(guts);
		this.doRestarts = doRestarts;
	}

	public void run() {
		while (true) {
			try {
				poll();
			} catch (Throwable e) {
				tool.sendErrorReport(e);
			}
			
			try{
				Thread.sleep(20000);// pause 20 seconds.
			} catch (Throwable e) {
				tool.sendErrorReport(e);
			}

		}
	};

	public synchronized void poll() throws SecurityException {
		log.info("Polling");
		CommandId lastCommand = null;
		AuthorizationToken token = guts.data.config.get().token();
		KeeperId id = guts.data.config.get().id();
		for (
				KeeperCommands q = guts.snap.getKeeperQueue(id, token); 
				q.length() > 0; 
				q = guts.snap.keeperQueuePoll(id, lastCommand, token)) {
			log.info("Queue has " + q.length() + " commands.");
			Command c = q.earliest();
			execute(c);
			lastCommand = c.id();
		}
		log.info("Poll Complete");
	}
	
	private void executeInstallCommand(InstallCommand c){
		try {
			final KeeperConfig config = guts.data.config.get();
			final InstallationInfo i = guts.data.installs.get(c.install());
			final DesktopAppHandler desktopHandler = tool.handlerForInstall(c.install());
			final InstallationPlan plan = guts.snap.getInstallableForKeeper(c.version(), c.id(), config.id(), config.token());
			
			ResolvedJavaLaunchSpec launch = new HandyTool(guts).resolveJavaLaunch(plan);
			
			desktopHandler.handleInstall(i.isKeeperSoftware(), launch);
			
			i.setCurrentVersion(launch.id());
			guts.data.installs.put(i.id(), i);
			
			if(i.isKeeperSoftware() && doRestarts){
				guts.snap.keeperQueuePoll(guts.data.config.get().id(), c.id(), guts.data.config.get().token());
				log.info("Restarting due to keeper update");
				guts.desktop.restart();
			}
			
			
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void executeInstallCommand(UninstallCommand c){
		final DesktopAppHandler h = tool.handlerForInstall(c.install());
		h.uninstall();
	}
		
	private void execute(final Command c){
		log.info("Executing command " + c.id() + " (" + c.getClass().getSimpleName() + ")");
		c.accept(new CommandVisitor<Void>() {
			
			public Void visit(InstallCommand c) {
				executeInstallCommand(c);
				
				return null;
			}
			
			public Void visit(UninstallCommand c) {
				
				executeInstallCommand(c);
				
				return null;
			}
			
		});
	}
	
	public void start(){
		Thread t = new Thread(this);
		t.setName("Polling thread");
		t.start();
	}
}
