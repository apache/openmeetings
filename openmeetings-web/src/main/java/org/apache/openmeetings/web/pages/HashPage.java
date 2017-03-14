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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.room.SwfPanel.SWF;
import static org.apache.openmeetings.web.room.SwfPanel.SWF_TYPE_NETWORK;
import static org.apache.openmeetings.web.room.SwfPanel.SWF_TYPE_SETTINGS;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.SwfPanel;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.openmeetings.web.user.record.VideoPlayer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class HashPage extends BaseInitedPage implements IUpdatable {
	private static final long serialVersionUID = 1L;
	public static final String PANEL_MAIN = "panel-main";
	public static final String INVITATION_HASH = "invitation";
	private static final String HASH = "secure";
	private final WebMarkupContainer recContainer = new WebMarkupContainer("panel-recording");
	private final VideoInfo vi = new VideoInfo("info", null);
	private final VideoPlayer vp = new VideoPlayer("player");
	private String errorKey = "invalid.hash";
	private boolean error = true;
	private MainPanel mp = null;
	private RoomPanel rp = null;

	public HashPage(PageParameters p) {
		StringValue secure = p.get(HASH);
		StringValue invitation = p.get(INVITATION_HASH);

		WebSession ws = WebSession.get();
		ws.checkHashes(secure, invitation);

		recContainer.setVisible(false);
		add(new EmptyPanel(PANEL_MAIN).setVisible(false));
		if (!invitation.isEmpty()) {
			Invitation i = ws.getInvitation();
			if (i == null) {
				errorKey = "535";
			} else if (!i.isAllowEntry()) {
				errorKey = Valid.OneTime == i.getValid() ? "534" : "1271";
			} else {
				Recording rec = i.getRecording();
				if (rec != null) {
					vi.setVisible(!i.isPasswordProtected());
					vp.setVisible(!i.isPasswordProtected());
					if (!i.isPasswordProtected()) {
						vi.update(null, rec);
						vp.update(null, rec);
					}
					recContainer.setVisible(true);
					error = false;
				}
				Room r = i.getRoom();
				if (r != null) {
					createRoom(r.getId());
					if (i.isPasswordProtected() && rp != null) {
						mp.getChat().setVisible(false);
						rp.setOutputMarkupPlaceholderTag(true).setVisible(false);
					}
				}
			}
		} else if (!secure.isEmpty()) {
			Long recId = getRecordingId(), roomId = ws.getRoomId();
			if (recId == null && roomId == null) {
				errorKey = "1599";
			} else if (recId != null) {
				recContainer.setVisible(true);
				Recording rec = getBean(RecordingDao.class).get(recId);
				vi.update(null, rec);
				vp.update(null, rec);
				error = false;
			} else {
				createRoom(roomId);
			}
		}
		StringValue swf = p.get(SWF);
		if (!swf.isEmpty() && (SWF_TYPE_NETWORK.equals(swf.toString()) || SWF_TYPE_SETTINGS.equals(swf.toString()))) {
			replace(new SwfPanel(PANEL_MAIN, p));
			error = false;
		}
		add(recContainer.add(vi.setShowShare(false).setOutputMarkupPlaceholderTag(true),
				vp.setOutputMarkupPlaceholderTag(true)), new InvitationPasswordDialog("i-pass", this));
	}

	private void createRoom(Long roomId) {
		error = false;
		getHeader().setVisible(false);
		// need to re-fetch Room object to initialize all collections
		Room room = getBean(RoomDao.class).get(roomId);
		if (room != null) {
			rp = new RoomPanel(CHILD_ID, room);
			mp = new MainPanel(PANEL_MAIN, rp);
			replace(mp);
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		remove(urlParametersReceivingBehavior);
		add(new MessageDialog("access-denied", getString("invalid.hash"), getString(errorKey), DialogButtons.OK,
				DialogIcon.ERROR) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("autoOpen", error);
				behavior.setOption("resizable", false);
			}

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				// no-op
			}
		});
	}

	@Override
	protected void onParameterArrival(IRequestParameters requestParameters, AjaxRequestTarget target) {
	}

	@Override
	public void update(AjaxRequestTarget target) {
		Invitation i = WebSession.get().getInvitation();
		if (i.getRoom() != null && rp != null) {
			rp.show(target);
		} else if (i.getRecording() != null) {
			target.add(vi.update(target, i.getRecording()).setVisible(true)
					, vp.update(target, i.getRecording()).setVisible(true));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forCSS(".invite.om-icon{display: none !important;}", "no-invite-to-room"));
	}
}
