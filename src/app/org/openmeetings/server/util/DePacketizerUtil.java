package org.openmeetings.server.util;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

//import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;
import org.openmeetings.server.beans.ServerFrameBean;

/**
 * @author sebastianwagner
 *
 */
public class DePacketizerUtil {
	
	private static final Logger log = Logger.getLogger(DePacketizerUtil.class);
	//private static Logger log = Logger.getLogger(DePacketizerUtil.class);
	
	public ServerFrameBean handleReceivingBytes(byte[] incomingBytes) {
		try {
			
			log.debug("LENGTH incomingBytes "+incomingBytes.length);
			
			throw new Exception("This Class is deprecated use the Protocol Codec Filter");
			
//			byte[] dst_mode = this.extractByte(incomingBytes,0,4);
//			Integer mode = this.convertByteArrayToInt(dst_mode);
//			log.debug("mode "+mode);
//			
//			byte[] dst = this.extractByte(incomingBytes,4,4);
//			Integer sequenceNumber = this.convertByteArrayToInt(dst);
//			log.debug("sequenceNumber "+sequenceNumber);
//			  
//			byte[] dst_lengthSecurityToken = this.extractByte(incomingBytes,8,4);
//			Integer lengthSecurityToken = this.convertByteArrayToInt(dst_lengthSecurityToken);
//			log.debug("lengthSecurityToken "+lengthSecurityToken);
//			
//			byte[] dst_xValue = this.extractByte(incomingBytes,12,4);
//			Integer xValue = this.convertByteArrayToInt(dst_xValue);
//			log.debug("xValue "+xValue);
//			
//			byte[] dst_yValue = this.extractByte(incomingBytes,16,4);
//			Integer yValue = this.convertByteArrayToInt(dst_yValue);
//			log.debug("yValue "+yValue);
//
//			byte[] dst_width = this.extractByte(incomingBytes,20,4);
//			Integer width = this.convertByteArrayToInt(dst_width);
//			log.debug("width "+width);
//			
//			byte[] dst_height = this.extractByte(incomingBytes,24,4);
//			Integer height = this.convertByteArrayToInt(dst_height);
//			log.debug("height "+height);
//
//			byte[] dst_lengthPayload = this.extractByte(incomingBytes,28,4);
//			Integer lengthPayload = this.convertByteArrayToInt(dst_lengthPayload);
//			log.debug("lengthPayload "+lengthPayload);
//
////			byte[] dst_securityToken = this.extractByte(incomingBytes,32,lengthSecurityToken);
////			String publicSID = new String(dst_securityToken);
////			log.debug("publicSID "+publicSID);
//			
//			//byte[] imageBytes = this.extractByte(incomingBytes,32 + lengthSecurityToken,lengthPayload);
//			
//			byte[] imageBytes = new byte[0];
//			
//			//this.writeImageToDisc(imageBytes);
//			return new ServerFrameBean(mode, sequenceNumber, lengthSecurityToken, xValue, yValue,
//											width, height, lengthPayload, "1", imageBytes);
//			
		} catch (Exception err) {
			log.error("[handleReceivingBytes]",err);
		}
		return null;
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
	
	/**
	 * For testing and debug issues
	 * 
	 * @param imageBytes
	 */
	private void writeImageToDisc(byte[] imageBytes) {
		try {
			
			Date t = new Date();
			
			FileOutputStream fos = new FileOutputStream("myImage"+t.getTime()+".jpg");
	
			fos.write(imageBytes, 0, imageBytes.length);
	
			fos.close();
			
		} catch (Exception err) {
			log.error("[writeImageToDisc]",err);
		}
	}
	
	private int convertByteArrayToInt(byte[] bytebuf) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(bytebuf);
		return buffer.getInt(0);
	}

}
