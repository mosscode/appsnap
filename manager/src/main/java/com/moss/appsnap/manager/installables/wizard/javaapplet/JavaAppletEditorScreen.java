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
package com.moss.appsnap.manager.installables.wizard.javaapplet;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.launch.spec.applet.AppletParameter;

public class JavaAppletEditorScreen extends AbstractScreen<JavaAppletEditorScreenView> {
	private DefaultListModel parametersModel;
	private final NewJavaAppletInstallableWizardState wizModel;
	private ScreenAction nextAction;
	
	public JavaAppletEditorScreen(final NewJavaAppletInstallableWizardState wizModel, final ScreenAction backAction) {
		super("Configure to taste", new JavaAppletEditorScreenView());
		this.wizModel = wizModel;
		
		view.backButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction.setEnvironment(environment);
				backAction.actionPerformed(null);
			}
		});
		
		
		
		
		view.argumentsList().setCellRenderer(new DefaultListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				
				AppletParameter param = (AppletParameter) value;
				String displayValue = param.name() + "=" + param.value();
				
				return super.getListCellRendererComponent(list, displayValue, index, isSelected,cellHasFocus);
			}
			
		});
		

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
					selectedIndex = wizModel.parameters.size();
				}else{
				}
				
				String name = JOptionPane.showInputDialog(view, "Enter Parameter Name");
				if(name==null)return;
				String value = JOptionPane.showInputDialog(view, "Enter Parameter Value");
				if(value==null) return;
				
				AppletParameter arg = new AppletParameter(name, value);
				wizModel.parameters.add(selectedIndex, arg);
				parametersModel.add(selectedIndex, arg);
			}
		});
		
		view.removeButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = view.argumentsList().getSelectedIndex();
				wizModel.parameters.remove(selectedIndex);
				parametersModel.remove(selectedIndex);
			}
		});
		view.editButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = view.argumentsList().getSelectedIndex();
				
				if(selectedIndex!=-1){
					AppletParameter arg = wizModel.parameters.get(selectedIndex);
					String name = JOptionPane.showInputDialog(view, "Enter Parameter Name", arg.name());
					if(name==null)return;
					String value = JOptionPane.showInputDialog(view, "Enter Parameter Value", arg.value());
					if(value==null) return;
					
					arg = new AppletParameter(name, value);
					wizModel.parameters.set(selectedIndex, arg);
					parametersModel.set(selectedIndex, arg);
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
				// APPLY ARGUMENTS EDITS
				wizModel.params.spec.parameters().clear();
				wizModel.params.spec.parameters().addAll(wizModel.parameters);
				
				nextAction.setEnvironment(environment);
				nextAction.actionPerformed(e);
//				wizModel.entryHandler.appletEntered(wizModel.params);
			}
		});
		
		update();
	}
	
	@Override
	public void shown() {
		this.parametersModel = new DefaultListModel();
		for(AppletParameter next : wizModel.parameters){
			parametersModel.addElement(next);
		}

		view.argumentsList().setModel(parametersModel);
	}
	
	public void setNextAction(ScreenAction nextAction) {
		this.nextAction = nextAction;
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
