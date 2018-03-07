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
package org.apache.openmeetings.web.room.sidebar.icon;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.room.Room;

public class UserSpeaksIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;

	public UserSpeaksIcon(String id, String uid) {
		super(id, uid);
		mainCssClass = "audio-activity ";
	}

	private boolean isActive() {
		Client c = getClient();
		return c != null && (c.hasActivity(Activity.broadcastA) && roomHasRight(Room.Right.exclusive));
	}

	@Override
	protected String getTitle() {
		return getString(isActive() ? "372" : "371");
	}

	@Override
	protected String getAlign() {
		return "";
	}

	@Override
	protected boolean isClickable() {
		return isActive();
	}

	@Override
	protected String getScript() {
		return String.format("VideoManager.clickExclusive('%s');", uid);
	}
}
