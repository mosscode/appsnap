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
package com.moss.appsnap.api.installables;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.moss.appkeep.api.ComponentId;
import com.moss.launch.components.Component;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.launch.spec.JavaAppletSpec;
import com.moss.launch.spec.app.AppProfile;
import com.moss.launch.spec.app.bundle.BundleSpec;
import com.moss.launch.spec.applet.AppletProfile;

@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("serial")
public class ComponentResolveTool implements Serializable {
	
	public static  List<Component> allComponentsInResolutionOrder(JavaAppSpec launchSpec){
		final List<Component> components = new LinkedList<Component>();
		
		{
			components.addAll(launchSpec.components());
			
			for(BundleSpec b : launchSpec.bundles()){
				components.addAll(b.components());
			}
			
			for(AppProfile p : launchSpec.profiles()){
				components.addAll(p.components());
			}
		}
		return components;
	}

	public static final List<Component> allComponentsInResolutionOrder(JavaAppletSpec launchSpec) {
		List<Component> components = new LinkedList<Component>();

		components.addAll(launchSpec.components());

		for(AppletProfile p : launchSpec.profiles()){
			components.addAll(p.components());
		}
		return components;
	}
	
	
	private final List<Component> components;
	private final List<ComponentId> resolutions;
	
	public ComponentResolveTool(JavaAppletSpec launchSpec, List<ComponentId> resolutions) {
		this.resolutions = resolutions;
		this.components = allComponentsInResolutionOrder(launchSpec);
	}
	
	public ComponentResolveTool(JavaAppSpec launchSpec, List<ComponentId> resolutions) {
		this.resolutions = resolutions;
		this.components = allComponentsInResolutionOrder(launchSpec);
	}
	
	public ComponentId resolve(Component c){
		final int index = components.indexOf(c);
		return resolutions.get(index);
	}
	
}