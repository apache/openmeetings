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
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addUserToRoom;
import static org.apache.openmeetings.web.app.Application.exitRoom;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getClientBySid;
import static org.apache.openmeetings.web.app.Application.getOnlineClient;
import static org.apache.openmeetings.web.app.Application.getRoomClients;
import static org.apache.openmeetings.web.app.Application.update;
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
import java.util.UUID;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
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
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.activities.ActivitiesPanel;
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
import org.apache.wicket.extensions.ajax.AjaxDownload;
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
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, webAppRootKey);
	private static final String ACCESS_DENIED_ID = "access-denied";
	private static final String EVENT_DETAILS_ID = "event-details";
	public enum Action {
		kick
		, exclusive
		, mute
	}
	private final Room r;
	private final boolean isInterview;
	private final WebMarkupContainer room = new WebMarkupContainer("roomContainer");
	private final AbstractDefaultAjaxBehavior roomEnter = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			target.appendJavaScript("setRoomSizes();");
			ExtendedClientProperties cp = WebSession.get().getExtendedProperties();
			getBean(ConferenceLogDao.class).add(
					ConferenceLog.Type.roomEnter
					, getUserId(), "0", r.getId()
					, cp.getRemoteAddress()
					, "" + r.getId());
			Client _c = getClient();
			JSONObject options = VideoSettings.getInitJson(cp, r.getId(), _c.getSid())
					.put("uid", _c.getUid())
					.put("interview", isInterview)
					.put("showMicStatus", !r.getHiddenElements().contains(RoomElement.MicrophoneStatus));
			target.appendJavaScript(String.format("VideoManager.init(%s);", options));
			WebSocketHelper.sendRoom(new RoomMessage(r.getId(), getUserId(), RoomMessage.Type.roomEnter));
			// play video from other participants
			initVideos(target);
			getMainPanel().getChat().roomEnter(r, target);
			if (r.isFilesOpened()) {
				sidebar.setFilesActive(target);
			}
			if (Room.Type.presentation != r.getType()) {
				List<Client> mods = Application.getRoomClients(r.getId(), c -> c.hasRight(Room.Right.moderator));
				if (mods.isEmpty()) {
					waitApplyModeration.open(target);
				}
			}
			wb.update(target);
		}
	};
	private RedirectMessageDialog roomClosed;
	private MessageDialog clientKicked, waitForModerator, waitApplyModeration;

	private RoomMenuPanel menu;
	private RoomSidebar sidebar;
	private ActivitiesPanel activities;
	private final AbstractWbPanel wb;
	private String sharingUser = null;
	private String recordingUser = null;
	private byte[] pdfWb;
	private final AjaxDownload download = new AjaxDownload(new ResourceStreamResource() {
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

	public void startDownload(AjaxRequestTarget target, byte[] bb) {
		pdfWb = bb;
		download.initiate(target);
	}

	public RoomPanel(String id, Room r) {
		super(id);
		this.r = r;
		this.isInterview = Room.Type.interview == r.getType();
		this.wb = isInterview ? new InterviewWbPanel("whiteboard", this) : new WbPanel("whiteboard", this);
	}

	private void initVideos(AjaxRequestTarget target) {
		StringBuilder sb = new StringBuilder();
		boolean hasStreams = false;
		Client _c = getClient();
		for (Client c: getRoomClients(getRoom().getId()) ) {
			boolean self = _c.getUid().equals(c.getUid());
			for (String uid : c.getStreams()) {
				JSONObject jo = videoJson(c, self, c.getSid(), getBean(ISessionManager.class), uid);
				sb.append(String.format("VideoManager.play(%s);", jo));
				hasStreams = true;
			}
		}
		if (isInterview && recordingUser == null && hasStreams && _c.hasRight(Right.moderator)) {
			sb.append("WbArea.setRecStartEnabled(true);");
		}
		if (!Strings.isEmpty(sb)) {
			target.appendJavaScript(sb);
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		//let's refresh user in client
		update(getClient().updateUser(getBean(UserDao.class)));
		Component accessDenied = new WebMarkupContainer(ACCESS_DENIED_ID).setVisible(false);
		Component eventDetail = new WebMarkupContainer(EVENT_DETAILS_ID).setVisible(false);

		room.add(menu = new RoomMenuPanel("menu", this));
		room.add(AttributeModifier.append("data-room-id", r.getId()));
		if (isInterview) {
			room.add(new WebMarkupContainer("wb-area").add(wb));
		} else {
			Droppable<FileItem> wbArea = new Droppable<FileItem>("wb-area") {
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
					if (wb.isVisible() && o instanceof FileItem) {
						FileItem f = (FileItem)o;
						if (sidebar.getFilesPanel().isSelected(f)) {
							for (Entry<String, FileItem> e : sidebar.getFilesPanel().getSelected().entrySet()) {
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
							deniedMessage = getString("error.hash.period") + String.format(" %s - %s", getDateFormat().format(a.getStart()), getDateFormat().format(a.getEnd()));
						}
					}
				}
			} else {
				allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(getUserId()));
				log.debug("public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
				if (!allowed) {
					User u = getClient().getUser();
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
		if (event.getPayload() instanceof WebSocketPushPayload) {
			WebSocketPushPayload wsEvent = (WebSocketPushPayload) event.getPayload();
			if (wsEvent.getMessage() instanceof RoomMessage) {
				RoomMessage m = (RoomMessage)wsEvent.getMessage();
				IPartialPageRequestHandler handler = wsEvent.getHandler();
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
							Client c = getClientBySid(uid);
							if (c == null) {
								log.error("Not existing/BAD user has stopped recording {} != {} !!!!", uid);
								return;
							}
							recordingUser = null;
							update(c.remove(Client.Activity.record));
							menu.update(handler);
							updateInterviewRecordingButtons(handler);
						}
						break;
					case recordingStarted:
						{
							JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
							String uid = obj.getString("uid");
							String sid = obj.getString("sid");
							Client c = getClientBySid(sid);
							if (c == null) {
								log.error("Not existing user has started recording {} !!!!", sid);
								return;
							}
							recordingUser = sid;
							update(c.addStream(uid).set(Client.Activity.record));
							menu.update(handler);
							updateInterviewRecordingButtons(handler);
						}
						break;
					case sharingStoped:
						{
							JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
							String uid = obj.getString("uid");
							Client c = getClientBySid(obj.getString("sid"));
							if (c == null) {
								log.error("Not existing user has started sharing {} !!!!", obj);
								return;
							}
							handler.appendJavaScript(String.format("VideoManager.close('%s', true);", uid));
							sharingUser = null;
							update(c.removeStream(uid).remove(Client.Activity.share));
							menu.update(handler);
						}
						break;
					case sharingStarted:
						{
							String uid = ((TextRoomMessage)m).getText();
							Client c = getOnlineClient(uid);
							if (c == null) {
								log.error("Not existing user has started sharing {} !!!!", uid);
								return;
							}
							sharingUser = uid;
							update(c.set(Client.Activity.share));
							menu.update(handler);
						}
						break;
					case rightUpdated:
						{
							String uid = ((TextRoomMessage)m).getText();
							Client c = getOnlineClient(uid);
							if (c == null) {
								log.error("Not existing user in rightUpdated {} !!!!", uid);
								return;
							}
							Client _c = getClient();
							boolean self = _c.getUid().equals(c.getUid());
							handler.appendJavaScript(String.format("VideoManager.update(%s);"
									, c.streamJson(_c.getSid(), self, getBean(ISessionManager.class)).toString()
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
						Client c = getOnlineClient(uid);
						if (c == null) {
							// screen client, ext video stream
							c = getClientBySid(obj.getString("sid"));
						}
						if (c == null) {
							log.error("Not existing user in newStream {} !!!!", uid);
							return;
						}
						Client _c = getClient();
						boolean self = _c.getSid().equals(c.getSid());
						if (!self) {
							JSONObject jo = videoJson(c, self, _c.getSid(), getBean(ISessionManager.class), uid);
							handler.appendJavaScript(String.format("VideoManager.play(%s);", jo));
						}
						if (_c.getSid().equals(c.getSid())) {
							update(c.addStream(uid));
						}
						updateInterviewRecordingButtons(handler);
					}
						break;
					case closeStream:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						String uid = obj.getString("uid");
						Client c = getClientBySid(obj.getString("sid"));
						if (c == null) {
							log.error("Not existing user in closeStream {} !!!!", obj);
							return;
						}
						Client _c = getClient();
						if (_c.getUid().equals(c.getUid())) {
							update(c.removeStream(uid));
						}
						handler.appendJavaScript(String.format("VideoManager.close('%s');", uid));
						updateInterviewRecordingButtons(handler);
					}
						break;
					case roomEnter:
						sidebar.update(handler);
						menu.update(handler);
						// TODO should this be fixed?
						//activities.addActivity(new Activity(m, Activity.Type.roomEnter), handler);
						break;
					case roomExit:
						//TODO check user/remove tab
						sidebar.update(handler);
						activities.add(new Activity(m, Activity.Type.roomExit), handler);
						break;
					case roomClosed:
						handler.add(room.setVisible(false));
						roomClosed.open(handler);
						break;
					case requestRightModerator:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightModerator), handler);
						break;
					case requestRightPresenter:
						activities.add(new Activity((TextRoomMessage)m, Activity.Type.reqRightPresenter), handler);
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
						activities.remove(((TextRoomMessage)m).getText(), handler);
						break;
					case haveQuestion:
						if (getClient().hasRight(Room.Right.moderator) || getUserId().equals(m.getUserId())) {
							activities.add(new Activity((TextRoomMessage)m, Activity.Type.haveQuestion), handler);
						}
						break;
					case kick:
						{
							//FIXME TODO add line to activities about user kick
							//activities.add(new Activity(m, Activity.Type.roomExit), handler);
							String uid = ((TextRoomMessage)m).getText();
							if (getClient().getUid().equals(uid)) {
								handler.add(room.setVisible(false));
								getMainPanel().getChat().toggle(handler, false);
								clientKicked.open(handler);
								exitRoom(getClient());
							}
						}
						break;
					case audioActivity:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						Client c = getClientBySid(obj.getString("sid"));
						if (c == null) {
							log.error("Not existing user in audioActivity {} !!!!", obj);
							return;
						}
						if (!getClient().getUid().equals(c.getUid())) {
							handler.appendJavaScript(String.format("if (typeof VideoManager !== 'undefined') {VideoManager.micActivity('%s', %s);}", c.getUid(), obj.getBoolean("active")));
						}
					}
						break;
					case mute:
					{
						JSONObject obj = new JSONObject(((TextRoomMessage)m).getText());
						Client c = getClientBySid(obj.getString("sid"));
						if (c == null) {
							log.error("Not existing user in mute {} !!!!", obj);
							return;
						}
						if (!getClient().getUid().equals(c.getUid())) {
							handler.appendJavaScript(String.format("if (typeof VideoManager !== 'undefined') {VideoManager.mute('%s', %s);}", obj.getString("uid"), obj.getBoolean("mute")));
						}
					}
						break;
				}
			}
		}
		super.onEvent(event);
	}

	private void updateInterviewRecordingButtons(IPartialPageRequestHandler handler) {
		Client _c = getClient();
		if (isInterview && _c.hasRight(Right.moderator)) {
			if (recordingUser == null) {
				boolean hasStreams = false;
				for (Client cl : getRoomClients(r.getId())) {
					if (!cl.getStreams().isEmpty()) {
						hasStreams = true;
						break;
					}
				}
				handler.appendJavaScript(String.format("WbArea.setRecStopEnabled(false);WbArea.setRecStartEnabled(%s);", hasStreams));
			} else {
				handler.appendJavaScript("WbArea.setRecStartEnabled(false);WbArea.setRecStopEnabled(true);");
			}
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (room.isVisible()) {
			//We are setting initial rights here
			Client c = getClient();
			addUserToRoom(c.setRoom(getRoom()));
			SOAPLogin soap = WebSession.get().getSoapLogin();
			if (soap != null && soap.isModerator()) {
				c.allow(Right.superModerator);
				update(c);
			} else {
				//FIXME TODO !!! c.getUser != getUserId
				Set<Right> rr = AuthLevelUtil.getRoomRight(c.getUser(), r, r.isAppointment() ? getBean(AppointmentDao.class).getByRoom(r.getId()) : null, getRoomClients(r.getId()).size());
				if (!rr.isEmpty()) {
					c.allow(rr);
					update(c);
				}
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

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		getBasePage().getHeader().setVisible(false);
		getMainPanel().getTopControls().setVisible(false);
		if (r.isHidden(RoomElement.Chat) || !isVisible()) {
			getMainPanel().getChat().toggle(handler, false);
		}
		if (handler != null) {
			handler.add(getBasePage().getHeader(), getMainPanel().getTopControls());
			if (isVisible()) {
				handler.appendJavaScript("roomLoad();");
			}
		}
		return this;
	}

	public void show(IPartialPageRequestHandler handler) {
		if (!r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.add(this.setVisible(true));
		handler.appendJavaScript("roomLoad();");
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		handler.add(getBasePage().getHeader().setVisible(true), getMainPanel().getTopControls().setVisible(true));
		if (r.isHidden(RoomElement.Chat)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.appendJavaScript("if (typeof roomUnload == 'function') { roomUnload(); }");
		Application.exitRoom(getClient());
		getMainPanel().getChat().roomExit(r, handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RoomPanel.class, "jquery.dialogextend.js"))));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RoomPanel.class, "room.js"))));
		if (isInterview) {
			response.render(JavaScriptHeaderItem.forReference(INTERVIEWWB_JS_REFERENCE));
		} else {
			response.render(JavaScriptHeaderItem.forReference(WB_JS_REFERENCE));
		}
		WebSession ws = WebSession.get();
		if (!Strings.isEmpty(r.getRedirectURL()) && (ws.getSoapLogin() != null || ws.getInvitation() != null)) {
			response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(
					String.format("function roomReload(event, ui) {window.location.href='%s';}", r.getRedirectURL())
					, String.format("room-reload-%s", UUID.randomUUID()))));
		}
		if (room.isVisible()) {
			response.render(OnDomReadyHeaderItem.forScript(roomEnter.getCallbackScript()));
		}
	}

	public void requestRight(Right right, IPartialPageRequestHandler handler) {
		RoomMessage.Type reqType = null;
		List<Client> mods = Application.getRoomClients(r.getId(), c -> c.hasRight(Room.Right.moderator));
		if (mods.size() == 0) {
			if (r.isModerated()) {
				//dialog
				waitForModerator.open(handler);
				return;
			} else {
				// we found no-one we can ask, allow right
				broadcast(update(getClient().allow(right)));
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
			WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getUserId(), reqType, getClient().getUid()));
		}
	}

	public void allowRight(Client client, Right... rights) {
		client.allow(rights);
		update(client);
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
		update(client);
		broadcast(client);
	}

	public void kickUser(Client client) {
		WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoom().getId(), client.getUserId(), Type.kick, client.getUid()));
	}

	public void broadcast(Client client) {
		RoomBroadcaster.sendUpdatedClient(client);
		WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getUserId(), RoomMessage.Type.rightUpdated, client.getUid()));
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
		Room r = getRoom();
		return !isInterview && !r.isHidden(RoomElement.ScreenSharing)
				&& r.isAllowRecording() && getClient().hasRight(Right.share)
				&& sharingUser == null;
	}

	public RoomSidebar getSidebar() {
		return sidebar;
	}

	public AbstractWbPanel getWb() {
		return wb;
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
		return null;
	}
}
