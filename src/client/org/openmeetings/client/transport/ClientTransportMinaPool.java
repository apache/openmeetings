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
package org.openmeetings.client.transport;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.gui.ClientShowStatusMessage;

/**
 * @author sebastianwagner
 *
 */
public class ClientTransportMinaPool {
	
	private static Logger log = Logger.getLogger(ClientTransportMinaPool.class);

	private static Map<Integer,ClientPacketMinaProcess> socketConnections = new HashMap<Integer,ClientPacketMinaProcess>();
	
	public static void startConnections() {
		try {
			socketConnections.put(0,new ClientPacketMinaProcess(ClientConnectionBean.port,0));
		
			log.debug("socketConnections -1- "+socketConnections.size());
			
			ClientShowStatusMessage.doConnectedEvent();
			
		} catch (Exception err) {
			log.error("[startConnections]",err);
		}
	}
	
	public static void sendMessage(Object clientBean) throws Exception {
		ClientPacketMinaProcess cProcess = socketConnections.get(0);
		
		cProcess.sendData(clientBean);
		
	}
	
	public static void closeSession() throws Exception {
		try {
		
			ClientPacketMinaProcess cProcess = socketConnections.get(0);
			
			cProcess.closeSession();
		
		} catch (Exception err) {
			log.error("[closeSession]",err);
		}
		
	}
	
}
