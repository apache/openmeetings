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

import static de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.BUTTON_MARKUP_ID;
import static java.time.Duration.ZERO;
import static org.apache.openmeetings.core.util.ChatWebSocketHelper.ID_USER_PREFIX;
import static org.apache.openmeetings.db.entity.calendar.Appointment.allowedStart;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.VideoSettings.VIDEO_SETTINGS_JS;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.RoomMessage.Type;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.QuickPollManager;
import org.apache.openmeetings.web.app.TimerService;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.activities.Activity;
import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.openmeetings.web.room.sidebar.RoomSidebar;
import org.apache.openmeetings.web.room.wb.AbstractWbPanel;
import org.apache.openmeetings.web.room.wb.InterviewWbPanel;
import org.apache.openmeetings.web.room.wb.WbAction;
import org.apache.openmeetings.web.room.wb.WbPanel;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.web.util.TouchPunchResourceReference;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.ws.api.BaseWebSocketBehavior;
import org.apache.wicket.protocol.ws.api.event.WebSocketPushPayload;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceStreamResource;

import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;
import org.apache.openmeetings.mediaserver.KurentoHandler;
import org.apache.openmeetings.mediaserver.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import org.wicketstuff.jquery.core.JQueryBehavior;
import org.wicketstuff.jquery.core.Options;
import org.wicketstuff.jquery.ui.interaction.droppable.Droppable;
import org.wicketstuff.jquery.ui.settings.JQueryUILibrarySettings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Alert;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import jakarta.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation("ROOM")
public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RoomPanel.class);
	public static final String PARAM_ACTION = "action";
	private static final String ACCESS_DENIED_ID = "access-denied";
	private static final String EVENT_DETAILS_ID = "event-details";
	public enum Action {
		KICK("kick")
		, MUTE_OTHERS("muteOthers")
		, MUTE("mute")
		, TOGGLE_RIGHT("toggleRight");

		private final String jsName;

		private Action(String jsName) {
			this.jsName = jsName;
		}

		public static Action of(String jsName) {
			return Stream.of(Action.values())
					.filter(a -> a.jsName.equals(jsName))
					.findAny()
					.orElse(null);
		}
	}
	private final Room r;
	private final WebMarkupContainer room = new WebMarkupContainer("roomContainer");
	private final AbstractDefaultAjaxBehavior roomEnter = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			log.debug("RoomPanel::roomEnter");
			WebSession ws = WebSession.get();
			Client c = getClient();
			JSONObject options = VideoSettings.getInitJson(c.getSid())
					.put("uid", c.getUid())
					.put("userId", c.getUserId())
					.put("rights", c.toJson(true).getJSONArray("rights"))
					.put("interview", r.isInterview())
					.put("audioOnly", r.isAudioOnly())
					.put("allowRecording", r.isAllowRecording())
					.put("questions", r.isAllowUserQuestions())
					.put("showMicStatus", !r.getHiddenElements().contains(RoomElement.MICROPHONE_STATUS));
			if (!Strings.isEmpty(r.getRedirectURL()) && (ws.getSoapLogin() != null || ws.getInvitation() != null)) {
				options.put("reloadUrl", r.getRedirectURL());
			}
			StringBuilder sb = new StringBuilder("Room.init(").append(options.toString(new NullStringer())).append(");")
					.append(wb.getInitScript())
					.append(getQuickPollJs());
			sb.append(sendClientsOnInit());
			target.appendJavaScript(sb);

			WebSocketHelper.sendRoom(new TextRoomMessage(r.getId(), c, RoomMessage.Type.ROOM_ENTER, c.getUid()));
			// play video from other participants
			initVideos(target);
			getMainPanel().getChat().roomEnter(r, target);
			if (r.isFilesOpened()) {
				sidebar.setFilesActive(target);
			}
			if (Room.Type.PRESENTATION != r.getType()) {
				boolean modsEmpty = noModerators();
				log.debug("RoomPanel::roomEnter, mods IS EMPTY ? {}, is MOD ? {}", modsEmpty, c.hasRight(Room.Right.MODERATOR));
				if (modsEmpty) {
					showIdeaAlert(target, getString(r.isModerated() ? "641" : "498"));
				}
			}
			if (r.isWaitRecording()) {
				showIdeaAlert(target, getString("1315"));
			}
			wb.update(target);
			jsInited = true;
		}

		private CharSequence sendClientsOnInit() {
			Client c = getClient();
			StringBuilder res = new StringBuilder();
			if (c.hasRight(Room.Right.MODERATOR) || !r.isHidden(RoomElement.USER_COUNT)) {
				res.append(createAddClientJs(c));
			}
			return res;
		}

		private void initVideos(AjaxRequestTarget target) {
			StringBuilder sb = new StringBuilder();
			JSONArray streams = new JSONArray();
			cm.streamByRoom(getRoom().getId())
				.map(Client::getStreams)
				.flatMap(List::stream)
				.forEach(sd -> streams.put(sd.toJson()));
			if (streams.length() > 0) {
				sb.append("VideoManager.play(").append(streams).append(", ").append(kHandler.getTurnServers(getClient())).append(");");
			}
			if (r.isInterview() && streamProcessor.recordingAllowed(getClient())) {
				sb.append("WbArea.setRecEnabled(true);");
			}
			if (!Strings.isEmpty(sb)) {
				target.appendJavaScript(sb);
			}
		}
	};
	private RedirectMessageDialog roomClosed;
	private Modal<String> clientKicked;
	private Alert waitModerator;

	private RoomMenuPanel menu;
	private RoomSidebar sidebar;
	private final AbstractWbPanel wb;
	private String fuid;
	private String ftype;
	private final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new ResourceStreamResource() {
		private static final long serialVersionUID = 1L;

		{
			setCacheDuration(ZERO);
		}

		@Override
		protected IResourceStream getResourceStream(Attributes attributes) {
			setFileName(EXTENSION_PDF.equals(ftype) ? "whiteboard.pdf" : "slide.png");
			return new FileResourceStream(Paths.get(System.getProperty("java.io.tmpdir"), fuid).toFile());
		}
	}) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onDownloadCompleted(AjaxRequestTarget target) {
			super.onDownloadCompleted(target);
			try {
				Files.deleteIfExists(Paths.get(System.getProperty("java.io.tmpdir"), fuid));
			} catch (Exception e) {
				log.error("unexcepted error while clean-up", e);
			}
			fuid = null;
			ftype = null;
		}
	};
	Component eventDetail = new WebMarkupContainer(EVENT_DETAILS_ID).setVisible(false);
	private boolean avInited = false;
	private boolean jsInited = false;

	@Inject
	private ClientManager cm;
	@Inject
	private UserDao userDao;
	@Inject
	private AppointmentDao apptDao;
	@Inject
	private QuickPollManager qpollManager;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private StreamProcessor streamProcessor;
	@Inject
	private TimerService timerService;
	@Inject
	private FileItemDao fileDao;

	public RoomPanel(String id, Room r) {
		super(id);
		this.r = r;
		this.wb = r.isInterview() ? new InterviewWbPanel("whiteboard", this) : new WbPanel("whiteboard", this);
	}

	public void startDownload(IPartialPageRequestHandler handler, String type, String fuid) {
		this.fuid = fuid;
		ftype = type;
		download.initiate(handler);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		//let's refresh user in client
		Client c = getClient().updateUser(userDao);
		Component accessDenied = new WebMarkupContainer(ACCESS_DENIED_ID).setVisible(false);

		room.setOutputMarkupPlaceholderTag(true);
		room.add(menu = new RoomMenuPanel("menu", this));
		room.add(AttributeModifier.append("data-room-id", r.getId()));
		if (r.isInterview()) {
			room.add(new WebMarkupContainer("wb-area").add(wb));
		} else {
			Droppable<BaseFileItem> wbArea = new Droppable<>("wb-area") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onConfigure(JQueryBehavior behavior) {
					super.onConfigure(behavior);
					behavior.setOption("hoverClass", Options.asString("droppable-hover"));
					behavior.setOption("accept", Options.asString(".recorditem, .fileitem, .readonlyitem"));
				}

				@Override
				public void onDrop(AjaxRequestTarget target, Component component) {
					Object o = component.getDefaultModelObject();
					if (wb.isVisible() && o instanceof BaseFileItem f) {
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
		} else if (cm.streamByRoom(r.getId()).count() >= r.getCapacity()) {
			accessDenied = new ExpiredMessageDialog(ACCESS_DENIED_ID, getString("99"), menu);
			room.setVisible(false);
		} else if (r.getId().equals(WebSession.get().getRoomId())) {
			// secureHash/invitationHash, already checked
		} else {
			boolean allowed = Application.get().isRoomAllowedToUser(r, c.getUser());
			String deniedMessage = null;
			if (r.isAppointment()) {
				Appointment a = apptDao.getByRoom(r.getId());
				if (allowed) {
					Calendar cal = WebSession.getCalendar();
					if (a.isOwner(getUserId()) || cal.getTime().after(allowedStart(a.getStart())) && cal.getTime().before(a.getEnd())) {
						eventDetail = new EventDetailDialog(EVENT_DETAILS_ID, a);
					} else {
						allowed = false;
						deniedMessage = String.format("%s %s - %s", getString("error.hash.period"), getDateFormat().format(a.getStart()), getDateFormat().format(a.getEnd()));
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
		RepeatingView groupstyles = new RepeatingView("groupstyle");
		add(groupstyles.setVisible(room.isVisible() && !r.getGroups().isEmpty()));
		if (room.isVisible()) {
			add(new NicknameDialog("nickname", this));
			add(download);
			add(new BaseWebSocketBehavior("media"));
			for (RoomGroup rg : r.getGroups()) {
				WebMarkupContainer groupstyle = new WebMarkupContainer(groupstyles.newChildId());
				groupstyle.add(AttributeModifier.append("href"
						, (String)RequestCycle.get().urlFor(new GroupCustomCssResourceReference(), new PageParameters().add("id", rg.getGroup().getId()))
						));
				groupstyles.add(groupstyle);
			}
			//We are setting initial rights here
			final int count = cm.addToRoom(c.setRoom(getRoom()));
			SOAPLogin soap = WebSession.get().getSoapLogin();
			if (soap != null && soap.isModerator()) {
				c.allow(Right.SUPER_MODERATOR);
			} else {
				Set<Right> rr = AuthLevelUtil.getRoomRight(c.getUser(), r, r.isAppointment() ? apptDao.getByRoom(r.getId()) : null, count);
				if (!rr.isEmpty()) {
					c.allow(rr);
					log.info("Setting rights for client:: {} -> {}", rr, c.hasRight(Right.MODERATOR));
				}
			}
			if (r.isModerated() && r.isWaitModerator()
					&& !c.hasRight(Right.MODERATOR)
					&& noModerators())
			{
				room.setVisible(false);
				createWaitModerator(true);
				getMainPanel().getChat().toggle(null, false);
			}
			timerService.scheduleModCheck(r);
		} else {
			add(new WebMarkupContainer("nickname").setVisible(false));
		}
		cm.update(c);
		if (waitModerator == null) {
			createWaitModerator(false);
		}
		add(room, accessDenied, eventDetail, waitModerator);
		add(clientKicked = new TextContentModal("client-kicked", new ResourceModel("606")));
		clientKicked
			.header(new ResourceModel("797"))
			.setCloseOnEscapeKey(false)
			.setBackdrop(Backdrop.FALSE)
			.addButton(new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, Model.of(""), Buttons.Type.Outline_Primary, new ResourceModel("54")) {
				private static final long serialVersionUID = 1L;

				public void onClick(AjaxRequestTarget target) {
					clientKicked.close(target);
					menu.exit(target);
				}
			});
	}

	@Override
	public void onEvent(IEvent<?> event) {
		if (!jsInited) {
			return;
		}
		Client curClient = getClient();
		if (curClient != null && event.getPayload() instanceof WebSocketPushPayload) {
			WebSocketPushPayload wsEvent = (WebSocketPushPayload) event.getPayload();
			if (wsEvent.getMessage() instanceof RoomMessage m) {
				IPartialPageRequestHandler handler = wsEvent.getHandler();
				switch (m.getType()) {
					case POLL_CREATED:
						menu.updatePoll(handler, m.getUserId());
						break;
					case POLL_UPDATED:
						menu.updatePoll(handler, null);
						break;
					case RECORDING_TOGGLED:
						menu.update(handler);
						updateInterviewRecordingButtons(handler);
						break;
					case SHARING_TOGGLED:
						menu.update(handler);
						break;
					case RIGHT_UPDATED:
						onRightUpdated(curClient, (TextRoomMessage)m, handler);
						break;
					case ROOM_ENTER:
						onRoomEnter(curClient, (TextRoomMessage)m, handler);
						break;
					case ROOM_EXIT:
						onRoomExit((TextRoomMessage)m, handler);
						break;
					case ROOM_CLOSED:
						handler.add(room.setVisible(false));
						roomClosed.show(handler);
						break;
					case REQUEST_RIGHT_MODERATOR:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_MODERATOR), handler);
						break;
					case REQUEST_RIGHT_PRESENTER:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_PRESENTER), handler);
						break;
					case REQUEST_RIGHT_WB:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_WB), handler);
						break;
					case REQUEST_RIGHT_SHARE:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_SHARE), handler);
						break;
					case REQUEST_RIGHT_REMOTE:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_REMOTE), handler);
						break;
					case REQUEST_RIGHT_A:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_A), handler);
						break;
					case REQUEST_RIGHT_AV:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_AV), handler);
						break;
					case REQUEST_RIGHT_MUTE_OTHERS:
						sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_MUTE_OTHERS), handler);
						break;
					case ACTIVITY_REMOVE:
						sidebar.removeActivity(((TextRoomMessage)m).getText(), handler);
						break;
					case HAVE_QUESTION:
						if (curClient.hasRight(Room.Right.MODERATOR) || getUserId().equals(m.getUserId())) {
							sidebar.addActivity(new Activity((TextRoomMessage)m, Activity.Type.REQ_RIGHT_HAVE_QUESTION), handler);
						}
						break;
					case KICK:
						onKick(curClient, (TextRoomMessage)m, handler);
						break;
					case MUTE:
						onMute(curClient, (TextRoomMessage)m, handler);
						break;
					case MUTE_OTHERS:
						onMuteOthers((TextRoomMessage)m, handler);
						break;
					case QUICK_POLL_UPDATED:
						menu.update(handler);
						handler.appendJavaScript(getQuickPollJs());
						break;
					case KURENTO_STATUS:
						menu.update(handler);
						break;
					case WB_RELOAD:
						if (Room.Type.INTERVIEW != r.getType()) {
							wb.reloadWb(handler);
						}
						break;
					case MODERATOR_IN_ROOM:
						if (!r.isModerated() || !r.isWaitModerator()) {
							log.warn("Something weird: `moderatorInRoom` in wrong room {}", r);
						} else if (!curClient.hasRight(Room.Right.MODERATOR)) {
							boolean moderInRoom = Boolean.TRUE.equals(Boolean.valueOf(((TextRoomMessage)m).getText()));
							log.warn("!! moderatorInRoom: {}", moderInRoom);
							if (room.isVisible() != moderInRoom) {
								handler.add(room.setVisible(moderInRoom));
								getMainPanel().getChat().toggle(handler, moderInRoom && !r.isHidden(RoomElement.CHAT));
								if (room.isVisible()) {
									handler.appendJavaScript(roomEnter.getCallbackScript());
									handler.add(waitModerator.setVisible(false));
								} else {
									handler.add(waitModerator.setVisible(true));
								}
							}
						}
						break;
					case WB_PUT_FILE:
						onWbPutFile((TextRoomMessage)m);
						break;
					case FILE_TREE_UPDATE:
						onFileTreeUpdate(handler);
						break;
				}
			}
		}
		super.onEvent(event);
	}

	private void onRightUpdated(Client curClient, TextRoomMessage m, IPartialPageRequestHandler handler) {
		String uid = m.getText();
		Client c = cm.get(uid);
		if (c == null) {
			log.error("Not existing user in rightUpdated {} !!!!", uid);
			return;
		}
		boolean self = curClient.getUid().equals(c.getUid());
		StringBuilder sb = new StringBuilder("Room.updateClient(")
				.append(c.toJson(self).toString(new NullStringer()))
				.append(");")
				.append(sendClientsOnUpdate());
		handler.appendJavaScript(sb);
		sidebar.update(handler);
		menu.update(handler);
		wb.update(handler);
		updateInterviewRecordingButtons(handler);
	}

	private void onRoomEnter(Client curClient, TextRoomMessage m, IPartialPageRequestHandler handler) {
		sidebar.update(handler);
		menu.update(handler);
		String uid = m.getText();
		Client c = cm.get(uid);
		if (c == null) {
			log.error("Not existing user in rightUpdated {} !!!!", uid);
			return;
		}
		boolean self = curClient.getUid().equals(c.getUid());
		if (self || curClient.hasRight(Room.Right.MODERATOR) || !r.isHidden(RoomElement.USER_COUNT)) {
			handler.appendJavaScript("Room.addClient(["
					+ c.toJson(self).toString(new NullStringer()) + "]);");
		}
		sidebar.addActivity(new Activity(m, Activity.Type.ROOM_ENTER), handler);
	}

	private void onRoomExit(TextRoomMessage m, IPartialPageRequestHandler handler) {
		String uid = m.getText();
		sidebar.update(handler);
		sidebar.addActivity(new Activity(m, Activity.Type.ROOM_EXIT), handler);
		handler.appendJavaScript("Room.removeClient('" + uid + "'); Chat.removeTab('" + ID_USER_PREFIX + m.getUserId() + "');");
	}

	private void onKick(Client curClient, TextRoomMessage m, IPartialPageRequestHandler handler) {
		String uid = m.getText();
		if (curClient.getUid().equals(uid)) {
			handler.add(room.setVisible(false));
			getMainPanel().getChat().toggle(handler, false);
			clientKicked.show(handler);
			cm.exitRoom(curClient);
		}
	}

	private void onMute(Client curClient, TextRoomMessage m, IPartialPageRequestHandler handler) {
		JSONObject obj = new JSONObject(m.getText());
		Client c = cm.getBySid(obj.getString("sid"));
		if (c == null) {
			log.error("Not existing user in mute {} !!!!", obj);
			return;
		}
		if (!curClient.getUid().equals(c.getUid())) {
			handler.appendJavaScript(String.format("if (typeof(VideoManager) !== 'undefined') {VideoManager.mute('%s', %s);}", obj.getString("uid"), obj.getBoolean("mute")));
		}
	}

	private void onMuteOthers(TextRoomMessage m, IPartialPageRequestHandler handler) {
		String uid = m.getText();
		Client c = cm.get(uid);
		if (c == null) {
			// no luck
			return;
		}
		handler.appendJavaScript(String.format("if (typeof(VideoManager) !== 'undefined') {VideoManager.muteOthers('%s');}", uid));
	}

	private void onWbPutFile(TextRoomMessage m) {
		JSONObject obj = new JSONObject(m.getText());
		getWb().sendFileToWb(fileDao.getAny(obj.getLong("fileId")), obj.getBoolean("clean"));
	}

	private void onFileTreeUpdate(IPartialPageRequestHandler handler) {
		sidebar.getFilesPanel().update(handler);
	}

	private String getQuickPollJs() {
		return String.format("Room.quickPoll(%s);", qpollManager.toJson(r.getId()));
	}

	private void updateInterviewRecordingButtons(IPartialPageRequestHandler handler) {
		Client curClient = getClient();
		if (r.isInterview() && curClient.hasRight(Right.MODERATOR)) {
			if (streamProcessor.isRecording(r.getId())) {
				handler.appendJavaScript("if (typeof(WbArea) === 'object') {WbArea.setRecStarted(true);}");
			} else if (streamProcessor.recordingAllowed(getClient())) {
				boolean hasStreams = cm.streamByRoom(r.getId())
						.anyMatch(cl -> !cl.getStreams().isEmpty());
				handler.appendJavaScript(String.format("if (typeof(WbArea) === 'object') {WbArea.setRecStarted(false);WbArea.setRecEnabled(%s);}", hasStreams));
			}
		}
	}

	public boolean isModerator(long userId, long roomId) {
		return isModerator(cm, userId, roomId);
	}

	public static boolean isModerator(ClientManager cm, long userId, long roomId) {
		return hasRight(cm, userId, roomId, Right.MODERATOR);
	}

	public static boolean hasRight(ClientManager cm, long userId, long roomId, Right r) {
		return cm.streamByRoom(roomId)
				.anyMatch(c -> c.sameUserId(userId) && c.hasRight(r));
	}

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		getBasePage().getHeader().setVisible(false);
		getMainPanel().getTopControls().setVisible(false);
		Component loader = getBasePage().getLoader().setVisible(false);
		if (r.isHidden(RoomElement.CHAT) || !isVisible()) {
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
		getMainPanel().getChat().toggle(handler, !r.isHidden(RoomElement.CHAT));
		handler.add(this.setVisible(true));
		handler.appendJavaScript("Room.load();");
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		if (eventDetail instanceof EventDetailDialog evtDialog) {
			evtDialog.close(handler);
		}
		handler.add(getBasePage().getHeader().setVisible(true), getMainPanel().getTopControls().setVisible(true));
		if (r.isHidden(RoomElement.CHAT)) {
			getMainPanel().getChat().toggle(handler, true);
		}
		handler.appendJavaScript("if (typeof(Room) !== 'undefined') { Room.unload(); }");
		cm.exitRoom(getClient());
		getMainPanel().getChat().roomExit(r, handler);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/room.js")) {
			private static final long serialVersionUID = 1L;

			@Override
			public List<HeaderItem> getDependencies() {
				return List.of(
					VIDEO_SETTINGS_JS,
					new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/wb.js"))
					, new PriorityHeaderItem(JavaScriptHeaderItem.forReference(JQueryUILibrarySettings.get().getJavaScriptReference())));
			}
		});
		response.render(JavaScriptHeaderItem.forReference(TouchPunchResourceReference.instance()));
		if (room.isVisible()) {
			response.render(OnDomReadyHeaderItem.forScript(roomEnter.getCallbackScript()));
		}
	}

	public void requestRight(Right right, IPartialPageRequestHandler handler) {
		RoomMessage.Type reqType = null;
		if (noModerators()) {
			if (r.isModerated()) {
				showIdeaAlert(handler, getString("696"));
				return;
			} else {
				// we found no-one we can ask, allow right
				rightsUpdated(cm.update(getClient().allow(right)));
			}
		}
		// ask
		switch (right) {
			case MODERATOR:
				reqType = Type.REQUEST_RIGHT_MODERATOR;
				break;
			case PRESENTER:
				reqType = Type.REQUEST_RIGHT_PRESENTER;
				break;
			case WHITEBOARD:
				reqType = Type.REQUEST_RIGHT_WB;
				break;
			case SHARE:
				reqType = Type.REQUEST_RIGHT_SHARE;
				break;
			case AUDIO:
				reqType = Type.REQUEST_RIGHT_A;
				break;
			case MUTE_OTHERS:
				reqType = Type.REQUEST_RIGHT_MUTE_OTHERS;
				break;
			case REMOTE_CONTROL:
				reqType = Type.REQUEST_RIGHT_REMOTE;
				break;
			case VIDEO:
				reqType = Type.REQUEST_RIGHT_AV;
				break;
			default:
				break;
		}
		if (reqType != null) {
			WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getClient(), reqType, getClient().getUid()));
		}
	}

	public void allowRight(Client client, Right... rights) {
		rightsUpdated(client.allow(rights));
	}

	public void denyRight(Client client, Right... rights) {
		for (Right right : rights) {
			client.deny(right);
		}
		client.getCamStreams().forEach(sd -> {
			if (sd.has(Client.Activity.AUDIO) && !client.hasRight(Right.AUDIO)) {
				sd.remove(Client.Activity.AUDIO);
			}
			if (sd.has(Client.Activity.VIDEO) && !client.hasRight(Right.VIDEO)) {
				sd.remove(Client.Activity.VIDEO);
			}
		});
		rightsUpdated(client);
	}

	public void rightsUpdated(Client c) {
		cm.update(c);
		streamProcessor.rightsUpdated(c);
	}

	public void broadcast(Client client) {
		cm.update(client);
		WebSocketHelper.sendRoom(new TextRoomMessage(getRoom().getId(), getClient(), RoomMessage.Type.RIGHT_UPDATED, client.getUid()));
	}

	@Override
	protected void process(IPartialPageRequestHandler handler, JSONObject o) throws IOException {
		if (room.isVisible() && "room".equals(o.optString("area"))) {
			final String type = o.optString("type");
			if ("wb".equals(type)) {
				WbAction a = WbAction.of(o.getString(PARAM_ACTION));
				wb.processWbAction(a, o.optJSONObject("data"), handler);
			} else if ("room".equals(type)) {
				sidebar.roomAction(handler, o);
			} else if ("av".equals(type)) {
				ExtendedClientProperties cp = WebSession.get().getExtendedProperties();
				Client c = cp.setSettings(o.optJSONObject("settings")).update(getClient());
				if (!avInited) {
					avInited = true;
					if (Room.Type.CONFERENCE == r.getType()) {
						if (!c.isAllowed(Client.Activity.AUDIO)) {
							c.allow(Room.Right.AUDIO);
						}
						if (!c.getRoom().isAudioOnly() && !c.isAllowed(Client.Activity.VIDEO)) {
							c.allow(Room.Right.VIDEO);
						}
						streamProcessor.onToggleActivity(c, c.getRoom().isAudioOnly()
								? Client.Activity.AUDIO
								: Client.Activity.AUDIO_VIDEO);
					}
				}
				c.getCamStreams().forEach(sd -> {
					sd.setWidth(c.getWidth());
					sd.setHeight(c.getHeight());
				});
				broadcast(c);
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
		Client c = getClient();
		return c.getScreenStream().isPresent() || streamProcessor.screenShareAllowed(c);
	}

	public RoomSidebar getSidebar() {
		return sidebar;
	}

	public AbstractWbPanel getWb() {
		return wb;
	}

	public String getPublishingUser() {
		return null;
	}

	public boolean isInterview() {
		return r.isInterview();
	}

	private void createWaitModerator(final boolean autoopen) {
		waitModerator = new Alert("wait-moderator", new ResourceModel("wait-moderator.message"), new ResourceModel("wait-moderator.title")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Component createMessage(String markupId, IModel<String> message) {
				return super.createMessage(markupId, message).setEscapeModelStrings(false);
			}
		};
		waitModerator.type(Alert.Type.Warning).setCloseButtonVisible(false);
		waitModerator.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(autoopen);
	}

	@Override
	protected String getCssClass() {
		String clazz = "room " + r.getType().name();
		if (r.isHidden(RoomElement.TOP_BAR)) {
			clazz += " no-menu";
		}
		if (r.isHidden(RoomElement.ACTIVITIES)) {
			clazz += " no-activities";
		}
		if (r.isHidden(RoomElement.CHAT)) {
			clazz += " no-chat";
		}
		if (!r.isHidden(RoomElement.MICROPHONE_STATUS)) {
			clazz += " mic-status";
		}
		return clazz;
	}

	private void showIdeaAlert(IPartialPageRequestHandler handler, String msg) {
		showAlert(handler, "info", msg, "far fa-lightbulb");
	}

	private void showAlert(IPartialPageRequestHandler handler, String type, String msg, String icon) {
		handler.appendJavaScript("OmUtil.alert('" + type + "', '<i class=\"" + icon + "\"></i>&nbsp;"
				+ StringEscapeUtils.escapeEcmaScript(msg)
				+ "', 10000)");
	}

	private CharSequence createAddClientJs(Client c) {
		JSONArray arr = new JSONArray();
		cm.streamByRoom(r.getId()).forEach(cl -> arr.put(cl.toJson(c.getUid().equals(cl.getUid()))));
		return new StringBuilder()
				.append("Room.addClient(")
				.append(arr.toString(new NullStringer()))
				.append(");");
	}

	private CharSequence sendClientsOnUpdate() {
		Client c = getClient();
		StringBuilder res = new StringBuilder();
		if (r.isHidden(RoomElement.USER_COUNT)) {
			if (c.hasRight(Room.Right.MODERATOR)) {
				res.append(createAddClientJs(c));
			} else {
				res.append("Room.removeOthers();");
			}
		}
		return res;
	}

	private boolean noModerators() {
		return cm.streamByRoom(r.getId())
				.filter(cl -> cl.hasRight(Room.Right.MODERATOR))
				.findAny()
				.isEmpty();
	}
}
