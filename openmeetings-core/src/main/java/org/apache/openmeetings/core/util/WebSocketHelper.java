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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.ws.WsMessageAll;
import org.apache.openmeetings.core.util.ws.WsMessageRoom;
import org.apache.openmeetings.core.util.ws.WsMessageRoomMsg;
import org.apache.openmeetings.core.util.ws.WsMessageRoomOthers;
import org.apache.openmeetings.core.util.ws.WsMessageUser;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IWsClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class WebSocketHelper {
	private static final Logger log = LoggerFactory.getLogger(WebSocketHelper.class);
	public static final <T> Predicate<T> alwaysTrue() {
		return x -> true;
	}

	private WebSocketHelper() {
		// denied
	}

	public static void sendClient(final IWsClient omClient, byte[] b) {
		if (omClient != null) {
			sendClient(omClient, c -> {
				try {
					c.sendMessage(b, 0, b.length);
				} catch (Exception e) {
					log.error("Error while sending binary message to client", e);
				}
			});
		}
	}

	public static void sendClient(final IWsClient omClient, JSONObject msg) {
		log.trace("Sending WebSocket message to Client: {} -> {}", omClient, msg);
		if (omClient != null) {
			sendClient(omClient, c -> {
				try {
					c.sendMessage(msg.toString());
				} catch (Exception e) {
					log.error("Error while sending message to client", e);
				}
			});
		}
	}

	public static void sendClient(final IWsClient omClient, final RoomMessage m) {
		log.trace("Sending WebSocket message to client: {} {}", m.getType(), m instanceof TextRoomMessage txtMsg ? txtMsg.getText() : "");
		sendClient(omClient, c -> {
			try {
				c.sendMessage(m);
			} catch (Exception e) {
				log.error("Error while sending message to client", e);
			}
		});
	}

	public static IApplication getApp() {
		return (IApplication)Application.get(getWicketApplicationName());
	}

	private static void sendClient(IWsClient client, Consumer<IWebSocketConnection> wsc) {
		new Thread(() -> {
			Application app = (Application)getApp();
			WebSocketSettings settings = WebSocketSettings.Holder.get(app);
			IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
			Executor executor = settings.getWebSocketPushMessageExecutor();
			final IWebSocketConnection wc = reg.getConnection(app, client.getSessionId(), new PageIdKey(client.getPageId()));
			if (wc != null && wc.isOpen()) {
				executor.run(() -> wsc.accept(wc));
			}
		}).start();
	}

	public static boolean send(IClusterWsMessage msg) {
		if (msg instanceof WsMessageRoomMsg msgRoom) {
			sendRoom(msgRoom.msg(), false);
		} else if (msg instanceof WsMessageRoomOthers msgRoomOthers) {
			sendRoomOthers(msgRoomOthers.getRoomId(), msgRoomOthers.getUid(), msgRoomOthers.getMsg(), false);
		} else if (msg instanceof WsMessageRoom roomMsg) {
			sendRoom(roomMsg.getRoomId(), roomMsg.getMsg(), false);
		} else if (msg instanceof WsMessageUser msgUser) {
			sendUser(msgUser.getUserId(), msgUser.getMsg(), null, false);
		} else if (msg instanceof WsMessageAll msgAll) {
			sendAll(msgAll.msg(), false);
		}
		return true;
	}

	public static void sendRoom(final RoomMessage m) {
		sendRoom(m, true);
	}

	private static void sendRoom(final RoomMessage m, boolean publish) {
		if (publish) {
			publish(new WsMessageRoomMsg(m));
		}
		log.trace("Sending WebSocket message to room: {} {}", m.getType(), m instanceof TextRoomMessage txtMsg ? txtMsg.getText() : "");
		sendRoom(m.getRoomId(), (t, c) -> t.sendMessage(m), alwaysTrue());
	}

	public static void sendServer(final RoomMessage m) {
		log.trace("Sending WebSocket message to All: {}", m);
		sendAll(c -> {
			try {
				c.sendMessage(m);
			} catch (Exception e) {
				log.error("Error while sending message to Server", e);
			}
		});
	}

	public static void sendRoom(final Long roomId, final JSONObject m) {
		sendRoom(roomId, m, true);
	}

	private static void sendRoom(final Long roomId, final JSONObject m, boolean publish) {
		if (publish) {
			publish(new WsMessageRoom(roomId, m));
		}
		sendRoom(roomId, m, alwaysTrue(), null);
	}

	public static void sendRoomOthers(final Long roomId, final String uid, final JSONObject m) {
		sendRoomOthers(roomId, uid, m, true);
	}

	private static void sendRoomOthers(final Long roomId, final String uid, final JSONObject m, boolean publish) {
		if (publish) {
			publish(new WsMessageRoomOthers(roomId, uid, m));
		}
		sendRoom(roomId, m, c -> !uid.equals(c.getUid()), null);
	}

	public static void sendUser(final Long userId, final JSONObject m) {
		sendUser(userId, m, null, true);
	}

	static void sendUser(final Long userId, final JSONObject m, BiFunction<JSONObject, Client, JSONObject> func, boolean publish) {
		if (publish) {
			publish(new WsMessageUser(userId, m));
		}
		send(a -> ((IApplication)a).getBean(IClientManager.class).listByUser(userId).stream()
				, (t, c) -> doSend(t, c, m, func, "user"), alwaysTrue());
	}

	public static void sendAll(final String m) {
		sendAll(m, true);
	}

	private static void sendAll(final String m, boolean publish) {
		if (publish) {
			publish(new WsMessageAll(m));
		}
		log.trace("Sending text WebSocket message to All: {}", m);
		sendAll(c -> doSend(c, m, "ALL"));
	}

	private static void sendAll(Consumer<IWebSocketConnection> sender) {
		new Thread(() -> {
			Application app = (Application)getApp();
			if (app == null) {
				return; // Application is not ready
			}
			WebSocketSettings settings = WebSocketSettings.Holder.get(app);
			IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
			Executor executor = settings.getWebSocketPushMessageExecutor();
			for (IWebSocketConnection wc : reg.getConnections(app)) {
				if (wc != null && wc.isOpen()) {
					executor.run(() -> sender.accept(wc));
				}
			}
		}).start();
	}

	public static void publish(@Nonnull IClusterWsMessage m) {
		IApplication app = getApp();
		new Thread(() -> app.publishWsTopic(m)).start();
	}

	public static void sendRoom(final Long roomId, final JSONObject m, Predicate<Client> check, BiFunction<JSONObject, Client, JSONObject> func) {
		log.trace("Sending json WebSocket message to room: {}", m);
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
		send(a -> ((IApplication)a).getBean(IClientManager.class).streamByRoom(roomId), consumer, check);
	}

	static void send(
			final Function<Application, Stream<Client>> func
			, BiConsumer<IWebSocketConnection, Client> consumer
			, Predicate<Client> check)
	{
		new Thread(() -> {
			Application app = (Application)getApp();
			if (app == null) {
				return; // Application is not ready
			}
			WebSocketSettings settings = WebSocketSettings.Holder.get(app);
			IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
			Executor executor = settings.getWebSocketPushMessageExecutor();
			func.apply(app)
					.filter(check)
					.forEach(c -> {
						final IWebSocketConnection wc = reg.getConnection(app, c.getSessionId(), new PageIdKey(c.getPageId()));
						if (wc != null && wc.isOpen()) {
							executor.run(() -> consumer.accept(wc, c));
						}
					});
		}).start();
	}
}
