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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

public class StreamInspectorView extends JPanel {
	private JList list_1;
	private JLabel securityModeLabel;
	private JButton removeButton;
	private JButton addButton_1;
	private JButton changeSecurityModeButton;
	private JTable table_2;
	private JButton addButton;
	private JButton pullUpdateButton;
	private JButton newUpdateButton;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane;
	class Table_1TableModel extends AbstractTableModel {
		private final String[] COLUMNS = new String[] {
			"App", "Version"
		};
		private final String[][] CELLS = new String[][] {
			{"LoanGrow Trenchworker", "0.34.0"},
			{"LoanGrow Mechanic", "0.34.0"},
			{"LoanGrow Overseer", "0.34.0"}
		};
		public int getRowCount() {
			return CELLS.length;
		}
		public int getColumnCount() {
			return COLUMNS.length;
		}
		public String getColumnName(int column) {
			return COLUMNS[column];
		}
		public Object getValueAt(int row, int column) {
			return CELLS[row].length > column ? CELLS[row][column] : (column + " - " + row);
		}
	}

	private JTable table_1;
	class TableTableModel extends AbstractTableModel {
		private final String[] COLUMNS = new String[] {
			"Version", "Date"
		};
		private final String[][] CELLS = new String[][] {
				{"2010.8", "2010-02-14 14:00 PST"},
				{"2010.1", "2010-01-3 12:00 PST"}
		};
		public int getRowCount() {
			return CELLS.length;
		}
		public int getColumnCount() {
			return COLUMNS.length;
		}
		public String getColumnName(int column) {
			return COLUMNS[column];
		}
		public Object getValueAt(int row, int column) {
			return CELLS[row].length > column ? CELLS[row][column] : (column + " - " + row);
		}
	}

	private JTable table;
	public StreamInspectorView() {
		super();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,0};
		setLayout(gridBagLayout_1);

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {};
		panel.setLayout(gridBagLayout);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(panel, gridBagConstraints);

		final JTabbedPane tabbedPane = new JTabbedPane();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridy = 0;
		add(tabbedPane, gridBagConstraints_2);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		tabbedPane.addTab("Apps", null, panel_1, null);

		final JScrollPane scrollPane_2 = new JScrollPane();
		panel_1.add(scrollPane_2, BorderLayout.CENTER);

		table_2 = new JTable();
		scrollPane_2.setViewportView(table_2);

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,0};
		panel_3.setLayout(gridBagLayout_2);
		panel_1.add(panel_3, BorderLayout.EAST);

		addButton = new JButton();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(5, 5, 0, 5);
		panel_3.add(addButton, gridBagConstraints_1);
		addButton.setText("Add...");

		final JPanel panel_4 = new JPanel();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.weighty = 1.0;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel_3.add(panel_4, gridBagConstraints_3);

		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		tabbedPane.addTab("Updates", null, panel_5, null);

		final JSplitPane splitPane = new JSplitPane();
		panel_5.add(splitPane);
		splitPane.setDividerLocation(200);

		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		table = new JTable();
		table.setModel(new TableTableModel());
		scrollPane.setViewportView(table);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new GridBagLayout());
		splitPane.setRightComponent(panel_2);

		scrollPane_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.weighty = 1.0;
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 0;
		panel_2.add(scrollPane_1, gridBagConstraints_5);

		table_1 = new JTable();
		table_1.setModel(new Table_1TableModel());
		scrollPane_1.setViewportView(table_1);

		final JToolBar toolBar = new JToolBar();
		panel_5.add(toolBar, BorderLayout.NORTH);

		pullUpdateButton = new JButton();
		toolBar.add(pullUpdateButton);
		pullUpdateButton.setText("Pull Update...");

		newUpdateButton = new JButton();
		toolBar.add(newUpdateButton);
		newUpdateButton.setText("New Update...");

		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new BorderLayout());
		tabbedPane.addTab("Security", null, panel_6, null);

		final JPanel panel_4_1 = new JPanel();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.columnWidths = new int[] {0};
		gridBagLayout_3.rowHeights = new int[] {0,0};
		panel_4_1.setLayout(gridBagLayout_3);
		panel_6.add(panel_4_1);

		final JPanel panel_9 = new JPanel();
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.weightx = 1.0;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 0;
		panel_4_1.add(panel_9, gridBagConstraints_11);
		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.rowHeights = new int[] {0};
		panel_9.setLayout(gridBagLayout_4);

		securityModeLabel = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(5, 10, 5, 0);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		panel_9.add(securityModeLabel, gridBagConstraints_8);
		securityModeLabel.setText("Security Mode: ");

		changeSecurityModeButton = new JButton();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.weightx = 1.0;
		panel_9.add(changeSecurityModeButton, gridBagConstraints_9);
		changeSecurityModeButton.setText("Change...");

		final JPanel panel_8 = new JPanel();
		panel_8.setBorder(new TitledBorder(null, "Groups", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panel_8.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.weighty = 1.0;
		gridBagConstraints_14.fill = GridBagConstraints.BOTH;
		gridBagConstraints_14.gridy = 1;
		gridBagConstraints_14.gridx = 0;
		panel_4_1.add(panel_8, gridBagConstraints_14);

		final JScrollPane scrollPane_2_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_12.weighty = 1.0;
		gridBagConstraints_12.fill = GridBagConstraints.BOTH;
		gridBagConstraints_12.weightx = 1.0;
		panel_8.add(scrollPane_2_1, gridBagConstraints_12);

		list_1 = new JList();
		scrollPane_2_1.setViewportView(list_1);

		final JPanel panel_6_1 = new JPanel();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.fill = GridBagConstraints.VERTICAL;
		panel_8.add(panel_6_1, gridBagConstraints_13);
		panel_6_1.setLayout(new GridBagLayout());

		addButton_1 = new JButton();
		addButton_1.setText("Add");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		panel_6_1.add(addButton_1, gridBagConstraints_4);

		removeButton = new JButton();
		removeButton.setText("Remove");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridy = 1;
		gridBagConstraints_6.gridx = 0;
		panel_6_1.add(removeButton, gridBagConstraints_6);

		final JPanel panel_7 = new JPanel();
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.weighty = 1.0;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		panel_6_1.add(panel_7, gridBagConstraints_7);
	}
	public JTable updatesTable() {
		return table;
	}
	public JTable updateVersionsTable() {
		return table_1;
	}
	public JButton newUpdateButton() {
		return newUpdateButton;
	}
	public JButton pullUpdateButton() {
		return pullUpdateButton;
	}
	public JButton addAppButton() {
		return addButton;
	}
	public JTable appsTable() {
		return table_2;
	}
	public JButton changeSecurityModeButton() {
		return changeSecurityModeButton;
	}
	public JButton addGroupButton() {
		return addButton_1;
	}
	public JButton removeGroupButton() {
		return removeButton;
	}
	public JList groupsList() {
		return list_1;
	}
	public JLabel securityModeLabel() {
		return securityModeLabel;
	}
}
