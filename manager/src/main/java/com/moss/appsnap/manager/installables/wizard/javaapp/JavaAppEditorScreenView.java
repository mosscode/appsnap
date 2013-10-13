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
package com.moss.appsnap.manager.installables.wizard.javaapp;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

public class JavaAppEditorScreenView extends JPanel {
	private JButton backButton;
	private JButton editButton;
	private JList list;
	private JButton removeButton;
	private JButton addButton;
	private JButton okButton;
	private JTextField textField_1;
	public JavaAppEditorScreenView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0};
		gridBagLayout.rowHeights = new int[] {0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.CENTER);

		final JLabel label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setText("Version Label");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		panel.add(label, gridBagConstraints_1);

		textField_1 = new JTextField();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 1;
		panel.add(textField_1, gridBagConstraints_4);

		final JLabel initialVersionLabel = new JLabel();
		initialVersionLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		initialVersionLabel.setText("Arguments");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_2.insets = new Insets(0, 5, 5, 5);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		panel.add(initialVersionLabel, gridBagConstraints_2);

		final JPanel panel_2 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0};
		panel_2.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_5.weighty = 1.0;
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 1;
		panel.add(panel_2, gridBagConstraints_5);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.BOTH;
		gridBagConstraints_9.weighty = 1.0;
		gridBagConstraints_9.weightx = 1.0;
		panel_2.add(scrollPane, gridBagConstraints_9);

		list = new JList();
		scrollPane.setViewportView(list);

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0,0};
		panel_3.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_6.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 1;
		panel_2.add(panel_3, gridBagConstraints_6);

		addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		panel_3.add(addButton, gridBagConstraints_10);

		editButton = new JButton();
		editButton.setText("Edit");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		panel_3.add(editButton, gridBagConstraints);

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_7.weighty = 1.0;
		gridBagConstraints_7.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		panel_3.add(removeButton, gridBagConstraints_7);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.columnWidths = new int[] {0,0,0};
		panel_1.setLayout(gridBagLayout_3);
		add(panel_1, BorderLayout.SOUTH);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(JavaAppEditorScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 0;
		panel_1.add(backButton, gridBagConstraints_3);

		okButton = new JButton();
		okButton.setIcon(SwingResourceManager.getIcon(JavaAppEditorScreenView.class, "/go-next.png"));
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridx = 2;
		gridBagConstraints_8.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.weightx = 1.0;
		panel_1.add(okButton, gridBagConstraints_8);
		okButton.setText("Next");
	}
	public JTextField versionLabel() {
		return textField_1;
	}
	public JButton okButton() {
		return okButton;
	}
	public JButton addButton() {
		return addButton;
	}
	public JButton removeButton() {
		return removeButton;
	}
	public JList argumentsList() {
		return list;
	}
	public JButton editButton() {
		return editButton;
	}
	public JButton backButton() {
		return backButton;
	}
}
