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
package com.moss.appsnap.server.groups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.api.groups.AppPublicationInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.InstallableInfo;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.server.Data;
import com.moss.appsnap.server.installables.Installable;
import com.moss.appsnap.server.installables.StoredApp;
import com.moss.appsnap.server.streams.StoredStream;
import com.moss.appsnap.server.streams.StreamAppStatus;
import com.sleepycat.je.LockMode;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Publication {
	private PublicationId id; 
	private String name;
	private String description;
	private StreamId stream;
	private AppId app;
	private boolean isKeeperPublication;
	private AppType type;
	
	@Deprecated Publication() {}
	
	public Publication(PublicationId id, String name, String description, StreamId stream, AppId app, boolean isKeeperPublication, final AppType type) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.stream = stream;
		this.app = app;
		this.isKeeperPublication = isKeeperPublication;
		this.type = type;
	}
	
	public PublicationInfo toInfoDto(){
		return new PublicationInfo(id, name, description, isKeeperPublication, type);
	}
	public AppId app() {
		return app;
	}
	
	public PublicationId id() {
		return id;
	}
	
	public StreamId stream() {
		return stream;
	}
	
	public void name(String name) {
		this.name = name;
	}
	
	public void description(String description) {
		this.description = description;
	}
	
	public String name() {
		return name;
	}
	
	public AppPublicationInfo toDto(final Data data){
		final StoredStream stream = data.streams.get(this.stream, null, LockMode.READ_COMMITTED);
		final StoredApp app = data.apps.get(this.app, null, LockMode.READ_COMMITTED);
		final StreamAppStatus status = stream.currentStatus(this.app);
		final InstallableInfo versionInfo;
		if(status.version()!=null){
			final Installable i = data.installables.get(status.version(), null, LockMode.READ_COMMITTED);
			versionInfo = i.toInfoDto();
		}else{
			versionInfo = null;
		}
		return new AppPublicationInfo(id, name, description, app.infoDto(), versionInfo, this.stream);
	}
}
