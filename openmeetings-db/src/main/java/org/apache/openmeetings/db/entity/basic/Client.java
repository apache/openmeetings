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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.protocol.ws.api.registry.IKey;

/**
 * Temporary class, later will be merged with {@link org.apache.openmeetings.db.entity.room.Client}
 * @author solomax
 *
 */
public class Client implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	public enum Activity {
		broadcastA //sends Audio to the room
		, broadcastV //sends Video to the room
		, broadcastAV //sends Audio+Video to the room
		, share
		, record
		, publish //sends A/V to external server
		, muted
		, exclusive
	}
	public enum Pod {
		none, left, right;
	}
	private final String sessionId;
	private int pageId;
	private final User user;
	private Long roomId;
	private final String uid;
	private final String sid;
	private final Set<Right> rights = new HashSet<>();
	private final Set<Activity> activities = new HashSet<>();
	private final Date connectedSince;
	private Pod pod;
	private int cam = -1;
	private int mic = -1;
	private int width = 0;
	private int height = 0;

	public Client(String sessionId, Long userId, UserDao dao) {
		this(sessionId, 0, userId, dao);
	}

	public Client(String sessionId, int pageId, Long userId, UserDao dao) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		this.user = dao.get(userId);
		this.connectedSince = new Date();
		uid = UUID.randomUUID().toString();
		sid = UUID.randomUUID().toString();
	}

	public Client(org.apache.openmeetings.db.entity.room.Client rcl, UserDao dao) {
		this.sessionId = UUID.randomUUID().toString();
		this.pageId = 0;
		this.user = dao.get(rcl.getUserId());
		this.connectedSince = new Date();
		uid = rcl.getPublicSID();
		sid = UUID.randomUUID().toString();
		this.roomId = rcl.getRoomId();
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

	public User getUser() {
		return user;
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getUid() {
		return uid;
	}

	public String getSid() {
		return sid;
	}

	public Set<Right> getRights() {
		return rights;
	}

	public boolean hasRight(Right right) {
		if (Right.superModerator == right) {
			return rights.contains(right);
		}
		return rights.contains(Right.superModerator) || rights.contains(Right.moderator) ? true : rights.contains(right);
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public boolean hasActivity(Activity a) {
		return activities.contains(a);
	}

	public void toggle(Activity a) {
		if (hasActivity(a)) {
			remove(a);
		} else {
			set(a);
		}
	}

	public void set(Activity a) {
		activities.add(a);
		switch (a) {
			case broadcastV:
			case broadcastA:
				if (hasActivity(Activity.broadcastA) && hasActivity(Activity.broadcastV)) {
					activities.add(Activity.broadcastAV);
				}
				break;
			case broadcastAV:
				activities.add(Activity.broadcastA);
				activities.add(Activity.broadcastV);
				break;
			default:
		}
	}

	public void remove(Activity a) {
		activities.remove(a);
		switch (a) {
			case broadcastV:
			case broadcastA:
				activities.remove(Activity.broadcastAV);
				break;
			case broadcastAV:
				activities.remove(Activity.broadcastA);
				activities.remove(Activity.broadcastV);
				break;
			default:
		}
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

	public Pod getPod() {
		return pod;
	}

	public void setPod(Integer i) {
		if (i != null && i.intValue() > 0) {
			this.pod = i.intValue() == 1 ? Pod.left : Pod.right;
		} else {
			this.pod = Pod.none;
		}
	}

	public boolean isCamEnabled() {
		return cam > -1;
	}

	public int getCam() {
		return cam;
	}

	public void setCam(int cam) {
		this.cam = cam;
	}

	public boolean isMicEnabled() {
		return mic > -1;
	}

	public int getMic() {
		return mic;
	}

	public void setMic(int mic) {
		this.mic = mic;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Client)) {
			return false;
		}
		Client other = (Client) obj;
		if (uid == null) {
			if (other.uid != null) {
				return false;
			}
		} else if (!uid.equals(other.uid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Client [uid=" + uid + ", sessionId=" + sessionId + ", pageId=" + pageId + ", userId=" + user.getId() + ", roomId=" + roomId
				+ ", rights=" + rights + ", activities=" + activities + ", connectedSince=" + connectedSince + ", pod = " + pod + "]";
	}
}
