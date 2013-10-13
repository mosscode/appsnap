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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.AppsnapServiceInfo;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.keeper.data.Data;
import com.moss.appsnap.keeper.data.KeeperConfig;
import com.moss.appsnap.keeper.socketapi.ApiMessageConnection;
import com.moss.appsnap.keeper.socketapi.ApiMessageHandler;
import com.moss.appsnap.keeper.socketapi.ControlPanelLaunchFunction;
import com.moss.appsnap.keeper.socketapi.InstallFunction;
import com.moss.appsnap.keeper.socketapi.LaunchFunction;
import com.moss.appsnap.keeper.socketapi.PingFuction;
import com.moss.appsnap.keeper.socketapi.PollFunction;
import com.moss.appsnap.keeper.socketapi.SocketFunction;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

public class Keeper {
	private final Log log = LogFactory.getLog(getClass());
	private final Guts guts;
	
	public Keeper(final Url location) {
		log.info("Starting keeper for " + location);
		
		final DesktopIntegrationStrategy desktop = new Bootstrapper().discover();
		final ProxyFactory proxyFactory = new ProxyFactory(new HessianProxyProvider());
		final AppsnapService snap = proxyFactory.create(AppsnapService.class, location.toString());
		
		AppsnapServiceInfo serviceInfo;
		while(true){
			try {
				serviceInfo = snap.serviceInfo();
				break;
			} catch (Exception e) {
				log.error("Error talking to appsnap.  Will try again in 5 seconds.  The message was: " + e.getMessage(), e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		final File dataLocation = desktop.keeperDataDir(serviceInfo.id());
		
		try {
			final File logOutput = new File(dataLocation, "log4j.log");
			Logger.getRootLogger().addAppender(new FileAppender(new SimpleLayout(), logOutput.getAbsolutePath()));
			log.info("Configured logging to " + logOutput.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		log.info("Starting from " + dataLocation.getAbsolutePath());
		
		Data data;
		try {
			data = new Data(dataLocation);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		
		if(data.config.get()==null){
			if(System.getProperty("dev-bootstrap", "false").equals("true")){
				KeeperConfig config = new KeeperConfig();
				data.config.put(config);
			}else{
				throw new NullPointerException("Keeper data has no config: have you run the installer?");
			}
		}
		if(location==null){
			throw new NullPointerException();
		}
		
		guts = new Guts(
				serviceInfo,
				location,
				proxyFactory,
				snap,
				data,
				desktop
				);

		desktop.noLongerGutless(guts);
		
		final Poller poller = new Poller(guts, true);
		poller.start();
		
		desktop.thePollerWasStartedAndHereItIs(poller);
		
		desktop.startLocalApiServer(new ApiMessageHandler() {
			final SocketFunction[] commands = new SocketFunction[]{
					new PollFunction(guts, poller),
					new InstallFunction(guts, poller),
					new ControlPanelLaunchFunction(guts),
					new LaunchFunction(guts, poller),
					new PingFuction()
			};
			
			public void handle(ApiMessageConnection socket) {
				try {
					log.info("Opening connection");
					StringBuilder input = new StringBuilder();
					Reader r = new InputStreamReader(socket.in());
					log.info("Reading input");
//					char[] b = new char[1024];
//					for(int x=r.read(b);x!=-1;x=r.read(b)){
//						input.append(b, 0, x);
//					}
					for(int x = r.read();x!=-1 && x!='\n'; x = r.read()){
						input.append((char)x);
					}
//					r.close();
					
					log.info("Command: " + input);
					
					String commandName; 
					String commandParams; 
							
					int argsDelimiterPos = input.indexOf(" ");
					if(argsDelimiterPos!=-1){
						commandName = input.substring(0, argsDelimiterPos);
						commandParams = input.substring(argsDelimiterPos+1);
					}else{
						commandName = input.toString();
						commandParams = "";
					}
					SocketFunction command = null;
					for(SocketFunction next : commands){
						if(next.name().equals(commandName)){
							command = next;
						}
					}
					
					String response;
					if(command==null){
						response = ("Invalid command: " + commandName);
					}else{
						try {
							response = command.execute(commandParams);
						} catch (Exception e) {
							e.printStackTrace();
							response = e.getClass().getSimpleName() + ":" + e.getMessage();
						}
					}

					log.info("Sending response: " + response);
					
					Writer w = new OutputStreamWriter(socket.out());
					w.write(response);
					w.write('\n');
					w.flush();
					w.close();
					
				} catch (Throwable e) {
					e.printStackTrace();
				} finally{
					socket.close();
				}				
			}
		});
		
	}

}
