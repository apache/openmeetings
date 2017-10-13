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
package org.apache.openmeetings.db.entity.user;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.room.Room;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getPrivateMessages", query = "SELECT c FROM PrivateMessage c ORDER BY c.id"),
	@NamedQuery(name = "getPrivateMessageById", query = "SELECT c FROM PrivateMessage c WHERE c.id = :id "),
	@NamedQuery(name = "updatePrivateMessagesReadStatus", query = "UPDATE PrivateMessage c SET c.isRead = :isRead WHERE c.id IN (:ids) "),
	@NamedQuery(name = "moveMailsToFolder", query = "UPDATE PrivateMessage c SET c.folderId = :folderId WHERE c.id IN (:ids) "),
	@NamedQuery(name = "deletePrivateMessages", query = "DELETE FROM PrivateMessage c WHERE c.id IN (:ids) "),
	@NamedQuery(name = "getPrivateMessagesByRoom", query = "SELECT c FROM PrivateMessage c WHERE c.room.id = :roomId ")
})
@Table(name = "private_message")
@Root(name = "privatemessage")
public class PrivateMessage implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	public static final Long INBOX_FOLDER_ID = Long.valueOf(0);
	public static final Long SENT_FOLDER_ID = Long.valueOf(-1);
	public static final Long TRASH_FOLDER_ID = Long.valueOf(-2);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, name = "privateMessageId")
	private Long id;

	@Column(name = "subject")
	@Element(data = true, required = false)
	private String subject;

	@Lob
	@Column(name = "message")
	@Element(data = true, required = false)
	private String message;

	@Column(name = "inserted")
	@Element(data = true)
	private Date inserted;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "from_id")
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private User from;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "to_id")
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private User to;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "owner_id")
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private User owner;

	@Column(name = "booked_room", nullable = false)
	@Element(data = true)
	private boolean bookedRoom;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id")
	@ForeignKey(enabled = true)
	@Element(data = true, required = false)
	private Room room;

	@Column(name = "is_read", nullable = false)
	@Element(data = true)
	private boolean isRead;

	@Column(name = "private_message_folder_id")
	@Element(data = true, name = "privateMessageFolderId", required = false)
	private Long folderId;

	@Column(name = "is_contact_request", nullable = false)
	@Element(data = true)
	private boolean isContactRequest;

	@Column(name = "user_contact_id")
	@Element(data = true, required = false)
	private Long userContactId;

	public PrivateMessage() {
		//def constructor
	}

	public PrivateMessage(PrivateMessage copy) {
		subject = copy.subject;
		message = copy.message;
		from = copy.from;
		to = copy.to;
		owner = copy.owner;
		bookedRoom = copy.bookedRoom;
		room = copy.room;
		isRead = copy.isRead;
		folderId = copy.folderId;
		isContactRequest = copy.isContactRequest;
		userContactId = copy.userContactId;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isBookedRoom() {
		return bookedRoom;
	}

	public void setBookedRoom(boolean bookedRoom) {
		this.bookedRoom = bookedRoom;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean getIsContactRequest() {
		return isContactRequest;
	}

	public void setIsContactRequest(boolean isContactRequest) {
		this.isContactRequest = isContactRequest;
	}

	public Long getUserContactId() {
		return userContactId;
	}

	public void setUserContactId(Long userContactId) {
		this.userContactId = userContactId;
	}
}
