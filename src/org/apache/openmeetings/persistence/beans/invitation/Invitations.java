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
package org.apache.openmeetings.persistence.beans.invitation;

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
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.User;

@Entity
@Table(name = "invitations")
public class Invitations implements Serializable {
	private static final long serialVersionUID = 1153321347974705506L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long invitations_id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invitedBy", nullable = true)
	@ForeignKey(enabled = true)
	private User invitedBy;
	
	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Column(name = "deleted")
	private boolean deleted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roomid", nullable = true)
	@ForeignKey(enabled = true)
	private Room room;

	// the hash for the link
	@Column(name = "hash")
	private String hash;
	
	@Column(name = "invitedname")
	private String invitedname;
	
	@Column(name = "invitedEMail")
	private String invitedEMail;
	
	@Column(name = "ispasswordprotected")
	private Boolean isPasswordProtected;
	
	@Column(name = "invitationpass")
	private String invitationpass;

	// this is necessary as a room can be shared between multiple domains
	// is either *public* or an organisation_ID
	@Column(name = "conferencedomain")
	private String conferencedomain;

	// Invitations by Time are only valid between the validFrom validTo
	// TimeStamp
	@Column(name = "isValidByTime")
	private Boolean isValidByTime = false;
	
	@Column(name = "validFrom")
	private Date validFrom;
	
	@Column(name = "validTo")
	private Date validTo;
	// An invitation which is canBeUsedOnlyOneTime = true can be only used
	// one-time
	@Column(name = "canBeUsedOnlyOneTime")
	private Boolean canBeUsedOnlyOneTime = false;
	
	@Column(name = "invitationWasUsed")
	private Boolean invitationWasUsed = false;
	

	@Column(name = "appointment_id")
	private Long appointmentId;

	// BaseuRL defined at creation of InvitationLink - used for chronological
	// reminders on serverside (updates)
	@Column(name = "baseUrl")
	private String baseUrl;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "omtimezoneId", insertable = true, updatable = true)
	@ForeignKey(enabled = true)
	private OmTimeZone omTimeZone; // In UTC +/- hours
	
	private boolean allowEntry = true;

	public Invitations() {
		super();
	}

	public Long getInvitations_id() {
		return invitations_id;
	}

	public void setInvitations_id(Long invitations_id) {
		this.invitations_id = invitations_id;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public User getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(User invitedBy) {
		this.invitedBy = invitedBy;
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getInvitedname() {
		return invitedname;
	}

	public void setInvitedname(String invitedname) {
		this.invitedname = invitedname;
	}

	public String getInvitedEMail() {
		return invitedEMail;
	}

	public void setInvitedEMail(String invitedEMail) {
		this.invitedEMail = invitedEMail;
	}

	public Boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}

	public void setIsPasswordProtected(Boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}

	public String getInvitationpass() {
		return invitationpass;
	}

	public void setInvitationpass(String invitationpass) {
		this.invitationpass = invitationpass;
	}

	public String getConferencedomain() {
		return conferencedomain;
	}

	public void setConferencedomain(String conferencedomain) {
		this.conferencedomain = conferencedomain;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Boolean getCanBeUsedOnlyOneTime() {
		return canBeUsedOnlyOneTime;
	}

	public void setCanBeUsedOnlyOneTime(Boolean canBeUsedOnlyOneTime) {
		this.canBeUsedOnlyOneTime = canBeUsedOnlyOneTime;
	}

	public Boolean getInvitationWasUsed() {
		return invitationWasUsed;
	}

	public void setInvitationWasUsed(Boolean invitationWasUsed) {
		this.invitationWasUsed = invitationWasUsed;
	}

	public Boolean getIsValidByTime() {
		return isValidByTime;
	}

	public void setIsValidByTime(Boolean isValidByTime) {
		this.isValidByTime = isValidByTime;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public boolean isAllowEntry() {
		return allowEntry;
	}

	public void setAllowEntry(boolean allowEntry) {
		this.allowEntry = allowEntry;
	}

	public OmTimeZone getOmTimeZone() {
		return omTimeZone;
	}

	public void setOmTimeZone(OmTimeZone omTimeZone) {
		this.omTimeZone = omTimeZone;
	}
}
