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
package org.apache.openmeetings.web.room.menu;

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.util.GroupLogoResourceReference.getUrl;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage.Type;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ImagePanel;
import org.apache.openmeetings.web.common.OmButton;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.room.OmRedirectTimerBehavior;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

public class RoomMenuPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private MenuPanel menuPanel;
	private final StartSharingButton shareBtn;
	private final Label roomName;
	private static final FastDateFormat df = FastDateFormat.getInstance("dd.MM.yyyy HH:mm");
	private final OmButton askBtn = new OmButton("ask") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onInitialize() {
			super.onInitialize();
			setOutputMarkupPlaceholderTag(true);
			setVisible(false);
		}

		@Override
		public void onClick(AjaxRequestTarget target) {
			Client c = room.getClient();
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoom().getId(), c, Type.haveQuestion, c.getUid()));
		}
	};
	private final RoomPanel room;
	private RoomMenuItem exitMenuItem;
	private RoomMenuItem filesMenu;
	private final ImagePanel logo = new ImagePanel("logo") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getImageUrl() {
			return getUrl(getRequestCycle(), getGroup().getId());
		}
	};
	private final PollsSubMenu pollsSubMenu;
	private final ActionsSubMenu actionsSubMenu;

	public RoomMenuPanel(String id, final RoomPanel room) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		this.room = room;
		Room r = room.getRoom();
		setVisible(!r.isHidden(RoomElement.TopBar));
		add((roomName = new Label("roomName", r.getName())).setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
		String tag = getGroup().getTag();
		add(logo, new Label("tag", tag).setVisible(!Strings.isEmpty(tag)));
		add((shareBtn = new StartSharingButton("share", room.getUid()))
				.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
		pollsSubMenu = new PollsSubMenu(room, this);
		actionsSubMenu = new ActionsSubMenu(room, this, shareBtn);
	}

	private Group getGroup() {
		Room r = room.getRoom();
		return r.getGroups() == null || r.getGroups().isEmpty()
				? new Group()
				: r.getGroups().get(0).getGroup();
	}

	@Override
	protected void onInitialize() {
		exitMenuItem = new RoomMenuItem(getString("308"), getString("309"), "room menu exit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				exit(target);
			}
		};
		filesMenu = new RoomMenuItem(getString("245"), null, false);
		actionsSubMenu.init();
		pollsSubMenu.init();
		add((menuPanel = new MenuPanel("menu", getMenu())).setVisible(isVisible()));

		add(askBtn.add(AttributeModifier.replace(ATTR_TITLE, getString("84"))));
		Label demo = new Label("demo", Model.of(""));
		Room r = room.getRoom();
		add(demo.setVisible(r.isDemoRoom() && r.getDemoTime() != null && room.getRoom().getDemoTime().intValue() > 0));
		if (demo.isVisible()) {
			demo.add(new OmRedirectTimerBehavior(room.getRoom().getDemoTime().intValue(), "637") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimer(int remain) {
					getComponent().add(AttributeModifier.replace(ATTR_TITLE, getText("639", remain)));
				}

				@Override
				protected void onFinish(AjaxRequestTarget target) {
					exit(target);
				}
			});
		}
		super.onInitialize();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		pollsSubMenu.renderHead(response);
	}

	private List<IMenuItem> getMenu() {
		List<IMenuItem> menu = new ArrayList<>();
		exitMenuItem.setEnabled(false);
		menu.add(exitMenuItem.setTop(true));

		filesMenu.getItems().add(new RoomMenuItem(getString("15"), getString("1479")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				room.getSidebar().showUpload(target);
			}
		});
		menu.add(filesMenu.setTop(true));

		if (actionsSubMenu.isVisible()) {
			menu.add(actionsSubMenu.getMenu());
		}
		if (pollsSubMenu.isVisible()) {
			menu.add(pollsSubMenu.getMenu());
		}
		return menu;
	}

	public void update(IPartialPageRequestHandler handler) {
		if (!isVisible()) {
			return;
		}
		Room r = room.getRoom();
		boolean isInterview = Room.Type.interview == r.getType();
		User u = room.getClient().getUser();
		boolean notExternalUser = u.getType() != User.Type.contact;
		exitMenuItem.setEnabled(notExternalUser);
		filesMenu.setEnabled(!isInterview && room.getSidebar().isShowFiles());
		boolean moder = room.getClient().hasRight(Room.Right.moderator);
		actionsSubMenu.update(moder, notExternalUser, r);
		pollsSubMenu.update(moder, notExternalUser, r);
		menuPanel.update(handler);
		StringBuilder roomClass = new StringBuilder("room name");
		StringBuilder roomTitle = new StringBuilder();
		if (room.getRecordingUser() != null) {
			ClientManager cm = getBean(ClientManager.class);
			Client recClient = cm.getBySid(room.getRecordingUser());
			if (recClient != null) {
				roomTitle.append(String.format("%s %s %s %s %s", getString("419")
						, recClient.getUser().getLogin(), recClient.getUser().getFirstname(), recClient.getUser().getLastname(), df.format(recClient.getConnectedSince())));
				roomClass.append(" screen");
			}
			Client pubClient = cm.getBySid(room.getPublishingUser());
			if (pubClient != null) {
				if (recClient != null) {
					roomTitle.append('\n');
				}
				roomTitle.append(String.format("%s %s %s %s %s", getString("1504")
						, pubClient.getUser().getLogin(), pubClient.getUser().getFirstname(), pubClient.getUser().getLastname(), "URL"));
				roomClass.append(" screen");
			}
		}
		handler.add(roomName.add(AttributeModifier.replace(ATTR_CLASS, roomClass), AttributeModifier.replace(ATTR_TITLE, roomTitle)));
		handler.add(askBtn.setVisible(!moder && r.isAllowUserQuestions()));
		handler.add(shareBtn.setVisible(room.screenShareAllowed()));
	}

	public void updatePoll(IPartialPageRequestHandler handler, Long createdBy) {
		pollsSubMenu.updatePoll(handler, createdBy);
		update(handler);
	}

	public void exit(IPartialPageRequestHandler handler) {
		getBean(ClientManager.class).exitRoom(room.getClient());
		if (WebSession.getRights().contains(User.Right.Dashboard)) {
			room.getMainPanel().updateContents(ROOMS_PUBLIC, handler);
		} else {
			String url = getBean(ConfigurationDao.class).getString(CONFIG_REDIRECT_URL_FOR_EXTERNAL, "");
			throw new RedirectToUrlException(Strings.isEmpty(url) ? getBaseUrl() : url);
		}
	}
}
