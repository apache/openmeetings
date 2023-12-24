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
package org.apache.openmeetings.db.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.calendar.MeetingMemberDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class CalendarMapper {
	@Inject
	private UserDao userDao;
	@Inject
	private AppointmentDao appointmentDao;
	@Inject
	private UserMapper uMapper;
	@Inject
	private RoomMapper rMapper;

	public Appointment get(AppointmentDTO dto, User u) {
		Appointment a = dto.getId() == null ? new Appointment() : appointmentDao.get(dto.getId());
		a.setId(dto.getId());
		a.setTitle(dto.getTitle());
		a.setLocation(dto.getLocation());
		a.setStart(dto.getStart().getTime());
		a.setEnd(dto.getEnd().getTime());
		a.setDescription(dto.getDescription());
		a.setOwner(dto.getOwner() == null ? u : userDao.get(dto.getOwner().getId()));
		a.setInserted(dto.getInserted());
		a.setUpdated(dto.getUpdated());
		a.setDeleted(dto.isDeleted());
		a.setReminder(dto.getReminder());
		a.setRoom(rMapper.get(dto.getRoom()));
		a.setIcalId(dto.getIcalId());
		List<MeetingMember> mml = new ArrayList<>();
		for(MeetingMemberDTO mm : dto.getMeetingMembers()) {
			MeetingMember m = null;
			if (mm.getId() != null) {
				//if ID is NOT null it should already be in the list
				for (MeetingMember m1 : a.getMeetingMembers()) {
					if (m1.getId().equals(mm.getId())) {
						m = m1;
						break;
					}
				}
				if (m == null) {
					throw new RuntimeException("Weird guest from different appointment is passed");
				}
			} else {
				m = get(mm, u);
				m.setAppointment(a);
			}
			mml.add(m);
		}
		a.setMeetingMembers(mml);
		a.setLanguageId(dto.getLanguageId());
		a.setPasswordProtected(dto.isPasswordProtected());
		a.setConnectedEvent(dto.isConnectedEvent());
		a.setReminderEmailSend(dto.isReminderEmailSend());
		return a;
	}

	public MeetingMember get(MeetingMemberDTO dto, User owner) {
		MeetingMember mm = new MeetingMember();
		mm.setId(dto.getId());
		if (dto.getUser().getId() != null) {
			mm.setUser(userDao.get(dto.getUser().getId()));
		} else {
			User u = null;
			UserDTO user = dto.getUser();
			if (User.Type.EXTERNAL == user.getType()) {
				// try to get ext. user
				u = userDao.getExternalUser(user.getExternalId(), user.getExternalType());
			}
			if (u == null && user.getAddress() != null) {
				u = userDao.getContact(user.getAddress().getEmail()
						, user.getFirstname()
						, user.getLastname()
						, user.getLanguageId()
						, user.getTimeZoneId()
						, owner);
			}
			if (u == null) {
				user.setType(User.Type.CONTACT);
				u = uMapper.get(user);
				u.getRights().clear();
			}
			if (Strings.isEmpty(u.getTimeZoneId())) {
				u.setTimeZoneId(owner.getTimeZoneId());
			}
			mm.setUser(u);
		}
		return mm;
	}
}
