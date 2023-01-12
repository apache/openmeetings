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

import static org.apache.openmeetings.db.bind.Constants.ROOM_FILE_NODE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.FileAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.file.BaseFileItem;

@Entity
@Table(name = "room_file")
@XmlRootElement(name = ROOM_FILE_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomFile implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlElement(name = "id")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "room_id")
	@XmlElement(name = "roomId")
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long roomId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "file_id", insertable = true, updatable = true, nullable = false)
	@ForeignKey(enabled = true)
	@XmlElement(name = "file")
	@XmlJavaTypeAdapter(FileAdapter.class)
	private BaseFileItem file;

	/*
	 * Index of whiteboard for this file, zero based
	 */
	@Column(name = "wb_idx", nullable = false)
	@XmlElement(name = "wbIdx", required = false)
	@XmlJavaTypeAdapter(value = LongAdapter.class, type = long.class)
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
