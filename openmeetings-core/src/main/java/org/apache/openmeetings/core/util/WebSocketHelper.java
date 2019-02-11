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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.ws.WsMessageAll;
import org.apache.openmeetings.core.util.ws.WsMessageRoom;
import org.apache.openmeetings.core.util.ws.WsMessageRoomMsg;
import org.apache.openmeetings.core.util.ws.WsMessageUser;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class WebSocketHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(WebSocketHelper.class, getWebAppRootKey());

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

	public static void send(IClusterWsMessage msg) {
		if (msg instanceof WsMessageRoomMsg) {
			sendRoom(((WsMessageRoomMsg)msg).getMsg(), false);
		} else if (msg instanceof WsMessageRoom) {
			WsMessageRoom m = (WsMessageRoom)msg;
			sendRoom(m.getRoomId(), m.getMsg(), false);
		} else if (msg instanceof WsMessageUser) {
			WsMessageUser m = (WsMessageUser)msg;
			sendUser(m.getUserId(), m.getMsg(), null, false);
		} else if (msg instanceof WsMessageAll) {
			sendAll(((WsMessageAll)msg).getMsg(), false);
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

	public static void sendUser(final Long userId, final JSONObject m) {
		sendUser(userId, m, null, true);
	}

	static void sendUser(final Long userId, final JSONObject m, BiFunction<JSONObject, Client, JSONObject> func, boolean publish) {
		if (publish) {
			publish(new WsMessageUser(userId, m));
		}
		send(a -> ((IApplication)a).getOmBean(IClientManager.class).listByUser(userId)
				, (t, c) -> doSend(t, c, m, func, "user"), null);
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
				executor.run(() -> doSend(c, m, "ALL"));
			}
		}).start();
	}

	protected static void publish(IClusterWsMessage m) {
		IApplication app = getApp();
		new Thread(() -> {
			app.publishWsTopic(m);
		}).start();
	}

	protected static void sendRoom(final Long roomId, final JSONObject m, Predicate<Client> check, BiFunction<JSONObject, Client, JSONObject> func) {
		log.debug("Sending WebSocket message: {}", m);
		sendRoom(roomId, (t, c) -> doSend(t, c, m, func, "room"), check);
	}

	static void doSend(IWebSocketConnection conn, Client c, JSONObject msg, BiFunction<JSONObject, Client, JSONObject> func, String suffix) {
		doSend(conn, (func == null ? msg : func.apply(msg, c)).toString(new NullStringer()), suffix);
	}

	private static void doSend(IWebSocketConnection c, String msg, String suffix) {
		try {
			c.sendMessage(msg);
		} catch (IOException e) {
			log.error("Error while sending message to {}", suffix, e);
		}
	}

	private static void sendRoom(final Long roomId, BiConsumer<IWebSocketConnection, Client> consumer, Predicate<Client> check) {
		send(a -> ((IApplication)a).getOmBean(IClientManager.class).listByRoom(roomId), consumer, check);
	}

	static void send(
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
