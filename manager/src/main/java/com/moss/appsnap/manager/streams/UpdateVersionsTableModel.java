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
package com.moss.appsnap.manager.streams;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamUpdate;

public class UpdateVersionsTableModel implements TableModel {
	private enum Column {
		APP{
			@Override
			public Class<?> clazz() {
				return String.class;
			}
			@Override
			public Object value(StreamDetails details, AppVersionInfo update) {
				return details.app(update.appInfo().id()).name();
			}
		},
		VERSION{
			@Override
			public Class<?> clazz() {
				return String.class;
			}
			@Override
			public Object value(StreamDetails details, AppVersionInfo update) {
				return update.versionInfo().label();
			}
		};
		
		abstract Class<?> clazz();
		abstract Object value(StreamDetails details, AppVersionInfo update);
		
	}
	
	private final StreamUpdate update;
	private final StreamDetails details;
	
	public UpdateVersionsTableModel(StreamUpdate update, StreamDetails details) {
		super();
		this.update = update;
		this.details = details;
	}
	
	private Column column(int index){
		return Column.values()[index];
	}
	public Class<?> getColumnClass(int columnIndex) {
		return column(columnIndex).clazz();
	}
	
	public int getColumnCount() {
		return Column.values().length;
	}
	public String getColumnName(int columnIndex) {
		return column(columnIndex).name();
	}
	public int getRowCount() {
		return update.versions().size();
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		return column(columnIndex).value(details, update.versions().get(rowIndex));
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	public void addTableModelListener(TableModelListener l) {
	}
	public void removeTableModelListener(TableModelListener l) {
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}
