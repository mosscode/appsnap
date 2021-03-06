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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

@SuppressWarnings("serial")
public class CreateVeracityAccountScreenView extends JPanel {
	private JTextPane textPane;
	private JButton nextButton;
	private JButton backButton;
	private JPasswordField passwordField;
	private JTextField textField;
	
	public CreateVeracityAccountScreenView(final String atAndVeracityHostname) {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0};
		gridBagLayout.columnWidths = new int[] {0,0,0};
		panel.setLayout(gridBagLayout);
		add(panel);

		final JLabel idLabel = new JLabel();
		idLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		idLabel.setText("ID: ");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(10, 5, 0, 5);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel.add(idLabel, gridBagConstraints_2);

		textField = new JTextField();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 0, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		panel.add(textField, gridBagConstraints);

		final JLabel atDomainLabel = new JLabel();
		atDomainLabel.setText(atAndVeracityHostname);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(10, 0, 0, 5);
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 2;
		panel.add(atDomainLabel, gridBagConstraints_1);

		final JLabel passwordLabel = new JLabel();
		passwordLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		passwordLabel.setText("Password:");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(10, 5, 10, 5);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel.add(passwordLabel, gridBagConstraints_3);

		passwordField = new JPasswordField();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(10, 0, 10, 5);
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		panel.add(passwordField, gridBagConstraints_4);

		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.fill = GridBagConstraints.BOTH;
		gridBagConstraints_8.gridwidth = 3;
		gridBagConstraints_8.weighty = 1.0;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 0;
		panel.add(panel_3, gridBagConstraints_8);

		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setForeground(Color.RED);
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(10, 10, 0, 10);
		gridBagConstraints_9.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_9.weighty = 1.0;
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.weightx = 1.0;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		panel_3.add(textPane, gridBagConstraints_9);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0,0};
		panel_1.setLayout(gridBagLayout_1);
		add(panel_1, BorderLayout.SOUTH);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(CreateVeracityAccountScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 0;
		panel_1.add(backButton, gridBagConstraints_5);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(CreateVeracityAccountScreenView.class, "/go-next.png"));
		nextButton.setText("Next");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_6.anchor = GridBagConstraints.EAST;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.gridx = 2;
		panel_1.add(nextButton, gridBagConstraints_6);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new GridBagLayout());
		add(panel_2, BorderLayout.NORTH);

		final JLabel whatUsernameAndLabel = new JLabel();
		whatUsernameAndLabel.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 24));
		whatUsernameAndLabel.setText("Choose an ID and Password");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 10, 5, 5);
		panel_2.add(whatUsernameAndLabel, gridBagConstraints_7);
	}
	public JButton backButton() {
		return backButton;
	}
	public JButton nextButton() {
		return nextButton;
	}
	public JTextField userField() {
		return textField;
	}
	public JPasswordField passwordField() {
		return passwordField;
	}
	public JTextPane errorsField() {
		return textPane;
	}

}
