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
package com.moss.appsnap.groupmanage.publications.add;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.moss.swing.IconChooser;

@SuppressWarnings("serial")
public class PublicationInfoDialogView extends JPanel {
	private JButton cancelButton;
	private JButton okButton;
	private IconChooser iconChooser;
	private JTextArea textArea;
	private JTextField textField;
	public PublicationInfoDialogView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.SOUTH);

		okButton = new JButton();
		okButton.setText("Ok");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel.add(okButton, gridBagConstraints);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_1.gridx = 2;
		panel.add(cancelButton, gridBagConstraints_1);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0};
		gridBagLayout_1.rowHeights = new int[] {0,0,0,0};
		panel_1.setLayout(gridBagLayout_1);
		add(panel_1, BorderLayout.CENTER);

		final JLabel nameLabel = new JLabel();
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setText("Name:");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel_1.add(nameLabel, gridBagConstraints_2);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 1;
		panel_1.add(textField, gridBagConstraints_4);

		final JLabel descriptionLabel = new JLabel();
		descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		descriptionLabel.setText("Description:");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints_3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel_1.add(descriptionLabel, gridBagConstraints_3);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_5.weighty = 1.0;
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 1;
		panel_1.add(scrollPane, gridBagConstraints_5);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);

		iconChooser = new IconChooser();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridheight = 3;
		gridBagConstraints_6.insets = new Insets(5, 5, 10, 0);
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 1;
		panel_1.add(iconChooser, gridBagConstraints_6);

		final JLabel iconLabel = new JLabel();
		iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		iconLabel.setText("Icon:");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 0, 0, 5);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridy = 3;
		gridBagConstraints_7.gridx = 0;
		panel_1.add(iconLabel, gridBagConstraints_7);
	}
	public IconChooser iconChooser() {
		return iconChooser;
	}
	public JTextArea descriptionField() {
		return textArea;
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

}
