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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimerService {
	private static final Logger log = LoggerFactory.getLogger(TimerService.class);
	private int modCheckInterval = 5;
	private final User sysUser = new User();
	private final Map<Long, CompletableFuture<Object>> modCheckMap = new ConcurrentHashMap<>();

	@Autowired
	private ClientManager cm;

	@PostConstruct
	private void init() {
		sysUser.setId(-1L);
		sysUser.setDisplayName("System");
	}

	private void doModCheck(Long roomId) {
		modCheckMap.put(
				roomId
				, new CompletableFuture<>().completeAsync(() -> {
					log.warn("Moderator room check {}", roomId);
					if (cm.listByRoom(roomId).isEmpty()) {
						modCheckMap.remove(roomId);
					} else {
						WebSocketHelper.sendRoom(new TextRoomMessage(roomId, sysUser, RoomMessage.Type.MODERATOR_IN_ROOM, "" + !cm.listByRoom(roomId, c -> c.hasRight(Right.MODERATOR)).isEmpty()));
						doModCheck(roomId);
					}
					return null;
				}, delayedExecutor(modCheckInterval, TimeUnit.SECONDS)));
	}

	public void scheduleModCheck(Room r) {
		if (r.isModerated() && r.isWaitModerator() && !cm.listByRoom(r.getId()).isEmpty() && !modCheckMap.containsKey(r.getId())) {
			doModCheck(r.getId());
		}
	}
}
