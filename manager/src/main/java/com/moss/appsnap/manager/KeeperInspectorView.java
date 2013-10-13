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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class KeeperInspectorView extends JPanel {
	public KeeperInspectorView() {
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.rowHeights = new int[] {0,0,0};
		setLayout(gridBagLayout_4);
		setVisible(true);
		setBounds(10, 10, 338, 249);

		final JPanel panel_6_1 = new JPanel();
		panel_6_1.setBorder(new TitledBorder(null, "Host Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.rowHeights = new int[] {0,0,0};
		panel_6_1.setLayout(gridBagLayout_3);
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_16.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_16.gridy = 0;
		gridBagConstraints_16.gridx = 0;
		add(panel_6_1, gridBagConstraints_16);
		
		final JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBorder(new TitledBorder(null, "Installations", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_19.weightx = 1.0;
		gridBagConstraints_19.weighty = 1.0;
		gridBagConstraints_19.fill = GridBagConstraints.BOTH;
		gridBagConstraints_19.gridy = 1;
		gridBagConstraints_19.gridx = 0;
		add(scrollPane_3, gridBagConstraints_19);

		JList list_2 = new JList();
		scrollPane_3.setViewportView(list_2);

		final JPanel panel_7 = new JPanel();
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_20.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_20.gridy = 2;
		gridBagConstraints_20.gridx = 0;
		add(panel_7, gridBagConstraints_20);
		

		final JButton button = new JButton();
		button.setText("Diagnostics");
		panel_7.add(button);
		
		

final JLabel appTrenchworkerLabel_1 = new JLabel();
		appTrenchworkerLabel_1.setText("Local IP:");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_12.insets = new Insets(5, 0, 0, 0);
		panel_6_1.add(appTrenchworkerLabel_1, gridBagConstraints_12);

		final JLabel loangrowTrenchworkerLabel_1 = new JLabel();
		loangrowTrenchworkerLabel_1.setText("10.3.3.23 [private]");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.weightx = 1.0;
		gridBagConstraints_13.gridy = 0;
		gridBagConstraints_13.gridx = 1;
		panel_6_1.add(loangrowTrenchworkerLabel_1, gridBagConstraints_13);

		final JLabel detectedIpLabel = new JLabel();
		detectedIpLabel.setText("Public IP:");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_17.gridy = 1;
		gridBagConstraints_17.gridx = 0;
		panel_6_1.add(detectedIpLabel, gridBagConstraints_17);

		final JLabel label_2 = new JLabel();
		label_2.setText("233.23.85.44 [detected]");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.gridy = 1;
		gridBagConstraints_18.gridx = 1;
		panel_6_1.add(label_2, gridBagConstraints_18);

		final JLabel age3MonthsLabel_1 = new JLabel();
		age3MonthsLabel_1.setText("Age:");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(5, 0, 0, 0);
		gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_14.gridy = 2;
		gridBagConstraints_14.gridx = 0;
		panel_6_1.add(age3MonthsLabel_1, gridBagConstraints_14);

		final JLabel label_1 = new JLabel();
		label_1.setText("3 Months, 2 Days");
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.gridy = 2;
		gridBagConstraints_15.gridx = 1;
		panel_6_1.add(label_1, gridBagConstraints_15);
	}
}
