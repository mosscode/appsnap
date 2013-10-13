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
package com.moss.appsnap.keeper.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.moss.swing.IconChooser;
import com.swtdesigner.SwingResourceManager;

public class VeracityProfileScreenView extends JPanel {
	private JButton backButton;
	private JButton nextButton;
	private JTextPane textPane;
	private IconChooser iconChooser;
	private JTextField textField_2;
	private JTextField textField_1;
	private JTextField textField;
	public VeracityProfileScreenView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0,0};
		gridBagLayout.columnWidths = new int[] {0,0,0};
		panel.setLayout(gridBagLayout);
		add(panel);

		iconChooser = new IconChooser();
		iconChooser.setDefaultIcon(SwingResourceManager.getIcon(VeracityProfileScreenView.class, "/com/moss/appsnap/keeper/installerapp/nobody.png"));
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_9.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_9.gridheight = 4;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		panel.add(iconChooser, gridBagConstraints_9);

		final JLabel firstNameLabel = new JLabel();
		firstNameLabel.setText("First Name:");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 1;
		panel.add(firstNameLabel, gridBagConstraints_5);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 2;
		panel.add(textField, gridBagConstraints);

		final JLabel middleNameLabel = new JLabel();
		middleNameLabel.setText("Middle Name:");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridy = 1;
		gridBagConstraints_6.gridx = 1;
		panel.add(middleNameLabel, gridBagConstraints_6);

		textField_1 = new JTextField();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 2;
		panel.add(textField_1, gridBagConstraints_1);

		final JLabel lastNameLabel = new JLabel();
		lastNameLabel.setText("Last Name:");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 1;
		panel.add(lastNameLabel, gridBagConstraints_7);

		textField_2 = new JTextField();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 2;
		panel.add(textField_2, gridBagConstraints_2);

		final JPanel panel_4 = new JPanel();
		panel_4.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.gridwidth = 2;
		gridBagConstraints_11.weighty = 1.0;
		gridBagConstraints_11.gridy = 3;
		gridBagConstraints_11.gridx = 1;
		panel.add(panel_4, gridBagConstraints_11);

		textPane = new JTextPane();
		textPane.setForeground(Color.RED);
		textPane.setOpaque(false);
		textPane.setEditable(false);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(10, 10, 0, 10);
		gridBagConstraints_3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.weighty = 1.0;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 0;
		panel_4.add(textPane, gridBagConstraints_3);

		final JPanel panel_2 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0,0};
		panel_2.setLayout(gridBagLayout_1);
		add(panel_2, BorderLayout.SOUTH);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(VeracityProfileScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 0;
		panel_2.add(backButton, gridBagConstraints_4);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(VeracityProfileScreenView.class, "/go-next.png"));
		nextButton.setText("Next");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.weightx = 1.0;
		gridBagConstraints_8.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_8.gridx = 2;
		panel_2.add(nextButton, gridBagConstraints_8);

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0};
		panel_3.setLayout(gridBagLayout_2);
		add(panel_3, BorderLayout.NORTH);

		final JLabel enterYourProfileLabel = new JLabel();
		enterYourProfileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		enterYourProfileLabel.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 24));
		enterYourProfileLabel.setText("Complete Your Profile");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		panel_3.add(enterYourProfileLabel, gridBagConstraints_12);

		final JTextPane pleaseProvideTheTextPane = new JTextPane();
		pleaseProvideTheTextPane.setContentType("text/html");
		pleaseProvideTheTextPane.setText("<html><body><p style=\"font-family:arial, sans-serif\">Please provide the following personal information to complete your profile.</p></body></html>");
		pleaseProvideTheTextPane.setOpaque(false);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(10, 10, 10, 10);
		gridBagConstraints_10.weightx = 1.0;
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridx = 0;
		panel_3.add(pleaseProvideTheTextPane, gridBagConstraints_10);
	}
	public IconChooser iconChooser() {
		return iconChooser;
	}
	public JTextField firstNameField() {
		return textField;
	}
	public JTextField middleNameField() {
		return textField_1;
	}
	public JTextField lastNameField() {
		return textField_2;
	}
	public JTextPane errorsField() {
		return textPane;
	}
	public JButton nextButton() {
		return nextButton;
	}
	public JButton backButton() {
		return backButton;
	}

}
