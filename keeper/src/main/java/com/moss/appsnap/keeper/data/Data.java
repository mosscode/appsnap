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
import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.moss.appkeep.tools.cache.AppkeepComponentCache;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.keeper.data.jaxbstore.JaxbStore;
import com.moss.appsnap.keeper.data.jaxbstore.SingletonJaxbStore;
import com.moss.jaxbhelper.JAXBHelper;

public class Data {
	public final JaxbStore<InstallableId, ResolvedJavaLaunchSpec> javaLaunchSpecs;
	
	public final JaxbStore<InstallId, InstallationInfo> installs;
	public final JaxbStore<InstallId, InstallationInfo> uninstalls;
	
	public final SingletonJaxbStore<KeeperConfig> config;

	public final AppkeepComponentCache componentsCache;
	
	public final File path;
	
	public Data(final File path) throws IOException {
		this.path = path;
		final JAXBHelper helper;
		try {
			helper = new JAXBHelper(
						ResolvedJavaLaunchSpec.class,
						InstallationInfo.class,
						KeeperConfig.class
					);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		componentsCache = new AppkeepComponentCache(new File(path, "components"));
		
		javaLaunchSpecs = new JaxbStore<InstallableId, ResolvedJavaLaunchSpec>(
				new File(path, "java-launch-specs"),
				helper
			) {
				@Override
				protected String fileName(InstallableId id) {
					return id.uuid().toString();
				}
			};
		
		installs = new JaxbStore<InstallId, InstallationInfo>(
				new File(path, "installs"),
				helper
			) {
				@Override
				protected String fileName(InstallId id) {
					return id.uuid().toString();
				}
			};
			
			
		uninstalls = new JaxbStore<InstallId, InstallationInfo>(
				new File(path, "uninstalls"),
				helper
			) {
				@Override
				protected String fileName(InstallId id) {
					return id.uuid().toString();
				}
			};
			
		config = new SingletonJaxbStore<KeeperConfig>(
				new File(path, "config.xml"),
				helper
			);
	}
	
}
