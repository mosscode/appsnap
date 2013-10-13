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
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;

import com.moss.appsnap.manager.util.FileSelectWidget.FileSelectionHandler;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.filechooser.FileChooserScreen;
import com.moss.greenshell.wizard.filechooser.FileChooserScreen.SelectionInfo;
import com.moss.launch.spec.JavaAppletSpec;

public class AppletSpecSelectScreen extends FileChooserScreen {
	private final NewJavaAppletInstallableWizardState wizModel;
	private ScreenAction selectionAction;
	
	public AppletSpecSelectScreen( final NewJavaAppletInstallableWizardState wizModel) {
		super("Select a launch spec");
		this.wizModel = wizModel;
		setFileFilter(new FileFilter(){
			@Override
			public String getDescription() {
				return "applet specs";
			}
			@Override
			public boolean accept(File path) {
				return path.exists() 
				&& 
				(
					path.isDirectory() 
					|| 
					path.getName().toLowerCase().endsWith(JavaAppletSpec.FILE_EXTENSION)
				);
			}
		});
		
		setHandler(new FileSelectionHandler() {
			
			
			
			public SelectionInfo handleSelection(File path) {
				Component c;
				boolean isChoosable = false;;
				if(path==null){
					c = (new JLabel("Nothing selected"));
				}else{

					System.out.println("Selected " + path);
					try {
						wizModel.params.spec = path==null?null:JavaAppletSpec.read(path);
						wizModel.specLocation = path.toURL();
						
						setPathNote(path.getName());
						
						
						JTextPane p = new JTextPane();
						p.setContentType("text/html");
						p.setText("Selection: " + wizModel.params.spec.name() + " (" + wizModel.params.spec.appletClass() + ")");
//						SpecViewer viewer = new SpecViewer();
//						viewer.show(wizModel.params.spec);
						c = p;
						isChoosable = true;
					} catch (Throwable e) {
						JTextPane p = new JTextPane();
						p.setContentType("text/html");
						p.setText("Error reading spec: " + e.getMessage());
						c = p;
						wizModel.params.spec = null;
						wizModel.specLocation = null;
					}
				}
				return new SelectionInfo(isChoosable, c);
			}
			
			public void fileChosen(File selection) {
				if(wizModel.params.spec!=null){
					selectionAction.setEnvironment(environment);
					selectionAction.actionPerformed(new ActionEvent(this, 1, ""));
				}else{
					JOptionPane.showMessageDialog(view, "You have not selected a valid launch spec - you must do so to proceed.");
				}				
			}
		});
	}
	
	public void setSelectionAction(ScreenAction selectionAction) {
		this.selectionAction = selectionAction;
	}
	
}
