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
package com.moss.appsnap.manager.apps.inspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

public class AppInspectorView extends JPanel {
	private JButton addVersionButton;
	private JLabel loangrowTrenchworkerLabel;
	private JTree tree;
	public AppInspectorView() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0};
		gridBagLayout.columnWidths = new int[] {0};
		setLayout(gridBagLayout);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0};
		panel.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		add(panel, gridBagConstraints_2);

		final JLabel nameLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		panel.add(nameLabel, gridBagConstraints_1);
		nameLabel.setText("Name:");

		loangrowTrenchworkerLabel = new JLabel();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		panel.add(loangrowTrenchworkerLabel, gridBagConstraints);
		loangrowTrenchworkerLabel.setText("LoanGrow Trenchworker");

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new TitledBorder(null, "Versions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 0;
		add(scrollPane, gridBagConstraints_4);

		tree = new JTree();
		tree.setRootVisible(false);
		scrollPane.setViewportView(tree);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 0;
		add(panel_1, gridBagConstraints_3);

		addVersionButton = new JButton();
		addVersionButton.setText("Add Version");
		panel_1.add(addVersionButton, new GridBagConstraints());
	}
	public JLabel nameLabel() {
		return loangrowTrenchworkerLabel;
	}
	public JButton addVersionButton() {
		return addVersionButton;
	}
	public JTree versionsTree() {
		return tree;
	}

}
