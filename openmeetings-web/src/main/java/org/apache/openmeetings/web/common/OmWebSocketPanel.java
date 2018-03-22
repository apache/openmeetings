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

import static org.apache.openmeetings.core.remote.KurentoHandler.KURENTO_TYPE;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.core.remote.KurentoHandler;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public abstract class OmWebSocketPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OmWebSocketPanel.class);
	private static final String CONNECTED_MSG = "socketConnected";
	private final AbstractAjaxTimerBehavior pingTimer = new AbstractAjaxTimerBehavior(Duration.seconds(30)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			log.debug("Sending WebSocket PING");
			WebSocketHelper.sendClient(getWsClient(), new byte[]{getUserId().byteValue()});
		}
	};
	private final WebSocketBehavior wsBehavior = new WebSocketBehavior() {
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
			if ("socketConnected".equals(msg.getText())) {
				OmWebSocketPanel.this.onConnect(handler);
				log.debug("WebSocketBehavior:: pingTimer is attached");
				pingTimer.restart(handler);
			} else {
				final JSONObject m;
				try {
					m = new JSONObject(msg.getText());
					if (KURENTO_TYPE.equals(m.optString("type"))) {
						kHandler.onMessage(getWsClient(), m);
					} else {
						OmWebSocketPanel.this.onMessage(handler, m);
					}
				} catch (Exception e) {
					//no-op
				}
			}
		}

		@Override
		protected void onAbort(AbortedMessage msg) {
			super.onAbort(msg);
			closeHandler(msg);
		}

		@Override
		protected void onClose(ClosedMessage msg) {
			super.onClose(msg);
			closeHandler(msg);
		}

		@Override
		protected void onError(WebSocketRequestHandler handler, ErrorMessage msg) {
			super.onError(handler, msg);
			closeHandler(msg);
		}
	};
	@SpringBean
	private transient KurentoHandler kHandler;

	public OmWebSocketPanel(String id) {
		super(id);
		add(pingTimer, wsBehavior);
		pingTimer.stop(null);
	}

	protected abstract IWsClient getWsClient();

	protected void onConnect(ConnectedMessage message) {
	}

	protected void onConnect(WebSocketRequestHandler handler) {
	}

	protected void closeHandler(AbstractClientMessage msg) {
		log.debug("WebSocketBehavior::closeHandler {}", msg);
	}

	protected void onMessage(WebSocketRequestHandler handler, JSONObject m) {
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return null;
	}
}
