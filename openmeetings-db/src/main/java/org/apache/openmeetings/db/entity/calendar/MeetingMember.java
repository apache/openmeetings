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

import static org.apache.openmeetings.db.bind.Constants.MMEMBER_NODE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.AppointmentAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@Table(name = "meeting_member")
@NamedQuery(name="getMeetingMemberById"
		, query="SELECT mm FROM MeetingMember mm WHERE mm.deleted = false AND mm.id = :id")
@NamedQuery(name="getMeetingMembers", query="SELECT mm FROM MeetingMember mm ORDER BY mm.id")
@NamedQuery(name="getMeetingMemberIdsByAppointment"
		, query="SELECT mm.id FROM MeetingMember mm WHERE mm.deleted = false AND mm.appointment.id = :id")
@XmlRootElement(name = MMEMBER_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class MeetingMember extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "meetingMemberId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "user_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "userid", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "appointment_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlElement(name = "appointment", required = false)
	@XmlJavaTypeAdapter(AppointmentAdapter.class)
	private Appointment appointment;

	@Column(name = "appointment_status")
	@XmlElement(name = "appointmentStatus", required = false)
	private String appointmentStatus; // status of the appointment denial, acceptance, wait.

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "invitation_id", nullable = true)
	@ForeignKey(enabled = true)
	@XmlTransient
	private Invitation invitation;

	@Column(name = "is_connected_event", nullable = false)
	@XmlTransient
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
