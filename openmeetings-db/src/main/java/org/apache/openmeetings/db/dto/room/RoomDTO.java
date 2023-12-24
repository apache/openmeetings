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

import static org.apache.openmeetings.db.util.DtoHelper.optEnum;
import static org.apache.openmeetings.db.util.DtoHelper.optEnumList;
import static org.apache.openmeetings.db.util.DtoHelper.optInt;
import static org.apache.openmeetings.db.util.DtoHelper.optLong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String tag;
	private String comment;
	private Room.Type type;
	private Long capacity = Long.valueOf(4);
	private boolean appointment;
	private String confno;
	private boolean isPublic;
	private boolean demo;
	private boolean closed;
	private Integer demoTime;
	private String externalId;
	private String externalType;
	private String redirectUrl;
	private boolean moderated;
	private boolean waitModerator;
	private boolean allowUserQuestions;
	private boolean allowRecording;
	private boolean waitRecording;
	private boolean audioOnly;
	private Set<RoomElement> hiddenElements = new HashSet<>();
	private List<RoomFileDTO> files = new ArrayList<>();

	public RoomDTO() {
		//def constructor
	}

	public RoomDTO(Room r) {
		id = r.getId();
		name = r.getName();
		tag = r.getTag();
		comment = r.getComment();
		type = r.getType();
		capacity = r.getCapacity();
		appointment = r.isAppointment();
		confno = r.getConfno();
		isPublic = r.getIspublic();
		demo = r.isDemoRoom();
		closed = r.isClosed();
		demoTime = r.getDemoTime();
		externalId = r.getExternalId();
		externalType = r.externalType();
		redirectUrl = r.getRedirectURL();
		moderated = r.isModerated();
		waitModerator = r.isWaitModerator();
		allowUserQuestions = r.isAllowUserQuestions();
		allowRecording = r.isAllowRecording();
		waitRecording = r.isWaitRecording();
		audioOnly = r.isAudioOnly();
		hiddenElements = r.getHiddenElements();
		files = RoomFileDTO.get(r.getFiles());
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Room.Type getType() {
		return type;
	}

	public void setType(Room.Type type) {
		this.type = type;
	}

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
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

	public boolean isDemo() {
		return demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public Integer getDemoTime() {
		return demoTime;
	}

	public void setDemoTime(Integer demoTime) {
		this.demoTime = demoTime;
	}

	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	public boolean isWaitModerator() {
		return waitModerator;
	}

	public void setWaitModerator(boolean waitModerator) {
		this.waitModerator = waitModerator;
	}

	public boolean isAllowUserQuestions() {
		return allowUserQuestions;
	}

	public void setAllowUserQuestions(boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public boolean isWaitRecording() {
		return waitRecording;
	}

	public void setWaitRecording(boolean waitRecording) {
		this.waitRecording = waitRecording;
	}

	public boolean isAudioOnly() {
		return audioOnly;
	}

	public void setAudioOnly(boolean audioOnly) {
		this.audioOnly = audioOnly;
	}

	public Set<RoomElement> getHiddenElements() {
		return hiddenElements;
	}

	public void setHiddenElements(Set<RoomElement> hiddenElements) {
		this.hiddenElements = hiddenElements;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public List<RoomFileDTO> getFiles() {
		return files;
	}

	public void setFiles(List<RoomFileDTO> files) {
		this.files = files;
	}

	public static List<RoomDTO> list(List<Room> l) {
		List<RoomDTO> rList = new ArrayList<>();
		if (l != null) {
			for (Room r : l) {
				rList.add(new RoomDTO(r));
			}
		}
		return rList;
	}

	public static RoomDTO fromString(String s) {
		return get(new JSONObject(s));
	}

	public static RoomDTO get(JSONObject o) {
		if (o == null) {
			return null;
		}
		RoomDTO r = new RoomDTO();
		r.id = optLong(o, "id");
		r.name = o.optString("name");
		r.tag = o.optString("tag");
		r.comment = o.optString("comment");
		r.type = optEnum(Room.Type.class, o, "type");
		if (r.type == null) {
			throw new IllegalArgumentException("Room should have valid type");
		}
		r.capacity = o.optLong("capacity", 4);
		r.appointment = o.optBoolean("appointment", false);
		r.confno = o.optString("confno");
		r.isPublic = o.optBoolean("isPublic", false);
		r.demo = o.optBoolean("demo", false);
		r.closed = o.optBoolean("closed", false);
		r.demoTime = optInt(o, "demoTime");
		r.externalId = o.optString("externalId", null);
		r.externalType = o.optString("externalType", null);
		r.redirectUrl = o.optString("redirectUrl");
		r.moderated = o.optBoolean("moderated", false);
		r.waitModerator = o.optBoolean("waitModerator", false);
		r.allowUserQuestions = o.optBoolean("allowUserQuestions", false);
		r.allowRecording = o.optBoolean("allowRecording", false);
		r.waitRecording = o.optBoolean("waitRecording", false);
		r.audioOnly = o.optBoolean("audioOnly", false);
		r.getHiddenElements().addAll(optEnumList(RoomElement.class, o.optJSONArray("hiddenElements")));
		JSONArray fa = o.optJSONArray("files");
		if (fa != null) {
			for (int i = 0; i < fa.length(); ++i) {
				r.getFiles().add(RoomFileDTO.get(fa.getJSONObject(i)));
			}
		}
		return r;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
