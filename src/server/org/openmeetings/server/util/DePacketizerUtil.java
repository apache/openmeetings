package org.openmeetings.server.util;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.server.beans.ServerFrameBean;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
/**
 * @author sebastianwagner
 *
 */
public class DePacketizerUtil {
	
	private static final Logger log = Red5LoggerFactory.getLogger(DePacketizerUtil.class, ScopeApplicationAdapter.webAppRootKey);
	
	public ServerFrameBean handleReceivingBytes(byte[] incomingBytes) {
		try {
			
			log.debug("LENGTH incomingBytes "+incomingBytes.length);
			
			throw new Exception("This Class is deprecated use the Protocol Codec Filter");
		} catch (Exception err) {
			log.error("[handleReceivingBytes]",err);
		}
		return null;
	}
}
