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

import static org.apache.openmeetings.web.app.Application.getHazelcast;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.util.ws.RoomMessage.Type;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;
import com.hazelcast.map.IMap;

@Component
public class QuickPollManager {
	private static final Logger log = LoggerFactory.getLogger(QuickPollManager.class);
	private static final String QPOLLS_KEY = "QPOLLS_KEY";

	private static IMap<Long, Map<Long, Boolean>> map() {
		return getHazelcast().getMap(QPOLLS_KEY);
	}

	public boolean isStarted(Long roomId) {
		return map().containsKey(roomId);
	}

	public void start(Client c) {
		Long roomId = c.getRoomId();
		if (!c.hasRight(Room.Right.PRESENTER) || isStarted(roomId)) {
			return;
		}
		log.debug("Starting quick poll, room: {}", roomId);
		IMap<Long, Map<Long, Boolean>> polls = map();
		polls.lock(roomId);
		polls.putIfAbsent(roomId, new ConcurrentHashMap<>());
		polls.unlock(roomId);
		WebSocketHelper.sendRoom(new TextRoomMessage(roomId, c, Type.QUICK_POLL_UPDATED, c.getUid()));
	}

	public void vote(Client c, boolean vote) {
		Long roomId = c.getRoomId();
		IMap<Long, Map<Long, Boolean>> polls = map();
		polls.lock(roomId);
		if (polls.containsKey(roomId)) {
			Map<Long, Boolean> votes = map().get(roomId);
			if (!votes.containsKey(c.getUserId())) {
				votes.put(c.getUserId(), vote);
				polls.put(roomId,  votes);
				WebSocketHelper.sendRoom(new TextRoomMessage(roomId, c, Type.QUICK_POLL_UPDATED, c.getUid()));
			}
		}
		polls.unlock(roomId);
	}

	public void close(Client c) {
		Long roomId = c.getRoomId();
		if (!c.hasRight(Room.Right.PRESENTER) || !isStarted(roomId)) {
			return;
		}
		map().remove(roomId);
		WebSocketHelper.sendRoom(new TextRoomMessage(roomId, c, Type.QUICK_POLL_UPDATED, c.getUid()));
	}

	public JSONObject toJson(Long roomId) {
		boolean started = isStarted(roomId);
		JSONObject o = new JSONObject().put("started", started);
		if (started) {
			Map<Long, Boolean> votes = map().get(roomId);
			o.put("voted", votes.containsKey(getUserId()));
			o.put("pros", votes.entrySet().stream().filter(Entry::getValue).count())
				.put("cons", votes.entrySet().stream().filter(e -> !e.getValue()).count());
		}
		return o;
	}
}
