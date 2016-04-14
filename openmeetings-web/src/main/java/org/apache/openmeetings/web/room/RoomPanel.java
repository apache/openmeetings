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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addUserToRoom;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.activities.ActivitiesPanel;
import org.apache.openmeetings.web.room.activities.Activity;
import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.openmeetings.web.room.message.RoomMessage;
import org.apache.openmeetings.web.room.sidebar.RoomSidebar;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.event.WebSocketPushPayload;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

@AuthorizeInstantiation("Room")
public class RoomPanel extends BasePanel {
	//TODO demoTime - demo timer
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, webAppRootKey);
	private final Room r;
	private final Client client;
	private final RoomMenuPanel menu;
	private final RoomSidebar sidebar;
	private final WebMarkupContainer room = new WebMarkupContainer("roomContainer");
	private final AbstractDefaultAjaxBehavior aab = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			target.appendJavaScript("setHeight();");
			//TODO SID etc
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			try {
				URL url = new URL(cfgDao.getBaseUrl());
				String path = url.getPath();
				path = path.substring(1, path.indexOf('/', 2) + 1);
				broadcast(new RoomMessage(r.getId(), RoomMessage.Type.roomEnter));
				getMainPage().getChat().roomEnter(r, target);
			} catch (MalformedURLException e) {
				log.error("Error while constructing room parameters", e);
			}
		}
	};
	private final ActivitiesPanel activities;
	
	public RoomPanel(String id, Room r) {
		super(id);
		this.r = r;
		Component accessDenied = new WebMarkupContainer("accessDenied").setVisible(false);
		boolean allowed = false;
		String deniedMessage = null;
		if (r.isAppointment()) {
			Appointment a = getBean(AppointmentDao.class).getByRoom(r.getId());
			if (a != null && !a.isDeleted()) {
				allowed = a.getOwner().getId().equals(getUserId());
				log.debug("appointed room, isOwner ? " + allowed);
				if (!allowed) {
					for (MeetingMember mm : a.getMeetingMembers()) {
						if (mm.getUser().getId() == getUserId()) {
							allowed = true;
							break;
						}
					}
				}
				/*
				TODO need to be reviewed
				Calendar c = WebSession.getCalendar();
				if (c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
					allowed = true;
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm"); //FIXME format
					deniedMessage = Application.getString(1271) + String.format(" %s - %s", sdf.format(a.getStart()), sdf.format(a.getEnd()));
				}
				*/
			}
		} else {
			allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(getUserId()));
			log.debug("public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
			if (!allowed) {
				User u = getBean(UserDao.class).get(getUserId());
				for (RoomGroup ro : r.getRoomGroups()) {
					for (GroupUser ou : u.getGroupUsers()) {
						if (ro.getGroup().getId().equals(ou.getGroup().getId())) {
							allowed = true;
							break;
						}
					}
					if (allowed) {
						break;
					}
				}
			}
		}
		if (!allowed) {
			if (deniedMessage == null) {
				deniedMessage = Application.getString(1599);
			}
			accessDenied = new ExpiredMessageDialog("accessDenied", deniedMessage);
			room.setVisible(false);
		}
		client = new Client(r.getId());
		room.add((menu = new RoomMenuPanel("roomMenu", this)).setVisible(!r.getHideTopBar()));
		room.add(new SwfPanel("whiteboard", client));
		room.add(aab);
		room.add(sidebar = new RoomSidebar("sidebar", this));
		room.add((activities = new ActivitiesPanel("activitiesPanel", r.getId())).setVisible(!r.isActivitiesHidden()));
		add(room, accessDenied);
	}

	@Override
	public void onEvent(IEvent<?> event) {
		if (event.getPayload() instanceof WebSocketPushPayload) {
			WebSocketPushPayload wsEvent = (WebSocketPushPayload) event.getPayload();
			if (wsEvent.getMessage() instanceof RoomMessage) {
				RoomMessage m = (RoomMessage)wsEvent.getMessage();
				IPartialPageRequestHandler handler = wsEvent.getHandler();
				switch (m.getType()) {
					case pollCreated:
						if (getUserId() != m.getUserId()) {
							menu.pollCreated(handler);
						}
					case pollClosed:
					case pollDeleted:
					case voted:
					case rightUpdated:
						menu.update(handler);
						break;
					case roomEnter:
						menu.update(handler);
						sidebar.updateUsers(handler);
						//activities.addActivity(m.getUid(), m.getSentUserId(), Activity.Type.roomEnter, handler);
						break;
					case roomExit:
						//TODO check user/remove tab
						sidebar.updateUsers(handler);
						activities.addActivity(m.getUid(), m.getUserId(), Activity.Type.roomExit, handler);
						break;
					case requestRightModerator:
						if (isModerator(getUserId(), r.getId())) {
							activities.addActivity(m.getUid(), m.getUserId(), Activity.Type.requestRightModerator, handler);
						}
						break;
					default:
						break;
				}
			}
		}
		super.onEvent(event);
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (room.isVisible()) {
			addUserToRoom(client, getPage().getPageId());
			User u = getBean(UserDao.class).get(getUserId());
			//TODO do we need to check GroupModerationRights ????
			if (AuthLevelUtil.hasAdminLevel(u.getRights())) {
				client.getRights().add(Client.Right.moderator);
			} else {
				if (!r.isModerated() && 1 == getRoomUsers(r.getId()).size()) {
					client.getRights().add(Client.Right.moderator);
				} else if (r.isModerated()) {
					//TODO why do we need supermoderator ????
					for (RoomModerator rm : r.getModerators()) {
						if (getUserId() == rm.getUser().getId()) {
							client.getRights().add(Client.Right.moderator);
							break;
						}
					}
				}
			}
		}
	}
	
	public static void broadcast(final RoomMessage m) {
		WebSocketSettings settings = WebSocketSettings.Holder.get(Application.get());
		IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
		Executor executor = settings.getWebSocketPushMessageExecutor();
		for (Client c : getRoomUsers(m.getRoomId())) {
			try {
				final IWebSocketConnection wsConnection = reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId()));
				if (wsConnection != null) {
					executor.run(new Runnable() {
						@Override
						public void run() {
							wsConnection.sendMessage(m);
						}
					});
				}
			} catch (Exception e) {
				log.error("Error while broadcasting message to room", e);
			}
		}
	}
	
	public static boolean isModerator(long userId, long roomId) {
		for (Client c : getRoomUsers(roomId)) {
			if (c.getUserId() == userId && c.hasRight(Client.Right.moderator)) {
				return true;
			}
		}
		return false;
	}
	
	public static void sendRoom(long roomId, String msg) {
		IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
		for (Client c : getRoomUsers(roomId)) {
			try {
				reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId())).sendMessage(msg);
			} catch (Exception e) {
				log.error("Error while sending message to room", e);
			}
		}
	}
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		handler.add(getMainPage().getHeader().setVisible(false), getMainPage().getTopControls().setVisible(false));
		if (r.isChatHidden()) {
			getMainPage().getChat().toggle(handler, false);
		}
		handler.appendJavaScript("roomLoad();");
	}
	
	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		handler.add(getMainPage().getHeader().setVisible(true), getMainPage().getTopControls().setVisible(true));
		if (r.isChatHidden()) {
			getMainPage().getChat().toggle(handler, true);
		}
		handler.appendJavaScript("$(window).off('resize.openmeetings');");
		RoomMenuPanel.roomExit(this);
		getMainPage().getChat().roomExit(r, handler);
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(RoomPanel.class, "room.js");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
		if (room.isVisible()) {
			response.render(OnDomReadyHeaderItem.forScript(aab.getCallbackScript()));
		}
	}

	class ExpiredMessageDialog extends MessageDialog {
		private static final long serialVersionUID = 1L;
		public boolean autoOpen = false;
		
		public ExpiredMessageDialog(String id, String message) {
			super(id, Application.getString(204), message, DialogButtons.OK, DialogIcon.ERROR);
			autoOpen = true;
		}
		
		@Override
		public boolean isModal() {
			return true;
		}
		
		@Override
		public void onConfigure(JQueryBehavior behavior) {
			super.onConfigure(behavior);
			behavior.setOption("autoOpen", autoOpen);
		}
		
		@Override
		public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
			menu.exit(handler);
		}
	}
	
	public Room getRoom() {
		return r;
	}
	
	public Client getClient() {
		return client;
	}
	
	public RoomSidebar getSidebar() {
		return sidebar;
	}

	public ActivitiesPanel getActivities() {
		return activities;
	}
}
