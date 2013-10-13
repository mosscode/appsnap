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
package com.moss.appsnap.keeper.data;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installs.InstallId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InstallationInfo {
	private InstallId id;
	private PublicationId publication;
	private boolean isKeeperSoftware;
	private String name;
	private InstallableId currentVersion;
	
	/**
	 * This is what the 'current dir' is set to when the program is launched
	 */
	private File launchDir;
	
	@Deprecated InstallationInfo() {}
	
	public InstallationInfo(InstallId id, boolean isKeeperSoftware, PublicationId publication, String name) {
		super();
		this.id = id;
		this.isKeeperSoftware = isKeeperSoftware;
		this.publication = publication;
		this.name = name;
	}
	public InstallableId currentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(InstallableId currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	public InstallId id() {
		return id;
	}
	public String name() {
		return name;
	}
	public PublicationId publication() {
		return publication;
	}
	public boolean isKeeperSoftware() {
		return isKeeperSoftware;
	}
	public File launchDir() {
		return launchDir;
	}
}
