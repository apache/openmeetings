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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerSharingViewerBean;
import org.openmeetings.server.beans.ServerViewerRegisterBean;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerSharingViewersList {

	private static final Logger log = Red5LoggerFactory.getLogger(ServerSharingSessionList.class, ScopeApplicationAdapter.webAppRootKey);

	private static Map<String,List<ServerSharingViewerBean>> viewerSessions = new HashMap<String,List<ServerSharingViewerBean>>();
	/**
	 * @param sessionId
	 * @param message
	 */
	public static synchronized void addNewViewerClient(Long sessionId,
			ServerViewerRegisterBean message) {
		try {
			
			log.debug("ADD client to viewer Session "+message.getPublicSID());
			
			if (viewerSessions.containsKey(message.getPublicSID())) {
				
				viewerSessions.get(message.getPublicSID()).add(new ServerSharingViewerBean(sessionId, message.getPublicSID()));
				
			} else {
				
				List<ServerSharingViewerBean> viewerBeansList = new LinkedList<ServerSharingViewerBean>();
				viewerBeansList.add(new ServerSharingViewerBean(sessionId, message.getPublicSID()));
				
				viewerSessions.put(message.getPublicSID(), viewerBeansList);
				
			}
		
		} catch (Exception err) {
			log.error("[addNewViewerClient]",err);
		}
	}
	
	public static List<ServerSharingViewerBean> getViewersByPublicSID(String publicSID){
		try {
			
			if (viewerSessions.containsKey(publicSID)) {
				
				return viewerSessions.get(publicSID);
				
			} else {
				//Just for debugging - remove that in prod cause it will throw millions of messages
				
				//log.debug("No Viewer registered for this publicSID "+publicSID);
				
			}
			
		} catch (Exception err) {
			log.error("[getViewersByPublicSID]",err);
		}
		return null;
	}

	/**
	 * @param sessionId
	 */
	public static synchronized void removeSession(Long sessionId) {
		try {
			
			log.debug("Close Viewer Session ");
			
			for (Iterator<String> iter = viewerSessions.keySet().iterator();iter.hasNext();) {
				String publicSID = iter.next();
				List<ServerSharingViewerBean> serverSharingViewers = viewerSessions.get(publicSID);
				
				for (ServerSharingViewerBean serverSharingViewerBean : serverSharingViewers) {
					
					if (serverSharingViewerBean.getSessionId().equals(sessionId)) {
						
						log.debug("##### REMOTE VIEWER FOUND TO CLOSE");
						
						serverSharingViewers.remove(serverSharingViewerBean);
					}
					
				}
				
				viewerSessions.put(publicSID, serverSharingViewers);
				
			}
			
		} catch (Exception err) {
			log.error("[removeSession]",err);
		}
	}

	
}
