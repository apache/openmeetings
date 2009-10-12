package org.openmeetings.client.screen;

import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.List;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.URL;
import java.awt.image.BufferedImage;

import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.media.Log;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.beans.ClientImageFrame;
import org.openmeetings.client.beans.ClientVirtualScreenBean;
import org.openmeetings.client.gui.ClientStartScreen;
import org.openmeetings.client.transport.ClientTransportMinaPool;
import org.openmeetings.client.util.ClientPacketizer;
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
	
	
	private Date startDate;

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
			this.startDate = new Date();
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
			this.startDate = new Date();
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
		
//		ClientPacketizer packetizer = new ClientPacketizer();
//		
//		for (ClientImageFrame clientImageFrame : clientImageFrames) {
//			
//			if (ClientRasterList.checkFrame(clientImageFrame)) {
//				
//				//Split this Package in the MAX MTU Size for our payload
//				
//				if (clientImageFrame.getPayload().length <= ClientConnectionBean.maxPayLoadSize) {
//				
//					byte[] packetFrame = packetizer.doPacketize(ClientConnectionBean.mode,ClientConnectionBean.getFrameNumber(), 
//											ClientConnectionBean.publicSID, clientImageFrame.getRect(), 0, 1, clientImageFrame.getPayload());
//					
//					this.sendPacketToServer(packetFrame);
//					
//				} else {
//
//					//log.debug("Frames ?? "+ClientConnectionBean.maxPayLoadSize+" "+clientImageFrame.getPayload().length);
//
//					//log.debug("Frames ?? "+Math.floor(ClientConnectionBean.maxPayLoadSize / clientImageFrame.getPayload().length));
//					
//					int frameNumbers = Double.valueOf(Math.floor(clientImageFrame.getPayload().length / ClientConnectionBean.maxPayLoadSize)).intValue();
//					
//					log.debug("frameNumbers "+frameNumbers);
//					
//					for (int i=0;i<frameNumbers;i++) {
//						
//						byte[] packetFrameSplit = this.extractByte(clientImageFrame.getPayload(), i*ClientConnectionBean.maxPayLoadSize, ClientConnectionBean.maxPayLoadSize);
//						
//						
//						byte[] packetFrame = packetizer.doPacketize(ClientConnectionBean.mode,ClientConnectionBean.getFrameNumber(), 
//								ClientConnectionBean.publicSID, clientImageFrame.getRect(), i, frameNumbers, packetFrameSplit);
//		
//						this.sendPacketToServer(packetFrame);
//						
//					}
//					
//					
//				}
//			}
//			
//		}
		
	}
	
	private byte[] extractByte(byte[] incomingByte, int start, int length) throws Exception {
		byte[] newByte = new byte[length];
		
		int index = 0;
		for (int i=start;i<start+length;i++) {
			
			newByte[index] = incomingByte[i];
			
			index++;
		}
		
		return newByte;
	}

//	public ClientImageFrame bufferImage() {
//		try {
//
//			ClientImageFrame clientImageFrame = new ClientImageFrame();
//			
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			
//			Rectangle screenRectangle = new Rectangle(
//					ClientVirtualScreenBean.vScreenSpinnerX,ClientVirtualScreenBean.vScreenSpinnerY,
//					ClientVirtualScreenBean.vScreenSpinnerWidth,ClientVirtualScreenBean.vScreenSpinnerHeight);
//			Robot robot = ClientVirtualScreenBean.robot;
//			if (robot==null) robot = new Robot();
//			//StartScreen.instance.showBandwidthWarning("capture  "+(new java.util.Date())+" and "+screenRectangle);
//			
//			
//			screenRectangle = new Rectangle(200,0,400,400);
//			
//			BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);
//
//			//Scale the image reduce size ... TODO: Later Stage
//			
//			Date endTime = new Date();
//			long timeInSeconds = (endTime.getTime()-this.startDate.getTime())/1000;
//			
//			log.debug("1 buffer start , end , delta "+this.startDate+" "+endTime+" :timeInSeconds: "+timeInSeconds);
//				
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//			JPEGEncodeParam encpar = encoder.getDefaultJPEGEncodeParam(imageScreen);
//			encpar.setQuality(ClientConnectionBean.imgQuality, false);
//			encoder.setJPEGEncodeParam(encpar);
//			encoder.encode(imageScreen);
//			
//			imageScreen.flush();
//						
//			//StartScreen.instance.showBandwidthWarning("capture Date and Size: "+(new java.util.Date())+" and "+out.size());
//			
//			log.debug("LENGHT: "+out.toByteArray().length);
//			
//			clientImageFrame.setPayload(out.toByteArray());
//			clientImageFrame.setRect(screenRectangle);
//			
//			return clientImageFrame;
//
//		} catch (FileNotFoundException e) {
//			System.out.println(e);
//			e.printStackTrace();
//		} catch (IOException ioe) {
//			System.out.println(ioe);
//			ioe.printStackTrace();
//		} catch (Exception e) {
//			System.out.println(e);
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	
	

	
}
