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
package org.apache.openmeetings.web.user;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.user.rooms.RoomListPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class InviteUserToRoomDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;
	private DialogButton cancel;
	private RoomListPanel publicRooms;
	private RoomListPanel privateRooms;
	private final InviteUserMessageDialog inviteMsg = new InviteUserMessageDialog("inviteMsg");
	private Long userId;

	private class InviteRoomListPanel extends RoomListPanel {
		private static final long serialVersionUID = 1L;

		public InviteRoomListPanel(String id, List<Room> rooms, final String label) {
			super(id, rooms, label);
		}

		@Override
		public void onRoomEnter(AjaxRequestTarget target, Long roomId) {
			inviteMsg.open(target, roomId, userId);
		}
	}

	public InviteUserToRoomDialog(String id) {
		super(id, "");
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("1131"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		add(publicRooms = new InviteRoomListPanel("publicRooms", new ArrayList<Room>(), getString("1135")));
		add(privateRooms = new InviteRoomListPanel("privateRooms", new ArrayList<Room>(), getString("1135")));
		add(inviteMsg);
		super.onInitialize();
	}

	private static List<Room> getPrivateRooms(Long userId1, Long userId2, RoomDao roomDao) {
		List<Long> orgIds = new ArrayList<>();
		List<Long> orgIds2 = new ArrayList<>();
		UserDao userDao = getBean(UserDao.class);
		for (GroupUser gu : userDao.get(userId1).getGroupUsers()) {
			orgIds.add(gu.getGroup().getId());
		}
		for (GroupUser gu : userDao.get(userId2).getGroupUsers()) {
			orgIds2.add(gu.getGroup().getId());
		}
		orgIds.retainAll(orgIds2);
		List<Room> result = new ArrayList<>();
		for (Long orgId : orgIds) {
			result.addAll(roomDao.getGroupRooms(orgId));
		}
		return result;
	}

	public void open(IPartialPageRequestHandler handler, Long userId) {
		this.userId = userId;
		RoomDao roomDao = getBean(RoomDao.class);
		publicRooms.update(handler, roomDao.getPublicRooms());
		privateRooms.update(handler, getPrivateRooms(getUserId(), userId, roomDao));
		open(handler);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(cancel);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		//no-op
	}
}
