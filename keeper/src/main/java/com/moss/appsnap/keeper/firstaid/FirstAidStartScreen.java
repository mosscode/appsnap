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
package com.moss.appsnap.keeper.firstaid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.keeper.Guts;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.SimpleMessageScreen;
import com.moss.greenshell.wizard.progress.ProgressMonitorScreen;
import com.moss.identity.tools.IdProover;

public class FirstAidStartScreen extends AbstractScreen<FirstAidStartScreenView>{
	public FirstAidStartScreen(Guts guts, PublicationId keeperPublication,
			IdProover idProver, ScreenAction backAction) {
		this(
				"Confirm First Aid Run", 
				"<html><body style=\"font-family:arial,sans-serif\" ><p><b>WARNING:</b> This is the first aid tool.  This is a special tool that should only be used:</p> <ul><li>When you have been directed to do so by technical support personel.</li><li>When your applications won't launch at all</li></ul></body></html>",
			guts,
			keeperPublication,
			idProver,
			backAction
			);
	}
	
	public FirstAidStartScreen(String title, String message, final Guts guts, final PublicationId keeperPublication,
			final IdProover idProver, final ScreenAction backAction) {
		super(title, new FirstAidStartScreenView());
		view.titleField().setText(title);
		view.messageField().setText(message);
		
		if(backAction==null){
			view.backButton().setVisible(false);
		}else{
			view.backButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					backAction.setEnvironment(environment);
					backAction.actionPerformed(e);
				}
			});
		}
		
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProgressMonitorScreen.runAndMonitor(
						new AbstractScreenAction("Running First Aid") {
							public void actionPerformed(ActionEvent e) {
								try {
									new FirstAidTool(guts, keeperPublication, idProver).run();
									environment.next(new SimpleMessageScreen("First Aid Complete"));
								} catch (Exception e1) {
									environment.failCatastrophically(e1);
								}
							}
						},
						environment
				);
			}
		});
	}
}
