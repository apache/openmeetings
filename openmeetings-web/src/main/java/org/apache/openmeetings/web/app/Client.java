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
package org.apache.openmeetings.web.app;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.wicket.protocol.ws.api.registry.IKey;

/**
 * Temporary class, later will be merged with {@link org.apache.openmeetings.db.entity.room.Client}
 * @author solomax
 *
 */
public class Client implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	public enum Right {
		superModerator
		, moderator
		, whiteBoard
		, share
		, remoteControl
		, audio
		, video
		, mute
		, exclusive
	}
	public enum Activity {
		broadcast
		, share
		, record
		, publish
		, muted
		, exclusive
	}
	private final String sessionId;
	private int pageId;
	private final Long userId;
	private Long roomId;
	private final String uid;
	private final Set<Right> rights = new HashSet<>();
	private final Set<Activity> activities = new HashSet<>();
	private final Date connectedSince;

	public Client(String sessionId, Long userId) {
		this(sessionId, 0, userId);
	}
	
	public Client(String sessionId, int pageId, Long userId) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		this.userId = userId;
		this.connectedSince = new Date();
		uid = UUID.randomUUID().toString();
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getPageId() {
		return pageId;
	}

	public Client setPageId(IKey key) {
		this.pageId = key.hashCode();
		return this;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUid() {
		return uid;
	}

	public Set<Right> getRights() {
		return rights;
	}

	public boolean hasRight(Right right) {
		return rights.contains(Right.superModerator) || rights.contains(Right.moderator) ? true : rights.contains(right);
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public boolean hasActivity(Activity activity) {
		return activities.contains(activity);
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
	}

	public Long getRoomId() {
		return roomId;
	}

	public Client setRoomId(Long roomId) {
		this.roomId = roomId;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pageId;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (pageId != other.pageId)
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
}
