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
package com.moss.appsnap.manager.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.moss.appsnap.api.security.SecurityMode;

public class SecurityModeWidget extends JPanel {
	private final ButtonGroup group = new ButtonGroup();
	
	private final Action openAction = new AbstractAction("Open") {
		
		public void actionPerformed(ActionEvent e) {
			selection = SecurityMode.BLACKLIST;
		}
	};
	
	private final Action closedAction = new AbstractAction("Closed") {
		
		public void actionPerformed(ActionEvent e) {
			selection = SecurityMode.WHITELIST;
		}
	};
	
	private JRadioButton openButton = register(new JRadioButton(openAction));
	private JRadioButton closedButton = register(new JRadioButton(closedAction));
	
	private SecurityMode selection;
	
	private ActionListener listener;
	
	public SecurityModeWidget() {
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.insets.right = 5;
			add(openButton, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
			add(closedButton, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 2;
			c.gridy = 0;
			c.weightx = 1;
			final JPanel spacer = new JPanel();
			add(spacer, c);
		}

	}
	
	public void setListener(ActionListener listener) {
		this.listener = listener;
		openButton.addActionListener(listener);
		closedButton.addActionListener(listener);
	}
	
	public void setSelection(SecurityMode selection) {
		switch(selection){
		case BLACKLIST:
			openButton.setSelected(true);
			break;
		case WHITELIST:
			closedButton.setSelected(true);
			break;
		default:
			throw new RuntimeException("Unknown value: " + selection);
		}
		this.selection = selection;
	}
	
	public SecurityMode getSelection() {
		return selection;
	}
	
	private JRadioButton register(JRadioButton b){
		group.add(b);
		return b;
	}
}
