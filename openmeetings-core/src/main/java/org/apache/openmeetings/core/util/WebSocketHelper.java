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

import static org.apache.openmeetings.core.remote.ScopeApplicationAdapter.getApp;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.ws.WsMessageAll;
import org.apache.openmeetings.core.util.ws.WsMessageChat;
import org.apache.openmeetings.core.util.ws.WsMessageRoom;
import org.apache.openmeetings.core.util.ws.WsMessageRoomMsg;
import org.apache.openmeetings.core.util.ws.WsMessageUser;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class WebSocketHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(WebSocketHelper.class, getWebAppRootKey());
	public static final String ID_TAB_PREFIX = "chatTab-";
	public static final String ID_ALL = ID_TAB_PREFIX + "all";
	public static final String ID_ROOM_PREFIX = ID_TAB_PREFIX + "r";
	public static final String ID_USER_PREFIX = ID_TAB_PREFIX + "u";

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
		final FastDateFormat fullFmt = FormatHelper.getDateTimeFormat(curUser);
		final FastDateFormat dateFmt = FormatHelper.getDateFormat(curUser);
		final FastDateFormat timeFmt = FormatHelper.getTimeFormat(curUser);
		for (ChatMessage m : list) {
			String smsg = m.getMessage();
			smsg = smsg == null ? smsg : " " + smsg.replaceAll("&nbsp;", " ") + " ";
			JSONObject from = new JSONObject()
					.put("id", m.getFromUser().getId())
					.put("displayName", m.getFromName())
					.put("name", m.getFromUser().getDisplayName());
			if (uFmt != null) {
				uFmt.accept(from, m.getFromUser());
			}
			arr.put(setScope(new JSONObject(), m, curUser.getId())
				.put("id", m.getId())
				.put("message", smsg)
				.put("from", from)
				.put("actions", curUser.getId() == m.getFromUser().getId() ? "short" : "full")
				.put("sent", fullFmt.format(m.getSent()))
				.put("date", dateFmt.format(m.getSent()))
				.put("time", timeFmt.format(m.getSent()))
				);
		}
		return new JSONObject()
			.put("type", "chat")
			.put("msg", arr);
	}

	public static void sendClient(final Client _c, byte[] b) {
		if (_c != null) {
			send(a -> Arrays.asList(_c), (t, c) -> {
				try {
					t.sendMessage(b, 0, b.length);
				} catch (IOException e) {
					log.error("Error while broadcasting byte[] to room", e);
				}
			}, null);
		}
	}

	public static void send(IClusterWsMessage _m) {
		if (_m instanceof WsMessageRoomMsg) {
			sendRoom(((WsMessageRoomMsg)_m).getMsg(), false);
		} else if (_m instanceof WsMessageRoom) {
			WsMessageRoom m = (WsMessageRoom)_m;
			sendRoom(m.getRoomId(), m.getMsg(), false);
		} else if (_m instanceof WsMessageChat) {
			WsMessageChat m = (WsMessageChat)_m;
			sendRoom(m.getChatMessage(), m.getMsg(), false);
		} else if (_m instanceof WsMessageUser) {
			WsMessageUser m = (WsMessageUser)_m;
			sendUser(m.getUserId(), m.getMsg(), false);
		} else if (_m instanceof WsMessageAll) {
			sendAll(((WsMessageAll)_m).getMsg(), false);
		}
	}

	public static void sendRoom(final RoomMessage m) {
		sendRoom(m, true);
	}

	private static void sendRoom(final RoomMessage m, boolean publish) {
		if (publish) {
			publish(new WsMessageRoomMsg(m));
		}
		log.debug("Sending WebSocket message: {} {}", m.getType(), m instanceof TextRoomMessage ? ((TextRoomMessage)m).getText() : "");
		sendRoom(m.getRoomId(), (t, c) -> t.sendMessage(m), null);
	}

	public static void sendRoom(final Long roomId, final JSONObject m) {
		sendRoom(roomId, m, true);
	}

	private static void sendRoom(final Long roomId, final JSONObject m, boolean publish) {
		if (publish) {
			publish(new WsMessageRoom(roomId, m));
		}
		sendRoom(roomId, m, null, null);
	}

	public static void sendRoom(ChatMessage m, JSONObject msg) {
		sendRoom(m, msg, true);
	}

	private static void sendRoom(ChatMessage m, JSONObject msg, boolean publish) {
		if (publish) {
			publish(new WsMessageChat(m, msg));
		}
		sendRoom(m.getToRoom().getId(), msg
				, c -> !m.isNeedModeration() || (m.isNeedModeration() && c.hasRight(Right.moderator))
				, null);
	}

	public static void sendUser(final Long userId, final String m) {
		sendUser(userId, m, true);
	}

	private static void sendUser(final Long userId, final String m, boolean publish) {
		if (publish) {
			publish(new WsMessageUser(userId, m));
		}
		send(a -> ((IApplication)a).getOmBean(IClientManager.class).listByUser(userId), (t, c) -> {
			try {
				t.sendMessage(m);
			} catch (IOException e) {
				log.error("Error while sending message to user", e);
			}
		}, null);
	}

	public static void sendAll(final String m) {
		sendAll(m, true);
	}

	private static void sendAll(final String m, boolean publish) {
		if (publish) {
			publish(new WsMessageAll(m));
		}
		new Thread(() -> {
			Application app = (Application)getApp();
			WebSocketSettings settings = WebSocketSettings.Holder.get(app);
			IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
			Executor executor = settings.getWebSocketPushMessageExecutor(); // new NewThreadExecutor();
			for (IWebSocketConnection c : reg.getConnections(app)) {
				executor.run(() -> {
					try {
						c.sendMessage(m);
					} catch (IOException e) {
						log.error("Error while sending message to ALL", e);
					}
				});
			}
		}).start();
	}

	protected static void publish(IClusterWsMessage m) {
		IApplication app = getApp();
		new Thread(() -> {
			app.publishWsTopic(m);
		}).start();
	}

	protected static void sendRoom(final Long roomId, final JSONObject m, Predicate<Client> check, BiFunction<JSONObject, Client, String> func) {
		log.debug("Sending WebSocket message: {}", m);
		sendRoom(roomId, (t, c) -> {
			try {
				t.sendMessage(func == null ? m.toString() : func.apply(m, c));
			} catch (IOException e) {
				log.error("Error while broadcasting message to room", e);
			}
		}, check);
	}

	private static void sendRoom(final Long roomId, BiConsumer<IWebSocketConnection, Client> consumer, Predicate<Client> check) {
		send(a -> ((IApplication)a).getOmBean(IClientManager.class).listByRoom(roomId), consumer, check);
	}

	private static void send(
			final Function<Application, Collection<Client>> func
			, BiConsumer<IWebSocketConnection, Client> consumer
			, Predicate<Client> check)
	{
		new Thread(() -> {
			Application app = (Application)getApp();
			WebSocketSettings settings = WebSocketSettings.Holder.get(app);
			IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
			Executor executor = settings.getWebSocketPushMessageExecutor(); //new NewThreadExecutor();
			for (Client c : func.apply(app)) {
				if (check == null || check.test(c)) {
					final IWebSocketConnection wc = reg.getConnection(app, c.getSessionId(), new PageIdKey(c.getPageId()));
					if (wc != null && wc.isOpen()) {
						executor.run(() -> consumer.accept(wc, c));
					}
				}
			}
		}).start();
	}

	public static class NewThreadExecutor implements Executor {
		@Override
		public void run(Runnable command) {
			new Thread(() -> {
				command.run();
			}).start();
		}
	}
}
