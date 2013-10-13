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
package com.moss.appsnap.keeper.storewidget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;

@SuppressWarnings("serial")
public class AppBrowserView extends JPanel {
	private JPanel panel_1;
	private JLabel superMarioBrothersLabel;
	private JTextPane theOriginalArcadeTextPane;
	private JTree tree;
	public AppBrowserView() {
		super();
		setLayout(new BorderLayout());

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		add(splitPane, BorderLayout.CENTER);

		final JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		tree = new JTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		scrollPane.setViewportView(tree);

		panel_1 = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0};
		panel_1.setLayout(gridBagLayout);
		splitPane.setRightComponent(panel_1);

		superMarioBrothersLabel = new JLabel();
		superMarioBrothersLabel.setFont(new Font("Sans", Font.BOLD | Font.ITALIC, 18));
		superMarioBrothersLabel.setText("Super Mario Brothers");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		panel_1.add(superMarioBrothersLabel, gridBagConstraints_1);

		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(10, 10, 10, 10);
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		panel_1.add(scrollPane_1, gridBagConstraints_2);

		theOriginalArcadeTextPane = new JTextPane();
		theOriginalArcadeTextPane.setText("The original arcade classic!  This recreation features all the original characters in all their glory.  Choose from Mario, Luigi, Yoshi, or the Princess.  Beat bowser, and save the land!");
		scrollPane_1.setViewportView(theOriginalArcadeTextPane);
	}
	public JTree catalogTree() {
		return tree;
	}
	public JPanel inspectorHolder() {
		return panel_1;
	}

}
