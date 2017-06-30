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
import java.util.List;
import java.util.Map.Entry;

import org.apache.openmeetings.core.session.store.IClientPersistenceStore;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.server.ClientSessionInfo;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Server;
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
		c.setServer(server);

		return cache.put(c);
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
	public StreamClient get(Long id) {
		try {
			if (!cache.containsKey(id)) {
				log.debug("Tried to get a non existing Client {}", id);
				return null;
			}
			return cache.get(id);
		} catch (Exception err) {
			log.error("[getClientByStreamId]", err);
		}
		return null;
	}

	@Override
	public StreamClient getClientByUid(String publicSID, Server server) {
		if (server == null) {
			server = serverUtil.getCurrentServer();
		}
		try {
			List<StreamClient> list = cache.getClientsByUid(server, publicSID);
			return list == null || list.isEmpty() ? null : list.get(0);
		} catch (Exception err) {
			log.error("[getClientByPublicSID]", err);
		}
		return null;
	}

	@Override
	public ClientSessionInfo getClientByUidAnyServer(String publicSID) {
		try {
			for (Entry<Long,List<StreamClient>> entry : cache.getClientsByUid(publicSID).entrySet()) {
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
	public boolean updateAVClient(StreamClient rcm) {
		try {
			// get the corresponding user session object and update the settings
			StreamClient rclUsual = get(rcm.getId());
			if (rclUsual != null) {
				rclUsual.setBroadCastId(rcm.getBroadCastId());
				rclUsual.setAvsettings(rcm.getAvsettings());
				rclUsual.setHeight(rcm.getHeight());
				rclUsual.setWidth(rcm.getWidth());
				cache.put(rclUsual);
			} else {
				log.debug("Tried to update a non existing Client {}", rclUsual);
			}

			update(rcm);
			return true;
		} catch (Exception err) {
			log.error("[updateAVClient]", err);
		}
		return false;
	}

	@Override
	public boolean update(StreamClient rcm) {
		try {
			if (cache.containsKey(rcm.getId())) {
				cache.put(rcm);
				return true;
			} else {
				log.debug("Tried to update a non existing Client {}", rcm.getId());
			}
		} catch (Exception err) {
			log.error("[updateClient]", err);
		}
		return false;
	}

	@Override
	public boolean remove(Long id) {
		try {
			if (cache.containsKey(id)) {
				cache.remove(id);
				return true;
			} else {
				log.debug("Tried to remove a non existing Client {}", id);
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
				if (rcl.isSharing()) {
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
			if (rcl.isRecordingStarted()) {
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
			if (rcl.isPublishStarted()) {
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
