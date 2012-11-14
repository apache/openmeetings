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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User an in-memory HashMap to store the current sessions.
 * 
 * FIXME: Add multiple lists to enhance performance, see FIXME tagged methods
 * 
 * @author sebawagner
 * 
 */
public class ClientListHashMapStore implements IClientList, ISharedSessionStore {

	private static HashMap<String, ClientSession> clientList = new HashMap<String, ClientSession>();

	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientListHashMapStore.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ManageCryptStyle manageCryptStyle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getRoomClients(java.lang
	 * .Long)
	 */
	public List<RoomClient> getRoomClients(Long room_id) {
		try {
			return this.getClientListByRoom(room_id);
		} catch (Exception err) {
			log.error("[getRoomClients]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#addClientListItem(java
	 * .lang.String, java.lang.String, java.lang.Integer, java.lang.String,
	 * java.lang.String, boolean)
	 */
	public synchronized RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl, boolean isAVClient) {
		try {

			// Store the Connection into a bean and add it to the HashMap
			RoomClient rcm = new RoomClient();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope(scopeName);
			long thistime = new Date().getTime();
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(thistime).toString()));

			rcm.setUserport(remotePort);
			rcm.setUserip(remoteAddress);
			rcm.setSwfurl(swfUrl);
			rcm.setIsMod(new Boolean(false));
			rcm.setCanDraw(new Boolean(false));
			rcm.setIsAVClient(isAVClient);

			if (clientList.containsKey(streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			clientList.put(
					ClientSessionUtil.getClientSessionKey(null,
							rcm.getStreamid()), new ClientSession(null, rcm));

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.remote.red5.IClientList#getAllClients()
	 */
	public synchronized Collection<RoomClient> getAllClients() {
		// only locally clients interesting
		List<RoomClient> rclList = new ArrayList<RoomClient>();
		for (ClientSession cSession : clientList.values()) {
			if (cSession.getServer() == null) {
				rclList.add(cSession.getRoomClient());
			}
		}
		return rclList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getClientByStreamId(java
	 * .lang.String)
	 */
	public synchronized RoomClient getClientByStreamId(String streamId) {
		try {
			String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
					streamId);
			if (!clientList.containsKey(uniqueKey)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}
			return clientList.get(uniqueKey).getRoomClient();
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getSyncClientByStreamId
	 * (java.lang.String)
	 */
	public synchronized RoomClient getSyncClientByStreamId(String streamId) {
		try {
			String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
					streamId);
			if (!clientList.containsKey(uniqueKey)) {
				log.debug("Tried to get a non existing Client " + streamId);
				return null;
			}

			RoomClient rcl = clientList.get(uniqueKey).getRoomClient();

			if (rcl == null) {
				return null;
			}

			if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				return null;
			}

			return clientList.get(uniqueKey).getRoomClient();
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getClientByPublicSID(
	 * java.lang.String, boolean)
	 */
	public RoomClient getClientByPublicSID(String publicSID, boolean isAVClient) {
		try {
			for (ClientSession cSession : clientList.values()) {

				RoomClient rcl = cSession.getRoomClient();

				if (!rcl.getPublicSID().equals(publicSID)) {
					continue;
				}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getClientByUserId(java
	 * .lang.Long)
	 */
	public synchronized RoomClient getClientByUserId(Long userId) {
		try {
			for (ClientSession cSession : clientList.values()) {
				if (cSession.getRoomClient().getUser_id().equals(userId)) {
					return cSession.getRoomClient();
				}
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#updateAVClientByStreamId
	 * (java.lang.String, org.apache.openmeetings.conference.room.RoomClient)
	 */
	public synchronized Boolean updateAVClientByStreamId(String streamId,
			RoomClient rcm) {
		try {

			// get the corresponding user session object and update the settings
			RoomClient rclUsual = getClientByPublicSID(rcm.getPublicSID(),
					false);
			if (rclUsual != null) {
				rclUsual.setBroadCastID(rcm.getBroadCastID());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setVHeight(rcm.getVHeight());
				rclUsual.setVWidth(rcm.getVWidth());
				rclUsual.setVX(rcm.getVX());
				rclUsual.setVY(rcm.getVY());
				String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
						rclUsual.getStreamid());
				ClientSession cSession = clientList.get(uniqueKey);
				if (cSession != null) {
					cSession.setRoomClient(rclUsual);
					clientList.put(uniqueKey, cSession);
				} else {
					log.debug("Tried to update a non existing Client "
							+ rclUsual.getStreamid());
				}
			}

			updateClientByStreamId(streamId, rcm);
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#updateClientByStreamId
	 * (java.lang.String, org.apache.openmeetings.conference.room.RoomClient)
	 */
	public synchronized Boolean updateClientByStreamId(String streamId,
			RoomClient rcm) {
		try {
			String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
					streamId);
			ClientSession cSession = clientList.get(uniqueKey);
			if (cSession != null) {
				cSession.setRoomClient(rcm);
				clientList.put(uniqueKey, cSession);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#removeClient(java.lang
	 * .String)
	 */
	public synchronized Boolean removeClient(String streamId) {
		try {
			String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
					streamId);
			if (clientList.containsKey(uniqueKey)) {
				clientList.remove(uniqueKey);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getClientListByRoom(java
	 * .lang.Long)
	 */
	// FIXME: Enhance performance by using multiple lists
	public synchronized List<RoomClient> getClientListByRoom(Long room_id) {
		List<RoomClient> roomClientList = new ArrayList<RoomClient>();
		try {

			// FIXME: Enhance performance by using multiple lists
			for (ClientSession cSession : clientList.values()) {

				RoomClient rcl = cSession.getRoomClient();

				// client initialized and same room
				if (rcl.getRoom_id() == null
						|| !room_id.equals(rcl.getRoom_id())) {
					continue;
				}
				if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
					continue;
				}
				if (rcl.getIsAVClient()) {
					continue;
				}

				// Only parse really those users out that are really a full
				// session object
				// and no pseudo session object like the audio/video or screen
				// sharing connection
				roomClientList.add(rcl);

			}
		} catch (Exception err) {
			log.error("[getClientListByRoom]", err);
		}
		return roomClientList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getClientListByRoomAll
	 * (java.lang.Long)
	 */
	public synchronized List<RoomClient> getClientListByRoomAll(Long room_id) {
		List<RoomClient> roomClientList = new ArrayList<RoomClient>();
		try {
			// FIXME: Enhance performance by using multiple lists
			for (ClientSession cSession : clientList.values()) {
				RoomClient rcl = cSession.getRoomClient();

				if (rcl.getRoom_id() != null
						&& rcl.getRoom_id().equals(room_id)) {
					// same room
					roomClientList.add(rcl);
				}
			}
		} catch (Exception err) {
			log.error("[getClientListByRoomAll]", err);
		}
		return roomClientList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getCurrentModeratorByRoom
	 * (java.lang.Long)
	 */
	public synchronized List<RoomClient> getCurrentModeratorByRoom(Long room_id) {
		List<RoomClient> rclList = new LinkedList<RoomClient>();
		List<RoomClient> currentClients = this.getClientListByRoom(room_id);
		// FIXME: Enhance performance by using multiple lists
		for (RoomClient rcl : currentClients) {
			if (rcl.getIsMod()) {
				log.debug("found client who is the Moderator: " + rcl);
				rclList.add(rcl);
			}
		}

		return rclList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getListByStartAndMax(int,
	 * int, java.lang.String, boolean)
	 */
	// FIXME not sorted
	public synchronized SearchResult<RoomClient> getListByStartAndMax(
			int start, int max, String orderby, boolean asc) {
		SearchResult<RoomClient> sResult = new SearchResult<RoomClient>();
		sResult.setObjectName(RoomClient.class.getName());
		sResult.setRecords(Long.valueOf(clientList.size()).longValue());
		LinkedList<RoomClient> myList = new LinkedList<RoomClient>();

		int i = 0;
		for (ClientSession cSession : clientList.values()) {
			if (i >= start) {
				myList.add(cSession.getRoomClient());
			}
			if (i > max) {
				break;
			}
			i++;
		}
		sResult.setResult(myList);

		return sResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.remote.red5.IClientList#removeAllClients()
	 */
	public synchronized void removeAllClients() {
		try {
			clientList.clear();
		} catch (Exception err) {
			log.error("[removeAllClients]", err);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getRecordingCount(long)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.remote.red5.IClientList#getPublisingCount(long)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.conference.room.ISharedSessionStore#
	 * syncSlaveClientSession
	 * (org.apache.openmeetings.persistence.beans.basic.Server, java.util.List)
	 */
	// FIXME: Add multiple lists to enhance performance
	public void syncSlaveClientSession(Server server,
			List<SlaveClientDto> clients) {

		System.err.println("Session 1 Length: " + clientList.size());
		for (ClientSession cSession : clientList.values()) {
			System.err.println("cSession: " + cSession.getServer()
					+ " cSession RCL " + cSession.getRoomClient());
		}

		// delete all existing client sessions by that slave, updating existing
		// ones
		// makes no sense, we don't know anything about the start or end date
		// so at this point we can just remove them all and add them new

		for (Iterator<Entry<String, ClientSession>> iter = clientList
				.entrySet().iterator(); iter.hasNext();) {
			Entry<String, ClientSession> entry = iter.next();
			if (entry.getValue().getServer().equals(server)) {
				iter.remove();
			}
		}

		System.err.println("Session 2 Length: " + clientList.size());

		for (SlaveClientDto slaveClientDto : clients) {
			String uniqueKey = ClientSessionUtil.getClientSessionKey(null,
					slaveClientDto.getStreamid());
			clientList.put(uniqueKey, new ClientSession(server, new RoomClient(
					slaveClientDto.getStreamid(),
					slaveClientDto.getPublicSID(), slaveClientDto.getRoomId(),
					slaveClientDto.getUserId(), slaveClientDto.getFirstName(),
					slaveClientDto.getLastName())));

		}
		
		System.err.println("Session 3 Length: " + clientList.size());

		for (ClientSession cSession : clientList.values()) {
			System.err.println("cSession: " + cSession.getServer()
					+ " cSession RCL " + cSession.getRoomClient());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.conference.room.ISharedSessionStore#
	 * getCurrentSlaveSessions()
	 */
	public List<SlaveClientDto> getCurrentSlaveSessions() {
		List<SlaveClientDto> clients = new ArrayList<SlaveClientDto>(
				clientList.size());
		for (ClientSession cSession : clientList.values()) {
			clients.add(new SlaveClientDto(cSession.getRoomClient()));
		}
		return clients;
	}

}
