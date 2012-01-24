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
package org.openmeetings.client.codec;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientCursorStatus;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.beans.ClientStatusBean;
import org.openmeetings.client.beans.ClientViewerRegisterBean;
import org.openmeetings.client.gui.ClientStartScreen;
import org.openmeetings.client.screen.ClientCaptureScreen;
import org.openmeetings.client.util.ClientPacketizer;


/**
 * @author sebastianwagner
 *
 */
public class ClientDesktopRequestEncoder implements ProtocolEncoder {

	private static Logger log = Logger.getLogger(ClientPacketizer.class);

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    	log.debug("DesktopRequestEncoder "+message.getClass().getName());
    	
    	if (message instanceof ClientFrameBean) {
    		
	    	ClientFrameBean clientFrameBean = (ClientFrameBean) message;
	    	
	    	Integer lengthPayload = clientFrameBean.getImageBytes().length;
			
			byte[] securityTokenAsByte = clientFrameBean.getPublicSID().getBytes();
			
			Integer lengthSecurityToken = securityTokenAsByte.length;
			
			Integer frameSize = 4 * 8 + lengthSecurityToken + lengthPayload;
			
	//		log.debug("mode "+request.getMode());
	//		log.debug("sequenceNumber "+request.getSequenceNumber());
	//		log.debug("lengthSecurityToken "+lengthSecurityToken);
	//		log.debug("xValue "+xValue);
	//		log.debug("yValue "+yValue);
	//		log.debug("width "+width);
	//		log.debug("height "+height);
	//		log.debug("lengthPayload "+lengthPayload);
	//		log.debug("publicSID "+request.getPublicSID());
	    	
			IoBuffer buffer = IoBuffer.allocate(frameSize, false);
			
			//mode is weather 1(sharing) or 2(recording) or 3(sharing + recording)
			buffer.put(this.convertIntToByteArray(clientFrameBean.getMode()));// 4 Byte 
			
			buffer.put(this.convertIntToByteArray(clientFrameBean.getSequenceNumber()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(clientFrameBean.getXValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientFrameBean.getYValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientFrameBean.getWidth()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientFrameBean.getHeight()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthPayload));//4 Byte
			buffer.put(securityTokenAsByte);//32 Byte usually
			buffer.put(clientFrameBean.getImageBytes());
			
			log.debug("FRAME SIZE "+ClientCaptureScreen.frameCalculated+"||"+frameSize+"||"+buffer.array().length);
	
	//        IoBuffer buffer = IoBuffer.allocate(12, false);
	//
	//        buffer.putInt(request.getWidth());
	//
	//        buffer.putInt(request.getHeight());
	//
	//        buffer.putInt(request.getNumberOfCharacters());
	//
	//        buffer.flip();
			
			//byte counter
			ClientCaptureScreen.frameCalculated += frameSize;
			if (ClientStartScreen.instance!=null) {
				ClientStartScreen.instance.updateScreen();
			}
			
			buffer.flip();
			
	        out.write(buffer);
        
    	} else if (message instanceof ClientStatusBean) {
    		
    		ClientStatusBean clientStatusBean = (ClientStatusBean) message;
    		
    		byte[] securityTokenAsByte = clientStatusBean.getPublicSID().getBytes();
    		
    		Integer lengthSecurityToken = securityTokenAsByte.length;
    		
    		Integer frameSize = 4 * 10 + lengthSecurityToken;
    		
    		IoBuffer buffer = IoBuffer.allocate(frameSize, false);
    		
    		//mode is weather 0(start) or 4(stop)
			buffer.put(this.convertIntToByteArray(clientStatusBean.getMode()));// 4 Byte 
			buffer.put(this.convertIntToByteArray(ClientConnectionBean.getFrameNumber()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getXValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getYValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getWidth()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getHeight()));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte => this data is not needed
			buffer.put(securityTokenAsByte);//32 Byte usually
			buffer.put(this.convertIntToByteArray(clientStatusBean.getTileWidth()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getTileHeight()));//4 Byte
			
    		//byte counter
			ClientCaptureScreen.frameCalculated += frameSize;
			if (ClientStartScreen.instance!=null) {
				ClientStartScreen.instance.updateScreen();
			}
			
			buffer.flip();
			
	        out.write(buffer);
    		
    	} else if (message instanceof ClientViewerRegisterBean) {
    		
    		ClientViewerRegisterBean clientViewerRegisterBean = (ClientViewerRegisterBean) message;
    		
    		
    		byte[] securityTokenAsByte = clientViewerRegisterBean.getPublicSID().getBytes();
    		
    		Integer lengthSecurityToken = securityTokenAsByte.length;
    		
    		Integer frameSize = 4 * 8 + lengthSecurityToken;
    		
    		IoBuffer buffer = IoBuffer.allocate(frameSize, false);
    		
    		//mode is 5
			buffer.put(this.convertIntToByteArray(clientViewerRegisterBean.getMode()));// 4 Byte 
			buffer.put(this.convertIntToByteArray(ClientConnectionBean.getFrameNumber()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => this data is not needed
			buffer.put(securityTokenAsByte);//32 Byte usually
			
			//byte counter
			ClientCaptureScreen.frameCalculated += frameSize;
			if (ClientStartScreen.instance!=null) {
				ClientStartScreen.instance.updateScreen();
			}
			
			buffer.flip();
			
	        out.write(buffer);
			
    	} else if (message instanceof ClientCursorStatus) {
    		
    		ClientCursorStatus clientCursorStatus = (ClientCursorStatus) message;
    		
    		byte[] securityTokenAsByte = clientCursorStatus.getPublicSID().getBytes();
    		
    		Integer lengthSecurityToken = securityTokenAsByte.length;
    		
    		Integer frameSize = 4 * 8 + lengthSecurityToken;
    		
    		IoBuffer buffer = IoBuffer.allocate(frameSize, false);
    		
    		//mode is 6
			buffer.put(this.convertIntToByteArray(clientCursorStatus.getMode()));// 4 Byte 
			buffer.put(this.convertIntToByteArray(ClientConnectionBean.getFrameNumber()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(clientCursorStatus.getX()));//4 Byte => X-Position
			buffer.put(this.convertIntToByteArray(clientCursorStatus.getY()));//4 Byte => Y-Position
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => Empty
			buffer.put(this.convertIntToByteArray(0));//4 Byte => this data is not needed
			buffer.put(securityTokenAsByte);//32 Byte usually
			
			//byte counter
			ClientCaptureScreen.frameCalculated += frameSize;
			if (ClientStartScreen.instance!=null) {
				ClientStartScreen.instance.updateScreen();
			}
			
			buffer.flip();
			
	        out.write(buffer);
			
    	}

    }

    private byte[] convertIntToByteArray(int val) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(val);
		return buffer.array();
	}

    public void dispose(IoSession session) throws Exception {

        // nothing to dispose

    }



}
