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

import static org.apache.openmeetings.db.util.RoomHelper.videoJson;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.wb.InterviewWbPanel.INTERVIEWWB_JS_REFERENCE;
import static org.apache.openmeetings.web.room.wb.WbPanel.WB_JS_REFERENCE;
import static org.apache.wicket.util.time.Duration.NONE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.RoomMessage.Type;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.QuickPollManager;
import org.apache.openmeetings.web.app.StreamClientManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.openmeetings.web.room.activities.Activity;
import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.openmeetings.web.room.sidebar.RoomSidebar;
import org.apache.openmeetings.web.room.wb.AbstractWbPanel;
import org.apache.openmeetings.web.room.wb.InterviewWbPanel;
import org.apache.openmeetings.web.room.wb.WbPanel;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.ws.api.event.WebSocketPushPayload;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
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
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, getWebAppRootKey());
	private static final String ACCESS_DENIED_ID = "access-denied";
	private static final String EVENT_DETAILS_ID = "event-details";
	public enum Action {
		kick
		, exclusive
		, mute
	}
	private final Room r;
	private final boolean interview;
	private final WebMarkupContainer room = new WebMarkupContainer("roomContainer");
	private final AbstractDefaultAjaxBehavior roomEnter = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			log.debug("RoomPanel::roomEnter");
			WebSession ws = WebSession.get();
			ExtendedClientProperties cp = ws.getExtendedProperties();
			getBean(ConferenceLogDao.class).add(
					ConferenceLog.Type.roomEnter
					, getUserId(), "0", r.getId()
					, cp.getRemoteAddress()
					, String.valueOf(r.getId()));
			Client _c = getClient();
			JSONObject options = VideoSettings.getInitJson(cp, r.getId(), _c.getSid())
					.put("uid", _c.getUid())
					.put("rights", _c.toJson(true).getJSONArray("rights"))
					.put("interview", interview)
					.put("showMicStatus", !r.getHiddenElements().contains(RoomElement.MicrophoneStatus))
					.put("exclusiveTitle", getString("1386"));
			if (!Strings.isEmpty(r.getRedirectURL()) && (ws.getSoapLogin() != null || ws.getInvitation() != null)) {
				options.put("reloadUrl", r.getRedirectURL());
			}
			StringBuilder sb = new StringBuilder("Room.init(").append(options.toString(new NullStringer())).append(");")
					.append(wb.getInitScript())
					.append("Room.setSize();")
					.append(getQuickPollJs());
			target.appendJavaScript(sb);
			WebSocketHelper.sendRoom(new RoomMessage(r.getId(), _c, RoomMessage.Type.roomEnter));
			// play video from other participants
			initVideos(target);
			getMainPanel().getChat().roomEnter(r, target);
			if (r.isFilesOpened()) {
				sidebar.setFilesActive(target);
			}
			if (Room.Type.presentation != r.getType()) {
				List<Client> mods = getBean(ClientManager.class).listByRoom(r.getId(), c -> c.hasRight(Room.Right.moderator));
				log.debug("RoomPanel::roomEnter, mods IS EMPTY ? {}, is MOD ? {}", mods.isEmpty(), _c.hasRight(Room.Right.moderator));
				if (mods.isEmpty()) {
					waitApplyModeration.open(target);
				}
			}
			wb.update(target);
		}

		private void initVideos(AjaxRequestTarget target) {
			StringBuilder sb = new StringBuilder();
			boolean hasStreams = false;
			Client _c = getClient();
			for (Client c: getBean(ClientManager.class).listByRoom(getRoom().getId())) {
				boolean self = _c.getUid().equals(c.getUid());
				for (String uid : c.getStreams()) {
					JSONObject jo = videoJson(c, self, c.getSid(), getBean(StreamClientManager.class), uid);
					sb.append(String.format("VideoManager.play(%s);", jo));
					hasStreams = true;
				}
			}
			if (interview && recordingUser == null && hasStreams && _c.hasRight(Right.moderator)) {
				sb.append("WbArea.setRecEnabled(true);");
			}
			if (!Strings.isEmpty(sb)) {
				target.appendJavaScript(sb);
			}
		}
	};
	private RedirectMessageDialog roomClosed;
	private MessageDialog clientKicked, waitForModerator, waitApplyModeration;

	private RoomMenuPanel menu;
	private RoomSidebar sidebar;
	private final AbstractWbPanel wb;
	private String sharingUser = null;
	private String recordingUser = null;
	private byte[] pdfWb;
	private final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new ResourceStreamResource() {
		private static final long serialVersionUID = 1L;

		{
			setCacheDuration(NONE);
			setFileName("whiteboard.pdf");
		}

		@Override
		protected IResourceStream getResourceStream(Attributes attributes) {
			return new AbstractResourceStream() {
				private static final long serialVersionUID = 1L;

				@Override
				public InputStream getInputStream() throws ResourceStreamNotFoundException {
					return new ByteArrayInputStream(pdfWb);
				}

				@Override
				public void close() throws IOException {
					//no-op
				}
			};
		}
	}) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onDownloadCompleted(AjaxRequestTarget target) {
			super.onDownloadCompleted(target);
			pdfWb = null;
		}
	};
	Component eventDetail = new WebMarkupContainer(EVENT_DETAILS_ID).setVisible(false);

	public RoomPanel(String id, Room r) {
		super(id);
		this.r = r;
		this.interview = Room.Type.interview == r.getType();
		this.wb = interview ? new InterviewWbPanel("whiteboard", this) : new WbPanel("whiteboard", this);
	}

	public void startDownload(AjaxRequestTarget target, byte[] bb) {
		pdfWb = bb;
		download.initiate(target);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		//let's refresh user in client
		ClientManager cm = getBean(ClientManager.class);
		cm.update(getClient().updateUser(getBean(UserDao.class)));
		Component accessDenied = new WebMarkupContainer(ACCESS_DENIED_ID).setVisible(false);

		room.add(AttributeModifier.append(ATTR_CLASS, r.getType().name()));
		room.add(menu = new RoomMenuPanel("menu", this));
		room.add(AttributeModifier.append("data-room-id", r.getId()));
		if (interview) {
			room.add(new WebMarkupContainer("wb-area").add(wb));
		} else {
			Droppable<BaseFileItem> wbArea = new Droppable<BaseFileItem>("wb-area") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onConfigure(JQueryBehavior behavior) {
					super.onConfigure(behavior);
					behavior.setOption("hoverClass", Options.asString("ui-state-hover"));
					behavior.setOption("accept", Options.asString(".recorditem, .fileitem, .readonlyitem"));
				}

				@Override
				public void onDrop(AjaxRequestTarget target, Component component) {
					Object o = component.getDefaultModelObject();
					if (wb.isVisible() && o instanceof BaseFileItem) {
						BaseFileItem f = (BaseFileItem)o;
						if (sidebar.getFilesPanel().isSelected(f)) {
							for (Entry<String, BaseFileItem> e : sidebar.getFilesPanel().getSelected().entrySet()) {
								wb.sendFileToWb(e.getValue(), false);
							}
						} else {
							wb.sendFileToWb(f, false);
						}
					}
				}
			};
			room.add(wbArea.add(wb));
		}
		room.add(roomEnter);
		room.add(sidebar = new RoomSidebar("sidebar", this));
		add(roomClosed = new RedirectMessageDialog("room-closed", "1098", r.isClosed(), r.getRedirectURL()));
		if (r.isClosed()) {
			room.setVisible(false);
		} else if (cm.listByRoom(r.getId()).size() >= r.getCapacity()) {
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
					boolean isOwner = a.getOwner().getId().equals(getUserId());
					allowed = isOwner;
					log.debug("appointed room, isOwner ? {}", isOwner);
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
						if (isOwner || c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
							eventDetail = new EventDetailDialog(EVENT_DETAILS_ID, a);
						} else {
							allowed = false;
							deniedMessage = String.format("%s %s - %s", getString("error.hash.period"), getDateFormat().format(a.getStart()), getDateFormat().format(a.getEnd()));
						}
					}
				}
			} else {
				allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(getUserId()));
				log.debug("public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
				if (!allowed) {
					User u = getClient().getUser();
					for (RoomGroup ro : r.getGroups()) {
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
		waitForModerator = new MessageDialog("wait-for-moderator", getString("204"), getString("696"), DialogButtons.OK, DialogIcon.LIGHT) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				// no-op
			}
		};
		waitApplyModeration = new MessageDialog("wait-apply-moderation", getString("204"), getString(r.isModerated() ? "641" : "498"), DialogButtons.OK, DialogIcon.LIGHT) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				// no-op
			}
		};
		add(room, accessDenied, eventDetail, waitForModerator, waitApplyModeration);
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
					//no-op
				}
			});
		} else {
			add(new WebMarkupContainer("wait-for-recording").setVisible(false));
		}
		if (room.isVisible()) {
			add(new NicknameDialog("nickname", this));
			add(download);
		} else {
			add(new WebMarkupContainer("nickname").setVisible(false));
		}
		add(clientKicked = new MessageDialog("client-kicked", getString("797"), getString("606"), DialogButtons.OK, DialogIcon.ERROR) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				menu.exit(handler);
			}
		});
	}

	@Override
	public void onEvent(IEvent<?> event) {
		Client _c = getClient();
		if (_c != null && event.getPayload() instanceof WebSocketPushPayload) {
			WebSocketPushPayload wsEvent = (WebSocketPushPayload) event.getPayload();
			if (wsEvent.getMessage() instanceof RoomMessage) {
				RoomMessage m = (RoomMessage)wsEvent.getMessage();
				IPartialPageRequestHandler handler = wsEvent.getHandler();
				ClientManager cm = getBean(ClientManager.class);
				switch (m.getType()) {
					case pollCreated:
						menu.updatePoll(handler, m.getUserId());
						break;
					case pollUpdated:
						menu.updatePoll(handler, null);
						break;
					case recordingStoped:
						{
							String uid = ((TextRoomMessage)m).getText();
							Client c = cm.getBySid(uid);
							if (c == null) {
								log.error("Not existing/BAD user has stopped recording {} != {} !!!!", uid);
								return;
							}
							recordingUser = null;
							cm.update(c.remove(Client.Activity.record));
							menu.update(handler);
							updateInterviewRecordingButtons(handler);
						}
						break;
					case recordingStarted:
						{
							JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
							String sid = obj.getString("sid");
							Client c = cm.getBySid(sid);
							if (c == null) {
								log.error("Not existing user has started recording {} !!!!", sid);
								return;
							}
							recordingUser = sid;
							cm.update(c.set(Client.Activity.record));
							menu.update(handler);
							updateInterviewRecordingButtons(handler);
						}
						break;
					case sharingStoped:
						{
							JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
							String uid = obj.getString("uid");
							Client c = cm.getBySid(obj.getString("sid"));
							if (c == null) {
								log.error("Not existing user has started sharing {} !!!!", obj);
								return;
							}
							handler.appendJavaScript(String.format("VideoManager.close('%s', true);", uid));
							sharingUser = null;
							cm.update(c.removeStream(uid).remove(Client.Activity.share));
							menu.update(handler);
						}
						break;
					case sharingStarted:
						{
							String uid = ((TextRoomMessage)m).getText();
							Client c = cm.get(uid);
							if (c == null) {
								log.error("Not existing user has started sharing {} !!!!", uid);
								return;
							}
							sharingUser = uid;
							cm.update(c.set(Client.Activity.share));
							menu.update(handler);
						}
						break;
					case rightUpdated:
						{
							String uid = ((TextRoomMessage)m).getText();
							Client c = cm.get(uid);
							if (c == null) {
								log.error("Not existing user in rightUpdated {} !!!!", uid);
								return;
							}
							boolean self = _c.getUid().equals(c.getUid());
							handler.appendJavaScript(String.format("VideoManager.update(%s);"
									, c.streamJson(_c.getSid(), self, getBean(StreamClientManager.class)).toString(new NullStringer())
									));
							sidebar.update(handler);
							menu.update(handler);
							wb.update(handler);
							updateInterviewRecordingButtons(handler);
						}
						break;
					case newStream:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						String uid = obj.getString("uid");
						Client c = cm.get(uid);
						if (c == null) {
							// screen client, ext video stream
							c = cm.getBySid(obj.getString("sid"));
						}
						if (c == null) {
							log.error("Not existing user in newStream {} !!!!", uid);
							return;
						}
						boolean self = _c.getSid().equals(c.getSid());
						StreamClientManager mgr = getBean(StreamClientManager.class);
						if (!self || Client.Type.room != mgr.get(uid).getType()) { // stream from others or self external video
							JSONObject jo = videoJson(c, false, _c.getSid(), mgr, uid);
							handler.appendJavaScript(String.format("VideoManager.play(%s);", jo));
						}
						if (self) {
							cm.update(c.addStream(uid));
						}
						updateInterviewRecordingButtons(handler);
					}
						break;
					case closeStream:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						String uid = obj.getString("uid");
						Client c = cm.getBySid(obj.getString("sid"));
						if (c != null) {
							//c == null means client exits the room
							if (_c.getUid().equals(c.getUid())) {
								cm.update(c.removeStream(uid));
							}
						}
						handler.appendJavaScript(String.format("VideoManager.close('%s');", uid));
						updateInterviewRecordingButtons(handler);
					}
						break;
					case roomEnter:
						sidebar.update(handler);
						menu.update(handler);
						sidebar.addActivity(new Activity(m, Activity.Type.roomEnter), handler);
						break;
					case roomExit:
						sidebar.update(handler);
						sidebar.addActivity(new Activity(m, Activity.Type.roomExit), handler);
						break;
					case roomClosed:
						handler.add(room.setVisible(false));
						roomClosed.open(handler);
						break;
					case requestRightModerator:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightModerator), handler);
						break;
					case requestRightPresenter:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightPresenter), handler);
						break;
					case requestRightWb:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightWb), handler);
						break;
					case requestRightShare:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightShare), handler);
						break;
					case requestRightRemote:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightRemote), handler);
						break;
					case requestRightA:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightA), handler);
						break;
					case requestRightAv:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightAv), handler);
						break;
					case requestRightExclusive:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.reqRightExclusive), handler);
						break;
					case activityRemove:
						sidebar.removeActivity(((TextRoomMessage)m).getText(), handler);
						break;
					case haveQuestion:
						if (_c.hasRight(Room.Right.moderator) || getUserId().equals(m.getUserId())) {
							sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.haveQuestion), handler);
						}
						break;
					case kick:
						{
							String uid = ((TextRoomMessage)m).getText();
							if (_c.getUid().equals(uid)) {
								handler.add(room.setVisible(false));
								getMainPanel().getChat().toggle(handler, false);
								clientKicked.open(handler);
								cm.exitRoom(_c);
							}
						}
						break;
					case mute:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						Client c = cm.getBySid(obj.getString("sid"));
						if (c == null) {
							log.error("Not existing user in mute {} !!!!", obj);
							return;
						}
						if (!_c.getUid().equals(c.getUid())) {
							handler.appendJavaScript(String.format("if (typeof(VideoManager) !== 'undefined') {VideoManager.mute('%s', %s);}", obj.getString("uid"), obj.getBoolean("mute")));
						}
					}
						break;
					case exclusive:
					{
						String uid = ((TextRoomMessage)m).getText();
						Client c = cm.get(uid);
						if (c == null) {
							// no luck
							return;
						}
						handler.appendJavaScript(String.format("if (typeof(VideoManager) !== 'undefined') {VideoManager.exclusive('%s');}", uid));
					}
						break;
					case quickPollUpdated:
					{
						menu.update(handler);
						handler.appendJavaScript(getQuickPollJs());
					}
						break;
				}
			}
		}
		super.onEvent(event);
	}

	private String getQuickPollJs() {
		return String.format("Room.quickPoll(%s);", getBean(QuickPollManager.class).toJson(r.getId()));
	}

	private void updateInterviewRecordingButtons(IPartialPageRequestHandler handler) {
		Client _c = getClient();
		if (interview && _c.hasRight(Right.moderator)) {
			if (recordingUser == null) {
				boolean hasStreams = false;
				for (Client cl : getBean(ClientManager.class).listByRoom(r.getId())) {
					if (!cl.getStreams().isEmpty()) {
						hasStreams = true;
						break;
					}
				}
				handler.appendJavaScript(String.format("if (typeof(WbArea) === 'object') {WbArea.setRecStarted(false);WbArea.setRecEnabled(%s);}", hasStreams));
			} else {
				handler.appendJavaScript("if (typeof(WbArea) === 'object') {WbArea.setRecStarted(true);}");
			}
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (room.isVisible()) {
			//We are setting initial rights here
			Client c = getClient();
			ClientManager cm = getBean(ClientManager.class);
			final int count = cm.addToRoom(c.setRoom(getRoom()));
			SOAPLogin soap = WebSession.get().getSoapLogin();
			if (soap != null && soap.isModerator()) {
				c.allow(Right.superModerator);
				cm.update(c);
			} else {
				Set<Right> rr = AuthLevelUtil.getRoomRight(c.getUser(), r, r.isAppointment() ? getBean(AppointmentDao.class).getByRoom(r.getId()) : null, count);
				if (!rr.isEmpty()) {
					c.allow(rr);
					cm.update(c);
					log.info("Setting rights for client:: {} -> {}", rr, cm.get(c.getUid()).hasRight(Right.moderator));
				}
			}
		}
	}

	public static boolean isModerator(long userId, long roomId) {
		return hasRight(userId, roomId, Right.moderator);
	}

	public static boolean hasRight(long userId, long roomId, Right r) {
		for (Client c : getBean(ClientManager.class).listByRoom(roomId)) {
			if (c.getUserId().equals(userId) && c.hasRight(r)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		getBasePage().getHeader().setVisible(false);
		getMainPanel().getTopControls().setVisible(false);
		Component loader = ((BasePage)getPage()).getLoader().setVisible(false);
		if (r.isHidden(RoomElement.Chat) || !isVisible()) {
			getMainPanel().getChat().toggle(handler, false);
		}
		if (handler != null) {
			handler.add(loader, getBasePage().getHeader(), getMainPanel().getTopControls());
			if (isVisible()) {
				handler.appendJavaScript("Room.load();");
			}
		}
		return this;
	}

	public void show(IPartialPageRequestHandler handler) {
		if (!r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.add(this.setVisible(true));
		handler.appendJavaScript("Room.load();");
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		if (eventDetail instanceof EventDetailDialog) {
			((EventDetailDialog)eventDetail).close(handler, null);
		}
		handler.add(getBasePage().getHeader().setVisible(true), getMainPanel().getTopControls().setVisible(true));
		if (r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.appendJavaScript("if (typeof(Room) !== 'undefined') { Room.unload(); }");
		getBean(ClientManager.class).exitRoom(getClient());
		getMainPanel().getChat().roomExit(r, handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(interview ? INTERVIEWWB_JS_REFERENCE : WB_JS_REFERENCE)));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RoomPanel.class, "room.js"))));
		if (room.isVisible()) {
			response.render(OnDomReadyHeaderItem.forScript(roomEnter.getCallbackScript()));
		}
	}

	public void requestRight(Right right, IPartialPageRequestHandler handler) {
		RoomMessage.Type reqType = null;
		ClientManager cm = getBean(ClientManager.class);
		List<Client> mods = cm.listByRoom(r.getId(), c -> c.hasRight(Room.Right.moderator));
		if (mods.isEmpty()) {
			if (r.isModerated()) {
				//dialog
				waitForModerator.open(handler);
				return;
			} else {
				// we found no-one we can ask, allow right
				broadcast(getBean(ClientManager.class).update(getClient().allow(right)));
			}
		}
		// ask
		switch (right) {
			case moderator:
				reqType = Type.requestRightModerator;
				break;
			case presenter:
				reqType = Type.requestRightPresenter;
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
			WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getClient(), reqType, getClient().getUid()));
		}
	}

	public void allowRight(Client client, Right... rights) {
		client.allow(rights);
		getBean(ClientManager.class).update(client);
		broadcast(client);
	}

	public void denyRight(Client client, Right... rights) {
		for (Right right : rights) {
			client.deny(right);
		}
		if (client.hasActivity(Client.Activity.broadcastA) && !client.hasRight(Right.audio)) {
			client.remove(Client.Activity.broadcastA);
		}
		if (client.hasActivity(Client.Activity.broadcastV) && !client.hasRight(Right.video)) {
			client.remove(Client.Activity.broadcastV);
		}
		getBean(ClientManager.class).update(client);
		broadcast(client);
	}

	public void broadcast(Client client) {
		RoomBroadcaster.sendUpdatedClient(client);
		WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getClient(), RoomMessage.Type.rightUpdated, client.getUid()));
	}

	@Override
	protected void process(IPartialPageRequestHandler handler, JSONObject o) {
		if (room.isVisible() && "room".equals(o.optString("area"))) {
			final String type = o.optString("type");
			if ("room".equals(type)) {
				//TODO actions
			}
			if ("mic".equals(type) && "activity".equals(o.optString("id"))) {
				WebSocketHelper.sendRoom(r.getId(), new JSONObject()
						.put("type", "mic")
						.put("id", "activity")
						.put("uid", getUid())
						.put("active", o.getBoolean("active")));
			}
		}
	}

	public Room getRoom() {
		return r;
	}

	public Client getClient() {
		return getMainPanel().getClient();
	}

	public String getUid() {
		return getMainPanel().getUid();
	}

	public boolean screenShareAllowed() {
		return !interview && !r.isHidden(RoomElement.ScreenSharing)
				&& r.isAllowRecording() && getClient().hasRight(Right.share)
				&& sharingUser == null;
	}

	public RoomSidebar getSidebar() {
		return sidebar;
	}

	public AbstractWbPanel getWb() {
		return wb;
	}

	public String getSharingUser() {
		return sharingUser;
	}

	public String getRecordingUser() {
		return recordingUser;
	}

	public String getPublishingUser() {
		return null;
	}

	public boolean isInterview() {
		return interview;
	}
}
