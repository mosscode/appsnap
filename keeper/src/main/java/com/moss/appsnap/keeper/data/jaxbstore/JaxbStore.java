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
package com.moss.appsnap.keeper.data.jaxbstore;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.jaxbhelper.JAXBHelper;

public abstract class JaxbStore<Id, T> {
	private final Log log = LogFactory.getLog(getClass());
	protected final File dataPath;
	private final JAXBHelper helper;
	
	public JaxbStore(File dataPath, JAXBHelper helper) throws IOException {
		super();
		this.dataPath = dataPath;
		this.helper = helper;
		
		preparePath();
	}
	
	protected void preparePath() throws IOException {
		if(!dataPath.exists() && !dataPath.mkdirs()){
			throw new IOException("Could not create directory: " + dataPath.getAbsolutePath());
		}
	}
	protected abstract String fileName(Id id);
	
	protected File pathForId(Id id){
		return new File(dataPath, fileName(id) + ".xml");
	}
	
	public T get(Id id){
		final File path = pathForId(id);
		if(log.isDebugEnabled()){
			log.debug("Getting from " + path.getAbsolutePath());
		}
		if(!path.exists()){
			return null;
		}else{
			try {
				return (T) helper.readFromFile(path);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void delete(Id id){
		final File path = pathForId(id);
		if(log.isDebugEnabled()){
			log.debug("Getting from " + path.getAbsolutePath());
		}
		if(!path.exists()){
			throw new RuntimeException("No such item: " + id);
		}else{
			if(!path.delete()){
				throw new RuntimeException("Could not delete file " + path.getAbsolutePath());
			}
		}
	}
	
	public void scan(ValueScanner<T> scanner){
		File[] files = dataPath.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.exists() && pathname.getName().endsWith(".xml");
			}
		});
		
		for(File next : files){
			try {
				final boolean keepScanning = scanner.scan((T) helper.readFromFile(next));
				if(!keepScanning){
					return;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void put(Id id, T spec){
		final File path = pathForId(id);
		try {
			helper.writeToFile(helper.writeToXmlString(spec), path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
