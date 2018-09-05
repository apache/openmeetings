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
package org.apache.openmeetings.db.entity.room;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "room_file")
@Root(name = "RoomFile")
public class RoomFile implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Element(data = true, required = true)
	private Long id;

	@Column(name = "room_id")
	@Element(data = true, required = true)
	private Long roomId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "file_id", insertable = true, updatable = true, nullable = false)
	@ForeignKey(enabled = true)
	@Element(data = true, required = true)
	private BaseFileItem file;

	/*
	 * Index of whiteboard for this file, zero based
	 */
	@Column(name = "wb_idx", nullable = false)
	private long wbIdx = 0;

	public RoomFile() {
		//def constructor
	}

	public RoomFile(Long roomId, BaseFileItem file, long wbIdx) {
		this.roomId = roomId;
		this.file = file;
		this.wbIdx = wbIdx;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public BaseFileItem getFile() {
		return file;
	}

	public void setFile(BaseFileItem file) {
		this.file = file;
	}

	public long getWbIdx() {
		return wbIdx;
	}

	public void setWbIdx(long wbIdx) {
		this.wbIdx = wbIdx;
	}
}
