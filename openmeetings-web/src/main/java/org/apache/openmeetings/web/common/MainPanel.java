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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getParam;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.apache.openmeetings.web.util.OmUrlFragment.getPanel;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
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
import org.apache.openmeetings.web.util.OmUrlFragment.MenuActions;
import org.apache.openmeetings.web.util.OmUrlFragment.MenuParams;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.AbortedMessage;
import org.apache.wicket.protocol.ws.api.message.AbstractClientMessage;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.ErrorMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.urlfragment.UrlFragment;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

public class MainPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPanel.class, getWebAppRootKey());
	private static final String DELIMITER = "     ";
	private final WebMarkupContainer EMPTY = new WebMarkupContainer(CHILD_ID);
	private String uid = null;
	private MenuPanel menu;
	private final WebMarkupContainer topControls = new WebMarkupContainer("topControls");
	private final WebMarkupContainer topLinks = new WebMarkupContainer("topLinks");
	private final MarkupContainer contents = new WebMarkupContainer("contents");
	private ChatPanel chat;
	private MessageDialog newMessage;
	private UserInfoDialog userInfo;
	private BasePanel panel;
	private InviteUserToRoomDialog inviteUser;
	private AbstractAjaxTimerBehavior pingTimer = new AbstractAjaxTimerBehavior(Duration.seconds(30)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			log.debug("Sending WebSocket PING");
			WebSocketHelper.sendClient(getClient(), new byte[]{getUserId().byteValue()});
		}
	};

	public MainPanel(String id) {
		this(id, null);
	}

	public MainPanel(String id, BasePanel _panel) {
		super(id);
		this.panel = _panel;
		setAuto(true);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		pingTimer.stop(null);
		add(pingTimer, new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
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
				super.onConnect(msg);
				ExtendedClientProperties cp = WebSession.get().getExtendedProperties();
				final Client client = new Client(getSession().getId(), msg.getKey().hashCode(), getUserId(), getBean(UserDao.class));
				uid = client.getUid();
				getBean(ClientManager.class).add(cp.update(client));
				log.debug("WebSocketBehavior::onConnect [uid: {}, session: {}, key: {}]", client.getUid(), msg.getSessionId(), msg.getKey());
			}

			@Override
			protected void onMessage(WebSocketRequestHandler handler, TextMessage msg) {
				if ("socketConnected".equals(msg.getText())) {
					if (panel != null) {
						updateContents(panel, handler);
					}
					log.debug("WebSocketBehavior:: pingTimer is attached");
					pingTimer.restart(handler);
				} else {
					final JSONObject m;
					try {
						m = new JSONObject(msg.getText());
						BasePanel p = getCurrentPanel();
						if (p != null) {
							p.process(handler, m);
						}
					} catch (Exception e) {
						//no-op
					}
				}
			}

			@Override
			protected void onAbort(AbortedMessage msg) {
				super.onAbort(msg);
				closeHandler(msg);
			}

			@Override
			protected void onClose(ClosedMessage msg) {
				super.onClose(msg);
				closeHandler(msg);
			}

			@Override
			protected void onError(WebSocketRequestHandler handler, ErrorMessage msg) {
				super.onError(handler, msg);
				closeHandler(msg);
			}

			private void closeHandler(AbstractClientMessage msg) {
				log.debug("WebSocketBehavior::closeHandler [uid: {}, session: {}, key: {}]", uid, msg.getSessionId(), msg.getKey());
				//no chance to stop pingTimer here :(
				if (uid != null) {
					getBean(ClientManager.class).exit(getClient());
					uid = null;
				}
			}
		});
	}

	@Override
	protected void onInitialize() {
		menu = new MenuPanel("menu", getMainMenu());
		add(topControls.setOutputMarkupPlaceholderTag(true).setMarkupId("topControls"));
		add(contents.add(getClient() == null || panel == null ? EMPTY : panel).setOutputMarkupId(true).setMarkupId("contents"));
		topControls.add(menu.setVisible(false), topLinks.setVisible(false).setOutputMarkupPlaceholderTag(true).setMarkupId("topLinks"));
		topLinks.add(new AjaxLink<Void>("messages") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_MESSAGES, target);
			}
		});
		topLinks.add(new AjaxLink<Void>("profile") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_EDIT, target);
			}
		});
		final AboutDialog about = new AboutDialog("aboutDialog");
		topLinks.add(new AjaxLink<Void>("about") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				about.open(target);
			}
		});
		if (getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled()) {
			add(new DebugBar("dev").setOutputMarkupId(true));
		} else {
			add(new EmptyPanel("dev").setVisible(false));
		}
		add(new OmAjaxClientInfoBehavior());
		add(about, chat = new ChatPanel("chatPanel"));
		add(newMessage = new MessageDialog("newMessageDialog", new CompoundPropertyModel<>(new PrivateMessage())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				BasePanel bp = getCurrentPanel();
				if (send.equals(button) && bp != null) {
					bp.onNewMessageClose(handler);
				}
			}
		});
		add(userInfo = new UserInfoDialog("userInfoDialog", newMessage));
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				userInfo.open(target, getParam(getComponent(), PARAM_USER_ID).toLong());
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
				newMessage.reset(true).open(target, getParam(getComponent(), PARAM_USER_ID).toOptionalLong());
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
				inviteUser.open(target, getParam(getComponent(), PARAM_USER_ID).toLong());
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(getNamedFunction("inviteUser", this, explicit(PARAM_USER_ID))));
			}
		});
		topLinks.add(new ConfirmableAjaxBorder("logout", getString("310"), getString("634")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		super.onInitialize();
	}

	private IMenuItem getSubItem(String lbl, String title, MenuActions action, MenuParams param) {
		return new MainMenuItem(lbl, title, action, param) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onClick(MainPanel.this, target);
			}
		};
	}

	private List<IMenuItem> getMainMenu() {
		List<IMenuItem> mmenu = new ArrayList<>();
		{
			// Dashboard Menu Points
			List<IMenuItem> l = new ArrayList<>();
			l.add(getSubItem("290", "1450", MenuActions.dashboardModuleStartScreen, null));
			l.add(getSubItem("291", "1451", MenuActions.dashboardModuleCalendar, null));
			mmenu.add(new OmMenuItem(getString("124"), l));
		}
		{
			// Conference Menu Points
			List<IMenuItem> l = new ArrayList<>();
			l.add(getSubItem("777", "1506", MenuActions.conferenceModuleRoomList, MenuParams.publicTabButton));
			l.add(getSubItem("779", "1507", MenuActions.conferenceModuleRoomList, MenuParams.privateTabButton));
			if (getBean(ConfigurationDao.class).getBool(CONFIG_MYROOMS_ENABLED, true)) {
				l.add(getSubItem("781", "1508", MenuActions.conferenceModuleRoomList, MenuParams.myTabButton));
			}
			List<Room> recent = getBean(RoomDao.class).getRecent(getUserId());
			if (!recent.isEmpty()) {
				l.add(new OmMenuItem(DELIMITER, (String)null));
			}
			for (Room r : recent) {
				final Long roomId = r.getId();
				l.add(new OmMenuItem(r.getName(), r.getName()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						RoomEnterBehavior.roomEnter((MainPage)getPage(), target, roomId);
					}
				});
			}
			mmenu.add(new OmMenuItem(getString("792"), l));
		}
		{
			// Recording Menu Points
			List<IMenuItem> l = new ArrayList<>();
			l.add(getSubItem("395", "1452", MenuActions.recordModule, null));
			mmenu.add(new OmMenuItem(getString("395"), l));
		}
		Set<Right> r = WebSession.getRights();
		boolean isAdmin = hasAdminLevel(r);
		if (isAdmin || hasGroupAdminLevel(r)) {
			// Administration Menu Points
			List<IMenuItem> l = new ArrayList<>();
			l.add(getSubItem("125", "1454", MenuActions.adminModuleUser, null));
			if (isAdmin) {
				l.add(getSubItem("597", "1455", MenuActions.adminModuleConnections, null));
			}
			l.add(getSubItem("126", "1456", MenuActions.adminModuleOrg, null));
			l.add(getSubItem("186", "1457", MenuActions.adminModuleRoom, null));
			if (isAdmin) {
				l.add(getSubItem("263", "1458", MenuActions.adminModuleConfiguration, null));
				l.add(getSubItem("348", "1459", MenuActions.adminModuleLanguages, null));
				l.add(getSubItem("1103", "1454", MenuActions.adminModuleLDAP, null));
				l.add(getSubItem("1571", "1572", MenuActions.adminModuleOAuth, null));
				l.add(getSubItem("367", "1461", MenuActions.adminModuleBackup, null));
				l.add(getSubItem("main.menu.admin.email", "main.menu.admin.email.desc", MenuActions.adminModuleEmail, null));
			}
			mmenu.add(new OmMenuItem(getString("6"), l));
		}
		return mmenu;
	}

	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler) {
		updateContents(f, handler, true);
	}

	private BasePanel getCurrentPanel() {
		Component prev = contents.get(CHILD_ID);
		if (prev != null && prev instanceof BasePanel) {
			return (BasePanel)prev;
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
				this.panel = npanel;
			}
			if (updateFragment) {
				UrlFragment uf = new UrlFragment(handler);
				uf.set(f.getArea().name(), f.getType());
			}
		}
	}

	private void updateContents(BasePanel panel, IPartialPageRequestHandler handler) {
		if (panel != null) {
			BasePanel prev = getCurrentPanel();
			if (prev != null) {
				prev.cleanup(handler);
			}
			handler.add(contents.replace(panel));
			panel.onMenuPanelLoad(handler);
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
		return getBean(ClientManager.class).get(uid);
	}
}
