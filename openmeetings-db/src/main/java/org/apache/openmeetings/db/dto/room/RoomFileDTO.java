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

import static org.apache.openmeetings.db.util.DtoHelper.optLong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.room.RoomFile;

import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomFileDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private long fileId;
	private long wbIdx = 0;

	public RoomFileDTO() {
		//def constructor
	}

	public RoomFileDTO(RoomFile rf) {
		id = rf.getId();
		fileId = rf.getFile().getId();
		wbIdx = rf.getWbIdx();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public long getWbIdx() {
		return wbIdx;
	}

	public void setWbIdx(long wbIdx) {
		this.wbIdx = wbIdx;
	}

	public static List<RoomFileDTO> get(List<RoomFile> rfl) {
		List<RoomFileDTO> r = new ArrayList<>();
		if (rfl != null) {
			for (RoomFile rf : rfl) {
				r.add(new RoomFileDTO(rf));
			}
		}
		return r;
	}

	public static RoomFileDTO get(JSONObject o) {
		if (o == null) {
			return null;
		}
		RoomFileDTO rf = new RoomFileDTO();
		rf.id = optLong(o, "id");
		rf.fileId = o.getLong("fileId");
		rf.wbIdx = optLong(o, "wbIdx");
		return rf;
	}
}
