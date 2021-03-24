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
package org.apache.openmeetings.db.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.lang.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthLevelUtil {
	private static final Logger log = LoggerFactory.getLogger(AuthLevelUtil.class);

	private AuthLevelUtil() {}

	public static boolean check(Set<User.Right> rights, User.Right level) {
		boolean result = rights.contains(level);
		log.debug("Level {} :: {}", level, result ? "[GRANTED]" : "[DENIED]");
		return result;
	}

	public static boolean hasUserLevel(Set<User.Right> rights) {
		return check(rights, User.Right.ROOM);
	}

	public static Set<Room.Right> getRoomRight(User u, Room r, Appointment a, int userCount) {
		Set<Room.Right> result = new HashSet<>();
		if (u == null) {
			return result;
		}
		if (//admin user get superModerator level, no-one can kick him/her
			hasAdminLevel(u.getRights())
			// user personal room
			|| Objects.equal(u.getId(), r.getOwnerId())
			// appointment owner is super moderator
			|| (r.isAppointment() && a != null && u.getId().equals(a.getOwner().getId())))
		{
			result.add(Room.Right.SUPER_MODERATOR);
		}
		if (result.isEmpty()) {
			if (!r.isModerated() && 1 == userCount) {
				//room is not moderated, first user is moderator!
				result.add(Room.Right.MODERATOR);
			}
			//performing loop here to set possible 'superModerator' right
			for (RoomModerator rm : r.getModerators()) {
				if (u.getId().equals(rm.getUser().getId())) {
					result.add(rm.isSuperModerator() ? Room.Right.SUPER_MODERATOR : Room.Right.MODERATOR);
					break;
				}
			}
			//no need to loop if client is moderator
			if (result.isEmpty() && r.getGroups() != null && !r.getGroups().isEmpty()) {
				for (RoomGroup rg : r.getGroups()) {
					for (GroupUser gu : u.getGroupUsers()) {
						if (gu.getGroup().getId().equals(rg.getGroup().getId()) && gu.isModerator()) {
							result.add(Room.Right.MODERATOR);
							break;
						}
					}
					if (!result.isEmpty()) {
						break;
					}
				}
			}
		}
		if (Room.Type.CONFERENCE == r.getType() && !result.contains(Room.Right.SUPER_MODERATOR) && !result.contains(Room.Right.MODERATOR) && !result.contains(Room.Right.VIDEO)) {
			result.add(Room.Right.AUDIO);
			result.add(Room.Right.VIDEO);
		}
		return result;
	}

	public static boolean hasAdminLevel(Set<User.Right> rights) {
		return check(rights, User.Right.ADMIN);
	}

	public static boolean hasGroupAdminLevel(Set<User.Right> rights) {
		return check(rights, User.Right.GROUP_ADMIN);
	}

	public static boolean hasWebServiceLevel(Set<User.Right> rights) {
		return check(rights, User.Right.SOAP);
	}

	public static boolean hasLoginLevel(Set<User.Right> rights) {
		return check(rights, User.Right.LOGIN);
	}
}
