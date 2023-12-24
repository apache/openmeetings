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
package org.apache.openmeetings.db.entity.log;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@Table(name = "conference_log")
@NamedQuery(name = "getLogRecentRooms", query = "SELECT c FROM ConferenceLog c "
	+ "WHERE c.roomId IS NOT NULL AND c.type = :roomEnter and c.userId = :userId ORDER BY c.inserted DESC")
@NamedQuery(name = "clearLogUserIpByUser", query = "UPDATE ConferenceLog c SET c.userip = NULL "
		+ "WHERE c.userip IS NOT NULL AND c.userId = :userId")
@NamedQuery(name = "clearLogUserIp", query = "UPDATE ConferenceLog c SET c.userip = NULL "
		+ "WHERE c.userip IS NOT NULL AND c.inserted < :date")
public class ConferenceLog implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	public enum Type {
		CLIENT_CONNECT
		, ROOM_ENTER
		, ROOM_LEAVE
		, CLIENT_DISCONNECT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;

	@Column(name = "inserted")
	private Date inserted;

	@Column(name = "insertedby")
	private long insertedby;

	// NULL means its a Guest/Invited User
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "streamid")
	private String streamid;

	@Column(name = "room_id")
	private Long roomId;

	@Column(name = "userip")
	private String userip;

	@Column(name = "scopename")
	private String scopeName;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public long getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(long insertedby) {
		this.insertedby = insertedby;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getStreamId() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
}
