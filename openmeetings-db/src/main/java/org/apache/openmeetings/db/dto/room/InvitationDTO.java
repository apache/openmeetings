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
package org.apache.openmeetings.db.dto.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InvitationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationDTO.class, getWebAppRootKey());
	private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private String email;
	private String firstname;
	private String lastname;
	private String message;
	private String subject;
	private Long roomId;
	private boolean passwordProtected;
	private String password;
	private Valid valid;
	private String validFrom;
	private String validTo;
	private long languageId;

	public InvitationDTO() {
		//def constructor
	}

	public String getEmail() {
		return email;
	}

	public InvitationDTO setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFirstname() {
		return firstname;
	}

	public InvitationDTO setFirstname(String firstname) {
		this.firstname = firstname;
		return this;
	}

	public String getLastname() {
		return lastname;
	}

	public InvitationDTO setLastname(String lastname) {
		this.lastname = lastname;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public InvitationDTO setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public InvitationDTO setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public Long getRoomId() {
		return roomId;
	}

	public InvitationDTO setRoomId(Long roomId) {
		this.roomId = roomId;
		return this;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public InvitationDTO setPasswordProtected(boolean passwordProtected) {
		this.passwordProtected = passwordProtected;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public InvitationDTO setPassword(String password) {
		this.password = password;
		return this;
	}

	public Valid getValid() {
		return valid;
	}

	public InvitationDTO setValid(Valid valid) {
		this.valid = valid;
		return this;
	}

	/**
	 * Method to get valid from date-time in format YYYY-MM-dd HH:mm:ss
	 *
	 * @return date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public String getValidFrom() {
		return validFrom;
	}

	/**
	 * Method to set valid from date-time in format YYYY-MM-dd HH:mm:ss
	 *
	 * @param validFrom
	 *            date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public InvitationDTO setValidFrom(String validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	/**
	 * Method to get valid to date-time in format YYYY-MM-dd HH:mm:ss
	 *
	 * @return date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public String getValidTo() {
		return validTo;
	}

	/**
	 * Method to set valid to date-time in format YYYY-MM-dd HH:mm:ss
	 *
	 * @param validTo
	 *            date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public InvitationDTO setValidTo(String validTo) {
		this.validTo = validTo;
		return this;
	}

	public long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	public Invitation get(Long userId, UserDao userDao, RoomDao roomDao) {
		Invitation i = new Invitation();
		i.setHash(UUID.randomUUID().toString());
		i.setPasswordProtected(passwordProtected);
		if (passwordProtected) {
			i.setPassword(CryptProvider.get().hash(password));
		}

		i.setUsed(false);
		i.setValid(valid);

		try {
			// valid period of Invitation
			switch (valid) {
				case Period:
					i.setValidFrom(new Date(SDF.parse(validFrom).getTime() - (5 * 60 * 1000)));
					i.setValidTo(SDF.parse(validTo));
					break;
				case Endless:
				case OneTime:
				default:
					break;
			}
		} catch (ParseException e) {
			log.error("Unexpected error while creating invitation", e);
			throw new RuntimeException(e);
		}

		i.setDeleted(false);

		i.setInvitedBy(userDao.get(userId));
		i.setInvitee(userDao.getContact(email, firstname, lastname, userId));
		if (Type.contact == i.getInvitee().getType()) {
			i.getInvitee().setLanguageId(languageId);
		}
		i.setRoom(roomDao.get(roomId));
		i.setInserted(new Date());
		i.setAppointment(null);
		return i;
	}

	public static InvitationDTO fromString(String s) {
		return get(new JSONObject(s));
	}

	public static InvitationDTO get(JSONObject o) {
		if (o == null) {
			return null;
		}
		InvitationDTO i = new InvitationDTO();
		i.firstname = o.optString("firstname");
		i.lastname = o.optString("lastname");
		i.email = o.optString("email");
		i.password = o.optString("password");
		i.passwordProtected = o.optBoolean("passwordProtected", false);
		i.subject = o.optString("subject");
		i.roomId = o.getLong("roomId");
		i.message = o.optString("message");
		i.valid = Valid.valueOf(o.optString("valid", Valid.Period.name()));
		i.validFrom = o.optString("validFrom");
		i.validTo = o.optString("validTo");
		return i;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
