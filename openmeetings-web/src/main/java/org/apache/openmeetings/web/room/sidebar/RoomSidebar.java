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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getOnlineClient;
import static org.apache.openmeetings.web.app.Application.getRoomClients;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.Client.Activity;
import org.apache.openmeetings.web.app.Client.Pod;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.RoomPanel.Action;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.widget.tabs.TabbedPanel;

public class RoomSidebar extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomSidebar.class, webAppRootKey);
	public static final String FUNC_TOGGLE_RIGHT = "toggleRight";
	public static final String FUNC_TOGGLE_ACTIVITY = "toggleActivity";
	public static final String FUNC_ACTION = "roomAction";
	public static final String FUNC_SETTINGS = "avSettings";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_ACTIVITY = "activity";
	public static final String PARAM_RIGHT = "right";
	public static final String PARAM_UID = "uid";
	public static final String PARAM_POD = "pod";
	public static final String PARAM_SETTINGS = "s";
	private final RoomPanel room;
	private final TabbedPanel tabs;
	private final ITab userTab;
	private final ITab fileTab;
	private final UploadDialog upload;
	private final RoomFilePanel roomFiles;
	private final SelfIconsPanel selfRights;
	private final ConfirmableAjaxBorder confirmKick;
	private boolean showFiles;
	private boolean avInited = false;
	private Client kickedClient;
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
				if (room.getClient().hasRight(Right.moderator)) {
					Action a = Action.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
					kickedClient = getOnlineClient(uid);
					if (kickedClient == null) {
						return;
					}
					switch (a) {
						case kick:
							if (!kickedClient.hasRight(Right.superModerator) && !room.getClient().getUid().equals(kickedClient.getUid())) {
								confirmKick.getDialog().open(target);
							}
							break;
						default:
					}
				}
			} catch (Exception e) {
				log.error("Unexpected exception while toggle 'action'", e);
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
					Client client = getOnlineClient(uid);
					if (client == null) {
						return;
					}
					if (client.hasRight(right)) {
						if (Right.audio == right) {
							room.denyRight(target, client, right, Right.video);
						} else {
							room.denyRight(target, client, right);
						}
					} else {
						if (Right.video == right) {
							room.allowRight(target, client, Right.audio, right);
						} else {
							room.allowRight(target, client, right);
						}
					}
				} else {
					room.requestRight(target, right);
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
				Activity a = Activity.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTIVITY).toString());
				Client c = getOnlineClient(uid);
				toggleActivity(c, a, target);
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
				JSONObject o = new JSONObject(s.toString());
				Client c = room.getClient();
				c.setCam(o.optInt("cam", -1));
				c.setMic(o.optInt("mic", -1));
				boolean interview = Room.Type.interview == room.getRoom().getType();
				c.setWidth(interview ? 320 : o.optInt("width"));
				c.setHeight(interview ? 260 : o.optInt("height"));
				if (!avInited) {
					avInited = true;
					if (Room.Type.conference == room.getRoom().getType()) {
						toggleActivity(c, Activity.broadcastAV, target);
					}
				}
				room.broadcast(target, c);
			}
		}
	};

	public RoomSidebar(String id, final RoomPanel room) {
		super(id);
		this.room = room;
		updateShowFiles();

		userTab = new ITab() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public IModel<String> getTitle() {
				return Model.of(getString("613"));
			}

			@Override
			public WebMarkupContainer getPanel(String containerId) {
				return new UserFragment(containerId, "user-panel");
			}
		};
		fileTab = new ITab() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return showFiles;
			}

			@Override
			public IModel<String> getTitle() {
				return Model.of(getString("614"));
			}

			@Override
			public WebMarkupContainer getPanel(String containerId) {
				return new FileFragment(containerId, "file-panel");
			}
		};
		add(tabs = new TabbedPanel("tabs", Arrays.asList(userTab, fileTab)).setActiveTab(room.getRoom().isFilesOpened() ? 1 : 0));
		roomFiles = new RoomFilePanel("tree", room);
		selfRights = new SelfIconsPanel("icons", room.getClient(), room, true);
		add(upload = new UploadDialog("upload", room, roomFiles));
		add(confirmKick = new ConfirmableAjaxBorder("confirm-kick", getString("603"), getString("605")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				room.kickUser(target, kickedClient);
			}
		});
		add(toggleRight, toggleActivity, roomAction, avSettings);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_TOGGLE_RIGHT, toggleRight, explicit(PARAM_RIGHT), explicit(PARAM_UID))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_TOGGLE_ACTIVITY, toggleActivity, explicit(PARAM_ACTIVITY), explicit(PARAM_UID), explicit(PARAM_POD))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, roomAction, explicit(PARAM_ACTION), explicit(PARAM_UID))));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_SETTINGS, avSettings, explicit(PARAM_SETTINGS))));
	}

	private ListView<Client> updateUsers() {
		//TODO do we need sort??
		users.setList(getRoomClients(room.getRoom().getId()));
		return users;
	}

	public class UserFragment extends Fragment {
		private static final long serialVersionUID = 1L;

		public UserFragment(String id, String markupId) {
			super(id, markupId, RoomSidebar.this);
			add(selfRights);
			add(updateUsers());
		}
	}

	public class FileFragment extends Fragment {
		private static final long serialVersionUID = 1L;

		public FileFragment(String id, String markupId) {
			super(id, markupId, RoomSidebar.this);
			add(roomFiles);
		}
	}

	private void updateShowFiles() {
		showFiles = !room.getRoom().isHidden(RoomElement.Files) && room.getClient().hasRight(Right.whiteBoard);
	}

	public void updateUsers(IPartialPageRequestHandler handler) {
		updateShowFiles();
		updateUsers();
		selfRights.setVisible(room.getRoom().isAllowUserQuestions() || room.getClient().hasRight(Right.moderator));
		selfRights.update(handler);
		handler.add(tabs);
	}

	public void updateFiles(IPartialPageRequestHandler handler) {
		roomFiles.update(handler);
	}

	public boolean isShowFiles() {
		return showFiles;
	}

	public void showUpload(IPartialPageRequestHandler handler) {
		upload.open(handler);
	}

	public void toggleActivity(Client c, Activity a, AjaxRequestTarget target) {
		if (c == null) {
			return;
		}
		if (!activityAllowed(c, a, room.getRoom()) && room.getClient().hasRight(Right.moderator)) {
			if (a == Activity.broadcastA || a == Activity.broadcastAV) {
				c.getRights().add(Room.Right.audio);
			}
			if (!room.getRoom().isAudioOnly() && (a == Activity.broadcastV || a == Activity.broadcastAV)) {
				c.getRights().add(Room.Right.video);
			}
		}
		if (activityAllowed(c, a, room.getRoom())) {
			if (a == Activity.broadcastA && !c.isMicEnabled()) {
				return;
			}
			if (a == Activity.broadcastV && !c.isCamEnabled()) {
				return;
			}
			if (a == Activity.broadcastAV && !c.isMicEnabled() && !c.isCamEnabled()) {
				return;
			}
			Pod pod = c.getPod();
			c.setPod(getRequest().getRequestParameters().getParameterValue(PARAM_POD).toOptionalInteger());
			if (pod != Pod.none && pod != c.getPod()) {
				//pod has changed, no need to toggle
				c.set(a);
			} else {
				c.toggle(a);
			}
			room.broadcast(target, c);
		}
	}

	public static boolean activityAllowed(Client c, Activity a, Room room) {
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
}
