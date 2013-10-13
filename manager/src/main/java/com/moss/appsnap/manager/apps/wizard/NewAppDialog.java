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
package com.moss.appsnap.manager.apps.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.UIManager;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.manager.apps.NewJavaAppParams;
import com.moss.appsnap.manager.apps.NewJavaAppletParams;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.greenshell.wizard.ProcessPanel;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.tools.IdProover;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;
import com.moss.swing.dialog.DialogablePanel;
import com.moss.swing.test.TestFrame;

@SuppressWarnings("serial")
public class NewAppDialog extends DialogablePanel {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		TestFrame f = new TestFrame();
		ProxyFactory proxies = new ProxyFactory(new HessianProxyProvider());
		AppsnapService snap = proxies.create(AppsnapService.class, args[0]);
		JDialog d = new NewAppDialog(
				snap,
				new SimpleIdProover(args[1], args[2]),
				new AppCreator() {
					public void createApp(NewJavaAppParams params) {
						System.out.println("Create app " + params);
					}
					public void createApp(NewJavaAppletParams params) {
						System.out.println("Create app " + params);
					};
				},
				proxies
				).makeDialogFor(f);
		WindowUtil.packWithinLimits(d, new Dimension(640, 480), null);
		d.setLocationRelativeTo(f);
		d.setVisible(true);
	}
	
	
	public NewAppDialog(AppsnapService snap,IdProover idProver, final AppCreator creatorAction, ProxyFactory proxies) {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
//		final NewJavaAppParams appParams = new NewJavaAppParams();
//		
//		final InstallableSelectionAction finalAction = new InstallableSelectionAction() {
//			
//			public void installableSelected(String label, String series, JavaAppSpec spec) {
//				appParams.initialVersion = new NewJavaAppInstallableParams(label, series, spec, ComponentLoadSource.LOCAL_MAVEN);
//				creatorAction.createApp(appParams);
//			}
//		};
//		
//		final NewInstallableWizardState wizModel = new NewInstallableWizardState(new AppDetails(null, null, false), snap, idProver, finalAction);
//		wizModel.setSeries("Default");
//		
//		final SpecSelectScreen initScreen = new SpecSelectScreen(wizModel);
		
		final AppTypeMenu initScreen = new AppTypeMenu(snap, idProver, creatorAction, proxies);
		
		
		ProcessPanel ppanel = (new ProcessPanel(initScreen));
		add(ppanel);
		ppanel.setFinalAction(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}
	
	
}
