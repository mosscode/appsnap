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
package com.moss.appsnap.manager;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

public class InstallInspectorView extends JPanel {
	public InstallInspectorView() {
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0};
		setLayout(gridBagLayout);

		final JPanel panel_6 = new JPanel();
		panel_6.setBounds(0, 0, 0, 0);
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0};
		gridBagLayout_1.columnWidths = new int[] {0,0};
		panel_6.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 0;
		add(panel_6, gridBagConstraints_7);
		

		final JLabel appTrenchworkerLabel = new JLabel();
		appTrenchworkerLabel.setText("Publication:");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(5, 0, 0, 0);
		panel_6.add(appTrenchworkerLabel, gridBagConstraints_6);

		final JLabel loangrowTrenchworkerLabel = new JLabel();
		loangrowTrenchworkerLabel.setText("LoanGrow Trenchworker (Instant Cash)");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		panel_6.add(loangrowTrenchworkerLabel, gridBagConstraints_3);

		final JLabel age3MonthsLabel = new JLabel();
		age3MonthsLabel.setText("Age:");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 0, 0, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 0;
		panel_6.add(age3MonthsLabel, gridBagConstraints_4);

		final JLabel label = new JLabel();
		label.setText("3 Months, 2 Days");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 1;
		panel_6.add(label, gridBagConstraints_5);

		final JTabbedPane tabbedPane_1 = new JTabbedPane();
		tabbedPane_1.setBounds(0, 0, 0, 0);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		add(tabbedPane_1, gridBagConstraints_1);


		final JPanel panel_4 = new JPanel();
		panel_4.setLayout(new BorderLayout());
		tabbedPane_1.addTab("Queue", null, panel_4, null);

		final JScrollPane scrollPane_4 = new JScrollPane();
		panel_4.add(scrollPane_4);

		JTable table = new JTable();
		scrollPane_4.setViewportView(table);

		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		tabbedPane_1.addTab("History", null, panel_5, null);

		final JScrollPane scrollPane_5 = new JScrollPane();
		panel_5.add(scrollPane_5);

		JTable table_1 = new JTable();
		scrollPane_5.setViewportView(table_1);
		

		final JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 0, 500, 375);
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.columnWidths = new int[] {0,0,0,0,0};
		panel_3.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		add(panel_3, gridBagConstraints_2);

		final JButton pingButton = new JButton();
		pingButton.setText("Ping");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_11.gridy = 0;
		gridBagConstraints_11.gridx = 0;
		panel_3.add(pingButton, gridBagConstraints_11);

		final JButton restartButton = new JButton();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridx = 2;
		gridBagConstraints_8.insets = new Insets(0, 5, 0, 0);
		panel_3.add(restartButton, gridBagConstraints_8);
		restartButton.setText("Restart");

		final JButton reinstallButton = new JButton();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridx = 3;
		gridBagConstraints_9.insets = new Insets(0, 5, 0, 0);
		panel_3.add(reinstallButton, gridBagConstraints_9);
		reinstallButton.setText("Reinstall");

		final JButton uninstallButton = new JButton();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridx = 4;
		gridBagConstraints_10.insets = new Insets(0, 5, 0, 5);
		panel_3.add(uninstallButton, gridBagConstraints_10);
		uninstallButton.setText("Uninstall");
	}
}
