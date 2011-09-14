package org.openmeetings.server.socket;

//import org.slf4j.Logger;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.util.DePacketizerUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ServerPacketHandler {
	
	private static final Logger log = Red5LoggerFactory.getLogger(ServerPacketHandler.class, ScopeApplicationAdapter.webAppRootKey);

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
