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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appkeep.api.ComponentId;
import com.moss.appsnap.api.installs.InstallId;
import com.moss.appsnap.keeper.DesktopAppHandler;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.ResolvedJavaLaunchSpec;
import com.moss.launch.components.Component;
import com.moss.launch.spec.app.launch.AppLaunchRecipie;
import com.moss.launch.spec.app.launch.Argument;
import com.moss.launch.tools.LaunchBuilder;
import com.moss.launch.tools.simplelauncher.ComponentResolver;
import com.moss.posixfifosockets.PosixFifoSocketAddress;
import com.moss.posixfifosockets.util.PosixFifoMisc;

public class FreeDesktopAppHandler extends DesktopAppHandler {
	protected static final String SHELL = "/bin/bash";
	
	private final Log log = LogFactory.getLog(getClass());
	
	private final FreeDesktopLayoutMap fsMap;
	
	public FreeDesktopAppHandler(InstallationInfo info, FreeDesktopLayoutMap fsMap, Guts guts) {
		super(info, guts);
		this.fsMap = fsMap;
	}

	@Override
	public void handleInstall(boolean daemon, final ResolvedJavaLaunchSpec launch) {

		try {
			final String pathToJava = "/usr/bin/java";
			final AppLaunchRecipie r = launch.spec();
			
			LaunchBuilder b = new LaunchBuilder(':');
			b.setMainClassName(r.mainClass().value());
			b.setJavaCommand(pathToJava);
			b.setClassPath(r.components(), new ComponentResolver() {
				public File locate(Component c) {
					final ComponentId r = launch.resolutionFor(c);
					return guts.data.componentsCache.getLocal(r, c);
				}
			});
			
			for(Argument arg : r.arguments()){
				b.addArgument(arg);
				log.info("Using arg " + arg);
			}

			final StringBuilder line = new StringBuilder();
			for(String next : b.parts()){
				line.append(next);
				line.append(' ');
			}

			final File scriptLocation = launchFile();

			writeLaunchScript(daemon, line.toString(), scriptLocation, fsMap.fifoSockets);

			if(daemon){
				log.info("Writing " + info.name() + "(" + info.id() + ") to gnome session");
				addToGnomeSession(scriptLocation);
			}else{
				log.info("Writing " + info.name() + "(" + info.id() + " to applications menu");
				addToApplicationsMenu(info.name(), info.id(), scriptLocation, fsMap);
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void launch() {
		try {
			Process p = Runtime.getRuntime().exec(new String[]{
					SHELL,
					launchFile().getAbsolutePath()
			});
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeDesktopFile(String name, File location, File target) throws IOException {
		FileWriter writer = new FileWriter(location);
		writer.append("[Desktop Entry]\n");
		writer.append("Type=Application\n");
		writer.append("Name=");
		writer.append(name);
		writer.append("\n");
		writer.append("Exec=");
		writer.append(SHELL);
		writer.append(" ");
		writer.append(target.getAbsolutePath());
		writer.append("\n");
		writer.flush();
		writer.close();
	}
	
	private static StringBuilder read(Reader reader) throws IOException {
		StringBuilder appsMenu = new StringBuilder();
		final char[] buf = new char[1024];
		for(int x=reader.read(buf);x!=-1;x=reader.read(buf)){
			appsMenu.append(buf, 0, x);
		}
		reader.close();
		
		return appsMenu;
	}
	
	private static void write(String text, File dest) throws IOException {
		Writer writer = new FileWriter(dest);
		writer.write(text);
		writer.close();
	}
//	info.name()
	
	private static void removeFromApplicationsMenu(InstallId id, File scriptLocation, FreeDesktopLayoutMap fsMap) throws IOException {
		removeFromApplicationsMenu("appsnap-install-" + id, scriptLocation, fsMap);
	}
	
	public static void removeFromApplicationsMenu(String fileName, File scriptLocation, FreeDesktopLayoutMap fsMap) throws IOException {
		final Log log = LogFactory.getLog(FreeDesktopAppHandler.class);
		
		String appsMenu;
		if(fsMap.customAppsMenuPath.exists()){
			appsMenu = read(new FileReader(fsMap.customAppsMenuPath)).toString();
		}else{
			appsMenu = read(new InputStreamReader(FreeDesktopAppHandler.class.getResourceAsStream("applications.menu"))).toString();
		}
		final File desktopFile = new File(fsMap.dotLocalShareAppsDir, fileName + ".desktop");

		final String entry = applicationsFileEntry(desktopFile);
		final String pattern = Pattern.quote(entry);
		
		log.info("Searching for \n" + entry + "\n\n using \n" + pattern);
		appsMenu = appsMenu.replaceAll(pattern, "");
		
		write(appsMenu, fsMap.customAppsMenuPath);
	}
	
	
	private static String applicationsFileEntry(File desktopFile){
		return 
			"\n<Include>\n" + 
			"    <Filename>" + desktopFile.getName() + "</Filename>\n" + 
			"</Include>\n";
	}
	
	private static void addToApplicationsMenu(String name, InstallId id, File scriptLocation, FreeDesktopLayoutMap fsMap) throws IOException {
		addToApplicationsMenu(name,  "appsnap-install-" + id, scriptLocation, fsMap);
	}
	public static void addToApplicationsMenu(String name, String fileName, File scriptLocation, FreeDesktopLayoutMap fsMap) throws IOException {
		
		String appsMenu;
		if(fsMap.customAppsMenuPath.exists()){
			appsMenu = read(new FileReader(fsMap.customAppsMenuPath)).toString();
		}else{
			appsMenu = read(new InputStreamReader(FreeDesktopAppHandler.class.getResourceAsStream("applications.menu"))).toString();
		}
		
		
		
		final File desktopFile = new File(fsMap.dotLocalShareAppsDir, fileName + ".desktop");
		writeDesktopFile(name, desktopFile, scriptLocation);
		
		final String entry = applicationsFileEntry(desktopFile);
		
		appsMenu = appsMenu.replace("</Menu>", entry + "</Menu>");
		
		write(appsMenu, fsMap.customAppsMenuPath);
		
	}
	
	private void addToGnomeSession(File scriptLocation) throws IOException {
		
		if(!fsMap.gnomeAutostartDir.exists() && !fsMap.gnomeAutostartDir.mkdirs()){
			throw new IOException("Could not create directory: " + fsMap.gnomeAutostartDir.getAbsolutePath());
		}
		
		final File location = new File(fsMap.gnomeAutostartDir, "appsnap-launch-" + info.id() + ".desktop");
		writeDesktopFile(info.name(), location, scriptLocation);

	}

	public static void writeLaunchScript(boolean isKeeper, String launchCommand, File scriptLocation, final PosixFifoSocketAddress fifoSocketServer) throws IOException {
		FileWriter writer = new FileWriter(scriptLocation);
		writer.append("#!" + SHELL + "\n\n");
		
		if(!isKeeper){
			{// WRITE THE FIFO SOCKETS FUNCTION
				Reader functionReader = new InputStreamReader(PosixFifoMisc.sendFifoSocketMessageBashFunction());
				char[] b = new char[1024];
				
				for(int x=functionReader.read(b); x!=-1;x=functionReader.read(b)){
					writer.write(b, 0, x);
				}
			}
			
			{
				// WRITE THE POLLING CALL TO THE KEEPER
				final char EOL = '\n';
				writer.append(EOL + 
						"CONTROL=" + fifoSocketServer.controlPipe().getAbsolutePath() + "\n" + 
						"DIR=" + fifoSocketServer.socketsDir().getAbsolutePath() + EOL + 
						"" + EOL + 
						"xmessage \"Checking for updates...\" -buttons \"\" -center -title \"Please Wait...\" &" + EOL + 
						"DIALOG=$!" + EOL + 
						"" + EOL + 
						"RESPONSE=`echo -n \"LAUNCH_POLL\" | sendFifoSocketMessage $CONTROL $DIR`" + EOL + 
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
			writer.append(launchCommand);
		}else{
			writer.append("while [ 1 ]; do \n");
			writer.append(launchCommand);
			writer.append('\n');
			writer.append("RESULT=$?\n");
			writer.append("if [ $RESULT != 0 ]; then echo \"Error: keeper quit with return $RESULT\"; exit; fi\n");
			writer.append("done");
		}
		
		
		writer.flush();
		writer.close();

	}

	private File launchFile(){
		return new File(guts.data.path, "launch-" + info.id() + ".sh");
	}

	@Override
	public void uninstall() {

		final File scriptLocation = launchFile();
		try {
			removeFromApplicationsMenu(this.info.id(), scriptLocation, fsMap);
		} catch (IOException e) {
			throw new RuntimeException("Error: " + e.getMessage(), e);
		}
	}
	
}
