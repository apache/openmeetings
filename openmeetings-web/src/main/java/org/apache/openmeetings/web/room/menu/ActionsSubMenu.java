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
package org.apache.openmeetings.web.room.menu;

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;

import java.io.Serializable;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.common.InvitationDialog;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ActionsSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private final InvitationDialog invite;
	private final SipDialerDialog sipDialer;
	private final RoomPanel room;
	private final RoomMenuPanel mp;
	private final StartSharingButton shareBtn;
	private RoomMenuItem actionsMenu;
	private RoomMenuItem inviteMenuItem;
	private RoomMenuItem shareMenuItem;
	private RoomMenuItem applyModerMenuItem;
	private RoomMenuItem applyWbMenuItem;
	private RoomMenuItem applyAvMenuItem;
	private RoomMenuItem sipDialerMenuItem;
	private RoomMenuItem downloadPngMenuItem;
	private RoomMenuItem downloadPdfMenuItem;
	private final boolean visible;

	public ActionsSubMenu(final RoomPanel room, final RoomMenuPanel mp, final StartSharingButton shareBtn) {
		this.room = room;
		this.mp = mp;
		this.shareBtn = shareBtn;
		RoomInvitationForm rif = new RoomInvitationForm("form", room.getRoom().getId());
		mp.add(invite = new InvitationDialog("invite", rif));
		rif.setDialog(invite);
		mp.add(sipDialer = new SipDialerDialog("sipDialer", room));
		visible = !room.getRoom().isHidden(RoomElement.ActionMenu);
	}

	public void init() {
		actionsMenu = new RoomMenuItem(mp.getString("635"), null, false);
		inviteMenuItem = new RoomMenuItem(mp.getString("213"), mp.getString("1489"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				invite.updateModel(target);
				invite.open(target);
			}
		};
		shareMenuItem = new RoomMenuItem(mp.getString("239"), mp.getString("1480"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				shareBtn.onClick(target);
			}
		};
		applyModerMenuItem = new RoomMenuItem(mp.getString("784"), mp.getString("1481"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				room.requestRight(Room.Right.moderator, target);
			}
		};
		applyWbMenuItem = new RoomMenuItem(mp.getString("785"), mp.getString("1492"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				room.requestRight(Room.Right.whiteBoard, target);
			}
		};
		applyAvMenuItem = new RoomMenuItem(mp.getString("786"), mp.getString("1482"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				room.requestRight(Room.Right.video, target);
			}
		};
		sipDialerMenuItem = new RoomMenuItem(mp.getString("1447"), mp.getString("1488"), false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				sipDialer.open(target);
			}
		};
		downloadPngMenuItem = new RoomMenuItem(mp.getString("download.png"), mp.getString("download.png")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				download(target, EXTENSION_PNG);
			}
		};
		downloadPdfMenuItem = new RoomMenuItem(mp.getString("download.pdf"), mp.getString("download.pdf")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				download(target, EXTENSION_PDF);
			}
		};
	}

	RoomMenuItem getMenu() {
		actionsMenu.setTop(true);
		actionsMenu.getItems().add(inviteMenuItem);
		actionsMenu.getItems().add(shareMenuItem);
		actionsMenu.getItems().add(applyModerMenuItem);
		actionsMenu.getItems().add(applyWbMenuItem);
		actionsMenu.getItems().add(applyAvMenuItem);
		actionsMenu.getItems().add(sipDialerMenuItem);
		actionsMenu.getItems().add(downloadPngMenuItem);
		actionsMenu.getItems().add(downloadPdfMenuItem);
		return actionsMenu;
	}

	public void update(final boolean moder, final boolean notExternalUser, final Room r) {
		if (!visible) {
			return;
		}
		boolean isInterview = Room.Type.interview == r.getType();
		downloadPngMenuItem.setEnabled(!isInterview);
		downloadPdfMenuItem.setEnabled(!isInterview);
		actionsMenu.setEnabled((moder && visible) || (!moder && r.isAllowUserQuestions()));
		inviteMenuItem.setEnabled(notExternalUser && moder);
		boolean shareVisible = room.screenShareAllowed();
		shareMenuItem.setEnabled(shareVisible);
		applyModerMenuItem.setEnabled(!moder);
		applyWbMenuItem.setEnabled(!room.getClient().hasRight(Room.Right.whiteBoard));
		applyAvMenuItem.setEnabled(!room.getClient().hasRight(Room.Right.audio) || !room.getClient().hasRight(Room.Right.video));
		sipDialerMenuItem.setEnabled(r.isSipEnabled() && isSipEnabled());
	}

	private static void download(AjaxRequestTarget target, String type) {
		target.appendJavaScript(String.format("WbArea.download('%s');", type));
	}

	public boolean isVisible() {
		return visible;
	}
}
