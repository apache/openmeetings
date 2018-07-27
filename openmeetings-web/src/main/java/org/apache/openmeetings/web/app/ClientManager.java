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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.core.util.WebSocketHelper.sendRoom;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.query.Predicates;

@Component
public class ClientManager implements IClientManager {
	private static final Logger log = getLogger(ClientManager.class, getWebAppRootKey());
	private static final String ROOMS_KEY = "ROOMS_KEY";
	private static final String ONLINE_USERS_KEY = "ONLINE_USERS_KEY";
	private static final String UID_BY_SID_KEY = "UID_BY_SID_KEY";
	private final Map<String, Client> onlineClients = new ConcurrentHashMap<>();
	private final Map<Long, Set<String>> onlineRooms = new ConcurrentHashMap<>();

	@Autowired
	private ConferenceLogDao confLogDao;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;
	@Autowired
	private Application app;

	private IMap<String, Client> map() {
		return app.hazelcast.getMap(ONLINE_USERS_KEY);
	}

	private Map<String, String> mapBySid() {
		return app.hazelcast.getMap(UID_BY_SID_KEY);
	}

	private IMap<Long, Set<String>> rooms() {
		return app.hazelcast.getMap(ROOMS_KEY);
	}

	@PostConstruct
	void init() {
		map().addEntryListener(new ClientListener(), true);
		rooms().addEntryListener(new RoomListener(), true);
	}

	public void add(Client c) {
		log.debug("Adding online client: {}, room: {}", c.getUid(), c.getRoom());
		c.setServerId(Application.get().getServerId());
		map().put(c.getUid(), c);
		onlineClients.put(c.getUid(), c);
		mapBySid().put(c.getSid(), c.getUid());
	}

	@Override
	public Client update(Client c) {
		map().put(c.getUid(), c);
		synchronized (onlineClients) {
			onlineClients.get(c.getUid()).merge(c);
		}
		return c;
	}

	@Override
	public Client get(String uid) {
		return uid == null ? null : onlineClients.get(uid);
	}

	@Override
	public Client getBySid(String sid) {
		if (sid == null) {
			return null;
		}
		String uid = mapBySid().get(sid);
		return uid == null ? null : get(uid);
	}

	@Override
	public String uidBySid(String sid) {
		if (sid == null) {
			return null;
		}
		return mapBySid().get(sid);
	}

	public void exitRoom(IClient c) {
		Long roomId = c.getRoomId();
		removeFromRoom(c);
		if (roomId != null) {
			sendRoom(new RoomMessage(roomId, c, RoomMessage.Type.roomExit));
			confLogDao.add(
					ConferenceLog.Type.roomLeave
					, c.getUserId(), "0", roomId
					, c.getRemoteAddress()
					, String.valueOf(roomId));
		}
	}

	@Override
	public void exit(IClient c) {
		if (c != null) {
			exitRoom(c);
			log.debug("Removing online client: {}, roomId: {}", c.getUid(), c.getRoomId());
			map().remove(c.getUid());
			onlineClients.remove(c.getUid());
			mapBySid().remove(c.getSid());
		}
	}

	public void clean(String serverId) {
		Map<String, Client> clients = map();
		for (Map.Entry<String, Client> e : clients.entrySet()) {
			if (serverId.equals(e.getValue().getServerId())) {
				exit(e.getValue());
			}
		}
	}

	@Override
	public Set<Long> getActiveRoomIds() {
		return onlineRooms.keySet();
	}

	/**
	 * This method will return count of users in room _after_ adding
	 *
	 * @param c - client to be added to the room
	 * @return count of users in room _after_ adding
	 */
	public int addToRoom(Client c) {
		Long roomId = c.getRoom().getId();
		log.debug("Adding online room client: {}, room: {}", c.getUid(), roomId);
		IMap<Long, Set<String>> rooms = rooms();
		rooms.lock(roomId);
		rooms.putIfAbsent(roomId, new ConcurrentHashSet<String>());
		Set<String> set = rooms.get(roomId);
		set.add(c.getUid());
		final int count = set.size();
		rooms.put(roomId, set);
		onlineRooms.put(roomId, set);
		rooms.unlock(roomId);
		update(c);
		return count;
	}

	public IClient removeFromRoom(IClient _c) {
		Long roomId = _c.getRoomId();
		log.debug("Removing online room client: {}, room: {}", _c.getUid(), roomId);
		if (roomId != null) {
			IMap<Long, Set<String>> rooms = rooms();
			rooms.lock(roomId);
			Set<String> clients = rooms.get(roomId);
			if (clients != null) {
				clients.remove(_c.getUid());
				rooms.put(roomId, clients);
				onlineRooms.put(roomId, clients);
			}
			rooms.unlock(roomId);
			if (_c instanceof StreamClient) {
				StreamClient sc = (StreamClient)_c;
				if (Client.Type.mobile != sc.getType() && Client.Type.sip != sc.getType()) {
					scopeAdapter.roomLeaveByScope(_c, roomId);
				}
			}
			if (_c instanceof Client) {
				scopeAdapter.dropSharing(_c, roomId);
				Client c = (Client)_c;
				IScope sc = scopeAdapter.getChildScope(roomId);
				for (String uid : c.getStreams()) {
					scopeAdapter.sendMessageById("quit", uid, sc);
				}
				c.setRoom(null);
				c.clear();
				update(c);
			}
		}
		return _c;
	}

	public boolean isOnline(Long userId) {
		boolean isUserOnline = false;
		for (Map.Entry<String, Client> e : map().entrySet()) {
			if (e.getValue().getUserId().equals(userId)) {
				isUserOnline = true;
				break;
			}
		}
		return isUserOnline;
	}

	public List<Client> list() {
		return new ArrayList<>(map().values());
	}

	@Override
	public Collection<Client> listByUser(Long userId) {
		return map().values(Predicates.equal("userId", userId));
	}

	@Override
	public List<Client> listByRoom(Long roomId) {
		return listByRoom(roomId, null);
	}

	public List<Client> listByRoom(Long roomId, Predicate<Client> filter) {
		List<Client> clients = new ArrayList<>();
		if (roomId != null) {
			Set<String> uids = onlineRooms.get(roomId);
			if (uids != null) {
				for (String uid : uids) {
					Client c = get(uid);
					if (c != null && (filter == null || filter.test(c))) {
						clients.add(c);
					}
				}
			}
		}
		return clients;
	}

	public Set<Long> listRoomIds(Long userId) {
		Set<Long> result = new HashSet<>();
		for (Entry<Long, Set<String>> me : onlineRooms.entrySet()) {
			for (String uid : me.getValue()) {
				Client c = get(uid);
				if (c != null && c.getUserId().equals(userId)) {
					result.add(me.getKey());
				}
			}
		}
		return result;
	}

	public boolean isInRoom(long roomId, long userId) {
		Set<String> clients = onlineRooms.get(roomId);
		if (clients != null) {
			for (String uid : clients) {
				Client c = get(uid);
				if (c != null && c.getUserId().equals(userId)) {
					return true;
				}
			}
		}
		return false;
	}

	private Client getByKeys(Long userId, String sessionId) {
		Client client = null;
		for (Map.Entry<String, Client> e : map().entrySet()) {
			Client c = e.getValue();
			if (c.getUserId().equals(userId) && c.getSessionId().equals(sessionId)) {
				client = c;
				break;
			}
		}
		return client;
	}

	public void invalidate(Long userId, String sessionId) {
		Client client = getByKeys(userId, sessionId);
		if (client != null) {
			Map<String, String> invalid = Application.get().getInvalidSessions();
			if (!invalid.containsKey(client.getSessionId())) {
				invalid.put(client.getSessionId(), client.getUid());
				exit(client);
			}
		}
	}

	public class ClientListener implements
			EntryAddedListener<String, Client>
			, EntryUpdatedListener<String, Client>
			, EntryRemovedListener<String, Client>
	{
		@Override
		public void entryAdded(EntryEvent<String, Client> event) {
			final String uid = event.getKey();
			synchronized (onlineClients) {
				if (onlineClients.containsKey(uid)) {
					onlineClients.get(uid).merge(event.getValue());
				} else {
					onlineClients.put(uid, event.getValue());
				}
			}
		}

		@Override
		public void entryUpdated(EntryEvent<String, Client> event) {
			synchronized (onlineClients) {
				onlineClients.get(event.getKey()).merge(event.getValue());
			}
		}

		@Override
		public void entryRemoved(EntryEvent<String, Client> event) {
			log.trace("ClientListener::Remove");
			onlineClients.remove(event.getKey());
		}
	}

	public class RoomListener implements
			EntryAddedListener<Long, Set<String>>
			, EntryUpdatedListener<Long, Set<String>>
			, EntryRemovedListener<Long, Set<String>>
	{
		@Override
		public void entryAdded(EntryEvent<Long, Set<String>> event) {
			log.trace("RoomListener::Add");
			onlineRooms.put(event.getKey(), event.getValue());
		}

		@Override
		public void entryUpdated(EntryEvent<Long, Set<String>> event) {
			log.trace("RoomListener::Update");
			onlineRooms.put(event.getKey(), event.getValue());
		}

		@Override
		public void entryRemoved(EntryEvent<Long, Set<String>> event) {
			log.trace("RoomListener::Remove");
			onlineRooms.remove(event.getKey(), event.getValue());
		}
	}
}
