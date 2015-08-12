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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.crypt.MD5;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class InvitationDTO {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationDTO.class, webAppRootKey);
	private static ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		};
	};
	
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
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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
	public Valid getValid() {
		return valid;
	}
	public void setValid(Valid valid) {
		this.valid = valid;
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
	 * @param validFrom date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
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
	 * @param validFrom date-time in format YYYY-MM-dd HH:mm:ss
	 */
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public long getLanguageId() {
		return languageId;
	}
	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}
	
	public Invitation get(Long userId, UserDao userDao, RoomDao roomDao) {
		Invitation i = new Invitation();
		String hashRaw = "HASH" + (System.currentTimeMillis());
		try {
			i.setHash(MD5.do_checksum(hashRaw));
		} catch (NoSuchAlgorithmException e) {
			//FIXME TODO need to throw other exception
			log.error("Unexpected error while creating invitation", e);
			throw new RuntimeException(e);
		}
		i.setPasswordProtected(passwordProtected);
		if (passwordProtected) {
			i.setPassword(ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(password));
		}

		i.setUsed(false);
		i.setValid(valid);
		
		try {
			// valid period of Invitation
			switch (valid) {
				case Period:
					i.setValidFrom(new Date(SDF.get().parse(validFrom).getTime() - (5 * 60 * 1000)));
					i.setValidTo(SDF.get().parse(validTo));
					break;
				case Endless:
				case OneTime:
				default:
					break;
			}
		} catch (ParseException e) {
			//FIXME TODO need to throw other exception
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
}
