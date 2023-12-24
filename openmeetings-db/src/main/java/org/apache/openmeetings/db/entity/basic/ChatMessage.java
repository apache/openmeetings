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
package org.apache.openmeetings.db.entity.basic;

import static org.apache.openmeetings.db.bind.Constants.CHAT_NODE;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "getChatMessageById", query = "SELECT c FROM ChatMessage c WHERE c.id = :id")
@NamedQuery(name = "getChatMessages", query = "SELECT c FROM ChatMessage c ORDER BY c.id")
@NamedQuery(name = "getGlobalChatMessages", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom IS NULL ORDER BY c.sent DESC")
@NamedQuery(name = "getChatMessagesByRoom", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom.id = :roomId"
		+ " AND (true = :all OR (false = :all AND c.needModeration = false)) ORDER BY c.sent DESC")
@NamedQuery(name = "getChatMessagesByUser", query = "SELECT c FROM ChatMessage c WHERE "
		+ "c.toUser IS NOT NULL AND c.toRoom IS NULL AND "
		+ "(c.fromUser.id = :userId OR c.toUser.id = :userId) ORDER BY c.sent DESC")
@NamedQuery(name = "getChatMessagesByUserTime", query = "SELECT c FROM ChatMessage c WHERE "
		+ "c.toUser IS NOT NULL AND c.toRoom IS NULL AND c.status <> :status AND "
		+ "(c.fromUser.id = :userId OR c.toUser.id = :userId) AND c.sent > :date ORDER BY c.sent DESC")
@NamedQuery(name = "chatCloseMessagesByUser", query = "UPDATE ChatMessage c SET c.status = :status WHERE "
		+ "c.toUser IS NOT NULL AND c.toRoom IS NULL AND c.status <> :status AND "
		+ "(c.fromUser.id = :userId OR c.toUser.id = :userId)")
@NamedQuery(name = "deleteChatGlobal", query = "DELETE FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom IS NULL")
@NamedQuery(name = "deleteChatRoom", query = "DELETE FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom.id = :roomId")
@NamedQuery(name = "deleteChatUser", query = "DELETE FROM ChatMessage c WHERE c.toRoom IS NULL AND c.toUser.id = :userId")
@NamedQuery(name = "purgeChatUserName", query = "UPDATE ChatMessage c SET c.fromName = :purged WHERE c.fromUser.id = :userId")
@Table(name = "chat")
@XmlRootElement(name = CHAT_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class ChatMessage implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	public enum Status {
		OPEN
		, CLOSED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlElement(name = "id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "from_user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "fromUserId", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User fromUser;

	@ManyToOne
	@JoinColumn(name = "to_room_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "toRoomId", required = false)
	@XmlJavaTypeAdapter(RoomAdapter.class)
	private Room toRoom;

	@ManyToOne
	@JoinColumn(name = "to_user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "toUserId", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User toUser;

	@Column(name = "message")
	@Lob
	@XmlElement(name = "message", required = false)
	private String message;

	@Column(name = "sent")
	@XmlElement(name = "sent", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date sent;

	@Column(name = "need_moderation", nullable = false)
	@XmlElement(name = "needModeration", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean needModeration;

	@Column(name = "from_name")
	@XmlElement(name = "from_name", required = false)
	private String fromName; // this is required for users with no first/last name specified

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "status", required = false)
	private Status status;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
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

	public boolean isNeedModeration() {
		return needModeration;
	}

	public void setNeedModeration(boolean needModeration) {
		this.needModeration = needModeration;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
