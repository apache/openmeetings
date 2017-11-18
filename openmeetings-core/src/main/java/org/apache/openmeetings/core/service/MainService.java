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
package org.apache.openmeetings.core.service;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author swagner
 *
 */
@Service
public class MainService {
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, OpenmeetingsVariables.getWebAppRootKey());

	@Autowired
	private AppointmentDao appointmentDao;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";

	private static boolean checkAppointment(Appointment a, User u) {
		if (a == null || a.isDeleted()) {
			return false;
		}
		if (a.getOwner().getId().equals(u.getId())) {
			log.debug("[isRoomAllowedToUser] appointed room, Owner entered");
			return true;
		}
		for (MeetingMember mm : a.getMeetingMembers()) {
			if (mm.getUser().getId().equals(u.getId())) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkGroups(Room r, User u) {
		if (null == r.getGroups()) { //u.getGroupUsers() can't be null due to user was able to login
			return false;
		}
		for (RoomGroup ro : r.getGroups()) {
			for (GroupUser ou : u.getGroupUsers()) {
				if (ro.getGroup().getId().equals(ou.getGroup().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRoomAllowedToUser(Room r, User u) {
		if (r == null) {
			return false;
		}
		if (r.isAppointment()) {
			Appointment a = appointmentDao.getByRoom(r.getId());
			return checkAppointment(a, u);
		} else {
			if (r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(u.getId()))) {
				log.debug("[isRoomAllowedToUser] public ? {} , ownedId ? {} ALLOWED", r.getIspublic(), r.getOwnerId());
				return true;
			}
			return checkGroups(r, u);
		}
	}
}
