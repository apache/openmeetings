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
package org.openmeetings.server.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.cache.ServerSharingSessionList;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerOutputHandler extends HttpServlet {
	
	private static final Logger log = Red5LoggerFactory.getLogger(ServerOutputHandler.class, ScopeApplicationAdapter.webAppRootKey);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */ 
	protected void doGet(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,IOException {
		
		try {
			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);
			
			String publicSID = httpServletRequest.getParameter("publicSID");
			if (publicSID == null) {
				log.error("publicSID is empty: "+publicSID);
				return;
			}
			
			String xAsString = httpServletRequest.getParameter("x");
			if (xAsString == null) {
				log.error("xAsString is empty: "+xAsString);
				return;
			}
			int x = Integer.parseInt(xAsString);
			
			String yAsString = httpServletRequest.getParameter("y");
			if (yAsString == null) {
				log.error("yAsString is empty: "+yAsString);
				return;
			}
			int y = Integer.parseInt(yAsString);
				
			//Testing & Debugging
			//ServerSharingSessionBean serverSharingSession = ServerSharingSessionList.getServerSharingSessionBeanByPublicSID(publicSID);
			//byte[] buffer = serverSharingSession.getServerFrameBeans().get(0).getImageBytes();
			
			ServerFrameBean sBean = ServerSharingSessionList.getFrameByPublicSID(publicSID, x, y);
			
			if (sBean == null) {
				return;
			}
			
			byte[] buffer = sBean.getImageBytesAsJPEG();
			
			String filename = "f_"+new Date().getTime()+".jpg";
			//Image myImage = 
			 
			httpServletResponse.reset();
			httpServletResponse.resetBuffer();
			//
			
			httpServletResponse.setContentType("image/jpeg");
			httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");
			//httpServletResponse.setHeader("Content-Length", ""+ buffer.length);
			httpServletResponse.setHeader("Cache-Control", "no-cache");
			//httpServletResponse.setHeader("Pragma", "no-cache" );
			httpServletResponse.setHeader( "pragma", "no-cache" );

			String encoding = httpServletRequest.getHeader("Accept-Encoding");
			
			log.debug("encoding: "+encoding);
			
			// gzip broser?
			
            if (encoding!=null && encoding.indexOf("gzip")>=0) {    
            	
            	log.debug("Browser does accept GZIP");
            	
        		httpServletResponse.setHeader("Content-Encoding","gzip");
        		
        		OutputStream out = httpServletResponse.getOutputStream();
        		
        		out.write(buffer);
        		
        		out.close();
                
            } else {  
            	
            	log.debug("Browser does NOT accept GZIP");
//                	
//                	//GunZip the Payload 
//	        		ByteArrayInputStream byteGzipOut = new ByteArrayInputStream(buffer);
//	        		GZIPInputStream gZipIn = new GZIPInputStream(byteGzipOut);
//
//	        		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
//	        		
//	        		byte[] bufferTemp = new byte[1024];
//	        		int count = 0;
//	        		while ((count = gZipIn.read(bufferTemp)) > 0 ){
//	        			bytesOut.write(bufferTemp,0,count);
//        			}
//        			bytesOut.close();
//        			gZipIn.close();
//        			
//        			log.debug("gZipIn CLosed");
//        			
//                	// Some old browser -> give them plain text.                        
//        			OutputStream out = httpServletResponse.getOutputStream();
//    				out.write(buffer);
//            		out.close();
        		
        		throw new Exception("Browser does NOT accept GZIP");
        		
                
            }
		} catch (Exception err) {
			
			log.error("[doGet]",err);
			
		}
			
	}
		

}
