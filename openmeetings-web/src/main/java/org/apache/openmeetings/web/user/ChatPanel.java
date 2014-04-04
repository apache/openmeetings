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
package org.apache.openmeetings.web.user;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Date;

import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.IWebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.plugins.emoticons.EmoticonsBehavior;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;

public class ChatPanel extends UserPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(ChatPanel.class, webAppRootKey);
	private static final long serialVersionUID = -9144707674886211557L;
	private static final String MESSAGE_AREA_ID = "messageArea";
	
	private JSONObject getMessage(ChatMessage m) throws JSONException {
		String msg = m.getMessage();
		msg = msg == null ? msg : " " + msg.replaceAll("&nbsp;", " ") + " ";
		return new JSONObject()
			.put("type", "chat")
			.put("msg", new JSONObject()
				.put("id", m.getId())
				.put("message", msg)
				.put("from", m.getFromUser().getFirstname() + " " + m.getFromUser().getLastname())
				.put("sent", getDateFormat().format(m.getSent()))
			);
	}

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		setMarkupId(id);

		add(new Behavior() {
			private static final long serialVersionUID = -2205036360048419129L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				ChatDao dao = getBean(ChatDao.class);
				try {				
					StringBuilder sb = new StringBuilder();
					//FIXME limited count should be loaded with "earlier" link
					for (ChatMessage m : dao.get(0, 30)) {
						sb.append("addChatMessageInternal(").append(getMessage(m).toString()).append(");");
					}
					if (sb.length() > 0) {
						sb.append("$('#").append(MESSAGE_AREA_ID).append("').emoticonize();");
						response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
					}
				} catch (JSONException e) {
					
				}
				super.renderHead(component, response);
			}
		});
		add(new EmoticonsBehavior("#" + MESSAGE_AREA_ID));
		add(new WebMarkupContainer("messages").setMarkupId(MESSAGE_AREA_ID));
		ChatToolbar toolbar = new ChatToolbar("toolbarContainer");
		final WysiwygEditor chatMessage = new WysiwygEditor("chatMessage", Model.of(""), toolbar);
		add(new Form<Void>("sendForm").add(
				toolbar
				, chatMessage.setOutputMarkupId(true)
				, new AjaxButton("send") {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						ChatDao dao = getBean(ChatDao.class);
						ChatMessage m = new ChatMessage();
						m.setMessage(unescapeXml(chatMessage.getDefaultModelObjectAsString()));
						m.setSent(new Date());
						m.setFromUser(getBean(UserDao.class).get(getUserId()));
						dao.update(m);
						IWebSocketConnectionRegistry reg = IWebSocketSettings.Holder.get(getApplication()).getConnectionRegistry();
						for (IWebSocketConnection c : reg.getConnections(getApplication())) {
							try {
								c.sendMessage(getMessage(m).toString());
							} catch(Exception e) {
								log.error("Error while sending message", e);
							}
						}
						chatMessage.setDefaultModelObject("");
						target.add(chatMessage);
					};
				}));
	}
}
