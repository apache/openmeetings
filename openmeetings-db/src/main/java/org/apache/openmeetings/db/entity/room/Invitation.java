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
package org.apache.openmeetings.db.entity.room;

import java.util.Date;
import java.util.stream.Stream;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "getInvitationbyId", query = "SELECT i FROM Invitation i WHERE i.deleted = false AND i.id = :id")
@NamedQuery(name = "getInvitationByHashCode", query = "SELECT i FROM Invitation i where i.hash LIKE :hashCode AND i.deleted = false")
@NamedQuery(name = "getInvitationByAppointment", query = "SELECT i FROM Invitation i WHERE i.appointment.id = :appointmentId")
@Table(name = "invitation", indexes = {
		@Index(name = "inv_hash_idx", columnList = "hash", unique = true)
})
public class Invitation extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	public enum MessageType {
		CREATE
		, UPDATE
		, CANCEL
	}

	public enum Valid {
		ONE_TIME(3)
		, PERIOD(2)
		, ENDLESS(1);
		private final int code;

		private Valid(int code) {
			this.code = code;
		}

		public static Valid fromInt(int valid) {
			return Stream.of(Valid.values())
					.filter(v -> v.code == valid)
					.findAny()
					.orElse(ONE_TIME);
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invited_by", nullable = true)
	@ForeignKey(enabled = true)
	private User invitedBy;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id", nullable = true)
	@ForeignKey(enabled = true)
	private Room room;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recording_id", nullable = true)
	@ForeignKey(enabled = true)
	private Recording recording;

	// the hash for the link
	@Column(name = "hash") // unique via index
	private String hash;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "invitee_id", nullable = true)
	@ForeignKey(enabled = true)
	private User invitee;

	@Column(name = "password_protected", nullable = false)
	private boolean passwordProtected;

	@Column(name = "password", length = 1024)
	private String password;

	// Invitations by Time are only valid between the validFrom validTo
	// TimeStamp
	@Column(name = "valid")
	@Enumerated(EnumType.STRING)
	private Valid valid = Valid.PERIOD;

	@Column(name = "valid_from")
	private Date validFrom;

	@Column(name = "valid_to")
	private Date validTo;

	@Column(name = "was_used", nullable = false)
	private boolean used;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "appointment_id", nullable = true)
	@ForeignKey(enabled = true)
	private Appointment appointment;

	//variable used in Flash
	@Transient
	private boolean allowEntry = true;

	public Invitation() {
		//def constructor
	}

	public Invitation(Invitation i) {
		id = i.id;
		invitedBy = i.invitedBy;
		setInserted(i.getInserted());
		setUpdated(i.getUpdated());
		setDeleted(i.isDeleted());
		room = i.room;
		recording = i.recording;
		hash = i.hash;
		invitee = i.invitee;
		passwordProtected = i.passwordProtected;
		password = i.password;
		valid = i.valid;
		validFrom = i.validFrom;
		validTo = i.validTo;
		used = i.used;
		appointment = i.appointment;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Recording getRecording() {
		return recording;
	}

	public void setRecording(Recording recording) {
		this.recording = recording;
	}

	public User getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(User invitedBy) {
		this.invitedBy = invitedBy;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public User getInvitee() {
		return invitee;
	}

	public void setInvitee(User invitee) {
		this.invitee = invitee;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(boolean passwordProtected) {
		this.passwordProtected = passwordProtected;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Valid getValid() {
		return valid;
	}

	public void setValid(Valid valid) {
		this.valid = valid;
	}

	public boolean isAllowEntry() {
		return allowEntry;
	}

	public void setAllowEntry(boolean allowEntry) {
		this.allowEntry = allowEntry;
	}

	public boolean isOwner(Long userId) {
		return invitedBy != null && invitedBy.getId().equals(userId);
	}
}
