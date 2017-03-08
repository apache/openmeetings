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
package org.apache.openmeetings.web.room.activities;

import static org.apache.openmeetings.core.util.WebSocketHelper.sendRoom;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getOnlineClient;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ActivitiesPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ActivitiesPanel.class, webAppRootKey);
	private static final String PARAM_ID = "id";
	private static final String ACTION = "action";
	private static final String PARAM_ROOM_ID = "roomid";
	private enum Action {
		accept, decline, close
	};
	private static final FastDateFormat df = FastDateFormat.getInstance("HH:mm:ss");
	private final Map<String, Activity> activities = new LinkedHashMap<>();
	private final RoomPanel room;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final AbstractDefaultAjaxBehavior action = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String id = getRequest().getRequestParameters().getParameterValue(PARAM_ID).toString();
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				assert(room.getRoom().getId().equals(roomId));
				Action action = Action.valueOf(getRequest().getRequestParameters().getParameterValue(ACTION).toString());
				Activity a = activities.get(id);
				if (a != null) {
					switch (action) {
						case close:
							remove(id, target);
							break;
						case decline:
							if (room.getClient().hasRight(Right.moderator)) {
								sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
							}
							break;
						case accept:
							Client client = getOnlineClient(a.getUid());
							if (room.getClient().hasRight(Right.moderator) && client != null && roomId == client.getRoomId()) {
								switch (a.getType()) {
									case reqRightModerator:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.moderator);
										break;
									case reqRightAv:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.audio, Right.video);
										break;
									case reqRightWb:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.whiteBoard);
										break;
									case reqRightShare:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.share);
										break;
									case reqRightRemote:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.remoteControl);
										break;
									case reqRightA:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.audio);
										break;
									case reqRightMute:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.mute);
										break;
									case reqRightExclusive:
										sendRoom(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, id));
										room.allowRight(client, Right.exclusive);
										break;
									default:
										break;
								}
							}
							break;
					}
				} else {
					log.error("It seems like we are being hacked!!!!");
				}
			} catch (Exception e) {
				log.error("Unexpected exception while processing activity action", e);
			}
		}

		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			super.renderHead(component, response);
			response.render(new PriorityHeaderItem(getNamedFunction("activityAction", this, explicit(PARAM_ROOM_ID), explicit(ACTION), explicit(PARAM_ID))));
		}
	};
	private ListView<Activity> lv = new ListView<Activity>("activities", new ArrayList<Activity>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<Activity> item) {
			Activity a = item.getModelObject();
			String text = "";
			Long roomId = room.getRoom().getId();
			Component accept = new WebMarkupContainer("accept").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.accept.name(), a.getId())));
			Component decline = new WebMarkupContainer("decline").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.decline.name(), a.getId())));
			switch (a.getType()) {
				case reqRightModerator:
				case reqRightWb:
				case reqRightShare:
				case reqRightRemote:
				case reqRightA:
				case reqRightAv:
				case reqRightMute:
				case reqRightExclusive:
					if (room.getClient().hasRight(Right.moderator)) {
						accept.setVisible(true);
						decline.setVisible(true);
					}
					break;
				case haveQuestion:
				case roomEnter:
				case roomExit:
					accept.setVisible(false);
					decline.setVisible(false);
					break;
			}
			User u = getBean(UserDao.class).get(a.getSender());
			String name = getUserId().equals(a.getSender()) ? getString("1362") : String.format("%s %s", u.getFirstname(), u.getLastname());
			switch (a.getType()) {
				case roomEnter:
					text = ""; // TODO should this be fixed?
					item.setVisible(false);
					break;
				case roomExit:
					text = String.format("%s %s [%s]", name, getString("1367"), df.format(a.getCreated()));
					break;
				case reqRightModerator:
					text = String.format("%s %s [%s]", name, getString("room.action.request.right.moderator"), df.format(a.getCreated()));
					break;
				case reqRightWb:
					text = String.format("%s %s [%s]", name, getString("694"), df.format(a.getCreated()));
					break;
				case reqRightShare:
					text = String.format("%s %s [%s]", name, getString("1070"), df.format(a.getCreated()));
					break;
				case reqRightRemote:
					text = String.format("%s %s [%s]", name, getString("1082"), df.format(a.getCreated()));
					break;
				case reqRightA:
					text = String.format("%s %s [%s]", name, getString("1603"), df.format(a.getCreated()));
					break;
				case reqRightAv:
					text = String.format("%s %s [%s]", name, getString("695"), df.format(a.getCreated()));
					break;
				case reqRightMute:
					text = String.format("%s %s [%s]", name, getString("1399"), df.format(a.getCreated()));//TODO un-mute 1398
					break;
				case reqRightExclusive:
					text = String.format("%s %s [%s]", name, getString("1427"), df.format(a.getCreated()));
					break;
				case haveQuestion:
					text = String.format("%s %s [%s]", name, getString("693"), df.format(a.getCreated()));
					break;
			}
			item.add(new WebMarkupContainer("close").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.close.name(), a.getId()))));
			item.add(accept, decline, new Label("text", text));
			item.add(AttributeAppender.append("class", getClass(a)));
		}

		private String getClass(Activity a) {
			String cls = "ui-state-default";
			switch (a.getType()) {
				case reqRightModerator:
				case reqRightWb:
				case reqRightShare:
				case reqRightRemote:
				case reqRightA:
				case reqRightAv:
				case reqRightMute:
				case reqRightExclusive:
				case haveQuestion:
					cls = "ui-state-highlight";
					break;
				case roomEnter:
				case roomExit:
			}
			return cls;
		}
	};

	public void add(Activity a, IPartialPageRequestHandler handler) {
		activities.put(a.getId(), a);
		update(handler);
		if (isVisible()) {
			handler.appendJavaScript("Activities.hightlight();");
		}
	}

	public void remove(String uid, IPartialPageRequestHandler handler) {
		activities.remove(uid);
		update(handler);
	}

	public void update(IPartialPageRequestHandler handler) {
		if (isVisible()) {
			lv.setList(new ArrayList<>(activities.values()));
			handler.add(container);
		}
	}

	public ActivitiesPanel(String id, RoomPanel room) {
		super(id);
		this.room = room;
		setVisible(!room.getRoom().isHidden(RoomElement.Activities));
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		add(container.add(lv).setOutputMarkupPlaceholderTag(true));
		add(action);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ActivitiesPanel.class, "activities.js"))));
		response.render(CssHeaderItem.forUrl("css/activities.css", "screen"));
	}
}
