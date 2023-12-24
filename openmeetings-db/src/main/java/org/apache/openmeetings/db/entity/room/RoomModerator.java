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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "getRoomModeratorById", query = "select c from RoomModerator as c where c.id = :id")
@NamedQuery(name = "getRoomModeratorsByIds", query = "select c from RoomModerator as c where c.id IN :ids")
@NamedQuery(name = "getRoomModeratorByRoomId", query = "select c from RoomModerator as c where c.roomId = :roomId AND c.deleted = false")
@NamedQuery(name = "getRoomModeratorByUserAndRoomId", query = "select c from RoomModerator as c "
		+ "where c.roomId = :roomId AND c.deleted = false AND c.user.id = :userId")
@Table(name = "room_moderator")
@XmlRootElement(name = "room_moderator")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomModerator extends HistoricalEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@Column(name = "roomId")
	@XmlTransient
	private Long roomId;

	@Column(name = "is_supermoderator", nullable = false)
	@XmlElement(name = "is_supermoderator", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean superModerator;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "user_id", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User user;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSuperModerator() {
		return superModerator;
	}

	public void setSuperModerator(boolean superModerator) {
		this.superModerator = superModerator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
}
