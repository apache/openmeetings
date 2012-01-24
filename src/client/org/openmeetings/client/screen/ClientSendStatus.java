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
package org.openmeetings.client.screen;

import java.awt.Rectangle;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientVirtualScreenBean;
import org.openmeetings.client.util.ClientPacketizer;

/**
 * @author sebastianwagner
 * 
 * @deprecated
 *
 */
public class ClientSendStatus extends BaseSendPacketToServer {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ClientSendStatus.class);

	public void sendStart() throws Exception {
		
		ClientPacketizer packetizer = new ClientPacketizer();
		
		Rectangle rect = new Rectangle(ClientVirtualScreenBean.vScreenSpinnerX,ClientVirtualScreenBean.vScreenSpinnerY,
								ClientVirtualScreenBean.vScreenSpinnerWidth,ClientVirtualScreenBean.vScreenSpinnerHeight);
		
		byte[] packetFrame = packetizer.doPacketize(0,ClientConnectionBean.getFrameNumber(), 
														ClientConnectionBean.publicSID, rect, 0, 0, new byte[0]);
		
		this.sendPacketToServer(packetFrame);
		
	}

}
