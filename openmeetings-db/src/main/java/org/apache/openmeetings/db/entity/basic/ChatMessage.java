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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "getChatMessageById", query = "SELECT c FROM ChatMessage c WHERE c.id = :id")
	, @NamedQuery(name = "getChatMessages", query = "SELECT c FROM ChatMessage c ORDER BY c.id")
	, @NamedQuery(name = "getGlobalChatMessages", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom IS NULL ORDER BY c.sent DESC")
	, @NamedQuery(name = "getChatMessagesByRoom", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom.id = :roomId"
			+ " AND (true = :all OR (false = :all AND c.needModeration = false)) ORDER BY c.sent DESC")
	, @NamedQuery(name = "getChatMessagesByUser", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NOT NULL AND c.toRoom IS NULL AND "
			+ "(c.fromUser.id = :userId OR c.toUser.id = :userId) ORDER BY c.sent DESC")
	, @NamedQuery(name = "getChatMessagesByUserTime", query = "SELECT c FROM ChatMessage c WHERE c.toUser IS NOT NULL AND c.toRoom IS NULL AND "
			+ "(c.fromUser.id = :userId OR c.toUser.id = :userId) AND c.sent > :date ORDER BY c.sent DESC")
	, @NamedQuery(name = "deleteChatGlobal", query = "DELETE FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom IS NULL")
	, @NamedQuery(name = "deleteChatRoom", query = "DELETE FROM ChatMessage c WHERE c.toUser IS NULL AND c.toRoom.id = :roomId")
	, @NamedQuery(name = "deleteChatUser", query = "DELETE FROM ChatMessage c WHERE c.toRoom IS NULL AND c.toUser.id = :userId")
	, @NamedQuery(name = "purgeChatUserName", query = "UPDATE ChatMessage c SET c.fromName = :purged WHERE c.fromUser.id = :userId")
})
@Table(name = "chat")
@Root(name = "ChatMessage")
public class ChatMessage implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(name = "id", data = true)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "from_user_id")
	@Element(name = "fromUserId", data = true, required = false)
	@ForeignKey(enabled = true)
	private User fromUser;

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

	@Column(name = "message")
	@Lob
	@Element(name = "message", data = true, required = false)
	private String message;

	@Column(name = "sent")
	@Element(name = "sent", data = true, required = false)
	private Date sent;

	@Column(name = "need_moderation", nullable = false)
	@Element(name = "needModeration", data = true, required = false)
	private boolean needModeration;

	@Column(name = "from_name")
	@Element(name = "from_name", data = true, required = false)
	private String fromName; // this is required for users with no first/last name specified

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
}
