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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;

public class AppointmentDTO {
	private Long id;
	private String title;
	private String location;
	private Date start;
	private Date end;
	private String description;
	private UserDTO owner;
	private Date inserted;
	private Date updated;
	private boolean deleted;
	private AppointmentReminderTypeDTO reminder;
	private RoomDTO room;
	private String icalId;
	private List<MeetingMemberDTO> meetingMembers;
	private Long languageId;
	private boolean passwordProtected;
	private boolean connectedEvent;
	private boolean reminderEmailSend;

	public AppointmentDTO(Appointment a) {
		id = a.getId();
		title = a.getTitle();
		location = a.getLocation();
		start = a.getStart();
		end = a.getEnd();
		description = a.getDescription();
		owner = new UserDTO(a.getOwner());
		inserted = a.getInserted();
		updated = a.getUpdated();
		deleted = a.isDeleted();
		reminder = new AppointmentReminderTypeDTO(a.getRemind());
		room = new RoomDTO(a.getRoom());
		icalId = a.getIcalId();
		meetingMembers = new ArrayList<>();
		for(MeetingMember mm: a.getMeetingMembers()) {
			meetingMembers.add(new MeetingMemberDTO(mm));
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
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

	public AppointmentReminderTypeDTO getReminder() {
		return reminder;
	}

	public void setReminder(AppointmentReminderTypeDTO reminder) {
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
}
