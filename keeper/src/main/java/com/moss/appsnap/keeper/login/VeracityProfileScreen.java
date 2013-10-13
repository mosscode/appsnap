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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import com.moss.anthroponymy.StFirstMiddleLastName;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.security.RegisterUserOutcome;
import com.moss.appsnap.keeper.util.ErrorsList;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.standard.DelegatedIdProofCheckRecipe;
import com.moss.identity.standard.Password;
import com.moss.identity.tools.IdProover;
import com.moss.identity.veracity.VeracityId;
import com.moss.identity.veracity.VeracityIdProover;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.jaxws.JAXWSProxyProvider;
import com.moss.swing.event.DocumentAdapter;
import com.moss.veracity.api.Management;
import com.moss.veracity.api.VtAccount;
import com.moss.veracity.api.VtAuthMode;
import com.moss.veracity.api.VtImage;
import com.moss.veracity.api.VtPasswordMechanism;
import com.moss.veracity.api.VtProfile;
import com.moss.veracity.api.util.NameParser;
import com.moss.veracity.api.util.ParsedName;

public class VeracityProfileScreen extends AbstractScreen<VeracityProfileScreenView> {
	
	private File profilePicPath;
	private String firstName;
	private String middleName;
	private String lastName;

	private final ErrorsList errors = new ErrorsList();
	
	public VeracityProfileScreen(final ProxyFactory proxies, final VeracityId id, final String password, final ScreenAction backAction, final AppsnapService snap, final PostLoginAction finalAction) {
		super("Profile Completion", new VeracityProfileScreenView());
			
		
		view.backButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction.setEnvironment(environment);
				backAction.actionPerformed(e);
			}
		});
		
		view.firstNameField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				firstName = view.firstNameField().getText();
				validate();
			}
		});
		
		view.middleNameField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				middleName = view.middleNameField().getText();
				validate();
			}
		});
		
		view.lastNameField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				lastName = view.lastNameField().getText();
				validate();
			}
		});
		
		view.iconChooser().addListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				profilePicPath = view.iconChooser().getImagePath();
				validate();
			}
		});
		
//		view.errorsField().setVisible(false);
		view.nextButton().setEnabled(false);
		
		final ScreenAction createAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					ParsedName n = NameParser.parse(id.getName());
					Url location = Url.http(n.getServiceName(), "/ManagementImpl");
					
					ProxyFactory proxies;
					{
						JAXWSProxyProvider p = new JAXWSProxyProvider();
						p.register(Management.class, Management.QNAME);
						proxies = new ProxyFactory(p);
					}
					
					Management veracity = proxies.create(Management.class, location.toString());
					
					VtAccount account = new VtAccount();
					account.setAuthMode(VtAuthMode.USER);
					account.setName(id.getName());
					
					VtProfile profile = new VtProfile();
					profile.setProfileName("default");
					profile.setName(
							new StFirstMiddleLastName(
									firstName, 
									isNullOrEmpty(middleName)?null:middleName.substring(0, 1),
									lastName
							)
					);
					
					if(profilePicPath!=null){
						VtImage image = new VtImage();
						
						FileInputStream in = new FileInputStream(profilePicPath);
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						byte[] b = new byte[1024*100];
						for(int x=in.read(b);x!=-1;x=in.read(b)){
							bytes.write(b, 0, x);
						}
						bytes.close();
						in.close();
						
						image.setData(bytes.toByteArray());
						
						profile.setImage(image);
					}
					
					account.add(profile);
					
					VtPasswordMechanism mechanism = new VtPasswordMechanism(password);
					account.add(mechanism);
					
					veracity.create(account, null);
					
					IdProover p = new VeracityIdProover(id, new Password(password));
					
					RegisterUserOutcome outcome = snap.registerUser(new DelegatedIdProofCheckRecipe(id));
					
					switch(outcome){
					case SUCCEEDED:
						break;
					default:
						throw new RuntimeException("This is crazy: the account already exists, but we just created it.");
					}
					
					finalAction.run(p, environment, null);
					
				} catch (Throwable t) {
					environment.failCatastrophically(t);
				}
			}
		};
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProgressMonitorScreen.runAndMonitor("Creating account...", createAction, environment);
			}
		});
	}
	
	private void validate(){
		errors.clear();
		
		if(isNullOrEmpty(firstName)){
			errors.add("You must specify a first name");
		}
		
		if(isNullOrEmpty(lastName)){
			errors.add("You must specify a last name");
		}
		view.errorsField().setText(errors.toString());
		
		view.nextButton().setEnabled(errors.isEmpty());
	}
	
	private boolean isNullOrEmpty(String text){
		return text==null || text.trim().length()==0;
	}
}
