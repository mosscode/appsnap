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
package com.moss.appsnap.server.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.moss.appkeep.api.mirror.PeerId;
import com.moss.appkeep.api.mirror.PeerInfo;
import com.moss.identity.Id;
import com.moss.identity.simple.SimpleId;
import com.moss.identity.standard.PasswordIdProofCheckRecipe;

@XmlAccessorType(XmlAccessType.FIELD)
public class PeerRegistration {
	
	private PeerId id;
	private String url;

	@XmlElement(name="network-logon")
	private SimpleId authenticationId;

	@XmlElement(name="network-logon-proof-check-recipie")
	private PasswordIdProofCheckRecipe authenticationRecipie;
	
	public PeerInfo toDto(){
		return new PeerInfo(id, url);
	}
	
	public PeerId id() {
		return id;
	}
	
	public String url() {
		return url;
	}
	
	public Id authenticationId() {
		return authenticationId;
	}
	
	public PasswordIdProofCheckRecipe authenticationRecipie() {
		return authenticationRecipie;
	}
}
