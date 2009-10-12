package org.openmeetings.client.util;

import java.awt.Rectangle;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;

/**
 * @author sebastianwagner
 * 
 * 
 * create the packet/frame that is going to be send over the wire
 * 
 * 
 * We have to use a max packet size that is smaller than usual Networks cut them!
 * 
 * that means 1400Bytes payload IS MAX
 * 
 * 
 *
 */
public class ClientPacketizer {

	private static Logger log = Logger.getLogger(ClientPacketizer.class);
	
	public byte[] doPacketize(int mode, Integer sequenceNumber, String publicSID, 
			Rectangle rect, int payLoadNumber, int payLoadMax, byte[] payload) {
		try {
			
			Integer lengthPayload = payload.length;
			
			byte[] securityTokenAsByte = publicSID.getBytes();
			
			Integer lengthSecurityToken = securityTokenAsByte.length;
			
			Integer width = Double.valueOf(rect.getWidth()).intValue();
			Integer height = Double.valueOf(rect.getHeight()).intValue();
			
			Integer xValue = Double.valueOf(rect.getX()).intValue();
			Integer yValue = Double.valueOf(rect.getY()).intValue();
			
			//Calculate the overall byte[] size for this frame/packet
			//The first byte is the sequence number :: this number is only for debugging
			//The 6 Bytes are for the length values
			//The seventh,eight == 3 bytes are empty to be filled with data
			//maybe that is needed at a later stage 
			//
			//... to be continued when its fixed and stable
			//
			
			if (lengthPayload > ClientConnectionBean.maxPayLoadSize) {
				throw new Exception("Payload bigger then Max MTU "+lengthPayload);
			}
			
			//Integer frameSize = 4 * 10 + lengthSecurityToken;
			Integer frameSize = 4 * 10 + lengthSecurityToken + lengthPayload;
			
			
			log.debug("mode "+mode);
			log.debug("sequenceNumber "+sequenceNumber);
			log.debug("lengthSecurityToken "+lengthSecurityToken);
			log.debug("xValue "+xValue);
			log.debug("yValue "+yValue);
			log.debug("width "+width);
			log.debug("height "+height);
			log.debug("lengthPayload "+lengthPayload);
			log.debug("publicSID "+publicSID);
			log.debug("payLoadNumber "+payLoadNumber);
			log.debug("payLoadMax "+payLoadMax);
			
			
			//ByteBuffer buffer = ByteBuffer.allocate(frameSize);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			buffer.put(this.convertIntToByteArray(mode));// 4 Byte
			buffer.put(this.convertIntToByteArray(sequenceNumber));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(xValue));//4 Byte
			buffer.put(this.convertIntToByteArray(yValue));//4 Byte
			buffer.put(this.convertIntToByteArray(width));//4 Byte
			buffer.put(this.convertIntToByteArray(height));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthPayload));//4 Byte
			buffer.put(securityTokenAsByte);//32 Byte usually
			
			buffer.put(this.convertIntToByteArray(payLoadNumber));//4 Byte
			buffer.put(this.convertIntToByteArray(payLoadMax));//4 Byte
			
			buffer.put(payload);
			
			byte[] frame = buffer.array();
			
			log.debug("FRAME SIZE "+frame.length);
			
			return frame;
			
		} catch (Exception er) {
			log.error("[Packetizer]",er);
		}
		
		return null;
	}
	
	private byte[] convertIntToByteArray(int val) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(val);
		return buffer.array();
	}
	
}
