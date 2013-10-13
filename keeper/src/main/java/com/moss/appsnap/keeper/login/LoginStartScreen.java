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

import com.moss.appsnap.api.AppsnapService;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleBackAction;
import com.moss.greenshell.wizard.menu.AbstractMenuScreen;
import com.moss.rpcutil.proxy.ProxyFactory;

public class LoginStartScreen extends AbstractMenuScreen {
	
	public LoginStartScreen(final String serviceName, final AppsnapService snap, final ProxyFactory proxyFactory, final PostLoginAction finalAction, final String veracityServiceDomain) {
		super("Is this your first time installing " + serviceName + " software?");
		
//		showNote("<html><body>This will walk you through the installation process.  The first step is to log-in to the system.");
		
		final ScreenAction backHereAction = new SimpleBackAction(this);
		
		addScreenActionItem("No", "Select this option if you already have a " + serviceName + " account.", null, new AbstractScreenAction(){
			public void actionPerformed(ActionEvent e) {
				setPathNote("Login");
				environment.next(new LoginScreen(snap, proxyFactory, backHereAction, finalAction, veracityServiceDomain));
			}
		});
		addScreenActionItem("Yes", "I don't have a " + serviceName + " account.  I want to sign-up up for a free account.", null, new AbstractScreenAction(){
			public void actionPerformed(ActionEvent e) {
				setPathNote("Sign-Up");
				environment.next(new NewOrExistingIdScreen(serviceName, backHereAction, snap, proxyFactory, finalAction));
			}
		});

	}
}
