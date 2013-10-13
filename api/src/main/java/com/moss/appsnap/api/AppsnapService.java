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
package com.moss.appsnap.api;

import java.util.List;

import com.moss.appkeep.api.endorse.x509.X509CertId;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppVersionSpec;
import com.moss.appsnap.api.apps.LaunchIntegrityException;
import com.moss.appsnap.api.catalog.GroupCatalog;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.InstallableDetails;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installs.CommandId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.InstallRegistrationResponse;
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
import com.moss.identity.Id;
import com.moss.identity.IdProof;
import com.moss.identity.IdProofCheckRecipe;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.launch.spec.JavaAppletSpec;

/*
 * EXPANSION:
 *   Make it work with non-java clients.
 * 
 * SECURITY:
 *   Will we need some sort of different mechanism for authorizing requests from 'regular' clients vs admin stuff?
 *   
 * CLIENTS:
 *   How are streams represented in the UI (apart from groups)?  The 'publications' tab?
 *   How will the AppBrowser in the installer interact with the service?
 */
public interface AppsnapService {
	
	//####################################################
	//# "PUBLIC" INTERFACE
	//####################################################
	
	AppsnapServiceInfo serviceInfo();
	
	List<GroupCatalog> getSoftwareCatalog(IdProof credentials) throws SecurityException;
	
	RegisterUserOutcome registerUser(IdProofCheckRecipe identification) throws SecurityException;
	
	UserInfo myAccount(IdProof credentials) throws SecurityException;
	
	/**
	 * @param lastCompletedCommand The id of the last command the keeper has successfully completed
	 * @return The list of outstanding commands (the commands since the 'lastCompletedCommand')
	 */
	KeeperCommands keeperQueuePoll(KeeperId keeper, CommandId lastCompletedCommand, AuthorizationToken keeperToken) throws SecurityException;
	
	
	InstallationPlan getInstallable(InstallableId id, IdProof credentials) throws SecurityException;
	
	InstallableDetails getInstallableDetails(InstallableId id, IdProof credentials) throws SecurityException;
	
	/**
	 * Used by a client to initiate a download of an installable.  This provides everything the client needs
	 * to be able to:
	 *    1) go out and get the parts needed to assemble the app, and then
	 *    2) assemble the app and launch it
	 */
	InstallationPlan getInstallableForKeeper(InstallableId id, CommandId commandId, KeeperId keeper, AuthorizationToken keeperToken) throws SecurityException;
	
	/**
	 * Used bu a client (typically an unmanged client) to get the current version for a particular app
	 * and stream.
	 */
	InstallableId currentVersion(StreamId stream, AppId app, IdProof credentials) throws SecurityException;
	
	
	//####################################################
	//# ADMINISTRATION INTERFACE
	//####################################################

	/*|------------------------------------------------------|
	 *|           SYSTEM MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	
	List<Url> listKeepers(IdProof credentials) throws SecurityException;
	
	/*|------------------------------------------------------|
	 *|           APPLICATIONS MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	InstallableId addJavaAppVersion(AppId application, String seriesName, String versionLabel, JavaAppSpec launchSpec, IdProof credentials) throws LaunchIntegrityException, SecurityException;
	
	InstallableId addJavaAppletVersion(AppId application, String seriesName, String versionLabel, JavaAppletSpec launchSpec, X509CertId jarsignCertificate, IdProof credentials) throws LaunchIntegrityException, SecurityException;
	
	AppId createJavaApplication(String name, String initialVersionLabel, JavaAppSpec launchSpec, boolean isKeeperSoftware, IdProof credentials) throws LaunchIntegrityException, SecurityException;
	
	AppId createJavaApplet(String name, String initialVersionLabel, JavaAppletSpec launchSpec, X509CertId jarsignCertificate, IdProof credentials) throws LaunchIntegrityException, SecurityException;
	
	AppDetails getAppDetails(AppId id, IdProof credentials) throws SecurityException;
	
	void renameApplication(AppId id, String name, IdProof credentials) throws SecurityException;
	
	List<AppInfo> listApps(IdProof credentials) throws SecurityException;
		
	/*|------------------------------------------------------|
	 *|                  GROUP MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	 
	List<GroupInfo> listGroups(IdProof credentials) throws SecurityException;
	
	GroupDetails getGroupDetails(GroupId group, IdProof credentials) throws SecurityException;
	
	GroupId createGroup(String name, SecurityMode type, IdProof credentials) throws SecurityException;
	
	PublicationId publishToGroup(String publicationName, String description, StreamId stream, AppId app, GroupId group, IdProof credentials) throws SecurityException;
	
	void addGroupMembers(GroupId group, List<Id> members, IdProof credentials) throws SecurityException;
	void removeGroupMembers(GroupId group, List<Id> members, IdProof credentials) throws SecurityException;
	
	void updateGroup(GroupId group, String name, SecurityMode type, IdProof credentials) throws SecurityException;
	
	void renamePublication(PublicationId publication, String newName, String description, IdProof credentials) throws SecurityException;

	/*|------------------------------------------------------|
	 *|                STREAMS MANAGEMENT                    |
	 *|------------------------------------------------------|*/
	 List<StreamInfo> listStreams(IdProof credentials) throws SecurityException;
	
	StreamId createStream(StreamId parent, String name, SecurityMode groupSecurityMode, IdProof credentials, AppId ... applications) throws SecurityException;
	
	/**
	 * This is how you create a stream update.
	 */
	StreamUpdateId sendDownStream(StreamId stream, String label, IdProof credentials, AppVersionSpec ... versions) throws SecurityException;
	
	/**
	 * This is how you push/pull an update down a subsidiary (child) stream.
	 */
	void relayDownstream(StreamId stream, StreamUpdateId upstreamUpdate, IdProof credentials) throws SecurityException;
	
	/*
	 * TODO: Should we add a threshold (Instant) here?
	 */
	List<StreamUpdate> listStreamUpdates(StreamId id, IdProof credentials) throws SecurityException;
	
	StreamDetails getStreamDetails(StreamId stream, IdProof credentials) throws SecurityException;
	
	void updateStream(StreamId id, String name, SecurityMode securityMode, List<GroupId> groupSecurityRoster, IdProof credentials) throws SecurityException ;
	
	void addAppToStream(StreamId stream, AppId app, IdProof credentials) throws SecurityException;
	
	/*|------------------------------------------------------|
	 *|              INSTALLATIONS MANAGEMENT                |
	 *|------------------------------------------------------|*/
	/**
	 * Registers a new keeper.  A keeper can be either a managed installation or an unmanaged installation
	 * (possibly even of software that is entirely unhandled by this appsnap service).  In the case of
	 * the installation of a keeper
	 */
	KeeperRegistrationResponse registerKeeper(PublicationId keeperSoftware, IdProof credentials) throws SecurityException;
	InstallRegistrationResponse registerInstall(PublicationId publication, KeeperId keeper, AuthorizationToken keeperToken, IdProof credentials) throws SecurityException;
	void recallInstall(InstallId installId, IdProof credentials) throws SecurityException;
	
	KeeperCommands getKeeperQueue(KeeperId keeper, AuthorizationToken keeperToken) throws SecurityException;
	KeeperCommands getKeeperHistory(KeeperId keeper, IdProof credentials) throws SecurityException;
	
}
