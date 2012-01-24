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
package org.openmeetings.server.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerFrameCursorStatus;
import org.openmeetings.server.beans.ServerSharingSessionBean;
import org.openmeetings.server.beans.ServerStatusBean;
import org.openmeetings.server.socket.ServerSocketMinaProcess;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerSharingSessionList {
	
	private static final Logger log = Red5LoggerFactory.getLogger(ServerSharingSessionList.class, ScopeApplicationAdapter.webAppRootKey);

	private static Map<String,ServerSharingSessionBean> sharingSessions = new HashMap<String,ServerSharingSessionBean>();
	
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	
	/**
	 * 
	 * Starts a new Sharing Session
	 * This can be either a Recording, or sharing or both Session
	 * each Client can only start one type of session
	 * a session is identified by its publicSID (MD5-Hash)
	 * 
	 * @param serverStatusBean
	 */
	public static void startSession (Long sessionId, ServerStatusBean serverStatusBean) {
		try {
			
			log.debug(" -- startSession");
			
			ServerSharingSessionBean serverSharingSessionBean;
			
			if (sharingSessions.containsKey(serverStatusBean.getPublicSID())) {
				
				serverSharingSessionBean = sharingSessions.get(serverStatusBean.getPublicSID());
			
			} else {
				
				serverSharingSessionBean = new ServerSharingSessionBean();
			}
			
			serverSharingSessionBean.setSessionId(sessionId);
			
			serverSharingSessionBean.setDeleted(false);
			
			serverSharingSessionBean.setHeight(serverStatusBean.getHeight());
			serverSharingSessionBean.setWidth(serverStatusBean.getWidth());
			serverSharingSessionBean.setXValue(serverStatusBean.getXValue());
			serverSharingSessionBean.setYValue(serverStatusBean.getYValue());
			
			serverSharingSessionBean.setTileWidth(serverStatusBean.getTileWidth());
			serverSharingSessionBean.setTileHeight(serverStatusBean.getTileHeight());
			
			log.debug("NEw Session with Tile W,H "+serverStatusBean.getTileWidth()+","+serverStatusBean.getTileHeight());
			
			//Duplicated info ;) I Know but it makes the object look more clean
			serverSharingSessionBean.setPublicSID(serverStatusBean.getPublicSID());
			
			serverSharingSessionBean.setSessionStarted(new Date());
			serverSharingSessionBean.setLastUpdate(new Date());
			
			if (serverStatusBean.getMode() == 1) {
				serverSharingSessionBean.setSharing(true);
				serverSharingSessionBean.setRecording(false);
			} else if (serverStatusBean.getMode() == 2) {
				serverSharingSessionBean.setSharing(false);
				serverSharingSessionBean.setRecording(true);
			} else if (serverStatusBean.getMode() == 3) {
				serverSharingSessionBean.setSharing(true);
				serverSharingSessionBean.setRecording(true);
			}
			
			int tileNumberWidth = Double.valueOf(Math.floor(serverStatusBean.getWidth() / serverStatusBean.getTileWidth())).intValue();
			int tileNumberHeight = Double.valueOf(Math.floor(serverStatusBean.getHeight() / serverStatusBean.getTileHeight())).intValue();
			
			int number = 0;
			
			for (int x=0;x<=tileNumberWidth;x++) {
				
				for (int y=0;y<=tileNumberHeight;y++) {
					
					int rect_x = ( x * serverStatusBean.getTileWidth() );
					int rect_y = ( y * serverStatusBean.getTileHeight() );
					
					log.debug("rect_x,rect_y,tileWidth,tileHeight "+rect_x+","+rect_y+","+serverStatusBean.getTileWidth()+","+serverStatusBean.getTileHeight());
					
					int rectWidth = serverStatusBean.getTileWidth();
					int rectHeight = serverStatusBean.getTileHeight();
					
					log.debug("-1- rectWidth,rectHeight"+rectWidth+","+rectHeight);
					
					if (rect_x + rectWidth > serverStatusBean.getWidth()) {
						rectWidth = serverStatusBean.getWidth() - rect_x;
					}
					if (rect_y + rectHeight > serverStatusBean.getHeight()) {
						rectHeight = serverStatusBean.getHeight() - rect_y;
					}
					
					log.debug("-2- rectWidth,rectHeight"+rectWidth+","+rectHeight);
					
					if (rectWidth == 0 || rectHeight == 0) {
						continue;
					}
					number++;
					
				}
			}
			
			log.debug("tileNumberWidth :: tileNumberHeight "+tileNumberWidth+" :: "+tileNumberHeight);
			
			//Reset the Images List to make the Buffer get filled again
			serverSharingSessionBean.setServerFrameBeans(new LinkedList<ServerFrameBean>());
			
			//[[Don't forget to count the leading 0]] outdated comment
			serverSharingSessionBean.setNumberOfImages(number);
			serverSharingSessionBean.setSendCompleteFlag(false);
			
			sharingSessions.put(serverStatusBean.getPublicSID(), serverSharingSessionBean);
				
			
			
		} catch (Exception err) {
			log.error("[startSession] ",err);
		}
	}
	
	public Boolean updateCursorPosition(ServerFrameCursorStatus cPosition) {
		try {
			
			if (sharingSessions.containsKey(cPosition.getPublicSID())) {
				
				//log.debug("found sharingSessions");
				
				ServerSharingSessionBean serverSharingSessionBean = sharingSessions.get(cPosition.getPublicSID());
				
				serverSharingSessionBean.setServerFrameCursorStatus(cPosition);
				
				
				/* #################################
				 * Send Cursor Position to ODSP-Connections
				 * 
				 */
				ServerSocketMinaProcess.sendCursorPositionToSession(cPosition);
				
				/* #################################
				 * Send Cursor to trigger HTTP-Connections
				 * 
				 */
				if (scopeApplicationAdapter != null) {
					scopeApplicationAdapter.sendScreenSharingCursorPos(cPosition);
				}
				
				sharingSessions.put(cPosition.getPublicSID(),serverSharingSessionBean);
				
			} else {
				
				log.debug("Number of Sessions: "+sharingSessions.size());
				
				for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
					log.debug("available Sessions : "+iter.next());
				}
				
				throw new Exception("updateCursorPosition where no Session is - Session already Closed? "+cPosition.getPublicSID());
				
			}
			
		} catch (Exception err) {
			log.error("[updateCursorPosition]",err);
		}
		return false;
	}
	
	
	public Boolean addFrameToSession(ServerFrameBean serverFrameBean) {
		try {
			
			if (sharingSessions.containsKey(serverFrameBean.getPublicSID())) {
				
				ServerSharingSessionBean serverSharingSessionBean = sharingSessions.get(serverFrameBean.getPublicSID());
				
				if (serverSharingSessionBean.isDeleted()) {
					//This happens because packets arrive later then the status message
					//this is quite usual
					log.debug("Session already Stopped");
					return false;
				}
				
				if (serverFrameBean.getMode() == 1) {
					serverSharingSessionBean.setSharing(true);
					serverSharingSessionBean.setRecording(false);
				} else if (serverFrameBean.getMode() == 2) {
					serverSharingSessionBean.setSharing(false);
					serverSharingSessionBean.setRecording(true);
				} else if (serverFrameBean.getMode() == 3) {
					serverSharingSessionBean.setSharing(true);
					serverSharingSessionBean.setRecording(true);
				}
				
				if (serverSharingSessionBean.getServerFrameBeans().size() < serverSharingSessionBean.getNumberOfImages()) {
					
					log.debug("Server Frame Buffer Not yet Full ");
					
					//Check for duplicates
					boolean foundDuplicate = false;
					
					//log.debug("Check :: "+serverFrameBean.getXValue()+"||"+serverFrameBean.getYValue());
					
					//log.debug("Check :: SIZE "+serverSharingSessionBean.getServerFrameBeans().size());
					
					for (ServerFrameBean serverFrameBeanStored : serverSharingSessionBean.getServerFrameBeans()) {
						
						if (serverFrameBeanStored.getXValue().equals(serverFrameBean.getXValue()) && 
								serverFrameBeanStored.getYValue().equals(serverFrameBean.getYValue())) {
							//log.debug("Found Duplicate Before Buffer is NOT full "+serverFrameBeanStored.getXValue()+"||"+serverFrameBeanStored.getYValue());
							foundDuplicate = true;
							serverFrameBeanStored.setImageBytesAsJPEG(serverFrameBean.getImageBytesAsJPEG());
							serverFrameBeanStored.setImageBytes(serverFrameBean.getImageBytes());
							break;
						}
					}
					
					if (!foundDuplicate) {
						//log.debug("Add to Cache "+serverFrameBean.getXValue()+"||"+serverFrameBean.getYValue());
						serverSharingSessionBean.getServerFrameBeans().add(serverFrameBean);
					}
					
					
				} else {
					
					/* ##################################
					 * Buffer Full, there is a complete Screen available
					 *
					 */
					
					//log.debug("Check :: SIZE -1- "+serverSharingSessionBean.getServerFrameBeans().size());
					//log.debug("Check :: SIZE -2- "+serverSharingSessionBean.getNumberOfImages());
					
					if (!serverSharingSessionBean.isSendCompleteFlag()) {
						
						log.debug("SEND Buffer Full Message -1- Start ScreenSharing or Recording for all Clients "+serverSharingSessionBean.getPublicSID());
						
						Date d = new Date();
						
						log.debug("SEND Buffer Full Message -2- Start ScreenSharing or Recording for all Clients "+(d.getTime() - serverSharingSessionBean.getSessionStarted().getTime()));
						
						if (scopeApplicationAdapter != null) {
							scopeApplicationAdapter.sendScreenSharingMessage(serverSharingSessionBean);
						}
						
						if (serverSharingSessionBean.isSharing()) {
							
							
						
						}
						
						if (serverSharingSessionBean.isRecording()) {
							
							//TODO: Add Scheduler to produce Frame capture
							
							//For Testing Write all Images to a standard dir
							//writeImagesToStandardDirectory(serverSharingSessionBean);
							
							
						}
						
						serverSharingSessionBean.setSendCompleteFlag(true);
						
						
					} else {
						
						/* #################################
						 * Send Screen to ODSP-Connections
						 * 
						 */
						ServerSocketMinaProcess.sendMessageToSession(serverFrameBean);
						
						/* #################################
						 * Send Screen to trigger HTTP-Connections
						 * 
						 */
						if (scopeApplicationAdapter != null) {
							scopeApplicationAdapter.sendScreenSharingFrame(serverFrameBean);
						}
						
					}
					
					//Check for duplicates
					boolean foundDuplicate = false;
					
					//log.debug("SEARCH getXValue(),getYValue()"+serverFrameBean.getXValue()+","+serverFrameBean.getYValue());
					
					for (ServerFrameBean serverFrameBeanStored : serverSharingSessionBean.getServerFrameBeans()) {
						
						//log.debug("serverFrameBeanStored.getXValue(),serverFrameBeanStored.getYValue()"+serverFrameBeanStored.getXValue()+","+serverFrameBeanStored.getYValue());
						
						if (serverFrameBeanStored.getXValue().equals(serverFrameBean.getXValue()) && 
								serverFrameBeanStored.getYValue().equals(serverFrameBean.getYValue())) {
							//log.debug("Found Duplicate Before Buffer is full");
							foundDuplicate = true;
							serverFrameBeanStored.setImageBytesAsJPEG(serverFrameBean.getImageBytesAsJPEG());
							serverFrameBeanStored.setImageBytes(serverFrameBean.getImageBytes());
							break;
						}
					}
					
					if (!foundDuplicate) {
						throw new Exception("Could Not Found Session for Frame "+serverFrameBean.getXValue()+"||"+serverFrameBean.getYValue()+"::"+serverFrameBean.getPublicSID());
					}
					
				}
				
				sharingSessions.put(serverFrameBean.getPublicSID(),serverSharingSessionBean);
				
			} else {
				
				log.debug("Number of Sessions: "+sharingSessions.size());
				
				for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
					log.debug("available Sessions : "+iter.next());
				}
				
				throw new Exception("addFrameToSession where no Session is - Session already Closed? "+serverFrameBean.getPublicSID());
				
			}
			
			
		} catch (Exception err) {
			log.error("[addFrameToSession] ",err);
		}
		return null;
	}
	
	public static synchronized ServerSharingSessionBean getServerSharingSessionBeanByPublicSID(String publicSID) {
		try {
			
			if (sharingSessions.containsKey(publicSID)) {
				return sharingSessions.get(publicSID);
			} else {
				
				log.debug("Number of Sessions: "+sharingSessions.size());
				
//				for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
//					log.debug("available Sessions : "+iter.next());
//				}
//				
//				throw new Exception("No Session found for Servlet Request "+publicSID);
			}
			
		} catch (Exception err) {
			log.error("[getServerSharingSessionBeanByPublicSID]",err);
		}
		return null;
	}

	public static synchronized ServerFrameBean getFrameByPublicSID(String publicSID, Integer x, Integer y) {
		try {
			
			if (sharingSessions.containsKey(publicSID)) {
				ServerSharingSessionBean serverSharingSessionBean = sharingSessions.get(publicSID);
				
				boolean found = false;
				
				for (ServerFrameBean serverFrameBean : serverSharingSessionBean.getServerFrameBeans()) {
					
					if (serverFrameBean.getXValue().equals(x) && serverFrameBean.getYValue().equals(y)) {
						
						found = true;
						return serverFrameBean;
						
					}
					
				}
				
				if (!found) {
					throw new Exception("No Frame found for Servlet Request "+publicSID+" x,y "+x+","+y);
				}
				
			} else {
				
				log.debug("Number of Sessions: "+sharingSessions.size());
				
				for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
					log.debug("available Sessions : "+iter.next());
				}
				
				throw new Exception("No Session found for Servlet Request "+publicSID);
			}
			
		} catch (Exception err) {
			log.error("[getServerSharingSessionBeanByPublicSID]",err);
		}
		return null;
	}


	public synchronized void stopSession(Long sessionId) {
		try {
			
			for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
				String publicSID = iter.next();
				ServerSharingSessionBean serverSharingSessionBean = sharingSessions.get(publicSID);
				
				if (serverSharingSessionBean.getSessionId().equals(sessionId)) {
					
					/* #################################
					 * Send Screen to ODSP-Connections
					 * 
					 */
					ServerStatusBean serverStatusBean = new ServerStatusBean();
					serverStatusBean.setMode(4);
					serverStatusBean.setHeight(0);
					serverStatusBean.setWidth(0);
					serverStatusBean.setPublicSID(publicSID);
					serverStatusBean.setSequenceNumber(0);
					serverStatusBean.setTileHeight(0);
					serverStatusBean.setTileWidth(0);
					serverStatusBean.setXValue(0);
					serverStatusBean.setYValue(0);
					ServerSocketMinaProcess.sendStatusToSession(serverStatusBean);
					
					/* #################################
					 * Send Screen to trigger HTTP-Connections
					 * 
					 */
					if (scopeApplicationAdapter != null) {
						serverSharingSessionBean.setDeleted(true);
						scopeApplicationAdapter.sendScreenSharingMessage(serverSharingSessionBean);
					}
					
					log.debug("Mark session as Deleted");
					
					sharingSessions.put(publicSID,serverSharingSessionBean);
					
					break;
				}
				
			}
			
		} catch (Exception err) {
			log.error("[removeSession]",err);
		}
	}
	
	/**
	 * @param sessionId
	 */
	public synchronized void removeSession(Long sessionId) {
		try {
			
			for (Iterator<String> iter = sharingSessions.keySet().iterator();iter.hasNext();) {
				String publicSID = iter.next();
				ServerSharingSessionBean serverSharingSessionBean = sharingSessions.get(publicSID);
				
				if (serverSharingSessionBean.getSessionId().equals(sessionId)) {
					
					/* #################################
					 * Send Screen to ODSP-Connections
					 * 
					 */
					ServerStatusBean serverStatusBean = new ServerStatusBean();
					serverStatusBean.setMode(4);
					serverStatusBean.setHeight(0);
					serverStatusBean.setWidth(0);
					serverStatusBean.setPublicSID(publicSID);
					serverStatusBean.setSequenceNumber(0);
					serverStatusBean.setTileHeight(0);
					serverStatusBean.setTileWidth(0);
					serverStatusBean.setXValue(0);
					serverStatusBean.setYValue(0);
					ServerSocketMinaProcess.sendStatusToSession(serverStatusBean);
					
					/* #################################
					 * Send Screen to trigger HTTP-Connections
					 * 
					 */
					if (scopeApplicationAdapter != null) {
						serverSharingSessionBean.setDeleted(true);
						scopeApplicationAdapter.sendScreenSharingMessage(serverSharingSessionBean);
					}
					
					log.debug("Session Found To Be removed");
					
					sharingSessions.remove(publicSID);
					
					break;
				}
				
			}
			
		} catch (Exception err) {
			log.error("[removeSession]",err);
		}
	}
	
	
}
