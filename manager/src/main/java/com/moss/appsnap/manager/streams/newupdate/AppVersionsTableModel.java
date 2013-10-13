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
package com.moss.appsnap.manager.streams.newupdate;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.moss.appsnap.api.apps.AppDetails;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.streams.AppVersionInfo;

public class AppVersionsTableModel implements TableModel {
	private enum Column {
		APP{
			@Override
			public Class<?> clazz() {
				return String.class;
			}
			@Override
			public Object value(AppVersionInfo oldVersion, AppVersionInfo newVersion, AppDetails app) {
				return app.name();
			}
		},
		OLD_VERSION{
			@Override
			public Class<?> clazz() {
				return String.class;
			}
			@Override
			public Object value(AppVersionInfo oldVersion, AppVersionInfo newVersion, AppDetails app) {
				return oldVersion.versionInfo()==null?"":oldVersion.versionInfo().label();
			}
		},
		NEW_VERSION{
			@Override
			public Class<?> clazz() {
				return String.class;
			}
			@Override
			public Object value(AppVersionInfo oldVersion, AppVersionInfo newVersion, AppDetails app) {
				return newVersion.versionInfo().label();
			}
		};
		
		abstract Class<?> clazz();
		abstract Object value(AppVersionInfo oldVersion, AppVersionInfo newVersion, AppDetails app);
		
	}

	private final List<AppVersionInfo> oldVersions;
	private final List<AppVersionInfo> newVersions;
	private final List<AppDetails> apps;
	
	public AppVersionsTableModel(final List<AppVersionInfo> oldVersions, final List<AppVersionInfo> versions, final List<AppDetails> apps) {
		super();
		this.oldVersions = oldVersions;
		this.newVersions = versions;
		this.apps = apps;
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
		return newVersions.size();
	}
	
	private AppDetails appDetails(AppId id){
		for(AppDetails next : apps){
			if(next.id().equals(id)){
				return next;
			}
		}
		return null;
	}
	private AppVersionInfo oldVersion(AppId id){
		for(AppVersionInfo next : oldVersions){
			if(next.appInfo().id().equals(id)){
				return next;
			}
		}
		return null;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		final AppVersionInfo newVersion = newVersions.get(rowIndex);
		final AppDetails app = appDetails(newVersion.appInfo().id());
		final AppVersionInfo oldVersion = oldVersion(newVersion.appInfo().id());
		return column(columnIndex).value(oldVersion, newVersion, app);
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
