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
package com.moss.appsnap.manager.installables.wizard.javaapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.JDialog;
import javax.swing.UIManager;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.appsnap.manager.installables.wizard.SeriesSelectionScreen;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ProcessPanel;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.tools.IdProover;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;
import com.moss.swing.dialog.DialogablePanel;
import com.moss.swing.test.TestFrame;

@SuppressWarnings("serial")
public final class NewVersionDialog extends DialogablePanel {
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		TestFrame f = new TestFrame();
		AppsnapService snap = new ProxyFactory(new HessianProxyProvider()).create(AppsnapService.class, args[0]);
		final AppDetails app = new AppDetails(null, "app", false, AppType.JAVA_APP, new AppVersionSeries("Trunk", Collections.EMPTY_LIST), new AppVersionSeries("0.0.x", Collections.EMPTY_LIST));
		NewJavaAppInstallableWizardState wizModel = new NewJavaAppInstallableWizardState(
				app,
				snap,
				new SimpleIdProover(args[1], args[2]),
				new JavaAppInstallableEntryHandler() {
					
					public void appEntered(String label, String series,JavaAppSpec spec) {
						System.out.println("Something selected " + label);
					}
				}
			);
		
		wizModel.params.series = ("Nonexistent");
		JDialog d = new NewVersionDialog(
				wizModel
				).makeDialogFor(f);
//		d.pack();
		WindowUtil.packWithinLimits(d, new Dimension(640, 480), null);
		d.setLocationRelativeTo(f);
		d.setVisible(true);
	}
	
	public NewVersionDialog(final AppDetails details, final AppsnapService snap, final IdProover idProver, JavaAppInstallableEntryHandler action) {
		this(new NewJavaAppInstallableWizardState(details, snap, idProver, action));
	}
	
	public NewVersionDialog(final NewJavaAppInstallableWizardState wizModel) {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		final SpecSelectScreen initScreen = new SpecSelectScreen(wizModel);
		
		final ScreenAction restartAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.previous(initScreen);
			}
		};
		
		final SeriesSelectionScreen seriesSelectScreen = new SeriesSelectionScreen(wizModel, restartAction);
		
		ScreenAction specSelectAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(seriesSelectScreen);
			}
		};
		
		seriesSelectScreen.setNextAction(new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				ScreenAction action = new EditorArgumentsSetupAction(
											new SimpleBackAction(seriesSelectScreen),
											wizModel
										);
				action.setEnvironment(environment);
				action.actionPerformed(new ActionEvent(this, 1, ""));
			}
		});
		
		initScreen.setSelectionAction(specSelectAction);
		
		ProcessPanel ppanel = (new ProcessPanel(initScreen));
		add(ppanel);
		ppanel.setFinalAction(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}
	
}
