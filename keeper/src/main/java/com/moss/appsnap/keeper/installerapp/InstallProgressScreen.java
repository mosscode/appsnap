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
package com.moss.appsnap.keeper.installerapp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.KeeperRegistrationResponse;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.keeper.Bootstrapper;
import com.moss.appsnap.keeper.DesktopIntegrationStrategy;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.HandyTool;
import com.moss.appsnap.keeper.Poller;
import com.moss.appsnap.keeper.data.Data;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.KeeperConfig;
import com.moss.appsnap.keeper.socketapi.ApiMessageConnection;
import com.moss.appsnap.keeper.socketapi.InstallFunction;
import com.moss.appsnap.keeper.socketapi.InstallFunction.Outcome;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.identity.tools.IdProover;
import com.moss.rpcutil.proxy.ProxyFactory;

public class InstallProgressScreen extends AbstractScreen<InstallProgressScreenView>{
	private final Log log = LogFactory.getLog(getClass());
	private final AppsnapService snap;
	private final IdProover idProver;
	private final PublicationInfo pub;
	private final InstallerParams params;
	private final ProxyFactory proxyFactory;
	
	public InstallProgressScreen(final InstallerParams params, final AppsnapService snap, final IdProover idProver, final PublicationInfo pub, final ProxyFactory proxyFactory) {
		super("Installing", new InstallProgressScreenView());
		setNotablePathMilestone(false);
		
		this.params = params;
		this.snap = snap;
		this.idProver = idProver;
		this.pub = pub;
		this.proxyFactory = proxyFactory;
	}
	
	
	@Override
	public void shown() {
		new Thread(){
			public void run() {
				try {
					
					DesktopIntegrationStrategy desktop = new Bootstrapper().discover();
					File dataDir = desktop.keeperDataDir(params.serviceId);
					
					boolean needsKeeperInstallToo;
					
					if(!dataDir.exists() || dataDir.list().length==0){
						// THIS IS A VIRGIN MACHINE - WE NEED TO INSTALL THE KEEPER
						needsKeeperInstallToo = true;
					}else{
						needsKeeperInstallToo = false;
					}
					
					// CREATE AND LOAD-UP THE KEEPER GUTS IN THIS PROCESS
					if(!dataDir.exists() && !dataDir.mkdirs()){
						throw new IOException("Could not create directory: " + dataDir.getAbsolutePath());
					}
					
					Data data = new Data(dataDir);
					Guts guts = new Guts(snap.serviceInfo(), new Url(params.url), proxyFactory, snap, data, desktop);
					desktop.noLongerGutless(guts);
					HandyTool tool = new HandyTool(guts);
					
//					if(!needsKeeperInstallToo && !desktop.keeperIsRunning()){
//						log.info("The keeper was installed but not running; redirecting user to first-aid");
//						//throw new RuntimeException("The keeper was installed but not running! We need to implement first aid, and then switch to it in this case");
//						environment.next(new FirstAidStartScreen(guts, params.keeperPublication, idProver, null));
//						return;
//					}
					
					if(needsKeeperInstallToo){
						
						// REGISTER KEEPER WITH APPSNAP
						final KeeperRegistrationResponse r = snap.registerKeeper(params.keeperPublication, idProver.giveProof());
						final KeeperConfig config;
						
						{// WRITE KEEPER CONFIGURATION
							config = new KeeperConfig();
							config.setId(r.keeper());
							config.setToken(r.token());
							guts.data.config.put(config);
						}
						
						final InstallationInfo keeperInstallInfo = new InstallationInfo(r.installResponse().installId(), true, params.keeperPublication, "keeper");
						guts.data.installs.put(keeperInstallInfo.id(), keeperInstallInfo);
						
						// THIS SHOULD CAUSE ALL THE INSTALLS TO BE DONE, ETC
						new Poller(guts, false).poll();
						
						guts.desktop.installControlPanel();
						
						// LAUNCH THE KEEPER AND WAIT FOR IT TO LAUNCH
						guts.desktop.appHandlerFor(guts.data.installs.get(keeperInstallInfo.id())).launch();
						
						while(!guts.desktop.keeperIsRunning()){
							log.info("Waiting for keeper to start");
							Thread.sleep(200);
						}
						
					}
					
					// INSTALL THE SELECTED PUBLICATION
					ApiMessageConnection socket = desktop.newMessageToHost();
					final InstallId id = tool.registerInstall(pub.name(), false, pub.id(), idProver).id();
					
					InstallFunction.Outcome result = InstallFunction.call(id, socket);
					
					if(result!=Outcome.OK){
						throw new RuntimeException("Error: " + result);
					}
					
					final InstallationInfo info = guts.data.installs.get(id);
					
					guts.desktop.appHandlerFor(info).launch();
					
				} catch (Throwable e) {
					environment.failCatastrophically(e);
					return;
				}
				environment.next(new InstallationLandingScreen());
			}
		}.start();

	}
}
