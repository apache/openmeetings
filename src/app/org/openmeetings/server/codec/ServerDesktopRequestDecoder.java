package org.openmeetings.server.codec;


import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerFrameCursorStatus;
import org.openmeetings.server.beans.ServerStatusBean;
import org.openmeetings.server.beans.ServerViewerRegisterBean;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


/**
 * @author sebastianwagner
 *
 */
public class ServerDesktopRequestDecoder extends CumulativeProtocolDecoder {

	private static final Logger log = Red5LoggerFactory.getLogger(ServerDesktopRequestDecoder.class, ScopeApplicationAdapter.webAppRootKey);

	private static final String DECODER_STATE_KEY = ServerDesktopRequestDecoder.class.getName() + ".STATE";

    public static final int MAX_IMAGE_SIZE = 5 * 400 * 400;

    private static class ServerFrameBeanState {

    	private Integer mode = null;//4Byte
    	private Integer sequenceNumber = null;//4Byte
    	private Integer lengthSecurityToken = null;//4Byte
    	private Integer xValue = null;//4Byte
    	private Integer yValue = null;//4Byte
    	private Integer width = null;//4Byte
    	private Integer height = null;//4Byte
    	private Integer lengthPayload = null;//4Byte
    	private String publicSID = null;//read from lengthSecurityToken, but should be 32Byte
    	private byte[] imageBytes = null;//read from lengthPayload
    	
    	//In case its mode 0 or 4 it is a start/stop Packet that has no payload
    	private Integer tileWidth = null;//4 Byte
    	private Integer tileHeight = null;// 4 Byte

    }
    
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		try {
			ServerFrameBeanState serverFrameBeanState = (ServerFrameBeanState) session.getAttribute(DECODER_STATE_KEY);
	        if (serverFrameBeanState == null) {
	        	serverFrameBeanState = new ServerFrameBeanState();
	            session.setAttribute(DECODER_STATE_KEY, serverFrameBeanState);
	        }

	        if (serverFrameBeanState.mode == null) {
	        	// try to set Modus
	            if (in.remaining() >= 4) {
	            	serverFrameBeanState.mode = in.getInt();
	            	//log.debug("mode SET "+serverFrameBeanState.mode);
	            } else {
	            	return false;
	            }
	        }
	        
	        if (serverFrameBeanState.sequenceNumber == null) {
	        	//try to set the sequenceNumber
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.sequenceNumber = in.getInt();
	        		//log.debug("sequenceNumber SET "+serverFrameBeanState.sequenceNumber);
	        	} else {
	            	return false;
	            }
	        }
	        
	        if (serverFrameBeanState.lengthSecurityToken == null) {
	        	//try to set the lengthSecurityToken
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.lengthSecurityToken = in.getInt();
	        		//log.debug("lengthSecurityToken SET "+serverFrameBeanState.lengthSecurityToken);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.xValue == null) {
	        	//try to set the xValue
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.xValue = in.getInt();
	        		//log.debug("xValue SET "+serverFrameBeanState.xValue);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.yValue == null) {
	        	
	        	//log.debug("yValue in.remaining() "+in.remaining());
	        	
	        	//try to set the yValue
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.yValue = in.getInt();
	        		//log.debug("yValue SET "+serverFrameBeanState.yValue);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.width == null) {
	        	
	        	//log.debug("width in.remaining() "+in.remaining());
	        	
	        	//try to set the width
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.width = in.getInt();
	        		//log.debug("width SET "+serverFrameBeanState.width);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.height == null) {
	        	
	        	//log.debug("height in.remaining() "+in.remaining());
	        	
	        	//try to set the height
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.height = in.getInt();
	        		//log.debug("height SET "+serverFrameBeanState.height);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.lengthPayload == null) {
	        	//try to set the lengthPayload
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.lengthPayload = in.getInt();
	        		//log.debug("lengthPayload SET "+serverFrameBeanState.lengthPayload);
	        	} else {
	            	return false;
	            }
	        }

	        if (serverFrameBeanState.publicSID == null) {
	        	//try to set the xValue
	        	
	        	if (in.remaining() >= serverFrameBeanState.lengthSecurityToken) {
	        	//if (in.prefixedDataAvailable(4,serverFrameBeanState.lengthSecurityToken)) {
	        		byte[] byteBuffer = new byte[serverFrameBeanState.lengthSecurityToken];
        			in.get(byteBuffer, 0, serverFrameBeanState.lengthSecurityToken);
	        		serverFrameBeanState.publicSID = new String(byteBuffer);
	        		
	        		log.debug("publicSID SET "+serverFrameBeanState.publicSID);
	        		
	        		if (serverFrameBeanState.mode == 5) {
	        			
	        			ServerViewerRegisterBean serverViewerRegisterBean = new ServerViewerRegisterBean();
	        			
	        			serverViewerRegisterBean.setMode(serverFrameBeanState.mode);
	        			serverViewerRegisterBean.setPublicSID(serverFrameBeanState.publicSID);
	        			
	        			//Write the result to the Handler
		        		out.write(serverViewerRegisterBean);
		        		
		        		//Reset the Buffer Values
		        		serverFrameBeanState.mode = null;
		        		serverFrameBeanState.sequenceNumber = null;
		        		serverFrameBeanState.lengthSecurityToken = null;
						serverFrameBeanState.xValue = null;
						serverFrameBeanState.yValue = null;
						serverFrameBeanState.width = null;
						serverFrameBeanState.height = null;
						serverFrameBeanState.lengthPayload = null;
						serverFrameBeanState.publicSID = null;
						serverFrameBeanState.imageBytes = null;
						serverFrameBeanState.tileHeight = null;
						serverFrameBeanState.tileWidth = null;

		                return true;
		                
	        		} else if (serverFrameBeanState.mode == 6) {
	        			
	        			ServerFrameCursorStatus serverFrameCursorStatus = new ServerFrameCursorStatus();
	        			
	        			serverFrameCursorStatus.setMode(serverFrameBeanState.mode);
	        			serverFrameCursorStatus.setSequenceNumber(serverFrameBeanState.sequenceNumber);
	        			serverFrameCursorStatus.setPublicSID(serverFrameBeanState.publicSID);
	        			serverFrameCursorStatus.setX(serverFrameBeanState.xValue);
	        			serverFrameCursorStatus.setY(serverFrameBeanState.yValue);
	        			
	        			//Write the result to the Handler
		        		out.write(serverFrameCursorStatus);
	        			
	        			//Reset the Buffer Values
		        		serverFrameBeanState.mode = null;
		        		serverFrameBeanState.sequenceNumber = null;
		        		serverFrameBeanState.lengthSecurityToken = null;
						serverFrameBeanState.xValue = null;
						serverFrameBeanState.yValue = null;
						serverFrameBeanState.width = null;
						serverFrameBeanState.height = null;
						serverFrameBeanState.lengthPayload = null;
						serverFrameBeanState.publicSID = null;
						serverFrameBeanState.imageBytes = null;
						serverFrameBeanState.tileHeight = null;
						serverFrameBeanState.tileWidth = null;
	        			
	        			return true;
	        		}
	        		
	        	} else {
	            	return false;
	            }
	        }	        

	        if (serverFrameBeanState.imageBytes == null && 
	        			(serverFrameBeanState.mode == 1 || serverFrameBeanState.mode == 2 || serverFrameBeanState.mode == 3)) {
	        	
	        	//log.debug("in.remaining() "+in.remaining());
	        	//log.debug("serverFrameBeanState.lengthPayload "+serverFrameBeanState.lengthPayload);
	        	
	        	if (in.remaining() >= serverFrameBeanState.lengthPayload) {
	        	//if (in.prefixedDataAvailable(4,serverFrameBeanState.lengthPayload)) {
	        		byte[] byteBuffer = new byte[serverFrameBeanState.lengthPayload];
	        		in.get(byteBuffer, 0, serverFrameBeanState.lengthPayload);
	        		
	        		serverFrameBeanState.imageBytes = byteBuffer;
	        		
	        		ServerFrameBean serverFrameBean = new ServerFrameBean(
	        							serverFrameBeanState.mode, 
	        							serverFrameBeanState.sequenceNumber, 
	        							serverFrameBeanState.lengthSecurityToken, 
	        							serverFrameBeanState.xValue, 
	        							serverFrameBeanState.yValue,
	        							serverFrameBeanState.width, 
	        							serverFrameBeanState.height, 
	        							serverFrameBeanState.lengthPayload,
	        							serverFrameBeanState.publicSID, 
	        							serverFrameBeanState.imageBytes);
	        		
	        		//Write the result to the Handler
	        		out.write(serverFrameBean);
	        		
	        		//Reset the Buffer Values
	        		serverFrameBeanState.mode = null;
	        		serverFrameBeanState.sequenceNumber = null;
	        		serverFrameBeanState.lengthSecurityToken = null;
					serverFrameBeanState.xValue = null;
					serverFrameBeanState.yValue = null;
					serverFrameBeanState.width = null;
					serverFrameBeanState.height = null;
					serverFrameBeanState.lengthPayload = null;
					serverFrameBeanState.publicSID = null;
					serverFrameBeanState.imageBytes = null;
					serverFrameBeanState.tileHeight = null;
					serverFrameBeanState.tileWidth = null;

	                return true;
	                
	        	} else {
	        		return false;
	        	}
	        }
	        
	        if (serverFrameBeanState.tileWidth == null && 
	        		(serverFrameBeanState.mode == 0 || serverFrameBeanState.mode == 4)) {
	        	//try to set the lengthPayload
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.tileWidth = in.getInt();
	        		log.debug("tileWidth SET "+serverFrameBeanState.tileWidth);
	        	} else {
	            	return false;
	            }
	        }
	        
	        if (serverFrameBeanState.tileHeight == null && 
	        		(serverFrameBeanState.mode == 0 || serverFrameBeanState.mode == 4)) {
	        	//try to set the lengthPayload
	        	if (in.remaining() >= 4) {
	        		serverFrameBeanState.tileHeight = in.getInt();
	        		
	        		ServerStatusBean serverStatusBean = new ServerStatusBean();

	        		serverStatusBean.setMode(serverFrameBeanState.mode);
	        		serverStatusBean.setSequenceNumber(serverFrameBeanState.sequenceNumber);
	        		serverStatusBean.setPublicSID(serverFrameBeanState.publicSID);
	        		serverStatusBean.setXValue(serverFrameBeanState.xValue);
	        		serverStatusBean.setYValue(serverFrameBeanState.yValue);
	        		serverStatusBean.setWidth(serverFrameBeanState.width);
	        		serverStatusBean.setHeight(serverFrameBeanState.height);
	        		serverStatusBean.setTileWidth(serverFrameBeanState.tileWidth);
	        		serverStatusBean.setTileHeight(serverFrameBeanState.tileHeight);
	        		
	        		log.debug("+++++++++ Out Buffer Full Status Bean");
	        		
	        		//Write the result to the Handler
	        		out.write(serverStatusBean);
	        		
	        		//Reset the Buffer Values
	        		serverFrameBeanState.mode = null;
	        		serverFrameBeanState.sequenceNumber = null;
	        		serverFrameBeanState.lengthSecurityToken = null;
					serverFrameBeanState.xValue = null;
					serverFrameBeanState.yValue = null;
					serverFrameBeanState.width = null;
					serverFrameBeanState.height = null;
					serverFrameBeanState.lengthPayload = null;
					serverFrameBeanState.publicSID = null;
					serverFrameBeanState.imageBytes = null;
					serverFrameBeanState.tileHeight = null;
					serverFrameBeanState.tileWidth = null;
	        		
	        		return true;
	        		
	        	} else {
	            	return false;
	            }
	        }
			
			return false;
			
		} catch (Exception err) {
			log.error("[doDecode]",err);
		}
		return false;
	}


}
