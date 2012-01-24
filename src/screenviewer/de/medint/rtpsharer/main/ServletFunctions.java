/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.medint.rtpsharer.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import de.medint.rtpsharer.util.ConfigUtil;


/**
 * 
 * @author o.becherer
 *
 */
public class ServletFunctions {
	
	/**
	 * 
	 * @param servletUrl
	 * @param jpegQuality
	 * @param height
	 * @param width
	 */

	public static void sendStartSignal() throws Exception{

		
		
		String servletUrl = "http://"+ConfigUtil.rtmphostlocal
								+":"+ConfigUtil.red5httpport
								+"/"+ConfigUtil.webAppRootKey
								+"/RTPMethodServlet";
		

		String url = servletUrl + "?method=streamer_start&room=" + ConfigUtil.ROOM 
						+ "&height=" + ConfigUtil.videoHeight 
						+ "&width=" + ConfigUtil.videoWidth 
						+ "&quality=" + ConfigUtil.quality 
						+ "&sid=" + ConfigUtil.SID 
						+ "&sharerIP=" + ConfigUtil.sharerIP
						+ "&publicSID=" + ConfigUtil.PUBLIC_SID
						+ "&host=" + ConfigUtil.rtmphostlocal
						+ "&rtpport=" + ConfigUtil.destinationPort;
			

		
		URLConnection c = getConnection(url);
		
		// open a stream which can write to the url
		DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

		dstream.writeUTF("ServletCall");
		dstream.flush();
		dstream.close();
		
		 // read the output from the URL
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
		
		String sIn = in.readLine();
		while (sIn != null) {
			if (sIn != null) {
				System.out.println(sIn);
			}
			sIn += in.readLine();
		}
		in.close();
		c = null;
		
	}
	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param servletUrl
	 * @param jpegQuality
	 * @param height
	 * @param width
	 */
	//---------------------------------------------------------------------------------------------------------
	public static void sendStopSignal() throws Exception{
		
		String servletUrl = "http://"+ConfigUtil.rtmphostlocal
								+":"+ConfigUtil.red5httpport
								+"/"+ConfigUtil.webAppRootKey
								+"/RTPMethodServlet";
		
		//Building ServletUrl
		String url = servletUrl + "?method=streamer_stop" +
											"&room=" + ConfigUtil.ROOM + 
											"&sid=" + ConfigUtil.SID +
											"&publicSID=" + ConfigUtil.PUBLIC_SID;
		
		URLConnection c = getConnection(url);
		
		// open a stream which can write to the url
		DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

		dstream.writeUTF("ServletCall");
		dstream.flush();
		dstream.close();
		
		 // read the output from the URL
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
		
		String sIn = in.readLine();
		while (sIn != null) {
			if (sIn != null) {
				System.out.println(sIn);
			}
			sIn += in.readLine();
		}
		in.close();
		c = null;
		
	}
	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Opening URLConnection
	 */
	//---------------------------------------------------------------------------------------------------------
	private static URLConnection getConnection(String servletUrl) throws Exception{
		
		URL u = new URL(servletUrl);
		URLConnection c = u.openConnection();

		// post multipart data
		c.setDoOutput(true);
		c.setDoInput(true);
		c.setUseCaches(false);
		
		return c;

	}
	//---------------------------------------------------------------------------------------------------------
	
}
