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

import static java.util.concurrent.CompletableFuture.delayedExecutor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import jakarta.annotation.PostConstruct;

import org.apache.openmeetings.core.sip.SipManager;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.wicket.ThreadContext;
import org.apache.openmeetings.mediaserver.KurentoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import jakarta.inject.Inject;

@Service
public class TimerService {
	private static final Logger log = LoggerFactory.getLogger(TimerService.class);
	private int modCheckInterval = 5;
	private int sipCheckInterval = 2;
	private final User sysUser = new User();
	private final Map<Long, CompletableFuture<Object>> modCheckMap = new ConcurrentHashMap<>();
	private final Map<Long, CompletableFuture<Object>> sipCheckMap = new ConcurrentHashMap<>();

	@Inject
	private ClientManager cm;
	@Inject
	private SipManager sipManager;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private Application app;

	@PostConstruct
	private void init() {
		sysUser.setId(-5L);
		sysUser.setDisplayName("System");
	}

	private void doModCheck(Long roomId) {
		modCheckMap.put(
				roomId
				, new CompletableFuture<>().completeAsync(() -> {
					ThreadContext.setApplication(app);
					log.warn("Moderator room check {}", roomId);
					if (cm.streamByRoom(roomId).findAny().isEmpty()) {
						modCheckMap.remove(roomId);
					} else {
						WebSocketHelper.sendRoom(new TextRoomMessage(roomId, sysUser, RoomMessage.Type.MODERATOR_IN_ROOM
								, "" + !cm.streamByRoom(roomId).filter(c -> c.hasRight(Right.MODERATOR)).findAny().isEmpty()));
						doModCheck(roomId);
					}
					return null;
				}, delayedExecutor(modCheckInterval, TimeUnit.SECONDS)));
	}

	private void doSipCheck(Long roomId) {
		sipCheckMap.put(
				roomId
				, new CompletableFuture<>().completeAsync(() -> {
					ThreadContext.setApplication(app);
					log.trace("Sip room check {}", roomId);
					Optional<Client> sipClient = cm.streamByRoom(roomId).filter(Client::isSip).findAny();
					cm.streamByRoom(roomId).filter(Predicate.not(Client::isSip)).findAny().ifPresentOrElse(c -> {
						updateSipLastName(sipClient, c.getRoom());
						doSipCheck(roomId);
					}, () -> {
						log.warn("No more clients in the room {}", roomId);
						sipCheckMap.remove(roomId);
						sipClient.ifPresent(cm::exit);
					});
					return null;
				}, delayedExecutor(sipCheckInterval, TimeUnit.SECONDS)));
	}

	private void updateSipLastName(Optional<Client> sipClient, Room r) {
		long count = sipManager.countUsers(r.getConfno());
		final String newLastName = "(" + count + ")";
		sipClient.ifPresentOrElse(c -> {
			if (!newLastName.equals(c.getUser().getLastname())) {
				c.getUser().setLastname(newLastName).resetDisplayName();
				cm.update(c);
				WebSocketHelper.sendRoom(new TextRoomMessage(r.getId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
			}
		}, () -> {
			User sipUser = sipManager.getSipUser(r);
			sipUser.setLastname(newLastName).resetDisplayName();
			Client c = new Client("-- unique - sip - session --", 1, sipUser, sipUser.getPictureUri());
			c.allow(Right.VIDEO, Right.AUDIO);
			cm.add(c);
			c.setRoom(r);
			cm.addToRoom(c);
			WebSocketHelper.sendRoom(new TextRoomMessage(r.getId(), c, RoomMessage.Type.ROOM_ENTER, c.getUid()));
		});
		kHandler.updateSipCount(r, count);
	}

	public void scheduleModCheck(Room r) {
		if (r.isModerated() && r.isWaitModerator() && !modCheckMap.containsKey(r.getId())) {
			doModCheck(r.getId());
		}
	}

	public void scheduleSipCheck(Room r) {
		// sip allowed and configured
		if (sipManager.getSipUser(r) != null && r.isSipEnabled() && !sipCheckMap.containsKey(r.getId())) {
			doSipCheck(r.getId());
		}
	}
}
