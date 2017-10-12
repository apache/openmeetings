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
package org.apache.openmeetings.db.entity.calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "meeting_member")
@NamedQueries({
	@NamedQuery(name="getMeetingMemberById"
			, query="SELECT mm FROM MeetingMember mm WHERE mm.deleted = false AND mm.id = :id")
	, @NamedQuery(name="getMeetingMembers", query="SELECT mm FROM MeetingMember mm ORDER BY mm.id")
	, @NamedQuery(name="getMeetingMemberIdsByAppointment"
			, query="SELECT mm.id FROM MeetingMember mm WHERE mm.deleted = false AND mm.appointment.id = :id")
})
@Root(name = "meetingmember")
public class MeetingMember extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "meetingMemberId", data = true)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(name = "userid", data = true, required = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "appointment_id", nullable = true)
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private Appointment appointment;

	@Column(name = "appointment_status")
	@Element(data = true, required = false)
	private String appointmentStatus; // status of the appointment denial, acceptance, wait.

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "invitation_id", nullable = true)
	@ForeignKey(enabled = true)
	private Invitation invitation;

	@Column(name = "is_connected_event", nullable = false)
	private boolean connectedEvent;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Invitation getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}

	public boolean isConnectedEvent() {
		return connectedEvent;
	}

	public void setConnectedEvent(boolean connectedEvent) {
		this.connectedEvent = connectedEvent;
	}
}
