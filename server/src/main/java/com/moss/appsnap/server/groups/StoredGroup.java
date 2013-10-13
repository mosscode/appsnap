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
package com.moss.appsnap.server.groups;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.moss.appsnap.api.catalog.GroupCatalog;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.api.groups.AppPublicationInfo;
import com.moss.appsnap.api.groups.GroupDetails;
import com.moss.appsnap.api.groups.GroupId;
import com.moss.appsnap.api.groups.GroupInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.security.SecurityMode;
import com.moss.appsnap.server.Data;
import com.moss.identity.Id;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StoredGroup {
	private GroupId id;
	private String name;
	private SecurityMode securityMode;
	private List<Id> membership = new LinkedList<Id>();
	private List<Id> administrators = new LinkedList<Id>();
	private List<Publication> publications = new LinkedList<Publication>();
	
	@Deprecated StoredGroup() {}
	
	public StoredGroup(GroupId id, String name, SecurityMode membershipType) {
		super();
		this.id = id;
		this.name = name;
		this.securityMode = membershipType;
	}
	
	public String name() {
		return name;
	}
	
	public GroupCatalog toCatalogDto(){
		final List<PublicationInfo> pubs = new ArrayList<PublicationInfo>(publications.size());
		for(Publication next : publications){
			pubs.add(next.toInfoDto());
		}
		return new GroupCatalog(id, name, pubs);
	}
	
	public boolean isAdministrator(Id id){
		for(final Id next : administrators){
			if(next.equals(id)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isMember(Id id){
		boolean isInList = false;
		for(final Id next : membership){
			if(next.equals(id)){
				isInList = true;
				break;
			}
		}
		
		return securityMode.allow(isInList);
	}
	public Publication publication(PublicationId id){
		for(Publication next : publications){
			if(next.id().equals(id)){
				return next;
			}
		}
		return null;
	}
	
	public void publish(Publication publication){
		this.publications.add(publication);
	}
	
//	public StoredStream streamForApp(final AppId appId, final DbWrap<StreamId, StoredStream> streams){
//		StoredStream stream = null;
//		for(Publication p : publications()){
//			final StoredStream next = streams.get(p.stream(), null, LockMode.READ_COMMITTED);
//			if(next==null){
//				throw new RuntimeException("Relational integrity error: no such stream: " + p.stream());
//			}else{
//				if(next.servesApp(appId)){
//					stream = next;
//				}
//			}
//		}
//		
//		return stream;
//	}
	
	public GroupId id() {
		return id;
	}
	
	public void setSecurityMode(SecurityMode securityMode) {
		this.securityMode = securityMode;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addAdministrator(Id administrator){
		this.administrators.add(administrator);
	}
	public void addMembers(List<Id> members){
		this.membership.addAll(members);
	}
	public void removeMembers(List<Id> members){
		List<Id> matches = new LinkedList<Id>();
		
		for(Id next : members){
			for(Id e : this.membership){
				if(e.equals(next)){
					matches.add(e);
				}
			}
		}
		this.membership.removeAll(matches);
	}
	
	public GroupInfo toDto(){
		return new GroupInfo(id, name);
	}
	
	public GroupDetails detailsDto(Data data){
		final List<AppPublicationInfo> publications = new LinkedList<AppPublicationInfo>();
		
		for(final Publication next : this.publications){
			publications.add(next.toDto(data));
		}
		return new GroupDetails(id, name, publications, securityMode, membership, administrators);
	}
	
	public List<Publication> publications() {
		return publications;
	}
}
