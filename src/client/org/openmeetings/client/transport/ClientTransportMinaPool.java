package org.openmeetings.client.transport;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.gui.ClientShowStatusMessage;

/**
 * @author sebastianwagner
 *
 */
public class ClientTransportMinaPool {
	
	private static Logger log = Logger.getLogger(ClientTransportMinaPool.class);

	private boolean isConnected = false;
	
	private static Map<Integer,ClientPacketMinaProcess> socketConnections = new HashMap<Integer,ClientPacketMinaProcess>();
	
	private static int currentIndexCounter = 0;
	
	public static void startConnections() {
		try {
			
//			int portRangeStart = ClientConnectionBean.port;
//			int portRange = 2;
//			
//			int index = 0;
//			for (int i=portRangeStart;i<(portRangeStart+portRange);i++) {
//				
//				socketConnections.put(index,new ClientPacketMinaProcess(ClientConnectionBean.port,i));
//				index++;
//			}
			
			socketConnections.put(0,new ClientPacketMinaProcess(ClientConnectionBean.port,0));
		
			log.debug("socketConnections -1- "+socketConnections.size());
			
			ClientShowStatusMessage.doConnectedEvent();
			
		} catch (Exception err) {
			log.error("[startConnections]",err);
		}
	}
	
	public static void sendMessage(Object clientBean) throws Exception {
//		if (currentIndexCounter == 2) {
//			currentIndexCounter = 0;
//		}
//		
//		ClientPacketMinaProcess cProcess = socketConnections.get(currentIndexCounter);
//		
//		cProcess.sendData(clientBean);
//		
//		currentIndexCounter++;
		
		ClientPacketMinaProcess cProcess = socketConnections.get(0);
		
		cProcess.sendData(clientBean);
		
	}
	
	public static void closeSession() throws Exception {
		try {
		
			ClientPacketMinaProcess cProcess = socketConnections.get(0);
			
			cProcess.closeSession();
		
		} catch (Exception err) {
			log.error("[closeSession]",err);
		}
		
	}
	
}
