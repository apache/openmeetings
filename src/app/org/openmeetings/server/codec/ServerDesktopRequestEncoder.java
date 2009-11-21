package org.openmeetings.server.codec;

import java.nio.ByteBuffer;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerFrameCursorStatus;
import org.openmeetings.server.beans.ServerStatusBean;

import java.util.zip.*;


/**
 * @author sebastianwagner
 *
 */
public class ServerDesktopRequestEncoder implements ProtocolEncoder {

	private static Logger log = Red5LoggerFactory.getLogger(ServerDesktopRequestEncoder.class, "openmeetings");

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    	log.debug("DesktopRequestEncoder "+message.getClass().getName());
    	
    	if (message instanceof ServerFrameBean) {
    		
    		ServerFrameBean clientFrameBean = (ServerFrameBean) message;
	    	
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
			
	//        IoBuffer buffer = IoBuffer.allocate(12, false);
	//
	//        buffer.putInt(request.getWidth());
	//
	//        buffer.putInt(request.getHeight());
	//
	//        buffer.putInt(request.getNumberOfCharacters());
	//
	//        buffer.flip();
			
			buffer.flip();
			
	        out.write(buffer);
        
    	} else if (message instanceof ServerStatusBean) {
    		
    		ServerStatusBean clientStatusBean = (ServerStatusBean) message;
    		
    		byte[] securityTokenAsByte = clientStatusBean.getPublicSID().getBytes();
    		
    		Integer lengthSecurityToken = securityTokenAsByte.length;
    		
    		Integer frameSize = 4 * 10 + lengthSecurityToken;
    		
    		IoBuffer buffer = IoBuffer.allocate(frameSize, false);
    		
    		//mode is weather 0(start) or 4(stop)
			buffer.put(this.convertIntToByteArray(clientStatusBean.getMode()));// 4 Byte 
			buffer.put(this.convertIntToByteArray(0));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getXValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getYValue()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getWidth()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getHeight()));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte => this data is not needed
			buffer.put(securityTokenAsByte);//32 Byte usually
			buffer.put(this.convertIntToByteArray(clientStatusBean.getTileWidth()));//4 Byte
			buffer.put(this.convertIntToByteArray(clientStatusBean.getTileHeight()));//4 Byte
			
			buffer.flip();
			
	        out.write(buffer);
    		
    	} else if (message instanceof ServerFrameCursorStatus) {
    		
    		ServerFrameCursorStatus cursorStatus = (ServerFrameCursorStatus) message;
    		
    		byte[] securityTokenAsByte = cursorStatus.getPublicSID().getBytes();
    		
    		Integer lengthSecurityToken = securityTokenAsByte.length;
    		
    		Integer frameSize = 4 * 8 + lengthSecurityToken;
    		
    		IoBuffer buffer = IoBuffer.allocate(frameSize, false);
    		
    		log.debug("######################### 1Send Cursor Bean "+cursorStatus.getMode());
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### Send Cursor Bean");
    		log.debug("######################### 9Send Cursor Bean");
    		
    		//mode is 6
			buffer.put(this.convertIntToByteArray(cursorStatus.getMode()));// 4 Byte 
			buffer.put(this.convertIntToByteArray(cursorStatus.getSequenceNumber()));//4 Byte
			buffer.put(this.convertIntToByteArray(lengthSecurityToken));//4 Byte
			buffer.put(this.convertIntToByteArray(cursorStatus.getX()));//4 Byte
			buffer.put(this.convertIntToByteArray(cursorStatus.getY()));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte
			buffer.put(this.convertIntToByteArray(0));//4 Byte => this data is not needed
			buffer.put(securityTokenAsByte);//32 Byte usually
			
			buffer.flip();
			
	        //out.write(buffer);
    		
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
