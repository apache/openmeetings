package org.openmeetings.server.socket;

//import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerStatusBean;
import org.openmeetings.server.cache.ServerSharingSessionList;
import org.openmeetings.server.util.DePacketizerUtil;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerPacketHandler {
	
	private static final Logger log = Logger.getLogger(ServerPacketHandler.class);
	//private static Logger log = Logger.getLogger(ServerPacketHandler.class);

	/**
	 * @param data
	 * 
	 * @deprecated
	 * 
	 */
	public void _handleIncomingByte(byte[] data) {
		try {
			
			DePacketizerUtil depacketizerUtil = new DePacketizerUtil();
			
			if (data.length>1024) {
				
				log.debug("Bigger Package "+data.length+" Number "+(data.length/1024));
				
				int frameNumber = Double.valueOf(Math.floor((data.length/1024))).intValue();
				
				for (int i=0;i<frameNumber;i++) {
					
					byte[] newPacket = this.extractByte(data, i, 1024);
					
					ServerFrameBean serverFrameBean = depacketizerUtil.handleReceivingBytes(newPacket);
					
					log.debug("MODE :: i: "+i+" MODE"+serverFrameBean.getMode());
					
				}
				
				
			} else {
				
				
				ServerFrameBean serverFrameBean = depacketizerUtil.handleReceivingBytes(data);
				
				log.debug("MODE :: "+serverFrameBean.getMode());
			}
            
			
			
			
			
//			if (serverFrameBean.getMode() == 0) {
//				log.debug("handleIncomingByte -- Status Bean ");
//				
//				ServerStatusBean serverStatusBean = new ServerStatusBean();
//				serverStatusBean.setMode(0);
//				serverStatusBean.setHeight(serverFrameBean.getHeight());
//				serverStatusBean.setWidth(serverFrameBean.getWidth());
//				serverStatusBean.setXValue(serverFrameBean.getXValue());
//				serverStatusBean.setYValue(serverFrameBean.getYValue());
//				serverStatusBean.setPublicSID(serverFrameBean.getPublicSID());
//				
//				ServerSharingSessionList.startSession(serverStatusBean);
//				
//			} else {
//				log.debug("handleIncomingByte -- Frame Bean ");
//				
//				ServerSharingSessionList.addFrameToSession(serverFrameBean);
//			}
            
		} catch (Exception err) {
			log.error("[handleIncomingByte]",err);
		}
		
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

}
