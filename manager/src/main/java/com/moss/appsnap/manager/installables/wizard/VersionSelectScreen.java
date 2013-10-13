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
package com.moss.appsnap.manager.installables.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.installables.InstallableInfo;
import com.moss.appsnap.manager.apps.tree.AppVersionsTreeHandlerFactory;
import com.moss.appsnap.uitools.tree.JTreeUtil;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.ScreenAction;

public class VersionSelectScreen extends AbstractScreen<VersionSelectScreenView> {
	
	private InstallableInfo selection;
	private ScreenAction nextAction;
	
	public VersionSelectScreen(String title, AppDetails details) {
		super(title, new VersionSelectScreenView());
		

		AppVersionsTreeHandlerFactory f = new AppVersionsTreeHandlerFactory(details);
		
		view.versionsTree().setModel(f.treeModel());
		view.versionsTree().setCellRenderer(f.cellRenderer());
		
		
		view.versionsTree().addTreeSelectionListener(
				new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent e) {
						
						// clear the existing selection
						selection = null;
						
						
						TreePath treeSelection = e.getNewLeadSelectionPath();
						if(treeSelection!=null){
							Object o = treeSelection.getLastPathComponent();
							if(o!=null && o instanceof InstallableInfo){
								selection = (InstallableInfo) o;
							}
						}
						
					}
				}
		);
		
		view.selectButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selection!=null){
					nextAction.setEnvironment(environment);
					nextAction.actionPerformed(e);
				}
			}
		});
		
		JTreeUtil.expandAll(view.versionsTree());
	}
	public InstallableInfo selection() {
		return selection;
	}
	public void setNextAction(ScreenAction nextAction) {
		this.nextAction = nextAction;
	}
}
