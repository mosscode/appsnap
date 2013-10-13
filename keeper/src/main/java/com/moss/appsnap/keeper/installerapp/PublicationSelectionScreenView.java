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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.swtdesigner.SwingResourceManager;

public class PublicationSelectionScreenView extends JPanel {
	private JButton backButton;
	private JButton nextButton;
	private JPanel panel_1;
	public PublicationSelectionScreenView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		add(panel, BorderLayout.SOUTH);

		backButton = new JButton();
		backButton.setIcon(SwingResourceManager.getIcon(PublicationSelectionScreenView.class, "/go-back.png"));
		backButton.setText("Back");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		panel.add(backButton, gridBagConstraints);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(PublicationSelectionScreenView.class, "/go-next.png"));
		nextButton.setText("Next");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		panel.add(nextButton, gridBagConstraints_1);

		panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		add(panel_1, BorderLayout.CENTER);
	}
	public JPanel holderPanel() {
		return panel_1;
	}
	public JButton nextButton() {
		return nextButton;
	}
	public JButton backButton() {
		return backButton;
	}

}
