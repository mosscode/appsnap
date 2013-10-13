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
/**
 * 
 */
package com.moss.appsnap.api.streams;

import java.util.List;

import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.security.SecurityMode;


public class StreamDetails extends StreamInfo {
	private final List<StreamUpdate> updates;
	private final List<AppVersionInfo> applications;
	private final List<GroupId> groupSecurityRoster;
	private final SecurityMode securityMode;
	
	public StreamDetails(StreamId id, String name, StreamId parentStream,
			List<AppVersionInfo> applications, SecurityMode securityMode, List<GroupId> groupSecurityRoster, List<StreamUpdate> updates) {
		super(id, parentStream, name);
		this.applications = applications;
		this.securityMode = securityMode;
		this.groupSecurityRoster = groupSecurityRoster;
		this.updates = updates;
	}
	
	public AppInfo app(AppId appId){
		for(AppVersionInfo next : applications){
			if(next.appInfo().id().equals(appId)){
				return next.appInfo();
			}
		}
		return null;
	}
	
	public List<AppVersionInfo> applications() {
		return applications;
	}
	
	public SecurityMode securityMode() {
		return securityMode;
	}
	
	public List<GroupId> groupSecurityRoster() {
		return groupSecurityRoster;
	}
	public List<StreamUpdate> updates() {
		return updates;
	}
}