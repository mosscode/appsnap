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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.DefaultComponentFactory;

class GroupManagerView extends JPanel {
	private JButton changeButton;
	private JLabel membershipLabel;
	private JList list_1;
	private JButton removeButton;
	private JButton button;
	private JButton editButton;
	private JTable table_1;
	private JButton recallButton;
	private JButton addButton;
	private JTable table;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JList list;
	public GroupManagerView() {
		super();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0};
		setLayout(gridBagLayout_1);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {};
		panel.setLayout(gridBagLayout);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(panel, gridBagConstraints);

		final JTabbedPane tabbedPane = new JTabbedPane();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		add(tabbedPane, gridBagConstraints_2);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		tabbedPane.addTab("Publications", null, panel_2, null);

		final JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane);

		table_1 = new JTable();
		scrollPane.setViewportView(table_1);

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0,0,0};
		panel_3.setLayout(gridBagLayout_2);
		panel_2.add(panel_3, BorderLayout.EAST);

		addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		panel_3.add(addButton, gridBagConstraints_5);

		editButton = new JButton();
		editButton.setText("Edit");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		panel_3.add(editButton, gridBagConstraints_1);

		recallButton = new JButton();
		recallButton.setText("Recall");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		panel_3.add(recallButton, gridBagConstraints_7);

		final JPanel panel_5 = new JPanel();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1.0;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 0;
		panel_3.add(panel_5, gridBagConstraints_6);

		final JPanel panel_9 = new JPanel();
		panel_9.setLayout(new BorderLayout());
		tabbedPane.addTab("Installations", null, panel_9, null);

		final JPanel panel_10 = new JPanel();
		final GridBagLayout gridBagLayout_5 = new GridBagLayout();
		gridBagLayout_5.rowHeights = new int[] {0,0};
		panel_10.setLayout(gridBagLayout_5);
		panel_9.add(panel_10, BorderLayout.EAST);

		final JButton recallButton_1 = new JButton();
		recallButton_1.setText("Recall");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(5, 5, 0, 5);
		panel_10.add(recallButton_1, gridBagConstraints_13);

		final JPanel panel_11 = new JPanel();
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.weighty = 1.0;
		gridBagConstraints_14.gridy = 1;
		gridBagConstraints_14.gridx = 0;
		panel_10.add(panel_11, gridBagConstraints_14);

		final JScrollPane scrollPane_2 = new JScrollPane();
		panel_9.add(scrollPane_2, BorderLayout.CENTER);

		table = new JTable();
		scrollPane_2.setViewportView(table);

		final JPanel panel_4 = new JPanel();
		final GridBagLayout gridBagLayout_6 = new GridBagLayout();
		gridBagLayout_6.rowHeights = new int[] {0,0,0};
		panel_4.setLayout(gridBagLayout_6);
		tabbedPane.addTab("Members", null, panel_4, null);

		final JPanel panel_8 = new JPanel();
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.rowHeights = new int[] {0,0};
		gridBagLayout_4.columnWidths = new int[] {0,0};
		panel_8.setLayout(gridBagLayout_4);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 0;
		panel_4.add(panel_8, gridBagConstraints_11);

		final JPanel panel_12 = new JPanel();
		panel_12.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = GridBagConstraints.BOTH;
		gridBagConstraints_16.gridx = 0;
		gridBagConstraints_16.gridy = 0;
		panel_8.add(panel_12, gridBagConstraints_16);

		membershipLabel = new JLabel();
		membershipLabel.setText("Membership:");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_17.gridy = 1;
		panel_8.add(membershipLabel, gridBagConstraints_17);

		changeButton = new JButton();
		changeButton.setText("Change...");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 0, 0, 0);
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		panel_8.add(changeButton, gridBagConstraints_4);

		final JComponent separator = DefaultComponentFactory.getInstance().createSeparator("Security Roster", SwingConstants.CENTER);
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.gridy = 1;
		gridBagConstraints_19.gridx = 0;
		panel_4.add(separator, gridBagConstraints_19);

		final JPanel panel_13 = new JPanel();
		panel_13.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.weighty = 1.0;
		gridBagConstraints_18.fill = GridBagConstraints.BOTH;
		gridBagConstraints_18.weightx = 1.0;
		gridBagConstraints_18.gridx = 0;
		gridBagConstraints_18.gridy = 2;
		panel_4.add(panel_13, gridBagConstraints_18);

		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.fill = GridBagConstraints.BOTH;
		gridBagConstraints_12.weightx = 1.0;
		gridBagConstraints_12.weighty = 1.0;
		gridBagConstraints_12.gridx = 0;
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.insets = new Insets(5, 5, 5, 5);
		panel_13.add(scrollPane_1, gridBagConstraints_12);

		list = new JList();
		scrollPane_1.setViewportView(list);

		final JPanel panel_6 = new JPanel();
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.BOTH;
		gridBagConstraints_15.gridx = 1;
		gridBagConstraints_15.gridy = 0;
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 0);
		panel_13.add(panel_6, gridBagConstraints_15);
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.rowHeights = new int[] {0,0,0};
		panel_6.setLayout(gridBagLayout_3);

		button = new JButton();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(5, 5, 5, 5);
		panel_6.add(button, gridBagConstraints_9);
		button.setText("Add");

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridy = 1;
		gridBagConstraints_8.gridx = 0;
		panel_6.add(removeButton, gridBagConstraints_8);

		final JPanel panel_7 = new JPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.weighty = 1.0;
		gridBagConstraints_10.gridy = 2;
		gridBagConstraints_10.gridx = 0;
		panel_6.add(panel_7, gridBagConstraints_10);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		tabbedPane.addTab("Administrators", null, panel_1, null);

		final JScrollPane scrollPane_3 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_3.weighty = 1.0;
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 0;
		panel_1.add(scrollPane_3, gridBagConstraints_3);

		list_1 = new JList();
		scrollPane_3.setViewportView(list_1);
	}
	public JButton addPublicationButton() {
		return addButton;
	}
	public JButton recalPublicationButton() {
		return recallButton;
	}
	public JTable publicationsTable() {
		return table_1;
	}
	public JButton editButton() {
		return editButton;
	}
	public JButton addMemberButton() {
		return button;
	}
	public JButton removeMemberButton() {
		return removeButton;
	}
	public JList membersList() {
		return list;
	}
	public JList administratorsList() {
		return list_1;
	}
	public JLabel membershipModeLabel() {
		return membershipLabel;
	}
	public JButton changeMembershipModeButton() {
		return changeButton;
	}

}
