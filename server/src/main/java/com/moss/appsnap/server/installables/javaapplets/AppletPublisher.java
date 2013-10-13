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
package com.moss.appsnap.server.installables.javaapplets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;

import com.moss.appkeep.api.ComponentId;
import com.moss.appkeep.api.endorse.x509.X509CertId;
import com.moss.appsnap.api.AppsnapService;
import com.moss.appsnap.api.apps.AppId;
import com.moss.appsnap.api.catalog.PublicationInfo;
import com.moss.appsnap.api.groups.PublicationId;
import com.moss.appsnap.api.installables.AppkeepDownloadVector;
import com.moss.appsnap.api.installables.ComponentResolveTool;
import com.moss.appsnap.api.installables.InstallableId;
import com.moss.appsnap.api.installables.InstallationPlan;
import com.moss.appsnap.api.installables.JavaAppletInstallableDetails;
import com.moss.appsnap.api.net.Url;
import com.moss.appsnap.api.streams.StreamId;
import com.moss.appsnap.server.Data;
import com.moss.appsnap.server.config.ServerConfiguration;
import com.moss.appsnap.server.groups.Publication;
import com.moss.appsnap.server.groups.StoredGroup;
import com.moss.appsnap.server.installables.Installable;
import com.moss.appsnap.server.installables.InstallableVisitor;
import com.moss.appsnap.server.installables.javaapps.JavaAppInstallable;
import com.moss.appsnap.server.streams.StoredStream;
import com.moss.identity.tools.IdProover;
import com.moss.jnlp.specmodel.JnlpDescriptor;
import com.moss.jnlp.specmodel.applet.AppletDescription;
import com.moss.jnlp.specmodel.applet.AppletParam;
import com.moss.jnlp.specmodel.common.Information;
import com.moss.jnlp.specmodel.common.MenuSpec;
import com.moss.jnlp.specmodel.common.Resources;
import com.moss.jnlp.specmodel.common.SecuritySpec;
import com.moss.jnlp.specmodel.common.ShortcutSpec;
import com.moss.jnlp.specmodel.common.UpdateSpec;
import com.moss.launch.components.Component;
import com.moss.launch.spec.applet.AppletParameter;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.sleepycat.je.LockMode;

public class AppletPublisher extends AbstractHandler {
	private final Data data;
	private final ProxyFactory proxies;
	private final ServerConfiguration config;
	private final IdProover idProver;
	
	public AppletPublisher(Data data, ProxyFactory proxies,
			ServerConfiguration config, IdProover idProver) {
		super();
		this.data = data;
		this.proxies = proxies;
		this.config = config;
		this.idProver = idProver;
	}

	public void handle(
			String target, 
			HttpServletRequest request, HttpServletResponse response, 
			int dispatch
			) throws IOException, ServletException {
		
		if(!request.getPathInfo().endsWith(".js")){
			return;
		}
		
		// READ PARAMETERS
		final String functionName = request.getParameter("functionName");
		final PublicationId publication = new PublicationId(request.getParameter("publication"));
		
		// FIND THE INSTALLABLE
		StoredGroup group = data.groupsByPublication.get(publication, null, LockMode.READ_COMMITTED);
		if(group==null){
			throw new RuntimeException();
		}
		Publication pub = group.publication(publication);
		
		StoredStream stream = data.streams.get(pub.stream(), null, LockMode.READ_COMMITTED);
		
		InstallableId version = stream.currentStatus(pub.app()).version();
		
		Installable i = data.installables.get(version, null, LockMode.DEFAULT);
		
		JavaAppletInstallable applet = i.accept(new InstallableVisitor<JavaAppletInstallable>() {
				public JavaAppletInstallable visit(JavaAppInstallable app) {
					return null;
				}
				public JavaAppletInstallable visit(JavaAppletInstallable applet) {
					return applet;
				}
			});
		
		if(applet==null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			PrintWriter w = response.getWriter();
			w.write("Bad request: Installable "+ i.id() + " does not describe an applet");
			w.close();
			return;
		}
		
		StringBuilder javaScript;
		try {
			javaScript = deployAppletJavascriptFuntion(functionName, applet);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			PrintWriter w = response.getWriter();
			w.write("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			w.close();
			return;
		}

		PrintWriter w = response.getWriter();
		w.write(javaScript.toString());
		w.close();
		
	}
	
	public static StringBuilder deployJnlp(AppsnapService snap, AppId app, StreamId stream, String appletClass, IdProover idProver) throws Exception {
		InstallableId version = snap.currentVersion(stream, app, idProver.giveProof());
		InstallationPlan plan = snap.getInstallable(version, idProver.giveProof());
		
		StringBuilder text = new StringBuilder();
		
//		JnlpDescriptor jnlp = translate(plan);
//		
//		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//		JAXBContext.newInstance(JnlpDescriptor.class).createMarshaller().marshal(jnlp, bytes);
//		
//		text.append(new String(bytes.toByteArray()));
		
		return text;
	}
	
	
	private static JnlpDescriptor translate(PublicationInfo pub, InstallationPlan plan, String serviceName, String href){

		Random random = new Random();
		
		AppkeepDownloadVector vector = plan.componentSources().get(random.nextInt(plan.componentSources().size()));
		
		Information information = new Information(
				pub.name(),
				serviceName,
				pub.description(),
				false,
				new ShortcutSpec(
						true,
						false,
						new MenuSpec(serviceName)
				)
			);
		
		Resources resources = new Resources();
		
		JavaAppletInstallableDetails details = JavaAppletInstallableDetails.grab(plan.details());
		
		for(Component c : details.launchSpec().components()){
			
		}
		
		SecuritySpec security = null;
		
		Integer width = 640;
		Integer height = 480;
		String documentbase = null;
		
		List<AppletParam> arguments = new ArrayList<AppletParam>(details.launchSpec().parameters().size());
		for(AppletParameter next : details.launchSpec().parameters()){
			arguments.add(new AppletParam(next.name(), next.value()));
		}
		
		AppletDescription applicationDescription = new AppletDescription(
				details.launchSpec().appletClass().toString(),
				details.launchSpec().name(),
				width,
				height,
				documentbase,
				arguments
				);
		
		JnlpDescriptor d = new JnlpDescriptor(
			href,
			vector.location().toString(),
			information,
			new UpdateSpec(),
			resources,
			security,
			applicationDescription
		);
		
		return d;
		
	}
	
	public StringBuilder deployAppletJavascriptFuntion(String functionName, JavaAppletInstallable applet) throws Exception {
		StringBuilder text = new StringBuilder();
		
		
		Url keepLocation = config.keepLocations().get(new Random().nextInt(config.keepLocations().size()));
		
		String codeBase = Url.http(keepLocation.host(), keepLocation.port(), "/by-component-id").toString();
		
		X509CertId certId = applet.jarsignCertificate();
		List<String> componentFiles = new LinkedList<String>();
		
		ComponentResolveTool resolver = new ComponentResolveTool(applet.launchSpec(), applet.componentResolutions());
		
		for(Component c : applet.launchSpec().components()){
			
//			
//			ComponentSelector s = new ComponentHandlesSelector(c.artifactHandles());
//			ComponentInfo info = keep.getInfos(Collections.singletonList(s), new UserAccountDownloadToken(idProver.giveProof())).get(0);
//			
//			keep.endorse(new ComponentSelector[]{s}, new JarsignEndorsement(new X509CertId[]{certId}));
//			keep.grantWorldAccess(Collections.singletonList(s), idProver.giveProof());
			
			ComponentId cId = resolver.resolve(c);
			
			String file = cId + ".jar?with-endorsements=jarsign:" + certId;
			
			componentFiles.add(file);
//			text.append(file);
//			text.append(",");
			
			String url = codeBase + file;
			System.out.println(c + ":" + url);
			
		}
		
//		ResolvedAppletSpec launch;
//		
//		{
//			JavaAppletSpec spec = new JavaAppletSpec("Installer Applet", new ClassName("com.moss.appsnap.keeper.installerapp.Installer"));
//			spec.addParameter("keeper-publication-id", "d7cb9f70-7ae6-433a-bd95-c3b5e971d461");
//			spec.addParameter("url", "http://localhost:6020/rpc");
//			
//			launch = new ResolvedAppletSpec(spec, null);
//		}
//		
//		String codeBase = "http://localhost:4555/by-component-id/";
		
		text.append("var " + functionName + " = { \n");
		text.append("\n");
		text.append("	deploy: function() { \n");
//	    var attributes = {codebase:'http://java.sun.com/products/plugin/1.5.0/demos/jfc/Java2D',
//                code:'java2d.Java2DemoApplet.class',
//                archive:'Java2Demo.jar',
//                width:710, height:540} ;
		text.append("		var attributes = {\n");
		text.append("			code:'" + applet.launchSpec().appletClass() + "',\n");
		text.append("			codebase:'" + codeBase + "',\n");
		
//		http://localhost:4555/by-component-id/8169fbbe-1b6b-46d5-ac4a-57dd86d4384c.jar?with-endorsements=jarsign:bfb6426c-0f64-4e1b-b949-ef0c0f6ec488;jarsign:bfb6426c-0f64-4e1b-b949-ef0c0f6ec488
//		AppkeepService keep = proxies.create(AppkeepService.class, config.keepLocations().get(0).toString());
		
		text.append("			archive:'");
//		X509CertId certId = keep.listCertificates().get(0).id();
//		for(Component c : applet.launchSpec().components()){
//			ComponentSelector s = new ComponentHandlesSelector(c.artifactHandles());
//			ComponentInfo info = keep.getInfos(Collections.singletonList(s), new UserAccountDownloadToken(idProver.giveProof())).get(0);
//			
//			keep.endorse(new ComponentSelector[]{s}, new JarsignEndorsement(new X509CertId[]{certId}));
//			keep.grantWorldAccess(Collections.singletonList(s), idProver.giveProof());
//			
//			String file = info.id() + ".jar?with-endorsements=jarsign:" + certId;
//			
//			text.append(file);
//			text.append(",");
//			
//			String url = codeBase + file;
//			System.out.println(s + ":" + url);
//			
//		}
		
		for(String file : componentFiles){
			text.append(file);
			text.append(",");
		}
		
		text.append("',\n");
		
//		text.append("			archive:'Java2Demo.jar',\n");
		text.append("			width:'100%',\n");
		text.append("			height:'100%'\n");
		text.append("		} ; \n");
		text.append("		var parameters = {\n");
		
		List<AppletParameter> params = applet.launchSpec().parameters();
		
		for(int x=0;x<params.size();x++){
			AppletParameter next = params.get(x);
//		}
//		for(AppletParameter next : applet.launchSpec().parameters()){
			text.append("			\"" + next.name() + "\":'" + next.value() + "'");
			if(x==params.size()-1){
				text.append("\n");
			}else{
				text.append(",\n");
			}
		}
		
//		text.append("			\"keeper-publication-id\":'d7cb9f70-7ae6-433a-bd95-c3b5e971d461',\n");
//		text.append("			url:'http://localhost:6020/rpc'\n");
		text.append("		} ; \n");
		text.append("		deployJava.runApplet(attributes, parameters, '1.5'); \n");
		
		text.append("	}, \n");
		text.append("	 \n");
		text.append("	do_initialize: function() { \n");
//		text.append("		document.write('fdsfds'); \n");
		text.append("	} \n");
		text.append("	 \n");
		text.append("}; \n");
		text.append(functionName + ".do_initialize(); \n");
		
		
		
		return text;
	}
}
