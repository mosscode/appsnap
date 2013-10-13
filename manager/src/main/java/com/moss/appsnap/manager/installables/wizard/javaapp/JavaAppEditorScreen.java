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
package com.moss.appsnap.manager.installables.wizard.javaapp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.launch.spec.app.launch.Argument;

public class JavaAppEditorScreen extends AbstractScreen<JavaAppEditorScreenView> {
	private DefaultListModel argsModel;
	private final NewJavaAppInstallableWizardState wizModel;
	
	public JavaAppEditorScreen(final NewJavaAppInstallableWizardState wizModel, final ScreenAction backAction) {
		super("Configure to taste", new JavaAppEditorScreenView());
		this.wizModel = wizModel;
		this.argsModel = new DefaultListModel();
		
		view.backButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction.setEnvironment(environment);
				backAction.actionPerformed(null);
			}
		});
		
		
		for(Argument next : wizModel.args){
			argsModel.addElement(next);
		}
		
		view.argumentsList().setModel(argsModel);

		view.argumentsList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = view.argumentsList().getSelectedIndex();
				
				if(!e.getValueIsAdjusting()){
					boolean hasSelection = selectedIndex!=-1;
					view.editButton().setEnabled(hasSelection);
					view.removeButton().setEnabled(hasSelection);
				}
			}
		});

		view.editButton().setEnabled(false);
		view.removeButton().setEnabled(false);
		view.addButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int selectedIndex = view.argumentsList().getSelectedIndex();
				
				if(selectedIndex==-1){
					selectedIndex = wizModel.args.size();
				}else{
				}
				String response = JOptionPane.showInputDialog(view, "Enter Argument Value");
				if(response!=null){
					Argument arg = new Argument(response);
					wizModel.args.add(selectedIndex, arg);
					argsModel.add(selectedIndex, arg);
				}
			}
		});
		
		view.removeButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = view.argumentsList().getSelectedIndex();
				wizModel.args.remove(selectedIndex);
				argsModel.remove(selectedIndex);
			}
		});
		view.editButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = view.argumentsList().getSelectedIndex();
				
				if(selectedIndex!=-1){
					Argument arg = wizModel.args.get(selectedIndex);
					String response = JOptionPane.showInputDialog(view, "Enter Argument Value", arg.toString());
					if(response!=null){
						arg = new Argument(response);
						wizModel.args.set(selectedIndex, arg);
						argsModel.set(selectedIndex, arg);
					}
				}
			}
		});
		
		view.versionLabel().getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				update();
			}
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			public void changedUpdate(DocumentEvent e) {
				update();
			}
		});
		
		
		view.okButton().setEnabled(false);
		
		view.okButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.done();
				// APPLY ARGUMENTS EDITS
				wizModel.params.spec.arguments().clear();
				wizModel.params.spec.arguments().addAll(wizModel.args);
				
				wizModel.selectAction.appEntered(wizModel.params.label, wizModel.params.series, wizModel.params.spec);
			}
		});
		
		update();
	}
	
	private void update(){
		wizModel.params.label = view.versionLabel().getText();
		
		view.okButton().setEnabled(wizModel.params.label!=null && wizModel.params.label.trim().length()>0);
	}
	
	private void handleError(Throwable e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(view, e.getMessage());
	}
}
