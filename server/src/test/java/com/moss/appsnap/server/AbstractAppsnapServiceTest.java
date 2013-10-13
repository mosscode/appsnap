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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.moss.appkeep.api.AppkeepService;
import com.moss.appkeep.api.ComponentInfo;
import com.moss.appkeep.api.mirror.PeerId;
import com.moss.appkeep.api.security.UserAccountDownloadToken;
import com.moss.appkeep.api.select.ComponentHandlesSelector;
import com.moss.appkeep.server.AppkeepServer;
import com.moss.appkeep.server.config.AdministratorConfig;
import com.moss.appkeep.server.config.ServerConfiguration;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.appsnap.api.apps.AppVersionSpec;
import com.moss.appsnap.api.apps.LaunchIntegrityException;
import com.moss.appsnap.api.groups.AppPublicationInfo;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.AppkeepDownloadVector;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installables.JavaAppInstallableDetails;
import com.moss.appsnap.api.installs.Command;
import com.moss.appsnap.api.installs.CommandId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.InstallRegistrationResponse;
import com.moss.appsnap.api.installs.InstallationCommands;
import com.moss.appsnap.api.installs.KeeperCommands;
import com.moss.appsnap.api.installs.KeeperRegistrationResponse;
import com.moss.appsnap.api.installs.commands.InstallCommand;
import com.moss.appsnap.api.installs.commands.UninstallCommand;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.fskit.TempDir;
import com.moss.identity.Id;
import com.moss.identity.IdProof;
import com.moss.identity.simple.SimpleId;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.standard.PasswordIdProofCheckRecipe;
import com.moss.identity.standard.PasswordProofRecipie;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.launch.components.Component;
import com.moss.launch.components.ComponentHandle;
import com.moss.launch.components.ComponentType;
import com.moss.launch.components.MavenCoordinatesHandle;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.launch.spec.launch.ClassName;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

public abstract class AbstractAppsnapServiceTest extends TestCase {
	private static final String APPKEEP_HOST = "127.0.0.1";
	private static final int APPKEEP_PORT = 5000;
	protected static final String APPKEEP_PASS = "supersecret";
	protected static final SimpleId APPKEEP_LOGON = new SimpleId("mr-admin");
	
	private IdProover appkeepAdminProver = new SimpleIdProover(APPKEEP_LOGON, APPKEEP_PASS);
	private IdProover snapAdminProver;
	
	private AppsnapService snap;
	private AppkeepService keep;
	private TempDir dataDir;
	private byte[] dummyData;
	
	protected Url keepLocation;
	private AppkeepServer keepServer;
	
	@Override
	protected final void setUp() throws Exception {
		{
			InputStream in = getClass().getResourceAsStream("random.data");
			if(in==null){
				throw new RuntimeException("Missing resource");
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream(512*1024);
			byte[] buffer = new byte[512*1024];
			for(int x=in.read(buffer); x!=-1;x = in.read(buffer)){
				out.write(buffer, 0, x);
			}
			in.close();
			out.close();
			dummyData = out.toByteArray();
		}
		
		dataDir = TempDir.create();
		{
			File appKeepDataPath = new File(dataDir, "appkeep");
			ServerConfiguration config = new ServerConfiguration();
			
			config.bindAddress(APPKEEP_HOST);
			config.publishAddress(config.bindAddress());
			config.bindPort(APPKEEP_PORT);
			config.publishPort(APPKEEP_PORT);
			config.storageDir(appKeepDataPath );
			config.id(PeerId.random());
			config.idProofRecipie(new PasswordProofRecipie(APPKEEP_LOGON, APPKEEP_PASS));
			config.administrators().add(new AdministratorConfig(new PasswordProofRecipie(APPKEEP_LOGON, APPKEEP_PASS)));
			keepServer = new AppkeepServer(config, null);
			
			ProxyFactory proxyFactory = new ProxyFactory(new HessianProxyProvider());
			
			keepLocation = Url.http(APPKEEP_HOST, APPKEEP_PORT, "/rpc");
			keep = proxyFactory.create(AppkeepService.class, keepLocation.toString());
		}

		

		snap = service(new File(dataDir, "appsnap"));
		snapAdminProver = serviceAdminProver();
	}
	
	
	@Override
	protected final void tearDown() throws Exception {
		try {
			keepServer.shutdown();
		}finally{
			try{
				tearDownService();
			}finally{
				dataDir.deleteRecursively();
			}
		}
	}
	
	protected abstract IdProover serviceAdminProver();
	
	protected abstract void tearDownService() throws Exception;
	
	protected abstract AppsnapService service(File dataLocation);
	
	private JavaAppSpec fakeSpec(){
		final JavaAppSpec spec = new JavaAppSpec();
		spec.setMainClass(new ClassName("testing123"));
		
		{
			MavenCoordinatesHandle m = new MavenCoordinatesHandle("com.moss.appsnap.fake", "appsnap-fake", "4.3.1");
			
			ComponentHandle[] handles = new ComponentHandle[]{
					m
			};
			
			Component c = new Component(ComponentType.JAR, handles);
			spec.add(c);
			
		}
		
		return spec;
	}
	
	public final void testAppsMaintainance() throws Exception {
		
		// MAKE A FAKE LAUNCH SPEC
		final JavaAppSpec spec = fakeSpec();
		
		{// TEST A BAD UPLOAD (WITH COMPONENTS THAT AREN'T IN THE KEEP)
			try {
				snap.createJavaApplication("junk", "1.0", spec, false, giveAdminProof());
				fail("The server should have choked on this, since the component isn't in the keep");
			} catch (LaunchIntegrityException e) {
				// EXPECTED
			}
		}
		
		final AppId app;
		{// TEST A GOOD UPLOAD
			
			// PRIME THE KEEP
			postToKeep(spec);
			
			final String appName = "my first app";
			app = snap.createJavaApplication(appName, "1.0", spec, false, giveAdminProof());
			
			assertAppPresence(app, appName, false);
			
			final String newName = "superduperappalooza-special_edition";
			
			snap.renameApplication(app, newName, giveAdminProof());
			
			assertAppPresence(app, newName, false);
		}
		
		{// TEST AN APP VERSION ADD
			final String pathName = "1.x";
			
			final InstallableId i = snap.addJavaAppVersion(app, pathName, "1.0", spec, giveAdminProof());
			
			assertInstallablePresense(app, i);
			
			final AppDetails details = snap.getAppDetails(app, giveAdminProof());
			
			final AppVersionSeries path = details.series(pathName);
			
			assertNotNull("should have created a path called " + pathName, path);
			
			assertEquals(1, path.installables().size());
			
			assertEquals(i, path.installables().get(0).id());
		}
	}
	
	public void testGroupsMaintainance() throws Exception {
		
		final GroupId group;
		{// TEST GROUP CREATION
			final String groupName = "cool-people";
			
			group = snap.createGroup(groupName, SecurityMode.WHITELIST, giveAdminProof());
			
			assertNotNull("Should have received a group id", group);
			
			final GroupDetails details = snap.getGroupDetails(group, giveAdminProof());
			
			assertEquals(group, details.id());
			
			assertEquals(groupName, details.name());
			
			assertNotNull(details.membership());
			
			assertEquals(0, details.membership().size());
			
			assertNotNull(details.administrators());
			
			assertEquals(1, details.administrators().size());
			
			final Id admin = details.administrators().get(0);
			
			assertEquals(snapAdminProver.getIdentity(), admin);
			
			assertEquals(SecurityMode.WHITELIST, details.membershipSecurityMode());
		}
		
		{// TEST BAD UPDATE
			try {
				snap.updateGroup(group, null, null, giveAdminProof());
				fail("This null update should have failed");
			} catch (Exception e) {
				// Expected
			}
		}
		
		final String newName = "loosers";
		{// TEST FULL UPDATE
			
			snap.updateGroup(group, newName, SecurityMode.BLACKLIST, giveAdminProof());
			
			final GroupDetails details = snap.getGroupDetails(group, giveAdminProof());
			
			assertEquals(newName, details.name());
			
			assertEquals(SecurityMode.BLACKLIST, details.membershipSecurityMode());
		}
		
		{// TEST GROUP LISTING
			
			GroupInfo info = null;
			for(final GroupInfo next : snap.listGroups(giveAdminProof())){
				if(next.id().equals(group)){
					if(info==null){
						info = next;
					}else{
						fail("Group showed up more than once in group listing!");
					}
				}
			}
			if(info==null){
				fail("Group should be in listing");
			}else{
				assertEquals(newName, info.name());
			}
			
		}
		
		{// TEST GROUP MEMBERSHIP ADDITION
			
			final Id newbie = new SimpleId("neeyo fight");
			
			snap.registerUser(new PasswordIdProofCheckRecipe(newbie, "my secret newbie password"));
			snap.addGroupMembers(group, Collections.singletonList(newbie), giveAdminProof());
			
			final GroupDetails details = snap.getGroupDetails(group, giveAdminProof());
			
			assertEquals(1, details.membership().size());
			
			assertEquals(newbie, details.membership().get(0));
		}
		
		{// TEST BASIC STREAM->GROUP PUBLISHING PERSISTENCE
			final AppId app;
			{
				final JavaAppSpec spec = fakeSpec();
				postToKeep(spec);
				app = snap.createJavaApplication("my first app", "1.0", spec, false, giveAdminProof());
			}
			
			final StreamId privateStream = snap.createStream(null, "important customers' deployment channel", SecurityMode.WHITELIST, giveAdminProof(), app);
			final StreamId publicStream = snap.createStream(null, "peons' deployment channel", SecurityMode.BLACKLIST, giveAdminProof(), app);
			
			try {
				snap.publishToGroup("the app", "My special app", privateStream, app, group, giveAdminProof());
				fail("Should have received a security exception due to group assosciativity controls");
			} catch (SecurityException e) {
				// Expected
			}
			
			final String publicationName = "special app 4 u";
			final PublicationId pub = snap.publishToGroup(publicationName, "My special app", publicStream, app, group, giveAdminProof());
			
			{// VERIFY THE PRESENCE OF THIS PUBLICATION
				final GroupDetails details = snap.getGroupDetails(group, giveAdminProof());
				
				final AppPublicationInfo pubInfo = details.publicationInfo(pub);
				assertNotNull("There should be record of this publication", pubInfo);
				assertEquals(pub, pubInfo.id());
				assertEquals(publicationName, pubInfo.name());
			}
			
			final String newPublicationName = "special 42";
			final String newPublicationDescription = " a very special 42 application";
			
			{// TEST PUBLICATION RENAMING
				snap.renamePublication(pub, newPublicationName, newPublicationDescription, giveAdminProof());
				final GroupDetails details = snap.getGroupDetails(group, giveAdminProof());
				
				final AppPublicationInfo pubInfo = details.publicationInfo(pub);
				assertNotNull("There should be record of this publication", pubInfo);
				assertEquals(pub, pubInfo.id());
				assertEquals(newPublicationName, pubInfo.name());
				assertEquals(newPublicationDescription, pubInfo.description());
			}
			
		}
	}
	
	public void testStreamsMaintainance() throws Exception {
		final String appName = "my first app";
		final AppId app;
		final InstallableId i;
		{
			final JavaAppSpec spec = fakeSpec();
			postToKeep(spec);
			app = snap.createJavaApplication(appName, "1.0", spec, false, giveAdminProof());
			i = snap.getAppDetails(app, giveAdminProof()).series().get(0).installables().get(0).id();
		}
		
		final String streamName = "Main Update Channel";
		final StreamId stream = snap.createStream(null, streamName, SecurityMode.BLACKLIST, giveAdminProof(), app);
		{// TEST TO SEE THAT THE STREAM WAS MADE CORRECTLY (by means of the details DTO)
			final StreamDetails details = snap.getStreamDetails(stream, giveAdminProof());
			
			assertNotNull("Should have received some details", details);
			
			assertEquals(streamName, details.name());
			assertEquals(stream, details.id());
			assertNull("This stream should not have a parent.", details.parent());
			assertNotNull("There should at least be a non-null list", details.applications());
			assertEquals(1, details.applications().size());
			final AppInfo appInfo = details.applications().get(0).appInfo();
			assertNotNull("There should be real info here.", appInfo);
			assertEquals(app, appInfo.id());
			assertEquals(appName, appInfo.name());
		}
		{// TEST TO SEE THAT THE STREAM WAS MADE CORRECTLY (by means of the listing)
			StreamInfo info = null;
			
			List<StreamInfo> infos = snap.listStreams(giveAdminProof());
			for(StreamInfo next : infos){
				if(next.id().equals(stream)){
					info = next;
				}
			}
			
			assertNotNull("The stream should be present in the streams listing.", giveAdminProof());
			
			assertEquals(stream, info.id());
			assertEquals(streamName, info.name());
		}
		
		final String newName = "Primary Update Channel";
		{// TEST AN INVALID STREAM UPDATE (all nulls)
			try {
				snap.updateStream(stream, null, null, null, giveAdminProof());
				fail("This was a bad update - it should have failed");
			} catch (Exception e) {
				// expected
			}
		}
		{// TEST AN INVALID STREAM UPDATE (bogus group)
			try {
				snap.updateStream(stream, newName, SecurityMode.WHITELIST, Collections.singletonList(GroupId.random()), giveAdminProof());
				fail("This was a bad update - it referenced a nonexistent group & should have failed");
			} catch (Exception e) {
				// expected
			}
		}
		{// TEST A VALID STREAM UPDATE
			snap.updateStream(stream, newName, SecurityMode.WHITELIST, null, giveAdminProof());
			
			final StreamDetails details = snap.getStreamDetails(stream, giveAdminProof());
			
			assertNotNull("We should have received a details DTO", details);
			assertEquals(stream, details.id());
			assertEquals(newName, details.name());
			assertEquals(SecurityMode.WHITELIST, details.securityMode());
			assertNotNull("The group security roster should not be null (even though we passed a null to update() earlier", details.groupSecurityRoster());
		}
		
		final StreamUpdateId updateId;
		{// TEST A FIRST STREAM UPDATE POSTING
			updateId = snap.sendDownStream(stream, "first update", giveAdminProof(), new AppVersionSpec(app, i));
			
			assertNotNull("Should have received an ID for the stream update", updateId);
			
			final InstallableId version = snap.currentVersion(stream, app, giveAdminProof());
			assertNotNull("There should be a current version", version);
			assertEquals(i, version);
		}
		
		final String childStreamName = "super conservative updates only";
		final StreamId childStream;
		
		{// TEST THE CREATION OF A BOGUS CHILD STREAM (specified apps)
			try {
				snap.createStream(stream, childStreamName, SecurityMode.BLACKLIST, giveAdminProof(), app);
				fail("This should have choked on the apps list");
			} catch (Exception e) {
				// expected
			}
		}
		{// TEST THE CREATION OF A CHILD STREAM
			childStream = snap.createStream(stream, childStreamName, SecurityMode.BLACKLIST, giveAdminProof());
			assertNotNull("Should have received an ID", childStream);
			
			final StreamDetails details = snap.getStreamDetails(childStream, giveAdminProof());
			
			assertNotNull("Should have received stream details", details);
			assertEquals(childStreamName, details.name());
			assertEquals(stream, details.parent());
			assertEquals(SecurityMode.BLACKLIST, details.securityMode());
			assertNotNull("Should have at least an empty list", details.groupSecurityRoster());
			assertEquals(0, details.groupSecurityRoster().size());
		}
		
		{// TEST AN INVALID RELAY (bogus update ID)
			try {
				snap.relayDownstream(StreamId.random(), updateId, giveAdminProof());
				fail("This should have choked on the bogus update ID");
			} catch (Exception e) {
				// Expected
			}
		}
		
		{// TEST AN INVALID RELAY (relaying to a non-child stream)
			try {
				snap.relayDownstream(stream, updateId, giveAdminProof());
				fail("This should have borked because the stream isn't a child stream");
			} catch (Exception e) {
				// Expected
			}
		}
		
		{// TEST A VALID RELAY
			snap.relayDownstream(childStream, updateId, giveAdminProof());
			
			final InstallableId childVersion = snap.currentVersion(childStream, app, giveAdminProof());
			assertNotNull("Child should have a version specified for app", childVersion);
			assertEquals(i, childVersion);
		}
		
		
		{// TEST ADDING AN APP TO AN EXISTING STREAM
			final AppId otherApp = snap.createJavaApplication("otherApp", "1.0", fakeSpec(), false, giveAdminProof());
			final InstallableId otherAppVersion1 = snap.getAppDetails(otherApp, giveAdminProof()).series().get(0).installables().get(0).id();
			
			{// BOGUS: NONEXISTENT APP
				
				try {
					snap.addAppToStream(stream, AppId.random(), giveAdminProof());
					fail("Should haved choked on this bogus app id");
				} catch (Exception e) {
					// EXPECTED
				}
			}
			{// BOGUS: CAN'T ADD TO A CHILD STREAM
				
				try {
					snap.addAppToStream(childStream, otherApp, giveAdminProof());
					fail("should reject this because you can't add apps directly to child streams");
				} catch (Exception e) {
					// EXPECTED
				}
			}
			{// BOGUS: APP ALREADY IN STREAM
				
				try {
					snap.addAppToStream(stream, app, giveAdminProof());
					fail("SHOULDHAVEFAILED");
				} catch (Exception e) {
					// EXPECTED
				}
			}

			{// SHOULD WORK
				snap.addAppToStream(stream, otherApp, giveAdminProof());
				
				{// ASSERT PRESENCE OF APP IN STREAM
					StreamDetails d = snap.getStreamDetails(stream, giveAdminProof());
					final AppInfo appInfo = d.app(otherApp);
					assertNotNull("Should have info about this app because it was just added", appInfo);
					assertEquals(otherApp, appInfo.id());
					final InstallableId currentVersion = snap.currentVersion(stream, otherApp, giveAdminProof());
					assertNull("Shouldn't be a version yet since there hasn't been a stream update since the app was added", currentVersion);
				}
				{// ASSERT PRESENCE OF APP IN CHILD STREAM
					StreamDetails d = snap.getStreamDetails(childStream, giveAdminProof());
					final AppInfo appInfo = d.app(otherApp);
					assertNotNull("Should have info about this app because it was just added", appInfo);
					assertEquals(otherApp, appInfo.id());
					final InstallableId currentVersion = snap.currentVersion(childStream, otherApp, giveAdminProof());
					assertNull("Shouldn't be a version yet since there hasn't been a stream update since the app was added", currentVersion);
				}
				
				final StreamUpdateId update = snap.sendDownStream(stream, "update for new app", giveAdminProof(), new AppVersionSpec(otherApp, otherAppVersion1));
				
				{// ASSERT NEW VERSION IN STREAM
					final InstallableId currentVersion = snap.currentVersion(stream, otherApp, giveAdminProof());
					assertEquals(otherAppVersion1, currentVersion);
				}
				{// MAKE SURE THE CHILD STREAM STILL HASN'T GOT THE VERSION
					final InstallableId currentVersion = snap.currentVersion(childStream, otherApp, giveAdminProof());
					assertNull("Shouldn't be a version yet since there hasn't been a stream update since the app was added", currentVersion);
				}
				
				snap.relayDownstream(childStream, update, giveAdminProof());
				
				{// ASSERT NEW VERSION IN CHILD STREAM
					final InstallableId currentVersion = snap.currentVersion(childStream, otherApp, giveAdminProof());
					assertEquals(otherAppVersion1, currentVersion);
				}
				
			}
			
		}
		
	}
	
	public void testInstallationsStuff() throws Exception {
		final AppId keeperApp;
		final InstallableId keeperAppVersion;
		{// TEST A GOOD UPLOAD
			
			// MAKE A FAKE LAUNCH SPEC
			final JavaAppSpec spec = fakeSpec();
			
			// PRIME THE KEEP
			postToKeep(spec);
			
			final String appName = "my keeper app";
			keeperApp = snap.createJavaApplication(appName, "1.0", spec, true, giveAdminProof());
			keeperAppVersion = snap.getAppDetails(keeperApp, giveAdminProof()).series().get(0).installables().get(0).id();
		}
		
		final AppId nonKeeperApp;
		final InstallableId nonKeeperAppVersion;
		{// TEST A GOOD UPLOAD
			
			// MAKE A FAKE LAUNCH SPEC
			final JavaAppSpec spec = fakeSpec();
			
			// PRIME THE KEEP
			postToKeep(spec);
			
			final String appName = "my misc game app";
			nonKeeperApp = snap.createJavaApplication(appName, "1.0", spec, false, giveAdminProof());
			nonKeeperAppVersion = snap.getAppDetails(nonKeeperApp, giveAdminProof()).series().get(0).installables().get(0).id();
		}

		
		final StreamId stream = snap.createStream(null, "app updates", SecurityMode.BLACKLIST, giveAdminProof(), keeperApp, nonKeeperApp);
		
		snap.sendDownStream(
				stream,
				"my update",
				giveAdminProof(), 
				new AppVersionSpec(nonKeeperApp, nonKeeperAppVersion),
				new AppVersionSpec(keeperApp, keeperAppVersion)
				);
		
		final GroupId group = snap.createGroup("my group", SecurityMode.WHITELIST, giveAdminProof());
		
		final PublicationId nonKeeperPublication = snap.publishToGroup("Regular user software", "My special app", stream, nonKeeperApp, group, giveAdminProof());
		final PublicationId keeperPublication = snap.publishToGroup("Software Maintainance Tool", "My special app", stream, keeperApp, group, giveAdminProof());
		
		{// TEST BAD MANAGED KEEPER (bogus publication)
			try {
				snap.registerKeeper(PublicationId.random(), giveAdminProof());
				fail("This should have failed");
			} catch (Exception e) {
				// expected
			}
		}
		
		{// TEST BAD MANAGED KEEPER (illegal keeper app)
			try {
				snap.registerKeeper(nonKeeperPublication, giveAdminProof());
				fail("This should have failed");
			} catch (Exception e) {
				// expected
			}
		}
		
		final KeeperRegistrationResponse managedKeeper = snap.registerKeeper(keeperPublication, giveAdminProof());
		{
			
			final KeeperCommands c = snap.getKeeperQueue(managedKeeper.keeper(), managedKeeper.token());
			assertNotNull("Should have a command queue for this keeper", c);
			assertEquals(managedKeeper.keeper(), c.keeper());
			assertNotNull("Should have an list containing a single install command set", c.commands());
			assertEquals(1, c.commands().size());
			assertTrue(c.commands().get(0) instanceof InstallCommand);
		}
		
		final KeeperRegistrationResponse unmanagedKeeper = snap.registerKeeper(null, giveAdminProof());
		{
			
			final KeeperCommands c = snap.getKeeperQueue(unmanagedKeeper.keeper(), unmanagedKeeper.token());
			assertNotNull("Should have a command queue for this keeper", c);
			assertEquals(unmanagedKeeper.keeper(), c.keeper());
			assertNotNull("Should have an empty list of commands", c.commands());
			assertEquals(0, c.commands().size());
		}
		
		{// TEST BASIC INSTALL REGISTRATION AND UNREGISTRATION
			final InstallId install;
			{ // INSTALL
				InstallRegistrationResponse r = snap.registerInstall(nonKeeperPublication, managedKeeper.keeper(), managedKeeper.token(), giveAdminProof());
				assertNotNull("We should have received a response", r);
				install = r.installId();
				assertNotNull("We should have received an install id", install);
				assertNotNull("Should have an list containing a single install command set", r.commands().commands());
				assertEquals(2, r.commands().commands().size());
				
				InstallationCommands installCommands = r.commands().forInstall(install);
				
				assertNotNull("Should have received a command set related to this new install", installCommands);
				
				assertEquals(1, installCommands.commands().size());
				assertTrue(installCommands.commands().get(0) instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) installCommands.commands().get(0);
				assertEquals(nonKeeperAppVersion, installCommand.version());
			}
			
			{ // UNINSTALL
				snap.recallInstall(install, giveAdminProof());
				final KeeperCommands q = snap.getKeeperQueue(managedKeeper.keeper(), managedKeeper.token());
				final InstallationCommands installCommands = q.forInstall(install);
				assertEquals(2, installCommands.commands().size());
				
				{
					Command c = installCommands.commands().get(0);
					assertTrue("The first command should be an install command", c instanceof InstallCommand);
				}
				{
					Command c = installCommands.commands().get(1);
					assertTrue("The latest command should be an uninstall command", c instanceof UninstallCommand);
				}
			}
			{ // TEST COMMAND CONSUMPTION (keeperQueuePoll())
				
				{// BAD (OUT-OF-ORDER) COMMAND CONSUMPTION
					final KeeperCommands q = snap.getKeeperQueue(managedKeeper.keeper(), managedKeeper.token());
					
					try {
						final Command latestCommand = q.commands().get(q.commands().size()-1);
						
						snap.keeperQueuePoll(managedKeeper.keeper(), latestCommand.id(), managedKeeper.token());
						
						fail("This out-of-order command poll should have failed");
					} catch (Exception e) {
						// EXPECTED
					}
				}
				
				{// BAD (BOGUS COMMAND ID) COMMAND CONSUMPTION
					try {
						
						snap.keeperQueuePoll(managedKeeper.keeper(), CommandId.random(), managedKeeper.token());
						
						fail("This command poll should have choked on the bogus id");
					} catch (Exception e) {
						// EXPECTED
					}
				}
				
				{// PROPER, ORDINAL COMMAND CONSUMPTION
					consumeKeeperCommands(managedKeeper);
				}
				
			}
		}
	}
	

	public void testStreamsPushing() throws Exception {
		final AppId app;
		final InstallableId version1;
		{// TEST A GOOD UPLOAD
			
			// MAKE A FAKE LAUNCH SPEC
			final JavaAppSpec spec = fakeSpec();
			
			// PRIME THE KEEP
			postToKeep(spec);
			
			app = snap.createJavaApplication("streams-testing-app", "1.0", spec, false, giveAdminProof());
			version1 = snap.getAppDetails(app, giveAdminProof()).series().get(0).installables().get(0).id();
		}
		
		// CREATE STREAM
		final StreamId stream = snap.createStream(null, "streams-testing-stream", SecurityMode.BLACKLIST, giveAdminProof(), app);
		snap.sendDownStream(stream, "my stream update", giveAdminProof(), new AppVersionSpec(app, version1));
		
		// CREATE GROUP
		final GroupId group = snap.createGroup("streams-testing-group", SecurityMode.BLACKLIST, giveAdminProof());
		
		final PublicationId pub = snap.publishToGroup("this crazy app called love", "My special app", stream, app, group, giveAdminProof());
		
		// REGISTER KEEPER
		final KeeperRegistrationResponse keeper = snap.registerKeeper(null, giveAdminProof());
		
		
		final InstallId install;
		{ // INSTALL
			InstallRegistrationResponse r = snap.registerInstall(pub, keeper.keeper(), keeper.token(), giveAdminProof());
			assertNotNull("We should have received a response", r);
			install = r.installId();
			assertNotNull("We should have received an install id", install);
			assertNotNull("Should have an list containing a single install command set", r.commands().commands());
			assertEquals(1, r.commands().commands().size());

			InstallationCommands installCommands = r.commands().forInstall(install);

			assertNotNull("Should have received a command set related to this new install", installCommands);

			assertEquals(1, installCommands.commands().size());
			assertTrue(installCommands.commands().get(0) instanceof InstallCommand);
			InstallCommand installCommand = (InstallCommand) installCommands.commands().get(0);
			assertEquals(version1, installCommand.version());
		}
		
		final InstallableId version2 = snap.addJavaAppVersion(app, "2.0", null, fakeSpec(), giveAdminProof());
		
		snap.sendDownStream(stream, "second stream update", giveAdminProof(), new AppVersionSpec(app, version2));
		
		{
			KeeperCommands q = snap.getKeeperQueue(keeper.keeper(), keeper.token());
			InstallationCommands installCommands = q.forInstall(install);

			assertNotNull("Should have received a command set related to this install", installCommands);
			
			assertEquals(2, installCommands.commands().size());
			{// FIRST COMMAND
				final Command c = installCommands.commands().get(0);
				assertTrue(c instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) c;
				assertEquals(version1, installCommand.version());
			}
			{// SECOND COMMAND
				final Command c = installCommands.commands().get(1);
				assertTrue(c instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) c;
				assertEquals(version2, installCommand.version());
			}
		}
		
		final InstallableId version3 = snap.addJavaAppVersion(app, "3.0", null, fakeSpec(), giveAdminProof());
		
		snap.sendDownStream(stream, "third stream update", giveAdminProof(), new AppVersionSpec(app, version3));
		
		{
			KeeperCommands q = snap.getKeeperQueue(keeper.keeper(), keeper.token());
			InstallationCommands installCommands = q.forInstall(install);

			assertNotNull("Should have received a command set related to this install", installCommands);
			
			assertEquals(3, installCommands.commands().size());
			{// FIRST COMMAND
				final Command c = installCommands.commands().get(0);
				assertTrue(c instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) c;
				assertEquals(version1, installCommand.version());
			}
			{// SECOND COMMAND
				final Command c = installCommands.commands().get(1);
				assertTrue(c instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) c;
				assertEquals(version2, installCommand.version());
			}
			{// THIRD COMMAND
				final Command c = installCommands.commands().get(2);
				assertTrue(c instanceof InstallCommand);
				InstallCommand installCommand = (InstallCommand) c;
				assertEquals(version3, installCommand.version());
			}
		}
		
		// TEST TO MAKE SURE THE COMMANDS ARE KNOCKED-OFF PROPERLY
		consumeKeeperCommands(keeper);
		
		
		// TODO: Test stream relay
	}
	
	private void consumeKeeperCommands(final KeeperRegistrationResponse keeper) throws SecurityException, IdProovingException{
		// PROPER, ORDINAL COMMAND CONSUMPTION
		CommandId lastCommand = null;
		for(
				KeeperCommands q = snap.getKeeperQueue(keeper.keeper(), keeper.token());
				q.length()>0;
				q = snap.keeperQueuePoll(keeper.keeper(), lastCommand, keeper.token())
		){
			
			Command c = q.earliest();
			// here's where a real keeper would actually execute the command
			lastCommand = c.id();
		}
	}
	
	private void assertAppPresence(final AppId app, final String appName, boolean isKeeperSoftware) throws Exception {
		List<AppInfo> infos = snap.listApps(giveAdminProof());
		
		assertNotNull(infos);
		assertTrue(infos.size()>0);
		
		AppInfo info = null;
		for(AppInfo next : infos){
			if(next.id().equals(app) && next.name().equals(appName)){
				info = next;
			}
		}
		
		assertNotNull("Couldn't find newly created app in listing!", info);
		
		AppDetails details = snap.getAppDetails(app, giveAdminProof());
		
		assertEquals(app, details.id());
		assertEquals(appName, details.name());
		assertEquals(isKeeperSoftware, details.isKeeperSoftware());
		
		assertNotNull(details.series());
		assertEquals(1, details.series().size());
		
		AppVersionSeries defaultPath = details.series().get(0);
		assertEquals(1, defaultPath.installables().size());
		
		
		// MAKE SURE THE RESULTING INSTALLABLE IS VALID
		InstallableId installableId = defaultPath.installables().get(0).id();
		
		assertInstallablePresense(app, installableId);
	}
	
	private void assertInstallablePresense(final AppId app, final InstallableId i) throws Exception {
		
		InstallationPlan plan = snap.getInstallable(i, giveAdminProof());
		
		assertNotNull("No installation plan was provided", plan);
		
		assertEquals(1, plan.componentSources().size());
		AppkeepDownloadVector vector = plan.componentSources().get(0);
		assertNotNull(vector.authorization());
		assertEquals(keepLocation, vector.location());
		

		JavaAppInstallableDetails javaDetails = JavaAppInstallableDetails.grab(plan.details());
		
		assertNotNull(javaDetails);
		assertNotNull(javaDetails.launchSpec());
		assertNotNull(javaDetails.launchSpec().components());
		assertEquals(1, javaDetails.launchSpec().components().size());
		
		Component c = javaDetails.launchSpec().components().get(0);
		
		ComponentInfo cInfo = keep.getInfo(new ComponentHandlesSelector(c.artifactHandles()), vector.authorization());
		
		assertNotNull(cInfo);
	}
	
	private IdProof giveAdminProof() throws IdProovingException {
		return snapAdminProver.giveProof();
	}
	
	private void postToKeep(JavaAppSpec spec){
		for(Component c : spec.components()){
			try {
				
				final ComponentInfo info = keep.getInfo(
						new ComponentHandlesSelector(c.artifactHandles()), 
						new UserAccountDownloadToken(appkeepAdminProver.giveProof())
						);
				
				if(info==null){
					keep.post(
							c.artifactHandles().toArray(new ComponentHandle[c.artifactHandles().size()]), 
							c.type(), 
							appkeepAdminProver.giveProof(), 
							new ByteArrayInputStream(dummyData)
					);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
