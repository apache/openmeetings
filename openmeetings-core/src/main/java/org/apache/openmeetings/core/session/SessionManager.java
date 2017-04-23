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
package org.apache.openmeetings.core.session;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.openmeetings.core.session.store.IClientPersistenceStore;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.server.ClientSessionInfo;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handle {@link StreamClient} objects.
 *
 * Use a kind of decorator pattern to inject the {@link Server} into every call.
 *
 * @author sebawagner
 *
 */
public class SessionManager implements ISessionManager {
	protected static final Logger log = Red5LoggerFactory.getLogger(SessionManager.class, webAppRootKey);

	@Autowired
	private ServerUtil serverUtil;

	/**
	 * Injected via Spring, needs a getter/setter because it can be configured
	 * Autowired will not suit here as there are multiple implementations of the
	 * {@link IClientPersistenceStore}
	 */
	private IClientPersistenceStore cache;

	public IClientPersistenceStore getCache() {
		return cache;
	}

	public void setCache(IClientPersistenceStore cache) {
		this.cache = cache;
	}

	@Override
	public void clearCache() {
		cache.clear();
	}

	@Override
	public StreamClient add(StreamClient c, Server server) {
		if (c == null) {
			return null;
		}
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		c.setConnectedSince(new Date());
		c.setRoomEnter(new Date());
		if (Strings.isEmpty(c.getPublicSID())) {
			c.setPublicSID(UUID.randomUUID().toString());
		}
		c.setServer(server);

		if (cache.containsKey(null, c.getStreamid())) {
			log.error("Tried to add an existing Client " + c.getStreamid());
			return null;
		}

		cache.put(c.getStreamid(), c);
		return c;
	}

	@Override
	public StreamClient addClientListItem(String streamId, String scopeName,
			int remotePort, String remoteAddress, String swfUrl, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {

			// Store the Connection into a bean and add it to the HashMap
			StreamClient rcm = new StreamClient();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope(scopeName);
			rcm.setPublicSID(UUID.randomUUID().toString());
			rcm.setServer(server);
			rcm.setUserport(remotePort);
			rcm.setUserip(remoteAddress);
			rcm.setSwfurl(swfUrl);
			rcm.setIsMod(false);
			rcm.setCanDraw(false);

			if (cache.containsKey(null, streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				return null;
			}

			cache.put(rcm.getStreamid(), rcm);

			return rcm;
		} catch (Exception err) {
			log.error("[addClientListItem]", err);
		}
		return null;
	}

	@Override
	public Collection<StreamClient> getClients() {
		return cache.getClients();
	}

	@Override
	public Collection<StreamClient> getClientsWithServer() {
		return cache.getClientsWithServer();
	}

	@Override
	public StreamClient getClientByStreamId(String streamId, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			if (!cache.containsKey(server, streamId)) {
				log.debug("Tried to get a non existing Client " + streamId + " server " + server);
				return null;
			}
			return cache.get(server, streamId);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	@Override
	public StreamClient getClientByPublicSID(String publicSID, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			List<StreamClient> list = cache.getClientsByPublicSID(server, publicSID);
			return list == null || list.isEmpty() ? null : list.get(0);
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	@Override
	public ClientSessionInfo getClientByPublicSIDAnyServer(String publicSID) {
		try {
			for (Entry<Long,List<StreamClient>> entry : cache.getClientsByPublicSID(publicSID).entrySet()) {
				for (StreamClient rcl : entry.getValue()) {
					return new ClientSessionInfo(rcl, entry.getKey());
				}
			}
		} catch (Exception err) {
			log.error("[getClientByPublicSIDAnyServer]", err);
		}
		return null;
	}

	@Override
	public StreamClient getClientByUserId(Long userId) {
		try {
			for (StreamClient rcl : cache.getClientsByUserId(null, userId)) {
				if (rcl.isScreenClient()) {
					continue;
				}

				return rcl;
			}
		} catch (Exception err) {
			log.error("[getClientByUserId]", err);
		}
		return null;
	}

	@Override
	public boolean updateAVClientByStreamId(String streamId, StreamClient rcm, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			// get the corresponding user session object and update the settings
			StreamClient rclUsual = getClientByPublicSID(rcm.getPublicSID(), server);
			if (rclUsual != null) {
				rclUsual.setBroadCastId(rcm.getBroadCastId());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setVHeight(rcm.getVHeight());
				rclUsual.setVWidth(rcm.getVWidth());
				rclUsual.setVX(rcm.getVX());
				rclUsual.setVY(rcm.getVY());
				StreamClient rclSaved = cache.get(server, rclUsual.getStreamid());
				if (rclSaved != null) {
					cache.put(rclUsual.getStreamid(), rclUsual);
				} else {
					log.debug("Tried to update a non existing Client " + rclUsual.getStreamid());
				}
			}

			updateClientByStreamId(streamId, rcm, false, server);
			return true;
		} catch (Exception err) {
			log.error("[updateAVClientByStreamId]", err);
		}
		return false;
	}

	@Override
	public boolean updateClientByStreamId(String streamId, StreamClient rcm, boolean updateRoomCount, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			StreamClient rclSaved = cache.get(server, streamId);

			if (rclSaved != null) {
				cache.put(streamId, rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client " + streamId);
			}
		} catch (Exception err) {
			log.error("[updateClientByStreamId]", err);
		}
		return false;
	}

	@Override
	public boolean removeClient(String streamId, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			if (cache.containsKey(server,streamId)) {
				cache.remove(server,streamId);
				return true;
			} else {
				log.debug("Tried to remove a non existing Client " + streamId);
			}
		} catch (Exception err) {
			log.error("[removeClient]", err);
		}
		return false;
	}

	@Override
	public List<StreamClient> getClientListByRoom(Long roomId) {
		List<StreamClient> roomClientList = new ArrayList<>();
		try {
			for (StreamClient rcl : cache.getClientsByRoomId(roomId)) {
				if (rcl.isScreenClient()) {
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

	@Override
	public Collection<StreamClient> getClientListByRoomAll(Long roomId) {
		try {
			return cache.getClientsByRoomId(roomId);
		} catch (Exception err) {
			log.error("[getClientListByRoomAll]", err);
		}
		return null;
	}

	@Override
	public List<StreamClient> getCurrentModeratorByRoom(Long roomId) {
		List<StreamClient> rclList = new LinkedList<>();
		List<StreamClient> currentClients = this.getClientListByRoom(roomId);
		for (StreamClient rcl : currentClients) {
			if (rcl.getIsMod()) {
				rclList.add(rcl);
			}
		}
		return rclList;
	}

	@Override
	public SearchResult<StreamClient> getListByStartAndMax(int start, int max, String orderby, boolean asc) {
		SearchResult<StreamClient> sResult = new SearchResult<>();
		sResult.setObjectName(StreamClient.class.getName());
		sResult.setRecords(Long.valueOf(cache.size()));
		sResult.setResult(cache.getClientsWithServer());
		return sResult;
	}

	@Override
	public long getRecordingCount(long roomId) {
		List<StreamClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfRecordingUsers = 0;
		for (StreamClient rcl : currentClients) {
			if (rcl.isStartRecording()) {
				numberOfRecordingUsers++;
			}
		}
		return numberOfRecordingUsers;
	}

	@Override
	public long getPublishingCount(long roomId) {
		List<StreamClient> currentClients = this.getClientListByRoom(roomId);
		int numberOfPublishingUsers = 0;
		for (StreamClient rcl : currentClients) {
			if (rcl.isStreamPublishStarted()) {
				numberOfPublishingUsers++;
			}
		}
		return numberOfPublishingUsers;
	}

	@Override
	public List<Long> getActiveRoomIdsByServer(Server server) {
		return cache.getRoomsIdsByServer(server == null ? serverUtil.getCurrentServer() : server);
	}

	@Override
	public String getSessionStatistics() {
		return cache.getDebugInformation(Arrays.asList(IClientPersistenceStore.DEBUG_DETAILS.SIZE));
	}

	@Override
	public void sessionStart() {
		// TODO Auto-generated method stub
	}
}
