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
package com.moss.appsnap.manager.streams.newupdate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.installables.InstallableInfo;
import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.manager.apps.select.VersionSelectDialog;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.swing.dialog.DialogablePanel;

@SuppressWarnings("serial")
public class NewUpdateDialog extends DialogablePanel {
	private final NewUpdateDialogView view = new NewUpdateDialogView();
	
	private boolean allInputIsValid = false;
	
	private String name;
	private List<AppVersionInfo> versions = new LinkedList<AppVersionInfo>();
	private final List<AppVersionInfo> currentVersions;
	private final List<AppDetails> apps;
	
	public NewUpdateDialog(final List<AppDetails> apps, final List<AppVersionInfo> currentVersions) {
		super(ExitMode.DISPOSE_ON_CLOSE);
		this.apps = apps;
		this.currentVersions = Collections.unmodifiableList(currentVersions);
		
		setLayout(new BorderLayout());
		add(view);
		
		view.addVersionButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addVersion();
			}
		});
		
		view.okButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(allInputIsValid){
					selectionHappened(name, versions);
					dispose();
				}else{
					JOptionPane.showMessageDialog(NewUpdateDialog.this, "Invalid input.");
				}
			}
		});
		
		view.cancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		view.labelField().getDocument().addDocumentListener(new DocumentListener() {
			private void update(){
				name = view.labelField().getText();
				validateInput();
			}
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
		
		updateVersionsTable();
	}
	private void validateInput(){
		allInputIsValid = true;
		if(name==null || name.trim().length()==0){
			allInputIsValid = false;
		}
		if(versions.size()==0){
			allInputIsValid = false;
		}
		
		view.okButton().setEnabled(allInputIsValid);
	}
	private void updateVersionsTable(){
		view.versionsTable().setModel(new AppVersionsTableModel(currentVersions, versions, apps));
		view.versionsTable().validate();
		view.validate();
		view.repaint();
	}
	
	private void addVersion(){
		List<AppOption> options = new LinkedList<AppOption>();
		
		for(AppDetails d : apps){
			boolean alreadySpecified = false;
			for(AppVersionInfo s : versions){
				if(s.appInfo().id().equals(d.id())){
					alreadySpecified = true;
				}
			}
			if(!alreadySpecified){
				options.add(new AppOption(d));
			}
		}
		
		final AppOption selection = (AppOption) 
								JOptionPane.showInputDialog(
										view, 
										"Select an app", 
										"Select an app", 
										JOptionPane.INFORMATION_MESSAGE, 
										null, 
										options.toArray(), 
										null
									);
		if(selection!=null){
			JDialog d = new VersionSelectDialog(selection.details){
				protected void selectionHappened(InstallableInfo installable) {
					AppVersionInfo currentVersion = currentVersion(selection.details.id());
					if(currentVersion.versionInfo()!=null && currentVersion.versionInfo().id().equals(installable.id())){
						JOptionPane.showMessageDialog(view, "Invalid selection: app " + selection.details.name() + " is already at version " + currentVersion.versionInfo().label() + " in this stream");
					}else{
						versions.add(new AppVersionInfo(selection.details, installable));
						validateInput();
						updateVersionsTable();
					}
				};
			}.makeDialogFor(view);
			d.setTitle("Select version");
			
			WindowUtil.packWithinLimits(d, new Dimension(300, 300), null);
			d.setLocationRelativeTo(view);
			d.setVisible(true);

		}
	}
	private AppVersionInfo currentVersion(AppId id){
		for(AppVersionInfo next : currentVersions){
			if(next.appInfo().id().equals(id)){
				return next;
			}
		}
		return null;
	}
	static class AppOption {
		final AppDetails details;
		
		public AppOption(AppDetails details) {
			super();
			this.details = details;
		}
		@Override
		public String toString() {
			return details.name();
		}
	}
	
	protected void selectionHappened(String name, List<AppVersionInfo> versions){
		
	}
}
