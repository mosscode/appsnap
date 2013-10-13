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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appkeep.api.ComponentId;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.keeper.DesktopAppHandler;
import com.moss.appsnap.keeper.Guts;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.data.ResolvedJavaLaunchSpec;
import com.moss.launch.components.Component;
import com.moss.launch.spec.app.launch.AppLaunchRecipie;
import com.moss.launch.spec.app.launch.Argument;
import com.moss.launch.tools.LaunchBuilder;
import com.moss.launch.tools.simplelauncher.ComponentResolver;
import com.moss.mswindows.shortcuts.ShellLink;

public class MSWindowsAppHandler extends DesktopAppHandler {
	private final Log log = LogFactory.getLog(getClass());
	private final WindowsFSLayout fsLayout;
	private final SnapDaemonFSLayout daemonFsLayout;
	private final File installDirPath;
	
	// For non-daemon apps
	private final File appLauncherExe;
	private final File portFilePath;
	private final File messagePath;
	
	// For daemon apps
	private final File daemonLauncherExe;
	private final File launchFilePath;
	
	public MSWindowsAppHandler(InstallationInfo info, Guts guts, WindowsFSLayout fsLayout, SnapDaemonFSLayout daemonFsLayout) {
		super(info, guts);
		this.fsLayout = fsLayout;
		this.daemonFsLayout = daemonFsLayout;
		this.installDirPath = new File(daemonFsLayout.installDirsPath, info.id().toString());
		this.appLauncherExe = new File(installDirPath, "app.exe");
		this.portFilePath = new File(installDirPath, "port.txt");
		this.messagePath = new File(installDirPath, "message.txt");
		this.launchFilePath = new File(installDirPath, "launch.txt");
		if(info.isKeeperSoftware()){
			this.daemonLauncherExe = new File(installDirPath, "appsnap-keeper-launcher.exe");
		}else{
			this.daemonLauncherExe = new File(installDirPath, "appsnap-daemon-launcher.exe");
		}
	}
	
	private StringBuilder launchCommand(final ResolvedJavaLaunchSpec launch){
		StringBuilder text = launchArgs(launch);
		final String pathToJava = "javaw";
		text.insert(0, " ");
		text.insert(0, pathToJava);
		
		return text;
	}
	private StringBuilder launchArgs(final ResolvedJavaLaunchSpec launch){
		final AppLaunchRecipie r = launch.spec();
		
		LaunchBuilder b = new LaunchBuilder(';');
		b.setMainClassName(r.mainClass().value());
		b.setJavaCommand("poop");
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
			if(!next.equals("poop")){
				line.append("\"");
				line.append(next);
				line.append("\"");
				line.append(' ');
			}
		}
		return line;
	}
	
	@Override
	public void handleInstall(boolean daemon, final ResolvedJavaLaunchSpec launch) {
		
		
		// write exe, port and params
		
		
		try {
			
			
			daemonFsLayout.mkdirs();
			
			if(!installDirPath.exists() && ! installDirPath.mkdirs()){
				throw new RuntimeException("Could not create directory: " + installDirPath.getAbsolutePath());
			}
			
			ShellLink lnk = new ShellLink();
			lnk.setWorkingDir(installDirPath.getAbsolutePath());
			
			if(daemon){
				try{
					MSWindowsDesktopIntegrationStrategy.writeDaemonLauncher(daemonLauncherExe);
				} catch (Throwable e) {
					log.warn("There was an error copying the daemon launcher exe.  This is expected if the keeper just launched.", e);
				}
				
				final File javaw = resolveFileFromPath("javaw.exe");
				
				final String args;
				{
					StringBuilder line = launchArgs(launch);
					args = line.toString();
					System.out.println("Using line: " + line);
//					String regex = "\"";
//					String replacement = "\\\\\"";
//					arg = line.toString().replaceAll(regex, replacement);
				}
				System.out.println("Using arg: " + args);
				
				{
					Writer w = new FileWriter(launchFilePath);
					w.write("\"" + javaw.getAbsolutePath() + "\" " + args + "");
					w.close();
				}
								
//				
//				lnk.setLinkTarget(daemonFsLayout.daemonLauncherExe.getAbsolutePath());
//				lnk.setCommandLineArguments("\"" + arg + "\"");
				
				lnk.setLinkTarget(daemonLauncherExe);
				
				String id = info.id().toString();
				String name = guts.serviceInfo.name() + "-keeper-" + id.substring(id.length()-4) + ".lnk";
				lnk.write(
					new File(
						fsLayout.startupMenu, 
						name
					)
				);
				
			}else{
				
				MSWindowsDesktopIntegrationStrategy.writeKeeperTalker(appLauncherExe);
				
				{
					Writer w = new FileWriter(messagePath);
					w.write("LAUNCH " + info.id());
					w.close();
				}
				
				lnk.setLinkTarget(appLauncherExe.getAbsolutePath());
//				lnk.setCommandLineArguments("\"LAUNCH " + info.id() + "\"");
				
				String fileName = info.name() + ".lnk";
				
				lnk.write(new File(fsLayout.desktop, fileName));
				lnk.write(new File(daemonFsLayout.daemonMenu, fileName));
			}
			
			
//			
//			writeLaunchScript(daemon, line.toString(), scriptLocation, fsMap.fifoSockets);
//
//			if(daemon){
//				log.info("Writing " + info.name() + "(" + info.id() + ") to gnome session");
//				addToGnomeSession(scriptLocation);
//			}else{
//				log.info("Writing " + info.name() + "(" + info.id() + " to applications menu");
//				addToApplicationsMenu(info.name(), info.id(), scriptLocation, fsMap);
//			}
//			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static File resolveFileFromPath(String name){
		File result = null;
		
		String[] paths = System.getenv("PATH").split(Pattern.quote(";"));
		
		for(String next : paths){
			File f = new File(next, name);
			System.out.println("Examining " + f);
			if(f.exists()){
				result = f;
			}
		}
		return result;
	}
	
	@Override
	public void launch() {
		InstallableId id = info.currentVersion();
		if(id==null){
			throw new NullPointerException("There is no current version for " + info.id());
		}
		ResolvedJavaLaunchSpec rls = guts.data.javaLaunchSpecs.get(id);
		
		StringBuilder line = launchCommand(rls);
		

		try {
			Process p;
			if(info.isKeeperSoftware()){
				
//				System.out.println("Using line: " + line);
//				String regex = "\"";
//				String replacement = "\\\\\"";
//				String arg = line.toString().replaceAll(regex, replacement);
//				System.out.println("Using arg: " + arg);
				ProcessBuilder pb = new ProcessBuilder(daemonLauncherExe.getAbsolutePath());
				pb.directory(installDirPath);
				p = pb.start();
				
			}else{
				p = Runtime.getRuntime().exec(line.toString());
			}

			new MonitorThread(p).start();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Launch error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
//		JOptionPane.showMessageDialog(null, "ERROR: NOT YET IMPLEMENTED");
	}
	@Override
	public void uninstall() {
		removeLinks(fsLayout.desktop);
		removeLinks(daemonFsLayout.daemonMenu);
	}
	
	
	private void removeLinks(File dir){
		if(dir.exists()){
			if(!dir.isDirectory()){
				throw new RuntimeException(dir.getAbsolutePath() + " is not a directory");
			}
			
			File[] children = dir.listFiles();
			
			if(children!=null){
				
				for(File child : children){
					if(child.isDirectory()){
						removeLinks(child);
					}else if(child.getName().toLowerCase().endsWith(".lnk")){
						ShellLink lnk = new ShellLink();
						
						try {
							lnk.read(child);
							String args = lnk.getCommandLineArguments();
							
							if(args!=null && args.contains(info.id().toString())){
								System.out.println(child.getAbsolutePath() + " points to me!");
								child.delete();
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
	}
	
	static class MonitorThread extends Thread {
		final Process p;
		
		public MonitorThread(Process p) {
			super();
			this.p = p;
		}

		@Override
		public void run() {
			try {
				Thread in = new DumperThread(p.getInputStream(), System.out);
				Thread out = new DumperThread(p.getErrorStream(), System.err);
				
				in.start();
				out.start();
				in.join();
				out.join();
				int result = p.waitFor();
				
				System.out.println("Process exited with code " + result);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	static class DumperThread extends Thread {
		final InputStream source;
		final PrintStream sink;
		
		public DumperThread(InputStream source, PrintStream sink) {
			super();
			this.source = source;
			this.sink = sink;
		}
		
		@Override
		public void run() {
			try {
				for(int b = source.read();b!=-1;b = source.read()){
					sink.write(b);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
