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
package com.moss.appsnap.keeper.socketapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.api.security.SecurityException;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.HandyTool;
import com.moss.appsnap.keeper.Poller;
import com.moss.appsnap.keeper.data.InstallationInfo;

public class InstallFunction implements SocketFunction {
	public static final String NAME = "INSTALL";
	public enum Outcome {
		OK,
		ERROR
	}
	
	public static Outcome call(InstallId id, ApiMessageConnection socket) throws IOException {
		
		Writer w = new OutputStreamWriter(socket.out());
		w.write(NAME);
		w.write(' ');
		w.write(id.toString());
		w.write('\n');
		w.flush();
//		w.close();
		
		Reader r = new InputStreamReader(socket.in());
		
		StringBuilder text = new StringBuilder();
		for(int c = r.read();c!=-1 && c!='\n';c = r.read()){
			text.append((char)c);
		}
//		char[] b = new char[1024];
//		for(int x=r.read(b);x!=-1;x=r.read(b)){
//			text.append(b, 0, x);
//		}
		r.close();
		System.out.println("Response: " + text);
		return Outcome.valueOf(text.toString());
	}
	
	private final Guts guts;
	private final Poller poller;
	private final HandyTool tool;
	
	public InstallFunction(Guts guts, Poller poller) {
		super();
		this.guts = guts;
		this.poller = poller;
		this.tool = new HandyTool(guts);
	}

	public String execute(String args) {
		try {
			InstallId id = new InstallId(args);
			
			final InstallationInfo info = guts.data.installs.get(id);
			if(info==null){
				return "No such install: " + id;
			}else{
				poller.poll();
				return Outcome.OK.name();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			return Outcome.ERROR.name();
		}
		
	}
	
	public String name() {
		return NAME;
	}
}
