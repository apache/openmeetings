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
package org.apache.openmeetings.persistence.beans.chat;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getChatMessageById", query = "SELECT c FROM ChatMessage c WHERE c.id = :id")
	, @NamedQuery(name = "getChatMessagesByUser", query = "SELECT DISTINCT c FROM ChatMessage c WHERE c.fromUser.user_id = :userId OR c.toUser.user_id = :userId ORDER BY c.sent DESC")
	, @NamedQuery(name = "getGlobalChatMessages", query = "SELECT DISTINCT c FROM ChatMessage c WHERE c.toUser IS NULL ORDER BY c.sent DESC")
})@Table(name = "chat")
@Root(name = "ChatMessage")
public class ChatMessage implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = 4248081997318897605L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "id", data = true)
	private long id;

	@ManyToOne
	@JoinColumn(name = "from_user_id")
	@Element(name = "fromUserId", data = true, required = false)
	@ForeignKey(enabled = true)
	private User fromUser;
	
	// necessary to hold messages from external guests enters by invitation hash
	@Column(name = "from_email")
	@Element(name = "fromEmail", data = true, required = false)
	private String fromEmail;
	
	@Column(name = "from_name")
	@Element(name = "fromName", data = true, required = false)
	private String fromName;
	
	@ManyToOne
	@JoinColumn(name = "to_room_id")
	@Element(name = "toRoomId", data = true, required = false)
	@ForeignKey(enabled = true)
	private Room toRoom;
	
	@ManyToOne
	@JoinColumn(name = "to_user_id")
	@Element(name = "toUserId", data = true, required = false)
	@ForeignKey(enabled = true)
	private User toUser;

	// necessary to hold messages to external guests enters by invitation hash
	@Column(name = "to_email")
	@Element(name = "toEmail", data = true, required = false)
	private String toEmail;

	@Column(name = "to_name")
	@Element(name = "toName", data = true, required = false)
	private String toName;
	
	@Column(name = "message")
	@Element(name = "message", data = true, required = false)
	private String message;
	
	@Column(name = "sent")
	@Element(name = "sent", data = true, required = false)
	private Date sent;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public Room getToRoom() {
		return toRoom;
	}

	public void setToRoom(Room toRoom) {
		this.toRoom = toRoom;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSent() {
		return sent;
	}

	public void setSent(Date sent) {
		this.sent = sent;
	}
}
