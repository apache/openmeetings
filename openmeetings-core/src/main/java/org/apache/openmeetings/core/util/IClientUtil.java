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
package org.apache.openmeetings.core.util;

import org.apache.openmeetings.db.entity.room.StreamClient;
import org.red5.server.api.IClient;
import org.red5.server.api.scope.IScope;

public class IClientUtil {
	private enum ConAttrs {
		omId
		, sharing
		, recordingId
	}

	public static void init(IClient client, String uid, boolean sharing) {
		client.setAttribute(ConAttrs.omId.name(), uid);
		client.setAttribute(ConAttrs.sharing.name(), sharing);
	}

	/**
	 * Id of {@link StreamClient} for this IConnection
	 *
	 * @param client - {@link IClient} to get if from
	 * @return - Id of {@link StreamClient} for this IConnection, or <code>null</code>
	 */
	public static String getId(IClient client) {
		Object o = client.getAttribute(ConAttrs.omId.name());
		return o instanceof String ? (String)o : null;
	}

	public static boolean isSharing(IClient client) {
		return Boolean.TRUE.equals(client.getAttribute(ConAttrs.sharing.name()));
	}

	public static Long getRecordingId(IScope scope) {
		return (Long)scope.getAttribute(ConAttrs.recordingId.name());
	}

	public static void setRecordingId(IScope scope, Long recordingId) {
		scope.setAttribute(ConAttrs.recordingId.name(), recordingId);
	}
}
