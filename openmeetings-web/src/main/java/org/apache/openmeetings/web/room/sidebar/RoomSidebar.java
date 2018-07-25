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
package org.apache.openmeetings.web.room.sidebar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.kickUser;
import static org.apache.openmeetings.web.room.RoomBroadcaster.sendUpdatedClient;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.ArrayList;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder.ConfirmableBorderDialog;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.RoomPanel.Action;
import org.apache.openmeetings.web.room.VideoSettings;
import org.apache.openmeetings.web.room.activities.ActivitiesPanel;
import org.apache.openmeetings.web.room.activities.Activity;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.JQueryUIBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class RoomSidebar extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomSidebar.class, getWebAppRootKey());
	public static final String FUNC_TOGGLE_RIGHT = "toggleRight";
	public static final String FUNC_TOGGLE_ACTIVITY = "toggleActivity";
	public static final String FUNC_ACTION = "roomAction";
	public static final String FUNC_SETTINGS = "avSettings";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_ACTIVITY = "activity";
	public static final String PARAM_RIGHT = "right";
	public static final String PARAM_UID = "uid";
	public static final String PARAM_SETTINGS = "s";
	private final RoomPanel room;
	private UploadDialog upload;
	private RoomFilePanel roomFiles;
	private final WebMarkupContainer userList = new WebMarkupContainer("user-list");
	private final WebMarkupContainer fileTab = new WebMarkupContainer("file-tab");
	private final SelfIconsPanel selfRights;
	private ConfirmableAjaxBorder confirmKick;
	private boolean showFiles;
	private boolean avInited = false;
	private Client kickedClient;
	private VideoSettings settings = new VideoSettings("settings");
	private ActivitiesPanel activities;
	private final ListView<Client> users = new ListView<Client>("user", new ArrayList<Client>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<Client> item) {
			item.add(new RoomClientPanel("user", item, room));
		}
	};
	private final AbstractDefaultAjaxBehavior roomAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String uid = getRequest().getRequestParameters().getParameterValue(PARAM_UID).toString();
				if (Strings.isEmpty(uid)) {
					return;
				}
				ClientManager cm = getBean(ClientManager.class);
				Client cl = room.getClient();
				Action a = Action.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
				switch (a) {
					case kick:
						if (cl.hasRight(Right.moderator)) {
							kickedClient = cm.get(uid);
							if (kickedClient == null) {
								return;
							}
							if (!kickedClient.hasRight(Right.superModerator) && !cl.getUid().equals(kickedClient.getUid())) {
								confirmKick.getDialog().open(target);
							}
						}
						break;
					case exclusive:
						if (room.getClient().hasRight(Right.exclusive)) {
							WebSocketHelper.sendRoom(new TextRoomMessage(room.getRoom().getId(), cl, RoomMessage.Type.exclusive, uid));
						}
						break;
					case mute:
					{
						JSONObject obj = uid.isEmpty() ? new JSONObject() : new JSONObject(uid);
						Client _c = cm.get(obj.getString("uid"));
						if (_c == null || !_c.hasActivity(Client.Activity.broadcastA)) {
							return;
						}
						if (cl.hasRight(Right.moderator) || cl.getUid().equals(_c.getUid())) {
							// basic checks, will throw in case of missing options
							obj.getBoolean("mute");
							obj.put("sid", cl.getSid());
							WebSocketHelper.sendRoom(new TextRoomMessage(room.getRoom().getId(), cl, RoomMessage.Type.mute, obj.toString()));
						}
					}
						break;
					default:
				}
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'roomAction'", e);
			}
		}
	};
	private final AbstractDefaultAjaxBehavior toggleRight = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String uid = getRequest().getRequestParameters().getParameterValue(PARAM_UID).toString();
				if (Strings.isEmpty(uid)) {
					return;
				}
				Right right = Right.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_RIGHT).toString());
				if (room.getClient().hasRight(Right.moderator)) {
					Client client = getBean(ClientManager.class).get(uid);
					if (client == null) {
						return;
					}
					if (client.hasRight(right)) {
						room.denyRight(client, right);
					} else {
						if (Right.video == right) {
							room.allowRight(client, Right.audio, right);
						} else {
							room.allowRight(client, right);
						}
					}
				} else {
					room.requestRight(right, target);
				}
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'right'", e);
			}
		}
	};
	private final AbstractDefaultAjaxBehavior toggleActivity = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String uid = getRequest().getRequestParameters().getParameterValue(PARAM_UID).toString();
				if (Strings.isEmpty(uid)) {
					return;
				}
				Client.Activity a = Client.Activity.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTIVITY).toString());
				Client c = getBean(ClientManager.class).get(uid);
				toggleActivity(c, a);
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'activity'", e);
			}
		}
	};
	private final AbstractDefaultAjaxBehavior avSettings = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			StringValue s = getRequest().getRequestParameters().getParameterValue(PARAM_SETTINGS);
			if (!s.isEmpty()) {
				ExtendedClientProperties cp = WebSession.get().getExtendedProperties();
				Client c = room.getClient();
				cp.setSettings(new JSONObject(s.toString())).update(c, room.isInterview());
				if (!avInited) {
					avInited = true;
					if (Room.Type.conference == room.getRoom().getType()) {
						toggleActivity(c, Client.Activity.broadcastAV);
					}
				}
				sendUpdatedClient(c);
				room.broadcast(c);
			}
		}
	};
	private final Label userCount = new Label("user-count", Model.of(""));

	public RoomSidebar(String id, final RoomPanel room) {
		super(id);
		this.room = room;
		selfRights = new SelfIconsPanel("icons", room.getUid(), true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final NameDialog addFolder = new NameDialog("addFolder", getString("712")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
				roomFiles.createFolder(target, getModelObject());
			}
		};
		final Form<?> form = new Form<>("form");
		ConfirmableBorderDialog confirmTrash = new ConfirmableBorderDialog("confirm-trash", getString("80"), getString("713"), form);
		roomFiles = new RoomFilePanel("tree", room, addFolder, confirmTrash);
		add(selfRights, userList.add(updateUsers()).setOutputMarkupId(true)
				, fileTab.setVisible(!room.isInterview()), roomFiles.setVisible(!room.isInterview()));

		add(addFolder, settings, userCount.setOutputMarkupId(true));
		add(toggleRight, toggleActivity, roomAction, avSettings);
		add(confirmKick = new ConfirmableAjaxBorder("confirm-kick", getString("603"), getString("605")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				kickUser(kickedClient);
			}
		});
		add(form.add(confirmTrash), upload = new UploadDialog("upload", room, roomFiles));
		updateShowFiles(null);
		add(new JQueryUIBehavior("#room-sidebar-tabs", "tabs", new Options("activate", "function(event, ui) {Room.setSize();}")));
		add(activities = new ActivitiesPanel("activities", room));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_TOGGLE_RIGHT, toggleRight, explicit(PARAM_RIGHT), explicit(PARAM_UID))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_TOGGLE_ACTIVITY, toggleActivity, explicit(PARAM_ACTIVITY), explicit(PARAM_UID))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, roomAction, explicit(PARAM_ACTION), explicit(PARAM_UID))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_SETTINGS, avSettings, explicit(PARAM_SETTINGS))));
	}

	private ListView<Client> updateUsers() {
		users.setList(getBean(ClientManager.class).listByRoom(room.getRoom().getId()));
		return users;
	}

	private void updateShowFiles(IPartialPageRequestHandler handler) {
		if (room.isInterview()) {
			return;
		}
		showFiles = !room.getRoom().isHidden(RoomElement.Files) && room.getClient().hasRight(Right.presenter);
		roomFiles.setReadOnly(!showFiles, handler);
	}

	public void update(IPartialPageRequestHandler handler) {
		if (room.getRoom() == null || room.getClient() == null) {
			return;
		}
		updateShowFiles(handler);
		updateUsers();
		userCount.setDefaultModelObject(users.getList().size());
		handler.add(selfRights.update(handler), userList, userCount);
	}

	public void updateFiles(IPartialPageRequestHandler handler) {
		roomFiles.update(handler);
	}

	public RoomFilePanel getFilesPanel() {
		return roomFiles;
	}

	public boolean isShowFiles() {
		return showFiles;
	}

	public void showUpload(IPartialPageRequestHandler handler) {
		upload.open(handler);
	}

	public void toggleActivity(Client c, Client.Activity a) {
		if (c == null) {
			return;
		}
		if (!activityAllowed(c, a, room.getRoom()) && room.getClient().hasRight(Right.moderator)) {
			if (a == Client.Activity.broadcastA || a == Client.Activity.broadcastAV) {
				c.allow(Room.Right.audio);
			}
			if (!room.getRoom().isAudioOnly() && (a == Client.Activity.broadcastV || a == Client.Activity.broadcastAV)) {
				c.allow(Room.Right.video);
			}
		}
		if (activityAllowed(c, a, room.getRoom())) {
			if (a == Client.Activity.broadcastA && !c.isMicEnabled()) {
				return;
			}
			if (a == Client.Activity.broadcastV && !c.isCamEnabled()) {
				return;
			}
			if (a == Client.Activity.broadcastAV && !c.isMicEnabled() && !c.isCamEnabled()) {
				return;
			}
			c.toggle(a);
			room.broadcast(c); //will update client
		}
	}

	public static boolean activityAllowed(Client c, Client.Activity a, Room room) {
		boolean r = false;
		switch (a) {
			case broadcastA:
				r = c.hasRight(Right.audio);
				break;
			case broadcastV:
				r = !room.isAudioOnly() && c.hasRight(Right.video);
				break;
			case broadcastAV:
				r = !room.isAudioOnly() && c.hasRight(Right.audio) && c.hasRight(Right.video);
				break;
			default:
				break;
		}
		return r;
	}

	public void setFilesActive(IPartialPageRequestHandler handler) {
		handler.appendJavaScript("$('#room-sidebar-tabs').tabs('option', 'active', 1);");
	}

	public void addActivity(Activity a, IPartialPageRequestHandler handler) {
		activities.add(a, handler);
	}

	public void removeActivity(String uid, IPartialPageRequestHandler handler) {
		activities.remove(uid, handler);
	}
}
