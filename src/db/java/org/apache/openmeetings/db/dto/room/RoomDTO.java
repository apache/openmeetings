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

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomType;

public class RoomDTO {
	private Long id;
	private String name;
	private String comment;
	private RoomType roomtype;
	private Long numberOfPartizipants = new Long(4);
	private boolean appointment;
	private String confno;
	
	public RoomDTO(Room r) {
		id = r.getRooms_id();
		name = r.getName();
		comment = r.getComment();
		roomtype = r.getRoomtype();
		numberOfPartizipants = r.getNumberOfPartizipants();
		appointment = r.getAppointment();
		confno = r.getConfno();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public RoomType getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(RoomType roomtype) {
		this.roomtype = roomtype;
	}

	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}

	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}

	public boolean isAppointment() {
		return appointment;
	}

	public void setAppointment(boolean appointment) {
		this.appointment = appointment;
	}

	public String getConfno() {
		return confno;
	}

	public void setConfno(String confno) {
		this.confno = confno;
	}
}
