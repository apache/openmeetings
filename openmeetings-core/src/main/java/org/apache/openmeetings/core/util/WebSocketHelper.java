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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class WebSocketHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(WebSocketHelper.class, webAppRootKey);

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
