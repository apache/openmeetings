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
package org.apache.openmeetings.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;

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
import org.apache.openmeetings.persistence.beans.room.Room;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getPrivateMessages", query = "select c from PrivateMessage c"),
	@NamedQuery(name = "getPrivateMessagesById", query = "select c from PrivateMessage c " +
			"where c.privateMessageId = :privateMessageId "),
	@NamedQuery(name = "getNumberMessages", query = "select COUNT(c.privateMessageId) from PrivateMessage c " +
			"where c.to.user_id = :toUserId " +
			"AND c.isTrash = :isTrash " +
			"AND c.owner.user_id = :toUserId " +
			"AND c.isRead = :isRead " +
			"AND c.privateMessageFolderId = :privateMessageFolderId "),
	@NamedQuery(name = "updatePrivateMessagesToTrash", query = "UPDATE PrivateMessage c " +
			"SET c.isTrash = :isTrash,c.privateMessageFolderId = :privateMessageFolderId " +
			"where c.privateMessageId IN (:privateMessageIds) "),
	@NamedQuery(name = "updatePrivateMessagesReadStatus", query = "UPDATE PrivateMessage c " +
			"SET c.isRead = :isRead " +
			"where c.privateMessageId IN (:privateMessageIds) "),
	@NamedQuery(name = "moveMailsToFolder", query = "UPDATE PrivateMessage c " +
			"SET c.privateMessageFolderId = :privateMessageFolderId, c.isTrash = false " +
			"where c.privateMessageId IN (:privateMessageIds) "),
	@NamedQuery(name = "deletePrivateMessages", query = "DELETE FROM PrivateMessage c " +
			"where c.privateMessageId IN (:privateMessageIds) "),
	@NamedQuery(name = "getPrivateMessagesByRoom", query = "select c from PrivateMessage c " +
			"where c.room.rooms_id = :roomId ")
})
@Table(name = "private_message")
@Root(name="privatemessage")
public class PrivateMessage implements Serializable {
	private static final long serialVersionUID = 7541117437029707792L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Element(data=true)
	private long privateMessageId;
	
	@Column(name="subject")
	@Element(data=true, required=false)
	private String subject;
	
	@Lob
	@Column(name="message")
	@Element(data=true, required=false)
	private String message;
	
	@Column(name="email")
	private String email;
	
	@Column(name="inserted")
	@Element(data=true)
	private Date inserted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="from_id")
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private User from;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="to_id")
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private User to;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_id")
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private User owner;
	
		
	@Column(name="booked_room")
	@Element(data=true)
	private Boolean bookedRoom;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="rooms_id")
	@ForeignKey(enabled = true)
	@Element(data=true, required=false)
	private Room room;
	
	@Column(name="is_read")
	@Element(data=true)
	private Boolean isRead;
	
	@Column(name="is_trash")
	@Element(data=true)
	private Boolean isTrash;
	
	@Column(name="parent_message_id")
	@Element(data=true)
	private Long parentMessage;
	
	@Column(name="private_message_folder_id")
	@Element(data=true)
	private Long privateMessageFolderId;
	
	@Column(name="is_contact_request")
	@Element(data=true)
	private Boolean isContactRequest;
	
	@Column(name="user_contact_id")
	@Element(data=true)
	private Long userContactId;

	public long getPrivateMessageId() {
		return privateMessageId;
	}
	public void setPrivateMessageId(long privateMessageId) {
		this.privateMessageId = privateMessageId;
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
	
	public Boolean getBookedRoom() {
		return bookedRoom;
	}
	public void setBookedRoom(Boolean bookedRoom) {
		this.bookedRoom = bookedRoom;
	}

	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

	public Long getParentMessage() {
		return parentMessage;
	}
	public void setParentMessage(Long parentMessage) {
		this.parentMessage = parentMessage;
	}
	
	public Boolean getIsTrash() {
		return isTrash;
	}
	public void setIsTrash(Boolean isTrash) {
		this.isTrash = isTrash;
	}
	
	public Long getPrivateMessageFolderId() {
		return privateMessageFolderId;
	}
	public void setPrivateMessageFolderId(Long privateMessageFolderId) {
		this.privateMessageFolderId = privateMessageFolderId;
	}
	
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	public Boolean getIsContactRequest() {
		return isContactRequest;
	}
	public void setIsContactRequest(Boolean isContactRequest) {
		this.isContactRequest = isContactRequest;
	}
	
	public Long getUserContactId() {
		return userContactId;
	}
	public void setUserContactId(Long userContactId) {
		this.userContactId = userContactId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
