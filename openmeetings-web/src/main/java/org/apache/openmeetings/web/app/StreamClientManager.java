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

import static org.apache.openmeetings.core.remote.ScopeApplicationAdapter.getApp;
import static org.apache.openmeetings.db.dao.room.SipDao.SIP_FIRST_NAME;
import static org.apache.openmeetings.db.dao.room.SipDao.SIP_USER_NAME;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getHazelcast;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.MobileService;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IStreamClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.Server;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handle {@link StreamClient} objects.
 *
 * Use a kind of decorator pattern to inject the {@link Server} into every call.
 *
 * @author sebawagner
 *
 */
@Component
public class StreamClientManager implements IStreamClientManager {
	protected static final Logger log = Red5LoggerFactory.getLogger(StreamClientManager.class, getWebAppRootKey());
	private static final String STREAM_CLIENT_KEY = "STREAM_CLIENT_KEY";

	@Autowired
	private ClientManager clientManager;
	@Autowired
	private StreamClientManager streamClientManager;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MobileService mobileService;
	@Autowired
	private RoomDao roomDao;

	public Map<String, StreamClient> map() {
		return getHazelcast().getMap(STREAM_CLIENT_KEY);
	}

	@Override
	public StreamClient add(StreamClient c) {
		if (c == null) {
			return null;
		}
		IApplication iapp = getApp();
		c.setServerId(iapp.getServerId());
		c.setConnectedSince(new Date());
		c.setRoomEnter(new Date());
		update(c);
		return c;
	}

	public Collection<StreamClient> list() {
		return map().values();
	}

	@Override
	public StreamClient get(String uid) {
		if (uid == null) {
			return null;
		}
		return map().get(uid);
	}

	@Override
	public IClient update(IClient c) {
		if (c instanceof StreamClient) {
			map().put(c.getUid(), (StreamClient)c);
		} else {
			clientManager.update((Client)c);
		}
		return c;
	}

	private static boolean hasVideo(StreamClient rcl) {
		return rcl != null && rcl.getAvsettings().contains("v");
	}

	private static boolean hasVideo(Client c) {
		return c != null && c.hasActivity(Activity.broadcastV);
	}

	@Override
	public StreamClient update(StreamClient rcl, boolean forceSize) {
		if (rcl == null) {
			return null;
		}
		Client client = clientManager.getBySid(rcl.getSid());
		if (client == null) {
			if (Client.Type.mobile == rcl.getType()) {
				Sessiondata sd = sessionDao.check(rcl.getSid());
				User u = userDao.get(sd.getUserId());
				rcl = mobileService.create(rcl, u);
				//Mobile client enters the room
				client = new Client(rcl, userDao.get(rcl.getUserId()));
				clientManager.add(client);
				if (rcl.getRoomId() != null) {
					client.setCam(0);
					client.setMic(0);
					client.setRoom(roomDao.get(rcl.getRoomId()));
					clientManager.addToRoom(client);
					WebSocketHelper.sendRoom(new RoomMessage(client.getRoom().getId(), client, RoomMessage.Type.roomEnter));
				}
			} else if (client == null && Client.Type.sip == rcl.getType()) {
				rcl.setLogin(SIP_USER_NAME);
				rcl.setUserId(SIP_USER_ID);
				//SipTransport enters the room
				User u = new User();
				u.setId(SIP_USER_ID);
				u.setLogin(SIP_USER_NAME);
				u.setFirstname(SIP_FIRST_NAME);
				client = new Client(rcl, u);
				clientManager.add(client);
				client.setCam(0);
				client.setMic(0);
				client.allow(Room.Right.audio, Room.Right.video);
				client.set(Activity.broadcastA);
				client.setRoom(roomDao.get(rcl.getRoomId()));
				clientManager.addToRoom(client);
				WebSocketHelper.sendRoom(new RoomMessage(client.getRoom().getId(), client, RoomMessage.Type.roomEnter));
			} else {
				return null;
			}
		}
		if (rcl.getRoomId() == null || !rcl.getRoomId().equals(client.getRoom().getId())) {
			return null;
		}
		User u = client.getUser();
		rcl.setUserId(u.getId());
		rcl.setRoomType(client.getRoomType());
		rcl.setLogin(u.getLogin());
		rcl.setFirstname(u.getFirstname());
		rcl.setLastname(u.getLastname());
		rcl.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
		rcl.setSuperMod(client.hasRight(Right.superModerator));
		rcl.setMod(client.hasRight(Right.moderator));
		if (client.hasActivity(Activity.broadcastA) && !client.isMicEnabled()) {
			client.remove(Activity.broadcastA);
		}
		if (client.hasActivity(Activity.broadcastV) && !client.isCamEnabled()) {
			client.remove(Activity.broadcastV);
		}
		if (client.hasActivity(Activity.broadcastA) || client.hasActivity(Activity.broadcastV)) {
			if (forceSize || rcl.getWidth() == 0 || rcl.getHeight() == 0) {
				rcl.setWidth(client.getWidth());
				rcl.setHeight(client.getHeight());
			}
			StringBuilder sb = new StringBuilder();
			if (client.hasActivity(Activity.broadcastA)) {
				sb.append('a');
			}
			if (client.hasActivity(Activity.broadcastV)) {
				sb.append('v');
			}
			if (!rcl.isBroadcasting() || hasVideo(rcl) != hasVideo(client)) {
				rcl.setBroadcasting(true);
			}
			rcl.setAvsettings(sb.toString());
		} else {
			rcl.setAvsettings("n");
			rcl.setBroadcasting(false);
		}
		clientManager.update(client);
		streamClientManager.update(rcl);
		return rcl;
	}

	@Override
	public boolean remove(String uid) {
		if (uid == null) {
			return false;
		}
		StreamClient c = map().remove(uid);
		return c != null;
	}

	@Override
	public List<StreamClient> list(Long roomId) {
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && Client.Type.sharing != c.getType())
				.collect(Collectors.toList());
	}

	@Override
	public long getRecordingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isRecordingStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getPublishingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isPublishStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getSharingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isSharingStarted())
				.collect(Collectors.toList()).size();
	}

	@Override
	public long getBroadcastingCount(Long roomId) {
		if (roomId == null) {
			return 0;
		}
		return list().stream()
				.filter(c -> roomId.equals(c.getRoomId()) && c.isBroadcasting() && c.getBroadcastId() != null)
				.collect(Collectors.toList()).size();
	}

	@Override
	public Set<Long> getActiveRoomIds(String serverId) {
		Set<Long> ids = new HashSet<>();
		if (serverId != null) {
			for (Map.Entry<String, StreamClient> e : map().entrySet()) {
				if (serverId.equals(e.getValue().getServerId())) {
					ids.add(e.getValue().getRoomId());
				}
			}
		}
		return ids;
	}

	public void clean(String serverId) {
		Map<String, StreamClient> streams = map();
		for (Iterator<Map.Entry<String, StreamClient>> iter = streams.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry<String, StreamClient> e = iter.next();
			if (serverId.equals(e.getValue().getServerId())) {
				iter.remove();
			}
		}
	}
}
