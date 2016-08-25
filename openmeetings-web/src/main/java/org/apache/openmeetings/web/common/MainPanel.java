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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addOnlineUser;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getClientByKeys;
import static org.apache.openmeetings.web.app.Application.removeOnlineUser;
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

import org.apache.openmeetings.db.dao.basic.NavigationDao;
import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.basic.Navimain;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.menu.MainMenuItem;
import org.apache.openmeetings.web.common.menu.MenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.user.AboutDialog;
import org.apache.openmeetings.web.user.ChatPanel;
import org.apache.openmeetings.web.user.MessageDialog;
import org.apache.openmeetings.web.user.UserInfoDialog;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.urlfragment.UrlFragment;

import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

public class MainPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPanel.class, webAppRootKey);
	public final static String PARAM_USER_ID = "userId";
	private Client client;
	private final MenuPanel menu;
	private final WebMarkupContainer topControls = new WebMarkupContainer("topControls");
	private final WebMarkupContainer topLinks = new WebMarkupContainer("topLinks");
	private final MarkupContainer contents;
	private final ChatPanel chat;
	private final MessageDialog newMessage;
	private final UserInfoDialog userInfo;

	public MainPanel(String id) {
		this(id, new WebMarkupContainer(CHILD_ID));
	}

	public MainPanel(String id, WebMarkupContainer panel) {
		super(id);
		client = new Client();
		add(topControls.setOutputMarkupPlaceholderTag(true).setMarkupId("topControls"));
		menu = new MenuPanel("menu", getMainMenu());
		contents = new WebMarkupContainer("contents");
		add(contents.add(panel).setOutputMarkupId(true).setMarkupId("contents"));
		topControls.add(menu.setVisible(false), topLinks.setVisible(false).setOutputMarkupPlaceholderTag(true).setMarkupId("topLinks"));
		topLinks.add(new AjaxLink<Void>("messages") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_MESSAGES, target);
			}
		});
		topLinks.add(new ConfirmableAjaxBorder("logout", getString("310"), getString("634")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
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
		add(about, chat = new ChatPanel("chatPanel"));
		add(newMessage = new MessageDialog("newMessageDialog", new CompoundPropertyModel<PrivateMessage>(new PrivateMessage())) {
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
				response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("showUserInfo", this, explicit(PARAM_USER_ID)), "showUserInfo")));
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
				response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("addContact", this, explicit(PARAM_USER_ID)), "addContact")));
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
				response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("privateMessage", this, explicit(PARAM_USER_ID)), "privateMessage")));
			}
		});
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				addOnlineUser(new Client(message.getSessionId(), message.getKey(), getUserId()));
				log.debug(String.format("WebSocketBehavior::onConnect [session: %s, key: %s]", message.getSessionId(), message.getKey()));
			}
			
			@Override
			protected void onClose(ClosedMessage message) {
				Client client = getClientByKeys(getUserId(), WebSession.get().getId());
				removeOnlineUser(client);
				super.onClose(message);
				log.debug("WebSocketBehavior::onClose");
			}
		});
	}

	private List<IMenuItem> getMainMenu() {
		List<IMenuItem> menu = new ArrayList<>();
		for (Naviglobal gl : getBean(NavigationDao.class).getMainMenu(AuthLevelUtil.hasAdminLevel(WebSession.getRights()))) {
			List<IMenuItem> l = new ArrayList<>();
			for (Navimain nm : gl.getMainnavi()) {
				l.add(new MainMenuItem(nm) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						onClick(MainPanel.this, target);
					}
				}); 
			}
			menu.add(new MenuItem(Application.getString(gl.getLabelId()), l));
		}
		return menu;
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
		BasePanel panel = getPanel(f.getArea(), f.getType());
		if (panel != null) {
			BasePanel prev = getCurrentPanel();
			if (prev != null) {
				prev.cleanup(handler);
			}
			handler.add(contents.replace(panel));
			if (updateFragment) {
				UrlFragment uf = new UrlFragment(handler);
				uf.set(f.getArea().name(), f.getType());
			}
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

	public Client getClient() {
		return client;
	}
}
