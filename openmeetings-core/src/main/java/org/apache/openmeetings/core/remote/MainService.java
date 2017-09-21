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
package org.apache.openmeetings.core.remote;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author swagner
 *
 */
@Service
public class MainService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";

	public boolean isRoomAllowedToUser(Room r, User u) {
		boolean allowed = false;
		if (r != null) {
			if (r.isAppointment()) {
				Appointment a = appointmentDao.getByRoom(r.getId());
				if (a != null && !a.isDeleted()) {
					allowed = a.getOwner().getId().equals(u.getId());
					log.debug("[loginWicket] appointed room, isOwner ? " + allowed);
					if (!allowed) {
						for (MeetingMember mm : a.getMeetingMembers()) {
							if (mm.getUser().getId().equals(u.getId())) {
								allowed = true;
								break;
							}
						}
					}
					/*
					TODO need to be reviewed
					Calendar c = WebSession.getCalendar();
					if (c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
						allowed = true;
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm"); //FIXME format
						deniedMessage = Application.getString("error.hash.period") + String.format(" %s - %s", sdf.format(a.getStart()), sdf.format(a.getEnd()));
					}
					*/
				}
			} else {
				allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(u.getId()));
				log.debug("[loginWicket] public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
				if (!allowed && null != r.getGroups()) {
					for (RoomGroup ro : r.getGroups()) {
						for (GroupUser ou : u.getGroupUsers()) {
							if (ro.getGroup().getId().equals(ou.getGroup().getId())) {
								allowed = true;
								break;
							}
						}
						if (allowed) {
							break;
						}
					}
				}
			}
		}
		return allowed;
	}

	@Override
	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("[resultReceived]" + arg0);
	}
}
