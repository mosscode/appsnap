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
package com.moss.appsnap.keeper.installerapp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JApplet;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.keeper.Bootstrapper;
import com.moss.appsnap.keeper.DesktopIntegrationStrategy;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.data.Data;
import com.moss.appsnap.keeper.firstaid.FirstAidStartScreen;
import com.moss.appsnap.keeper.login.LoginStartScreen;
import com.moss.appsnap.keeper.login.PostLoginAction;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ProcessPanel;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.ScreenEnvironment;
import com.moss.identity.tools.IdProover;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

/**
 * 1) authenticate user with snap
 * 2) prompt for app selection
 * 3) install keeper
 * 4) install app
 * 5) launch keeper
 * 6) launch app
 */
@SuppressWarnings("serial")
public class Installer extends JApplet {
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private final Log log = LogFactory.getLog(getClass());
	private final ProxyFactory proxyFactory = new ProxyFactory(new HessianProxyProvider());
	private ProcessPanel processPanel;
	
	public Installer() {
	}
	
	@Override
	public void init() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		processPanel = new ProcessPanel(new LoadingScreen());
		
		this.processPanel.setFinalAction(new Runnable() {
			public void run() {
				processPanel.done();
			}
		});
		
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(processPanel);
		
		new Thread(){
			public void run() {
				setPriority(MIN_PRIORITY);
				try {
					final InstallerParams params = InstallerParams.read(Installer.this, proxyFactory);
					final AppsnapService snap = proxyFactory.create(AppsnapService.class, params.url);
					
					final PostLoginAction action = new PostLoginAction() {
						public void run(IdProover idProver, ScreenEnvironment environment, ScreenAction backAction) {
							
							DesktopIntegrationStrategy desktop = new Bootstrapper().discover();
							File dataDir = desktop.keeperDataDir(params.serviceId);
							
							if(dataDir.exists() && !desktop.keeperIsRunning()){
								log.info("The keeper was installed but not running; redirecting user to first-aid");
								
								Guts guts;
								try {
									Data data = new Data(dataDir);
									guts = new Guts(snap.serviceInfo(), new Url(params.url), proxyFactory, snap, data, desktop);
									desktop.noLongerGutless(guts);
									
									
									//throw new RuntimeException("The keeper was installed but not running! We need to implement first aid, and then switch to it in this case");
									environment.next(new FirstAidStartScreen(
											"WARNING!",
											"<html><body style=\"font-face:arial,sans-serif; font-size:18pt;\"><p><b>BROKEN INSTALLATION DETECTED</b></p><p>It looks like your existing setup is broken.  In order to proceed, this will have to be remedied.  The suggested course of action is to run this \"first-aid\" tool and see if that fixes the problem.</p></body></html>",
											guts,
											params.keeperPublication,
											idProver,
											backAction
									));
									
								} catch (Throwable e1) {
									environment.failCatastrophically(e1);
								}
								
							}else{
								environment.next(new PublicationSelectionScreen(params, snap, idProver, proxyFactory, backAction));
							}

						}
					};
					
					ScreenAction loginStartAction = new AbstractScreenAction() {
						public void actionPerformed(ActionEvent e) {
							environment.next(new LoginStartScreen(params.serviceInfo.name(), snap, proxyFactory, action, Guts.veracityServiceDomain));
						}
					};
					WelcomeScreen start = new WelcomeScreen(params.serviceInfo.name(), loginStartAction);
					processPanel.start(start);
				} catch (Throwable e) {
					processPanel.failCatastrophically(e);
				}
			}
		}.start();
	}
}
