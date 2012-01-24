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
package org.openmeetings.client.gui;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;

/**
 * @author sebastianwagner
 *
 */
public class ClientShowStatusMessage {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ClientShowStatusMessage.class);
	
	public static void showStatusMessage(String message) {
		try {
			
			if (ClientConnectionBean.isViewer) {
				
				ClientViewerScreen.instance.showBandwidthWarning(message);
				
			} else {
				
				ClientStartScreen.instance.showBandwidthWarning(message);
				
			}
			
		} catch (Exception err) {
			
		}
	}
	
	public static void doConnectedEvent() {
		try {
			
			if (ClientConnectionBean.isViewer) {
				
				ClientViewerScreen.instance.doInitMessage();
				
			} else {
				
				//No action at the moment
				
			}
			
		} catch (Exception err) {
			
		}
	}

}
