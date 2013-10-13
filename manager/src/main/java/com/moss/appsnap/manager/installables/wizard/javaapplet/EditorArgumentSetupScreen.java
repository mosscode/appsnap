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

import java.awt.event.ActionEvent;
import java.util.List;

import com.moss.appsnap.api.installables.InstallableDetails;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.JavaAppletInstallableDetails;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.Screen;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.ScreenEnvironment;
import com.moss.greenshell.wizard.menu.AbstractMenuScreen;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.greenshell.wizard.progress.QuickRunnable;
import com.moss.launch.spec.applet.AppletParameter;

public class EditorArgumentSetupScreen extends AbstractMenuScreen{
	private final NewJavaAppletInstallableWizardState wizModel;
	private ScreenAction editorNextAction;
	
	public EditorArgumentSetupScreen(final NewJavaAppletInstallableWizardState wizModel, final InstallableId lastetVersionId, final ScreenAction backAction) {
		super("Load the arguments from the previous version as a default, or start from scratch?");
		this.wizModel = wizModel;
		
		
		setBackAction(backAction);
		super.showBackButton(true);
		
		final ScreenAction backHereAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.previous(EditorArgumentSetupScreen.this);
			}
		};
		
		addScreenActionItem(
				"Use the parameters from the prev. version",
				"Use the parameters from the prev. version",
				null,
				new AbstractScreenAction() {
					
					public void actionPerformed(ActionEvent e) {
						setPathNote("With Recycled Parameters");
						
						ProgressMonitorScreen.runAndMonitor(
								"Fetching previous version.", 
								new QuickRunnable() {
									public Screen run(ScreenEnvironment environment) {
										try {
											final InstallableDetails latestVersionDetails = wizModel.snap.getInstallableDetails(lastetVersionId, wizModel.idProver.giveProof());
											final JavaAppletInstallableDetails javaApp = JavaAppletInstallableDetails.grab(latestVersionDetails);
											List<AppletParameter> params = javaApp.launchSpec().parameters();
											wizModel.parameters.clear();
											wizModel.parameters.addAll(params);
											JavaAppletEditorScreen editor = new JavaAppletEditorScreen(wizModel, backHereAction);
											editor.setNextAction(editorNextAction);
											return editor;
										} catch (Throwable t) {
											environment.failCatastrophically(t);
											return null;
										}						
									}
								},	 
								environment
							);
						
					}
				}
			);
		addScreenActionItem(
				"Use the parameters already defined in the launch spec",
				"Use the parameters already defined in the launch spec",
				null,
				new AbstractScreenAction() {
					public void actionPerformed(ActionEvent e) {
						setPathNote("With Fresh Parameters");
						JavaAppletEditorScreen editor = new JavaAppletEditorScreen(wizModel, backHereAction);
						editor.setNextAction(editorNextAction);
						environment.next(editor);
					}
				}
			);
			
	}
	
	public void setEditorNextAction(ScreenAction editorNextAction) {
		this.editorNextAction = editorNextAction;
	}
	
}
