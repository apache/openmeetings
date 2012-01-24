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

import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 * 
 * 
 * @deprecated
 *
 */
public class BaseSendPacketToServer {
	
	private static Logger log = Logger.getLogger(BaseSendPacketToServer.class);

	
	protected void sendPacketToServer(byte[] packetFrame) {
		try {
			
			//ClientStartScreen.instance.clientPacketMinaProcess.sendData(packetFrame);
			
			//ClientTransportMinaPool.sendMessage(packetFrame);
			
		} catch (Exception err) {
			
			//ClientStartScreen.instance.showBandwidthWarning("Could not transmit Packets to Server");
			
			log.error("[sendPacketToServer]",err);
		}
	}
	
//	/**
//	 * @param bufferImage
//	 * @param url
//	 * @param fileName
//	 */
//	protected void sendPacketToServer(byte[] packetFrame) {
//		// TODO Auto-generated method stub
//		try {
//			
//			// get a datagram socket
//			   DatagramSocket socket = new DatagramSocket();
//
//			       // send request
//			   //byte[] buf = new byte[256];
//			   
//			   InetAddress address = InetAddress.getByName(ClientConnectionBean.host);
//			   
//			   DatagramPacket packet = new DatagramPacket(packetFrame, packetFrame.length, address, ClientConnectionBean.port);
//			   
//			   socket.setSoTimeout(1000);
//			   
//			   socket.send(packet);
//
//			   byte[] buf = new byte[256];
//			       // get response
//			   packet = new DatagramPacket(buf, buf.length);
//			   socket.receive(packet);
//
//			   // display response
//			   String received = new String(packet.getData(), 0, packet.getLength());
//			   System.out.println("Date of the Moment: " + received);
//
//			   socket.close();
//			
//		} catch (Exception err) {
//			
//			ClientStartScreen.instance.showBandwidthWarning("Could not transmit Packets to Server");
//			
//			log.error("[]",err);
//		}
//	}
	
}
