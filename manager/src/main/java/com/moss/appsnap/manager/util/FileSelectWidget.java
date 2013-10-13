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

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class FileSelectWidget extends JPanel {
	
	public interface FileSelectionHandler {
		boolean acceptQuick(File path);
		
		/**
		 * Returns null if the path is acceptable.
		 * Otherwise, it returns a string explaining 
		 * the unsuitability of the path.
		 */
		String acceptDeep(File path);
		
		void selectionHappened(File path);
	}
	
	
	private static final class DefaultHandler implements FileSelectionHandler {
		public String acceptDeep(File path) {
			if(path.exists()){
				return null;
			}else{
				return "Nonexistent path";
			}
		}
		public boolean acceptQuick(File path) {
			return acceptDeep(path)==null;
		}
		
		public void selectionHappened(File path) {
			System.out.println("IGNORED: Selected " + path.getAbsolutePath());
		}
	}
	private static final String NO_SELECTION_STRING = "<no-selection>";
	
	private final JLabel label = new JLabel(NO_SELECTION_STRING);
	private final JButton button = new JButton("...");
	private final JFileChooser fileChooser = new JFileChooser();
	private FileSelectionHandler handler;
	private String fileTypeDescription = "acceptable files";
	private File selection;

	public FileSelectWidget() {
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=0;
			c.gridy=0;
			c.fill=BOTH;
			c.weightx=1;
			c.weighty=1;
			c.insets = new Insets(0, 5, 0, 0);
			add(label, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=1;
			c.gridy=0;
			c.insets = new Insets(0, 0, 0, 0);
			c.fill=NONE;
			c.weightx=0;
			c.weighty=0;
			add(button, c);
		}
		
		
		handler = new DefaultHandler();
		
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return fileTypeDescription;
			}
			
			@Override
			public boolean accept(File f) {
				return handler.acceptQuick(f);
			}
		});
		
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(FileSelectWidget.this);
				if(result==JFileChooser.APPROVE_OPTION){
					final File selection = fileChooser.getSelectedFile();
					final String rejectReason = handler.acceptDeep(selection);
					if(rejectReason==null){
						setSelection(selection);
					}else{
						JOptionPane.showMessageDialog(FileSelectWidget.this, "Not a valid selection: " + rejectReason);
					}
				}
			}
		});
	}
	
	public void setHandler(FileSelectionHandler handler) {
		this.handler = handler;
	}
	
	public File getSelection() {
		return selection;
	}
	
	public void setSelection(File selection) {
		if(selection!=null){
			label.setText(selection.getAbsolutePath());
		}else{
			label.setText(NO_SELECTION_STRING);
		}
		this.selection = selection;
		if(handler!=null){
			handler.selectionHappened(selection);
		}
	}
	
	// bean properties
	public String getFileTypeDescription() {
		return fileTypeDescription;
	}
	
	public void setFileTypeDescription(String fileTypeDescription) {
		this.fileTypeDescription = fileTypeDescription;
	}
	
}
