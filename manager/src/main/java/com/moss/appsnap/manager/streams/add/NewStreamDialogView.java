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
package com.moss.appsnap.manager.streams.add;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.manager.util.SecurityModeWidget;

public class NewStreamDialogView extends JPanel {
	private JPanel panel_2;
	private JTabbedPane tabbedPane;
	private JButton cancelButton;
	private JButton okButton;
	private SecurityModeWidget securityModeWidget;
	private JLabel label;
	private JButton removeButton;
	private JButton addButton;
	private JList list;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField textField;
	public NewStreamDialogView() {
		super();
		setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		add(tabbedPane);

		final JPanel panel = new JPanel();
		tabbedPane.addTab("General", null, panel, null);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0,0};
		gridBagLayout.columnWidths = new int[] {0,0};
		panel.setLayout(gridBagLayout);

		final JLabel nameLabel = new JLabel();
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setText("Name:");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel.add(nameLabel, gridBagConstraints_2);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 0, 0, 5);
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		panel.add(textField, gridBagConstraints_3);

		final JLabel parentLabel = new JLabel();
		parentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		parentLabel.setText("Parent:");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		panel.add(parentLabel, gridBagConstraints);

		label = new JLabel();
		label.setText("New JLabel");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 0, 5);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 1;
		panel.add(label, gridBagConstraints_1);

		final JLabel groupSecurityModeLabel = new JLabel();
		groupSecurityModeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		groupSecurityModeLabel.setText("Group Security Mode:");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 0;
		panel.add(groupSecurityModeLabel, gridBagConstraints_4);

		securityModeWidget = new SecurityModeWidget();
		securityModeWidget.setSelection(SecurityMode.BLACKLIST);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 1;
		panel.add(securityModeWidget, gridBagConstraints_5);



		final JPanel panel_3 = new JPanel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weighty = 1.0;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 1;
		panel.add(panel_3, gridBagConstraints_8);

		panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		tabbedPane.addTab("Applications", null, panel_2, null);

		final JPanel panel_4 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0,0};
		panel_4.setLayout(gridBagLayout_1);
		panel_2.add(panel_4, BorderLayout.EAST);

		addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		panel_4.add(addButton, gridBagConstraints_10);

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.gridy = 1;
		gridBagConstraints_9.gridx = 0;
		panel_4.add(removeButton, gridBagConstraints_9);

		final JPanel panel_5 = new JPanel();
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.weighty = 1.0;
		gridBagConstraints_11.gridy = 2;
		gridBagConstraints_11.gridx = 0;
		panel_4.add(panel_5, gridBagConstraints_11);

		final JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		list = new JList();
		scrollPane.setViewportView(list);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		add(panel_1, BorderLayout.SOUTH);

		okButton = new JButton();
		okButton.setText("OK");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.EAST;
		gridBagConstraints_6.weightx = 1.0;
		panel_1.add(okButton, gridBagConstraints_6);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 5, 5);
		panel_1.add(cancelButton, gridBagConstraints_7);

	}
	public JList appsList() {
		return list;
	}
	public JButton addAppButton() {
		return addButton;
	}
	public JButton removeAppButton() {
		return removeButton;
	}
	public JTextField nameField() {
		return textField;
	}
	public JLabel parentNameField() {
		return label;
	}
	public SecurityModeWidget securityModeWidget() {
		return securityModeWidget;
	}
	public JButton okButton() {
		return okButton;
	}
	public JButton cancelButton() {
		return cancelButton;
	}
	public JTabbedPane tabbedPane() {
		return tabbedPane;
	}
	public JPanel appsTab() {
		return panel_2;
	}

}
