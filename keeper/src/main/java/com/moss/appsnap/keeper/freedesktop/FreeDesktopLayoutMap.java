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
package com.moss.appsnap.keeper.freedesktop;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Random;

import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.posixfifosockets.PosixFifoSocket;
import com.moss.posixfifosockets.PosixFifoSocketAddress;
import com.moss.posixfifosockets.PosixFifoSocketHandler;
import com.moss.posixfifosockets.PosixFifoSocketServer;

public class FreeDesktopLayoutMap {
	
	public static void main(String[] args) throws Exception {
		/*
		 * CONTROL=
		   DIR=
		 */
		PosixFifoSocketAddress address = new PosixFifoSocketAddress(new File("/home/stu/temp/fifo-sockets"));
		
		PosixFifoSocketHandler handler = new PosixFifoSocketHandler(){
			Random random = new Random();
			public void handle(PosixFifoSocket socket) {
				try {
					Reader r = new InputStreamReader(socket.in());
					StringBuilder text = new StringBuilder();
					char[] b = new char[1024];
					for(int x = r.read(b);x!=-1;x=r.read(b)){
						text.append(b, 0, x);
					}
					r.close();
					
					System.out.println("Received \"" + text + "\" request");
					
					OutputStream out = socket.out();
					if(random.nextBoolean()){
						out.write("OK".getBytes());
					}else{
						out.write("NO WAY!".getBytes());
					}
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		PosixFifoSocketServer server = new PosixFifoSocketServer(address, handler);
		server.start();
	}
	
	public final File homeDir = new File(System.getProperty("user.home"));
	public final File gnomeConfigDir = new File(homeDir, ".config");
	public final File gnomeMenusConfigDir = new File(gnomeConfigDir, "menus");
	public final File gnomeAutostartDir = new File(gnomeConfigDir, "autostart");
	
	public final File customAppsMenuPath = new File(gnomeMenusConfigDir, "applications.menu");
	
	public final File dotLocalDir = new File(homeDir, ".local");
	public final File dotLocalShareDir = new File(dotLocalDir, "share");
	public final File dotLocalShareAppsDir = new File(dotLocalShareDir, "applications");
	
	public final File keeperDataDir;
	public final PosixFifoSocketAddress fifoSockets;
	
	public final File controlPanelScript;
	
	public FreeDesktopLayoutMap(final AppsnapServiceId serviceId) {
		keeperDataDir = new File(homeDir, ".appsnap/daemon-data/" + serviceId.toString());
		
		File fifoSocketsPath = new File(keeperDataDir, "fifo-sockets");
		fifoSockets = new PosixFifoSocketAddress(fifoSocketsPath);
		
		controlPanelScript = new File(keeperDataDir, "launch-control-panel.sh");
	}
}
