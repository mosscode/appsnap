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
package com.moss.appsnap.keeper.controlpanel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ControlPanelView extends JPanel {
	private JButton button_1;
	private JButton button;
	private JList list;
	public ControlPanelView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0};
		gridBagLayout.columnWidths = new int[] {0,0};
		panel_1.setLayout(gridBagLayout);
		add(panel_1, BorderLayout.CENTER);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel_1.add(scrollPane, gridBagConstraints);

		list = new JList();
		scrollPane.setViewportView(list);

		final JPanel panel_2 = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0};
		panel_2.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		panel_1.add(panel_2, gridBagConstraints_1);

		button = new JButton();
		button.setText("Install");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		panel_2.add(button, gridBagConstraints_2);

		button_1 = new JButton();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel_2.add(button_1, gridBagConstraints_3);
		button_1.setText("Uninstall");

		final JPanel panel_3 = new JPanel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 0;
		panel_2.add(panel_3, gridBagConstraints_4);
	}
	public JList installsList() {
		return list;
	}
	public JButton installButton() {
		return button;
	}
	public JButton uninstallButton() {
		return button_1;
	}

}
