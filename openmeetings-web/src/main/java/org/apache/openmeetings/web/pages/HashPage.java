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
import static org.apache.openmeetings.web.app.WebSession.WICKET_ROOM_ID;
import static org.apache.openmeetings.web.app.WebSession.getRecordingId;
import static org.apache.openmeetings.web.room.SwfPanel.SWF;
import static org.apache.openmeetings.web.room.SwfPanel.SWF_TYPE_NETWORK;
import static org.apache.openmeetings.web.room.SwfPanel.SWF_TYPE_SETTINGS;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.openmeetings.web.room.SwfPanel;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.openmeetings.web.user.record.VideoPlayer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
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
	public static final String SECURE_HASH = "secureHash";
	public static final String INVITATION_HASH = "invitationHash";
	private static final String HASH = "hash";
	private final WebMarkupContainer recContainer = new WebMarkupContainer("panel-recording");
	private final VideoInfo vi = new VideoInfo("info", null);
	private final VideoPlayer vp = new VideoPlayer("player", null);
	private String errorKey = "invalid.hash";
	private boolean error = true;;

	public HashPage(PageParameters p) {
		StringValue secure = p.get(SECURE_HASH);
		if (secure.isEmpty()) {
			secure = p.get(HASH);
		}
		StringValue invitation = p.get(INVITATION_HASH);

		WebSession ws = WebSession.get();
		ws.checkHashes(secure, invitation);

		recContainer.setVisible(false);
		add(new EmptyPanel("panel-swf").setVisible(false));
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
				}
				Room r = i.getRoom();
				if (r != null) {
					replace(new SwfPanel("panel-swf", new PageParameters(p).add(WICKET_ROOM_ID, r.getId())));
				}
				error = false;
			}
		} else if (!secure.isEmpty()) {
			Long recId = getRecordingId();
			if (recId == null && ws.getRoomId() == null) {
				errorKey = "1599";
			} else if (recId != null) {
				recContainer.setVisible(true);
				Recording rec = getBean(RecordingDao.class).get(recId);
				vi.update(null, rec);
				vp.update(null, rec);
				error = false;
			} else {
				replace(new SwfPanel("panel-swf", new PageParameters(p).add(WICKET_ROOM_ID, ws.getRoomId())));
				error = false;
			}
		}
		StringValue swf = p.get(SWF);
		if (!swf.isEmpty() && (SWF_TYPE_NETWORK.equals(swf.toString()) || SWF_TYPE_SETTINGS.equals(swf.toString()))) {
			replace(new SwfPanel("panel-swf", p));
			error = false;
		}
		add(recContainer
			.add(vi.setShowShare(false).setOutputMarkupPlaceholderTag(true)
				, vp.setOutputMarkupPlaceholderTag(true)
				, new InvitationPasswordDialog("i-pass", this)));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new MessageDialog("access-denied", getString("invalid.hash"), getString(errorKey), DialogButtons.OK, DialogIcon.ERROR) {
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
		target.add(vi.update(target, i.getRecording()).setVisible(true)
				, vp.update(target, i.getRecording()).setVisible(true));
	}
}
