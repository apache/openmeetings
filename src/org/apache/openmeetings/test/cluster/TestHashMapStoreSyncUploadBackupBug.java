package org.apache.openmeetings.test.cluster;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.ClientListHashMapStore;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.conference.room.cache.HashMapStore;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.utils.crypt.ICryptString;
import org.apache.openmeetings.utils.crypt.MD5Implementation;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestHashMapStoreSyncUploadBackupBug extends AbstractOpenmeetingsSpringTest {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestHashMapStoreSyncUploadBackupBug.class, OpenmeetingsVariables.webAppRootKey);

	private ClientListHashMapStoreTesting sessionManager = new ClientListHashMapStoreTesting();

	@Test
	public void doClientTest() {

		log.debug("Cache size " + sessionManager.getAllClients().size());
		
		this.sessionManager.addClientListItem("streamId0", "hibernate",
				123, "localhost", "", false);
		
		RoomClient rcl = this.sessionManager.getClientByStreamId("streamId0", null);
		
		log.debug("rcl: "+rcl);
		
		RoomClient rcl2 = this.sessionManager.getClientByPublicSID(rcl.getPublicSID(), false, null);
		
		log.debug("rcl2: "+rcl2);
		
		assertNotNull(rcl2);

	}

	private class ClientListHashMapStoreTesting extends ClientListHashMapStore {

		public synchronized RoomClient addClientListItem(String streamId,
				String scopeName, Integer remotePort, String remoteAddress,
				String swfUrl, boolean isAVClient) {
			try {

				// Store the Connection into a bean and add it to the HashMap
				RoomClient rcm = new RoomClient();
				rcm.setConnectedSince(new Date());
				rcm.setStreamid(streamId);
				rcm.setScope(scopeName);

				long random = System.currentTimeMillis()
						+ new BigInteger(256, new Random()).longValue();

				ICryptString cryptStyle = new MD5Implementation();

				rcm.setPublicSID(cryptStyle.createPassPhrase(String.valueOf(
						random).toString()));

				rcm.setUserport(remotePort);
				rcm.setUserip(remoteAddress);
				rcm.setSwfurl(swfUrl);
				rcm.setIsMod(new Boolean(false));
				rcm.setCanDraw(new Boolean(false));
				rcm.setIsAVClient(isAVClient);

				if (cache.containsKey(null, streamId)) {
					log.error("Tried to add an existing Client " + streamId);
					return null;
				}

				cache.put(null, rcm.getStreamid(), rcm);

				return rcm;
			} catch (Exception err) {
				log.error("[addClientListItem]", err);
			}
			return null;
		}

		public HashMapStore getCache() {
			return cache;
		}

	}

}
