package org.openmeetings.client.gui;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;

/**
 * @author sebastianwagner
 *
 */
public class ClientShowStatusMessage {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ClientShowStatusMessage.class);
	
	public static void showStatusMessage(String message) {
		try {
			
			if (ClientConnectionBean.isViewer) {
				
				ClientViewerScreen.instance.showBandwidthWarning(message);
				
			} else {
				
				ClientStartScreen.instance.showBandwidthWarning(message);
				
			}
			
		} catch (Exception err) {
			
		}
	}
	
	public static void doConnectedEvent() {
		try {
			
			if (ClientConnectionBean.isViewer) {
				
				ClientViewerScreen.instance.doInitMessage();
				
			} else {
				
				//No action at the moment
				
			}
			
		} catch (Exception err) {
			
		}
	}

}
