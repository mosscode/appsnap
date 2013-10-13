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

import java.awt.event.ActionEvent;
import java.util.List;

import com.moss.appkeep.api.AppkeepService;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.manager.apps.NewJavaAppParams;
import com.moss.appsnap.manager.apps.NewJavaAppletParams;
import com.moss.appsnap.manager.apps.wizard.javaapp.KeeperSoftwarePromptMenu;
import com.moss.appsnap.manager.installables.NewInstallableParams.ComponentLoadSource;
import com.moss.appsnap.manager.installables.NewJavaAppInstallableParams;
import com.moss.appsnap.manager.installables.wizard.CertificateSelectionScreen;
import com.moss.appsnap.manager.installables.wizard.SeriesSelectionScreen;
import com.moss.appsnap.manager.installables.wizard.javaapp.EditorArgumentsSetupAction;
import com.moss.appsnap.manager.installables.wizard.javaapp.JavaAppInstallableEntryHandler;
import com.moss.appsnap.manager.installables.wizard.javaapp.NewJavaAppInstallableWizardState;
import com.moss.appsnap.manager.installables.wizard.javaapp.SpecSelectScreen;
import com.moss.appsnap.manager.installables.wizard.javaapplet.AppletSpecSelectScreen;
import com.moss.appsnap.manager.installables.wizard.javaapplet.JavaAppletEditorScreen;
import com.moss.appsnap.manager.installables.wizard.javaapplet.NewJavaAppletInstallableWizardState;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.greenshell.wizard.menu.AbstractMenuScreen;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.rpcutil.proxy.ProxyFactory;

public class AppTypeMenu extends AbstractMenuScreen {
	public AppTypeMenu(final AppsnapService snap, final IdProover idProver, final AppCreator creatorAction, final ProxyFactory proxies) {
		super("What type of app?");
		
		final ScreenAction backHereAction = new SimpleBackAction(this);
		
		addScreenActionItem(
				"Java Application", 
				"Standalone java application launched via a main() method.", 
				null, 
				new AbstractScreenAction() {
					public void actionPerformed(ActionEvent e) {
						final NewJavaAppParams appParams = new NewJavaAppParams();
						
						final JavaAppInstallableEntryHandler finalAction = new JavaAppInstallableEntryHandler() {
							
							public void appEntered(String label, String series, JavaAppSpec spec) {
								appParams.initialVersion = new NewJavaAppInstallableParams(label, series, spec, ComponentLoadSource.LOCAL_MAVEN);
								creatorAction.createApp(appParams);
							}
						};
						
						final NewJavaAppInstallableWizardState wizModel = new NewJavaAppInstallableWizardState(new AppDetails(null, null, false, null), snap, idProver, finalAction);
						wizModel.params.series = ("Default");
						
						final SpecSelectScreen initScreen = new SpecSelectScreen(wizModel);
						final ScreenAction backToSpecSelect = new SimpleBackAction(initScreen);
						
						final ScreenAction restartAction = new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								environment.previous(initScreen);
							}
						};
						ScreenAction specSelectAction = new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								
								final KeeperSoftwarePromptMenu keeperQuestion = new KeeperSoftwarePromptMenu(appParams, wizModel, restartAction);
								final ScreenAction backToKeeperQuestion = new SimpleBackAction(keeperQuestion);
								
								String defaultName = appParams.appName;
								if(defaultName==null){
									defaultName = wizModel.params.spec.name();
								}
								final AppBasicsEntryScreen basicsScreen = new AppBasicsEntryScreen(defaultName, appParams, wizModel, backToKeeperQuestion);
								
								
								keeperQuestion.setNextAction(new AbstractScreenAction() {
									public void actionPerformed(ActionEvent e) {
										environment.next(basicsScreen);
									}
								});
								
								basicsScreen.setNextAction(new AbstractScreenAction() {
									
									public void actionPerformed(ActionEvent e) {
										final ScreenAction backToBasicsScreen = new SimpleBackAction(basicsScreen);
										final SeriesSelectionScreen seriesSelectScreen = new SeriesSelectionScreen(wizModel, backToBasicsScreen);
										
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
										
										environment.next(seriesSelectScreen);
										
									}
								});
								
								environment.next(keeperQuestion);
							}
						};
						
						initScreen.setSelectionAction(specSelectAction);
						environment.next(initScreen);
					}
				}
			);
		
		
		addScreenActionItem(
				"Java Applet", 
				"Java Applet - the thing you can embed in a web browser.", 
				null, 
				new AbstractScreenAction() {
					public void actionPerformed(ActionEvent e) {
						final NewJavaAppletParams appletParams = new NewJavaAppletParams();
						
						final NewJavaAppletInstallableWizardState wizModel = new NewJavaAppletInstallableWizardState(new AppDetails(null, null, false, null), snap, idProver);
						
						
						AppletSpecSelectScreen specSelectScreen = new AppletSpecSelectScreen(wizModel);
						ScreenAction backToSpecSelectScreen = new SimpleBackAction(specSelectScreen);
						
						final String defaultName;
						if(appletParams.appName==null){
							if(wizModel.params.spec!=null){
								defaultName = wizModel.params.spec.name();
							}else{
								defaultName = "";
							}
						}else{
							defaultName = appletParams.appName;
						}
						
						final AppBasicsEntryScreen basicsScreen = new AppBasicsEntryScreen(defaultName, appletParams, wizModel, backToSpecSelectScreen);
						final ScreenAction backToBasicsScreen = new SimpleBackAction(basicsScreen);
						
						specSelectScreen.setSelectionAction(new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								wizModel.parameters.clear();
								wizModel.parameters.addAll(wizModel.params.spec.parameters());
								environment.next(basicsScreen);
							}
						});
						
						final SeriesSelectionScreen seriesSelectScreen = new SeriesSelectionScreen(wizModel, backToBasicsScreen);
						final ScreenAction backToSeriesSelectScreen = new SimpleBackAction(seriesSelectScreen);
						
						basicsScreen.setNextAction(new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								environment.next(seriesSelectScreen);
							}
						});
						
						final JavaAppletEditorScreen editorScreen = new JavaAppletEditorScreen(wizModel, backToSeriesSelectScreen);
						
						seriesSelectScreen.setNextAction(new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								environment.next(editorScreen);
							}
						});
						
						List<Url> keeperLocations;
						try {
							keeperLocations = snap.listKeepers(idProver.giveProof());
						} catch (SecurityException e1) {
							environment.failCatastrophically(e1);
							return;
						} catch (IdProovingException e1) {
							environment.failCatastrophically(e1);
							return;
						}
						
						final AppkeepService keep = proxies.create(AppkeepService.class, keeperLocations.get(0).toString());
						
						final CertificateSelectionScreen certSelectScreen = new CertificateSelectionScreen(true, keep.listCertificates());
						
						editorScreen.setNextAction(new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								environment.next(certSelectScreen);
							}
						});
						
						certSelectScreen.setNextAction(new AbstractScreenAction() {
							public void actionPerformed(ActionEvent e) {
								
								ProgressMonitorScreen.runAndMonitor(
										"Endorsing Jars & Creating App",
										new AbstractScreenAction() {
											public void actionPerformed(ActionEvent e) {
												
												System.out.println("Applet entered");
												wizModel.params.jarsignCert = certSelectScreen.selection();
												appletParams.initialVersion = wizModel.params;
												creatorAction.createApp(appletParams);
												environment.done();
											}
										},
										environment
									);

							}
						});
						
						environment.next(specSelectScreen);
					}	
				}
			);
	}
}
