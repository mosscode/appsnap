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
package com.moss.appsnap.server;

import java.io.File;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.appsnap.server.config.AdministratorConfig;
import com.moss.appsnap.server.config.ServerConfiguration;
import com.moss.identity.simple.SimpleId;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.standard.PasswordProofRecipie;
import com.moss.identity.tools.IdProover;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

public class HessianAppsnapServiceTest extends AbstractAppsnapServiceTest {
	private static final int APPSNAP_PORT=6032;
	private static final String APPSNAP_HOST="127.0.0.1";
	
	private static final SimpleId APPSNAP_ADMIN_LOGON = APPKEEP_LOGON;
	private static final String APPSNAP_ADMIN_PASS = APPKEEP_PASS;
	
	private AppSnapServer snapServer;
	
	@Override
	protected IdProover serviceAdminProver() {
		return new SimpleIdProover(APPSNAP_ADMIN_LOGON, APPSNAP_ADMIN_PASS);
	}
	
	@Override
	protected AppsnapService service(File dataLocation) {
		ProxyFactory proxyFactory = new ProxyFactory(new HessianProxyProvider());
		{
			// START UP AN EMBEDDED SERVER
			ServerConfiguration config = new ServerConfiguration();
			config.serviceName("Test service 123");
			config.serviceId(AppsnapServiceId.random());
			config.storageDir(dataLocation);
			config.bindPort(APPSNAP_PORT);
			config.publishPort(config.bindPort());
			config.bindAddress("127.0.0.1");
			config.publishAddress(config.bindAddress());
			config.idProofRecipie(new PasswordProofRecipie(APPSNAP_ADMIN_LOGON, APPSNAP_ADMIN_PASS));
			config.keepLocations().add(super.keepLocation);
			config.administrators().add(new AdministratorConfig(APPSNAP_ADMIN_LOGON, new PasswordProofRecipie(APPSNAP_ADMIN_LOGON, APPSNAP_ADMIN_PASS)));
			snapServer = new AppSnapServer(config, proxyFactory);
		}
		return proxyFactory.create(AppsnapService.class, "http://" + APPSNAP_HOST + ":" + APPSNAP_PORT + "/rpc");
	}
	
	@Override
	protected void tearDownService() throws Exception {
		snapServer.shutdown();
	}

}
