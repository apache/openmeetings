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
package org.apache.openmeetings.persistence.beans.calendar;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "meeting_members")
@Root(name = "meetingmember")
public class MeetingMember implements Serializable {
	private static final long serialVersionUID = -3864571325368787524L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true)
	private Long meetingMemberId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name = "userid", data = true, required = false)
	private User userid;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "appointment_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private Appointment appointment;

	@Column(name = "appointment_status")
	@Element(data = true, required = false)
	private String appointmentStatus; // status of the appointment denial, acceptance, wait.

	@Column(name = "starttime")
	private Date starttime;

	@Column(name = "updatetime")
	private Date updatetime;

	@Column(name = "deleted")
	@Element(data = true)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invitation", nullable = true, insertable = false)
	@ForeignKey(enabled = true)
	private Invitations invitation;

	/**
	 * java.util.TimeZone Id
	 */
	@Column(name = "time_zone_id")
	@Element(data = true, required = false)
	private String timeZoneId;

	@Column(name = "is_connected_event")
	private boolean isConnectedEvent;

	public Long getMeetingMemberId() {
		return meetingMemberId;
	}

	public void setMeetingMemberId(Long groupMemberId) {
		this.meetingMemberId = groupMemberId;
	}

	public User getUserid() {
		return userid;
	}

	public void setUserid(User userid) {
		this.userid = userid;
	}

	public String getAppointmentStatus() {
		return appointmentStatus;
	}

	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Invitations getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitations invitation) {
		this.invitation = invitation;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean getIsConnectedEvent() {
		return isConnectedEvent;
	}

	public void setIsConnectedEvent(boolean isConnectedEvent) {
		this.isConnectedEvent = isConnectedEvent;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

}
