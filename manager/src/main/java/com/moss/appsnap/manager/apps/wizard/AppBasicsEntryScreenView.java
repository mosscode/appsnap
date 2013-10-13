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
package com.moss.appsnap.manager.apps.wizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

@SuppressWarnings("serial")
public class AppBasicsEntryScreenView extends JPanel {
	private JButton backButton;
	private JTextArea textArea;
	private JTextPane textPane;
	private JButton nextButton;
	private JTextField textField;
	public AppBasicsEntryScreenView() {
		super();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0,0};
		setLayout(gridBagLayout_1);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0};
		gridBagLayout.rowHeights = new int[] {0,0,0,0};
		panel.setLayout(gridBagLayout);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.weighty = 1.0;
		gridBagConstraints_11.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.weightx = 1.0;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 1;
		add(panel, gridBagConstraints_11);

final JLabel appNameLabel = new JLabel();
		appNameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		appNameLabel.setText("Name");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel.add(appNameLabel, gridBagConstraints);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		panel.add(textField, gridBagConstraints_3);

		final JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText("Description");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_1.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		panel.add(descriptionLabel, gridBagConstraints_1);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		panel.add(scrollPane, gridBagConstraints_2);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setForeground(Color.RED);
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_9.fill = GridBagConstraints.BOTH;
		gridBagConstraints_9.gridwidth = 2;
		gridBagConstraints_9.gridy = 2;
		gridBagConstraints_9.gridx = 0;
		panel.add(textPane, gridBagConstraints_9);

		final JPanel panel_2 = new JPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.weighty = 1.0;
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.gridx = 1;
		panel.add(panel_2, gridBagConstraints_10);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.columnWidths = new int[] {0,0,0};
		panel_1.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_12.gridx = 0;
		gridBagConstraints_12.gridy = 2;
		add(panel_1, gridBagConstraints_12);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(AppBasicsEntryScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 0;
		panel_1.add(backButton, gridBagConstraints_5);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(AppBasicsEntryScreenView.class, "/go-next.png"));
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridx = 2;
		gridBagConstraints_8.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.weightx = 1.0;
		panel_1.add(nextButton, gridBagConstraints_8);
		nextButton.setText("Next");

		final JLabel label = new JLabel();
		label.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 24));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setText("Describe the Application");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		add(label, gridBagConstraints_4);
	}
	public JTextField appNameField() {
		return textField;
	}
	public JButton nextButton() {
		return nextButton;
	}
	public JTextPane errorsField() {
		return textPane;
	}
	public JTextArea descriptionField() {
		return textArea;
	}
	public JButton backButton() {
		return backButton;
	}

}
