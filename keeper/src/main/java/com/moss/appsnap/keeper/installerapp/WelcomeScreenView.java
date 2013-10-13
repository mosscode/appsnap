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
package com.moss.appsnap.keeper.installerapp;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

public class WelcomeScreenView extends JPanel {
	private JTextPane installingSoftwareFromTextPane;
	private JLabel welcomeLabel;
	private JButton beginButton;
	public WelcomeScreenView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.CENTER);

		welcomeLabel = new JLabel();
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Sans", Font.BOLD, 24));
		welcomeLabel.setText("Welcome to foobar.tld!");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(30, 0, 10, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel.add(welcomeLabel, gridBagConstraints);

		installingSoftwareFromTextPane = new JTextPane();
		installingSoftwareFromTextPane.setText("Installing software from foobar.tld is quick and easy.  Click the button below to begin the process.");
		installingSoftwareFromTextPane.setOpaque(false);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.insets = new Insets(0, 45, 30, 30);
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		panel.add(installingSoftwareFromTextPane, gridBagConstraints_2);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		add(panel_1, BorderLayout.SOUTH);

		beginButton = new JButton();
		beginButton.setIcon(SwingResourceManager.getIcon(WelcomeScreenView.class, "/go-next.png"));
		beginButton.setText("Click Here to Begin!");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		panel_1.add(beginButton, gridBagConstraints_1);
	}
	public JButton beginButton() {
		return beginButton;
	}
	public JLabel titleFIeld() {
		return welcomeLabel;
	}
	public JTextPane blurbField() {
		return installingSoftwareFromTextPane;
	}

}
