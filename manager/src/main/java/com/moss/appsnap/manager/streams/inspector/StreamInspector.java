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
package com.moss.appsnap.manager.streams.inspector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.apps.AppVersionSpec;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamUpdate;
import com.moss.appsnap.api.streams.StreamUpdateId;
import com.moss.appsnap.manager.DefaultFrame;
import com.moss.appsnap.manager.apps.select.AppSelectDialog;
import com.moss.appsnap.manager.streams.StreamUpdatesTableModel;
import com.moss.appsnap.manager.streams.UpdateVersionsTableModel;
import com.moss.appsnap.manager.streams.newupdate.NewUpdateDialog;
import com.moss.appsnap.manager.streams.updateselect.StreamUpdateSelector;
import com.moss.appsnap.uitools.AppVersionInfosTableModel;
import com.moss.identity.tools.IdProover;

@SuppressWarnings("serial")
public class StreamInspector extends DefaultFrame {
	private final StreamInspectorView view = new StreamInspectorView();
	private final AppsnapService snap;
	private final IdProover idProver;
	
	private StreamDetails details;
	
	public StreamInspector(final StreamDetails initDetails, final AppsnapService snap, final IdProover idProver) throws Exception {
		super("Stream: " + initDetails.name());
		this.details = initDetails;
		this.snap = snap;
		this.idProver = idProver;
		setContent(view);
		
		view.updatesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				updateSelection();
			}
		});
		
		view.newUpdateButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newUpdate();
			}
		});
		
		view.pullUpdateButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pullUpdate();
			}
		});
		
		view.addAppButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addApp();
			}
		});
		
		if(details.parent()!=null){
			// only parent streams can be the source of an update
			view.newUpdateButton().setVisible(false);
			view.addAppButton().setVisible(false);
		}else{
			// pulling from a parent stream is only possible if there actually is a parent stream
			view.pullUpdateButton().setVisible(false);
		}
		
		
		{
			view.addGroupButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						List<GroupInfo> groups = snap.listGroups(idProver.giveProof());
						List<GroupListEntry> options = new ArrayList<GroupListEntry>(groups.size());
						for(GroupInfo next : groups){
							options.add(new GroupListEntry(next));
						}
						
						GroupListEntry selection = (GroupListEntry) JOptionPane.showInputDialog(
															view, 
															"Select a Group", 
															"Select a Group", 
															JOptionPane.INFORMATION_MESSAGE, 
															null, 
															options.toArray(), 
															null
														);
						if(selection!=null){
							List<GroupId> members = new LinkedList<GroupId>();
							members.addAll(details.groupSecurityRoster());
							members.add(selection.details.id());
							snap.updateStream(details.id(), null, null, members, idProver.giveProof());
							refresh();
						}
					} catch (Exception e1) {
						handleError(e1);
					}
					
				}
			});
			
			view.removeGroupButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selection = view.groupsList().getSelectedIndex();
					if(selection!=-1){
						try {
							GroupId selectedGroup = details.groupSecurityRoster().get(selection);
							List<GroupId> members = new LinkedList<GroupId>();
							members.addAll(details.groupSecurityRoster());
							members.remove(selectedGroup);
							snap.updateStream(details.id(), null, null, members, idProver.giveProof());
							refresh();
						} catch (Exception e1) {
							handleError(e1);
						}
					}
				}
			});
			
			view.changeSecurityModeButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					if(details.securityMode()!=null){
						final SecurityMode other;
						switch(details.securityMode()){
						case BLACKLIST:
							other = SecurityMode.WHITELIST;
							break;
							
						case WHITELIST:
							other = SecurityMode.BLACKLIST;
							break;
							
						default:
							throw new RuntimeException("Unknown security mode type");
						}
						
						int response = JOptionPane.showConfirmDialog(view, "This will change the security mode to " + other + ".  Are you sure you want to proceed?");
						if(response==JOptionPane.OK_OPTION){
							try {
								snap.updateStream(details.id(), null, other, null, idProver.giveProof());
								refresh();
							} catch (Exception e1) {
								handleError(e1);
							}
						}
					}
				}
			});

		}
		
		updateSelection();
		
		showData(details);
	}
	
	
	static class GroupListEntry {
		final GroupInfo details;

		public GroupListEntry(GroupInfo details) {
			super();
			this.details = details;
		}
		
		@Override
		public String toString() {
			return details.name();
		}
	}
	
	private void addApp(){
		try {
			final AppInfo selection = new AppSelectDialog(snap.listApps(idProver.giveProof())).show(view);
			if(selection!=null){
				snap.addAppToStream(details.id(), selection.id(), idProver.giveProof());
				refresh();
			}
		} catch (Throwable e) {
			handleError(e);
		}
	}

	private void refresh(){
		try {
			showData(snap.getStreamDetails(details.id(), idProver.giveProof()));
		} catch (Throwable e) {
			handleError(e);
		}
	}
	private void showData(StreamDetails details) throws Exception {
		this.details = details;
		view.updatesTable().setModel(new StreamUpdatesTableModel(details));
		
		{
			DefaultListModel model = new DefaultListModel();
			List<GroupDetails> selectedGroups = new ArrayList<GroupDetails>(details.groupSecurityRoster().size());
			for(final GroupId next : details.groupSecurityRoster()){
				final GroupDetails groupDetails = snap.getGroupDetails(next, idProver.giveProof()); 
				selectedGroups.add(groupDetails);
				model.addElement(new GroupListEntry(groupDetails));
			}
			view.groupsList().setModel(model);
			view.securityModeLabel().setText("Security mode: " + details.securityMode().name());
			view.appsTable().setModel(new AppVersionInfosTableModel(details.applications()));
		}
	}
	
	private void pullUpdate(){
		try {
			final StreamDetails parent = snap.getStreamDetails(details.parent(), idProver.giveProof());
			
			final List<StreamUpdate> available = new LinkedList<StreamUpdate>() ;
			if(details.updates().size()>0){
				final StreamUpdateId lastUpdate = details.updates().get(details.updates().size()-1).id();
				
				boolean pastLastUpdate = false;
				for(StreamUpdate next : parent.updates()){
					if(pastLastUpdate){
						available.add(next);
					}else if(next.id().equals(lastUpdate)){
						pastLastUpdate = true;
					}
				}
			}else{
				available.addAll(parent.updates());
			}
			
			// SHOW UPDATE SELECTION DIALOG
			
			JDialog dialog = new StreamUpdateSelector(parent, available){
				protected void selectionHappened(StreamUpdate selection) {
					try {
						snap.relayDownstream(details.id(), selection.id(), idProver.giveProof());
						refresh();
					} catch (Exception e) {
						handleError(e);
					}
				}
			}.makeDialogFor(view);
			
			dialog.setName("Select a parent version to relay");
			dialog.pack();
			dialog.setLocationRelativeTo(view);
			dialog.setModal(true);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			handleError(e);
		}
	}
	
	private void newUpdate(){
		try {
			List<AppDetails> appDetails = new LinkedList<AppDetails>();
			
			for(AppVersionInfo next : details.applications()){
				appDetails.add(snap.getAppDetails(next.appInfo().id(), idProver.giveProof()));
			}
			
			JDialog d = new NewUpdateDialog(appDetails, details.applications()){
				protected void selectionHappened(String name, List<AppVersionInfo> versions) {
					try {
						AppVersionSpec[] versionSpecs = new AppVersionSpec[versions.size()];
						
						for(int x=0;x<versions.size();x++){
							AppVersionInfo next = versions.get(x);
							versionSpecs[x] = new AppVersionSpec(next.appInfo().id(), next.versionInfo().id());
						}
						
						snap.sendDownStream(details.id(), name, idProver.giveProof(), versionSpecs);
						
						refresh();
					} catch (Exception e) {
						handleError(e);
					}
				};
			}.makeDialogFor(StreamInspector.this);
			d.setTitle("New Update for Stream " + details.name());
			d.pack();
			d.setLocationRelativeTo(view);
			d.setModal(true);
			d.setVisible(true);
		} catch (Throwable e1) {
			handleError(e1);
		}
	}
	
	private void updateSelection(){
		int updateIndex = view.updatesTable().getSelectedRow();
		if(updateIndex!=-1){
			StreamUpdate update = details.updates().get(updateIndex);
			view.updateVersionsTable().setModel(new UpdateVersionsTableModel(update, details));
		}else{
			view.updateVersionsTable().setModel(new DefaultTableModel());
		}
	}
	
	private void handleError(Throwable e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
	}
}