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
package com.moss.appsnap.manager.apps.inspector;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppType;
import com.moss.appsnap.api.apps.AppVersionSeries;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallableInfo;
import com.moss.appsnap.manager.DefaultFrame;
import com.moss.appsnap.manager.WindowEnvironment;
import com.moss.appsnap.manager.apps.tree.AppVersionsTreeHandlerFactory;
import com.moss.appsnap.manager.installables.NewJavaAppletInstallableParams;
import com.moss.appsnap.manager.installables.wizard.javaapp.JavaAppInstallableEntryHandler;
import com.moss.appsnap.manager.installables.wizard.javaapp.NewJavaAppInstallableWizardState;
import com.moss.appsnap.manager.installables.wizard.javaapp.NewVersionDialog;
import com.moss.appsnap.manager.installables.wizard.javaapplet.JavaAppletInstallableEntryHandler;
import com.moss.appsnap.manager.installables.wizard.javaapplet.NewJavaAppletInstallableWizardState;
import com.moss.appsnap.manager.installables.wizard.javaapplet.NewJavaAppletVersionDialog;
import com.moss.appsnap.uitools.tree.JTreeUtil;
import com.moss.appsnap.uitools.tree.TreeNodeHandlerTreeCellRenderer;
import com.moss.appsnap.uitools.tree.TreeNodeHandlerTreeModel;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.identity.tools.IdProover;
import com.moss.launch.spec.JavaAppSpec;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

@SuppressWarnings("serial")
public class AppInspector extends DefaultFrame {
	private final AppsnapService snap;
	private final IdProover idProver;
	private final WindowEnvironment windows;
	
	private AppDetails details;
	private final AppInspectorView view = new AppInspectorView();
	
	private final AppVersionsTreeHandlerFactory treeNodeHandlerFactory;
	
	public AppInspector(final AppDetails details, final AppsnapService snap, final IdProover idProver, final WindowEnvironment windows) {
		super("App: " + details.name());
		this.details = details;
		this.windows = windows;
		this.snap = snap;
		this.idProver = idProver;
		treeNodeHandlerFactory = new AppVersionsTreeHandlerFactory(details);
		
		getContentPane().add(view);
		view.nameLabel().setText(details.name());
		view.versionsTree().setCellRenderer(new TreeNodeHandlerTreeCellRenderer(treeNodeHandlerFactory));
		
		view.addVersionButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String series;
				{// SERIES INITIALIZATION
					TreePath versionsTreeSelection = view.versionsTree().getSelectionPath();
					if(versionsTreeSelection!=null && versionsTreeSelection.getLastPathComponent() instanceof AppVersionSeries){
						series = ((AppVersionSeries) versionsTreeSelection.getLastPathComponent()).name();
					}else{
						series = "default";
					}
				}
				
				JDialog dialog;
				
				if(details.type()==AppType.JAVA_APP){
					JavaAppInstallableEntryHandler action = new JavaAppInstallableEntryHandler(){
						public void appEntered(String label, String series, JavaAppSpec spec) {
							try {
								snap.addJavaAppVersion(details.id(), series, label, spec, idProver.giveProof());
								refresh();
							} catch (Exception e) {
								handleError(e);
							}
						}
					};
					NewJavaAppInstallableWizardState wizModel = new NewJavaAppInstallableWizardState(details, snap, idProver, action);
					wizModel.params.series = series;

					dialog = new NewVersionDialog(wizModel).makeDialogFor(view);
				}else if(details.type()==AppType.JAVA_APPLET){
					JavaAppletInstallableEntryHandler handler = new JavaAppletInstallableEntryHandler(){
						public void appletEntered(NewJavaAppletInstallableParams params) {
							try {
								InstallableId id = snap.addJavaAppletVersion(
										details.id(), 
										params.series, 
										params.label, 
										params.spec, 
										params.jarsignCert, 
										idProver.giveProof()
										);
								refresh();
							} catch (Throwable e) {
								handleError(e);
							}
						}
					};
					
					NewJavaAppletInstallableWizardState wizModel = new NewJavaAppletInstallableWizardState(details, snap, idProver);
					
					ProxyFactory proxies = new ProxyFactory(new HessianProxyProvider());
					
					dialog = new NewJavaAppletVersionDialog(wizModel, proxies, handler).makeDialogFor(view);
				}else{
					throw new RuntimeException("Error: I don't know how to make a wizard for adding a version to an app of type " + details.type());
				}
				dialog.setTitle("New version for " + details.name());
				WindowUtil.packWithinLimits(dialog, new Dimension(640, 480), null);
				dialog.setModal(true);
				dialog.setLocationRelativeTo(view);
				dialog.setVisible(true);
			}
		});
		
		view.versionsTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e )
            { checkForTriggerEvent( e ); } 

            @Override
         public void mouseReleased( MouseEvent e )
            { checkForTriggerEvent( e ); } 

         private void checkForTriggerEvent( MouseEvent e ){
				if (e.isPopupTrigger() ) {
	            	TreePath path = view.versionsTree().getPathForLocation(e.getX(), e.getY());
	            	if(path!=null){
	            		Object selection = path.getLastPathComponent();
	            		if(selection instanceof InstallableInfo){
	            			final InstallableInfo info = (InstallableInfo)selection;
	            			
	            			
	            			JPopupMenu popup = new JPopupMenu();
	            			
	            			popup.add(new AbstractAction("Inspect") {
								public void actionPerformed(ActionEvent e) {
									windows.showInspector(info.id());
								}
							});
	            			
	            			popup.show( e.getComponent(), e.getX(), e.getY() );
	            			
	            		}
	            	}
				}
			}
		});
		
		loadTree();
	}
	
	
	private void refresh(){
		try {
			details = snap.getAppDetails(details.id(), idProver.giveProof());
			loadTree();
		} catch (Throwable e) {
			handleError(e);
		}
		
	}
	
	private void loadTree(){
		view.versionsTree().setModel(new TreeNodeHandlerTreeModel(treeNodeHandlerFactory, details));
		JTreeUtil.expandAll(view.versionsTree());
		
		view.versionsTree().invalidate();
		view.validate();
		view.repaint();
	}
	
	
	
	private void handleError(Throwable t){
		t.printStackTrace();
		JOptionPane.showMessageDialog(view, t.getMessage());
	}
}
