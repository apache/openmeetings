package org.openmeetings.client.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientImageFrame;


/**
 * @author sebastianwagner
 *
 */
public class ClientRasterList {
	
	private static Logger log = Logger.getLogger(ClientRasterList.class);
	
	private static List<ClientImageFrame> clientImageFrames = new LinkedList<ClientImageFrame>();
	
	public static synchronized void resetFrameHistory() {
		clientImageFrames = new LinkedList<ClientImageFrame>();
	}
	
	public static synchronized boolean checkFrame(ClientImageFrame newClientImageFrame) {
		try {
			
			boolean foundRect= false;
			
			for (ClientImageFrame clientImageFrame : clientImageFrames) {
				
				if (clientImageFrame.getRect().getX() == newClientImageFrame.getRect().getX() 
						&& clientImageFrame.getRect().getY() == newClientImageFrame.getRect().getY()) {
					log.debug("FOUND SAME POSITION");
					foundRect = true;
					if (clientImageFrame.getPayload().length == newClientImageFrame.getPayload().length) {
						log.debug("NO CHANGES IN Tile: "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
						return false;
					} else {
						log.debug("CHANGED TILE: "+clientImageFrame.getRect().getX()+" "+clientImageFrame.getRect().getY());
						clientImageFrame.setPayload(newClientImageFrame.getPayload());
						return true;
					}
					
				}
				
			}
			
			if (!foundRect) {
				log.debug("TILE NOT in List yet: "+newClientImageFrame.getRect().getX()+" "+newClientImageFrame.getRect().getY());
				clientImageFrames.add(newClientImageFrame);
				return true;
			}
			
		} catch (Exception err) {
			log.error("[checkFrame]",err);
		}
		return true;
	}

}
