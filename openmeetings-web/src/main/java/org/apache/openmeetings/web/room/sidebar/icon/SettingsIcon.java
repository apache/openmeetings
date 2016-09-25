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

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.room.RoomPanel;

public class SettingsIcon extends ClientIcon {
	private static final long serialVersionUID = 1L;
	
	public SettingsIcon(String id, Client client, RoomPanel room) {
		super(id, client, room);
		mainCssClass = "settings ";
	}

	@Override
	protected String getTitle() {
		return getString("306");
	}

	@Override
	protected boolean isClickable() {
		return true;
	}

	@Override
	protected String getScript() {
		return String.format("$('#lzapp').showAvSettings(%s);", Room.Type.interview == room.getRoom().getType());
	}
}
