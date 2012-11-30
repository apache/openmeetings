package org.apache.openmeetings.test.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.ClientListHashMapStore;
import org.apache.openmeetings.conference.room.ClientSession;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.conference.room.cache.HashMapStore;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.utils.crypt.ICryptString;
import org.apache.openmeetings.utils.crypt.MD5Implementation;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestHashMapStore extends AbstractOpenmeetingsSpringTest {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestHashMapStore.class, OpenmeetingsVariables.webAppRootKey);

	private ClientListHashMapStoreTesting sessionManager = new ClientListHashMapStoreTesting();

	int localSessions = 200;
	int slaveSessionSize = 200;

	@Test
	public void doClientTest() {

		log.debug("Cache size " + sessionManager.getAllClients().size());

		for (int i = 0; i < localSessions; i++) {
			this.sessionManager.addClientListItem("streamId" + i, "hibernate",
					123, "localhost", "", false);
			
			RoomClient rcl = this.sessionManager.getClientByStreamId("streamId" + i, null);
			rcl.setUser_id(Long.parseLong(""+i));
			this.sessionManager.updateClientByStreamId("streamId" + i, rcl, false);
		}

		long roomId = 1L;
		for (int i = 0; i < localSessions; i++) {

			if (i > 100) {
				roomId = 2;
			}

			RoomClient rcm = this.sessionManager.getClientByStreamId("streamId"
					+ i, null);

			rcm.setUser_id(Double.valueOf(Math.floor(i % 2)).longValue());

			if (i % 2 == 0) {
				rcm.setRoom_id(roomId);
				rcm.setIsAVClient(true);
				this.sessionManager.updateAVClientByStreamId("streamId" + i,
						rcm);
			} else {
				rcm.setRoom_id(roomId);
				this.sessionManager.updateClientByStreamId("streamId" + i, rcm,
						true);
			}

		}

		addSyncClients(1);
		addSyncClients(2);
		addSyncClients(4);

		log.debug("Local Cache size " + sessionManager.getAllClients().size());
		assertEquals(sessionManager.getAllClients().size(), localSessions);

		RoomClient rcl = sessionManager.getClientByStreamId("streamId51", null);
		log.debug("UserID " + rcl.getUser_id() + " PublicSID "
				+ rcl.getPublicSID() + " isAvClient " + rcl.getIsAVClient());
		RoomClient compareRcl = sessionManager.getClientByPublicSID(
				rcl.getPublicSID(), rcl.getIsAVClient(), null);
		log.debug("UserID " + compareRcl.getUser_id() + " PublicSID "
				+ compareRcl.getPublicSID());

		assertEquals(rcl.getStreamid(), compareRcl.getStreamid());
		assertEquals(rcl.getPublicSID(), compareRcl.getPublicSID());

		List<RoomClient> clientsByRoom = sessionManager.getClientListByRoom(1L, null);

		log.debug("clientsByRoom SIZE " + clientsByRoom.size());

		assertEquals(clientsByRoom.size(), 50);
		
		
		SearchResult<ClientSession> searchResult = sessionManager.getListByStartAndMax(0, Integer.MAX_VALUE, "", true);
		
		log.debug("Number of sessions Total " + searchResult.getRecords());
		
		assertEquals(searchResult.getRecords().intValue(), 800);
		
		addSyncClients(1);
		addSyncClients(2);
		addSyncClients(4);
		
		log.debug("Number of sessions Total " + searchResult.getRecords());
		
		assertEquals(searchResult.getRecords().intValue(), 800);
		
		Server testServer = new Server();
		testServer.setId(1L);
		
		for (RoomClient rSession : sessionManager.getCache().getClientsByServer(testServer).values()) {
			log.debug("rSession "+rSession);
		}
		
		RoomClient rcl2 = sessionManager.getClientByStreamId("streamId1050", testServer);
		log.debug("rcl2 " + rcl2);
		RoomClient compareRcl2 = sessionManager.getClientByPublicSID(
				rcl2.getPublicSID(), rcl2.getIsAVClient(), testServer);
		log.debug("compareRcl2 " + compareRcl2);

		assertEquals(rcl2.getStreamid(), compareRcl2.getStreamid());
		assertEquals(rcl2.getPublicSID(), compareRcl2.getPublicSID());
		
		
		RoomClient rclRemove = sessionManager.getClientByStreamId("streamId51", null);
		log.debug("rclRemove " + rclRemove);
		
		Boolean removed = sessionManager.removeClient("streamId51");
		
		assertTrue(removed);
		
		log.debug("Local Cache size " + sessionManager.getAllClients().size());
		assertEquals(sessionManager.getAllClients().size(), localSessions-1);
		
		RoomClient deletedNullClient = sessionManager.getClientByPublicSID(
				rclRemove.getPublicSID(), rclRemove.getIsAVClient(), null);
		
		log.debug("rclRemove: "+ rclRemove);
		
		if (deletedNullClient != null) {
			log.debug("deletedNullClient "+deletedNullClient);
		}
		
		assertNull(deletedNullClient);
		
		sessionManager.getCache().printDebugInformation(
				Arrays.asList(HashMapStore.DEBUG_DETAILS.SIZE,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));
		
		Server s1 = new Server();
		s1.setId(1L);
		Server s2 = new Server();
		s2.setId(2L);
		Server s3 = new Server();
		s3.setId(3L);
		Server s4 = new Server();
		s4.setId(4L);
		
		//Check if number of total sessions is same as sum of all sessions across all rooms
		int roomSession = sessionManager.getClientListByRoomAll(1L, null).size()
				+ sessionManager.getClientListByRoomAll(2L, null).size()
				+ sessionManager.getClientListByRoomAll(3L, s1).size()
				+ sessionManager.getClientListByRoomAll(4L, s2).size()
				+ sessionManager.getClientListByRoomAll(5L, s3).size()
				+ sessionManager.getClientListByRoomAll(6L, s4).size()
				;
		log.debug("Room Id 2 Number of Sessions: "+sessionManager.getClientListByRoomAll(2L, s1).size());
		log.debug("Room Id 3 Number of Sessions: "+sessionManager.getClientListByRoomAll(3L, s1).size());
		log.debug("Room Id 4 Number of Sessions: "+sessionManager.getClientListByRoomAll(4L, s1).size());
		log.debug("Room Id 5 Number of Sessions: "+sessionManager.getClientListByRoomAll(5L, s1).size());
		log.debug("roomSession: "+roomSession);
		
		assertEquals(roomSession, sessionManager.getCache().getTotalNumberOfSessions());

	}

	private void addSyncClients(long serverId) {
		Server s = new Server();
		s.setId(serverId);
		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>();
		for (int i = 0; i < slaveSessionSize; i++) {

			// avClient, 2 Clients share one userId
			int add = Double.valueOf(Math.floor((i % 2))).intValue();

			Long userId = (1000 * serverId) + i + add;

			SlaveClientDto slaveDto = new SlaveClientDto( //
					"streamId" + ((1000 * serverId) + i), //
					"publicSID_" + serverId + "_" + i, //
					new Long(2+serverId), //
					userId, //
					"firstName" + i, //
					"lastName" + i, //
					(i % 2 == 0), //
					"scope" + i, //
					"username" + i, //
					CalendarPatterns.getDateWithTimeByMiliSeconds(new Date())); //
			clients.add(slaveDto);
		}

		this.sessionManager.syncSlaveClientSession(s, clients);
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
				
				long random = System.currentTimeMillis() + new BigInteger(256, new Random()).longValue();

				ICryptString cryptStyle = new MD5Implementation();

				rcm.setPublicSID(cryptStyle.createPassPhrase(String.valueOf(random).toString()));

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
