package org.openmeetings.client.screen;

import java.awt.Rectangle;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientVirtualScreenBean;
import org.openmeetings.client.util.ClientPacketizer;

/**
 * @author sebastianwagner
 * 
 * @deprecated
 *
 */
public class ClientSendStatus extends BaseSendPacketToServer {
	
	private static Logger log = Logger.getLogger(ClientSendStatus.class);

	public void sendStart() throws Exception {
		
		ClientPacketizer packetizer = new ClientPacketizer();
		
		Rectangle rect = new Rectangle(ClientVirtualScreenBean.vScreenSpinnerX,ClientVirtualScreenBean.vScreenSpinnerY,
								ClientVirtualScreenBean.vScreenSpinnerWidth,ClientVirtualScreenBean.vScreenSpinnerHeight);
		
		byte[] packetFrame = packetizer.doPacketize(0,ClientConnectionBean.getFrameNumber(), 
														ClientConnectionBean.publicSID, rect, 0, 0, new byte[0]);
		
		this.sendPacketToServer(packetFrame);
		
	}

}
