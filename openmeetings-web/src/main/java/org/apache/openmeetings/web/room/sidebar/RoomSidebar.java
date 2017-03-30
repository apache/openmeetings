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
import java.util.List;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.Pod;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder.ConfirmableBorderDialog;
import org.apache.openmeetings.web.room.RoomBroadcaster;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.RoomPanel.Action;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.ui.widget.tabs.TabbedPanel;
import com.googlecode.wicket.kendo.ui.widget.tabs.TabListModel;

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
	private final ITab userTab, fileTab;
	private UploadDialog upload;
	private RoomFilePanel roomFiles;
	private final SelfIconsPanel selfRights;
	private ConfirmableAjaxBorder confirmKick;
	private boolean showFiles;
	private boolean avInited = false;
	private int selectedIdx = 0;
	private Client kickedClient;
	private final ListView<Client> users = new ListView<Client>("user", new ArrayList<Client>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<Client> item) {
			item.add(new RoomClientPanel("user", item, room));
		}
	};
	private final AddFolderDialog addFolder = new AddFolderDialog("addFolder", Application.getString(712)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			roomFiles.createFolder(target, getModelObject());
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
				Client cl = room.getClient();
				Action a = Action.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
				switch (a) {
					case kick:
						if (cl.hasRight(Right.moderator)) {
							kickedClient = getOnlineClient(uid);
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
							for (Client c : Application.getRoomClients(room.getRoom().getId())) {
								if (cl.getUid().equals(c.getUid())) {
									c.set(Activity.broadcastA);
								} else {
									c.remove(Activity.broadcastA);
								}
								room.broadcast(c);
							}
						}
						break;
					default:
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
							room.denyRight(client, right, Right.video);
						} else {
							room.denyRight(client, right);
						}
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
				Activity a = Activity.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTIVITY).toString());
				Client c = getOnlineClient(uid);
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
						toggleActivity(c, Activity.broadcastAV);
					}
				}
				RoomBroadcaster.sendUpdatedClient(c);
				room.broadcast(c);
			}
		}
	};

	public RoomSidebar(String id, final RoomPanel room) {
		super(id);
		this.room = room;

		userTab = new OmTab() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public String getCssClass() {
				return "om-icon big tab user";
			}

			@Override
			public IModel<String> getTitle() {
				return Model.of(getString("613"));
			}

			@Override
			public WebMarkupContainer getPanel(String containerId) {
				WebMarkupContainer p = new Fragment(containerId, "user-panel", RoomSidebar.this);
				p.add(selfRights, updateUsers());
				return p;
			}
		};
		fileTab = new OmTab() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public String getCssClass() {
				return "om-icon big tab file";
			}

			@Override
			public IModel<String> getTitle() {
				return Model.of(getString("614"));
			}

			@Override
			public WebMarkupContainer getPanel(String containerId) {
				WebMarkupContainer p = new Fragment(containerId, "file-panel", RoomSidebar.this);
				p.add(roomFiles);
				return p;
			}
		};
		add((tabs = new TabbedPanel("tabs", newTabModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setActiveTab(selectedIdx);
			}

			@Override
			public void onActivate(AjaxRequestTarget target, int index, ITab tab) {
				selectedIdx = index;
			}

			@Override
			protected WebMarkupContainer newTabContainer(String id, String tabId, ITab tab, int index) {
				WebMarkupContainer t = super.newTabContainer(id, tabId, tab, index);
				Label link = newTitleLabel("link", tab.getTitle());
				link.add(AttributeModifier.replace("href", "#" + tabId));
				link.add(AttributeModifier.append("class", ((OmTab)tab).getCssClass()));
				link.add(AttributeModifier.append("title", tab.getTitle()));
				t.replace(link);
				return t;
			}
		}).setOutputMarkupId(true));
		selfRights = new SelfIconsPanel("icons", room.getClient(), room, true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(addFolder);
		add(toggleRight, toggleActivity, roomAction, avSettings);
		add(confirmKick = new ConfirmableAjaxBorder("confirm-kick", getString("603"), getString("605")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				room.kickUser(kickedClient);
			}
		});
		final Form<?> form = new Form<>("form");
		ConfirmableBorderDialog confirmTrash = new ConfirmableBorderDialog("confirm-trash", getString("80"), getString("713"), form);
		roomFiles = new RoomFilePanel("tree", room, addFolder, confirmTrash);
		add(form.add(confirmTrash), upload = new UploadDialog("upload", room, roomFiles));
		updateShowFiles(null);
	}

	private TabListModel newTabModel() {
		return new TabListModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ITab> load() {
				List<ITab> l = new ArrayList<>();
				l.add(userTab);
				l.add(fileTab);
				return l;
			}
		};
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

	private void updateShowFiles(IPartialPageRequestHandler handler) {
		showFiles = !room.getRoom().isHidden(RoomElement.Files) && room.getClient().hasRight(Right.whiteBoard);
		roomFiles.setReadOnly(!showFiles, handler);
	}

	public void update(IPartialPageRequestHandler handler) {
		updateShowFiles(handler);
		updateUsers();
		selfRights.setVisible(room.getRoom().isAllowUserQuestions() || room.getClient().hasRight(Right.moderator));
		selfRights.update(handler);
		tabs.reload(handler);
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

	public void toggleActivity(Client c, Activity a) {
		if (c == null) {
			return;
		}
		if (!activityAllowed(c, a, room.getRoom()) && room.getClient().hasRight(Right.moderator)) {
			if (a == Activity.broadcastA || a == Activity.broadcastAV) {
				c.allow(Room.Right.audio);
			}
			if (!room.getRoom().isAudioOnly() && (a == Activity.broadcastV || a == Activity.broadcastAV)) {
				c.allow(Room.Right.video);
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
			room.broadcast(c);
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

	public void setFilesActive(IPartialPageRequestHandler handler) {
		selectedIdx = 1;
		tabs.reload(handler);
	}

	private static abstract class OmTab implements ITab {
		private static final long serialVersionUID = 1L;

		public abstract String getCssClass();
	}
}

