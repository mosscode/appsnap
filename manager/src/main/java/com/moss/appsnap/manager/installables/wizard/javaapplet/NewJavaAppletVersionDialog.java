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
package com.moss.appsnap.manager.installables.wizard.javaapplet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.UIManager;

import com.moss.appkeep.api.AppkeepService;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.appsnap.api.installables.InstallableDetails;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallableInfo;
import com.moss.appsnap.api.installables.JavaAppletInstallableDetails;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.manager.installables.wizard.CertificateSelectionScreen;
import com.moss.appsnap.manager.installables.wizard.EditExistingOrUploadNewLaunchSpecScreen;
import com.moss.appsnap.manager.installables.wizard.SeriesSelectionScreen;
import com.moss.appsnap.manager.installables.wizard.VersionSelectScreen;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ProcessPanel;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.simple.SimpleIdProover;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;
import com.moss.swing.dialog.DialogablePanel;
import com.moss.swing.test.TestFrame;

@SuppressWarnings("serial")
public final class NewJavaAppletVersionDialog extends DialogablePanel {
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		TestFrame f = new TestFrame();
		AppsnapService snap = new ProxyFactory(new HessianProxyProvider()).create(AppsnapService.class, args[0]);
		final AppDetails app = new AppDetails(null, "app", false, AppType.JAVA_APP, new AppVersionSeries("Trunk", Collections.EMPTY_LIST), new AppVersionSeries("0.0.x", Collections.EMPTY_LIST));
		NewJavaAppletInstallableWizardState wizModel = new NewJavaAppletInstallableWizardState(
				app,
				snap,
				new SimpleIdProover(args[1], args[2])
			);
		
		wizModel.params.series = ("Nonexistent");
		JDialog d = new NewJavaAppletVersionDialog(
				wizModel,
				null,
				null
				).makeDialogFor(f);
//		d.pack();
		WindowUtil.packWithinLimits(d, new Dimension(640, 480), null);
		d.setLocationRelativeTo(f);
		d.setVisible(true);
	}
	
	public NewJavaAppletVersionDialog(final AppDetails details, final AppsnapService snap, final IdProover idProver, final ProxyFactory proxies, final JavaAppletInstallableEntryHandler handler) {
		this(new NewJavaAppletInstallableWizardState(details, snap, idProver), proxies, handler);
	}
	
	public NewJavaAppletVersionDialog(final NewJavaAppletInstallableWizardState wizModel, final ProxyFactory proxies, final JavaAppletInstallableEntryHandler handler) {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		final AppletSpecSelectScreen specSelectScreen = new AppletSpecSelectScreen(wizModel);
		final ScreenAction backToSpecSelectScreen = new SimpleBackAction(specSelectScreen);
		
		
		final SeriesSelectionScreen seriesSelectScreen = new SeriesSelectionScreen(wizModel, backToSpecSelectScreen);
		final ScreenAction backToSeriesSelectScreen = new SimpleBackAction(seriesSelectScreen);
		
		specSelectScreen.setSelectionAction(new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(seriesSelectScreen);
			}
		});
		
		AppsnapService snap = wizModel.snap;
		IdProover idProver = wizModel.idProver;
		
		List<Url> keeperLocations;
		try {
			keeperLocations = snap.listKeepers(idProver.giveProof());
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		} catch (IdProovingException e1) {
			throw new RuntimeException(e1);
		}
		
		final AppkeepService keep = proxies.create(AppkeepService.class, keeperLocations.get(0).toString());
		
		final CertificateSelectionScreen certSelectScreen = new CertificateSelectionScreen(true, keep.listCertificates());
		
		final ScreenAction postEditorAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(certSelectScreen);
			}
		};
		
		
		final ScreenAction editorLoadAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				AppVersionSeries series = wizModel.details.series(wizModel.params.series);
				if(series!=null && series.installables().size()>0){
					// need to prompt for arguments selection
					final InstallableId lastetVersionId = series.installables().get(series.installables().size()-1).id();
					
					EditorArgumentSetupScreen next = new EditorArgumentSetupScreen(wizModel, lastetVersionId, backToSeriesSelectScreen);
					next.setEditorNextAction(postEditorAction);
					environment.next(next);
				}else{
					// just load the editor
					wizModel.parameters.addAll(wizModel.params.spec.parameters());
					
					
					JavaAppletEditorScreen next = new JavaAppletEditorScreen(wizModel, backToSeriesSelectScreen);
					
					next.setNextAction(postEditorAction);
					environment.next(next);
				}
				
				
			}
		};
		
		seriesSelectScreen.setNextAction(editorLoadAction);
		
		
		certSelectScreen.setNextAction(new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				wizModel.params.jarsignCert = certSelectScreen.selection();
				ProgressMonitorScreen.runAndMonitor(
						"Finishing",
						new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								handler.appletEntered(wizModel.params);
								environment.done();
							}
						},
						environment
					);

			}
		});
//		
//		basicsScreen.setNextAction(new AbstractScreenAction() {
//			public void actionPerformed(ActionEvent e) {
//				environment.next(seriesSelectScreen);
//			}
//		});
//		
//		final JavaAppletEditorScreen editorScreen = new JavaAppletEditorScreen(wizModel, backToSeriesSelectScreen);
//		
//		seriesSelectScreen.setNextAction(new AbstractScreenAction() {
//			public void actionPerformed(ActionEvent e) {
//				environment.next(editorScreen);
//			}
//		});
//		
//
//		
//		environment.next(specSelectScreen);
		
		
		ScreenAction editAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				final VersionSelectScreen versionSelectScreen = new VersionSelectScreen("What Version?", wizModel.details);
				
				versionSelectScreen.setNextAction(new AbstractScreenAction() {
					public void actionPerformed(ActionEvent e) {

						final InstallableId lastetVersionId = versionSelectScreen.selection().id();
						
						AppVersionSeries series = null;
						
						for(AppVersionSeries s : wizModel.details.series()){
							for(InstallableInfo i : s.installables()){
								if(i.id().equals(lastetVersionId)){
									series = s;
									break;
								}
							}
							
						}
						
						try {
							// need to prompt for arguments selection
							
							final InstallableDetails latestVersionDetails = wizModel.snap.getInstallableDetails(lastetVersionId, wizModel.idProver.giveProof());
							final JavaAppletInstallableDetails javaApplet = JavaAppletInstallableDetails.grab(latestVersionDetails);
							
							wizModel.params.spec = javaApplet.launchSpec();
							wizModel.params.jarsignCert = javaApplet.jarsignCert();
							if(series!=null){
								wizModel.params.series = series.name();
							}
							
							wizModel.parameters.clear();
							wizModel.parameters.addAll(javaApplet.launchSpec().parameters());
							
							JavaAppletEditorScreen editorScreen = new JavaAppletEditorScreen(wizModel, null);
							editorScreen.setNextAction(postEditorAction);
							
							environment.next(editorScreen);
						} catch (Throwable t){
							environment.failCatastrophically(t);
						}
						

					}
				});
				
				environment.next(versionSelectScreen);
				
			}
		};
		
		ScreenAction newAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(specSelectScreen);
			}
		};
		
		EditExistingOrUploadNewLaunchSpecScreen editOrSelectScreen = new EditExistingOrUploadNewLaunchSpecScreen(null, editAction, newAction);
		
		ProcessPanel ppanel = (new ProcessPanel(editOrSelectScreen));
		add(ppanel);
		ppanel.setFinalAction(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}
	
}
