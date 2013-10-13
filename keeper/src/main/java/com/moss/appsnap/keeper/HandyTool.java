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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appkeep.tools.cache.AppkeepComponentCache.CacheResolution;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.AppkeepDownloadVector;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installables.JavaAppInstallableDetails;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.InstallRegistrationResponse;
import com.moss.appsnap.api.installs.KeeperCommands;
import com.moss.appsnap.api.installs.KeeperRegistrationResponse;
import com.moss.appsnap.api.installs.commands.InstallCommand;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.KeeperConfig;
import com.moss.appsnap.keeper.data.ResolvedComponent;
import com.moss.appsnap.keeper.data.ResolvedJavaLaunchSpec;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.launch.components.Component;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.launch.spec.app.AppProfile;
import com.moss.launch.spec.app.bundle.BundleSpec;

public class HandyTool {
	private final Log log = LogFactory.getLog(getClass());
	private final Guts guts;

	public HandyTool(Guts guts) {
		super();
		this.guts = guts;
	}

	private static class DownloadFailureInfo {
		private final Throwable error;
		private final Url keepLocation;
		
		public DownloadFailureInfo(Throwable error, Url keepLocation) {
			super();
			this.error = error;
			this.keepLocation = keepLocation;
		}
	}
	
	public ResolvedJavaLaunchSpec resolveJavaLaunch(InstallationPlan plan){
//		final List<Component> components = plan.details().accept(new InstallableDetailsVisitor<List<Component>>() {
//			public List<Component> visit(JavaAppInstallableDetails details) {
//				
//				return components;
//			}
//		});
		
		final JavaAppInstallableDetails details = JavaAppInstallableDetails.grab(plan.details());
		
		final List<Component> components = new LinkedList<Component>();
		
		JavaAppSpec spec = details.launchSpec();
		
		components.addAll(spec.components());
		
		for(BundleSpec bundle : spec.bundles()){
			components.addAll(bundle.components());
		}
		
		for(AppProfile p : spec.profiles()){
			components.addAll(p.components());
		}
		
		final List<LocatableDownloadSource> connectors = new LinkedList<LocatableDownloadSource>();

		for(final AppkeepDownloadVector v : plan.componentSources()){
			connectors.add(new LocatableDownloadSource(v, guts.proxyFactory));
		}

		final List<ResolvedComponent> componentResolutions = new ArrayList<ResolvedComponent>(components.size());
		for(Component next : components){
			boolean downloadSucceeded = false;
			List<DownloadFailureInfo> errors = new LinkedList<DownloadFailureInfo>();
			for(LocatableDownloadSource source : connectors){
				try {
					CacheResolution resolution = guts.data.componentsCache.resolve(next, source);
					componentResolutions.add(new ResolvedComponent(resolution.componentId, next));
					downloadSucceeded = true;
				} catch (Throwable e) {
					e.printStackTrace();
					errors.add(new DownloadFailureInfo(e, source.location()));
				}
			}
			
			if(!downloadSucceeded){
				final StringBuilder text = new StringBuilder("Unable to download component:\n");
				text.append(next.toString());
				
				for(DownloadFailureInfo failure : errors){
					text.append("\n  Error at ");
					text.append(failure.keepLocation.toString());
					text.append('\n');
					printStackTrace(failure.error, text);
				}
				throw new RuntimeException(text.toString());
			}
		}
		
		final ResolvedJavaLaunchSpec launch = new ResolvedJavaLaunchSpec(plan.id(), details.launchSpec(), plan.componentResolutions());
		guts.data.javaLaunchSpecs.put(launch.id(), launch);
		return launch;
	}

	private static void printStackTrace(Throwable t, StringBuilder text){
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream s = new PrintStream(out);
			t.printStackTrace(s);
			s.flush();
			out.close();
			text.append(new String(out.toByteArray()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DesktopAppHandler handlerForInstall(InstallId id) {
		InstallationInfo info = guts.data.installs.get(id);
		if(info==null){
			throw new RuntimeException("No such install: " + id);
		}
		return guts.desktop.appHandlerFor(info);
	}
	
	public void sendErrorReport(Throwable e) {
		e.printStackTrace();
	}
	
	public KeeperCommands executeInstall(InstallCommand i, boolean daemon) throws SecurityException {
		final KeeperConfig config = guts.data.config.get();
		final InstallationInfo info = guts.data.installs.get(i.install());
		
		final InstallationPlan plan = guts.snap.getInstallableForKeeper(i.version(), i.id(), config.id(), config.token());
		final ResolvedJavaLaunchSpec spec = new HandyTool(guts).resolveJavaLaunch(plan);
		guts.data.javaLaunchSpecs.put(spec.id(), spec);
		
		final DesktopAppHandler handler = guts.desktop.appHandlerFor(info);
		handler.handleInstall(daemon, spec);
		return guts.snap.keeperQueuePoll(config.id(), i.id(), config.token());
	}
	
	public InstallationInfo registerInstall(String name, boolean isKeeperSoftware, PublicationId p, IdProover idProver) throws SecurityException, IdProovingException {
		final KeeperConfig config = guts.data.config.get();
		
		InstallRegistrationResponse iR = guts.snap.registerInstall(p, config.id(), config.token(), idProver.giveProof());
		
		final InstallationInfo info = new InstallationInfo(iR.installId(), isKeeperSoftware, p, name);
		guts.data.installs.put(info.id(), info);
		
		return info;
	}
	
	public void installKeeper(final PublicationId keeperPublication, final IdProover idProver) throws Exception {
		
		// REGISTER KEEPER WITH APPSNAP
		final KeeperRegistrationResponse r = guts.snap.registerKeeper(keeperPublication, idProver.giveProof());
		final KeeperConfig config;
		
		{// WRITE KEEPER CONFIGURATION
			config = new KeeperConfig();
			config.setId(r.keeper());
			config.setToken(r.token());
			guts.data.config.put(config);
		}
		
		final InstallationInfo keeperInstallInfo = new InstallationInfo(r.installResponse().installId(), true, keeperPublication, "keeper");
		guts.data.installs.put(keeperInstallInfo.id(), keeperInstallInfo);
		
		// THIS SHOULD CAUSE ALL THE INSTALLS TO BE DONE, ETC
		new Poller(guts, false).poll();
		
		// LAUNCH THE KEEPER & APP
		guts.desktop.appHandlerFor(keeperInstallInfo).launch();
		
		// WAIT FOR THE KEEPER TO LAUNCH
		while(!guts.desktop.keeperIsRunning()){
			log.info("Waiting for keeper to start");
			Thread.sleep(200);
		}
	}
	
	public void uninstall(final InstallId id){
		InstallationInfo info = guts.data.installs.get(id);
		if(info!=null){
			guts.desktop.appHandlerFor(info).uninstall();
			guts.data.uninstalls.put(id, info);
			guts.data.installs.delete(id);
		}
	}
}
