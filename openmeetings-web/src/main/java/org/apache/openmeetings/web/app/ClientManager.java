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
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.pages.auth.SignInPage.TOKEN_PARAM;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.mediaserver.KurentoHandler;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.query.Predicates;

import jakarta.inject.Inject;

@Component
public class ClientManager implements IClientManager {
	private static final Logger log = LoggerFactory.getLogger(ClientManager.class);
	private static final String ROOMS_KEY = "ROOMS_KEY";
	private static final String ONLINE_USERS_KEY = "ONLINE_USERS_KEY";
	private static final String SERVERS_KEY = "SERVERS_KEY";
	private static final String INSTANT_TOKENS_KEY = "INSTANT_TOKENS_KEY";
	private static final String UID_BY_SID_KEY = "UID_BY_SID_KEY";
	private final Map<String, Client> onlineClients = new ConcurrentHashMap<>();
	private final Map<Long, Set<String>> onlineRooms = new ConcurrentHashMap<>();
	private final Map<String, ServerInfo> onlineServers = new ConcurrentHashMap<>();

	@Inject
	private ConferenceLogDao confLogDao;
	@Inject
	private Application app;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private TimerService timerService;

	private IMap<String, Client> map() {
		return app.hazelcast.getMap(ONLINE_USERS_KEY);
	}

	private Map<String, String> mapBySid() {
		return app.hazelcast.getMap(UID_BY_SID_KEY);
	}

	private IMap<Long, Set<String>> rooms() {
		return app.hazelcast.getMap(ROOMS_KEY);
	}

	private IMap<String, ServerInfo> servers() {
		return app.hazelcast.getMap(SERVERS_KEY);
	}

	private IMap<String, InstantToken> tokens() {
		return app.hazelcast.getMap(INSTANT_TOKENS_KEY);
	}

	void init() {
		log.debug("Cluster:: PostConstruct");
		onlineClients.putAll(map());
		onlineRooms.putAll(rooms());
		onlineServers.putAll(servers());
		map().addEntryListener(new ClientListener(), true);
		rooms().addEntryListener(new RoomListener(), true);
		servers().addEntryListener((EntryUpdatedListener<String, ServerInfo>)(event -> {
			log.debug("Cluster:: Server was updated {} -> {}", event.getKey(), event.getValue());
			onlineServers.put(event.getKey(), event.getValue());
		}), true);
	}

	public void add(Client c) {
		confLogDao.add(
				ConferenceLog.Type.CLIENT_CONNECT
				, c.getUserId(), "0", null
				, c.getRemoteAddress()
				, "");
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

	public void exitRoom(Client c) {
		exitRoom(c, true);
	}

	public void exitRoom(Client c, boolean update) {
		Long roomId = c.getRoomId();
		log.debug("Removing online room client: {}, room: {}", c.getUid(), roomId);
		if (roomId != null) {
			IMap<Long, Set<String>> rooms = rooms();
			rooms.lock(roomId);
			Set<String> clients = rooms.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
			clients.remove(c.getUid());
			rooms.put(roomId, clients);
			onlineRooms.put(roomId, clients);
			rooms.unlock(roomId);
			if (clients.isEmpty()) {
				String serverId = c.getServerId();
				IMap<String, ServerInfo> servers = servers();
				servers.lock(serverId);
				ServerInfo si = servers.get(serverId);
				si.remove(c.getRoom());
				servers.put(serverId, si);
				onlineServers.put(serverId, si);
				servers.unlock(serverId);
			}
			kHandler.leaveRoom(c);
			c.setRoom(null);
			c.clear();
			if (update) {
				update(c);
			}

			sendRoom(new TextRoomMessage(roomId, c, RoomMessage.Type.ROOM_EXIT, c.getUid()));
			confLogDao.add(
					ConferenceLog.Type.ROOM_LEAVE
					, c.getUserId(), "0", roomId
					, c.getRemoteAddress()
					, String.valueOf(roomId));
		}
	}

	@Override
	public void exit(Client c) {
		if (c != null) {
			confLogDao.add(
					ConferenceLog.Type.CLIENT_DISCONNECT
					, c.getUserId(), "0", null
					, c.getRemoteAddress()
					, "");
			exitRoom(c, false);
			kHandler.remove(c);
			log.debug("Removing online client: {}, roomId: {}", c.getUid(), c.getRoomId());
			map().remove(c.getUid());
			onlineClients.remove(c.getUid());
			mapBySid().remove(c.getSid());
		}
	}

	public void serverAdded(String serverId, String url) {
		onlineServers.computeIfAbsent(serverId, id -> {
			ServerInfo si = new ServerInfo(url);
			servers().put(id, si);
			log.debug("Cluster:: server with id '{}' was added", id);
			return si;
		});
	}

	public void serverRemoved(String serverId) {
		Map<String, Client> clients = map();
		for (Map.Entry<String, Client> e : clients.entrySet()) {
			if (serverId.equals(e.getValue().getServerId())) {
				exit(e.getValue());
			}
		}
		log.debug("Cluster:: server with id '{}' was removed", serverId);
		servers().remove(serverId);
		onlineServers.remove(serverId);
	}

	/**
	 * This method will return count of users in room _after_ adding
	 *
	 * @param c - client to be added to the room
	 * @return count of users in room _after_ adding
	 */
	public int addToRoom(Client c) {
		Room r = c.getRoom();
		Long roomId = r.getId();
		confLogDao.add(
				ConferenceLog.Type.ROOM_ENTER
				, c.getUserId(), "0", roomId
				, c.getRemoteAddress()
				, String.valueOf(roomId));
		log.debug("Adding online room client: {}, room: {}", c.getUid(), roomId);
		IMap<Long, Set<String>> rooms = rooms();
		rooms.lock(roomId);
		Set<String> set = rooms.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
		set.add(c.getUid());
		final int count = set.size();
		rooms.put(roomId, set);
		onlineRooms.put(roomId, set);
		rooms.unlock(roomId);
		String serverId = c.getServerId();
		addRoomToServer(serverId, r);
		update(c);
		timerService.scheduleSipCheck(r);
		return count;
	}

	private void addRoomToServer(String serverId, Room r) {
		if (!onlineServers.get(serverId).getRooms().contains(r.getId())) {
			log.debug("Cluster:: room {} was not found for server '{}', adding ...", r.getId(), serverId);
			IMap<String, ServerInfo> servers = servers();
			servers.lock(serverId);
			ServerInfo si = servers.get(serverId);
			si.add(r);
			servers.put(serverId, si);
			onlineServers.put(serverId, si);
			servers.unlock(serverId);
		}
	}

	public boolean isOnline(Long userId) {
		boolean isUserOnline = false;
		for (Map.Entry<String, Client> e : map().entrySet()) {
			if (e.getValue().sameUserId(userId)) {
				isUserOnline = true;
				break;
			}
		}
		return isUserOnline;
	}

	@Override
	public Stream<Client> stream() {
		return map().values().stream();
	}

	@Override
	public Collection<Client> listByUser(Long userId) {
		return map().values(Predicates.equal("userId", userId));
	}

	@Override
	public Stream<Client> streamByRoom(Long roomId) {
		return Optional.ofNullable(roomId)
			.map(id -> onlineRooms.getOrDefault(id, Set.of()))
			.stream()
			.flatMap(Set::stream)
			.map(this::get)
			.filter(Objects::nonNull);
	}

	public boolean isInRoom(long roomId, long userId) {
		return Optional.of(roomId)
			.map(id -> onlineRooms.getOrDefault(id, Set.of()))
			.stream()
			.flatMap(Set::stream)
			.map(this::get)
			.anyMatch(c -> c != null && c.sameUserId(userId));
	}

	private List<Client> getByKeys(Long userId, String sessionId) {
		return map().values().stream()
				.filter(c -> c.sameUserId(userId) && c.getSessionId().equals(sessionId))
				.toList();
	}

	public void invalidate(Long userId, String sessionId) {
		for (Client c : getByKeys(userId, sessionId)) {
			Map<String, String> invalid = Application.get().getInvalidSessions();
			invalid.putIfAbsent(sessionId, c.getUid());
			exit(c);
		}
	}

	private String getServerUrl(Map.Entry<String, ServerInfo> e, Room r, UnaryOperator<String> generator) {
		final String curServerId = app.getServerId();
		String serverId = e.getKey();
		if (!curServerId.equals(serverId)) {
			addRoomToServer(serverId, r);
			return generator.apply(e.getValue().getUrl());
		}
		return null;
	}

	public String getServerUrl(Room r, UnaryOperator<String> inGenerator) {
		if (onlineServers.size() == 1) {
			log.debug("Cluster:: The only server found");
			return null;
		}
		UnaryOperator<String> generator = inGenerator == null ? baseUrl -> {
			String uuid = UUID.randomUUID().toString();
			tokens().put(uuid, new InstantToken(getUserId(), r.getId()));
			return Application.urlForPage(SignInPage.class, new PageParameters().add(TOKEN_PARAM, uuid), baseUrl);
		} : inGenerator;
		Optional<Map.Entry<String, ServerInfo>> existing = onlineServers.entrySet().stream()
				.filter(e -> e.getValue().getRooms().contains(r.getId()))
				.findFirst();
		if (existing.isPresent()) {
			return getServerUrl(existing.get(), r, generator);
		}
		Optional<Map.Entry<String, ServerInfo>> min = onlineServers.entrySet().stream()
				.min((e1, e2) -> e1.getValue().getCapacity() - e2.getValue().getCapacity());
		return getServerUrl(min.get(), r, generator);
	}

	Optional<InstantToken> getToken(StringValue uuid) {
		log.debug("Cluster:: Checking token {}, full list: {}", uuid, tokens().entrySet());
		return uuid.isEmpty() ? Optional.empty() : Optional.ofNullable(tokens().remove(uuid.toString()));
	}

	public class ClientListener implements
			EntryAddedListener<String, Client>
			, EntryUpdatedListener<String, Client>
			, EntryRemovedListener<String, Client>
	{
		private void process(EntryEvent<String, Client> event, boolean shouldAdd) {
			if (event.getMember().localMember()) {
				return;
			}
			final String uid = event.getKey();
			synchronized (onlineClients) {
				if (onlineClients.containsKey(uid)) {
					onlineClients.get(uid).merge(event.getValue());
				} else if (shouldAdd) {
					onlineClients.put(uid, event.getValue());
				}
			}
		}

		@Override
		public void entryAdded(EntryEvent<String, Client> event) {
			process(event, true);
		}

		@Override
		public void entryUpdated(EntryEvent<String, Client> event) {
			process(event, false);
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

	private static class ServerInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private int capacity = 0;
		private final String url;
		private final Set<Long> rooms = new HashSet<>();

		public ServerInfo(String url) {
			this.url = url;
		}

		public void add(Room r) {
			if (rooms.add(r.getId())) {
				log.debug("Cluster:: room {} is added to server, whole list {}", r.getId(), rooms);
				capacity += r.getCapacity();
			}
		}

		public void remove(Room r) {
			if (rooms.remove(r.getId())) {
				log.debug("Cluster:: room {} is removed from server, whole list {}", r.getId(), rooms);
				capacity -= r.getCapacity();
			}
		}

		public String getUrl() {
			return url;
		}

		public int getCapacity() {
			return capacity;
		}

		public Set<Long> getRooms() {
			return rooms;
		}

		@Override
		public String toString() {
			return "ServerInfo[rooms: " + rooms + "]";
		}
	}

	public static class InstantToken implements Serializable {
		private static final long serialVersionUID = 1L;
		private final long userId;
		private final long roomId;
		private final long created;

		InstantToken(long userId, long roomId) {
			this.userId = userId;
			this.roomId = roomId;
			created = System.currentTimeMillis();
		}

		public long getUserId() {
			return userId;
		}

		public long getRoomId() {
			return roomId;
		}
	}
}
