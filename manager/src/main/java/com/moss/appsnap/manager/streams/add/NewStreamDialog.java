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
package com.moss.appsnap.manager.streams.add;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.manager.apps.select.AppSelectDialog;
import com.moss.swing.dialog.DialogablePanel;

public class NewStreamDialog extends DialogablePanel {
	private NewStreamDialogView view = new NewStreamDialogView();
	
	private String name;
	private StreamInfo parent;
	private SecurityMode mode;
	private List<AppId> apps = new LinkedList<AppId>();
	private DefaultListModel appsModel = new DefaultListModel();
	
	private boolean allInputIsValid = false;
	
	public NewStreamDialog(final List<AppInfo> allApps) {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(view);
		
		DocumentListener updateListener = new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				updated();
			}
			
			public void insertUpdate(DocumentEvent e) {
				updated();
			}
			
			public void changedUpdate(DocumentEvent e) {
				updated();
			}
		};
		
		view.nameField().getDocument().addDocumentListener(updateListener);
		view.securityModeWidget().setListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updated();
			}
		});
		
		view.addAppButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppInfo selection = new AppSelectDialog(allApps).show(NewStreamDialog.this);
				if(selection!=null){
					apps.add(selection.id());
					appsModel.addElement(new AppOption(selection));
				}
				updated();
			}
		});
		view.removeAppButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = view.appsList().getSelectedIndex();
				if(index!=-1){
					appsModel.remove(index);
					apps.remove(index);
					updated();
				}
			}
		});
		
		view.okButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(allInputIsValid){
					selectionHappened(name, parent==null?null:parent.id(), mode, apps);
					dispose();
				}else{
					JOptionPane.showMessageDialog(NewStreamDialog.this, "Invalid input.");
				}
			}
		});
		
		view.cancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		view.appsList().setModel(appsModel);
		
	}
	
	private static class AppOption {
		private final AppInfo info;
		
		public AppOption(AppInfo info) {
			super();
			this.info = info;
		}

		@Override
		public String toString() {
			return info.name();
		}
	}
	
	public NewStreamDialog setParentStream(StreamInfo parent) {
		this.parent = parent;
		if(parent!=null){
			this.view.parentNameField().setText(parent.name());
			this.view.tabbedPane().removeTabAt(1);
		}else{
			this.view.parentNameField().setText("n/a");
		}
		return this;
	}
	
 	private void updated(){
		try {
			name = view.nameField().getText();
			mode = view.securityModeWidget().getSelection();
			
			allInputIsValid = 
				(name!=null && name.trim().length()>0) // name is valid
				&&
				mode!=null
				&& 
				((parent==null && apps.size()>0) || (parent!=null && apps.size()==0))
				;
		} catch (Throwable e) {
			allInputIsValid = false;
			e.printStackTrace();
		}
	}
	protected void selectionHappened(String name, StreamId parent, SecurityMode mode, List<AppId> apps){
		
	}
}
