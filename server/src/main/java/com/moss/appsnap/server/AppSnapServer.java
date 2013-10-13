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

import javax.xml.bind.JAXBContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.log.Slf4jLog;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.server.config.AdministratorConfig;
import com.moss.appsnap.server.config.ServerConfiguration;
import com.moss.appsnap.server.installables.javaapplets.AppletPublisher;
import com.moss.appsnap.server.security.PasswordCheckerImpl;
import com.moss.appsnap.server.security.UserAccount;
import com.moss.bdbadmin.core.AuthorizationLevel;
import com.moss.bdbadmin.core.Authorizer;
import com.moss.bdbadmin.core.BdbCategory;
import com.moss.bdbadmin.core.BdbEnv;
import com.moss.bdbadmin.core.BdbPrimaryDb;
import com.moss.bdbadmin.core.BdbSecondaryDb;
import com.moss.bdbadmin.core.BdbService;
import com.moss.bdbadmin.jetty.BdbAdminJettyAdapter;
import com.moss.bdbwrap.DbWrap;
import com.moss.bdbwrap.EnvironmentWrap;
import com.moss.bdbwrap.SecondaryDbWrap;
import com.moss.identity.Id;
import com.moss.identity.simple.SimpleId;
import com.moss.identity.simple.SimpleIdIdToolPlugin;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.standard.PasswordIdProofCheckRecipe;
import com.moss.identity.standard.PasswordProofRecipie;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdTool;
import com.moss.identity.veracity.VeracityId;
import com.moss.identity.veracity.VeracityIdToolPlugin;
import com.moss.jaxbhelper.JAXBHelper;
import com.moss.rpcutil.jetty.SwitchingContentHandler;
import com.moss.rpcutil.jetty.hessian.HessianContentHandler;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;
import com.sleepycat.je.LockMode;

public class AppSnapServer {

	public static void main(String[] args) throws Exception {
		System.setProperty("org.mortbay.log.class", Slf4jLog.class.getName());
		
		File log4jConfigFile = new File("log4j.xml");
		
		if (log4jConfigFile.exists()) {
			DOMConfigurator.configureAndWatch(log4jConfigFile.getAbsolutePath(), 1000);
		}
		else {
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.INFO);
		}

		
		ServerConfiguration config;
		
		JAXBContext context = JAXBContext.newInstance(ServerConfiguration.class, VeracityId.class);
		JAXBHelper helper = new JAXBHelper(context);
		
		Logger log = Logger.getLogger(AppSnapServer.class);
		
		
		File[] configPaths = new File[]{
				new File("settings.xml"),
				new File(new File(System.getProperty("user.home")), ".appsnap-server.xml"),
				new File("/etc/appsnap-server.xml")
		};
		
		
		File configFile = null;
		
		for(File next : configPaths){
			if(next.exists()){
				configFile = next;
			}
		}
		
		if(configFile==null){
			
			log.warn("No config file found.");
			
			for(int x=configPaths.length-1;x>=0;x--){
				File next = configPaths[x];
				log.warn("Attempting to create default config file at " + next.getAbsolutePath());
				ServerConfiguration defaults = new ServerConfiguration();
				defaults.idProofRecipie(new PasswordProofRecipie(new SimpleId("mr-admin-dude"), "my-super-secret-password"));
				try {
					helper.writeToFile(helper.writeToXmlString(defaults), next);
					log.warn("done");
					break;
				} catch (Exception e) {
					log.warn("Error writing file: " + e.getMessage(), e);
				}
			}
			System.exit(1);
			return;
		} else{
			log.info("Reading configuration from " + configFile.getAbsolutePath());
			config = helper.readFromFile(configFile);
		}

		ProxyFactory proxyFactory = new ProxyFactory(new HessianProxyProvider());
		try {
			new AppSnapServer(config, proxyFactory);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unexpected Error.  Shutting Down.");
			System.exit(1);
		}
	}
	
	private final Log log = LogFactory.getLog(getClass());
	private final Server jetty;
	private final Data data;
	private final IdProover idProover;
	private final IdTool idTool;
	
	public AppSnapServer(final ServerConfiguration config, final ProxyFactory proxyFactory) {
		try {
			config.validate();
			
			log.info("Launching service " + config.serviceId());
			
			jetty = new Server();
			SelectChannelConnector connector = new SelectChannelConnector();
			connector.setPort(config.publishPort());
			connector.setHost(config.bindAddress());
			jetty.addConnector(connector);
			
			data = new Data(new File(config.storageDir(), "bdb-data"), new Long(10 * 1024 * 100 /*10mb*/));
			
			{// ADMINISTRATOR BOOTSTRAPPING MECHANISM
				for(AdministratorConfig admin : config.administrators()){
					UserAccount user = data.userAccounts.get(admin.authenticationId(), null, LockMode.READ_COMMITTED);
					if(user==null){
						log.warn("Bootstrapping admin account for " + admin.authenticationId());
						user = new UserAccount(
									new PasswordIdProofCheckRecipe(
												admin.authenticationId(), 
												admin.idProofRecipie().password().getPassword()
									)
								);
						user.setAdministrator(true);
					}
					if(!user.isAdministrator()){
						log.warn("Promoting account " + admin.authenticationId() + " to administrator");
						user.setAdministrator(true);
					}
					
					data.userAccounts.put(user.id(), user, null);
					data.passwords.put(admin.authenticationId().toString(), admin.idProofRecipie().password().getPassword(), null);
				}
			}
			{// IDENTITY TOOLS
				idProover = new SimpleIdProover((SimpleId)config.idProofRecipie().id(), config.idProofRecipie().password());
				idTool = new IdTool(
								new SimpleIdIdToolPlugin(new PasswordCheckerImpl(config, data)), 
								new VeracityIdToolPlugin(proxyFactory)
							);
			}
			{// MAIN RPC SERVICE
				AppsnapServiceImpl mapService = new AppsnapServiceImpl(data, config, idTool, idProover, proxyFactory);
				String path = "/rpc";
				SwitchingContentHandler handler = new SwitchingContentHandler(path);
//				handler.addHandler(new JAXWSContentHandler(path, mapService));
				handler.addHandler(new HessianContentHandler(AppsnapService.class, mapService));
				jetty.addHandler(handler);
			}
			
			{// BDBADMIN SERVICE
				BdbService service = new BdbService(buildBdbadminTree(data), new BdbAdminAuthorizer(data), proxyFactory);
				jetty.addHandler(new BdbAdminJettyAdapter("/bdbadmin", service));
			}
			
			{
				jetty.addHandler(new AppletPublisher(data, proxyFactory, config, idProover));
			}
			jetty.start();
			
			log.info("SERVER READY FOR ACTION");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	private static class BdbAdminAuthorizer implements Authorizer {
		private final Data data;
		
		public BdbAdminAuthorizer(Data data) {
			super();
			this.data = data;
		}

		public AuthorizationLevel authorize(Id identity) {
			UserAccount userAccount = data.userAccounts.get(identity, null, LockMode.READ_COMMITTED);
			if(userAccount!=null && userAccount.isAdministrator()){
				return AuthorizationLevel.READ_WRITE;
			}else{
				return AuthorizationLevel.NONE;
			}
		}
	}
	
	private BdbCategory buildBdbadminTree(EnvironmentWrap data){
		
		BdbEnv e = new BdbEnv();
		e.setName("data");
		e.setEnv(data.env);
		
		for(DbWrap<?, ?> next : data.databases()){
			BdbPrimaryDb primary = new BdbPrimaryDb(next.name, next.db, e);
			e.databases().add(primary);
			
			for(SecondaryDbWrap<?, ?> s : next.secondaries){
				BdbSecondaryDb secondary = new BdbSecondaryDb(s.name, s.db, primary, e);
				e.databases().add(secondary);
			}
		}
		
		BdbCategory root = new BdbCategory("root");
		root.environments().add(e);
		return root;
	}
	
	public void shutdown() throws Exception {
		log.info("Shutting down");
		jetty.stop();
		data.close();
		log.info("Exit");
	}
}
