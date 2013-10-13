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
package com.moss.appsnap.keeper.firstaid;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.swtdesigner.SwingResourceManager;

public class FirstAidStartScreenView extends JPanel {
	private JButton runFirstAidButton;
	private JButton button;
	private JLabel firstAidToolLabel;
	private JTextPane warningThisIsTextPane;
	public FirstAidStartScreenView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {0,0};
		panel.setLayout(gridBagLayout_1);
		add(panel, BorderLayout.CENTER);

		final JLabel label = new JLabel();
		label.setIcon(SwingResourceManager.getIcon(FirstAidStartScreenView.class, "/com/moss/appsnap/keeper/firstaid/first_aid_kit.png"));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		panel.add(label, gridBagConstraints);

		warningThisIsTextPane = new JTextPane();
		warningThisIsTextPane.setFont(new Font("Sans", Font.PLAIN, 12));
		warningThisIsTextPane.setContentType("text/html");
		warningThisIsTextPane.setText("<html><body style=\"font-family:arial,sans-serif\" ><p><b>WARNING:</b> This is the first aid tool.  This is a special tool that should only be used:</p> <ul><li>When you have been directed to do so by technical support personel.</li><li>When your applications won't launch at all</li></ul></body></html>");
		warningThisIsTextPane.setOpaque(false);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_3.insets = new Insets(10, 10, 0, 5);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 1;
		panel.add(warningThisIsTextPane, gridBagConstraints_3);

		firstAidToolLabel = new JLabel();
		firstAidToolLabel.setHorizontalAlignment(SwingConstants.CENTER);
		firstAidToolLabel.setFont(new Font("Sans", Font.BOLD, 24));
		firstAidToolLabel.setText("First Aid Tool");
		add(firstAidToolLabel, BorderLayout.NORTH);

		final JPanel panel_1 = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0,0};
		panel_1.setLayout(gridBagLayout);
		add(panel_1, BorderLayout.SOUTH);

		button = new JButton();
		button.setIcon(SwingResourceManager.getIcon(FirstAidStartScreenView.class, "/go-back.png"));
		button.setText("Back");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel_1.add(button, gridBagConstraints_2);

		runFirstAidButton = new JButton();
		runFirstAidButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
			}
		});
		runFirstAidButton.setIcon(SwingResourceManager.getIcon(FirstAidStartScreenView.class, "/go-next.png"));
		runFirstAidButton.setText("Run First Aid");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 2;
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.weightx = 1.0;
		panel_1.add(runFirstAidButton, gridBagConstraints_1);
	}
	public JTextPane messageField() {
		return warningThisIsTextPane;
	}
	public JLabel titleField() {
		return firstAidToolLabel;
	}
	public JButton backButton() {
		return button;
	}
	public JButton nextButton() {
		return runFirstAidButton;
	}

}
