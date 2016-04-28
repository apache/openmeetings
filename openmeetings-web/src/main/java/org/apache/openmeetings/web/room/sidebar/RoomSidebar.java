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

import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.Client.Right;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.widget.tabs.TabbedPanel;

public class RoomSidebar extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomSidebar.class, webAppRootKey);
	public static final String FUNC_CHANGE_RIGHT = "changeRight";
	public static final String FUNC_ACTION = "roomAction";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_RIGHT = "right";
	public static final String PARAM_UID = "uid";
	private final RoomPanel room;
	private final TabbedPanel tabs;
	private final ITab userTab;
	private final ITab fileTab;
	private final UploadDialog upload;
	private final RoomFilePanel roomFiles;
	private final RoomRightPanel selfRights;
	private boolean showFiles;
	private final ListView<Client> users = new ListView<Client>("user", new ArrayList<Client>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<Client> item) {
			item.add(new RoomClientPanel("user", item, room));
		}
	};
	private final AbstractDefaultAjaxBehavior action = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
		}
	};
	private final AbstractDefaultAjaxBehavior requestRight = new AbstractDefaultAjaxBehavior() {
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
				log.error("Unexpected exception while processing activity action", e);
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
		selfRights = new RoomRightPanel("rights", room.getClient(), room);
		add(upload = new UploadDialog("upload", room, roomFiles));
		add(requestRight, action);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction(FUNC_CHANGE_RIGHT, requestRight, explicit(PARAM_RIGHT), explicit(PARAM_UID)), FUNC_CHANGE_RIGHT)));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction(FUNC_ACTION, action, explicit(PARAM_ACTION), explicit(PARAM_UID)), FUNC_ACTION)));
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

	public boolean isShowFiles() {
		return showFiles;
	}
	
	public void showUpload(IPartialPageRequestHandler handler) {
		upload.open(handler);
	}
}
