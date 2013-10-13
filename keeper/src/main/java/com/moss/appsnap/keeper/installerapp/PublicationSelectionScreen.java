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
package com.moss.appsnap.keeper.installerapp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.keeper.storewidget.AppBrowser;
import com.moss.appsnap.keeper.storewidget.PublicationSelectionHandler;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.identity.tools.IdProover;
import com.moss.rpcutil.proxy.ProxyFactory;

public class PublicationSelectionScreen extends AbstractScreen<PublicationSelectionScreenView> {
	private final Log log = LogFactory.getLog(getClass());
	private final AppBrowser browser;
	
	public PublicationSelectionScreen(final InstallerParams params, final AppsnapService snap, final IdProover idProver, final ProxyFactory proxyFactory, final ScreenAction backAction) {
		super("Select an App", new PublicationSelectionScreenView());
		browser = new AppBrowser(snap, idProver);
		view.holderPanel().add(browser);
		view.nextButton().setEnabled(false);
		
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
		
		browser.setHandler(new PublicationSelectionHandler() {
			public void publicationSelected(PublicationInfo p) {
				
				if(p==null){
					view.nextButton().setEnabled(false);
				}else{
					log.info("Selected " + p.name() + "(" + p.id() + ")");
					view.nextButton().setEnabled(true);
				}
			}
		});
		
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PublicationInfo p = browser.getSelection();
				setPathNote(p.name());
				environment.next(new InstallProgressScreen(params, snap, idProver, p, proxyFactory));
			}
		});
	}
}
