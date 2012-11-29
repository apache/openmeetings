/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.conference.room;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.cache.HashMapStore;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User an in-memory HashMap to store the current sessions.
 * 
 * @author sebawagner
 * 
 */
public class ClientListHashMapStore implements IClientList, ISharedSessionStore {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			ClientListHashMapStore.class, OpenmeetingsVariables.webAppRootKey);
	
	protected static HashMapStore cache = new HashMapStore();

	@Autowired
	private ManageCryptStyle manageCryptStyle;
	
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
			
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(random).toString()));

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

	public synchronized Collection<RoomClient> getAllClients() {
		HashMap<String, RoomClient> clients = cache.getClientsByServer(null);
		if (clients == null) {
			return new ArrayList<RoomClient>(0);
		}
		return clients.values();
	}

	public synchronized RoomClient getClientByStreamId(String streamId, Server server) {
		try {
			if (!cache.containsKey(server, streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return cache.get(server, streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public synchronized RoomClient getSyncClientByStreamId(String streamId) {
		try {
			if (!cache.containsKey(null, streamId)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}

			RoomClient rcl = cache.get(null, streamId);

			if (rcl == null) {
				return null;
			}

			if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				return null;
			}

			return cache.get(null, streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	public RoomClient getClientByPublicSID(String publicSID, boolean isAVClient, Server server) {
		try {
			for (RoomClient rcl : cache.getClientsByPublicSID(server, publicSID)) {
				if (rcl.getIsAVClient() != isAVClient) {
					continue;
				}
				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	public synchronized RoomClient getClientByUserId(Long userId) {
		try {
			for (RoomClient rcl : cache.getClientsByUserId(null, userId)) {
				
				if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
					continue;
				}
				
				if (rcl.getIsAVClient()) {
					continue;
				}
				
				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByUserId]", err);
		}
		return null;
	}

	public synchronized Boolean updateAVClientByStreamId(String streamId,
			RoomClient rcm) {
		try {

			// get the corresponding user session object and update the settings
			RoomClient rclUsual = getClientByPublicSID(rcm.getPublicSID(),
					false, null);
			if (rclUsual != null) {
				rclUsual.setBroadCastID(rcm.getBroadCastID());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setVHeight(rcm.getVHeight());
				rclUsual.setVWidth(rcm.getVWidth());
				rclUsual.setVX(rcm.getVX());
				rclUsual.setVY(rcm.getVY());
				RoomClient rclSaved = cache.get(null, rclUsual.getStreamid());
				if (rclSaved != null) {
					cache.put(null,rclUsual.getStreamid(), rclUsual);
				} else {
					log.debug("Tried to update a non existing Client "
							+ rclUsual.getStreamid());
				}
			}

			updateClientByStreamId(streamId, rcm, false);
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean updateClientByStreamId(String streamId,
			RoomClient rcm, boolean updateRoomCount) {
		try {
			
			RoomClient rclSaved = cache.get(null, streamId);
			
			if (rclSaved != null) {
				cache.put(null, streamId, rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[updateClientByStreamId]", err);
		}
		return null;
	}

	public synchronized Boolean removeClient(String streamId) {
		try {
			if (cache.containsKey(null,streamId)) {
				cache.remove(null,streamId);
				return true;
			} else {
				log.debug("Tried to remove a non existing Client " + streamId);
				return false;
			}
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return null;
	}

	public synchronized ArrayList<RoomClient> getClientListByRoom(Long roomId) {
		ArrayList<RoomClient> roomClientList = new ArrayList<RoomClient>();
		try {

			for (RoomClient rcl : cache.getClientsByRoomId(roomId).values()) {

				if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
					continue;
				}
				if (rcl.getIsAVClient()) {
					continue;
				}

				// Only parse really those users out that are really a full session object
				// and no pseudo session object like the audio/video or screen
				// sharing connection
				roomClientList.add(rcl);

			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	public synchronized Collection<RoomClient> getClientListByRoomAll(Long roomId) {
		try {
			return cache.getClientsByRoomId(roomId).values();
		} catch (Exception err) {
			log.error("[getClientListByRoomAll]", err);
		}
		return null;
	}

	public synchronized List<RoomClient> getCurrentModeratorByRoom(Long room_id) {
		List<RoomClient> rclList = new LinkedList<RoomClient>();
		List<RoomClient> currentClients = this.getClientListByRoom(room_id);
		for (RoomClient rcl : currentClients) {
			if (rcl.getIsMod()) {
				rclList.add(rcl);
			}
		}

		return rclList;
	}

	// FIXME not sorted
	public synchronized SearchResult<ClientSession> getListByStartAndMax(
			int start, int max, String orderby, boolean asc) {
		SearchResult<ClientSession> sResult = new SearchResult<ClientSession>();
		sResult.setObjectName(RoomClient.class.getName());
		sResult.setRecords(Long.valueOf(cache.size()).longValue());
		ArrayList<ClientSession> myList = new ArrayList<ClientSession>(cache.size());
		
		//FIXME: Improve the handling of the Arrays/Map/List so that this reparsing is not needed
		for (Entry<Long, LinkedHashMap<String, RoomClient>> entry : cache.values().entrySet()) {
			for (RoomClient rcl : entry.getValue().values()) {
				myList.add(new ClientSession(entry.getKey(), rcl));
			}
		}
		
		sResult.setResult(myList);
		return sResult;
	}

	public long getRecordingCount(long roomId) {
		List<RoomClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfRecordingUsers = 0;
		for (RoomClient rcl : currentClients) {
			if (rcl.isStartRecording()) {
				numberOfRecordingUsers++;
			}
		}
		return numberOfRecordingUsers;
	}

	public long getPublishingCount(long roomId) {
		List<RoomClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfPublishingUsers = 0;
		for (RoomClient rcl : currentClients) {
			if (rcl.isStreamPublishStarted()) {
				numberOfPublishingUsers++;
			}
		}
		return numberOfPublishingUsers;
	}
	
	public void cleanSessionsOfDeletedOrDeactivatedServer(Server server) {
		//we need to summarize those clients in a second list first, cause there are 
		//multiple lists to be cleaned up and an iterator will not work
		ArrayList<RoomClient> serverList = new ArrayList<RoomClient>();
		serverList.addAll(cache.getClientsByServer(server).values());
		
		for (RoomClient rcl : serverList) {
			cache.remove(server, rcl.getStreamid());
		}
	}

	public void syncSlaveClientSession(Server server,
			List<SlaveClientDto> clients) {
		
		// delete all existing client sessions by that slave, updating existing ones
		// makes no sense, we don't know anything about the start or end date
		// so at this point we can just remove them all and add them new
		cleanSessionsOfDeletedOrDeactivatedServer(server);

		for (SlaveClientDto slaveClientDto : clients) {
			cache.put(
					server, slaveClientDto.getStreamid(),
					new RoomClient(
								slaveClientDto.getStreamid(), 
								slaveClientDto.getPublicSID(),
								slaveClientDto.getRoomId(), 
								slaveClientDto.getUserId(),
								slaveClientDto.getFirstName(), 
								slaveClientDto.getLastName(), 
								slaveClientDto.isAVClient(),
								slaveClientDto.getUsername(),
								slaveClientDto.getConnectedSince(),
								slaveClientDto.getScope()
							));
		}

	}

	public List<SlaveClientDto> getCurrentSlaveSessions() {
		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>(
				cache.size());
		for (RoomClient rcl : cache.getClientsByServer(null).values()) {
			clients.add(new SlaveClientDto(rcl));
		}
		return clients;
	}
	
//	/*
//	 * (non-Javadoc)
//	 * @see org.apache.openmeetings.conference.room.IClientList#getActiveRoomsByServer()
//	 */
//	public Map<Server,List<Long>> getActiveRoomsByServer() {
//		Map<Server,List<Long>> serverRooms = new HashMap<Server,List<Long>>();
//		
//		for (ClientSession cSession : clientList.values()) {
//			
//			//We don't care about incomplete sessions or clients that are not logged into any room
//			if (cSession.getRoomClient() == null 
//					|| cSession.getRoomClient().getRoom_id() == null) {
//				continue;
//			}
//			
//			List<Long> roomIds = serverRooms.get(cSession.getServer());
//			if (roomIds == null) {
//				roomIds = new ArrayList<Long>();
//			}
//			if (!roomIds.contains(cSession.getRoomClient().getRoom_id())) {
//				roomIds.add(cSession.getRoomClient().getRoom_id());
//			}
//			serverRooms.put(cSession.getServer(), roomIds);
//		}
//		
//		return serverRooms;
//	}

}
