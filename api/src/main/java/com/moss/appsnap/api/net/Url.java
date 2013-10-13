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
package com.moss.appsnap.api.net;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * http://test.com
 */
@XmlJavaTypeAdapter(UrlAdapter.class)
@SuppressWarnings("serial")
public class Url implements Serializable {
	public static class Query implements Serializable {
		private QueryParam[] params;
		
		public Query(String query){
			if(query.charAt(0)!='?'){
				throw new RuntimeException("Queries must start with a ?");
			}
			
			final String[] parts = query.substring(1).split(Pattern.quote("&"));
			
			this.params = new QueryParam[parts.length];
			
			for(int x=0;x<parts.length;x++){
				final String part = parts[x];
				final int separatorIndex = part.indexOf('=');
				final String name = part.substring(0, separatorIndex);
				final String value = part.substring(separatorIndex+1);
				
				this.params[x] = new QueryParam(name, value);
			}
		}
		
		public String toString(){
			StringBuilder text = new StringBuilder();
			for(int x=0;x<params.length;x++){
				if(x==0){
					text.append("?");
				}else{
					text.append("&");
				}
				QueryParam p = params[x];
				text.append(p.name);
				text.append("=");
				text.append(p.value);
			}
			return text.toString();
		}
	}
	
	public static class Path implements Serializable {
		String[] segments;
		
		public Path(String path) {
			if(path.length()==0 || path.charAt(0)!='/'){
				throw new UrlSyntaxException("Path is missing leading slash");
			}
			segments = path.split(Pattern.quote("/"));
		}
		
		@Override
		public String toString() {
			StringBuilder text = new StringBuilder();
			
			for(String segment : segments){
				if(segment.length()>0){
					text.append("/");
					text.append(segment);
				}
			}
			
			return text.toString();
		}
	}
	public static class QueryParam implements Serializable {
		private String name;
		private String value;
		
		public QueryParam(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		
		public String name() {
			return name;
		}
		
		public String value() {
			return value;
		}
	}
	private String protocol;
	private String host;
	private String rootSymbol;
	private String userInfo;
	private int port;
	private Path path;
	private Query query;
	private String fragment;
	
	public Url(Url base, String relativePath) {
		this(base.protocol, base.host, base.rootSymbol, base.userInfo, base.port, new Path(base.path.toString() + "/" + relativePath), base.query, null);
	}
	
	public Url(String protocol, String host, String rootSymbol, String userInfo, int port, Path path, Query query, String fragment) {
		super();
		this.protocol = protocol;
		this.host = host;
		this.rootSymbol = rootSymbol;
		this.userInfo = userInfo;
		this.port = port;
		this.path = path;
		this.query = query;
		this.fragment = fragment;
	}
	
	
	public Url(URI uri){
		this(uri.getScheme(), uri.getHost(), "/", uri.getUserInfo(), uri.getPort(), new Path(uri.getPath()), uri.getQuery()==null?null:new Query(uri.getQuery()), uri.getFragment());
	}
	
	public Url(String text){
		this(URI.create(text));
	}
	
	public static Url http(String host, String path){
		return new Url("http", host, "/", null, 80, new Path(path), null, null);
	}
	public static Url http(String host, String path, Query q){
		return new Url("http", host, "/", null, 80, new Path(path), q, null);
	}
	public static Url http(String host, int port, String path){
		return new Url("http", host, "/", null, port, new Path(path), null, null);
	}
	public static Url https(String host, String path){
		return new Url("https", host, "/", null, 443, new Path(path), null, null);
	}
	public static Url https(String host, int port, String path){
		return new Url("https", host, "/", null, port, new Path(path), null, null);
	}
	private URI asURI() throws URISyntaxException{
		return new URI(protocol, userInfo, host, port, path.toString(), query==null?null:query.toString(), fragment);
	}
	
	
	public String fragment() {
		return fragment;
	}
	
	public String host() {
		return host;
	}
	public Path path() {
		return path;
	}
	public int port() {
		return port;
	}
	public String protocol() {
		return protocol;
	}
	public Query query() {
		return query;
	}
	public String rootSymbol() {
		return rootSymbol;
	}
	
	public String userInfo() {
		return userInfo;
	}
	
	public Url descend(String directoryName){
		if(directoryName.indexOf('/')!=-1){
			throw new UrlSyntaxException("You can only descend one directory at a time, but you specified more than one: \"" + directoryName + "\"");
		}
		return new Url(this, directoryName);
	}
	
	@Override
	public String toString() {
		try {
			return asURI().toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Url && o.toString().equals(toString());
	}
}
