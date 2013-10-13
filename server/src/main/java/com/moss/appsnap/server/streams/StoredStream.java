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
package com.moss.appsnap.server.streams;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.Instant;

import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppVersionSpec;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.api.streams.StreamUpdate;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.appsnap.server.Data;
import com.moss.appsnap.server.installables.Installable;
import com.moss.appsnap.server.installables.StoredApp;
import com.moss.joda.time.xml.InstantAdapter;
import com.sleepycat.je.LockMode;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StoredStream {
	private StreamId id;
	private StreamId parentStream;
	private String name;
	private SecurityMode securityMode;
	@XmlJavaTypeAdapter(InstantAdapter.class)
	private Instant whenCreated;
	private List<GroupId> securityRoster = new LinkedList<GroupId>();
	private List<StreamAppStatus> apps = new LinkedList<StreamAppStatus>();
	private List<StreamUpdateId> updates = new LinkedList<StreamUpdateId>();
	
	@Deprecated StoredStream() {}
	
	public StoredStream(StreamId id, String name, SecurityMode securityMode, StreamId parentStream, Instant whenCreated) {
		super();
		this.id = id;
		this.name = name;
		this.securityMode = securityMode;
		this.parentStream = parentStream;
		this.whenCreated = whenCreated;
	}
	
	public boolean servesApp(AppId app){
		return currentStatus(app)!=null;
	}
	
	public StreamAppStatus currentStatus(AppId app){
		for(StreamAppStatus next : apps){
			if(next.app().equals(app)){
				return next;
			}
		}
		return null;
	}
	public List<StreamAppStatus> apps() {
		return apps;
	}
	
	public boolean hasUpdate(StreamUpdateId update){
		boolean hasIt = false;
		
		for(StreamUpdateId next : updates()){
			if(next.equals(update)){
				hasIt = true;
			}
		}
		
		return hasIt;
	}
	
	public void addUpdate(StoredStreamUpdate u){
		
		
		for(final AppVersionSpec next : u.appUpdates()){
			final StreamAppStatus status = currentStatus(next.getApplication());
			if(status==null){
				throw new RuntimeException("Stream "+ id + " doesnt' track application " + next.getApplication());
			}else{
				status.update(u.id(), next.getVersion());
			}
		}
		
		for(AppVersionSpec a : u.appUpdates()){
			for(AppVersionSpec b : u.appUpdates()){
				if(a!=b && a.getApplication().equals(b.getApplication())){
					throw new RuntimeException("Multiple specs for same app not allowed");
				}
			}
			
		}
		
		this.updates.add(u.id());
	}
	public StreamId id() {
		return id;
	}
	
	public void addApp(AppId app){
		if(app==null){
			throw new NullPointerException();
		}else{
			this.apps.add(new StreamAppStatus(app, null, null));
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSecurityMode(SecurityMode securityMode) {
		this.securityMode = securityMode;
	}
	
	public void setSecurityRoster(List<GroupId> securityRoster) {
		this.securityRoster = securityRoster;
	}
	
	public List<StreamUpdateId> updates() {
		return updates;
	}
	
	public StreamDetails toDetailsDto(Data data){
		List<AppInfo> appInfos = new LinkedList<AppInfo>();
		List<AppVersionInfo> versionInfo = new LinkedList<AppVersionInfo>();
		
		for(StreamAppStatus next : this.apps){
			final StoredApp app = data.apps.get(next.app(), null, LockMode.READ_COMMITTED);
			final Installable i = data.installables.get(next.version(), null, LockMode.READ_COMMITTED);
			final AppInfo appInfo = app.infoDto();
			versionInfo.add(new AppVersionInfo(appInfo, i==null?null:i.toInfoDto()));
			appInfos.add(appInfo);
		}
		
		List<StreamUpdate> updates = new ArrayList<StreamUpdate>(this.updates.size());
		for(StreamUpdateId next : this.updates){
			updates.add(data.streamUpdates.get(next, null, LockMode.READ_COMMITTED).toDto(data.installables, appInfos));
		}
		
		return new StreamDetails(id, name, parentStream, versionInfo, securityMode, this.securityRoster, updates);
	}
	
	public StreamId parentStream() {
		return parentStream;
	}
	
	private boolean isInRoster(GroupId group){
		boolean isInRoster = false;
		{
			for(GroupId next : this.securityRoster){
				if(next.equals(group)){
					isInRoster = true;
				}
			}
		}
		
		return isInRoster;
	}
	
	public boolean isAllowedAt(final GroupId group){
		final boolean isAllowed;
		
		final boolean isRosterMember = isInRoster(group);
		
		switch(securityMode){
		case WHITELIST:
			isAllowed = isRosterMember;
			break;
		case BLACKLIST:
			isAllowed = !isRosterMember;
			break;
		default:
			throw new RuntimeException("Unknown security mode: " + securityMode);
		}
		
		return isAllowed;
	}
	
	public StreamInfo toInfoDto(){
		return new StreamInfo(id, parentStream, name);
	}
}
