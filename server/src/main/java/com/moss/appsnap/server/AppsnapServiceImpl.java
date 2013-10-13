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
package com.moss.appsnap.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.moss.appkeep.api.AppkeepService;
import com.moss.appkeep.api.ComponentId;
import com.moss.appkeep.api.ComponentInfo;
import com.moss.appkeep.api.NoMatchingComponentException;
import com.moss.appkeep.api.endorse.JarsignEndorsement;
import com.moss.appkeep.api.endorse.x509.X509CertId;
import com.moss.appkeep.api.security.AnonDownloadToken;
import com.moss.appkeep.api.security.UserAccountDownloadToken;
import com.moss.appkeep.api.select.ComponentHandlesSelector;
import com.moss.appkeep.api.select.ComponentSelector;
import com.moss.appkeep.api.select.DirectComponentSelector;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.AppsnapServiceInfo;
import com.moss.appsnap.api.AuthorizationToken;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.apps.AppVersionSpec;
import com.moss.appsnap.api.apps.LaunchIntegrityException;
import com.moss.appsnap.api.catalog.GroupCatalog;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.AppkeepDownloadVector;
import com.moss.appsnap.api.installables.ComponentResolveTool;
import com.moss.appsnap.api.installables.InstallableDetails;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installs.Command;
import com.moss.appsnap.api.installs.CommandId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.InstallRegistrationResponse;
import com.moss.appsnap.api.installs.InstallationCommands;
import com.moss.appsnap.api.installs.KeeperCommands;
import com.moss.appsnap.api.installs.KeeperId;
import com.moss.appsnap.api.installs.KeeperRegistrationResponse;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.RegisterUserOutcome;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.security.UserInfo;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.api.streams.StreamUpdate;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.appsnap.server.config.ServerConfiguration;
import com.moss.appsnap.server.groups.Publication;
import com.moss.appsnap.server.groups.StoredGroup;
import com.moss.appsnap.server.installables.Installable;
import com.moss.appsnap.server.installables.InstallableVisitor;
import com.moss.appsnap.server.installables.StoredApp;
import com.moss.appsnap.server.installables.StoredAppVersionSeries;
import com.moss.appsnap.server.installables.javaapplets.JavaAppletInstallable;
import com.moss.appsnap.server.installables.javaapps.JavaAppInstallable;
import com.moss.appsnap.server.installs.KeeperRegistration;
import com.moss.appsnap.server.installs.StoredCommand;
import com.moss.appsnap.server.installs.StoredInstall;
import com.moss.appsnap.server.installs.commands.StoredInstallCommand;
import com.moss.appsnap.server.installs.commands.StoredUninstallCommand;
import com.moss.appsnap.server.security.UserAccount;
import com.moss.appsnap.server.streams.StoredStream;
import com.moss.appsnap.server.streams.StoredStreamUpdate;
import com.moss.appsnap.server.streams.StreamAppStatus;
import com.moss.bdbwrap.SearchVisitor;
import com.moss.bdbwrap.ValueScanner;
import com.moss.bdbwrap.bdbsession.WorkAtom;
import com.moss.identity.Id;
import com.moss.identity.IdProof;
import com.moss.identity.IdProofCheckRecipe;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.identity.tools.IdTool;
import com.moss.launch.components.Component;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.launch.spec.JavaAppletSpec;
import com.moss.launch.spec.LaunchSpecValidationException;
import com.moss.launch.spec.app.AppProfile;
import com.moss.launch.spec.app.bundle.BundleSpec;
import com.moss.launch.spec.applet.AppletProfile;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;

public class AppsnapServiceImpl implements AppsnapService {
	private final Log log = LogFactory.getLog(getClass());
	
	private final Data data;
	private final ServerConfiguration config;
	private final IdTool idtool;
	private final IdProover idProver;
	private final ProxyFactory proxies;
	private final Duration downloadGrantDuration = new Duration(1000*60*30);// 30 min
	
	public AppsnapServiceImpl(Data data, ServerConfiguration config, IdTool idTool, IdProover idProver, ProxyFactory proxies) {
		super();
		this.data = data;
		this.config = config;
		this.idtool = idTool;
		this.idProver = idProver;
		this.proxies = proxies;
	}
	
	public AppsnapServiceInfo serviceInfo() {
		return new AppsnapServiceInfo(config.serviceId(), config.serviceName());
	}
	
	public List<Url> listKeepers(IdProof credentials) throws SecurityException {
		return config.keepLocations();
	}
	public List<GroupCatalog> getSoftwareCatalog(IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		final List<GroupCatalog> results = new LinkedList<GroupCatalog>();
		
		data.groups.scan(
				new ValueScanner<StoredGroup>() {
					public void inspect(StoredGroup next) {
						if(user.isAdministrator() || next.isAdministrator(user.id()) || next.isMember(user.id())){
							results.add(next.toCatalogDto());
						}
					}
				},
				null
			);
		
		return results;
	}
	
	public RegisterUserOutcome registerUser(IdProofCheckRecipe identification) throws SecurityException {
		
		if(data.userAccounts.get(identification.id(), null, LockMode.READ_COMMITTED)!=null){
			return RegisterUserOutcome.LOGIN_ALREADY_EXISTS;
		}else{
			final UserAccount account = new UserAccount(identification);
			
			data.userAccounts.put(account.id(), account, null);
			return RegisterUserOutcome.SUCCEEDED;
		}
	}
	
	public UserInfo myAccount(IdProof credentials) throws SecurityException {
		return findAssertAndAuthenticateUser(credentials).toDto();
	}
	
	private void assertGroupAdministrator(StoredGroup group, UserAccount user) throws SecurityException {
		if(!user.isAdministrator() && !group.isAdministrator(user.id())){
			throw new SecurityException("User " + user.id() + " is not an administrator of group " + group.name() + " (" + group.id() + ")");
		}
	}
	private void assertGroupMember(StoredGroup group, UserAccount user) throws SecurityException {
		if(!user.isAdministrator() && !group.isMember(user.id())){
			throw new SecurityException("User " + user.id() + " is not an member of group " + group.name() + " (" + group.id() + ")");
		}
	}
	private UserAccount findAssertAndAuthenticateUser(IdProof credentials) throws SecurityException {
		try {
			final Id id = idtool.getVerifier(credentials).id();
			UserAccount user = data.userAccounts.get(id, null, LockMode.READ_COMMITTED);
			if(user==null){
				throw new SecurityException("No such user: " + id);
			}else{
				return user;
			}
		} catch (IdProovingException e) {
			throw new SecurityException("Authentication error: " + e.getMessage(), e);
		}
		
	}
	
	private UserAccount findAssertAndAuthenticateAdministrator(IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		if(!user.isAdministrator()){
			throw new SecurityException(user.id() + " does not have administrative privileges");
		}
		return user;
	}
	
	
	private KeeperRegistration findAssertAndAuthenticateKeeper(KeeperId keeperId, AuthorizationToken token) throws SecurityException {
		final KeeperRegistration keeper = data.keepers.get(keeperId, null, LockMode.READ_COMMITTED);
		
		if(keeper==null){
			throw new NullPointerException("No such keeper: " + keeperId);
		}
		
		if(!keeper.token().equals(token)){
			throw new SecurityException("Bad keeper token");
		}
		
		return keeper;
	}
	
	private List<Component> allComponents(JavaAppSpec launchSpec){
		final List<Component> components = new LinkedList<Component>();
		
		{
			components.addAll(launchSpec.components());
			
			for(BundleSpec b : launchSpec.bundles()){
				components.addAll(b.components());
			}
			
			for(AppProfile p : launchSpec.profiles()){
				components.addAll(p.components());
			}
		}
		return components;
	}
	
	private List<ComponentSelector> select(List<Component> components){
		final List<ComponentSelector> selectors = new ArrayList<ComponentSelector>(components.size());
		for (Component component : components) {
			selectors.add(new ComponentHandlesSelector(component.artifactHandles()));
		}
		
		return selectors;
	}
	
	private List<ComponentSelector> selectAllComponents(JavaAppSpec launchSpec){
		return select(allComponents(launchSpec));
	}
	
	private List<ComponentSelector> selectAllComponents(JavaAppletSpec launchSpec){

		final List<Component> components = new LinkedList<Component>();
		
		{
			components.addAll(launchSpec.components());
			
			
			for(AppletProfile p : launchSpec.profiles()){
				components.addAll(p.components());
			}
		}
		
		final List<ComponentSelector> selectors = new ArrayList<ComponentSelector>(components.size());
		for (Component component : components) {
			selectors.add(new ComponentHandlesSelector(component.artifactHandles()));
		}
		
		return selectors;
	}
	/*|------------------------------------------------------|
	 *|           APPLICATIONS MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	
	
	private void validateLaunchSpec(JavaAppletSpec launchSpec) throws LaunchIntegrityException {
		// MAKE SURE THE LAUNCH SPEC ISN'T TOTAL TRASH
		try {
			launchSpec.validate();
		} catch (LaunchSpecValidationException e) {
			throw new LaunchIntegrityException(e);
		}
	}
	private void validateLaunchSpec(JavaAppSpec launchSpec) throws LaunchIntegrityException {
		// MAKE SURE THE LAUNCH SPEC ISN'T TOTAL TRASH
		try {
			launchSpec.validate();
		} catch (LaunchSpecValidationException e) {
			throw new LaunchIntegrityException(e);
		}
		
	}
	
	private List<ComponentId> resolveComponents(List<Component> everything) throws LaunchIntegrityException {
		try {
			final List<ComponentId> resolutions = new ArrayList<ComponentId>(everything.size());
			AppkeepService keep = proxies.create(AppkeepService.class, config.keepLocations().get(0).toString());
			
			List<ComponentInfo> infos = keep.getInfos(select(everything), new UserAccountDownloadToken(idProver.giveProof()));
			
			for(int x=0;x<everything.size();x++){
				ComponentInfo info = infos.get(x);
				if(info==null){
					throw new LaunchIntegrityException("Component is missing from keep: " + everything.get(x));
				}
				resolutions.add(infos.get(x).id());
			}
			
			return resolutions;
		} catch (com.moss.appkeep.api.security.SecurityException e) {
			throw new RuntimeException("Error resolving components", e);
		} catch (IdProovingException e){
			throw new RuntimeException("Error resolving components", e);
		}
	}
	
	public InstallableId addJavaAppletVersion(AppId application,
			String seriesName, String versionLabel, JavaAppletSpec launchSpec,
			X509CertId jarsignCertificate, IdProof credentials)
			throws LaunchIntegrityException, SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		validateLaunchSpec(launchSpec);
		
		final StoredApp app = data.apps.get(application, null, LockMode.READ_COMMITTED);
		
		if(app==null){
			throw new RuntimeException("No such app: " + application);
		}else{
			
			final JavaAppletInstallable i = new JavaAppletInstallable(
							InstallableId.random(), 
							versionLabel, 
							launchSpec, 
							jarsignCertificate,
							resolveComponents(ComponentResolveTool.allComponentsInResolutionOrder(launchSpec))
						);
			

			doAppletInstallableAutoStuff(i, jarsignCertificate);
			
			// PATH MAINTAINANCE
			{
				final StoredAppVersionSeries path;
				if(seriesName==null){
					path = app.defaultSeries();
				} else if(app.hasSeries(seriesName)){
					path = app.series(seriesName);
				}else{
					path = app.newSeries(seriesName);
				}
				path.append(i.id());
			}
			
			new WorkAtom(data) {
				@Override
				protected void doWork(Transaction tx) throws Exception {
					data.apps.put(app.id(), app, tx);
					data.installables.put(i.id(), i, tx);
				}
			}.run();
			
			return i.id();
		}
	}
	
	public InstallableId addJavaAppVersion(AppId application, final String seriesName, String versionLabel, JavaAppSpec launchSpec, IdProof credentials) throws LaunchIntegrityException, SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		validateLaunchSpec(launchSpec);
		
		final StoredApp app = data.apps.get(application, null, LockMode.READ_COMMITTED);
		
		if(app==null){
			throw new RuntimeException("No such app: " + application);
		}else{
			
			final Installable i = new JavaAppInstallable(
							InstallableId.random(), 
							versionLabel, 
							launchSpec, 
							resolveComponents(allComponents(launchSpec))
						);
			
			
			// PATH MAINTAINANCE
			{
				final StoredAppVersionSeries path;
				if(seriesName==null){
					path = app.defaultSeries();
				} else if(app.hasSeries(seriesName)){
					path = app.series(seriesName);
				}else{
					path = app.newSeries(seriesName);
				}
				path.append(i.id());
			}
			
			new WorkAtom(data) {
				@Override
				protected void doWork(Transaction tx) throws Exception {
					data.apps.put(app.id(), app, tx);
					data.installables.put(i.id(), i, tx);
				}
			}.run();
			
			return i.id();
		}
	}
	
	public AppId createJavaApplication(String name, String initialVersionLabel, JavaAppSpec launchSpec, boolean isKeeperSoftware, IdProof credentials) throws LaunchIntegrityException, SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
				
		final StoredApp app = new StoredApp(AppId.random(), name, AppType.JAVA_APP, isKeeperSoftware);
		final StoredAppVersionSeries defaultPath = app.addDefaultPath();
		
		final Installable i;
		if(launchSpec!=null){
			validateLaunchSpec(launchSpec);
			
			i = new JavaAppInstallable(
					InstallableId.random(), 
					initialVersionLabel, 
					launchSpec, 
					resolveComponents(allComponents(launchSpec))
				);
			
			defaultPath.append(i.id());
		}else{
			i = null;
		}
		
		new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.apps.put(app.id(), app, tx);
				if(i!=null){
					data.installables.put(i.id(), i, tx);
				}
			}
		}.run();
		
		return app.id();
	}
	
	
	private void doAppletInstallableAutoStuff(JavaAppletInstallable i, X509CertId jarsignCertificate){
		if(jarsignCertificate!=null){
			AppkeepService keep = proxies.create(AppkeepService.class, config.keepLocations().get(0).toString());
			List<ComponentSelector> selectors = new LinkedList<ComponentSelector>();
			for(ComponentId id : i.componentResolutions()){
				selectors.add(new DirectComponentSelector(id));
			}
			keep.endorse(
					selectors.toArray(new ComponentSelector[selectors.size()]),
					new JarsignEndorsement(jarsignCertificate)
				);
			
			try {
				keep.grantWorldAccess(selectors, idProver.giveProof());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public AppId createJavaApplet(String name, String initialVersionLabel, JavaAppletSpec launchSpec, X509CertId jarsignCertificate, IdProof credentials)
			throws LaunchIntegrityException, SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
				
		final StoredApp app = new StoredApp(AppId.random(), name, AppType.JAVA_APPLET, false);
		final StoredAppVersionSeries defaultPath = app.addDefaultPath();
		
		final JavaAppletInstallable i;
		if(launchSpec!=null){
			validateLaunchSpec(launchSpec);
			
			
			i = new JavaAppletInstallable(
					InstallableId.random(), 
					initialVersionLabel, 
					launchSpec,
					jarsignCertificate,
					resolveComponents(ComponentResolveTool.allComponentsInResolutionOrder(launchSpec))
				);
			
			doAppletInstallableAutoStuff(i, jarsignCertificate);
			
			defaultPath.append(i.id());
		}else{
			i = null;
		}
		
		new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.apps.put(app.id(), app, tx);
				if(i!=null){
					data.installables.put(i.id(), i, tx);
				}
			}
		}.run();
		
		return app.id();
	}
	
	public AppDetails getAppDetails(AppId id, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		final StoredApp app = data.apps.get(id, null, LockMode.READ_COMMITTED);
		
		if(app==null){
			throw new RuntimeException("No such app: " + id);
		}else{
			return app.detailsDto(data.installables);
		}
	}
	
	public void renameApplication(AppId id, String name, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		final StoredApp app = data.apps.get(id, null, LockMode.READ_COMMITTED);
		
		if(app==null){
			throw new RuntimeException("No such app: " + id);
		}else{
			app.name(name);
			data.apps.put(app.id(), app, null);
		}
	}
	
	
	public List<AppInfo> listApps(IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		final List<AppInfo> infos = new LinkedList<AppInfo>();
		
		data.apps.scan(
				new ValueScanner<StoredApp>() {
					public void inspect(StoredApp next) {
						infos.add(next.infoDto());
					}
				},
				null
			);
		
		return infos;
	}
	
//	public InstallationPlan getInstallable(PublicationId id, KeeperId keeperId, AuthorizationToken token) throws SecurityException {
//		findAssertAndAuthenticateKeeper(keeperId, token);
//		
//		final StoredGroup group = data.groupsByPublication.get(id, null, LockMode.READ_COMMITTED);
//		if(group==null){
//			throw new RuntimeException("No such publication: " + id);
//		}
//		
//		final Publication p = group.publication(id);
//		if(p==null){
//			throw new RuntimeException("No such publication: " + id);
//		}
//		
//		final StoredStream stream = data.streams.get(p.stream(), null, LockMode.READ_COMMITTED);
//		
//		return fetchInstallationPlan(stream.currentStatus(p.app()).version());
//	}

	private InstallationPlan fetchInstallationPlan(InstallableId id){
		Installable i = data.installables.get(id, null, LockMode.DEFAULT);
		if(i==null){
			throw new RuntimeException("No such installable: " + id);
		}
		
//		final List<ComponentSelector> selectors = selectAllComponents(i.launchSpec());
		
		final List<ComponentSelector> selectors = i.accept(new InstallableVisitor<List<ComponentSelector>>() {
			public List<ComponentSelector> visit(JavaAppInstallable app) {
				return selectAllComponents(app.launchSpec());
			}
			public List<ComponentSelector> visit(JavaAppletInstallable applet) {
				return selectAllComponents(applet.launchSpec());
			}
		});
		
		
		List<AppkeepDownloadVector> vectors = new LinkedList<AppkeepDownloadVector>();
		
		for(Url next : config.keepLocations()){
			AppkeepService keep = proxies.create(AppkeepService.class, next.toString());
			try {
				AnonDownloadToken token = keep.grantAccess(selectors, downloadGrantDuration, idProver.giveProof());
				vectors.add(new AppkeepDownloadVector(next, token));
			} catch (com.moss.appkeep.api.security.SecurityException e) {
				log.error("Error talking to appkeep at " + next, e);
			} catch (NoMatchingComponentException e) {
				log.warn("Unable to find component at " + next, e);
			} catch (IdProovingException e){
				log.error("There was an error proving my identity", e);
			}
		}
		if(vectors.size()==0){
			throw new RuntimeException("No download vectors available for installable " + id + ".  See server logs for more details.");
		}
		
		return new InstallationPlan(i.id(), i.toDetailsDto(), vectors, i.componentResolutions());
	}
	

	public InstallationPlan getInstallable(InstallableId id, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		return fetchInstallationPlan(id);
	}
	
	public InstallableDetails getInstallableDetails(InstallableId id, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		Installable i = data.installables.get(id, null, LockMode.DEFAULT);
		if(i==null){
			throw new RuntimeException("No such installable: " + id);
		}
		return i.toDetailsDto();
	}
	
	public InstallationPlan getInstallableForKeeper(InstallableId id, CommandId commandId, KeeperId keeperId, AuthorizationToken keeperToken) throws SecurityException {
		final KeeperRegistration keeper = findAssertAndAuthenticateKeeper(keeperId, keeperToken);
		
		final StoredCommand command = data.commandsQueue.commands.get(commandId, null, LockMode.READ_COMMITTED);
		if(command==null){
			throw new SecurityException("Invalid parameters; Access denied");
		}
		
		if(!command.keeper().equals(keeper.id())){
			throw new SecurityException("Invalid parameters; Access denied");
		}
		
		if(!(command instanceof StoredInstallCommand)){
			throw new SecurityException("Invalid parameters; Access denied");
		}
		
		StoredInstallCommand installCommand = (StoredInstallCommand) command;
		
		if(!installCommand.version().equals(id)){
			throw new SecurityException("Invalid parameters; Access denied");
		}
		
		final StoredInstall install = data.installs.get(command.install(), null, LockMode.READ_COMMITTED);
		
		final StoredApp app = data.apps.get(install.app(), null, LockMode.READ_COMMITTED);
		return fetchInstallationPlan(id);
	}
	
	
	/*|------------------------------------------------------|
	 *|                  GROUP MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	 
	public List<GroupInfo> listGroups(IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		
		final List<GroupInfo> infos = new LinkedList<GroupInfo>();
		
		data.groups.scan(
				new ValueScanner<StoredGroup>() {
					public void inspect(StoredGroup next) {
						infos.add(next.toDto());
					};
				},
				null
			);
		
		return infos;
	}
	
	public GroupDetails getGroupDetails(GroupId id, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
				
		final StoredGroup group = data.groups.get(id, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such group: " + id);
		}else{
			assertGroupAdministrator(group, user);
			return group.detailsDto(data);
		}
	}
	
	public GroupId createGroup(String name, SecurityMode type, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredGroup group = new StoredGroup(GroupId.random(), name, type);
		
		group.addAdministrator(user.id());
		
		data.groups.put(group.id(), group, null);
		
		return group.id();
	}
	
	public PublicationId publishToGroup(String publicationName, String description, StreamId streamId, final AppId appId, GroupId groupId, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredStream stream = data.streams.get(streamId, null, LockMode.READ_COMMITTED);
		if(stream==null){
			throw new RuntimeException("No such stream: " + streamId);
		}
		
		final StoredApp app = data.apps.get(appId, null, LockMode.READ_UNCOMMITTED);
		if(app==null){
			throw new RuntimeException("No such app: " + appId);
		}
		
		if(stream.currentStatus(appId)==null){
			throw new RuntimeException("App " + appId + " is not available in stream " + streamId);
		}
		
		final StoredGroup group = data.groups.get(groupId, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such group: " + groupId);
		}else{
			assertGroupAdministrator(group, user);
			
			if(stream.isAllowedAt(groupId)){
				Publication p = new Publication(PublicationId.random(), publicationName, description, streamId, appId, app.isKeeperSoftware(), app.type());
				group.publish(p);
				data.groups.put(group.id(), group, null);
				return p.id();
			}else{
				throw new SecurityException("Stream " + streamId + " is not allowed to be added to group " + groupId + ".");
			}
		}
	}
	
	public void addGroupMembers(final GroupId groupId, List<Id> members, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredGroup group = data.groups.get(groupId, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such group: " + groupId);
		}
		
		// ENFORCE GROUP SECURITY
		assertGroupAdministrator(group, user);

		// VALIDATE MEMBERS LIST
		if(members==null || members.size()==0){
			throw new RuntimeException("No members specified");
		}else{
			// verify that these really are current members of the group
			for(Id next : members){
				final UserAccount nextUser = data.userAccounts.get(next, null, LockMode.READ_COMMITTED);
				if(nextUser==null){
					throw new RuntimeException("No such user account: " + next);
				}
			}
		}
		
		// MAKE THE CHANGES
		group.addMembers(members);
		
		data.groups.put(group.id(), group, null);
	}
	
	public void removeGroupMembers(final GroupId groupId, List<Id> members, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredGroup group = data.groups.get(groupId, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such group: " + groupId);
		}

		// ENFORCE GROUP SECURITY
		assertGroupAdministrator(group, user);
		
		// VALIDATE MEMBERS LIST
		if(members==null || members.size()==0){
			throw new RuntimeException("No members specified");
		}else{
			// verify that these really are current members of the group
			for(Id next : members){
				if(!group.isMember(next)){
					throw new RuntimeException(next + " is not a member of group " + group.name() + " (" + group.id() + ")");
				}
			}
		}
		
		group.removeMembers(members);
		
		data.groups.put(group.id(), group, null);
	}
	
	public void updateGroup(final GroupId groupId, final String name, final SecurityMode securityMode, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredGroup group = data.groups.get(groupId, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such group: " + groupId);
		}
		
		// ENFORCE GROUP SECURITY
		assertGroupAdministrator(group, user);
		
		{// make the changes
			
			boolean hasUpdate = false;
			
			if(name!=null){
				group.setName(name);
				hasUpdate = true;
			}
			
			if(securityMode!=null){
				group.setSecurityMode(securityMode);
				hasUpdate = true;
			}
			
			
			if(!hasUpdate){
				throw new RuntimeException("No updated data specified");
			}else{
				data.groups.put(group.id(), group, null);
			}
		}
		
		
	}
	
	public void renamePublication(PublicationId publicationId, String newName, String description, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final StoredGroup group = data.groupsByPublication.get(publicationId, null, LockMode.READ_COMMITTED);
		
		if(group==null){
			throw new RuntimeException("No such publication: " + publicationId);
		}
		
		// ENFORCE GROUP SECURITY
		assertGroupAdministrator(group, user);
		
		
		// MAKE THE CHANGES
		final Publication p = group.publication(publicationId);
		
		if(p==null){
			throw new RuntimeException("Internal consistency error: publication " + publicationId + ", group " + group.id());
		}
		
		p.name(newName);
		p.description(description);
		
		data.groups.put(group.id(), group, null);
	}
	
	/*|------------------------------------------------------|
	 *|                STREAMS MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	
	public StreamId createStream(StreamId parentId, String name, SecurityMode groupSecurityMode, IdProof credentials, AppId ... applications) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final Instant now = new Instant();
		
		final StoredStream stream = new StoredStream(StreamId.random(), name, groupSecurityMode, parentId, now);
		
		// VALIDATE THE APPS LIST
		if(parentId==null){
			if(applications==null || applications.length==0){
				throw new NullPointerException("You must supply at least one application for the stream");
			}
			for(AppId next : applications){
				stream.addApp(next);
			}
		}else{
			if(applications!=null && applications.length>0){
				throw new RuntimeException("Child streams should not specify an application - they are automatically inherited from their parent");
			}
			final StoredStream parent = data.streams.get(parentId, null, LockMode.READ_COMMITTED);
			if(parent==null){
				throw new RuntimeException("No such parent stream: " + parentId);
			}
			
			for(StreamAppStatus next : parent.apps()){
				stream.addApp(next.app());
			}
		}
		
			
		data.streams.put(stream.id(), stream, null);
		return stream.id();
	}
	
	public  List<StreamInfo> listStreams(IdProof credentials) throws SecurityException{
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final List<StreamInfo> infos = new LinkedList<StreamInfo>();
		
		data.streams.scan(
				new ValueScanner<StoredStream>() {
					public void inspect(StoredStream next) {
						infos.add(next.toInfoDto());
					}
				},
				null
			);
		
		return infos;
	}
	

	public StreamDetails getStreamDetails(StreamId id, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		StoredStream stream = data.streams.get(id, null, LockMode.READ_COMMITTED);
		
		if(stream==null){
			throw new RuntimeException("No such stream: " + id);
		}else{
			return stream.toDetailsDto(data);
		}
	}
	
	public StreamUpdateId sendDownStream(final StreamId id, final String label, final IdProof credentials, final AppVersionSpec ... versions) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final Instant now = new Instant();
		final StoredStream stream = data.streams.get(id, null, LockMode.READ_COMMITTED);
		
		if(stream==null){
			throw new RuntimeException("No such stream: " + id);
		}else{
			
			if(versions==null || versions.length==0){
				throw new RuntimeException("You must specify at least one app version update per stream udpate");
			}else{
				
				// VALIDATE THE APP VERSIONS
				for(AppVersionSpec next : versions){
					final StoredApp app = data.apps.get(next.getApplication(), null, LockMode.READ_COMMITTED);
					if(app==null){
						throw new RuntimeException("No such app: " + next.getApplication());
					}else{
						boolean foundInstallable = false;
						for(StoredAppVersionSeries path: app.series()){
							for(InstallableId i : path.installables()){
								if(i.equals(next.getVersion())){
									foundInstallable = true;
								}
							}
						}
						
						if(!foundInstallable){
							throw new RuntimeException(next.getVersion() + " is not a valid version for application " + next.getApplication());
						}
						
						final InstallableId currentVersion = stream.currentStatus(app.id()).version();
						if(currentVersion!=null && currentVersion.equals(next.getVersion())){
							throw new RuntimeException("App " + app.name() + " is already at version " + next.getVersion());
						}
					}
					
				}
				
				final StoredStreamUpdate update = new StoredStreamUpdate(StreamUpdateId.random(), label, now, Arrays.asList(versions));
				stream.addUpdate(update);
				
				final List<StoredInstallCommand> installs = installs(
						now,
						stream.id(),
						versions
					);
				
				
				new WorkAtom(data) {
					@Override
					protected void doWork(Transaction tx) throws Exception {
						data.streams.put(id, stream, tx);
						data.streamUpdates.put(update.id(), update, tx);
						
						for(final StoredInstallCommand next : installs){
							data.commandsQueue.commands.put(next.id(), next, tx);
						}
					}
				}.run();
				
				return update.id();
			}

			
		}
	}
	
	final List<StoredInstallCommand> installs(final Instant now, final StreamId streamId, final AppVersionSpec[] versions){
		final List<StoredInstallCommand> installs = new LinkedList<StoredInstallCommand>();
		
		data.installByStream.keySearchForward(
				streamId, 
				new SearchVisitor<StreamId, StoredInstall>() {
					public boolean next(StreamId key, StoredInstall value) {
						if(key.equals(streamId)){
							
							AppVersionSpec newVersion = null;
							for(AppVersionSpec next : versions){
								if(next.getApplication().equals(value.app())){
									newVersion = next;
								}
							}
							
							if(newVersion!=null){
								installs.add(
										new StoredInstallCommand(
											CommandId.random(),
											value.id(),
											value.keeper(),
											data.nextCommandOrderNumber(),
											now,
											newVersion.getVersion()
										)
								);
							}
							// keep searching
							return true;
						}else{
							// stop searching
							return false;
						}
					}
				},
				null
			);
		
		return installs;
	}
	
	public void relayDownstream(StreamId streamId, StreamUpdateId upstreamUpdateId, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final Instant now = new Instant();
		
		final StoredStream stream = data.streams.get(streamId, null, LockMode.READ_COMMITTED);
		final StoredStreamUpdate update = data.streamUpdates.get(upstreamUpdateId, null, LockMode.READ_COMMITTED);
		
		/*
		 * VALIDATE THE REQUEST
		 *   Among other things, here we make sure that this is indeed a valid update from the parent stream.
		 */
		if(stream==null){
			throw new RuntimeException("No such stream: " + streamId);
		}else if(update==null){
			throw new RuntimeException("No such update: " + streamId);
		} else {
			if(stream.parentStream()==null){
				throw new RuntimeException("Stream " + streamId + " is not a child stream");
			}else{
				final StoredStream parent = data.streams.get(stream.parentStream(), null, LockMode.READ_COMMITTED);
				if(parent==null){
					throw new RuntimeException("Relational intergrity error: stream " + stream.parentStream() + " does not exist.");
				}else{
					if(!parent.hasUpdate(upstreamUpdateId)){
						throw new RuntimeException("Update " + upstreamUpdateId + " is not present in parent stream");
					}
				}
			}
		}
		
		final List<StoredInstallCommand> installs = installs(
				now,
				stream.id(),
				update.appUpdates().toArray(new AppVersionSpec[]{})
			);
		
		/*
		 * RELAY THE UPDATE
		 */
		stream.addUpdate(update);
		
		new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.streams.put(stream.id(), stream, tx);
				
				for(StoredInstallCommand command : installs){
					data.commandsQueue.commands.put(command.id(), command, tx);
				}
			}
		}.run();
	}
	
	public void updateStream(StreamId id, String name, SecurityMode securityMode, 
			List<GroupId> groupSecurityRoster, IdProof credentials) throws SecurityException {
		
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		StoredStream stream = data.streams.get(id, null, LockMode.READ_COMMITTED);
		
		if(stream==null){
			throw new RuntimeException("No such stream: " + id);
		}else{
			boolean somethingChanged = false;
			if(name!=null){
				stream.setName(name);
				somethingChanged = true;
			}
			
			if(securityMode!=null){
				stream.setSecurityMode(securityMode);
				somethingChanged = true;
			}
			
			if(groupSecurityRoster!=null){
				for(GroupId next : groupSecurityRoster){
					final StoredGroup group = data.groups.get(next, null, LockMode.READ_COMMITTED);
					if(group==null){
						throw new RuntimeException("No such group: " + next);
					}
				}
				stream.setSecurityRoster(groupSecurityRoster);
				somethingChanged = true;
			}
			
			if(somethingChanged){
				data.streams.put(stream.id(), stream, null);
			} else {
				throw new RuntimeException("Nothing to update: no new data was supplied?");
			}
		}
	}
	
	public List<StreamUpdate> listStreamUpdates(StreamId id, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final StoredStream stream = data.streams.get(id, null, LockMode.READ_COMMITTED);
		
		if(stream==null){
			throw new RuntimeException("No such stream: " + id);
		}else{
			List<AppInfo> appInfos = new LinkedList<AppInfo>();
			for(StreamAppStatus status : stream.apps()){
				final StoredApp app = data.apps.get(status.app(), null, LockMode.READ_COMMITTED);
				appInfos.add(app.infoDto());
			}
			List<StreamUpdate> updates = new LinkedList<StreamUpdate>();
			
			for(StreamUpdateId next : stream.updates()){
				final StoredStreamUpdate update = data.streamUpdates.get(next, null, LockMode.READ_COMMITTED);
				updates.add(update.toDto(data.installables, appInfos));
			}
			
			return updates;
		}
	}
	
	
	public InstallableId currentVersion(StreamId streamId, AppId app, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		final StoredStream stream = data.streams.get(streamId, null, LockMode.READ_COMMITTED);
		
		if(stream==null){
			throw new RuntimeException("No such stream: " + streamId);
		}else{
			
			final StreamAppStatus status = stream.currentStatus(app);
			
			if(status==null){
				throw new RuntimeException("Invalid app for stream - this app id doesn't correspond to an app in this stream:" + app);
			}else{
				return status.version();
			}
		}
	}
	
	public void addAppToStream(StreamId streamId, AppId appId, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateAdministrator(credentials);
		
		final StoredStream stream = data.streams.get(streamId, null, LockMode.READ_COMMITTED);
		final StoredApp app = data.apps.get(appId, null, LockMode.READ_COMMITTED);
		
		// VALIDATE INPUT
		if(app==null){
			throw new RuntimeException("No such app: " + appId);
		}
		if(stream==null){
			throw new RuntimeException("No such stream: " + streamId);
		}
		
		if(stream.parentStream()!=null){
			throw new RuntimeException("You can only add apps to a root stream");
		}
		
		if(stream.currentStatus(appId)!=null){
			throw new RuntimeException("App " + appId + " is already in stream " + streamId);
		}
		
		// ADD THE APP TO ALL EFFECTED STREAMS
		stream.addApp(appId);
		
		final List<StoredStream> subStreams = new LinkedList<StoredStream>();
		collectSubStreams(streamId, subStreams);
		
		for(StoredStream next : subStreams){
			next.addApp(appId);
		}
		
		new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.streams.put(stream.id(), stream, tx);
				for(StoredStream next : subStreams){
					data.streams.put(next.id(), next, tx);
				}
			}
		}.run();
	}
	
	private void collectSubStreams(StreamId parent, List<StoredStream> children){
		List<StoredStream> results = subStreams(parent);
		children.addAll(results);
		for(StoredStream next : results){
			collectSubStreams(next.id(), children);
		}
	}
	
	
	private List<StoredStream> subStreams(final StreamId parent){
		final List<StoredStream> results = new LinkedList<StoredStream>();
		data.streamsByParent.keySearchForward(
				parent, 
				new SearchVisitor<StreamId, StoredStream>() {
					public boolean next(StreamId key, StoredStream value) {
						if(key.equals(parent)){
							results.add(value);
							return true;
						}else{
							
							return false;
						}
					}
				}, 
				null
			);
		return results;
	}

	
	/*|------------------------------------------------------|
	 *|              INSTALLATIONS MANAGEMENT                |
	 *|------------------------------------------------------|*/
	public KeeperRegistrationResponse registerKeeper(PublicationId keeperSoftwareId, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		final Instant now = new Instant();
		
		final KeeperRegistration keeper = new KeeperRegistration(KeeperId.random(), AuthorizationToken.random(new Random()));
		
		WorkAtom action = new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.keepers.put(keeper.id(), keeper, tx);
			}
		};
		
		// REGISTER THE KEEPER INSTALL IF APPLICABLE
		if(keeperSoftwareId!=null){
			final StoredGroup group;
			final Publication p;
			{// FETCH THE PUBLICATION
				group = data.groupsByPublication.get(keeperSoftwareId, null, LockMode.READ_COMMITTED);
				if(group==null){
					throw new RuntimeException("No such publication: " + keeperSoftwareId);
				}
				
				p = group.publication(keeperSoftwareId);
				if(p==null){
					throw new RuntimeException("No such publication: " + keeperSoftwareId);
				}
			}
			
			// ENFORCE GROUP SECURITY
			assertGroupMember(group, user);
			
			{// VERIFY THAT THIS IS LEGAL KEEPER SOFTWARE
				final StoredApp app = data.apps.get(p.app(), null, LockMode.READ_COMMITTED);
				if(app==null){
					throw new RuntimeException("No such app: " + p.app());
				}
				
				if(!app.isKeeperSoftware()){
					throw new RuntimeException("App " + app.id() + " is not registered as keeper software");
				}
			}
			
			InstallRegistrationWrapper w = prepareInstallRegistration(p, keeper.id(), now);

			keeper.setKeeperInstall(w.install);
			
			action = action.add(w.creationAction);
		}
		
		
		action.run();
		
		
		InstallRegistrationResponse r;
		
		if(keeperSoftwareId!=null){
			r = new InstallRegistrationResponse(keeper.keeperInstall(), fetchCommands(keeper.id(), data.commandsQueue));
		}else{
			r=null;
		}
		
		return new KeeperRegistrationResponse(keeper.id(), keeper.token(), r);
	}
	
	

	private static final class InstallRegistrationWrapper {
		private final InstallId install;
		private final WorkAtom creationAction;
		
		public InstallRegistrationWrapper(InstallId install, WorkAtom creationAction) {
			super();
			this.install = install;
			this.creationAction = creationAction;
		}

		public InstallId register(){
			creationAction.run();
			return install;
		}
			
	}
	
	private InstallRegistrationWrapper prepareInstallRegistration(final Publication pub, KeeperId keeper, final Instant now){
		final StoredInstall install = new StoredInstall(InstallId.random(), pub.id(), pub.app(), keeper, pub.stream());
		
		final StoredInstallCommand installCommand;
		{// CREATE INSTALL COMMAND
			final StoredStream stream = data.streams.get(pub.stream(), null, LockMode.READ_COMMITTED);//group.streamForApp(appId, data.streams);
			
			if(stream==null){
				throw new RuntimeException("Internal consistency error: no such stream " + pub.stream());
			}
			
			
			final InstallableId appVersion = stream.currentStatus(pub.app()).version();
			
			if(appVersion==null){
				throw new RuntimeException("There is no version of " + pub.app() + " in the stream");
			}
			
			installCommand = new StoredInstallCommand(
					CommandId.random(),
					install.id(),
					keeper,
					data.nextCommandOrderNumber(),
					now,
					appVersion
			);
		}
		
		WorkAtom action = new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.installs.put(install.id(), install, tx);
				data.commandsQueue.commands.put(installCommand.id(), installCommand, tx);
			}
		};
		
		return new InstallRegistrationWrapper(install.id(), action);
	}
	
	public InstallRegistrationResponse registerInstall(PublicationId publicationId, KeeperId keeperId, AuthorizationToken keeperToken, IdProof credentials) throws SecurityException {
		final KeeperRegistration keeper = data.keepers.get(keeperId, null, LockMode.READ_COMMITTED);
		if(keeper==null){
			throw new RuntimeException("No such keeper: " + keeperId);
		} 
		
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		final Instant now = new Instant();
		
		final StoredGroup group = data.groupsByPublication.get(publicationId, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException("No such publication: " + publicationId);
		}
		
		final Publication p = group.publication(publicationId);
		if(p==null){
			throw new RuntimeException("No such publication: " + publicationId);
		}
		
		// ENFORCE GROUP SECURITY
		assertGroupMember(group, user);
		if(!keeper.token().equals(keeperToken)){
			throw new SecurityException("Invalid keeper token");
		}
		
		{// ENFORCE THE RESTRICTION THAT THIS METHOD CANNOT BE USED TO REGISTER INSTALLS OF KEEPER SOFTWARE
			final StoredApp app = data.apps.get(p.app(), null, LockMode.READ_COMMITTED);
			if(app==null){
				throw new RuntimeException("No such application: " + p.app());
			}
			
			if(app.isKeeperSoftware()){
				throw new RuntimeException("This is keeper software - it can only be installed as part of a keeper registration");
			}
		}
		
		
		InstallRegistrationWrapper w = prepareInstallRegistration(p, keeper.id(), now);

		InstallId install = w.register();
		
		return new InstallRegistrationResponse(install, fetchCommands(keeperId, data.commandsQueue));
	}
	
	
	private static final class KeeperCommandsBuilder {
		private final Log log = LogFactory.getLog(getClass());
		private final KeeperId keeper;
		private final List<StoredCommand> commands = new LinkedList<StoredCommand>();
		
		public KeeperCommandsBuilder(KeeperId keeper) {
			super();
			this.keeper = keeper;
		}

		
		public void add(StoredCommand c){
			this.commands.add(c);
		}
		
		public KeeperCommands build(){
			
			if(log.isDebugEnabled()){
				log.debug("Constructing result for keeper " + keeper + " with " + commands.size() + " commands");
			}
			return new KeeperCommands(keeper, sortAndTranslate(this.commands));
		}
	}
	
	private static List<Command> sortAndTranslate(List<StoredCommand> installCommands){
		Collections.sort(installCommands, new Comparator<StoredCommand>() {
			public int compare(StoredCommand a, StoredCommand b) {
				return a.orderKey().compareTo(b.orderKey());
			}
		});
		
		List<Command> dtos = new LinkedList<Command>();
		for(StoredCommand nextCommand : installCommands){
			dtos.add(nextCommand.toDto());
		}
		
		return dtos;
	}
	
	private KeeperCommands fetchCommands(final KeeperId keeper, final CommandsData data){
		final KeeperCommandsBuilder builder = new KeeperCommandsBuilder(keeper);
		
		data.commandsByKeeper.keySearchForward(
				keeper, 
				new SearchVisitor<KeeperId, StoredCommand>() {
					
					public boolean next(KeeperId key, StoredCommand value) {
						if(key.equals(keeper)){
							builder.add(value);
							return true;
						}else{
							return false;
						}
					}
				}, 
				null
			);
		
		return builder.build();
	}
	
	private InstallationCommands fetchCommands(final InstallId install, final CommandsData data){
		final List<StoredCommand> commands = new LinkedList<StoredCommand>();
		
		data.commandsByInstall.keySearchForward(
				install, 
				new SearchVisitor<InstallId, StoredCommand>() {
					
					public boolean next(InstallId key, StoredCommand value) {
						if(key.equals(install)){
							commands.add(value);
							return true;
						}else{
							return false;
						}
					}
				}, 
				null
			);
		
		return new InstallationCommands(install, sortAndTranslate(commands));
	}
	
	public void recallInstall(InstallId installId, IdProof credentials) throws SecurityException {
		final UserAccount user = findAssertAndAuthenticateUser(credentials);
		
		final Instant now = new Instant();
		
		final StoredInstall install = data.installs.get(installId, null, LockMode.READ_COMMITTED);
		
		if(install==null){
			throw new RuntimeException("No such install: " + installId);
		}
		
		{// ENFORCE GROUP SECURITY
			final StoredGroup group = data.groupsByPublication.get(install.publication(), null, LockMode.READ_COMMITTED);
			assertGroupAdministrator(group, user);
		}
		
		StoredUninstallCommand c = new StoredUninstallCommand(
							CommandId.random(),
							install.id(),
							install.keeper(),
							data.nextCommandOrderNumber(),
							now
						);
		
		data.commandsQueue.commands.put(c.id(), c, null);
		
	}
	
	public KeeperCommands getKeeperQueue(KeeperId keeperId, AuthorizationToken keeperToken) throws SecurityException {
		final KeeperRegistration keeper = data.keepers.get(keeperId, null, LockMode.READ_COMMITTED);
		
		if(keeper==null){
			throw new RuntimeException("No such keeper: " + keeperId);
		}
		
		if(!keeper.token().equals(keeperToken)){
			throw new SecurityException("Invalid keeper token");
		}
		
		return fetchCommands(keeperId, data.commandsQueue);
	}
	
	public KeeperCommands getKeeperHistory(KeeperId keeperId, IdProof credentials) throws SecurityException {
		findAssertAndAuthenticateAdministrator(credentials);
		
		return fetchCommands(keeperId, data.commandsHistory);
	}
	
	public KeeperCommands keeperQueuePoll(KeeperId keeperId, final CommandId lastCompletedCommandId, AuthorizationToken keeperToken) throws SecurityException {
		{// AUTHENTICATE REQUEST
			final KeeperRegistration keeper = data.keepers.get(keeperId, null, LockMode.READ_COMMITTED);
			
			if(keeper==null){
				throw new RuntimeException("No such keeper: " + keeperId);
			}
			
			if(!keeper.token().equals(keeperToken)){
				throw new SecurityException("Invalid keeper token");
			}
		}
		
		final KeeperCommands q = fetchCommands(keeperId, data.commandsQueue);
		if(!q.earliest().id().equals(lastCompletedCommandId)){
			throw new RuntimeException("Out of order execution - command " + lastCompletedCommandId + " is not the earliest command in your queue.");
		}
		
		// FETCH THE COMMAND
		final StoredCommand lastCompletedCommand = data.commandsQueue.commands.get(lastCompletedCommandId, null, LockMode.READ_COMMITTED);
		if(lastCompletedCommand==null){
			throw new RuntimeException("No such command: " + lastCompletedCommandId);
		}
		
		// MOVE THE COMMAND FROM THE QUEUE TO THE HISTORY
		new WorkAtom(data) {
			@Override
			protected void doWork(Transaction tx) throws Exception {
				data.commandsQueue.commands.delete(lastCompletedCommandId, tx);
				data.commandsHistory.commands.put(lastCompletedCommand.id(), lastCompletedCommand, tx);
			}
		}.run();
		
		return fetchCommands(keeperId, data.commandsQueue);
	}
	
}
