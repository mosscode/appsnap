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
package com.moss.appsnap.manager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.apps.AppInfo;
import com.moss.appsnap.api.catalog.GroupCatalog;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.api.groups.AppPublicationInfo;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.InstallableDetails;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.api.streams.StreamInfo;
import com.moss.appsnap.groupmanage.GroupManager;
import com.moss.appsnap.groupmanage.PublicationAction;
import com.moss.appsnap.manager.apps.NewJavaAppParams;
import com.moss.appsnap.manager.apps.NewJavaAppletParams;
import com.moss.appsnap.manager.apps.inspector.AppInspector;
import com.moss.appsnap.manager.apps.wizard.AppCreator;
import com.moss.appsnap.manager.apps.wizard.NewAppDialog;
import com.moss.appsnap.manager.groups.AddGroupDialog;
import com.moss.appsnap.manager.installables.InstallableInspector;
import com.moss.appsnap.manager.publications.PublicationInspector;
import com.moss.appsnap.manager.streams.add.NewStreamDialog;
import com.moss.appsnap.manager.streams.inspector.StreamInspector;
import com.moss.appsnap.manager.streams.tree.StreamsTreeNodeFactory;
import com.moss.appsnap.uitools.tree.WindowUtil;
import com.moss.identity.IdProofRecipie;
import com.moss.identity.simple.SimpleIdIdToolPlugin;
import com.moss.identity.simple.swing.SimpleProofRecipieEditor;
import com.moss.identity.tools.IdProover;
import com.moss.identity.tools.IdProovingException;
import com.moss.identity.tools.IdTool;
import com.moss.identity.tools.swing.proofrecipie.ProofRecipieEditor;
import com.moss.identity.tools.swing.proofrecipie.ProofRecipieEditorDialog;
import com.moss.identity.veracity.VeracityIdToolPlugin;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;
import com.moss.swing.dialog.DialogablePanel.ExitMode;
import com.moss.veracity.identity.swing.VeracityProofRecipieEditorPlugin;

public class Manager {
	private static final Dimension MIN_WIN_SIZE = new Dimension(800, 600); 
	private static final Dimension MIN_INTERNAL_FRAME_SIZE = new Dimension(250, 250);
	
	private final ManagerView view = new ManagerView();
	private final JFrame window = new JFrame("Appsnap Manager");
	private final AppsnapService snap;
	
	private final WindowEnvironment windows = new WindowEnvironment() {
		public void showInspector(InstallableId installable) {
			raiseOrCreate(installable, installableInspectorFactory);
		}
		public void showInspector(PublicationId publication) {
			raiseOrCreate(publication, publicationInspectorFactory);
		}
	};
	
	private static interface FrameFactory<Id, T extends JInternalFrame>{
		T create(Id id) throws Exception ;
	}
	
	private Map<Object, WeakReference<JInternalFrame>> inspectors = new HashMap<Object, WeakReference<JInternalFrame>>();
	
	private final FrameFactory<PublicationId, PublicationInspector> publicationInspectorFactory = new FrameFactory<PublicationId, PublicationInspector>() {
		public PublicationInspector create(PublicationId id) throws Exception {
			List<GroupCatalog> catalogs = snap.getSoftwareCatalog(prover.giveProof());
			for (GroupCatalog next : catalogs) {
				
				for(PublicationInfo nextPub : next.getPublications()){
					if(nextPub.id().equals(id)){
						return new PublicationInspector(nextPub);
					}
				}
			}
			
			return null;
		}
	};
	
	private final FrameFactory<InstallableId, InstallableInspector> installableInspectorFactory = new FrameFactory<InstallableId, InstallableInspector>() {
		public InstallableInspector create(InstallableId id) throws Exception {
			InstallableDetails details = snap.getInstallableDetails(id, prover.giveProof());
			return (new InstallableInspector(details));
		}
	};
	
	private final FrameFactory<AppId, AppInspector> appInspectorFactory = new FrameFactory<AppId, AppInspector>() {
		public AppInspector create(AppId id) throws Exception {
			AppDetails details = snap.getAppDetails(id, prover.giveProof());
			return new AppInspector(details, snap, prover, windows);
		}
	};
	
	private final FrameFactory<GroupId, JInternalFrame> groupInspectorFactory = new FrameFactory<GroupId, JInternalFrame>() {
		public JInternalFrame create(GroupId id) throws Exception {
			final GroupDetails details = snap.getGroupDetails(id, prover.giveProof());
			GroupManager m = new GroupManager(details, snap, prover);
			DefaultFrame frame = new DefaultFrame("Group: " + details.name());
			frame.setContent(m);
			
			m.setPublicationInspectionAction(new PublicationAction() {
				public void execute(AppPublicationInfo info) {
					windows.showInspector(info.id());
				}
			});
			
			return frame;
		}
	};
	
	private final FrameFactory<StreamId, JInternalFrame> streamInspectorFactory = new FrameFactory<StreamId, JInternalFrame>() {
		public JInternalFrame create(StreamId id) throws Exception {
			final StreamDetails details = snap.getStreamDetails(id, prover.giveProof());
			return new StreamInspector(details, snap, prover);
		}
		
	};
	
	private IdProover prover;

	public Manager(final AppsnapService snap, final String veracityServiceName) throws Exception {
		super();
		this.snap = snap;
		
		
		view.addAppButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newAppDialog();
			}
		});
		
		view.appsList().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
			}
		});
		
		view.appsList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>1){
					showSelectedApp();
				}
			}
		});
		
		view.appsList().addMouseListener(
		         new MouseAdapter() {
		             public void mousePressed( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             public void mouseReleased( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             private void checkForTriggerEvent( MouseEvent e )
		             {
		                if ( e.isPopupTrigger() ) {
		                	int index = view.appsList().locationToIndex(e.getPoint());
		                	if(index!=-1){
		                		AppListEntry selection = (AppListEntry) view.appsList().getModel().getElementAt(index);
		                		makePopupFor(selection.app).show( e.getComponent(),
		                				e.getX(), e.getY() );               
		                	}
		                }
		             }
		          }
		       );  
		
		
		view.groupsList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>1){
					showSelectedGroup();
				}
			}
		});
		
		view.groupsList().addMouseListener(
		         new MouseAdapter() {
		             public void mousePressed( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             public void mouseReleased( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             private void checkForTriggerEvent( MouseEvent e )
		             {
		                if ( e.isPopupTrigger() ) {
		                	int index = view.groupsList().locationToIndex(e.getPoint());
		                	if(index!=-1){
		                		GroupListItem selection = (GroupListItem) view.groupsList().getModel().getElementAt(index);
		                		makePopupFor(selection.info).show( e.getComponent(),
		                				e.getX(), e.getY() );               
		                	}
		                }
		             }
		          }
		       );  
		
		view.streamsTree().addMouseListener(
		         new MouseAdapter() {
		             public void mousePressed( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             public void mouseReleased( MouseEvent e )
		                { checkForTriggerEvent( e ); } 

		             private void checkForTriggerEvent( MouseEvent e )
		             {
		                if ( e.isPopupTrigger() ) {
		                	TreePath path = view.streamsTree().getPathForLocation(e.getX(), e.getY());
		                	if(path!=null){
		                		Object selection = path.getLastPathComponent();
		                		if(selection instanceof StreamInfo){
		                			StreamInfo stream = (StreamInfo)selection;
		                			makePopupFor(stream).show( e.getComponent(),
		                					e.getX(), e.getY() );               
		                			
		                		}
		                	}
		                }
		             }
		          }
		       );  
		
		
		
		view.addStreamButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addStreamDialog();
			}
		});
		
		view.addGroupButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGroupDialog();
			}
		});
		
		window.getContentPane().add(view);
		window.pack();
		if(window.getWidth()<MIN_WIN_SIZE.width){
			window.setSize(MIN_WIN_SIZE.width, window.getHeight());
		}
		if(window.getHeight()<MIN_WIN_SIZE.height){
			window.setSize(window.getWidth(), MIN_WIN_SIZE.height);
		}
		window.setLocationRelativeTo(null);
		
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});
		

		window.setVisible(true);
		
		
		final ProxyFactory proxies = new ProxyFactory(new HessianProxyProvider());

		final ProofRecipieEditor editor = new ProofRecipieEditor(
				new VeracityProofRecipieEditorPlugin(false, false, veracityServiceName),
				new SimpleProofRecipieEditor(false)
				);
		
		final ProofRecipieEditorDialog.Callback loginCallback = new ProofRecipieEditorDialog.Callback() {
			
			public boolean selected(IdProofRecipie r) {
				IdTool tool = new IdTool(new VeracityIdToolPlugin(proxies), new SimpleIdIdToolPlugin(null));
				
				try {
					prover = tool.getProver(r);
					snap.myAccount(prover.giveProof());
				} catch (SecurityException e) {
					handleError(e);
					return false;
				} catch (IdProovingException e) {
					handleError(e);
					return false;
				}
				
				try {
					loadAppsList();
					loadStreamsTree();
					loadGroupsList();
				} catch (Exception e) {
					handleError(e);
					return false;
				}
				
				return true;
			}
			
			public void cancelled() {
				System.exit(0);
			}
		};
		
		ProofRecipieEditorDialog d = new ProofRecipieEditorDialog(ExitMode.DISPOSE_ON_CLOSE, editor, loginCallback);
		
		JDialog dialog = d.makeDialogFor(window);
		dialog.setTitle("Login");
		dialog.setModal(true);
		dialog.pack();
		dialog.setLocationRelativeTo(view);
		dialog.setVisible(true);
		
	}
	
	@SuppressWarnings("serial")
	private JPopupMenu makePopupFor(final GroupInfo group){
		JPopupMenu m = new JPopupMenu();

		m.add(new AbstractAction("Open"){
			public void actionPerformed(ActionEvent e) {
				raiseOrCreate(group.id(), groupInspectorFactory);
			}
		});
		
		m.add(new AbstractAction("Rename") {
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(view, "Enter a new name for group " + group.name());
				if(newName!=null){
					try {
						snap.updateGroup(group.id(), newName, null, prover.giveProof());
						loadGroupsList();
					} catch (Exception e1) {
						handleError(e1);
					}
				}
			}
		});
		
		return m;
	}
	
	@SuppressWarnings("serial")
	private JPopupMenu makePopupFor(final StreamInfo stream){
		JPopupMenu m = new JPopupMenu();

		m.add(new AbstractAction("Open"){
			public void actionPerformed(ActionEvent e) {
				raiseOrCreate(stream.id(), streamInspectorFactory);
			}
		});
		
		m.add(new AbstractAction("Rename") {
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(view, "Enter a new name for stream " + stream.name());
				if(newName!=null){
					try {
						snap.updateStream(stream.id(), newName, null, null,  prover.giveProof());
						loadStreamsTree();
					} catch (Exception e1) {
						handleError(e1);
					}
				}
			}
		});
		
		return m;
	}
	
	@SuppressWarnings("serial")
	private JPopupMenu makePopupFor(final AppInfo app){
		JPopupMenu m = new JPopupMenu();
		
		m.add(new AbstractAction("Open"){
			public void actionPerformed(ActionEvent e) {
				raiseOrCreate(app.id(), appInspectorFactory);
			}
		});
		
		m.add(new AbstractAction("Rename") {
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(view, "Enter a new name for app " + app.name());
				if(newName!=null){
					try {
						snap.renameApplication(app.id(), newName, prover.giveProof());
						loadAppsList();
					} catch (Exception e1) {
						handleError(e1);
					}
				}
			}
		});
		
		return m;
	}
	
	private void showSelectedGroup(){
		final GroupListItem selection = (GroupListItem) view.groupsList().getSelectedValue();
		if(selection!=null){
			raiseOrCreate(selection.info.id(), groupInspectorFactory);
		}
	}
	
	@SuppressWarnings("serial")
	private void newGroupDialog(){
		JDialog d = new AddGroupDialog(){
			protected void selectionHappened(String name, SecurityMode securityMode) {
				try {
					snap.createGroup(name, securityMode, prover.giveProof());
				} catch (Exception e) {
					handleError(e);
				}
				loadGroupsList();
			}
		}.makeDialogFor(view);
		d.setTitle("New Group");
		d.pack();
		d.setLocationRelativeTo(view);
		d.setModal(true);
		d.setVisible(true);
	}
	
	private void loadGroupsList(){
		try {
			final List<GroupInfo> groups = snap.listGroups(prover.giveProof());
			DefaultListModel model = new DefaultListModel();
			for(GroupInfo next : groups){
				model.addElement(new GroupListItem(next));
			}
			view.groupsList().setModel(model);
		} catch (Exception e) {
			handleError(e);
		}
	}
	
	private static class GroupListItem {
		private final GroupInfo info;

		public GroupListItem(GroupInfo info) {
			super();
			this.info = info;
		}
		
		@Override
		public String toString() {
			return info.name();
		}
		
	}
	
	private void newAppDialog(){
		
		AppCreator creationAction = new AppCreator() {
			public void createApp(NewJavaAppParams params) {
				try {
					snap.createJavaApplication(params.appName, params.initialVersion.label, params.initialVersion.spec, params.isKeeper, prover.giveProof());
				} catch (Exception e) {
					handleError(e);
				}
				try {
					loadAppsList();
				} catch (Exception e) {
					handleError(e);
				}
			}
			public void createApp(NewJavaAppletParams params) {
				try {
					snap.createJavaApplet(params.appName, params.initialVersion.label, params.initialVersion.spec, params.initialVersion.jarsignCert, prover.giveProof());
				} catch (Exception e) {
					handleError(e);
				}
				try {
					loadAppsList();
				} catch (Exception e) {
					handleError(e);
				}
			}
		};
		JDialog d = new NewAppDialog(snap, prover, creationAction, new ProxyFactory(new HessianProxyProvider())).makeDialogFor(window);
		d.setTitle("New App Wizard");
		WindowUtil.packWithinLimits(d, new Dimension(800, 600), null);
		d.setLocationRelativeTo(window);
		d.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	private void addStreamDialog(){

		try {
			final StreamInfo parent;
			{
				TreePath streamsSelection = view.streamsTree().getSelectionPath();
				if(streamsSelection!=null){
					parent = ((StreamInfo)streamsSelection.getLastPathComponent());
				}else{
					parent = null;
				}
			}
			
			List<AppInfo> apps = snap.listApps(prover.giveProof());
			JDialog dialog = new NewStreamDialog(apps){
				protected void selectionHappened(String name, StreamId parent, SecurityMode mode, List<AppId> apps) {
					try {
						snap.createStream(parent, name, mode, prover.giveProof(), apps.toArray(new AppId[apps.size()]));
						loadStreamsTree();
					} catch (Exception e) {
						handleError(e);
					}
				};
			}
			.setParentStream(parent)
			.makeDialogFor(Manager.this.view);
			dialog.setModal(true);
			dialog.setTitle("New Stream");
			dialog.pack();
			dialog.setLocationRelativeTo(Manager.this.view);
			dialog.setVisible(true);
		} catch (Throwable e1) {
			handleError(e1);
		}
	}
	
	private void showSelectedStream(){
		final TreePath selection = view.streamsTree().getSelectionPath();
		if(selection!=null){
			final Object v = selection.getLastPathComponent();
			if(v instanceof StreamInfo){
				final StreamInfo info = (StreamInfo)v;
				raiseOrCreate(info.id(), streamInspectorFactory);
			}
		}
	}
	
	private void showSelectedApp(){
		Object selection = view.appsList().getSelectedValue();
		if(selection!=null){
			AppInfo app = ((AppListEntry)selection).app;
			raiseOrCreate(app.id(), this.appInspectorFactory);
		}
	}
	
	
	private <Id> void raiseOrCreate(final Id id, FrameFactory<Id, ?> factory){
		WeakReference<JInternalFrame> reference = inspectors.get(id);
		if(reference==null || reference.get()==null){
			try {
				JInternalFrame frame = factory.create(id);
				frame.addInternalFrameListener(new InternalFrameAdapter(){
					@Override
					public void internalFrameClosing(InternalFrameEvent e) {
						inspectors.remove(id);
					}
				});
				reference = new WeakReference<JInternalFrame>(frame);
				inspectors.put(id, reference);
				showAFrame(frame);
			} catch (Exception e) {
				handleError(e);
			}
		}else{
			JInternalFrame frame = reference.get();
			view.desktopPane().moveToFront(frame);
			try {
				if(frame.isIcon()){
					frame.setIcon(false);
				}
				reference.get().setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void showAFrame(JInternalFrame window){
		window.pack();
		view.desktopPane().add(window);
		
		if(window.getWidth()<MIN_INTERNAL_FRAME_SIZE.width){
			window.setSize(MIN_INTERNAL_FRAME_SIZE.width, window.getHeight());
		}
		if(window.getHeight()<MIN_INTERNAL_FRAME_SIZE.height){
			window.setSize(window.getWidth(), MIN_INTERNAL_FRAME_SIZE.height);
		}
		
		
		boolean oversizedWidth = window.getWidth()>view.desktopPane().getWidth();
		boolean oversizedHeight = window.getHeight()>view.desktopPane().getHeight();
		
		if(oversizedHeight && oversizedWidth){
			try {
				window.setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}else if(oversizedHeight){
			window.setSize(window.getWidth(), view.desktopPane().getHeight());
		}else if(oversizedWidth){
			window.setSize(view.desktopPane().getWidth(), window.getHeight());
		}
		window.setVisible(true);
	}

	private void loadStreamsTree() throws Exception {
		final List<StreamInfo> streams = snap.listStreams(prover.giveProof());
		
		StreamsTreeNodeFactory f = new StreamsTreeNodeFactory(streams);
		
		view.streamsTree().setModel(f.treeModel());
		view.streamsTree().setCellRenderer(f.treeRenderer());
		view.streamsTree().invalidate();
		view.validate();
		view.streamsTree().repaint();
	}
	
	private void loadAppsList() throws Exception {
		List<AppInfo> apps = snap.listApps(prover.giveProof());
		
		DefaultListModel model = new DefaultListModel();
		
		for(AppInfo next : apps){
			model.addElement(new AppListEntry(next));
		}
		
		view.appsList().setModel(model);
	}
	
	private void handleError(Throwable t){
		t.printStackTrace();
		JOptionPane.showMessageDialog(this.view, "Error: " + t.getMessage());
	}
	
	public void shutdown(){
		System.exit(0);
	}
	
	private static final class AppListEntry {
		private final AppInfo app;

		public AppListEntry(AppInfo app) {
			super();
			this.app = app;
		}
		
		@Override
		public String toString() {
			return app.name();
		}
	}
}
