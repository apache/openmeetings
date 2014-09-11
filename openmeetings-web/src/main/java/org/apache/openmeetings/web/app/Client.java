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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.protocol.ws.api.registry.IKey;

/**
 * Temporary class, later will be merged with {@link org.apache.openmeetings.db.entity.room.Client}
 * @author solomax
 *
 */
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Right {
		moderator
	}
	private String sessionId;
	private int pageId;
	private long userId;
	private String uid;
	private Set<Right> rights = new HashSet<Right>();

	public Client() {
	}
	
	public Client(String sessionId, IKey key, long userId) {
		this(sessionId, key.hashCode(), userId);
	}
	
	public Client(String sessionId, int pageId, long userId) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pageId;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		if (userId != other.userId)
			return false;
		return true;
	}

	public Set<Right> getRights() {
		return rights;
	}

	public boolean hasRight(Right right) {
		return rights.contains(Right.moderator) ? true : rights.contains(right);
	}
}
