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

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.util.List;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientCursorStatus;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.beans.ClientImageFrame;
import org.openmeetings.client.transport.ClientTransportMinaPool;
import org.openmeetings.client.util.ClientRaster;
import org.openmeetings.client.util.ClientRasterList;

public class ClientCaptureScreen {
	
	private static Logger log = Logger.getLogger(ClientCaptureScreen.class);
	
	
	//For testing the Preformance of the Compressions Rate
	
	//Test Results on MacBook Pro 1400x900 Pixel
	
	//JPEG 0.8 compression        => Bytes: 245017
	//JPEG 0.8 compression + GZip => Bytes:  51954 => Verify JPEG Decompression!
	//PNG                         => Bytes: 
	//PNG + ZLib                  => Bytes: 
	
	public static long frameCalculated = 0;
	
	
	public static void main(String[] args) {
		//new ClientCaptureScreen();
	}

	/**
	 * 
	 * @param sendSingle
	 *
	 * 14.09.2009 21:43:43
	 * sebastianwagner
	 * 
	 *
	 */
	public ClientCaptureScreen(boolean sendSingle) {
		try {
			log.debug("captureScreenStart");
			//StartScreen.instance.showBandwidthWarning("capture publicSID: "+publicSID);
			ClientConnectionBean.isloading = true;
			if (sendSingle) {
				this.singleScreen();
			} else {
				this.captureScreen();
			}
		} catch (Exception err) {
			log.error("[ClientCaptureScreen]",err);
		}
	}
	
	public ClientCaptureScreen(int mode) {
		try {
			log.debug("captureScreenStart");
			//StartScreen.instance.showBandwidthWarning("capture publicSID: "+publicSID);
			ClientConnectionBean.isloading = true;
			this.captureMode(mode);
			
		} catch (Exception err) {
			log.error("[ClientCaptureScreen]",err);
		}

	}
	
	/**
	 * @param mode
	 */
	private void captureMode(int mode) throws Exception {
		
		List<ClientImageFrame> clientImageFrames = ClientRaster.tileScreen();
		
		for (ClientImageFrame clientImageFrame : clientImageFrames) {
			
			if (ClientRasterList.checkFrame(clientImageFrame)) {
				
				log.debug("SEND NEW FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
				
				ClientFrameBean clientFrameBean = new ClientFrameBean();
				clientFrameBean.setMode(ClientConnectionBean.mode);
				clientFrameBean.setSequenceNumber(ClientConnectionBean.getFrameNumber());
				clientFrameBean.setPublicSID(ClientConnectionBean.publicSID);
				clientFrameBean.setXValue(Double.valueOf(clientImageFrame.getRect().getX()).intValue());
				clientFrameBean.setYValue(Double.valueOf(clientImageFrame.getRect().getY()).intValue());
				clientFrameBean.setHeight(Double.valueOf(clientImageFrame.getRect().getHeight()).intValue());
				clientFrameBean.setWidth(Double.valueOf(clientImageFrame.getRect().getWidth()).intValue());
				clientFrameBean.setImageBytes(clientImageFrame.getPayload());
				
				ClientTransportMinaPool.sendMessage(clientFrameBean);
				
			} else {
				log.debug("NO CHANGE IN THIS FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
			}
			
		}
		
		if (ClientConnectionBean.sendMousePosition){
			ClientCursorStatus clientCursorStatus = new ClientCursorStatus();
			clientCursorStatus.setSequenceNumber(ClientConnectionBean.getFrameNumber());
			clientCursorStatus.setPublicSID(ClientConnectionBean.publicSID);
			
			PointerInfo a = MouseInfo.getPointerInfo();
			Point mouseP = a.getLocation();
			
			Integer x = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * mouseP.getX())).intValue();
			Integer y = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * mouseP.getY())).intValue();
			
			clientCursorStatus.setX(x);
			clientCursorStatus.setY(y);
			
			ClientTransportMinaPool.sendMessage(clientCursorStatus);
		}
		
	}

	/**
	 * For testing
	 * 
	 * @throws Exception
	 */
	public void singleScreen() throws Exception {
		
		List<ClientImageFrame> clientImageFrames = ClientRaster.tileScreen();
		
		ClientImageFrame clientImageFrame = clientImageFrames.get(0);
			
		if (ClientRasterList.checkFrame(clientImageFrame)) {
			
			log.debug("SEND NEW FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
			
			ClientFrameBean clientFrameBean = new ClientFrameBean();
			clientFrameBean.setMode(ClientConnectionBean.mode);
			clientFrameBean.setSequenceNumber(ClientConnectionBean.getFrameNumber());
			clientFrameBean.setPublicSID(ClientConnectionBean.publicSID);
			clientFrameBean.setXValue(Double.valueOf(clientImageFrame.getRect().getX()).intValue());
			clientFrameBean.setYValue(Double.valueOf(clientImageFrame.getRect().getY()).intValue());
			clientFrameBean.setHeight(Double.valueOf(clientImageFrame.getRect().getHeight()).intValue());
			clientFrameBean.setWidth(Double.valueOf(clientImageFrame.getRect().getWidth()).intValue());
			clientFrameBean.setImageBytes(clientImageFrame.getPayload());
			
			ClientTransportMinaPool.sendMessage(clientFrameBean);
			
			//this.sendPacketToServer(packetFrame);
				
		} else {
			log.debug("NO CHANGE IN THIS FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
		}
			
		
	}

	public void captureScreen() throws Exception {
		
		List<ClientImageFrame> clientImageFrames = ClientRaster.tileScreen();
		
		for (ClientImageFrame clientImageFrame : clientImageFrames) {
			
			if (ClientRasterList.checkFrame(clientImageFrame)) {
				
				log.debug("SEND NEW FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
				
				ClientFrameBean clientFrameBean = new ClientFrameBean();
				clientFrameBean.setMode(ClientConnectionBean.mode);
				clientFrameBean.setSequenceNumber(ClientConnectionBean.getFrameNumber());
				clientFrameBean.setPublicSID(ClientConnectionBean.publicSID);
				clientFrameBean.setXValue(Double.valueOf(clientImageFrame.getRect().getX()).intValue());
				clientFrameBean.setYValue(Double.valueOf(clientImageFrame.getRect().getY()).intValue());
				clientFrameBean.setHeight(Double.valueOf(clientImageFrame.getRect().getHeight()).intValue());
				clientFrameBean.setWidth(Double.valueOf(clientImageFrame.getRect().getWidth()).intValue());
				clientFrameBean.setImageBytes(clientImageFrame.getPayload());
				
				ClientTransportMinaPool.sendMessage(clientFrameBean);
				
			} else {
				log.debug("NO CHANGE IN THIS FRAME "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
			}
			
		}
	}
}
