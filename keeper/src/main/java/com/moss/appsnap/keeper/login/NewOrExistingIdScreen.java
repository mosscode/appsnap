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

public class NewOrExistingIdScreen extends AbstractMenuScreen {
	public NewOrExistingIdScreen(final String serviceName, final ScreenAction backAction, final AppsnapService snap, final ProxyFactory proxies, final PostLoginAction finalAction) {
		super("Do you already have a network ID?");
		
		showBackButton(true);
		setBackAction(backAction);
		
		final ScreenAction backHereAction = new SimpleBackAction(this);
		
		addScreenActionItem("Yes, I have one already", "Please let me use my existing ID", "", new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(new SignUpScreen(backHereAction, snap, proxies, finalAction, "@" + serviceName));
			}
		});
		
		addScreenActionItem("No, I need to create one.", "Create a new veracity ID", "", new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.next(new CreateVeracityAccountScreen(serviceName, proxies, backHereAction, finalAction, snap));
			}
		});

	}
}
