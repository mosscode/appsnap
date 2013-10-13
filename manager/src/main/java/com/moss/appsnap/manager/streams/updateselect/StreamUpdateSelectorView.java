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
package com.moss.appsnap.manager.streams.updateselect;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

public class StreamUpdateSelectorView extends JPanel {
	private JButton cancelButton;
	private JButton selectButton;
	private JTable table_2;
	private JScrollPane scrollPane_2;
	private JTable table_1;
	private JScrollPane scrollPane_1;
	public StreamUpdateSelectorView() {
		super();
		setLayout(new BorderLayout());

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		add(splitPane);

		scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);

		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new GridBagLayout());
		splitPane.setRightComponent(panel_2);

		scrollPane_2 = new JScrollPane();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel_2.add(scrollPane_2, gridBagConstraints);

		table_2 = new JTable();
		scrollPane_2.setViewportView(table_2);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0};
		panel.setLayout(gridBagLayout);
		add(panel, BorderLayout.SOUTH);

		selectButton = new JButton();
		selectButton.setText("Select");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints_2.anchor = GridBagConstraints.EAST;
		gridBagConstraints_2.weightx = 1.0;
		panel.add(selectButton, gridBagConstraints_2);

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		panel.add(cancelButton, gridBagConstraints_1);
	}
	public JButton selectButton() {
		return selectButton;
	}
	public JButton cancelButton() {
		return cancelButton;
	}
	public JTable updatesTable() {
		return table_1;
	}
	public JTable updateVersionsTable() {
		return table_2;
	}

}
