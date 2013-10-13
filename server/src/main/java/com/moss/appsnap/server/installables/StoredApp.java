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
package com.moss.appsnap.server.installables;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.bdbwrap.DbWrap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StoredApp {
	private AppId id;
	private AppType type;
	private String name;
	private final List<StoredAppVersionSeries> series = new LinkedList<StoredAppVersionSeries>();
	private boolean isKeeperSoftware = false;
	
	@Deprecated StoredApp() {}
	
	public StoredApp(AppId id, String name, AppType type, boolean isKeeperSoftware) {
		super();
		this.id = id;
		this.name = name;
		this.isKeeperSoftware = isKeeperSoftware;
		this.type = type;
	}
	
	public boolean isKeeperSoftware(){
		return isKeeperSoftware;
	}
	public AppType type() {
		return type;
	}
	public StoredAppVersionSeries newSeries(String name){
		if(series(name)!=null){
			throw new RuntimeException("There is already a series named " + name);
		}

		StoredAppVersionSeries newSeries = new StoredAppVersionSeries(name);
		this.series.add(newSeries);
		return newSeries;
	}
	
	public AppId id() {
		return id;
	}
	public String name() {
		return name;
	}
	public void name(String name) {
		this.name = name;
	}
	
	public StoredAppVersionSeries addDefaultPath(){
		return newSeries("default");
	}
	
	public StoredAppVersionSeries defaultSeries(){
		return series("default");
	}
	
	public boolean hasSeries(String name){
		return series(name)!=null;
	}
	public StoredAppVersionSeries series(String name){
		StoredAppVersionSeries p = null;
		
		for(StoredAppVersionSeries next : series){
			if(next.name().equals(name)){
				p = next;
				break;
			}
		}
		return p;
	}
	
	public List<StoredAppVersionSeries> series() {
		return series;
	}
	
	public AppInfo infoDto(){
		AppType type = this.type;
		if(type==null){
			Log log = LogFactory.getLog(getClass());
			log.warn("automagically making this look like a java app");
			type = AppType.JAVA_APP;
		}
		return new AppInfo(id, name, type);
	}
	public AppDetails detailsDto(DbWrap<InstallableId, Installable> installables){
		AppVersionSeries[] paths = new AppVersionSeries[this.series.size()];
		
		for(int x=0;x<this.series.size();x++){
			paths[x] = this.series.get(x).toDto(installables);
		}
		
		return new AppDetails(id, name, isKeeperSoftware, type, paths);
		
	}
}
