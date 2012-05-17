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
package org.openmeetings.app.data.calendar.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;

public class AppointmentDTO {

	private long appointmentId;
	private Long categoryId;
	private Long reminderId;
	private Long roomId;
	private Long roomTypeId;
	private String title;
	private String location;
	private String comment;
	private Calendar start;
	private Calendar end;
	private Boolean isPasswordProtected;
	private List<MeetingMemberDTO> meetingMember = new ArrayList<MeetingMemberDTO>();

	public AppointmentDTO(Appointment appointment, TimeZone timezone) {
		appointmentId = appointment.getAppointmentId();
		categoryId = (appointment.getAppointmentCategory() != null) ? appointment
				.getAppointmentCategory().getCategoryId() : null;
		reminderId = (appointment.getRemind() != null) ? appointment
				.getRemind().getTypId() : null;
		roomId = (appointment.getRoom() != null) ? appointment.getRoom()
				.getRooms_id() : null;
		roomTypeId = (roomId != null && appointment.getRoom().getRoomtype() != null) ? appointment
				.getRoom().getRoomtype().getRoomtypes_id()
				: null;
		title = appointment.getAppointmentName();
		location = appointment.getAppointmentLocation();
		comment = appointment.getAppointmentDescription();
		start = appointment.appointmentStartAsCalendar(timezone);
		end = appointment.appointmentEndAsCalendar(timezone);
		isPasswordProtected = appointment.getIsPasswordProtected();
		for (MeetingMember meetingMemberItem : appointment.getMeetingMember()) {
			meetingMember.add(new MeetingMemberDTO(meetingMemberItem));
		}
	}

	public long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getReminderId() {
		return reminderId;
	}

	public void setReminderId(Long reminderId) {
		this.reminderId = reminderId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}

	public void setIsPasswordProtected(Boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}

	public List<MeetingMemberDTO> getMeetingMember() {
		return meetingMember;
	}

	public void setMeetingMember(List<MeetingMemberDTO> meetingMember) {
		this.meetingMember = meetingMember;
	}

	public Long getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

}
