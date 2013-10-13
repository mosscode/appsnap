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

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.AppsnapService;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.IdProofRecipie;
import com.moss.identity.simple.SimpleIdIdToolPlugin;
import com.moss.identity.simple.swing.SimpleProofRecipieEditor;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdTool;
import com.moss.identity.tools.swing.proofrecipie.ProofRecipieEditor;
import com.moss.identity.veracity.VeracityIdToolPlugin;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.veracity.identity.swing.VeracityProofRecipieEditorPlugin;

public class LoginScreen extends AbstractScreen<LoginScreenView>{
	private final Log log = LogFactory.getLog(getClass());
	
	public LoginScreen(final AppsnapService snap, final ProxyFactory proxyFactory, final ScreenAction backAction, final PostLoginAction finalAction, final String veracityServiceDomain) {
		super("Login",new LoginScreenView());
		
		final ScreenAction backHereAction = new SimpleBackAction(this);
		
		if(backAction!=null){
			view.backButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					backAction.setEnvironment(environment);
					backAction.actionPerformed(e);
				}
			});
		}else{
			view.backButton().setVisible(false);
		}
		
		final ProofRecipieEditor editor = new ProofRecipieEditor(
				new VeracityProofRecipieEditorPlugin(false, false, veracityServiceDomain),
				new SimpleProofRecipieEditor(false)
				);

		
		view.holderPanel().add(editor);
		
		
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(editor.hasErrors()){
					editor.showCurrentErrors();
					return;
				}else{
					
					ProgressMonitorScreen.runAndMonitor(
							"Logging-In", 
							new AbstractScreenAction() {
								public void actionPerformed(ActionEvent e) {
									IdProofRecipie r = editor.getValue();
									
									IdTool tool = new IdTool(new SimpleIdIdToolPlugin(null), new VeracityIdToolPlugin(proxyFactory));
									
									IdProover idProver;
									try {
										idProver = tool.getProver(r);
										snap.myAccount(idProver.giveProof());
									} catch (Throwable e2) {
										environment.previous(LoginScreen.this);
										JOptionPane.showMessageDialog(view, "Login Error: " + e2.getMessage());
										return;
									}
									
									setPathNote(r.id().toString());
									System.out.println(editor.getValue());
									
									finalAction.run(idProver, environment, backHereAction);
									
								}
							}, 
							environment
						);
					
				}
			}
		});
	}
}
