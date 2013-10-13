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
package com.moss.appsnap.groupmanage.publications.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import com.moss.appsnap.api.streams.AppVersionInfo;
import com.moss.appsnap.api.streams.StreamDetails;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.uitools.tree.TreeNodeHandler;
import com.moss.appsnap.uitools.tree.TreeNodeHandlerFactory;
import com.moss.appsnap.uitools.tree.TreeNodeHandlerTreeCellRenderer;
import com.moss.appsnap.uitools.tree.TreeNodeHandlerTreeModel;

public class StreamsTreeNodeFactory implements TreeNodeHandlerFactory {

	private final Object root = new Object();
	private List<StreamDetails> roots = new LinkedList<StreamDetails>();
	private List<StreamDetails> data;
	
	public TreeModel treeModel(){
		return new TreeNodeHandlerTreeModel(this, root);
	}
	
	public TreeCellRenderer treeRenderer(){
		return new TreeNodeHandlerTreeCellRenderer(this);
	}
	
	public StreamsTreeNodeFactory(List<StreamDetails> data) {
		super();
		this.data = new ArrayList<StreamDetails>(data);
		for(StreamDetails next : data){
			if(next.parent()==null || find(next.parent(), data)==null){
				roots.add(next);
			}
		}
	}
	
	private static StreamDetails find(StreamId id, List<StreamDetails> options){
		for(StreamDetails next : options){
			if(next.id().equals(id)){
				return next;
			}
		}
		return null;
	}
	private StreamDetails find(StreamId id){
		return find(id, data);
	}
	
	private List<StreamDetails> childrenOf(StreamId parent){
		final List<StreamDetails> children = new LinkedList<StreamDetails>();
		for(StreamDetails next : data){
			if(next.parent()!=null && next.parent().equals(parent)){
				children.add(next);
			}
		}
		return children;
	}
	
	public TreeNodeHandler<?> handler(Object value) {
		if(value == root){
			return new RootHandler(roots);
		} else if(value instanceof StreamDetails){
			StreamDetails info = ((StreamDetails)value);
			return new StreamHandler(info, childrenOf(info.id()));
		} else if(value instanceof AppVersionInfo){
			return new AppHandler((AppVersionInfo)value);
		} else {
			return null;
		}
	}
	
	
}
