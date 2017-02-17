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
package org.apache.openmeetings.core.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class WebSocketHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(WebSocketHelper.class, webAppRootKey);
	public static final String ID_TAB_PREFIX = "chatTab-";
	public static final String ID_ALL = ID_TAB_PREFIX + "all";
	public static final String ID_ROOM_PREFIX = ID_TAB_PREFIX + "r";
	public static final String ID_USER_PREFIX = ID_TAB_PREFIX + "u";

	public static void sendClient(final Client c, byte[] b) {
		if (c != null) {
			send(a -> Arrays.asList(c), (t) -> {
				try {
					t.sendMessage(b, 0, b.length);
				} catch (IOException e) {
					log.error("Error while broadcasting byte[] to room", e);
				}
			}, null);
		}
	}

	public static void sendRoom(final RoomMessage m) {
		log.debug("Sending WebSocket message: {} {}", m.getType(), m instanceof TextRoomMessage ? ((TextRoomMessage)m).getText() : "");
		sendRoom(m.getRoomId(), (t) -> t.sendMessage(m), null);
	}

	public static void sendRoom(final Long roomId, final String m) {
		sendRoom(roomId, m, null);
	}

	private static JSONObject setScope(JSONObject o, ChatMessage m, long curUserId) {
		String scope, scopeName = null;
		if (m.getToUser() != null) {
			User u = curUserId == m.getToUser().getId() ? m.getFromUser() : m.getToUser();
			scope = ID_USER_PREFIX + u.getId();
			scopeName = String.format("%s %s", u.getFirstname(), u.getLastname());
		} else if (m.getToRoom() != null) {
			scope = ID_ROOM_PREFIX + m.getToRoom().getId();
			o.put("needModeration", m.isNeedModeration());
		} else {
			scope = ID_ALL;
		}
		return o.put("scope", scope).put("scopeName", scopeName);
	}

	public static JSONObject getMessage(long curUserId, List<ChatMessage> list, FastDateFormat fmt, BiConsumer<JSONObject, User> uFmt) {
		JSONArray arr = new JSONArray();
		for (ChatMessage m : list) {
			String smsg = m.getMessage();
			smsg = smsg == null ? smsg : " " + smsg.replaceAll("&nbsp;", " ") + " ";
			JSONObject from = new JSONObject()
					.put("id", m.getFromUser().getId())
					.put("name", m.getFromUser().getFirstname() + " " + m.getFromUser().getLastname());
			if (uFmt != null) {
				uFmt.accept(from, m.getFromUser());
			}
			arr.put(setScope(new JSONObject(), m, curUserId)
				.put("id", m.getId())
				.put("message", smsg)
				.put("from", from)
				.put("actions", curUserId == m.getFromUser().getId() ? "short" : "full")
				.put("sent", fmt.format(m.getSent())));
		}
		return new JSONObject()
			.put("type", "chat")
			.put("msg", arr);
	}

	public static void sendRoom(ChatMessage m, JSONObject msg) {
		sendRoom(m.getToRoom().getId(), msg.toString()
				, c -> !m.isNeedModeration() || (m.isNeedModeration() && c.hasRight(Right.moderator)));
	}

	public static void sendRoom(final Long roomId, final String m, Predicate<Client> check) {
		log.debug("Sending WebSocket message: {}", m);
		sendRoom(roomId, (t) -> {
			try {
				t.sendMessage(m);
			} catch (IOException e) {
				log.error("Error while broadcasting message to room", e);
			}
		}, check);
	}

	public static void sendUser(final Long userId, final String m) {
		send(a -> ((IApplication)a).getOmClients(userId), (t) -> {
			try {
				t.sendMessage(m);
			} catch (IOException e) {
				log.error("Error while sending message to user", e);
			}
		}, null);
	}

	//TODO should this be unified???
	public static void sendAll(final String m) {
		Application app = Application.get(OpenmeetingsVariables.wicketApplicationName);
		WebSocketSettings settings = WebSocketSettings.Holder.get(app);
		IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
		Executor executor = settings.getWebSocketPushMessageExecutor();
		for (IWebSocketConnection c : reg.getConnections(app)) {
			executor.run(() -> {
				try {
					c.sendMessage(m);
				} catch (IOException e) {
					log.error("Error while sending message to ALL", e);
				}
			});
		}
	}

	public static void sendRoom(final Long roomId, Consumer<IWebSocketConnection> consumer, Predicate<Client> check) {
		send(a -> ((IApplication)a).getOmRoomClients(roomId), consumer, check);
	}

	public static void send(
			final Function<Application, List<Client>> func
			, Consumer<IWebSocketConnection> consumer
			, Predicate<Client> check)
	{
		Application app = Application.get(OpenmeetingsVariables.wicketApplicationName);
		WebSocketSettings settings = WebSocketSettings.Holder.get(app);
		IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
		Executor executor = settings.getWebSocketPushMessageExecutor();
		for (Client c : func.apply(app)) {
			if (check == null || (check != null && check.test(c))) {
				final IWebSocketConnection wc = reg.getConnection(app, c.getSessionId(), new PageIdKey(c.getPageId()));
				if (wc != null && wc.isOpen()) {
					executor.run(() -> consumer.accept(wc));
				}
			}
		}
	}
}
