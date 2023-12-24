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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.mediaserver.KurentoHandler.KURENTO_TYPE;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.AbortedMessage;
import org.apache.wicket.protocol.ws.api.message.AbstractClientMessage;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.ErrorMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.apache.openmeetings.mediaserver.KurentoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

import jakarta.inject.Inject;

public abstract class OmWebSocketPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OmWebSocketPanel.class);
	public static final String CONNECTED_MSG = "socketConnected";
	private final AtomicBoolean connected = new AtomicBoolean();

	@Inject
	private KurentoHandler kHandler;
	private boolean pingable = false;

	protected OmWebSocketPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(newWsBehavior(), new Behavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				if (!pingable) {
					log.debug("pingTimer is attached");
					pingable = true;
					super.renderHead(component, response);
					response.render(OnDomReadyHeaderItem.forScript("OmUtil.ping();"));
				}
			}
		});
	}

	private WebSocketBehavior newWsBehavior() {
		return new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(
						String.format("Wicket.Event.subscribe(Wicket.Event.Topic.WebSocket.Opened, function() {Wicket.WebSocket.send('%s');});",CONNECTED_MSG)
						, "ws-connected-script"));
			}

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				OmWebSocketPanel.this.onConnect(message);
			}

			@Override
			protected void onMessage(WebSocketRequestHandler handler, TextMessage msg) {
				if (CONNECTED_MSG.equals(msg.getText())) {
					if (connected.compareAndSet(false, true)) {
						OmWebSocketPanel.this.onConnect(handler);
					}
				} else {
					final JSONObject m;
					try {
						m = new JSONObject(msg.getText());
						switch(m.optString("type", "")) {
							case KURENTO_TYPE:
								kHandler.onMessage(getWsClient(), m);
								break;
							case "mic":
								micMessage(m);
								break;
							case "ping":
								log.trace("Sending WebSocket PING");
								handler.appendJavaScript("OmUtil.ping();");
								WebSocketHelper.sendClient(getWsClient(), new byte[]{getUserId() == null ? 0 : getUserId().byteValue()});
								break;
							default:
								OmWebSocketPanel.this.onMessage(handler, m);
						}
					} catch (Exception e) {
						log.error("Error while processing incoming message", e);
					}
				}
			}

			@Override
			protected void onAbort(AbortedMessage msg) {
				closeHandler(msg);
			}

			@Override
			protected void onClose(ClosedMessage msg) {
				closeHandler(msg);
			}

			@Override
			protected void onError(WebSocketRequestHandler handler, ErrorMessage msg) {
				closeHandler(msg);
			}

			private void micMessage(final JSONObject m) {
				IWsClient curClient = getWsClient();
				if (!(curClient instanceof Client)) {
					return;
				}
				Client c = (Client)curClient;
				if (c.getRoomId() == null) {
					return;
				}
				WebSocketHelper.sendRoomOthers(c.getRoomId(), c.getUid(), m.put("uid", c.getUid()));
			}
		};
	}

	protected abstract IWsClient getWsClient();

	/**
	 * Override this method to add your own logic
	 *
	 * @param message - connected message
	 */
	protected void onConnect(ConnectedMessage message) {
	}

	/**
	 * Override this method to add your own logic
	 *
	 * @param handler - handler to perform some updates
	 */
	protected void onConnect(WebSocketRequestHandler handler) {
	}

	protected void closeHandler(AbstractClientMessage msg) {
		log.debug("WebSocketBehavior::closeHandler {}", msg);
		kHandler.remove(getWsClient());
	}

	/**
	 * Override this method to add your own logic
	 *
	 * @param handler - handler to perform some updates
	 * @param m - incoming message as {@link JSONObject}
	 *
	 * @throws IOException in case some IO operation fails
	 */
	protected void onMessage(WebSocketRequestHandler handler, JSONObject m) throws IOException {
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return null;
	}
}
