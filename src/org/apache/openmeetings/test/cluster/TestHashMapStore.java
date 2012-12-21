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
import org.apache.openmeetings.cluster.beans.ServerDTO;
import org.apache.openmeetings.conference.room.ClientListHashMapStore;
import org.apache.openmeetings.conference.room.ClientSession;
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.conference.room.cache.HashMapStore;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.remote.ConferenceService;
import org.apache.openmeetings.remote.MainService;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.utils.crypt.ICryptString;
import org.apache.openmeetings.utils.crypt.MD5Implementation;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestHashMapStore extends AbstractOpenmeetingsSpringTest {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestHashMapStore.class, OpenmeetingsVariables.webAppRootKey);

	private ClientListHashMapStoreTesting sessionManager = new ClientListHashMapStoreTesting();

	@Autowired
	private MainService mService;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private ConferenceService conferenceService;
	@Autowired
	private ServerDao serverDao;

	int localSessions = 200;
	int slaveSessionSize = 200;

	@Test
	public void doClientTest() {

		log.debug("Cache size " + sessionManager.getAllClients().size());

		for (int i = 0; i < localSessions; i++) {
			this.sessionManager.addClientListItem("streamId" + i, "hibernate",
					123, "localhost", "", false);

			RoomClient rcl = this.sessionManager.getClientByStreamId("streamId"
					+ i, null);
			rcl.setUser_id(Long.parseLong("" + i));
			this.sessionManager.updateClientByStreamId("streamId" + i, rcl,
					false);
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

		Server s1 = serverDao.get(1L);
		if (s1 == null) {
			Server s = new Server();
			s.setName("name 1");
			s.setAddress("127.0.0.1");
			s.setPort(5080);
			s.setUser("swagner");
			s.setPass("qweqwe");
			s.setWebapp("openmeetings");
			s.setProtocol("http");
			s.setActive(true);
			s.setComment("");
			serverDao.update(s, 1L);
			s1 = serverDao.get(1L);
		}
		Server s2 = serverDao.get(2L);
		if (s2 == null) {
			Server s = new Server();
			s.setName("name 2");
			s.setAddress("127.0.0.1");
			s.setPort(5080);
			s.setUser("swagner");
			s.setPass("qweqwe");
			s.setWebapp("openmeetings");
			s.setProtocol("http");
			s.setActive(true);
			s.setComment("");
			serverDao.update(s, 1L);
			s2 = serverDao.get(2L);
		}
		Server s3 = serverDao.get(3L);
		if (s3 == null) {
			Server s = new Server();
			s.setName("name 3");
			s.setAddress("127.0.0.1");
			s.setPort(5080);
			s.setUser("swagner");
			s.setPass("qweqwe");
			s.setWebapp("openmeetings");
			s.setProtocol("http");
			s.setActive(true);
			s.setComment("");
			serverDao.update(s, 1L);
			s3 = serverDao.get(3L);
		}

		addSyncClients(s1, 3, 6);
		addSyncClients(s2, 4, 7);
		addSyncClients(s3, 5, 8);

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

		List<RoomClient> clientsByRoom = sessionManager.getClientListByRoom(1L,
				null);

		log.debug("clientsByRoom SIZE " + clientsByRoom.size());

		assertEquals(clientsByRoom.size(), 50);

		SearchResult<ClientSession> searchResult = sessionManager
				.getListByStartAndMax(0, Integer.MAX_VALUE, "", true);

		log.debug("Number of sessions Total " + searchResult.getRecords());

		sessionManager.getCache().printDebugInformation(
				Arrays.asList(HashMapStore.DEBUG_DETAILS.SIZE,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));

		assertEquals(searchResult.getRecords().intValue(), 1400);

		addSyncClients(s1, 3, 6);
		addSyncClients(s2, 4, 7);
		addSyncClients(s3, 5, 8);

		log.debug("Number of sessions Total " + searchResult.getRecords());

		assertEquals(searchResult.getRecords().intValue(), 1400);

		for (RoomClient rSession : sessionManager.getCache()
				.getClientsByServer(s1).values()) {
			log.debug("rSession " + rSession);
		}

		String streamId = "streamId" + ((1000 * 3 * s1.getId()) + 1);
		RoomClient rcl2 = sessionManager.getClientByStreamId(streamId, s1);
		log.debug("rcl2 " + rcl2);
		RoomClient compareRcl2 = sessionManager.getClientByPublicSID(
				rcl2.getPublicSID(), rcl2.getIsAVClient(), s1);
		log.debug("compareRcl2 " + compareRcl2);

		assertEquals(rcl2.getStreamid(), compareRcl2.getStreamid());
		assertEquals(rcl2.getPublicSID(), compareRcl2.getPublicSID());

		RoomClient rclRemove = sessionManager.getClientByStreamId("streamId51",
				null);
		log.debug("rclRemove " + rclRemove);

		Boolean removed = sessionManager.removeClient("streamId51");

		assertTrue(removed);

		log.debug("Local Cache size " + sessionManager.getAllClients().size());
		assertEquals(sessionManager.getAllClients().size(), localSessions - 1);

		RoomClient deletedNullClient = sessionManager.getClientByPublicSID(
				rclRemove.getPublicSID(), rclRemove.getIsAVClient(), null);

		log.debug("rclRemove: " + rclRemove);

		if (deletedNullClient != null) {
			log.debug("deletedNullClient " + deletedNullClient);
		}

		assertNull(deletedNullClient);

		sessionManager.getCache().printDebugInformation(
				Arrays.asList(HashMapStore.DEBUG_DETAILS.SIZE,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));

		// Check if number of total sessions is same as sum of all sessions
		// across all rooms
		int roomSession = sessionManager.getClientListByRoomAll(1L, null)
				.size()
				+ sessionManager.getClientListByRoomAll(2L, null).size()
				+ sessionManager.getClientListByRoomAll(3L, s1).size()
				+ sessionManager.getClientListByRoomAll(4L, s2).size()
				+ sessionManager.getClientListByRoomAll(5L, s3).size()
				+ sessionManager.getClientListByRoomAll(6L, s1).size()
				+ sessionManager.getClientListByRoomAll(7L, s2).size()
				+ sessionManager.getClientListByRoomAll(8L, s3).size();
		log.debug("Room Id 1 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(1L, null).size());
		log.debug("Room Id 2 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(2L, null).size());
		log.debug("Room Id 3 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(3L, s1).size());
		log.debug("Room Id 4 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(4L, s2).size());
		log.debug("Room Id 5 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(5L, s3).size());
		log.debug("Room Id 6 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(6L, s1).size());
		log.debug("Room Id 7 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(7L, s2).size());
		log.debug("Room Id 8 Number of Sessions: "
				+ sessionManager.getClientListByRoomAll(8L, s3).size());
		log.debug("roomSession: " + roomSession);

		assertEquals(roomSession, sessionManager.getCache()
				.getTotalNumberOfSessions());

		Sessiondata sessionData = mService.getsessiondata();

		Users us = (Users) userManagement.loginUser(
				sessionData.getSession_id(), username, userpass, null, false);

		log.debug("us " + us);

		assertTrue(us != null);

		// Is running already on server3
		ServerDTO server = conferenceService.getServerForSession(
				sessionData.getSession_id(), 8);
		log.debug("server " + server);
		assertEquals(server.getId().longValue(), 3);

		// New Room requested that is running on no server
		ServerDTO serverNew = conferenceService.getServerForSession(
				sessionData.getSession_id(), 9);
		log.debug("serverNew " + serverNew);
		assertEquals(serverNew, null);

	}

	private void addSyncClients(Server server, long... roomIds) {

		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>();

		for (long roomId : roomIds) {
			for (int i = 0; i < slaveSessionSize; i++) {

				// avClient, 2 Clients share one userId
				int add = Double.valueOf(Math.floor((i % 2))).intValue();

				Long userId = (1000 * roomId * server.getId()) + i + add;

				SlaveClientDto slaveDto = new SlaveClientDto(
						//
						"streamId" + ((1000 * roomId * server.getId()) + i), //
						"publicSID_" + roomId + "_ " + server.getId() + "_" + i, //
						roomId, //
						userId, //
						"firstName" + roomId + "_ " + server.getId() + "_" + i, //
						"lastName" + roomId + "_ " + server.getId() + "_" + i, //
						(i % 2 == 0), //
						"scope" + roomId, //
						"username" + roomId + "_ " + server.getId() + "_" + i, //
						CalendarPatterns
								.getDateWithTimeByMiliSeconds(new Date())); //
				clients.add(slaveDto);
			}
		}

		this.sessionManager.syncSlaveClientSession(server, clients);
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
