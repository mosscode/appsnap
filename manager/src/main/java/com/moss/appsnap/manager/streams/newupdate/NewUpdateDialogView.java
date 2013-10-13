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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.DefaultComponentFactory;

public class NewUpdateDialogView extends JPanel {
	private JButton removeButton;
	private JButton addButton;
	private JButton cancelButton;
	private JButton okButton;
	private JTable table;
	private JTextField textField;
	public NewUpdateDialogView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0};
		gridBagLayout.columnWidths = new int[] {0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.CENTER);

		final JLabel label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 5, 0, 10);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 0;
		panel.add(label, gridBagConstraints_5);
		label.setText("Label");

		final JPanel panel_2 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0};
		panel_2.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		panel.add(panel_2, gridBagConstraints);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		panel_2.add(textField, gridBagConstraints_1);

		final JLabel versionsLabel = DefaultComponentFactory.getInstance().createLabel("Versions");
		versionsLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(10, 5, 0, 5);
		gridBagConstraints_8.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridy = 1;
		gridBagConstraints_8.gridx = 0;
		panel.add(versionsLabel, gridBagConstraints_8);

		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panel_1.setLayout(new BorderLayout());
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		panel.add(panel_1, gridBagConstraints_2);

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0};
		panel_3.setLayout(gridBagLayout_2);
		panel_1.add(panel_3, BorderLayout.EAST);

		addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		panel_3.add(addButton, gridBagConstraints_4);

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_3.weighty = 1.0;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel_3.add(removeButton, gridBagConstraints_3);

		final JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);

		final JLabel label_1 = DefaultComponentFactory.getInstance().createLabel("&New Label:");
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, label_1);

		final JPanel panel_1_1 = new JPanel();
		panel_1_1.setLayout(new GridBagLayout());
		add(panel_1_1, BorderLayout.SOUTH);

		okButton = new JButton();
		okButton.setText("OK");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.EAST;
		gridBagConstraints_6.weightx = 1.0;
		panel_1_1.add(okButton, gridBagConstraints_6);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 5, 5);
		panel_1_1.add(cancelButton, gridBagConstraints_7);
	}
	public JButton okButton() {
		return okButton;
	}
	public JButton cancelButton() {
		return cancelButton;
	}
	public JTable versionsTable() {
		return table;
	}
	public JButton addVersionButton() {
		return addButton;
	}
	public JButton removeVersionButton() {
		return removeButton;
	}
	public JTextField labelField() {
		return textField;
	}

}
