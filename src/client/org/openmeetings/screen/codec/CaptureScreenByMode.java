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
package org.openmeetings.screen.codec;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.transport.ClientTransportMinaPool;
import org.openmeetings.screen.codec.beans.ClientFrameBuffer;
import org.openmeetings.screen.codec.beans.ClientImageFrameModus;

public class CaptureScreenByMode {
	
	private static Logger log = Logger.getLogger(CaptureScreenByMode.class);
	
	private static final int bufferSize = 3;
	
	private static List<ClientFrameBuffer> frameBuffer = new LinkedList<ClientFrameBuffer>();
	
	/**
	 * 
	 * 
	 * 1) Fill Buffer
	 * 2) Check Buffer Full
	 * 3) Do comparison => switch buffer index
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * each Buffer Pool is one second 
	 * so the PRE buffer is one second
	 * => we buffer always 1 second, analyze 
	 * that data and then send it
	 * 
	 * Buffer 1 
	 * Buffer 2
	 * Buffer 3
	 * Buffer 4,na
	 * Buffer 5,na
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4 && buffer1 != buffer5
	 * => send buffer1 with 0.1 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4
	 * => send buffer1 with 0.2 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3
	 * => send buffer1 with 0.4 quality
	 * 
	 * buffer1 != buffer2 
	 * => send buffer1 with 0.6 quality
	 * 
	 * buffer1 == buffer2
	 * => send nothing
	 * 
	 * => switch buffers in one direction
	 * 
	 */
	
	/**
	 * 
	 *
	 * 24.09.2009 10:24:43
	 * sebastianwagner
	 * 
	 *
	 */
	public CaptureScreenByMode() {
		try {
			
			//log.debug("CaptureScreenByMode :: "+mode);
			
			
			//Get the current Screen
			this.captureMode();
			
		} catch (Exception err) {
			log.error("[ClientCaptureScreen]",err);
		}
	}
	
	/**
	 * @param mode
	 */
	private void captureMode() throws Exception {
		
		List<ClientImageFrameModus> clientImageFrameByModues = ClientRasterByMode.tileScreen();
		
		
		//Fill the buffer with Images
		for (ClientImageFrameModus clientImageFrameModus : clientImageFrameByModues) {
			
			this.checkAndAddFrame(clientImageFrameModus);
			
		}
		
		//Analyze the buffer 
		for (ClientFrameBuffer clientFrameBuffer : frameBuffer) {
			
			
			log.debug("Buffer Size "+clientFrameBuffer.getBuffer().size());
			
			if (clientFrameBuffer.getBuffer().size() >= bufferSize) {
				
				log.debug("Buffer is Full for Tile "+clientFrameBuffer.getRect().getX()+" "+clientFrameBuffer.getRect().getY());
				
				byte[] bImage1 = clientFrameBuffer.getBuffer().get(0).getPayload();
				byte[] bImage2 = clientFrameBuffer.getBuffer().get(1).getPayload();
				byte[] bImage3 = clientFrameBuffer.getBuffer().get(2).getPayload();
//				BufferedImage bImage4 = clientFrameBuffer.getBuffer().get(3).getImage();
//				BufferedImage bImage5 = clientFrameBuffer.getBuffer().get(4).getImage();
				
				if (bImage1.length != bImage2.length) {
					
					//Is equal, send nothing
					log.debug("Is equal, send nothing "+clientFrameBuffer.getRect().getX()+" "+clientFrameBuffer.getRect().getY());
					
				} else {
					
					log.debug("baseImage1 IS NOT EQUAL baseImage2");
					
					if (bImage2.length != bImage3.length) {
						
						//Send with Quality of 0.6
						this.sendByteBuffer(clientFrameBuffer.getBuffer().get(0), 4);
						
					} else {
						
						log.debug("bImage2 IS NOT EQUAL bImage3");
						this.sendByteBuffer(clientFrameBuffer.getBuffer().get(0), 3);
						
					}
				}
				
				
			} else if (clientFrameBuffer.getBuffer().size() == 1) {
				
				log.debug("SEND INITIALLY "+clientFrameBuffer.getRect().getX()+" "+clientFrameBuffer.getRect().getY());
				
				//Initially send them all
				this.sendByteBuffer(clientFrameBuffer.getBuffer().get(0), 3);
				
			}
			
			
		}
		
	}
	
	private void sendByteBuffer(ClientImageFrameModus clientImageFrameModus, int modus) {
		try {
			
			byte[] payload = null;
			
			if (modus == 3) {
				payload = clientImageFrameModus.getPayload();
			} else {
				log.debug("Do not send this Image");
				payload = clientImageFrameModus.getPayload();
				
				//payload = clientImageFrameModus.getPayload();
			}
			
			//GZip
			ByteArrayOutputStream byteGzipOut = new ByteArrayOutputStream();
	    	GZIPOutputStream gZipOut = new GZIPOutputStream(byteGzipOut);
	    	
	    	gZipOut.write(payload);
	    	gZipOut.close();
	    	
	    	log.debug("SEND NEW FRAME "+clientImageFrameModus.getRect().getX()+" "+clientImageFrameModus.getRect().getY()+" q:"+modus);
			
			ClientFrameBean clientFrameBean = new ClientFrameBean();
			
			clientFrameBean.setMode(ClientConnectionBean.mode);
			clientFrameBean.setSequenceNumber(ClientConnectionBean.getFrameNumber());
			clientFrameBean.setPublicSID(ClientConnectionBean.publicSID);
			clientFrameBean.setXValue(Double.valueOf(clientImageFrameModus.getRect().getX()).intValue());
			clientFrameBean.setYValue(Double.valueOf(clientImageFrameModus.getRect().getY()).intValue());
			clientFrameBean.setHeight(Double.valueOf(clientImageFrameModus.getRect().getHeight()).intValue());
			clientFrameBean.setWidth(Double.valueOf(clientImageFrameModus.getRect().getWidth()).intValue());
			clientFrameBean.setImageBytes(byteGzipOut.toByteArray());
			
			ClientTransportMinaPool.sendMessage(clientFrameBean);
			
			
		} catch (Exception err) {
			log.error("[sendByteBufferAsJpeg]",err);
		}
	}
	
	private void checkAndAddFrame(ClientImageFrameModus clientImageFrameModus) {
		try {
			
			boolean foundRect= false;
			
			for (ClientFrameBuffer clientFrameBuffer : frameBuffer) {
				
				if (clientFrameBuffer.getRect().getX() == clientImageFrameModus.getRect().getX() 
						&& clientFrameBuffer.getRect().getY() == clientImageFrameModus.getRect().getY()) {
					log.debug("FOUND SAME POSITION");
					foundRect = true;
					
					
					//Weather this buffer is already full then we need to slice the first and add it 
					//to the end or the buffer is not yet full, then we just add it to the end
					if (clientFrameBuffer.getBuffer().size() >= bufferSize) {
						clientFrameBuffer.getBuffer().remove(0);
					}
					
					clientFrameBuffer.getBuffer().add(clientImageFrameModus);
					
				}
				
			}
			
			if (!foundRect) {
				log.debug("TILE NOT in Buffer: "+clientImageFrameModus.getRect().getX()+" "+clientImageFrameModus.getRect().getY());
				ClientFrameBuffer clientFrameBuffer = new ClientFrameBuffer();
				clientFrameBuffer.getBuffer().add(clientImageFrameModus);
				clientFrameBuffer.setRect(clientImageFrameModus.getRect());
				frameBuffer.add(clientFrameBuffer);
			}
			
		} catch (Exception err) {
			log.error("[checkAndAddFrame]",err);
		}
	}
	
	
}
