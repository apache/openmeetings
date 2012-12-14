package org.apache.openmeetings.test.cluster;

import static org.junit.Assert.assertEquals;
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
import org.apache.openmeetings.conference.room.RoomClient;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.conference.room.cache.HashMapStore;
import org.apache.openmeetings.data.basic.dao.ServerDao;
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

public class TestHashMapStoreSyncBug extends AbstractOpenmeetingsSpringTest {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestHashMapStoreSyncBug.class, OpenmeetingsVariables.webAppRootKey);

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
		
		this.sessionManager.addClientListItem("streamId0", "hibernate",
				123, "localhost", "", false);
		
		RoomClient rcl = this.sessionManager.getClientByStreamId("streamId0", null);
		rcl.setUser_id(Long.parseLong("1"));
		rcl.setRoom_id(1L);
		this.sessionManager.updateClientByStreamId("streamId0", rcl, false);

		Server s1 = serverDao.get(1L);
		if (s1 == null) {
			serverDao.saveServer(1L, "name 1", "127.0.0.1", 5080, "swagner",
					"qweqwe", "openmeetings", "http", true, "", 1L);
			s1 = serverDao.get(1L);
		}

		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>();
		SlaveClientDto slaveDto = new SlaveClientDto(
				//
				"streamId0" , //
				"publicSID_slave1", //
				2L, //
				2L, //
				"firstName 2" , //
				"lastName 2" , //
				false, //
				"2", //
				"username 2" , //
				CalendarPatterns
						.getDateWithTimeByMiliSeconds(new Date())); //
		clients.add(slaveDto);

		this.sessionManager.syncSlaveClientSession(s1, clients);

		sessionManager.getCache().printDebugInformation(
				Arrays.asList(HashMapStore.DEBUG_DETAILS.SIZE,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));

		Sessiondata sessionData = mService.getsessiondata();

		Users us = (Users) userManagement.loginUser(
				sessionData.getSession_id(), username, userpass, null, false);

		log.debug("us " + us);

		assertTrue(us != null);
		
		// Is running already on server null
		ServerDTO server = conferenceService.getServerForSession(
				sessionData.getSession_id(), 1);
		log.debug("server " + server);
		assertTrue(server == null);

		// Is running already on server 1
		ServerDTO servert2 = conferenceService.getServerForSession(
				sessionData.getSession_id(), 2);
		log.debug("servert2 " + servert2);
		assertEquals(servert2.getId().longValue(), 1);
		
		//empty server 1
		List<SlaveClientDto> clientsServer1 = new ArrayList<SlaveClientDto>();
		this.sessionManager.syncSlaveClientSession(s1, clientsServer1);
		
		log.debug("\n\r##################### \n\r AFTER USER IS REMOVED \n\r####################");

		sessionManager.getCache().printDebugInformation(
				Arrays.asList(HashMapStore.DEBUG_DETAILS.SIZE,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_STREAMID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_PUBLICSID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_USERID,
						HashMapStore.DEBUG_DETAILS.CLIENT_BY_ROOMID));
		
		// Is running already on server null
		ServerDTO servert3 = conferenceService.getServerForSession(
				sessionData.getSession_id(), 1);
		log.debug("servert3 " + servert3);
		assertTrue(servert3 == null);

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
