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
package org.apache.openmeetings.db.dto.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.record.Recording;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String hash;
	private Long roomId;
	private String status;
	private boolean interview;
	private Date start;
	private Date end;
	private Integer width;
	private Integer height;
	private Long ownerId;
	private String externalType;

	public RecordingDTO() {
		//def constructor
	}

	public RecordingDTO(Recording r) {
		this.id = r.getId();
		this.name = r.getName();
		this.hash = r.getHash();
		this.roomId = r.getRoomId();
		this.status = r.getStatus().name();
		this.interview = r.isInterview();
		this.start = r.getRecordStart();
		this.end = r.getRecordEnd();
		this.width = r.getWidth();
		this.height = r.getHeight();
		this.ownerId = r.getOwnerId();
		this.externalType = r.getExternalType();
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isInterview() {
		return interview;
	}

	public void setInterview(boolean interview) {
		this.interview = interview;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getExternalType() {
		return externalType;
	}

	public RecordingDTO setExternalType(String externalType) {
		this.externalType = externalType;
		return this;
	}

	public static List<RecordingDTO> list(List<Recording> l) {
		List<RecordingDTO> list = new ArrayList<>();
		if (l != null) {
			for (Recording r : l) {
				list.add(new RecordingDTO(r));
			}
		}
		return list;
	}
}
