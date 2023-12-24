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

import static java.util.UUID.randomUUID;

import jakarta.annotation.Nonnull;

/**
 * Temporary class for test recording, will be unified
 *
 */
public class WsClient implements IWsClient {
	private static final long serialVersionUID = 1L;

	private final String sessionId;
	private final int pageId;
	private final String uid;

	public WsClient(String sessionId, int pageId) {
		this.sessionId = sessionId;
		this.pageId = pageId;
		uid = randomUUID().toString();
	}
	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int getPageId() {
		return pageId;
	}

	@Override
	@Nonnull
	public String getUid() {
		return uid;
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
		if (!(obj instanceof WsClient)) {
			return false;
		}
		WsClient other = (WsClient) obj;
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
		return "Client [uid=" + uid + ", sessionId=" + sessionId + ", pageId=" + pageId + "]";
	}
}
