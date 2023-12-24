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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelConfirm;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getParam;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;
import static org.apache.openmeetings.web.util.OmUrlFragment.getPanel;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.menu.MainMenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.OmMenuItem;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.user.AboutDialog;
import org.apache.openmeetings.web.user.InviteUserToRoomDialog;
import org.apache.openmeetings.web.user.MessageDialog;
import org.apache.openmeetings.web.user.UserInfoDialog;
import org.apache.openmeetings.web.user.chat.ChatPanel;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.web.util.OmUrlFragment.MenuActions;
import org.apache.openmeetings.web.util.ProfileImageResourceReference;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.AbstractClientMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.urlfragment.UrlFragment;

import com.github.openjson.JSONObject;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.references.BootstrapJavaScriptReference;
import jakarta.inject.Inject;

public class MainPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private final WebMarkupContainer empty = new WebMarkupContainer(CHILD_ID);
	private String uid = null;
	private MenuPanel menu;
	private final WebMarkupContainer topControls = new WebMarkupContainer("topControls");
	private final WebMarkupContainer topLinks = new WebMarkupContainer("topLinks");
	private final MarkupContainer contents = new WebMarkupContainer("contents");
	private ChatPanel chat;
	private MessageDialog newMessage;
	private UserInfoDialog userInfo;
	private BasePanel curPanel;
	private InviteUserToRoomDialog inviteUser;

	@Inject
	private ClientManager cm;
	@Inject
	private UserDao userDao;
	@Inject
	private RoomDao roomDao;

	public MainPanel(String id) {
		this(id, null);
	}

	public MainPanel(String id, BasePanel curPanel) {
		super(id);
		this.curPanel = curPanel;
		setAuto(true);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new OmWebSocketPanel("ws-panel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				StringBuilder handlers = new StringBuilder()
						.append("Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_FAILURE, hideBusyIndicator);")
						.append("Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_BEFORE, showBusyIndicator);")
						.append("Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_SUCCESS, hideBusyIndicator);")
						.append("Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_COMPLETE, hideBusyIndicator);")
						.append("Wicket.Event.subscribe(Wicket.Event.Topic.WebSocket.Opened, function() { Wicket.WebSocket.send('socketConnected'); });");
				response.render(OnDomReadyHeaderItem.forScript(handlers));
			}

			@Override
			protected void onConnect(ConnectedMessage msg) {
				ExtendedClientProperties cp = WebSession.get().getExtendedProperties();
				final User u = userDao.get(getUserId());
				final Client client = new Client(
						getSession().getId()
						, msg.getKey().hashCode()
						, u
						, ProfileImageResourceReference.getUrl(getRequestCycle(), u));
				uid = client.getUid();
				cm.add(cp.update(client));
				log.debug("WebSocketBehavior::onConnect [uid: {}, session: {}, key: {}]", client.getUid(), msg.getSessionId(), msg.getKey());
			}

			@Override
			protected void onConnect(WebSocketRequestHandler handler) {
				if (curPanel != null) {
					updateContents(curPanel, handler);
				}
			}

			@Override
			protected void onMessage(WebSocketRequestHandler handler, JSONObject m) throws IOException {
				BasePanel p = getCurrentPanel();
				if (p != null) {
					p.process(handler, m);
				}
			}

			@Override
			protected void closeHandler(AbstractClientMessage msg) {
				log.debug("WebSocketBehavior::closeHandler uid - {}", uid);
				super.closeHandler(msg);
				if (uid != null) {
					cm.exit(getClient());
					uid = null;
				}
			}

			@Override
			protected IWsClient getWsClient() {
				return getClient();
			}
		});
		menu = new MenuPanel("menu", getMainMenu());
		add(topControls.setOutputMarkupPlaceholderTag(true).setMarkupId("topControls"));
		add(contents.add(getClient() == null || curPanel == null ? empty : curPanel).setOutputMarkupId(true).setMarkupId("contents"));
		topControls.add(menu.setVisible(false), topLinks.setVisible(false).setOutputMarkupPlaceholderTag(true).setMarkupId("topLinks"));
		final AboutDialog about = new AboutDialog("aboutDialog");
		topLinks.add(new AjaxLink<Void>("about") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				about.show(target);
			}
		});
		if (getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled()) {
			add(new DebugBar("dev")
					.positionBottom()
					.add(AttributeModifier.append("class", "end-0"))
					.setOutputMarkupId(true));
		} else {
			add(new EmptyPanel("dev").setVisible(false));
		}
		add(new OmAjaxClientInfoBehavior());
		add(about, chat = new ChatPanel("chatPanel"));
		add(newMessage = new MessageDialog("newMessageDialog", new CompoundPropertyModel<>(new PrivateMessage())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSend(IPartialPageRequestHandler handler) {
				BasePanel bp = getCurrentPanel();
				if (bp != null) {
					bp.onNewMessageClose(handler);
				}
			}
		});
		add(userInfo = new UserInfoDialog("userInfoDialog", newMessage));
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				userInfo.show(target, getParam(getComponent(), PARAM_USER_ID).toLong());
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(getNamedFunction("showUserInfo", this, explicit(PARAM_USER_ID))));
			}
		});
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				ContactsHelper.addUserToContactList(getParam(getComponent(), PARAM_USER_ID).toLong());
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(getNamedFunction("addContact", this, explicit(PARAM_USER_ID))));
			}
		});
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				newMessage.reset(true).show(target, getParam(getComponent(), PARAM_USER_ID).toOptionalLong());
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(getNamedFunction("privateMessage", this, explicit(PARAM_USER_ID))));
			}
		});
		add(inviteUser = new InviteUserToRoomDialog("invite-to-room"));
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				inviteUser.show(target, getParam(getComponent(), PARAM_USER_ID).toLong());
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(getNamedFunction("inviteUser", this, explicit(PARAM_USER_ID))));
			}
		});
		AjaxLink<Void> logout = new AjaxLink<>("logout") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}

		};
		logout.add(newOkCancelConfirm(this, getString("634")));
		topLinks.add(logout);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(BootstrapJavaScriptReference.instance()))); //this one should go before jquery-ui
		super.renderHead(response);
	}

	private OmMenuItem getSubItem(String lbl, String title, MenuActions action) {
		return new MainMenuItem(lbl, title, action) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onClick(MainPanel.this, target);
			}
		};
	}

	private List<INavbarComponent> getMainMenu() {
		List<INavbarComponent> mmenu = new ArrayList<>();
		createDashboardMenu(mmenu);
		createRoomsMenu(mmenu);
		if (OpenmeetingsVariables.isRecordingsEnabled()) {
			// Recording Menu Points
			mmenu.add(getSubItem("395", "1452", MenuActions.RECORD));
		}
		createSettingsMenu(mmenu);
		Set<Right> r = WebSession.getRights();
		if (r.stream().anyMatch(right -> right.name().contains("ADMIN"))) {
			boolean isAdmin = hasAdminLevel(r);
			boolean isGrpAdmin = hasGroupAdminLevel(r);
			// Administration Menu Points
			List<INavbarComponent> l = new ArrayList<>();
			if (isAdmin || isGrpAdmin) {
				l.add(getSubItem("125", "1454", MenuActions.ADMIN_USER));
			}
			if (isAdmin || r.contains(Right.ADMIN_CONNECTIONS)) {
				l.add(getSubItem("597", "1455", MenuActions.ADMIN_CONNECTION));
			}
			if (isAdmin || isGrpAdmin) {
				l.add(getSubItem("126", "1456", MenuActions.ADMIN_GROUP));
				l.add(getSubItem("186", "1457", MenuActions.ADMIN_ROOM));
			}
			if (isAdmin || r.contains(Right.ADMIN_CONFIG)) {
				l.add(getSubItem("263", "1458", MenuActions.ADMIN_CONFIG));
			}
			if (isAdmin || r.contains(Right.ADMIN_LABEL)) {
				l.add(getSubItem("348", "1459", MenuActions.ADMIN_LABEL));
			}
			if (isAdmin) {
				l.add(getSubItem("1103", "1454", MenuActions.ADMIN_LDAP));
				l.add(getSubItem("1571", "1572", MenuActions.ADMIN_OAUTH));
			}
			if (isAdmin || r.contains(Right.ADMIN_BACKUP)) {
				l.add(getSubItem("367", "1461", MenuActions.ADMIN_BACKUP));
			}
			if (isAdmin) {
				l.add(getSubItem("main.menu.admin.email", "main.menu.admin.email.desc", MenuActions.ADMIN_EMAIL));
			}
			if (isAdmin || isGrpAdmin) {
				l.add(getSubItem("main.menu.admin.extra", "main.menu.admin.extra.desc", MenuActions.ADMIN_EXTRA));
			}
			mmenu.add(new OmMenuItem(getString("6"), l));
		}
		return mmenu;
	}

	private void createDashboardMenu(List<INavbarComponent> mmenu) {
		List<INavbarComponent> l = new ArrayList<>();
		l.add(getSubItem("290", "1450", MenuActions.DASHBOARD_START));
		l.add(getSubItem("291", "1451", MenuActions.DASHBOARD_CALENDAR));
		mmenu.add(new OmMenuItem(getString("124"), l));
	}

	private void createRoomsMenu(List<INavbarComponent> mmenu) {
		List<INavbarComponent> l = new ArrayList<>();
		l.add(getSubItem("777", "1506", MenuActions.ROOMS_PUBLIC));
		l.add(getSubItem("779", "1507", MenuActions.ROOMS_GROUP));
		if (isMyRoomsEnabled()) {
			l.add(getSubItem("781", "1508", MenuActions.ROOMS_MY));
		}
		List<Room> recent = roomDao.getRecent(getUserId());
		if (!recent.isEmpty()) {
			l.add(new OmMenuItem(null, (String)null));
		}
		for (Room r : recent) {
			final Long roomId = r.getId();
			l.add(new OmMenuItem(r.getName(), r.getName()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onClick(AjaxRequestTarget target) {
					RoomEnterBehavior.roomEnter((MainPage)getPage(), target, roomId);
				}
			});
		}
		mmenu.add(new OmMenuItem(getString("792"), l));
	}

	private void createSettingsMenu(List<INavbarComponent> mmenu) {
		List<INavbarComponent> l = new ArrayList<>();
		l.add(getSubItem("1188", "1188", MenuActions.PROFILE_MESSAGE));
		l.add(getSubItem("377", "377", MenuActions.PROFILE_EDIT));
		l.add(getSubItem("1172", "1172", MenuActions.PROFILE_SEARCH));
		l.add(getSubItem("profile.invitations", "profile.invitations", MenuActions.PROFILE_INVITATION));
		l.add(getSubItem("1548", "1548", MenuActions.PROFILE_WIDGET));
		mmenu.add(new OmMenuItem(getString("4"), l));
	}

	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler) {
		updateContents(f, handler, true);
	}

	private BasePanel getCurrentPanel() {
		Component prev = contents.get(CHILD_ID);
		if (prev instanceof BasePanel basePrev) {
			return basePrev;
		}
		return null;
	}

	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler, boolean updateFragment) {
		BasePanel npanel = getPanel(f.getArea(), f.getType());
		log.debug("updateContents:: npanels IS null ? {}, client IS null ? {}", npanel == null, getClient() == null);
		if (npanel != null) {
			if (getClient() != null) {
				updateContents(npanel, handler);
			} else {
				this.curPanel = npanel;
			}
			if (updateFragment) {
				UrlFragment uf = new UrlFragment(handler);
				uf.set(f.getArea().zone(), f.getType());
			}
		}
	}

	private void updateContents(BasePanel inPanel, IPartialPageRequestHandler handler) {
		if (inPanel != null) {
			BasePanel prev = getCurrentPanel();
			if (prev != null) {
				prev.cleanup(handler);
			}
			handler.add(contents.replace(inPanel));
			handler.appendJavaScript("$('#" + this.getMarkupId() + "').attr('class', 'main " + inPanel.getCssClass() + "');");
			inPanel.onMenuPanelLoad(handler);
		}
	}

	public MenuPanel getMenu() {
		return menu;
	}

	public WebMarkupContainer getTopLinks() {
		return topLinks;
	}

	public WebMarkupContainer getTopControls() {
		return topControls;
	}

	public ChatPanel getChat() {
		return chat;
	}

	public MessageDialog getMessageDialog() {
		return newMessage;
	}

	public String getUid() {
		return uid;
	}

	public Client getClient() {
		return cm.get(uid);
	}
}
