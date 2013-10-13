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
package com.moss.appsnap.keeper;

import java.io.File;

import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.appsnap.keeper.freedesktop.FreeDesktopIntegrationStrategy;
import com.moss.appsnap.keeper.macosx.MacOSXDesktopIntegrationStrategy;
import com.moss.appsnap.keeper.windows.MSWindowsDesktopIntegrationStrategy;

public class Bootstrapper {
	
	public File dataDir(AppsnapServiceId serviceId){
		return discover().keeperDataDir(serviceId);
	}
	
	public DesktopIntegrationStrategy discover(){
		final DesktopIntegrationStrategy handlerFactory;
		{// FIGURE OUT WHAT OS WE'RE RUNNING ON
			final String osName = System.getProperty("os.name");
			
			if(osName.toLowerCase().contains("windows")){
				handlerFactory = new MSWindowsDesktopIntegrationStrategy();
			}else if(osName.equals("Linux")){
				handlerFactory = new FreeDesktopIntegrationStrategy();
			}else if(osName.equals("MacOSX")){
				handlerFactory = new MacOSXDesktopIntegrationStrategy();
			}else{
				throw new RuntimeException("No handler available for environment: (os=" + osName + ")");
			}
		}
		return handlerFactory;
	}
	
	private static boolean matches(String value, String[] matchables){
		for(String next : matchables){
			if(next.equals(value)){
				return true;
			}
		}
		return false;
	}
}
