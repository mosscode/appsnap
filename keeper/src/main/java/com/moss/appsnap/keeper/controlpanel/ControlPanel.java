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
package com.moss.appsnap.keeper.controlpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.HandyTool;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.KeeperQuery;
import com.moss.appsnap.keeper.data.jaxbstore.ValueScanner;
import com.moss.appsnap.keeper.installerapp.InstallerParams;
import com.moss.appsnap.keeper.installerapp.PublicationSelectionScreen;
import com.moss.appsnap.keeper.login.LoginStartScreen;
import com.moss.appsnap.keeper.login.PostLoginAction;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.greenshell.wizard.ProcessPanel;
import com.moss.greenshell.wizard.ScreenAction;
import com.moss.greenshell.wizard.ScreenEnvironment;
import com.moss.identity.tools.IdProover;

/**
 * OPTIONS:
 *   Install new software
 */
public class ControlPanel extends JPanel {
	private ControlPanelView view = new ControlPanelView();
	
	private static class ListEntry {
		final InstallId id;
		final String name;
		
		public ListEntry(InstallId id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}
	private final Guts guts;
	
	public ControlPanel(final Guts guts, final String veracityServiceDomain) {
		this.guts = guts;
		setLayout(new BorderLayout());
		add(view);
		
		refresh();
		
		view.uninstallButton().setEnabled(false);
		
		view.installsList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				view.uninstallButton().setEnabled(view.installsList().getSelectedIndex()!=-1);
			}
		});
		
		view.uninstallButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ListEntry selection = (ListEntry) view.installsList().getSelectedValue();
				
				if(selection!=null){
					try {
						HandyTool tool = new HandyTool(guts);
						tool.uninstall(selection.id);
						refresh();
					} catch (Throwable e1) {
						handleError(e1);
					}
				}
			}
		});
		
		view.installButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KeeperQuery q = new KeeperQuery();
				
				guts.data.installs.scan(q);
				
				final InstallerParams params = new InstallerParams(
						guts.snapLocation.toString(),
						guts.serviceInfo,
						q.results().get(0).publication()
					);

				final JFrame w = new JFrame(guts.serviceInfo.name() + " app store");
				
				ProcessPanel panel = new ProcessPanel(){
					@Override
					public void done() {
						w.dispose();
						refresh();
					}
				};
				
				
				PostLoginAction action = new PostLoginAction(){
					public void run(IdProover idProver, ScreenEnvironment environment,
							ScreenAction backAction) {
						environment.next(new PublicationSelectionScreen(params, guts.snap, idProver, guts.proxyFactory, null));				
					}
				};
				
				LoginStartScreen screen = new LoginStartScreen(guts.serviceInfo.name(), guts.snap, guts.proxyFactory, action, veracityServiceDomain);
				
				panel.start(screen);
				
				
				w.getContentPane().add(panel);
				WindowUtil.packWithinLimits(w, new Dimension(800, 600), null);
				w.setLocationRelativeTo(null);
				w.setVisible(true);
			}
		});
		
		
	}
	
	private void handleError(Throwable e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
	}
	private void refresh(){
		final DefaultListModel model = new DefaultListModel();
		
		guts.data.installs.scan(new ValueScanner<InstallationInfo>() {
					public boolean scan(InstallationInfo value) {
						if(!value.isKeeperSoftware()){
							model.addElement(new ListEntry(value.id(), value.name()));
						}
						return true;
					}
				});
		
		view.installsList().setModel(model);
	}
	
}
