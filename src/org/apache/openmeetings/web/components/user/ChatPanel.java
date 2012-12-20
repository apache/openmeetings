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
package org.apache.openmeetings.web.components.user;

import java.io.Serializable;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.ws.IWebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ChatPanel extends UserPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(ChatPanel.class, OpenmeetingsVariables.webAppRootKey);
	private static final long serialVersionUID = -9144707674886211557L;
	private String message;
	
	private class ChatMessage extends TextMessage implements IWebSocketPushMessage, Serializable {
		private static final long serialVersionUID = -3802182673895471248L;

		public ChatMessage(String msg) {
			super(msg);
		}
	}
	
	public ChatPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		setMarkupId(id);
		
		add(new WebMarkupContainer("messages").setMarkupId("messageArea"));
		final Form<Void> f = new Form<Void>("sendForm");
		f.add(new TextArea<String>("message", new PropertyModel<String>(ChatPanel.this, "message")).setOutputMarkupId(true));
		f.add(new Button("send").add(new AjaxFormSubmitBehavior("onclick"){
			private static final long serialVersionUID = -3746739738826501331L;
			
			protected void onSubmit(AjaxRequestTarget target) {
				//Application.getBean(ChatService) sendMessageToOverallChat
				new WebSocketPushBroadcaster(IWebSocketSettings.Holder.get(getApplication()).getConnectionRegistry())
					.broadcastAll(getApplication(), new ChatMessage(message));
				/*for (IWebSocketConnection c : IWebSocketSettings.Holder.get(getApplication()).getConnectionRegistry().getConnections(getApplication())) {
					try {
						c.sendMessage(message);
					} catch(Exception e) {
						log.error("Error while sending message", e);
					}
				}*/
				ChatPanel.this.message = "";
				target.add(f);
			};
		}));
		add(f.setOutputMarkupId(true));
	}

}
