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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.moss.appkeep.api.ComponentId;
import com.moss.appsnap.api.installables.ComponentResolveTool;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.launch.components.Component;
import com.moss.launch.spec.JavaAppSpec;

@XmlRootElement(name="resolved-launch-spec")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResolvedJavaLaunchSpec {
	private InstallableId id;
	private JavaAppSpec spec;
	private List<ComponentId> componentResolutions;
	
	@Deprecated ResolvedJavaLaunchSpec() {}
	
	public ResolvedJavaLaunchSpec(InstallableId id, JavaAppSpec spec, List<ComponentId> componentResolutions) {
		super();
		this.id = id;
		this.spec = spec;
		this.componentResolutions = componentResolutions;
	}
	public InstallableId id() {
		return id;
	}
	public JavaAppSpec spec() {
		return spec;
	}
	
	@XmlTransient
	private List<Component> componentsInResolutionOrder;
	
	public ComponentId resolutionFor(final Component c){
		if(componentsInResolutionOrder==null){
			componentsInResolutionOrder = ComponentResolveTool.allComponentsInResolutionOrder(spec);
		}
		
		int index = componentsInResolutionOrder.indexOf(c);
		return componentResolutions.get(index);
//		for(ResolvedComponent next : componentResolutions){
//			if(next.specComponent()==c){
//				return next;
//			}
//		}
//		throw new RuntimeException("There is no resolution for component " + c);
	}
}
