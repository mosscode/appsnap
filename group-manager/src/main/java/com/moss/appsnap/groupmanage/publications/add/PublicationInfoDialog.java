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
package com.moss.appsnap.groupmanage.publications.add;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.moss.appsnap.groupmanage.util.ErrorsList;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.swing.dialog.DialogablePanel;
import com.moss.swing.event.DocumentAdapter;
import com.moss.swing.test.TestFrame;

@SuppressWarnings("serial")
public class PublicationInfoDialog extends DialogablePanel {
	public static void main(String[] args) {
		PublicationInfoDialog d = new PublicationInfoDialog();
		
		d.setCallback(new Callback() {
			public void publicationInfoEntered(String name, String description, File iconPath) {
				System.out.println("Name: " + name);
				System.out.println("Description: " + description);
				System.out.println("Path: " + iconPath);
			}
		});
		
		JFrame f = new TestFrame();
		
		JDialog dialog = d.makeDialogFor(f);
		WindowUtil.packWithinLimits(dialog, new Dimension(800, 600), null);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
		
	}
	public static interface Callback {
		void publicationInfoEntered(String name, String description, File iconPath);
	}
	
	private final PublicationInfoDialogView view = new PublicationInfoDialogView();
	
	private String name;
	private String description;
	private File iconPath;
	
	private Callback callback;
	
	private ErrorsList errors = new ErrorsList();
	
	public PublicationInfoDialog() {
		super(ExitMode.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(view);
		
		
		view.nameField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				name = view.nameField().getText();
				validateinput();
			}
		});
		
		view.descriptionField().getDocument().addDocumentListener(new DocumentAdapter(){
			@Override
			public void updateHappened() {
				description = view.descriptionField().getText();
				validateinput();
			}
		});
		
		view.iconChooser().addListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iconPath = view.iconChooser().getImagePath();
				validateinput();
			}
		});
		
		view.cancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		view.okButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(errors.isEmpty()){
					callback.publicationInfoEntered(name, description, iconPath);
					dispose();
				}else{
					JOptionPane.showMessageDialog(view, errors.toString());
				}
			}
		});
		
		validateinput();
	}
	
	public void setValue(String name, String description, Icon icon){
		this.name = name;
		this.description = description;
		this.iconPath = null;
		
		view.nameField().setText(name==null?"":name);
		view.descriptionField().setText(description==null?"":description);
		view.iconChooser().setDefaultIcon(icon);
		
	}
	
	private void validateinput(){
		errors.clear();
		
		if(isNullOrEmpty(name)){
			errors.add("You must specify a name.");
		}
		if(isNullOrEmpty(description)){
			errors.add("You must specify a description.");
		}
		
	}
	
	private boolean isNullOrEmpty(String text){
		return text==null || text.trim().length()==0;
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}