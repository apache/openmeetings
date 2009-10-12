package org.openmeetings.server.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerSharingSessionBean;
import org.openmeetings.server.cache.ServerSharingSessionList;
import org.openmeetings.servlet.outputhandler.ScreenServlet;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerOutputHandler extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ServerOutputHandler.class);

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
