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
import com.moss.appsnap.keeper.util.ErrorsList;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.identity.veracity.VeracityId;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.swing.event.DocumentAdapter;

public class CreateVeracityAccountScreen extends AbstractScreen<CreateVeracityAccountScreenView> {
	
	private final String serviceName;
	private final ErrorsList errors = new ErrorsList();
	private String user;
	private String password;
	
	
	public CreateVeracityAccountScreen(final String serviceName, final ProxyFactory proxies, final ScreenAction backAction, final PostLoginAction finalAction, final AppsnapService snap) {
		super("What username and password?", new CreateVeracityAccountScreenView("@" + serviceName));
		
		this.serviceName = serviceName;
		
		view.backButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction.setEnvironment(environment);
				backAction.actionPerformed(e);
			}
		});

		
		final ScreenAction backHere = new SimpleBackAction(this);
		
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.next(new VeracityProfileScreen(proxies, new VeracityId(user + "@" + serviceName), password, backHere, snap, finalAction));
			}
		});
		
		view.userField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				user = view.userField().getText();
				validate();
			}
		});

		view.passwordField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				password = view.passwordField().getText();
				validate();
			}
		});
		
		
	}
	

	private void validate(){
		errors.clear();
		
		if(isNullOrEmpty(user)){
			errors.add("You must specify a user name for the ID");
		}
		
		if(isNullOrEmpty(password)){
			errors.add("You must specify a password");
		}
		view.errorsField().setText(errors.toString());
		
		view.nextButton().setEnabled(errors.isEmpty());
	}
	
	private boolean isNullOrEmpty(String text){
		return text==null || text.trim().length()==0;
	}
}
