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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.greenshell.wizard.AbstractScreen;
import com.moss.greenshell.wizard.AbstractScreenAction;
import com.moss.greenshell.wizard.ScreenAction;

public class SeriesSelectionScreen extends AbstractScreen<SeriesSelectionScreenView> {
	private final JTextField textField;
	private final NewInstallableWizardState<?> wizModel;
	private ScreenAction nextAction;
	
	public SeriesSelectionScreen(final NewInstallableWizardState<?> wizModel, final ScreenAction backAction) {
		super("To which version series does this version belong?", new SeriesSelectionScreenView());
		this.wizModel = wizModel;
		
		view.backButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction.setEnvironment(environment);
				backAction.actionPerformed(null);
			}
		});
		
		final ScreenAction backhereAction = new AbstractScreenAction() {
			public void actionPerformed(ActionEvent e) {
				environment.previous(SeriesSelectionScreen.this);
			}
		};
		
		{
			// add all the existing paths
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			for(AppVersionSeries path : wizModel.details.series()){
				model.addElement(path.name());
			}
			
			// add the pre-selected path if it doesn't already exist
			if(wizModel.params.series!=null){
				boolean found = false;
				for(AppVersionSeries path : wizModel.details.series()){
					if(path.name().equals(wizModel.params.series)){
						found = true;
					}
				}
				if(!found){
					model.addElement(wizModel.params.series);
				}
			}
			view.pathComboBox().setModel(model);
		}
		
		view.nextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPathNote("Added to series \"" + wizModel.params.series +"\"");
				nextAction.setEnvironment(environment);
				nextAction.actionPerformed(e);
			}
		});
		
		if(wizModel.params.series==null){
			view.pathComboBox().setSelectedIndex(-1);
		}else{
			view.pathComboBox().setSelectedItem(wizModel.params.series);
		}
		
		view.pathComboBox().addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updated();
			}
		});
		
		textField = (JTextField) view.pathComboBox().getEditor().getEditorComponent();
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				updated();
			}
			
			public void insertUpdate(DocumentEvent e) {
				updated();
			}
			
			public void changedUpdate(DocumentEvent e) {
				updated();
			}
		});
//		.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				System.out.println(".");
//				updated();
//			}
//		});
		updated();
		
//		new Thread(){
//			public void run() {
//				while(true){
//					try {
//						Thread.sleep(1000);
//						System.out.println(selection);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
	}
	
	
	public void setNextAction(ScreenAction nextAction) {
		this.nextAction = nextAction;
	}
	
	
	void updated(){
		wizModel.params.series = textField.getText();
		
		if(wizModel.params.series!=null && wizModel.params.series.trim().length()>0){
			view.nextButton().setEnabled(true);
		}else{
			view.nextButton().setEnabled(false);
		}
	}
}
