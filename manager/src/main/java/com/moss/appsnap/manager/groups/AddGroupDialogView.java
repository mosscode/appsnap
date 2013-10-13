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
package com.moss.appsnap.manager.groups;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.moss.appsnap.manager.util.SecurityModeWidget;

public class AddGroupDialogView extends JPanel {
	private JTextPane textPane;
	private SecurityModeWidget securityModeWidget;
	private JTextField textField;
	private JButton cancelButton;
	private JButton okButton;
	public AddGroupDialogView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel_1_1 = new JPanel();
		panel_1_1.setLayout(new GridBagLayout());
		add(panel_1_1, BorderLayout.PAGE_END);

		okButton = new JButton();
		okButton.setText("OK");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		panel_1_1.add(okButton, gridBagConstraints);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		panel_1_1.add(cancelButton, gridBagConstraints_1);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0};
		gridBagLayout.rowHeights = new int[] {0,0,0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.CENTER);

		final JLabel nameLabel = new JLabel();
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setText("Name:");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel.add(nameLabel, gridBagConstraints_2);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 1;
		panel.add(textField, gridBagConstraints_5);

		final JLabel securityModeLabel = new JLabel();
		securityModeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		securityModeLabel.setText("Security Mode:");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel.add(securityModeLabel, gridBagConstraints_3);

		securityModeWidget = new SecurityModeWidget();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		panel.add(securityModeWidget, gridBagConstraints_4);

		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.setForeground(Color.RED);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.gridwidth = 2;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		panel.add(textPane, gridBagConstraints_7);

		final JPanel panel_1 = new JPanel();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1.0;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 1;
		panel.add(panel_1, gridBagConstraints_6);
	}
	public SecurityModeWidget securityModeWidget() {
		return securityModeWidget;
	}
	public JTextField nameField() {
		return textField;
	}
	public JButton okButton() {
		return okButton;
	}
	public JButton cancelButton() {
		return cancelButton;
	}
	public JTextPane errorsText() {
		return textPane;
	}
	
}
