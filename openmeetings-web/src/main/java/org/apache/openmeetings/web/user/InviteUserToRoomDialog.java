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

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.user.rooms.RoomListPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class InviteUserToRoomDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private RoomListPanel publicRooms;
	private RoomListPanel privateRooms;
	private final InviteUserMessageDialog inviteMsg = new InviteUserMessageDialog("inviteMsg");
	private Long userId;

	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;

	private class InviteRoomListPanel extends RoomListPanel {
		private static final long serialVersionUID = 1L;

		public InviteRoomListPanel(String id, List<Room> rooms, final String label) {
			super(id, rooms, label);
		}

		@Override
		public void onRoomEnter(AjaxRequestTarget target, Long roomId) {
			inviteMsg.show(target, roomId, userId);
		}
	}

	public InviteUserToRoomDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1131"));

		addButton(OmModalCloseButton.of());
		add(publicRooms = new InviteRoomListPanel("publicRooms", new ArrayList<>(), getString("1135")));
		add(privateRooms = new InviteRoomListPanel("privateRooms", new ArrayList<>(), getString("1135")));
		add(inviteMsg);
		super.onInitialize();
	}

	private List<Room> getPrivateRooms(Long userId1, Long userId2, RoomDao roomDao) {
		List<Long> orgIds = new ArrayList<>();
		List<Long> orgIds2 = new ArrayList<>();
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

	public void show(IPartialPageRequestHandler handler, Long userId) {
		this.userId = userId;
		publicRooms.update(handler, roomDao.getPublicRooms());
		privateRooms.update(handler, getPrivateRooms(getUserId(), userId, roomDao));
		super.show(handler);
	}
}
