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
package com.moss.appsnap.manager.groups;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.moss.appsnap.api.security.SecurityMode;
import com.moss.swing.dialog.DialogablePanel;

@SuppressWarnings("serial")
public class AddGroupDialog extends DialogablePanel {
	private AddGroupDialogView view = new AddGroupDialogView();
	
	private String name;
	private SecurityMode securityMode;
	
	private boolean allInputIsValid = false;
	private List<String> errors = new LinkedList<String>();
	
	public AddGroupDialog() {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(view);
	
		view.okButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonPressed();
			}
		});
		
		view.cancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		DocumentListener dl = new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				handleInput();
			}
			public void insertUpdate(DocumentEvent e) {
				handleInput();
			}
			public void removeUpdate(DocumentEvent e) {
				handleInput();
			}
		};
		
		view.nameField().getDocument().addDocumentListener(dl);
		
		view.securityModeWidget().setSelection(SecurityMode.WHITELIST);
		view.securityModeWidget().setListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleInput();
			}
		});
		
		handleInput();
	}
	
	private void handleInput(){
		// glean
		name = view.nameField().getText();
		securityMode = view.securityModeWidget().getSelection();
		
		// validate
		errors.clear();
		if(name==null || name.trim().length()==0){
			errors.add("You must specify a name");
		}
		if(securityMode==null){
			errors.add("You must specify a security mode");
		}
		allInputIsValid = errors.size()==0;
		
		
		StringBuilder text = new StringBuilder();
		for(String error : errors){
			text.append(error);
			text.append('\n');
		}
		view.errorsText().setText(text.toString());
		
		view.okButton().setEnabled(allInputIsValid);
	}
	
	private void okButtonPressed(){
		if(allInputIsValid){
			selectionHappened(name, securityMode);
			dispose();
		}
	}
	
	protected void selectionHappened(String name, SecurityMode securityMode){
		
	}
}
