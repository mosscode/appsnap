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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

public class LoginScreenView extends JPanel {
	private JButton backButton;
	private JPanel panel;
	private JButton nextButton;
	public LoginScreenView() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0,0,0};
		setLayout(gridBagLayout);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.columnWidths = new int[] {0,0};
		gridBagLayout_3.rowHeights = new int[] {0};
		panel_1.setLayout(gridBagLayout_3);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.BOTH;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 0;
		add(panel_1, gridBagConstraints_6);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(LoginScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel_1.add(backButton, gridBagConstraints);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(LoginScreenView.class, "/go-next.png"));
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.weightx = 1.0;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 1;
		gridBagConstraints_9.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		panel_1.add(nextButton, gridBagConstraints_9);
		nextButton.setText("Next");

		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new BorderLayout());
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.gridy = 0;
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		add(panel_3, gridBagConstraints_10);

		final JLabel loginOrSignupLabel = new JLabel();
		loginOrSignupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loginOrSignupLabel.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 24));
		loginOrSignupLabel.setText("Login");
		panel_3.add(loginOrSignupLabel);

		final JPanel panel_2 = new JPanel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 1;
		add(panel_2, gridBagConstraints_4);
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.columnWidths = new int[] {0,0};
		gridBagLayout_2.rowHeights = new int[] {0};
		panel_2.setLayout(gridBagLayout_2);

		panel = new JPanel();
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.columnWidths = new int[] {};
		panel.setLayout(gridBagLayout_4);
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.BOTH;
		gridBagConstraints_14.gridy = 0;
		gridBagConstraints_14.gridx = 1;
		panel_2.add(panel, gridBagConstraints_14);
	}
	public JButton nextButton() {
		return nextButton;
	}
	public JPanel holderPanel() {
		return panel;
	}
	public JButton backButton() {
		return backButton;
	}

}
