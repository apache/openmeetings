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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;
import static org.apache.openmeetings.web.room.RoomPanel.broadcast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.Client.Right;
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
	private static final String PARAM_UID = "uid";
	private static final String ACTION = "action";
	private static final String PARAM_ROOM_ID = "roomid";
	private enum Action {
		accept, decline, close
	};
	private static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		};
	};
	private final Map<String, Activity> activities = new LinkedHashMap<>();
	private final RoomPanel room;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final AbstractDefaultAjaxBehavior action = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String uid = getRequest().getRequestParameters().getParameterValue(PARAM_UID).toString(); 
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				assert(room.getRoom().getId().equals(roomId));
				Action action = Action.valueOf(getRequest().getRequestParameters().getParameterValue(ACTION).toString());
				Activity a = activities.get(uid);
				if (a != null) {
					switch (action) {
						case close:
							remove(uid, target);
							break;
						case decline:
							if (isModerator(getUserId(), roomId)) {
								broadcast(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, uid));
							}
							break;
						case accept:
							if (isModerator(getUserId(), roomId)) {
								switch (a.getType()) {
									case requestRightModerator:
										Client client = null;
										for (Client c : getRoomUsers(room.getRoom().getId())) { //FIXME TODO add Map somewhere
											if (c.getUid().equals(uid)) {
												client = c;
												break;
											}
										}
										if (client != null) {
											client.getRights().add(Right.moderator);
											broadcast(new TextRoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.activityRemove, uid));
											broadcast(new RoomMessage(room.getRoom().getId(), getUserId(), RoomMessage.Type.rightUpdated));
										}
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
			response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("activityAction", this, explicit(PARAM_ROOM_ID), explicit(ACTION), explicit(PARAM_UID)), "activityAction")));
		}
	};
	private ListView<Activity> lv = new ListView<Activity>("activities", new ArrayList<Activity>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<Activity> item) {
			Activity a = item.getModelObject();
			String text = "";
			Long roomId = room.getRoom().getId();
			Component accept = new WebMarkupContainer("accept").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.accept.name(), a.getUid()))).setVisible(false);
			Component decline = new WebMarkupContainer("decline").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.decline.name(), a.getUid()))).setVisible(false);
			switch (a.getType()) {
				case roomEnter:
					text = ""; // TODO should this be fixed?
					item.setVisible(false);
					break;
				case roomExit:
				{
					User u = getBean(UserDao.class).get(a.getSender());
					text = String.format("%s %s %s [%s]", u.getFirstname(), u.getLastname(), getString("1367"), df.get().format(a.getCreated()));
				}
					break;
				case requestRightModerator:
				{
					User u = getBean(UserDao.class).get(a.getSender());
					text = String.format("%s %s %s [%s]", u.getFirstname(), u.getLastname(), getString("room.action.request.right.moderator"), df.get().format(a.getCreated()));
					accept.setVisible(true);
					decline.setVisible(true);
				}
				//ask question 693
					break;
			}
			item.add(new WebMarkupContainer("close").add(new AttributeAppender("onclick", String.format("activityAction(%s, '%s', '%s');", roomId, Action.close.name(), a.getUid()))));
			item.add(accept, decline, new Label("text", text));
			item.add(AttributeAppender.append("class", getClass(a)));
		}
		
		private String getClass(Activity a) {
			switch (a.getType()) {
				case requestRightModerator:
					return "ui-state-highlight";
				case roomEnter:
				case roomExit:
			}
			return "ui-state-default";
		}
	};

	public void add(Activity a, IPartialPageRequestHandler handler) {
		activities.put(a.getUid(), a);
		update(handler);
		handler.appendJavaScript("hightlightActivities();");
	}

	public void remove(String uid, IPartialPageRequestHandler handler) {
		activities.remove(uid);
		update(handler);
	}

	public void update(IPartialPageRequestHandler handler) {
		lv.setList(new ArrayList<>(activities.values()));
		handler.add(container);
	}
	
	public ActivitiesPanel(String id, RoomPanel room) {
		super(id);
		this.room = room;
		setVisible(!room.getRoom().isHidden(RoomElement.Activities));
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		add(container.add(lv).setOutputMarkupId(true));
		add(action);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ActivitiesPanel.class, "activities.js"))));
		response.render(CssHeaderItem.forUrl("css/activities.css", "screen"));
	}
}
