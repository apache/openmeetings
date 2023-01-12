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
package org.apache.openmeetings.db.bind.adapter;

import java.util.Locale;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.wicket.util.string.Strings;

public class RoomElementAdapter extends XmlAdapter<String, Room.RoomElement> {

	@Override
	public String marshal(Room.RoomElement v) throws Exception {
		return v.name().toUpperCase(Locale.ROOT);
	}

	@Override
	public Room.RoomElement unmarshal(String v) throws Exception {
		if ("TopBar".equals(v)) {
			return Room.RoomElement.TOP_BAR;
		}
		if ("ActionMenu".equals(v)) {
			return Room.RoomElement.ACTION_MENU;
		}
		if ("PollMenu".equals(v)) {
			return Room.RoomElement.POLL_MENU;
		}
		if ("ScreenSharing".equals(v)) {
			return Room.RoomElement.SCREEN_SHARING;
		}
		if ("MicrophoneStatus".equals(v)) {
			return Room.RoomElement.MICROPHONE_STATUS;
		}
		if ("UserCount".equals(v)) {
			return Room.RoomElement.USER_COUNT;
		}
		return Strings.isEmpty(v) ? null : Room.RoomElement.valueOf(v.toUpperCase(Locale.ROOT));
	}
}
