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
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

import jakarta.inject.Inject;

public class ActivitiesPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ActivitiesPanel.class);
	private static final String PARAM_ID = "id";
	private static final String ACTION = "action";
	private static final String PARAM_ROOM_ID = "roomid";
	private static final String ACTIVITY_FMT = "%s %s [%s]";
	private static final String ACTIVITY_FMT_RTL = "%3$s %2$s [%1$s]";
	private enum Action {
		ACCEPT, DECLINE, CLOSE;

		public static Action of(StringValue val) {
			if (val.isEmpty()) {
				return null;
			}
			return Action.valueOf(val.toString().toUpperCase(Locale.ROOT));
		}
	}
	private static final FastDateFormat df = FastDateFormat.getInstance("HH:mm:ss");
	private final Map<String, Activity> activities = new LinkedHashMap<>();
	private final RoomPanel room;
	private final AbstractDefaultAjaxBehavior actionBehavior = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		private TextRoomMessage getRemoveMsg(String id) {
			return new TextRoomMessage(room.getRoom().getId(), room.getClient(), RoomMessage.Type.ACTIVITY_REMOVE, id);
		}

		@Override
		protected void respond(AjaxRequestTarget target) {
			if (!isVisible()) {
				return;
			}
			try {
				String id = getRequest().getRequestParameters().getParameterValue(PARAM_ID).toString();
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				Action act = Action.of(getRequest().getRequestParameters().getParameterValue(ACTION));
				Activity a = activities.get(id);
				if (a == null || !room.getRoom().getId().equals(roomId)) {
					log.error("It seems like we are being hacked!!!!");
					return;
				}
				switch (act) {
					case CLOSE:
						remove(target, id);
						break;
					case DECLINE:
						if (room.getClient().hasRight(Right.MODERATOR)) {
							sendRoom(getRemoveMsg(id));
						}
						break;
					case ACCEPT:
						Client client = cm.get(a.getUid());
						if (room.getClient().hasRight(Right.MODERATOR) && client != null && client.getRoom() != null && roomId == client.getRoom().getId()) {
							switch (a.getType()) {
								case REQ_RIGHT_MODERATOR:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.MODERATOR);
									break;
								case REQ_RIGHT_AV:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.AUDIO, Right.VIDEO);
									break;
								case REQ_RIGHT_PRESENTER:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.PRESENTER);
									break;
								case REQ_RIGHT_WB:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.WHITEBOARD);
									break;
								case REQ_RIGHT_SHARE:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.SHARE);
									break;
								case REQ_RIGHT_REMOTE:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.REMOTE_CONTROL);
									break;
								case REQ_RIGHT_A:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.AUDIO);
									break;
								case REQ_RIGHT_MUTE_OTHERS:
									sendRoom(getRemoveMsg(id));
									room.allowRight(client, Right.MUTE_OTHERS);
									break;
								default:
									break;
							}
						}
						break;
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

	@Inject
	private ClientManager cm;

	public ActivitiesPanel(String id, RoomPanel room) {
		super(id);
		this.room = room;
		setVisible(!room.getRoom().isHidden(RoomElement.ACTIVITIES));
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		add(actionBehavior);
	}

	private boolean shouldSkip(final boolean self, final Activity a) {
		return !self && a.getType().isAction() && !room.getClient().hasRight(Right.MODERATOR);
	}

	public void add(Activity a, IPartialPageRequestHandler handler) {
		if (!isVisible()) {
			return;
		}
		final boolean self = getUserId().equals(a.getSender());
		if (shouldSkip(self, a)) {
			return;
		}
		if (a.getType().isAction()) {
			remove(handler, activities.entrySet().parallelStream()
				.filter(e -> a.getSender().equals(e.getValue().getSender()) && a.getType() == e.getValue().getType())
				.map(e -> e.getValue().getId())
				.toArray(String[]::new));
		}
		activities.put(a.getId(), a);
		String text = "";
		final String name = self ? getString("1362") : a.getName();
		final String fmt = ((BasePage)getPage()).isRtl() ? ACTIVITY_FMT_RTL : ACTIVITY_FMT;
		switch (a.getType()) {
			case ROOM_ENTER:
				text = String.format(fmt, name, getString("activities.msg.enter"), df.format(a.getCreated()));
				break;
			case ROOM_EXIT:
				text = String.format(fmt, name, getString("activities.msg.exit"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_MODERATOR:
				text = String.format(fmt, name, getString("activities.request.right.moderator"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_PRESENTER:
				text = String.format(fmt, name, getString("activities.request.right.presenter"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_WB:
				text = String.format(fmt, name, getString("activities.request.right.wb"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_SHARE:
				text = String.format(fmt, name, getString("activities.request.right.share"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_REMOTE:
				text = String.format(fmt, name, getString("activities.request.right.remote"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_A:
				text = String.format(fmt, name, getString("activities.request.right.audio"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_AV:
				text = String.format(fmt, name, getString("activities.request.right.video"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_MUTE_OTHERS:
				text = String.format(fmt, name, getString("activities.request.right.muteothers"), df.format(a.getCreated()));
				break;
			case REQ_RIGHT_HAVE_QUESTION:
				text = String.format(fmt, name, getString("activities.ask.question"), df.format(a.getCreated()));
				break;
			default:
				break;
		}
		final JSONObject aobj = new JSONObject()
			.put("id", a.getId())
			.put("uid", a.getUid())
			.put("cssClass", getClass(a))
			.put("text", text)
			.put("action", a.getType().isAction())
			.put("find", false);

		switch (a.getType()) {
			case REQ_RIGHT_MODERATOR, REQ_RIGHT_PRESENTER, REQ_RIGHT_WB, REQ_RIGHT_SHARE, REQ_RIGHT_REMOTE
					, REQ_RIGHT_A, REQ_RIGHT_AV, REQ_RIGHT_MUTE_OTHERS:
				aobj.put("accept", room.getClient().hasRight(Right.MODERATOR));
				aobj.put("decline", room.getClient().hasRight(Right.MODERATOR));
				break;
			case REQ_RIGHT_HAVE_QUESTION:
				aobj.put("find", !self);
			case ROOM_ENTER, ROOM_EXIT:
				aobj.put("accept", false);
				aobj.put("decline", false);
				break;
			default:
				break;
		}
		handler.appendJavaScript(new StringBuilder("Activities.add(").append(aobj.toString()).append(");"));
	}

	public void remove(IPartialPageRequestHandler handler, String...ids) {
		if (ids.length < 1) {
			return;
		}
		JSONArray arr = new JSONArray();
		for (String id : ids) {
			arr.put(id);
			activities.remove(id);
		}
		handler.appendJavaScript(String.format("Activities.remove(%s);", arr));
	}

	private static CharSequence getClass(Activity a) {
		StringBuilder cls = new StringBuilder();
		switch (a.getType()) {
			case REQ_RIGHT_MODERATOR, REQ_RIGHT_PRESENTER, REQ_RIGHT_WB, REQ_RIGHT_SHARE, REQ_RIGHT_REMOTE
					, REQ_RIGHT_A, REQ_RIGHT_AV, REQ_RIGHT_MUTE_OTHERS, REQ_RIGHT_HAVE_QUESTION:
				cls.append("bg-warning");
				break;
			case ROOM_ENTER, ROOM_EXIT:
				cls.append("bg-white auto-clean");
				break;
			default:
				break;
		}
		return cls;
	}
}
