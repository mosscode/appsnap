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
package com.moss.appsnap.manager.streams.inspector;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class StreamSecurityWidgetView extends JPanel {
	private JLabel securityModeLabel;
	private JButton removeButton;
	private JButton addButton;
	private JList list_1;
	public StreamSecurityWidgetView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel_4 = new JPanel();
		panel_4.setLayout(new BorderLayout());
		add(panel_4);

		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new GridBagLayout());
		panel_4.add(panel_6, BorderLayout.EAST);

		addButton = new JButton();
		addButton.setText("Add");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		panel_6.add(addButton, gridBagConstraints);

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		panel_6.add(removeButton, gridBagConstraints_1);

		final JPanel panel_7 = new JPanel();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		panel_6.add(panel_7, gridBagConstraints_2);

		final JPanel panel_8 = new JPanel();
		panel_8.setLayout(new GridBagLayout());
		panel_4.add(panel_8, BorderLayout.NORTH);

		securityModeLabel = new JLabel();
		securityModeLabel.setText("Security Mode: ");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.insets = new Insets(5, 10, 5, 5);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 0;
		panel_8.add(securityModeLabel, gridBagConstraints_3);

		final JScrollPane scrollPane_2 = new JScrollPane();
		panel_4.add(scrollPane_2, BorderLayout.CENTER);

		list_1 = new JList();
		scrollPane_2.setViewportView(list_1);
	}
	public JList groupsList() {
		return list_1;
	}
	public JButton addButton() {
		return addButton;
	}
	public JButton removeButton() {
		return removeButton;
	}
	public JLabel securityModeLabel() {
		return securityModeLabel;
	}

}
