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
package org.apache.openmeetings.db.manager;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class RoomManager {
	private static final Logger log = LoggerFactory.getLogger(RoomManager.class);

	@Inject
	private AppointmentDao appointmentDao;

	private static boolean checkAppointment(Appointment a, User u) {
		if (a == null || a.isDeleted()) {
			return false;
		}
		if (a.isOwner(u.getId())) {
			log.debug("[isRoomAllowedToUser] appointed room, Owner entered");
			return true;
		}
		return a.getMeetingMembers().stream()
				.map(MeetingMember::getUser)
				.map(User::getId)
				.anyMatch(userId -> userId.equals(u.getId()));
	}

	private static boolean checkGroups(Room r, User u) {
		if (null == r.getGroups()) { //u.getGroupUsers() can't be null due to user was able to login
			return false;
		}
		Set<Long> roomGroups = r.getGroups().stream()
				.map(RoomGroup::getGroup)
				.map(Group::getId)
				.collect(Collectors.toSet());
		return u.getGroupUsers().stream()
				.map(GroupUser::getGroup)
				.map(Group::getId)
				.anyMatch(roomGroups::contains);
	}

	public boolean isRoomAllowedToUser(Room r, User u) {
		if (r == null) {
			return false;
		}
		if (r.isAppointment()) {
			Appointment a = appointmentDao.getByRoom(r.getId());
			return checkAppointment(a, u);
		}
		if (r.getIspublic() || r.isOwner(u.getId())) {
			log.debug("[isRoomAllowedToUser] public ? {} , ownedId ? {} ALLOWED", r.getIspublic(), r.getOwnerId());
			return true;
		}
		return checkGroups(r, u);
	}
}
