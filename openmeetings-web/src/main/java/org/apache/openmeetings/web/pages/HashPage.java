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
package org.apache.openmeetings.web.pages;

import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.basic.WsClient;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.common.OmAjaxClientInfoBehavior;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.common.OmWebSocketPanel;
import org.apache.openmeetings.web.room.IconTextModal;
import org.apache.openmeetings.web.room.NetTestPanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.VideoSettings;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.openmeetings.web.user.record.VideoPlayer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import jakarta.inject.Inject;

public class HashPage extends BaseInitedPage implements IUpdatable {
	private static final long serialVersionUID = 1L;
	public static final String APP_KEY = "app";
	public static final String APP_TYPE_NETWORK = "network";
	public static final String APP_TYPE_SETTINGS = "settings";
	public static final String SWF_KEY = "swf";
	public static final String PANEL_MAIN = "panel-main";
	public static final String I_PASS_ID = "i-pass";
	public static final String PANEL_RECORDING = "panel-recording";
	public static final String INVITATION_HASH = "invitation";
	static final String HASH = "secure";
	static final String LANG = "language";
	private final WebMarkupContainer recContainer = new WebMarkupContainer(PANEL_RECORDING);
	private final VideoInfo videoInfo = new VideoInfo("info");
	private final VideoPlayer videoPlayer = new VideoPlayer("player");
	private boolean error = true;
	private MainPanel mainPanel = null;
	private RoomPanel roomPanel = null;
	private WebMarkupContainer passwdDialog = new EmptyPanel(I_PASS_ID);
	private final StringValue secure;
	private final StringValue invitation;
	private final StringValue swf;
	private final StringValue app;
	private final long lang;

	@Inject
	private RoomDao roomDao;
	@Inject
	private RecordingDao recDao;

	public HashPage(PageParameters p) {
		secure = p.get(HASH);
		invitation = p.get(INVITATION_HASH);
		swf = p.get(SWF_KEY);
		app = swf.isEmpty() ? p.get(APP_KEY) : swf;
		lang = p.get(LANG).toLong(-1L);
	}

	private void setLanguage() {
		if (lang > -1) {
			WebSession ws = WebSession.get();
			ws.setLanguage(lang);
			ws.setLocale(LocaleHelper.getLocale(lang));
		}
	}

	private void createRoom(Long roomId, boolean visible) {
		if (roomPanel != null) {
			return; // all done
		}
		Room room = roomDao.get(roomId); // this call required to eager-fetch everything
		// need to re-fetch Room object to initialize all collections
		if (room != null && !room.isDeleted()) {
			getLoader().setVisible(true);
			getHeader().setVisible(false);
			error = false;
			roomPanel = new RoomPanel(CHILD_ID, room);
			roomPanel.setOutputMarkupPlaceholderTag(true).setVisible(visible);
			mainPanel = new MainPanel(PANEL_MAIN, roomPanel);
			replace(mainPanel);
		}
	}

	private void processApp() {
		if (APP_TYPE_NETWORK.equals(app.toString())) {
			replace(new NetTestPanel(PANEL_MAIN).add(AttributeModifier.append("class", "app")));
			error = false;
		} else if (APP_TYPE_SETTINGS.equals(app.toString())) {
			replace(new VideoSettings(PANEL_MAIN)
				.replace(new OmWebSocketPanel("ws-panel") {
					private static final long serialVersionUID = 1L;
					private WsClient c = null;

					@Override
					protected void onConnect(ConnectedMessage message) {
						c = new WsClient(message.getSessionId(), message.getKey().hashCode());
					}

					@Override
					protected void onConnect(WebSocketRequestHandler handler) {
						super.onConnect(handler);
						handler.appendJavaScript(
								String.format("VideoSettings.init(%s);VideoSettings.open();"
										, VideoSettings.getInitJson("noclient")
											.put("infoMsg", getString("close.settings.tab"))));
					}

					@Override
					protected IWsClient getWsClient() {
						return c;
					}
				})
				.add(new OmAjaxClientInfoBehavior()));
			error = false;
		}
	}

	private String processInvitation() {
		WebSession ws = WebSession.get();
		Invitation i = ws.checkInviteHash(invitation, false);
		if (i == null) {
			return getString("error.hash.invalid");
		} else if (!i.isAllowEntry()) {
			FastDateFormat sdf = FormatHelper.getDateTimeFormat(i.getInvitee());
			return Valid.ONE_TIME == i.getValid()
					? getString("error.hash.used")
					: String.format("%s %s - %s, %s", getString("error.hash.period")
							, sdf.format(i.getValidFrom()), sdf.format(i.getValidTo())
							, i.getInvitee().getTimeZoneId());
		} else {
			passwdDialog = new InvitationPasswordDialog(I_PASS_ID, i, this);
			Recording rec = i.getRecording();
			Room r = i.getRoom();
			error = rec == null && r == null;
			if (ws.getInvitation() != null && r != null) {
				createRoom(r.getId(), false);
			}
			update(null);
		}
		return null;
	}

	private String processSecure() {
		WebSession ws = WebSession.get();
		ws.checkSecureHash(secure);
		Long recId = getRecordingId(), roomId = ws.getRoomId();
		if (recId == null && roomId == null) {
			return getString("1599");
		}
		error = false;
		if (recId != null) {
			recContainer.setVisible(true);
			Recording rec = recDao.get(recId);
			videoInfo.update(null, rec);
			videoPlayer.update(null, rec);
		} else {
			createRoom(roomId, true);
		}
		return null;
	}

	@Override
	protected void onInitialize() {
		setLanguage();
		super.onInitialize();

		String errorMsg = getString("invalid.hash");
		recContainer
			.setOutputMarkupPlaceholderTag(true)
			.setOutputMarkupId(true)
			.setVisible(false);
		add(new EmptyPanel(PANEL_MAIN)
			.setOutputMarkupPlaceholderTag(true)
			.setOutputMarkupId(true)
			.setVisible(false));
		if (!app.isEmpty()) {
			processApp();
		} else if (!invitation.isEmpty()) {
			errorMsg = processInvitation();
		} else if (!secure.isEmpty()) {
			errorMsg = processSecure();
		}

		add(recContainer.add(videoInfo, videoPlayer), passwdDialog);
		add(new IconTextModal("access-denied")
				.withLabel(errorMsg)
				.withErrorIcon()
				.addButton(OmModalCloseButton.of("54"))
				.header(new ResourceModel("invalid.hash"))
				.show(error)
		);

		remove(urlParametersReceivingBehavior);
	}

	@Override
	protected void onParameterArrival(IRequestParameters requestParameters, AjaxRequestTarget target) {
		//no-op
	}

	@Override
	public void update(AjaxRequestTarget target) {
		Invitation i = WebSession.get().getInvitation();
		if (error || i == null) {
			return;
		}
		setLanguage();
		Recording rec = i.getRecording();
		if (i.getRoom() != null) {
			createRoom(i.getRoom().getId(), true);
			// invitation is in session so we can show the room
			if (roomPanel.getMainPanel() != null) {
				// everything was inited
				roomPanel.show(target);
			} else {
				roomPanel.setVisible(true);
			}
		} else if (rec != null) {
			recContainer.setVisible(true);
			videoInfo.update(target, rec).setVisible(true);
			videoPlayer.update(target, rec).setVisible(true);
		}
		if (target != null) {
			for (Component comp : new Component[]{getLoader(), getHeader(), mainPanel, roomPanel, recContainer}) {
				if (comp != null) {
					target.add(comp);
				}
			}
			target.appendJavaScript("""
				const elem = document.querySelector('html');
				Object.entries({
					'xml:lang': '%1$s',
					'lang': '%1$s',
					'dir': '%2$s'
				}).forEach(kv => elem.setAttribute(kv[0], kv[1]));
				""".formatted(getLanguageCode(), isRtl() ? "rtl" : "ltr"));
		}
	}
}
