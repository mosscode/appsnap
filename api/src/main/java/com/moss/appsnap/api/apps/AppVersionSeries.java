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
package com.moss.appsnap.api.apps;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.moss.appsnap.api.installables.InstallableInfo;

/**
 * A version path identifies a series of related versions of a given application.
 * It more or less corresponds to the series of releases made from a given 'branch' of
 * an application.
 * 
 * For example, Mocrosoft Swerd exists in 2 versions: 1.x, and 2.x.  Mocrosoft continues
 * to release updates to both version of Swerd.  In appsnap, each version of the product 
 * (1.x, 2.x) would have its own AppVersionPath.  Program updates such as 1.0.1 would
 * show up as new installables in the "1.x" AppVersionPath, while updates such as 2.1.2
 * would show up in the "2.x" AppVersionPath.
 */
@SuppressWarnings("serial")
public class AppVersionSeries implements Serializable {
	private String name;
	private List<InstallableInfo> installables = new LinkedList<InstallableInfo>();
	
	public AppVersionSeries(String name, List<InstallableInfo> installables) {
		super();
		this.name = name;
		this.installables = installables;
	}
	
	public String name() {
		return name;
	}
	
	public List<InstallableInfo> installables() {
		return installables;
	}
}
