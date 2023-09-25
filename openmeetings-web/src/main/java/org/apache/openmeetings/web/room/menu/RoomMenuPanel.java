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

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.GroupLogoResourceReference.getUrl;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_GROUP;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_MY;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
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
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.OmMenuItem;
import org.apache.openmeetings.web.room.OmTimerBehavior;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;

import org.apache.wicket.util.string.Strings;
import org.apache.openmeetings.mediaserver.KurentoHandler;
import org.apache.openmeetings.mediaserver.StreamProcessor;

import com.github.openjson.JSONObject;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public class RoomMenuPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private MenuPanel menuPanel;
	private final WebMarkupContainer shareBtn;
	private final Label roomName;
	private static final FastDateFormat df = FastDateFormat.getInstance("dd.MM.yyyy HH:mm");
	private final AjaxLink<Void> askBtn = new AjaxLink<>("ask") {
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
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoom().getId(), c, Type.HAVE_QUESTION, c.getUid()));
		}
	};
	private final RoomPanel room;
	private OmMenuItem exitMenuItem;
	private final ImagePanel logo = new ImagePanel("logo") {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getImageUrl() {
			return getUrl(getRequestCycle(), getGroup().getId());
		}
	};
	private final PollsSubMenu pollsSubMenu;
	private final ActionsSubMenu actionsSubMenu;
	private final ExtrasSubMenu extrasSubMenu;

	@Inject
	private ClientManager cm;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private ChatDao chatDao;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private StreamProcessor streamProcessor;

	public RoomMenuPanel(String id, final RoomPanel room) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		this.room = room;
		Room r = room.getRoom();
		setVisible(!r.isHidden(RoomElement.TOP_BAR));
		add((roomName = new Label("roomName", r.getName())).setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
		String tag = getGroup().getTag();
		add(logo, new Label("tag", tag).setVisible(!Strings.isEmpty(tag)));
		add(shareBtn = new WebMarkupContainer("share"));
		shareBtn.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		pollsSubMenu = new PollsSubMenu(room, this);
		actionsSubMenu = new ActionsSubMenu(room, this);
		extrasSubMenu = new ExtrasSubMenu(room);
	}

	private Group getGroup() {
		Room r = room.getRoom();
		return r.getGroups() == null || r.getGroups().isEmpty()
				? new Group()
				: r.getGroups().get(0).getGroup();
	}

	@Override
	protected void onInitialize() {
		exitMenuItem = new OmMenuItem(getString("308"), getString("309"), FontAwesome6IconType.right_from_bracket_s) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				chatDao.closeMessages(getUserId());
				exit(target);
			}
		};
		actionsSubMenu.init();
		pollsSubMenu.init();
		extrasSubMenu.init();
		add((menuPanel = new MenuPanel("menu", getMenu())).setVisible(isVisible()));

		add(askBtn.add(AttributeModifier.replace(ATTR_TITLE, getString("84"))));
		Label demo = new Label("demo", Model.of(""));
		Room r = room.getRoom();
		add(demo.setVisible(r.isDemoRoom() && r.getDemoTime() != null && room.getRoom().getDemoTime().intValue() > 0));
		if (demo.isVisible()) {
			demo.add(new OmTimerBehavior(room.getRoom().getDemoTime().intValue(), "637") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTimer(int remain) {
					getComponent().add(AttributeModifier.replace(ATTR_TITLE, getText(getString("637"), remain)));
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

	private List<INavbarComponent> getMenu() {
		List<INavbarComponent> menu = new ArrayList<>();
		menu.add(exitMenuItem);

		if (actionsSubMenu.isVisible()) {
			menu.add(actionsSubMenu.getMenu());
		}
		if (pollsSubMenu.isVisible()) {
			menu.add(pollsSubMenu.getMenu());
		}
		menu.add(extrasSubMenu.getMenu());
		return menu;
	}

	public void update(IPartialPageRequestHandler handler) {
		if (!isVisible()) {
			return;
		}
		Room r = room.getRoom();
		User u = room.getClient().getUser();
		boolean notExternalUser = u.getType() != User.Type.CONTACT;
		boolean moder = room.getClient().hasRight(Room.Right.MODERATOR);
		actionsSubMenu.update(moder, notExternalUser);
		pollsSubMenu.update(moder, notExternalUser, r);
		menuPanel.update(handler);
		StringBuilder roomClass = new StringBuilder("room name");
		String roomTitle = "";
		if (streamProcessor.isRecording(r.getId())) {
			JSONObject ru = kHandler.getRecordingUser(r.getId());
			if (!Strings.isEmpty(ru.optString("login"))) {
				roomTitle += getString("419") + " " + ru.getString("login");
				if (!Strings.isEmpty(ru.optString("firstName"))) {
					roomTitle += " " + ru.getString("firstName");
				}
				if (!Strings.isEmpty(ru.optString("lastName"))) {
					roomTitle += " " + ru.getString("lastName");
				}
				roomTitle += " " + df.format(new Date(ru.getLong("started")));
				roomClass.append(" screen");
			}
		}
		handler.add(roomName.add(AttributeModifier.replace(ATTR_CLASS, roomClass), AttributeModifier.replace(ATTR_TITLE, roomTitle)));
		handler.add(askBtn.setVisible(!moder && r.isAllowUserQuestions()));
		handler.add(shareBtn.setVisible(room.screenShareAllowed()));
		handler.appendJavaScript("$('#share-dlg-btn').off().click(Sharer.open);");
	}

	public void updatePoll(IPartialPageRequestHandler handler, Long createdBy) {
		pollsSubMenu.updatePoll(handler, createdBy);
		update(handler);
	}

	public void exit(IPartialPageRequestHandler handler) {
		cm.exitRoom(room.getClient());
		if (WebSession.getRights().contains(User.Right.DASHBOARD)) {
			final Room r = room.getRoom();
			if (isMyRoomsEnabled() && r != null && getUserId().equals(r.getOwnerId())) {
				room.getMainPanel().updateContents(ROOMS_MY, handler);
			} else if (r != null && !r.getIspublic()) {
				room.getMainPanel().updateContents(ROOMS_GROUP, handler);
			} else {
				room.getMainPanel().updateContents(ROOMS_PUBLIC, handler);
			}
		} else {
			WebSession.get().invalidate();
			String url = cfgDao.getString(CONFIG_REDIRECT_URL_FOR_EXTERNAL, "");
			throw new RedirectToUrlException(Strings.isEmpty(url) ? getBaseUrl() : url);
		}
	}
}
