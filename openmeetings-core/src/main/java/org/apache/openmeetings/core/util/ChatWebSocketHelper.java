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

import static org.apache.openmeetings.core.util.WebSocketHelper.alwaysTrue;
import static org.apache.openmeetings.core.util.WebSocketHelper.doSend;
import static org.apache.openmeetings.core.util.WebSocketHelper.publish;

import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.ws.WsMessageChat;
import org.apache.openmeetings.core.util.ws.WsMessageChat2All;
import org.apache.openmeetings.core.util.ws.WsMessageChat2User;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.util.ws.IClusterWsMessage;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class ChatWebSocketHelper {
	public static final String ID_TAB_PREFIX = "chatTab-";
	public static final String ID_ALL = ID_TAB_PREFIX + "all";
	public static final String ID_ROOM_PREFIX = ID_TAB_PREFIX + "r";
	public static final String ID_USER_PREFIX = ID_TAB_PREFIX + "u";

	private ChatWebSocketHelper() {
		// denied
	}

	private static JSONObject setScope(JSONObject o, ChatMessage m, long curUserId) {
		String scope, scopeName = null;
		if (m.getToUser() != null) {
			User u = curUserId == m.getToUser().getId() ? m.getFromUser() : m.getToUser();
			scope = ID_USER_PREFIX + u.getId();
			scopeName = u.getDisplayName();
		} else if (m.getToRoom() != null) {
			scope = ID_ROOM_PREFIX + m.getToRoom().getId();
			o.put("needModeration", m.isNeedModeration());
		} else {
			scope = ID_ALL;
		}
		return o.put("scope", scope).put("scopeName", scopeName);
	}

	public static JSONObject getMessage(User curUser, List<ChatMessage> list, BiConsumer<JSONObject, User> uFmt) {
		JSONArray arr = new JSONArray();
		for (ChatMessage m : list) {
			String smsg = m.getMessage();
			smsg = smsg == null ? smsg : " " + smsg.replace("&nbsp;", " ") + " ";
			JSONObject from = new JSONObject()
					.put("id", m.getFromUser().getId())
					.put("displayName", m.getFromName())
					.put("name", m.getFromUser().getDisplayName());
			if (uFmt != null) {
				uFmt.accept(from, m.getFromUser());
			}
			arr.put(setDates(setScope(new JSONObject(), m, curUser.getId())
					.put("id", m.getId())
					.put("message", smsg)
					.put("from", from)
					.put("actions", curUser.getId().equals(m.getFromUser().getId()) ? "short" : "full")
				, m, curUser, true));
		}
		return new JSONObject()
			.put("type", "chat")
			.put("msg", arr);
	}

	public static boolean send(IClusterWsMessage msg) {
		if (msg instanceof WsMessageChat chatMsg) {
			if (msg instanceof WsMessageChat2User userMsg) {
				sendUser(userMsg.getUserId(), userMsg.getChatMessage(), userMsg.getMsg(), false);
			} else if (msg instanceof WsMessageChat2All allMsg) {
				sendAll(allMsg.getChatMessage(), allMsg.getMsg(), false);
			} else {
				sendRoom(chatMsg.getChatMessage(), chatMsg.getMsg(), false);
			}
			return true;
		}
		return false;
	}

	public static void sendRoom(ChatMessage m, JSONObject msg) {
		sendRoom(m, msg, true);
	}

	private static JSONObject setDates(JSONObject o, ChatMessage m, User u, boolean immediate) {
		final FastDateFormat fullFmt = FormatHelper.getDateTimeFormat(u);
		final FastDateFormat dateFmt = FormatHelper.getDateFormat(u);
		final FastDateFormat timeFmt = FormatHelper.getTimeFormat(u);
		JSONObject obj = immediate ? o : o.getJSONArray("msg").getJSONObject(0);
		obj.put("sent", fullFmt.format(m.getSent()))
				.put("date", dateFmt.format(m.getSent()))
				.put("time", timeFmt.format(m.getSent()));
		return o;
	}

	private static void sendRoom(ChatMessage m, JSONObject msg, boolean publish) {
		if (publish) {
			publish(new WsMessageChat(m, msg));
		}
		WebSocketHelper.sendRoom(m.getToRoom().getId(), msg
				, c -> !m.isNeedModeration() || (m.isNeedModeration() && c.hasRight(Right.MODERATOR))
				, (o, c) -> setDates(o, m, c.getUser(), false));
	}

	public static void sendUser(final Long userId, ChatMessage m, JSONObject msg) {
		sendUser(userId, m, msg, true);
	}

	private static void sendUser(final Long userId, ChatMessage m, JSONObject msg, boolean publish) {
		if (publish) {
			publish(new WsMessageChat2User(userId, m, msg));
		}
		WebSocketHelper.sendUser(userId, msg, (o, c) -> setDates(o, m, c.getUser(), false), false);
	}

	public static void sendAll(ChatMessage m, JSONObject msg) {
		sendAll(m, msg, true);
	}

	private static void sendAll(ChatMessage m, JSONObject msg, boolean publish) {
		if (publish) {
			publish(new WsMessageChat2All(m, msg));
		}
		WebSocketHelper.send(a -> ((IApplication)a).getBean(IClientManager.class).stream()
				, (t, c) -> doSend(t, c, msg, (o, cm) -> setDates(o, m, c.getUser(), false), "all"), alwaysTrue());
	}
}
