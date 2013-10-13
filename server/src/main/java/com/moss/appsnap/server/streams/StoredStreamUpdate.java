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
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.api.streams.StreamUpdate;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.appsnap.server.installables.Installable;
import com.moss.bdbwrap.DbWrap;
import com.moss.joda.time.xml.InstantAdapter;
import com.sleepycat.je.LockMode;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StoredStreamUpdate {
	private StreamUpdateId id;
	private String label;
	@XmlJavaTypeAdapter(InstantAdapter.class)
	private Instant whenEffective;
	private List<AppVersionSpec> appUpdates = new LinkedList<AppVersionSpec>();
	
	@Deprecated StoredStreamUpdate() {}
	
	public StoredStreamUpdate(StreamUpdateId id, String label, Instant whenEffective, List<AppVersionSpec> appUpdates) {
		super();
		this.id = id;
		this.label = label;
		this.whenEffective = whenEffective;
		this.appUpdates = appUpdates;
	}
	
	public StreamUpdateId id() {
		return id;
	}
	
	private AppInfo find(AppId app, List<? extends AppInfo> apps){
		for(AppInfo next : apps){
			if(next.id().equals(app)){
				return next;
			}
		}
		return null;
	}
	
	public StreamUpdate toDto(final DbWrap<InstallableId, Installable> installables, final List<? extends AppInfo> apps){
		final List<AppVersionInfo> versions = new ArrayList<AppVersionInfo>(appUpdates.size());
		for(AppVersionSpec next : appUpdates){
			final Installable i = installables.get(next.getVersion(), null, LockMode.READ_COMMITTED);
			final AppInfo appInfo = find(next.getApplication(), apps);
			if(appInfo==null){
				throw new NullPointerException();
			}
			
			final AppVersionInfo avi = new AppVersionInfo(appInfo, i.toInfoDto());
			versions.add(avi);
		}
		return new StreamUpdate(id, label, whenEffective, versions);
	}
	public Instant whenEffective() {
		return whenEffective;
	}
	
	public List<AppVersionSpec> appUpdates() {
		return appUpdates;
	}
}
