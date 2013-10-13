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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.appsnap.keeper.DesktopAppHandler;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.posix.AbstractPosixDesktopIntegrationStrategy;
import com.moss.posixfifosockets.PosixFifoSocketAddress;
import com.moss.posixfifosockets.util.PosixFifoMisc;

public class FreeDesktopIntegrationStrategy extends AbstractPosixDesktopIntegrationStrategy {
	private FreeDesktopLayoutMap fsMap;
	
	public DesktopAppHandler appHandlerFor(InstallationInfo info) {
		return new FreeDesktopAppHandler(info, fsMap, guts);
	}
	public File keeperDataDir(AppsnapServiceId serviceId) {
		fsMap = new FreeDesktopLayoutMap(serviceId);
		return fsMap.keeperDataDir;
	}
	
	@Override
	protected PosixFifoSocketAddress localApiServerSocketAddress() {
		return fsMap.fifoSockets;
	}
	
	@Override
	public void restart() {
		System.exit(0);
	}
	
	@Override
	public void installControlPanel() {
		try {
			Writer w = new FileWriter(fsMap.controlPanelScript);
			
			// BANG LINE
			w.write("#!");
			w.write(FreeDesktopAppHandler.SHELL);
			w.write('\n');
			
			{// WRITE THE FIFO SOCKETS FUNCTION
				Reader functionReader = new InputStreamReader(PosixFifoMisc.sendFifoSocketMessageBashFunction());
				char[] b = new char[1024];
				
				for(int x=functionReader.read(b); x!=-1;x=functionReader.read(b)){
					w.write(b, 0, x);
				}
			}
			
			{

				// WRITE THE POLLING CALL TO THE KEEPER
				final char EOL = '\n';
				w.append(EOL + 
						"CONTROL=" + fsMap.fifoSockets.controlPipe().getAbsolutePath() + "\n" + 
						"DIR=" + fsMap.fifoSockets.socketsDir().getAbsolutePath() + EOL + 
						"" + EOL + 
						"xmessage \"Checking for updates...\" -buttons \"\" -center -title \"Please Wait...\" &" + EOL + 
						"DIALOG=$!" + EOL + 
						"" + EOL + 
						"RESPONSE=`echo -n \"LAUNCH_CONTROL_PANEL\" | sendFifoSocketMessage $CONTROL $DIR`" + EOL + 
						"" + EOL + 
						"echo \"RESPONSE: $RESPONSE\"" + EOL + 
						"" + EOL + 
						"kill $DIALOG" + EOL + 
						"" + EOL + 
						"if [ \"$RESPONSE\" != \"OK\" ] " + EOL + 
						"then" + EOL + 
						"	xmessage -center -buttons OK -default OK  \"Polling error: $RESPONSE\"" + EOL + 
						"	exit 1;" + EOL + 
						"fi" + EOL +
						"" + EOL 
				);
			}
			w.close();
			
			FreeDesktopAppHandler.addToApplicationsMenu(guts.serviceInfo.name() + " Control Panel", "appsnap-" + guts.serviceInfo.id(), fsMap.controlPanelScript, fsMap);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
