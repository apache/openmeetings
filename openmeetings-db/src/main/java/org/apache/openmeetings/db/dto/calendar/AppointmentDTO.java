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
package org.apache.openmeetings.db.dto.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.util.TimezoneUtil;

import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AppointmentDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String title;
	private String location;
	private Calendar start;
	private Calendar end;
	private String description;
	private UserDTO owner;
	private Date inserted;
	private Date updated;
	private boolean deleted;
	private Reminder reminder;
	private RoomDTO room;
	private String icalId;
	private List<MeetingMemberDTO> meetingMembers = new ArrayList<>();
	private Long languageId;
	private String password;
	private boolean passwordProtected;
	private boolean connectedEvent;
	private boolean reminderEmailSend;

	public AppointmentDTO() {
		//def constructor
	}

	public AppointmentDTO(Appointment a) {
		id = a.getId();
		title = a.getTitle();
		location = a.getLocation();
		TimeZone tz = TimezoneUtil.getTimeZone(a.getOwner());
		start = Calendar.getInstance(tz);
		start.setTime(a.getStart());
		end = Calendar.getInstance(tz);
		end.setTime(a.getEnd());
		description = a.getDescription();
		owner = new UserDTO(a.getOwner());
		inserted = a.getInserted();
		updated = a.getUpdated();
		deleted = a.isDeleted();
		reminder = a.getReminder();
		room = new RoomDTO(a.getRoom());
		icalId = a.getIcalId();
		if (a.getMeetingMembers() != null) {
			for(MeetingMember mm : a.getMeetingMembers()) {
				meetingMembers.add(new MeetingMemberDTO(mm));
			}
		}
		languageId = a.getLanguageId();
		passwordProtected = a.isPasswordProtected();
		connectedEvent = a.isConnectedEvent();
		reminderEmailSend = a.isReminderEmailSend();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserDTO getOwner() {
		return owner;
	}

	public void setOwner(UserDTO owner) {
		this.owner = owner;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Reminder getReminder() {
		return reminder;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}

	public RoomDTO getRoom() {
		return room;
	}

	public void setRoom(RoomDTO room) {
		this.room = room;
	}

	public String getIcalId() {
		return icalId;
	}

	public void setIcalId(String icalId) {
		this.icalId = icalId;
	}

	public List<MeetingMemberDTO> getMeetingMembers() {
		return meetingMembers;
	}

	public void setMeetingMembers(List<MeetingMemberDTO> meetingMembers) {
		this.meetingMembers = meetingMembers;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(boolean passwordProtected) {
		this.passwordProtected = passwordProtected;
	}

	public boolean isConnectedEvent() {
		return connectedEvent;
	}

	public void setConnectedEvent(boolean connectedEvent) {
		this.connectedEvent = connectedEvent;
	}

	public boolean isReminderEmailSend() {
		return reminderEmailSend;
	}

	public void setReminderEmailSend(boolean reminderEmailSend) {
		this.reminderEmailSend = reminderEmailSend;
	}

	public static List<AppointmentDTO> list(List<Appointment> list) {
		List<AppointmentDTO> result = new ArrayList<>(list.size());
		for (Appointment a : list) {
			result.add(new AppointmentDTO(a));
		}
		return result;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
