package org.openmeetings.servlet.outputhandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.utils.image.ImageUtility;
import org.openmeetings.utils.image.ZipUtility;
import org.openmeetings.utils.stringhandlers.StringComparer;

import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

import com.anotherbigidea.flash.movie.ImageUtil;
import com.anotherbigidea.flash.movie.Movie;
import com.anotherbigidea.flash.movie.Shape;
import com.anotherbigidea.flash.readers.MovieBuilder;
import com.anotherbigidea.flash.readers.SWFReader;
import com.anotherbigidea.flash.readers.TagParser;
import com.anotherbigidea.flash.writers.SWFWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.jmx.snmp.Timestamp;

public class ScreenServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ScreenServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */ 
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		
		
			if (httpServletRequest.getContentLength() > 0) {
			
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
				log.debug("publicSID: " + publicSID);	
				
				String room = httpServletRequest.getParameter("room");
				if (room == null) {
					room = "default";
				}
				log.debug("room: " + room);	

				String domain = httpServletRequest.getParameter("domain");
				if (domain == null) {
					domain = "default";
				}
				log.debug("domain: " + domain);
				
				String record = httpServletRequest.getParameter("record");
				if (record == null) {
					record = "no";
				}
				
				log.debug("record: " + record);
				
				ServletMultipartRequest upload = new ServletMultipartRequest(httpServletRequest, 104857600); // max 100 mb
				
				
				Long users_id = Sessionmanagement.getInstance().checkSession(sid);
				Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
				// Switch between scharers
				Configuration c = Configurationmanagement.getInstance().getConfKey(user_level, "screen_viewer");
				
				if(c == null || c.getConf_value().equals("0")){
					doStandardSharing(sid, publicSID, room, domain, upload, record, httpServletResponse);
				}
				else{
					doJrDeskTopSharing(sid, publicSID, room, domain, upload, record, httpServletResponse);
				}
					
			}

	}
	
	
	private void doStandardSharing(String sid, String publicSID, String room, String domain, ServletMultipartRequest upload, String record, HttpServletResponse httpServletResponse) throws ServletException,IOException{
		
		try {
			System.out.println("ScreenServlet Call");
			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
				Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
				
				if (user_level > 0) {
					
					//check if this Client is still inside the Room
					boolean userIsInRoom = false;
					boolean doProcess = false;
					
					HashMap<String, RoomClient> clientList = ClientListManager.getInstance().getClientList();
					for (Iterator iter = clientList.keySet().iterator();iter.hasNext();) {
						RoomClient rcl = clientList.get(iter.next());
						if (rcl.getPublicSID().equals(publicSID)) {
							log.debug("found RoomClient");
							if (rcl.getRoom_id() != null && rcl.getRoom_id().toString().equals(room)) {
								log.debug("User is inside Room");
								userIsInRoom = true;
								doProcess = true;
							} else {
								log.debug("User already left room, block Screen - logical Room Leave");
								OutputStream out = httpServletResponse.getOutputStream();
								String returnValue = "close";
								out.write(returnValue.getBytes());
								//return;
							}
						}
					}
					
					if (!userIsInRoom) {
						log.debug("User already left room, block Screen - Browser Closed");
						OutputStream out = httpServletResponse.getOutputStream();
						String returnValue = "close";
						out.write(returnValue.getBytes());
						//return;
					}

					if (doProcess) {
						//make a complete name out of domain(organisation) + roomname
						String roomName = domain + "_" + room;
						//trim whitespaces cause it is a directory name
						roomName = StringUtils.deleteWhitespace(roomName);
	
						//Get the current User-Directory
	
						String current_dir = getServletContext().getRealPath("/");
						log.debug("Current_dir: " + current_dir);
	
						String working_dir = "";
						log.debug("MAX_READ_BYTES"+MultipartRequest.MAX_READ_BYTES);
	
						working_dir = current_dir + "desktop" + File.separatorChar + roomName + File.separatorChar;
	
						//Add the Folder for the Room if it does not exist yet
						File localFolder = new File(working_dir);
						if (!localFolder.exists()) {
							localFolder.mkdir();
						}
	
						log.debug("#### UploadHandler working_dir: "+ working_dir);
						
					
						InputStream is = upload.getFileContents("Filedata");
	
						//trim whitespace
						String fileSystemName = StringUtils.deleteWhitespace(upload.getFileSystemName("Filedata"));
	
						String newFileSystemName = StringComparer.getInstance()
								.compareForRealPaths(
										fileSystemName.substring(0,
												fileSystemName.length() - 4));
						String newFileSystemExtName = fileSystemName.substring(
								fileSystemName.length() - 4,
								fileSystemName.length()).toLowerCase();
	
						//trim long names cause cannot output that
						if (newFileSystemName.length() >= 17) {
							newFileSystemName = newFileSystemName.substring(0,16);
						}
						
						String completeName = working_dir + newFileSystemName+"_"+sid;
	
						File f = new File(completeName + newFileSystemExtName);
						if (f.exists()) {
							f.delete();
						}
	
						log.debug("*****2 ***** completeName: "+ completeName + newFileSystemExtName);
						FileOutputStream fos = new FileOutputStream(completeName + newFileSystemExtName);
	
						byte[] buffer = new byte[1024];
						int len = 0;
						
						while ( (len= is.read(buffer, 0, buffer.length)) >-1 ) {
							fos.write(buffer, 0, len);
						}
	
						fos.close();
						is.close();	
						
						
						
						LinkedHashMap<String,Object> hs = new LinkedHashMap<String,Object>();
						hs.put("user", UsersDaoImpl.getInstance().getUser(users_id));
						hs.put("message", "desktop");
						hs.put("action", "newSlide");
						hs.put("fileName", newFileSystemName+"_"+sid+newFileSystemExtName);
						
						
						
						ScopeApplicationAdapter.getInstance().sendMessageByRoomAndDomain(Long.valueOf(room).longValue(),hs);
					
					}
	
				} else {
					log.debug("user not logged in");
				}

				
				
//				OutputStream out = httpServletResponse.getOutputStream();
//				String returnValue = "ok";
//				out.write(returnValue.getBytes());
//				
			
		} catch (Exception e) {
			log.error("ee " + e);
			e.printStackTrace();
			throw new ServletException(e);
		}

		
	}
	
	/**
	 * 
	 * I'll pass the swfFileName with the fileName, sothat every sharing-session has its own SWF
	 * 
	 * @param unzippedImageData
	 */
	private void writeSWFFile(String fileName, String swfFileName) throws Exception{
		log.debug("writeSWFFile");
		
		 	int[] size = new int[2];
		 	//--open the JPEG
	        FileInputStream jpegIn = new FileInputStream( fileName );
	       
		 	Shape image =  ImageUtil.shapeForImage( jpegIn, size );
	        
	        int width  = size[0];
	        int height = size[1];
	        jpegIn.close();
	        
	        //--Add a black border to the image shape (origin is in top left corner)
	        image.defineLineStyle( 1, null );  //default color is black
	        image.setLineStyle( 1 );
	        image.line( width, 0 );
	        image.line( width, height );
	        image.line( 0, height );
	        image.line( 0, 0 );        
	        
	        //String outputFileName = "output.swf";
	        
	        
	        File file = new File(swfFileName);
	        
	        if(!file.exists()){
		        Movie movie = new Movie( width+10, height+10, 12, 5, null );
		        movie.appendFrame().placeSymbol( image, 5, 5 );
		        
		        movie.write( swfFileName);
	        }
	        else{
	        	//Parse
	        	MovieBuilder builder = new MovieBuilder();
	        	FileInputStream movieOut = new FileInputStream( swfFileName );
	        	
	        	TagParser parser = new TagParser( builder );
	        	SWFReader reader = new SWFReader( parser, movieOut );
	        	
	        	try{
	        		reader.readFile();
	        		movieOut.close();
	        	}
	        	catch(Exception e){System.out.println("ERROR : " + e.getMessage());}

	        	Movie movie = builder.getMovie();
	        	
	        	movie.appendFrame().placeSymbol( image, 5, 5 );
		        
		        movie.write( swfFileName);
	        	
	        }
	}
	
	
	/**
	 * 
	 * @param sid
	 * @param publicSID
	 * @param room
	 * @param domain
	 * @param upload
	 * @param record
	 * @param httpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	//-------------------------------------------------------------------------------------------------------------------------------------
	private void doJrDeskTopSharing(String sid, String publicSID, String room, String domain, ServletMultipartRequest upload, String record, HttpServletResponse httpServletResponse) throws ServletException,IOException{
		
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(sid);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		
			if (user_level > 0) {
			
				//check if this Client is still inside the Room
				boolean userIsInRoom = false;
				boolean doProcess = false;
			
				HashMap<String, RoomClient> clientList = ClientListManager.getInstance().getClientList();
				for (Iterator iter = clientList.keySet().iterator();iter.hasNext();) {
					RoomClient rcl = clientList.get(iter.next());
					if (rcl.getPublicSID().equals(publicSID)) {
						log.debug("found RoomClient");
						if (rcl.getRoom_id() != null && rcl.getRoom_id().toString().equals(room)) {
							log.debug("User is inside Room");
							userIsInRoom = true;
							doProcess = true;
						} else {
							log.debug("User already left room, block Screen - logical Room Leave");
							OutputStream out = httpServletResponse.getOutputStream();
							String returnValue = "close";
							out.write(returnValue.getBytes());
						
						}
					}
				}
				
				
			
				if (!userIsInRoom) {
					log.debug("User already left room, block Screen - Browser Closed");
					OutputStream out = httpServletResponse.getOutputStream();
					String returnValue = "close";
					out.write(returnValue.getBytes());
					//return;
				}

				if (doProcess) {
					//make a complete name out of domain(organisation) + roomname
					String roomName = domain + "_" + room;
					//trim whitespaces cause it is a directory name
					roomName = StringUtils.deleteWhitespace(roomName);

					//Get the current User-Directory

					String current_dir = getServletContext().getRealPath("/");
					log.debug("Current_dir: " + current_dir);

					String working_dir = "";
					log.debug("MAX_READ_BYTES"+MultipartRequest.MAX_READ_BYTES);

					working_dir = current_dir + "desktop" + File.separatorChar + roomName + File.separatorChar;

					//Add the Folder for the Room if it does not exist yet
					File localFolder = new File(working_dir);
					if (!localFolder.exists()) {
						localFolder.mkdir();
					}

					if (record.equals("yes")) {
						working_dir += "record" + File.separatorChar;
						//Add the Folder for the Room-Recording if it does not exist yet
						File localFolder2 = new File(working_dir);
						if (!localFolder2.exists()) {
							localFolder2.mkdir();
						}
					}
					
					log.debug("#### UploadHandler working_dir: "+ working_dir);
					
					//Write Data To File
					
					
					
					InputStream is = upload.getFileContents("Filedata");
					ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
					byte[] buffy = new byte[1024];
					int leng = 0;
				
					while ( (leng= is.read(buffy, 0, buffy.length)) >-1 ) {
						bos.write(buffy, 0, leng);
					}
				
					byte[] ba = bos.toByteArray(); 
				
					if(ba.length < 59)
						return;

					// entzippen
					Object o = ZipUtility.byteArraytoObject(ba);
					ArrayList al = (ArrayList)o;
				
				
					byte[] temps = (byte[]) al.get(0);
					BufferedImage bi = ImageUtility.read(temps);
				
					//trim whitespace
					String fileSystemName = StringUtils.deleteWhitespace(upload.getFileSystemName("Filedata"));

					String newFileSystemName = StringComparer.getInstance()
						.compareForRealPaths(
								fileSystemName.substring(0,
										fileSystemName.length() - 4));
					String newFileSystemExtName = fileSystemName.substring(
						fileSystemName.length() - 4,
						fileSystemName.length()).toLowerCase();

					//trim long names cause cannot output that
					if (newFileSystemName.length() >= 17) {
						newFileSystemName = newFileSystemName.substring(0,16);
					}
				
					String completeName = working_dir + newFileSystemName+"_"+sid;

				
					if (record.equals("yes")) {
						Date d = new Date();
						completeName += "_"+d.getTime();
					} else {
						File f = new File(completeName + newFileSystemExtName);
						if (f.exists()) {
							f.delete();
						}
					}
					
					String fileNameComplete = completeName + newFileSystemExtName;
					ImageIO.write(bi,"jpg",new File(fileNameComplete));
					
					String swfNameComplete = completeName + ".swf";
					log.debug("swfNameComplete: "+swfNameComplete);
					
					
					if (!record.equals("yes")) {
						LinkedHashMap<String,Object> hs = new LinkedHashMap<String,Object>();
						hs.put("user", UsersDaoImpl.getInstance().getUser(users_id));
						hs.put("message", "desktop");
						hs.put("action", "newSlide");
						hs.put("fileName", newFileSystemName+"_"+sid+newFileSystemExtName);
						hs.put("swffileName", newFileSystemName+"_"+sid+".swf");
						
						ScopeApplicationAdapter.getInstance().sendMessageByRoomAndDomain(Long.valueOf(room).longValue(),hs);
					
						
					}
					else
						writeSWFFile(fileNameComplete,swfNameComplete);
				}

		} else {
			log.debug("user not logged in");
		}
	
	
		} catch (Exception e) {
			log.error("ee " + e);
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	//-------------------------------------------------------------------------------------------------------------------------------------
}
