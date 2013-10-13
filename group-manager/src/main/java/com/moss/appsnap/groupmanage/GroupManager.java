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
package com.moss.appsnap.groupmanage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.groups.AppPublicationInfo;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.groupmanage.publications.PublicationsTableModel;
import com.moss.appsnap.groupmanage.publications.add.PublicationInfoDialog;
import com.moss.appsnap.groupmanage.publications.add.SelectPublicationDialog;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.identity.Id;
import com.moss.identity.tools.IdProover;

@SuppressWarnings("serial")
public class GroupManager extends JPanel {
	private final GroupManagerView view = new GroupManagerView();
	
	private final AppsnapService snap;
	private final IdProover idProver;
	
	private GroupDetails details;
	
	private PublicationAction publicationInspectionAction;
	
	public GroupManager(final GroupDetails initDetails, final AppsnapService snap, final IdProover idProver) {
		setLayout(new BorderLayout());
		add(view);
		
		this.snap = snap;
		this.idProver = idProver;
		
		this.details = initDetails;
		
		view.addPublicationButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAddPublicationDialog();
			}
		});
		
		view.editButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelectedPubliction();
			}
		});
		view.editButton().setEnabled(false);
		view.recalPublicationButton().setEnabled(false);
		view.publicationsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				final int selectedIndex = view.publicationsTable().getSelectedRow();
				
				if(selectedIndex!=-1){
					boolean rowSelected = view.publicationsTable().getSelectedRow()!=-1;
					view.recalPublicationButton().setEnabled(rowSelected);
					view.editButton().setEnabled(rowSelected);
					
					final AppPublicationInfo p = details.publications().get(selectedIndex);
					System.out.println("Selected publication " + p.id() + " of group " + details.id());
				}
			}
		});
		
		view.publicationsTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					final int selectedIndex = view.publicationsTable().getSelectedRow();
					if(selectedIndex!=-1 && publicationInspectionAction!=null){
						publicationInspectionAction.execute(details.publications().get(selectedIndex));
					}
				}
			}
		});
		
		view.changeMembershipModeButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final SecurityMode newMode = SecurityMode.toggleValue(details.membershipSecurityMode());
				final String message = "This will change the membership security mode to " + newMode + ".  Proceed?";
				final int result = JOptionPane.showConfirmDialog(view, message, "Confirm security mode change", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION){
					try {
						snap.updateGroup(details.id(), null, newMode, GroupManager.this.idProver.giveProof());
						refresh();
					} catch (Exception e1) {
						handleError(e1);
					}
				}
			}
		});
		view.addMemberButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMember();
			}
		});
		view.removeMemberButton().setEnabled(false);
		view.removeMemberButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedMember();
			}
		});
		view.membersList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					view.removeMemberButton().setEnabled(view.membersList().getSelectedIndex()!=-1);
				}
			}
		});
		
		refresh();
	}
	
	public void setPublicationInspectionAction(PublicationAction action){
		publicationInspectionAction = action;
	}
	
	private void addMember(){
		// TODO: IMPlement me
	}
	
	private void removeSelectedMember(){
		final int selectedIndices[] = view.membersList().getSelectedIndices();
		if(selectedIndices!=null && selectedIndices.length>0){
			List<Id> membership = new LinkedList<Id>();
			for(int index : selectedIndices){
				membership.add(details.membership().get(index));
			}
			
			try {
				snap.removeGroupMembers(details.id(), membership, idProver.giveProof());
			} catch (Exception e) {
				handleError(e);
			}
		}
	}
	
	private void editSelectedPubliction(){
		final int selectionIdx = view.publicationsTable().getSelectedRow();
		if(selectionIdx!=-1){
			final AppPublicationInfo p = details.publications().get(selectionIdx);

			PublicationInfoDialog d = new PublicationInfoDialog();

			d.setValue(p.name(), p.description(), null);

			d.setCallback(new PublicationInfoDialog.Callback() {
				public void publicationInfoEntered(String name, String description, File iconPath) {
					try {
						snap.renamePublication(p.id(), name, description, idProver.giveProof());
						refresh();
					}catch(Exception e){
						handleError(e);
					}

				}
			});

			JDialog dialog = d.makeDialogFor(view);
			WindowUtil.packWithinLimits(dialog, new Dimension(800, 600), null);
			dialog.setLocationRelativeTo(view);
			dialog.setModal(true);
			dialog.setVisible(true);


		}
	}
	private void refresh(){
		try {
			details = snap.getGroupDetails(details.id(), idProver.giveProof());
			view.publicationsTable().setModel(new PublicationsTableModel(details.publications()));
			
			{// members
				DefaultListModel model = new DefaultListModel();
				for(Id next : details.membership()){
					model.addElement(next);
				}
				view.membersList().setModel(model);
				
				view.membershipModeLabel().setText("Membership Mode: " + details.membershipSecurityMode());
			}
			
			{// administrators
				DefaultListModel model = new DefaultListModel();
				for(Id next : details.administrators()){
					model.addElement(next);
				}
				view.administratorsList().setModel(model);
			}
			
		} catch (Exception e) {
			handleError(e);
		}
		
	}
	
	private void showAddPublicationDialog(){
		try {
			final List<StreamInfo> infos = snap.listStreams(idProver.giveProof());
			List<StreamDetails> details = new LinkedList<StreamDetails>();
			for(StreamInfo next : infos){
				details.add(snap.getStreamDetails(next.id(), idProver.giveProof()));
			}
			JDialog d = new SelectPublicationDialog(this.details.id(), details){
				protected void selectionHappened(final StreamDetails stream, final com.moss.appsnap.api.streams.AppVersionInfo selection) {
					
					PublicationInfoDialog d = new PublicationInfoDialog();
					d.setCallback(new PublicationInfoDialog.Callback() {
						public void publicationInfoEntered(String name, String description, File iconPath) {
							try {
								snap.publishToGroup(name, description, stream.id(), selection.appInfo().id(), GroupManager.this.details.id(), idProver.giveProof());
							} catch (Exception e) {
								handleError(e);
							}
							refresh();

						}
					});
					
					JDialog dialog = d.makeDialogFor(view);
					WindowUtil.packWithinLimits(dialog, new Dimension(800, 600), null);
					dialog.setLocationRelativeTo(view);
					dialog.setModal(true);
					dialog.setVisible(true);
				}
			}.makeDialogFor(view);
			d.setTitle("Select a publication");
			d.pack();
			d.setLocationRelativeTo(view);
			d.setModal(true);
			d.setVisible(true);
		} catch (Exception e) {
			handleError(e);
		}
		
	}
	
	private void handleError(Throwable t){
		t.printStackTrace();
		JOptionPane.showMessageDialog(view, "Error: " + t.getMessage());
	}
}
