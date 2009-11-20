package org.openmeetings.client.screen;

import org.apache.log4j.Logger;
import org.openmeetings.client.transport.ClientTransportMinaPool;
import org.openmeetings.screen.codec.CaptureScreenByMode;
 
/**
 * @author sebastianwagner
 *
 */
public class ClientSentScreen extends Thread {
	
	public static int mode = 1;
	
	public static boolean threadRunning = false;
	
	private static Logger log = Logger.getLogger(ClientSentScreen.class);
	
	@Override
	public void run() {
		try {
			
			log.debug("ClientSentScreen RUN");
			
			while (threadRunning) {
			
				if (mode == 1) {
					new ClientCaptureScreen(1);
				} else if (mode == 2) {
					new CaptureScreenByMode();
				}

				//Wait for 500 milliseconds for the next run
				ClientSentScreen.sleep(500);
			
			}
			
		} catch (Exception err) {
			log.error("[ClientSentScreen]",err);
		}
	}

}
