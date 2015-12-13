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
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Entity
@Table(name = "meeting_members")
@Root(name="meetingmember")
public class MeetingMember implements Serializable {
	private static final long serialVersionUID = -3864571325368787524L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private Long meetingMemberId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(name="userid", data=true, required=false)
	private User userid;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="appointment_id", nullable=true)
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private Appointment appointment;
	
	@Column(name="firstname")
	@Element(data=true, required=false)
	private String firstname;
	
	@Column(name="lastname")
	@Element(data=true, required=false)
	private String lastname;
	
	@Column(name="member_status")
	@Element(data=true, required=false)
	private String memberStatus; // internal, external.
	
	@Column(name="appointment_status")
	@Element(data=true, required=false)
	private String appointmentStatus; //status of the appointment denial, acceptance, wait.
	
	@Column(name="email")
	@Element(data=true, required=false)
	private String email;
	
	@Column(name="phone")
	private String phone;
			
	@Column(name="starttime")
	private Date starttime;
	
	@Column(name="updatetime")
	private Date updatetime;
	
	@Column(name="deleted")
	@Element(data=true)
	private boolean deleted;
	
	@Column(name="invitor")
	@Element(data=true)
	private Boolean invitor;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="invitation", nullable=true, insertable=false)
	@ForeignKey(enabled = true)
	private Invitations invitation;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="omtimezoneId", nullable=true, insertable=true)
	@ForeignKey(enabled = true)
	private OmTimeZone omTimeZone;
	
	@Column(name="is_connected_event")
	private Boolean isConnectedEvent;
	
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
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getMemberStatus() {
		return memberStatus;
	}
	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
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
	
	public Boolean getInvitor() {
		return invitor;
	}
	public void setInvitor(Boolean invitor) {
		this.invitor = invitor;
	}

	public OmTimeZone getOmTimeZone() {
		return omTimeZone;
	}
	public void setOmTimeZone(OmTimeZone omTimeZone) {
		this.omTimeZone = omTimeZone;
	}
	
	public Boolean getIsConnectedEvent() {
		return isConnectedEvent;
	}
	public void setIsConnectedEvent(Boolean isConnectedEvent) {
		this.isConnectedEvent = isConnectedEvent;
	}
	
	
}
