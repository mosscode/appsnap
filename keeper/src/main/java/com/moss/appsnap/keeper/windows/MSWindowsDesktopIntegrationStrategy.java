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
package com.moss.appsnap.keeper.windows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.appsnap.keeper.AbstractDesktopIntegrationStrategy;
import com.moss.appsnap.keeper.DesktopAppHandler;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.socketapi.ApiMessageConnection;
import com.moss.appsnap.keeper.socketapi.ApiMessageHandler;
import com.moss.mswindows.shortcuts.ItemidGrabber;
import com.moss.mswindows.shortcuts.ShellLink;

public class MSWindowsDesktopIntegrationStrategy extends AbstractDesktopIntegrationStrategy {
	private static final Log log = LogFactory.getLog(MSWindowsDesktopIntegrationStrategy.class);
	
	private static final String TALKER_EXE_RESOURCE_PATH = "/com/moss/appsnap/keeper/windows/keeper-talker.exe";
	private static final String DAEMON_LAUNCHER_EXE_RESOURCE_PATH = "/com/moss/appsnap/keeper/windows/ms-windows-daemon-launcher.exe";;
	
	private final int KEEPER_LOCAL_PORT = 2323;
	

	
	private final WindowsFSLayout fsLayout = new WindowsFSLayout();
	private SnapDaemonFSLayout snapFsLayout;
	
	public DesktopAppHandler appHandlerFor(InstallationInfo info) {
		return new MSWindowsAppHandler(info, guts, fsLayout, snapFsLayout);
	}
	
	@Override
	public void installControlPanel() {
		try {
			snapFsLayout.mkdirs();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		String fileName = guts.serviceInfo.name() + " App Manager.lnk";
//		File desktopLink = new File(fsLayout.desktop, fileName);
		final File controlPanel = new File(snapFsLayout.daemonMenu, fileName);
		
		ShellLink lnk = new ShellLink();
		
		lnk.setLinkTarget(snapFsLayout.desktopExe.getAbsolutePath());
		lnk.setCommandLineArguments("\"LAUNCH_CONTROL_PANEL\"");
		lnk.setWorkingDir(snapFsLayout.desktopExe.getParentFile().getAbsolutePath());
		
		try {
			lnk.write(controlPanel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private File configDir;
	
	// c:\Documents and Settings\stu\Application Data\appsnap\daemon-data\335ebc6c-48b6-4b6f-a789-c2a0c84eb0ec
	public File keeperDataDir(AppsnapServiceId serviceId) {
		configDir = new File(fsLayout.daemonDataDir, serviceId.toString());
		
//		if(!configDir.exists()){
//			if(!configDir.mkdirs())
//				throw new RuntimeException("Could not create directory: " + configDir.getAbsolutePath());
//		}
		
		snapFsLayout = new SnapDaemonFSLayout(fsLayout, serviceId);
		
		return configDir;
	}
	
	@Override
	public void noLongerGutless(Guts guts) {
		super.noLongerGutless(guts);
		
		snapFsLayout = new SnapDaemonFSLayout(fsLayout, guts.serviceInfo);
		
		
		writeBinaries();
		
		installControlPanel();
		
	}
	
	private void writeBinaries(){
		if(snapFsLayout.desktopExe.exists() && !snapFsLayout.desktopExe.delete()){
			throw new RuntimeException("Could not delete file: " + snapFsLayout.desktopExe.getAbsolutePath());
		}
		
		try {
			writeToFile(ItemidGrabber.class.getResourceAsStream(TALKER_EXE_RESOURCE_PATH), snapFsLayout.desktopExe);
		} catch (Throwable e) {
			throw new RuntimeException("Error copying keeper talker from resources.", e);
		}

//		try {
//			writeToFile(ItemidGrabber.class.getResourceAsStream(DAEMON_LAUNCHER_EXE_RESOURCE_PATH), snapFsLayout.daemonLauncherExe);
//		} catch (Throwable e) {
//			log.warn("There was an error copying the daemon launcher exe.  This is expected if the keeper just launched.", e);
//		}

	}
	public static void writeDaemonLauncher(File dest) throws IOException {
		writeToFile(ItemidGrabber.class.getResourceAsStream(DAEMON_LAUNCHER_EXE_RESOURCE_PATH), dest);
	}
	
	public static void writeKeeperTalker(File dest) throws IOException {
		writeToFile(ItemidGrabber.class.getResourceAsStream(TALKER_EXE_RESOURCE_PATH), dest);
	}
	
	private static void writeToFile(InputStream in, File dest) throws IOException {
		byte[] b = new byte[1024*1024];
		OutputStream out = new FileOutputStream(dest);
		for(int x=in.read(b); x!=-1; x=in.read(b)){
			out.write(b, 0, x);
		}
		in.close();
		out.close();
		log.info("Copied to " + dest.getAbsolutePath());
	}
	
	@Override
	public void restart() {
		System.exit(0);
	}
	
	@Override
	public ApiMessageConnection newMessageToHost() {
		try {
			Socket s = new Socket("127.0.0.1", KEEPER_LOCAL_PORT);
			return new SocketMessageConnection(s);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private static class SocketMessageConnection implements ApiMessageConnection {
		private final Socket s;
		
		public SocketMessageConnection(Socket s) {
			super();
			this.s = s;
		}
		public void close() {
//			try {
//				s.close();
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
		}
		public InputStream in() {
			try {
				return s.getInputStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public OutputStream out() {
			try {
				return s.getOutputStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void startLocalApiServer(final ApiMessageHandler handler) {
		try {
			{
				if(!snapFsLayout.portFile.exists() && !snapFsLayout.portFile.createNewFile()){
					throw new RuntimeException("Could not create file: " + snapFsLayout.portFile.getAbsolutePath());
				}
				snapFsLayout.portFile.deleteOnExit();
				Writer w = new FileWriter(snapFsLayout.portFile);
				w.write(Integer.toString(KEEPER_LOCAL_PORT));
				w.close();
			}
			final ServerSocket server = new ServerSocket(KEEPER_LOCAL_PORT);//, 0, InetAddress.getLocalHost());
			
			new Thread("Local API Server Thread"){
				public void run() {
					while(true){
						try {
							log.info("Waiting for local connection.");
							Socket s = server.accept();
							log.info("Connection established.");
							handler.handle(new SocketMessageConnection(s));
							log.info("Done");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public boolean keeperIsRunning() {
		return snapFsLayout.portFile.exists();
	}
}
