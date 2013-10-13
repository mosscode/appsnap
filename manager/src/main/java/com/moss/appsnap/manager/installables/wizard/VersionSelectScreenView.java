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
package com.moss.appsnap.manager.installables.wizard;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

public class VersionSelectScreenView extends JPanel {
	private JTree tree;
	private JLabel label;
	private JButton nextButton;
	public VersionSelectScreenView() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0};
		setLayout(gridBagLayout);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 24));
		label.setText("New JLabel");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(label, gridBagConstraints);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		add(panel, gridBagConstraints_1);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 0;
		panel.add(scrollPane, gridBagConstraints_4);

		tree = new JTree();
		scrollPane.setViewportView(tree);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		add(panel_1, gridBagConstraints_2);

		nextButton = new JButton();
		nextButton.setIcon(SwingResourceManager.getIcon(VersionSelectScreenView.class, "/go-next.png"));
		nextButton.setText("Next");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.anchor = GridBagConstraints.EAST;
		panel_1.add(nextButton, gridBagConstraints_3);
	}
	public JButton selectButton() {
		return nextButton;
	}
	public JLabel titleLabel() {
		return label;
	}
	public JTree versionsTree() {
		return tree;
	}

}
