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
package com.moss.appsnap.keeper.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.security.RegisterUserOutcome;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.greenshell.wizard.SimpleMessageScreen;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.IdProofCheckRecipe;
import com.moss.identity.IdProofRecipie;
import com.moss.identity.simple.SimpleIdIdToolPlugin;
import com.moss.identity.simple.swing.SimpleIdConfirmationConfigPanelPlugin;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.identity.tools.IdTool;
import com.moss.identity.tools.swing.proofcheckrecipie.IdConfirmationConfigPanel;
import com.moss.identity.veracity.VeracityIdToolPlugin;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.veracity.identity.swing.VeracityIdConfirmationEditorPlugin;

public class SignUpScreen extends AbstractScreen<SignUpScreenView> {
	public SignUpScreen(final ScreenAction backAction, final AppsnapService snap, final ProxyFactory proxies, final PostLoginAction finalAction, final String veracityHost) {
		super("Sign-Up", new SignUpScreenView());


		view.backButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					backAction.setEnvironment(environment);
					backAction.actionPerformed(e);
				}
			});

		
		final IdConfirmationConfigPanel editor = new IdConfirmationConfigPanel(
				new VeracityIdConfirmationEditorPlugin(false, true, veracityHost),
				new SimpleIdConfirmationConfigPanelPlugin()
		);
		
		view.holderPanel().add(editor);
		
		

		view.nextButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!editor.hasErrors()){
						
						ProgressMonitorScreen.runAndMonitor(
								"Creating User Account", 
								new AbstractScreenAction() {
									public void actionPerformed(ActionEvent e) {
										IdProofCheckRecipe checkRecipie = editor.getValue();
										
										final ScreenAction backHere = new SimpleBackAction(SignUpScreen.this);
										final RegisterUserOutcome outcome;
										try {
											outcome = snap.registerUser(checkRecipie);
										} catch (SecurityException e1) {
											environment.next(new SimpleMessageScreen("Security Error: " + e1.getMessage(), backHere));
											return;
										}
										
										switch(outcome){
										case LOGIN_ALREADY_EXISTS:
											environment.next(new SimpleMessageScreen("Login already exists!", backHere));
											return;
										case SUCCEEDED:
											break;
										default:
											environment.next(new SimpleMessageScreen("Unknown Response: " + outcome));
											return;
										}
										
										IdProofRecipie proofRecipie = editor.getProofRecipie();
										
										if(proofRecipie!=null){
											// THE EDITOR WAS ABLE TO TELL US HOW TO PROOVE THE ID AS WELL AS HOW TO CONFIRM IT
											IdTool tool = new IdTool(
													new SimpleIdIdToolPlugin(null), 
													new VeracityIdToolPlugin(proxies)
											);
											
											IdProover idProver;
											try {
												idProver = tool.getProver(proofRecipie);
											} catch (IdProovingException e1) {
												environment.failCatastrophically(e1);
												return;
											}
											
											finalAction.run(idProver, environment, backHere);
										}else{
											// THE EDITOR DIDN'T HAVE THE INFORMATION NEEDED TO PROVE THE ID
											
											environment.next(new LoginScreen(snap, proxies, backAction, finalAction, veracityHost));
										}
									}
								},
								environment
							);
					}
					
				}
			});
	}
}
