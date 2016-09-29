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
import static org.apache.openmeetings.web.app.Application.getOnlineClient;
import static org.apache.openmeetings.web.app.Application.getRoomClients;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.core.remote.ConferenceLibrary;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.RoomMessage.Type;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.activities.ActivitiesPanel;
import org.apache.openmeetings.web.room.activities.Activity;
import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.openmeetings.web.room.sidebar.RoomSidebar;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.whiteboard.WhiteboardBehavior;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

@AuthorizeInstantiation("Room")
public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, webAppRootKey);
	private static final String ACCESS_DENIED_ID = "access-denied";
	private static final String EVENT_DETAILS_ID = "event-details";
	public enum Action {
		kick
		, settings
		, refresh
	}
	private final Room r;
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
				target.appendJavaScript(String.format("initVideo(%s);", new JSONObject()
						.put("uid", getClient().getUid())
						.put("audioOnly", r.isAudioOnly())
						.put("SID", WebSession.getSid())
						.put("interview", Room.Type.interview == r.getType())
						//.put("protocol", cfgDao.getConfValue(CONFIG_FLASH_PROTOCOL, String.class, ""))
						.put("host", url.getHost())
						//.put("port", cfgDao.getConfValue(CONFIG_FLASH_PORT, String.class, ""))
						.put("app", path + r.getId())
						.put("labels", SwfPanel.getStringLabels("448", "449", "450", "451", "758", "447", "52", "53"
								, "1429", "1430", "775", "452", "767", "764", "765", "918", "54", "761", "762"))
						.toString()
						));
				broadcast(new RoomMessage(r.getId(), getUserId(), RoomMessage.Type.roomEnter));
				getMainPanel().getChat().roomEnter(r, target);
			} catch (MalformedURLException e) {
				log.error("Error while constructing room parameters", e);
			}
		}
	};
	private RedirectMessageDialog roomClosed;
	private RoomMenuPanel menu;
	private RoomSidebar sidebar;
	private ActivitiesPanel activities;
	private String sharingUser = null;
	private String recordingUser = null;
	private String publishingUser = null; //TODO add
	private long activeWbId = -1;
	
	public RoomPanel(String id, Room r) {
		super(id);
		this.r = r;
		//TODO check here and set 
		//private String recordingUser = null;
		//private String sharingUser = null;
		//private String publishingUser = null;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Component accessDenied = new WebMarkupContainer(ACCESS_DENIED_ID).setVisible(false);
		Component eventDetail = new WebMarkupContainer(EVENT_DETAILS_ID).setVisible(false);

		room.add(menu = new RoomMenuPanel("menu", this));
		Droppable<FileItem> wbArea = new Droppable<FileItem>("wb-area") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("hoverClass", Options.asString("ui-state-hover"));
				behavior.setOption("accept", Options.asString(".recorditem, .fileitem"));
			}
			
			@Override
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (activeWbId > -1 && o instanceof FileItem) {
					sendFileToWb((FileItem)o, false);
				}
			}
		};
		WebMarkupContainer wb = new WebMarkupContainer("whiteboard");
		room.add(wbArea.add(wb.setOutputMarkupId(true)));
		room.add(new WhiteboardBehavior("1", wb.getMarkupId(), null, null, null));
		room.add(aab);
		room.add(sidebar = new RoomSidebar("sidebar", this));
		room.add(activities = new ActivitiesPanel("activities", this));
		add(roomClosed = new RedirectMessageDialog("room-closed", "1098", r.isClosed(), r.getRedirectURL()));
		if (r.isClosed()) {
			room.setVisible(false);
		} else if (getRoomClients(r.getId()).size() >= r.getNumberOfPartizipants()) {
			accessDenied = new ExpiredMessageDialog(ACCESS_DENIED_ID, getString("99"), menu);
			room.setVisible(false);
		} else if (r.getId().equals(WebSession.get().getRoomId())) {
			// secureHash/invitationHash, already checked
		} else {
			boolean allowed = false;
			String deniedMessage = null;
			if (r.isAppointment()) {
				Appointment a = getBean(AppointmentDao.class).getByRoom(r.getId());
				if (a != null && !a.isDeleted()) {
					allowed = a.getOwner().getId().equals(getUserId());
					log.debug("appointed room, isOwner ? " + allowed);
					if (!allowed) {
						for (MeetingMember mm : a.getMeetingMembers()) {
							if (getUserId().equals(mm.getUser().getId())) {
								allowed = true;
								break;
							}
						}
					}
					if (allowed) {
						Calendar c = WebSession.getCalendar();
						if (c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
							eventDetail = new EventDetailDialog(EVENT_DETAILS_ID, a);
						} else {
							allowed = false;
							deniedMessage = getString("1271") + String.format(" %s - %s", getDateFormat().format(a.getStart()), getDateFormat().format(a.getEnd()));
						}
					}
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
					deniedMessage = getString("1599");
				}
				accessDenied = new ExpiredMessageDialog(ACCESS_DENIED_ID, deniedMessage, menu);
				room.setVisible(false);
			}
		}
		add(room, accessDenied, eventDetail);
		if (r.isWaitForRecording()) {
			add(new MessageDialog("wait-for-recording", getString("1316"), getString("1315"), DialogButtons.OK, DialogIcon.LIGHT) {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onConfigure(JQueryBehavior behavior) {
					super.onConfigure(behavior);
					behavior.setOption("autoOpen", true);
					behavior.setOption("resizable", false);
				}
				
				@Override
				public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				}
			});
		} else {
			add(new WebMarkupContainer("wait-for-recording").setVisible(false));
		}
		if (room.isVisible()) {
			add(new NicknameDialog("nickname", this));
		} else {
			add(new WebMarkupContainer("nickname").setVisible(false));
		}
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
						break;
					case recordingStoped:
						{
							String uid = ((TextRoomMessage)m).getText();
							if (Strings.isEmpty(uid) || !uid.equals(recordingUser)) {
								log.error("Not existing/BAD user has stopped recording {} != {} !!!!", uid, recordingUser);
							}
							recordingUser = null;
							menu.update(handler);
							Client c = getOnlineClient(uid);
							if (c == null) {
								log.error("Not existing user has stopped recording {} !!!!", uid);
								return;
							}
							c.getActivities().remove(Client.Activity.record);
						}
						break;
					case recordingStarted:
						{
							recordingUser = ((TextRoomMessage)m).getText();
							menu.update(handler);
							Client c = getOnlineClient(recordingUser);
							if (c == null) {
								log.error("Not existing user has started recording {} !!!!", recordingUser);
								return;
							}
							c.getActivities().add(Client.Activity.record);
						}
						break;
					case sharingStoped:
						//TODO check sharingUser == ((TextRoomMessage)m).getText();
						sharingUser = null;
						menu.update(handler);
						break;
					case sharingStarted:
						{
							sharingUser = ((TextRoomMessage)m).getText();
							menu.update(handler);
						}
						break;
					case rightUpdated:
						sidebar.updateUsers(handler);
						menu.update(handler);
						break;
					case roomEnter:
						sidebar.updateUsers(handler);
						menu.update(handler);
						// TODO should this be fixed?
						//activities.addActivity(new Activity(m, Activity.Type.roomEnter), handler);
						break;
					case roomExit:
						//TODO check user/remove tab
						sidebar.updateUsers(handler);
						activities.add(new Activity(m, Activity.Type.roomExit), handler);
						break;
					case roomClosed:
						handler.add(room.setVisible(false));
						roomClosed.open(handler);
						break;
					case requestRightModerator:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightModerator), handler);
						break;
					case requestRightWb:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightWb), handler);
						break;
					case requestRightShare:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightShare), handler);
						break;
					case requestRightRemote:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightRemote), handler);
						break;
					case requestRightA:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightA), handler);
						break;
					case requestRightAv:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightAv), handler);
						break;
					case requestRightMute:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightMute), handler);
						break;
					case requestRightExclusive:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightExclusive), handler);
						break;
					case activityRemove:
						{
							TextRoomMessage tm = (TextRoomMessage)m;
							activities.remove(tm.getText(), handler);
						}
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
			//We are setting initial rights here
			Client c = getClient();
			addUserToRoom(c.setRoomId(getRoom().getId()));
			SOAPLogin soap = WebSession.get().getSoapLogin();
			if (soap != null && soap.isModerator()) {
				c.getRights().add(Right.superModerator);
			} else {
				User u = getBean(UserDao.class).get(getUserId());
				Right rr = AuthLevelUtil.getRoomRight(u, r, r.isAppointment() ? getBean(AppointmentDao.class).getByRoom(r.getId()) : null, getRoomClients(r.getId()).size());
				if (rr != null) {
					c.getRights().add(rr);
				}
			}
		}
	}
	
	public static void broadcast(final RoomMessage m) {
		WebSocketSettings settings = WebSocketSettings.Holder.get(Application.get());
		IWebSocketConnectionRegistry reg = settings.getConnectionRegistry();
		Executor executor = settings.getWebSocketPushMessageExecutor();
		for (Client c : getRoomClients(m.getRoomId())) {
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
		return hasRight(userId, roomId, Right.moderator);
	}
	
	public static boolean hasRight(long userId, long roomId, Right r) {
		for (Client c : getRoomClients(roomId)) {
			if (c.getUserId().equals(userId) && c.hasRight(r)) {
				return true;
			}
		}
		return false;
	}
	
	public static void sendRoom(long roomId, String msg) {
		IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
		for (Client c : getRoomClients(roomId)) {
			try {
				reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId())).sendMessage(msg);
			} catch (Exception e) {
				log.error("Error while sending message to room", e);
			}
		}
	}

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		getBasePage().getHeader().setVisible(false);
		getMainPanel().getTopControls().setVisible(false);
		if (r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, false);
		}
		if (handler != null) {
			handler.add(getBasePage().getHeader(), getMainPanel().getTopControls());
			handler.appendJavaScript("roomLoad();");
		}
		return this;
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		handler.add(getBasePage().getHeader().setVisible(true), getMainPanel().getTopControls().setVisible(true));
		if (r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.appendJavaScript("$(window).off('resize.openmeetings');");
		RoomMenuPanel.roomExit(getClient());
		getMainPanel().getChat().roomExit(r, handler);
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

	public void requestRight(AjaxRequestTarget target, Right right) {
		RoomMessage.Type reqType = null;
		switch (right) {
			case moderator:
				reqType = Type.requestRightModerator;
				break;
			case whiteBoard:
				reqType = Type.requestRightWb;
				break;
			case share:
				reqType = Type.requestRightWb;
				break;
			case audio:
				reqType = Type.requestRightA;
				break;
			case exclusive:
				reqType = Type.requestRightExclusive;
				break;
			case mute:
				reqType = Type.requestRightMute;
				break;
			case remoteControl:
				reqType = Type.requestRightRemote;
				break;
			case video:
				reqType = Type.requestRightAv;
				break;
			default:
				break;
		}
		if (reqType != null) {
			RoomPanel.broadcast(new TextRoomMessage(getRoom().getId(), getUserId(), reqType, getClient().getUid()));
		}
	}
	
	public void allowRight(AjaxRequestTarget target, Client client, Right... rights) {
		for (Right right : rights) {
			client.getRights().add(right);
		}
		broadcast(target, client);
	}

	public void denyRight(AjaxRequestTarget target, Client client, Right... rights) {
		for (Right right : rights) {
			client.getRights().remove(right);
		}
		broadcast(target, client);
	}

	public void broadcast(AjaxRequestTarget target, Client client) {
		broadcast(new RoomMessage(getRoom().getId(), getUserId(), RoomMessage.Type.rightUpdated));
		RoomBroadcaster.sendUpdatedClient(client);
	}
	
	public Room getRoom() {
		return r;
	}
	
	public Client getClient() {
		return getMainPanel().getClient();
	}

	public boolean screenShareAllowed() {
		Room r = getRoom();
		return Room.Type.interview != r.getType() && !r.isHidden(RoomElement.ScreenSharing)
				&& r.isAllowRecording() && getClient().hasRight(Right.share) && getSharingUser() == null;
	}
	
	public RoomSidebar getSidebar() {
		return sidebar;
	}

	public ActivitiesPanel getActivities() {
		return activities;
	}
	
	public String getSharingUser() {
		return sharingUser;
	}

	public String getRecordingUser() {
		return recordingUser;
	}

	public String getPublishingUser() {
		return publishingUser;
	}

	public void sendFileToWb(FileItem fi, boolean clean) {
		if (activeWbId > -1) {
			if (fi.getType() == FileItem.Type.WmlFile) {
				getBean(ConferenceLibrary.class).sendToWhiteboard(getClient().getUid(), activeWbId, fi);
			} else {
				String url = urlFor(new RoomResourceReference(), new PageParameters().add("id", fi.getId())).toString();
				getBean(ScopeApplicationAdapter.class).sendToWhiteboard(getClient().getUid(), activeWbId, fi, url, clean);
			}
		}
	}
}
