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
package org.apache.openmeetings.db.dto.room;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.room.Room;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckDto {
	private final long roomId;
	private final Room.Type roomType;
	private final boolean audioOnly;
	private final Set<Activity> activities = new HashSet<>();

	public CheckDto(Client c) {
		roomId = c.getRoom().getId();
		roomType = c.getRoom().getType();
		audioOnly = c.getRoom().isAudioOnly();
		if (c.hasActivity(Activity.broadcastA)) {
			activities.add(Activity.broadcastA);
		}
		if (c.hasActivity(Activity.broadcastV)) {
			activities.add(Activity.broadcastV);
		}
	}

	public long getRoomId() {
		return roomId;
	}

	public Room.Type getRoomType() {
		return roomType;
	}

	public boolean isAudioOnly() {
		return audioOnly;
	}

	public Set<Activity> getActivities() {
		return activities;
	}
}
