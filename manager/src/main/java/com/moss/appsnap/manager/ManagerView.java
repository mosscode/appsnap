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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

public class ManagerView extends JPanel {
	private JButton addGroupButton;
	private JButton addButton;
	private JDesktopPane desktopPane;
	private JButton addButton_1;
	private JList list_1;
	private JTree tree;
	private JList list;
	public ManagerView() {
		super();
		setLayout(new BorderLayout());

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(250);
		add(splitPane);

		final JTabbedPane tabbedPane = new JTabbedPane();
		splitPane.setLeftComponent(tabbedPane);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		tabbedPane.addTab("Apps", null, panel_2, null);

		final JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane);

		list = new JList();
		scrollPane.setViewportView(list);

		final JToolBar toolBar_1 = new JToolBar();
		panel_2.add(toolBar_1, BorderLayout.NORTH);

		addButton_1 = new JButton();
		addButton_1.setText("Add");
		toolBar_1.add(addButton_1);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		tabbedPane.addTab("Streams", null, panel_1, null);

		final JToolBar toolBar = new JToolBar();
		panel_1.add(toolBar, BorderLayout.NORTH);

		addButton = new JButton();
		addButton.setText("Add");
		toolBar.add(addButton);

		final JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, BorderLayout.CENTER);

		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		scrollPane_1.setViewportView(tree);

		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new BorderLayout());
		tabbedPane.addTab("Groups", null, panel_3, null);

		final JScrollPane scrollPane_3 = new JScrollPane();
		panel_3.add(scrollPane_3, BorderLayout.CENTER);

		list_1 = new JList();
		scrollPane_3.setViewportView(list_1);

		final JToolBar toolBar_3 = new JToolBar();
		panel_3.add(toolBar_3, BorderLayout.NORTH);

		addGroupButton = new JButton();
		addGroupButton.setText("Add");
		toolBar_3.add(addGroupButton);

		desktopPane = new JDesktopPane();
		splitPane.setRightComponent(desktopPane);

		final JInternalFrame internalFrame = new JInternalFrame();
		internalFrame.getContentPane().setLayout(new BorderLayout());
		internalFrame.setIconifiable(true);
		internalFrame.setMaximizable(true);
		internalFrame.setResizable(true);
		internalFrame.setTitle("App Install: 15f53072-286d-4e54-8901-26bc3e26e40c");
		internalFrame.setVisible(true);
		internalFrame.setBounds(10, 275, 376, 335);
		desktopPane.add(internalFrame);

		final InstallInspectorView installInspectorView = new InstallInspectorView();
		internalFrame.getContentPane().add(installInspectorView);

		final JInternalFrame internalFrame_1 = new JInternalFrame();
		internalFrame_1.getContentPane().setLayout(new BorderLayout());
		internalFrame_1.setIconifiable(true);
		internalFrame_1.setMaximizable(true);
		internalFrame_1.setResizable(true);
		internalFrame_1.setTitle("App Keeper: 46a0b474-682d-480d-b9ea-3a23a003a24b");
		internalFrame_1.setVisible(true);
		internalFrame_1.setBounds(364, 10, 338, 249);
		desktopPane.add(internalFrame_1);

		final KeeperInspectorView keeperInspectorView = new KeeperInspectorView();
		internalFrame_1.getContentPane().add(keeperInspectorView);

	}
	public JList appsList() {
		return list;
	}
	public JButton addAppButton() {
		return addButton_1;
	}
	public JDesktopPane desktopPane() {
		return desktopPane;
	}
	public JTree streamsTree() {
		return tree;
	}
	public JButton addStreamButton() {
		return addButton;
	}
	public JButton addGroupButton() {
		return addGroupButton;
	}
	public JList groupsList() {
		return list_1;
	}

}
