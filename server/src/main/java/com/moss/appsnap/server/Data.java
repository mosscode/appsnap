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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.installs.KeeperId;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.appsnap.server.data.IdentityFromStringFactory;
import com.moss.appsnap.server.groups.Publication;
import com.moss.appsnap.server.groups.StoredGroup;
import com.moss.appsnap.server.installables.Installable;
import com.moss.appsnap.server.installables.StoredApp;
import com.moss.appsnap.server.installables.javaapplets.JavaAppletInstallable;
import com.moss.appsnap.server.installables.javaapps.JavaAppInstallable;
import com.moss.appsnap.server.installs.KeeperRegistration;
import com.moss.appsnap.server.installs.StoredInstall;
import com.moss.appsnap.server.installs.commands.StoredInstallCommand;
import com.moss.appsnap.server.installs.commands.StoredUninstallCommand;
import com.moss.appsnap.server.security.UserAccount;
import com.moss.appsnap.server.streams.StoredStream;
import com.moss.appsnap.server.streams.StoredStreamUpdate;
import com.moss.bdbwrap.DbWrap;
import com.moss.bdbwrap.EnvironmentWrap;
import com.moss.bdbwrap.SecondaryDbWrap;
import com.moss.bdbwrap.defaults.DefaultJaxbDbWrap;
import com.moss.bdbwrap.defaults.DefaultMultiKeyJaxbSecondaryDbWrap;
import com.moss.bdbwrap.defaults.DefaultSingleKeyJaxbSecondaryDbWrap;
import com.moss.bdbwrap.jaxb.JaxbContextProvider;
import com.moss.bdbwrap.tostring.StringFromStringFactory;
import com.moss.bdbwrap.tostring.ToStringSerializer;
import com.moss.identity.Id;
import com.moss.identity.simple.SimpleId;
import com.moss.identity.veracity.VeracityId;

public class Data extends EnvironmentWrap {

	private final JaxbContextProvider jaxb = new JaxbContextProvider() {
		public JAXBContext context() {
			return jaxbCtx;
		}
	};
	
	public final DbWrap<KeeperId, KeeperRegistration> keepers = new DefaultJaxbDbWrap<KeeperId, KeeperRegistration>(
			"keepers",
			KeeperId.class,
			jaxb,
			this
		);
	
	public final DbWrap<InstallId, StoredInstall> installs = new DefaultJaxbDbWrap<InstallId, StoredInstall>(
			"installs",
			InstallId.class,
			jaxb,
			this
		);
	
	public final SecondaryDbWrap<StreamId, StoredInstall> installByStream = new DefaultSingleKeyJaxbSecondaryDbWrap<StreamId, StoredInstall>(
			"installs-by-stream",
			installs,
			StreamId.class
		) {
		@Override
		public StreamId extractKey(StoredInstall data) {
			return data.stream();
		}
	};
	
	public final CommandsData commandsQueue = new CommandsData("queue", jaxb, this);
	
	public final CommandsData commandsHistory = new CommandsData("history", jaxb, this);
	
	public final DbWrap<Id, UserAccount> userAccounts = new DefaultJaxbDbWrap<Id, UserAccount>(
			"user-accounts", 
			new IdentityFromStringFactory(),
			jaxb, 
			this
		);
	
	public final DbWrap<AppId, StoredApp> apps = new DefaultJaxbDbWrap<AppId, StoredApp>(
			"applications", 
			AppId.class,
			jaxb, 
			this
		);
	
	public final DbWrap<InstallableId, Installable> installables = new DefaultJaxbDbWrap<InstallableId, Installable> (
			"installables", 
			InstallableId.class,
			jaxb, 
			this
		);
	
	public final DbWrap<GroupId, StoredGroup> groups = new DefaultJaxbDbWrap<GroupId, StoredGroup>(
			"groups",
			GroupId.class,
			jaxb,
			this
		);

	public final SecondaryDbWrap<PublicationId, StoredGroup> groupsByPublication = new DefaultMultiKeyJaxbSecondaryDbWrap<PublicationId, StoredGroup>(
			"groups-by-publication",
			groups,
			PublicationId.class
			) {
		@Override
		protected List<PublicationId> createKeys(StoredGroup data) {
			final List<PublicationId> keys = new ArrayList<PublicationId>(data.publications().size());
			for(Publication p : data.publications()){
				keys.add(p.id());
			}
			return keys;
		}
	}.withImmutableSecondaryKey(false);
		
	public final DbWrap<StreamId, StoredStream> streams = new DefaultJaxbDbWrap<StreamId, StoredStream>(
			"streams",
			StreamId.class,
			jaxb,
			this
		);
	
	public final SecondaryDbWrap<StreamId, StoredStream> streamsByParent = new DefaultSingleKeyJaxbSecondaryDbWrap<StreamId, StoredStream>(
			"streams-by-parent",
			streams,
			StreamId.class
		) {
			@Override
			public StreamId extractKey(StoredStream data) {
				return data.parentStream();
			}
		};
	
	public final DbWrap<StreamUpdateId, StoredStreamUpdate> streamUpdates = new DefaultJaxbDbWrap<StreamUpdateId, StoredStreamUpdate>(
			"stream-updates",
			StreamUpdateId.class,
			jaxb,
			this
		);
	
	public final DbWrap<String, String> passwords = new DbWrap<String, String>(
			"passwords",
			new ToStringSerializer<String>(new StringFromStringFactory()),
			new ToStringSerializer<String>(new StringFromStringFactory()),
			this
		);
	
	private final JAXBContext jaxbCtx;
	
	private final long commandOrderBase = System.currentTimeMillis();
	private long commandOrderNumber = 0;
	
	public Data(File location, long cacheSize) throws Exception {
		super(location,  cacheSize);
		
		jaxbCtx = JAXBContext.newInstance(
					VeracityId.class,
					SimpleId.class,
					UserAccount.class,
					StoredApp.class,
					
					Installable.class,
					JavaAppInstallable.class,
					JavaAppletInstallable.class,
					
					StoredGroup.class,
					StoredStream.class,
					StoredStreamUpdate.class,
					StoredInstall.class,
					KeeperRegistration.class,
					
					StoredInstallCommand.class,
					StoredUninstallCommand.class
				);
		
		load();
	}
	
	public synchronized String nextCommandOrderNumber(){
		commandOrderNumber++;
		
		return commandOrderBase + "-" + commandOrderNumber;
	}
}
