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
package com.moss.appsnap.keeper.firstaid;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.keeper.DesktopAppHandler;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.HandyTool;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.KeeperQuery;
import com.moss.appsnap.keeper.data.ResolvedJavaLaunchSpec;
import com.moss.appsnap.keeper.data.jaxbstore.ValueScanner;
import com.moss.identity.tools.IdProover;

/*
 * FIRST AID
 *  The main point of the first-aid is to launch/resurrect/reinstall a downed keeper.
 *  
 *   1) Try to re-launch the existing keeper.
 *    if not working
 *   2) Reinstall it
 *   
 *  A secondary point is to fix all the launch shortcuts
 */
public class FirstAidTool {
	private final Log log = LogFactory.getLog(getClass());
	private final Guts guts;
	private final PublicationId keeperPublication;
	private final IdProover idProver;
	
	public FirstAidTool(Guts guts, PublicationId keeperPublication,
			IdProover idProver) {
		super();
		this.guts = guts;
		this.keeperPublication = keeperPublication;
		this.idProver = idProver;
	}

	public void run() throws Exception{
		fixKeeper();
		guts.desktop.installControlPanel();
		fixAllLaunchScripts();
	}
	
	public void fixKeeper() throws Exception {
		KeeperQuery search = new KeeperQuery();
		guts.data.installs.scan(new KeeperQuery());
		if(search.results().size()>1){
			throw new RuntimeException("There is more than one piece of keeper software installed on this machine!");
		} else if(search.results().size()==0){
			// THIS IS TOTALLY HOSED - WE NEED TO INSTALL FROM SCRATCH
			log.info("No keeper installed - installing one from scratch.");
			new HandyTool(guts).installKeeper(keeperPublication, idProver);
		} else {
			InstallationInfo keeperInstall = search.results().get(0);
			guts.desktop.appHandlerFor(keeperInstall).launch();

			// WAIT UP TO 20 SECONDS FOR THE KEEPER TO LAUNCH
			final long start = System.currentTimeMillis();
			while(!guts.desktop.keeperIsRunning() && ((System.currentTimeMillis()-start)>20000)){
				log.info("Waiting for keeper to start");
				Thread.sleep(200);
			}
			
			if(!guts.desktop.keeperIsRunning()){
				log.info("This just isn't working - let's try installing from scratch");
				new HandyTool(guts).installKeeper(keeperPublication, idProver);
			}
		}
	}
	
	
	
	public void fixAllLaunchScripts(){
		guts.data.installs.scan(new ValueScanner<InstallationInfo>() {
			public boolean scan(InstallationInfo value) {
				try {
					ResolvedJavaLaunchSpec launch = guts.data.javaLaunchSpecs.get(value.currentVersion());
					DesktopAppHandler appHandler = guts.desktop.appHandlerFor(value);
					appHandler.handleInstall(value.isKeeperSoftware(), launch);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return true;
			}
		});
	}
}
