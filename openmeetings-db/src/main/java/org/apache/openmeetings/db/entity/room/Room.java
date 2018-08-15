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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Entity
@FetchGroups({
	@FetchGroup(name = "roomModerators", attributes = { @FetchAttribute(name = "moderators") })
	, @FetchGroup(name = "roomGroups", attributes = { @FetchAttribute(name = "groups") })
	, @FetchGroup(name = "roomFiles", attributes = { @FetchAttribute(name = "files") })
})
@NamedQueries({
	@NamedQuery(name = "getNondeletedRooms", query = "SELECT r FROM Room r WHERE r.deleted = false"),
	@NamedQuery(name = "getPublicRooms", query = "SELECT r from Room r WHERE r.ispublic = true and r.deleted = false and r.type = :type"),
	@NamedQuery(name = "getRoomByOwnerAndTypeId", query = "SELECT r FROM Room as r WHERE r.ownerId = :ownerId "
					+ "AND r.type = :type AND r.deleted = false"),
	@NamedQuery(name = "selectMaxFromRooms", query = "SELECT COUNT(r.id) from Room r WHERE r.deleted = false AND r.name LIKE :search "),
	@NamedQuery(name = "getRoomByExternalId", query = "SELECT r FROM Room as r "
			+ "WHERE r.externalId = :externalId AND r.externalType = :externalType "
			+ "AND r.type = :type AND r.deleted = false"),
	@NamedQuery(name = "getPublicRoomsOrdered", query = "SELECT r from Room r WHERE r.ispublic= true AND r.deleted= false AND r.appointment = false ORDER BY r.name ASC"),
	@NamedQuery(name = "getRoomById", query = "SELECT r FROM Room r WHERE r.deleted = false AND r.id = :id"),
	@NamedQuery(name = "getRoomsByIds", query = "SELECT r FROM Room r WHERE r.deleted = false AND r.id IN :ids"),
	@NamedQuery(name = "getSipRoomIdsByIds", query = "SELECT r.id FROM Room r WHERE r.deleted = false AND r.sipEnabled = true AND r.id IN :ids"),
	@NamedQuery(name = "countRooms", query = "SELECT COUNT(r) FROM Room r WHERE r.deleted = false"),
	@NamedQuery(name = "getBackupRooms", query = "SELECT r FROM Room r ORDER BY r.id"),
	@NamedQuery(name = "getRoomsCapacityByIds", query = "SELECT SUM(r.capacity) FROM Room r WHERE r.deleted = false AND r.id IN :ids")
	, @NamedQuery(name = "getGroupRooms", query = "SELECT DISTINCT rg.room FROM RoomGroup rg LEFT JOIN FETCH rg.room "
			+ "WHERE rg.group.id = :groupId AND rg.room.deleted = false AND rg.room.appointment = false "
			+ "ORDER BY rg.room.name ASC")
})
@Table(name = "room")
@Root(name = "room")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Room extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final int CONFERENCE_TYPE_ID = 1;
	public static final int PRESENTATION_TYPE_ID = 3;
	public static final int INTERVIEW_TYPE_ID = 4;

	@XmlType(namespace="org.apache.openmeetings.room.right")
	public enum Right {
		superModerator
		, moderator
		, presenter
		, whiteBoard
		, share
		, remoteControl
		, audio
		, video
		, exclusive
	}

	@XmlType(namespace="org.apache.openmeetings.room.element")
	public enum RoomElement {
		TopBar
		, Chat
		, Activities
		, Files
		, ActionMenu
		, PollMenu
		, ScreenSharing
		, Whiteboard
		, MicrophoneStatus
	}

	@XmlType(namespace="org.apache.openmeetings.room.type")
	public enum Type {
		conference(CONFERENCE_TYPE_ID)
		, presentation(PRESENTATION_TYPE_ID)
		, interview(INTERVIEW_TYPE_ID);
		//, custom(5)
		private int id;

		Type(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Type get(Long type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Type get(Integer type) {
			return get(type == null ? 1 : type.intValue());
		}

		public static Type get(int type) {
			Type rt = Type.conference;
			switch (type) {
				case PRESENTATION_TYPE_ID:
					rt = Type.presentation;
					break;
				case INTERVIEW_TYPE_ID:
					rt = Type.interview;
					break;
				default:
					//no-op
			}
			return rt;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Element(data = true, required = false, name = "rooms_id")
	private Long id;

	@Column(name = "name")
	@Element(data = true, required=false)
	private String name;

	@Lob
	@Column(name = "comment")
	@Element(data = true, required = false)
	private String comment;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@Element(name = "roomtypeId", data = true, required = false)
	private Type type = Type.conference;

	@Column(name = "ispublic", nullable = false)
	@Element(name = "ispublic", data = true, required = false)
	private boolean ispublic;

	@Column(name = "capacity")
	@Element(name = "numberOfPartizipants", data = true, required = false)
	private long capacity = 4L;

	@Column(name = "appointment", nullable = false)
	@Element(data = true, required = false)
	private boolean appointment;

	// Vars to simulate external Room
	@Column(name = "external_id")
	@Element(data = true, required = false)
	private String externalId;

	@Column(name = "external_type")
	@Element(data = true, required = false)
	private String externalType;

	@Column(name = "demo_room", nullable = false)
	@Element(name = "isDemoRoom", data = true, required = false)
	private boolean demoRoom;

	@Column(name = "demo_time")
	@Element(data = true, required = false)
	private Integer demoTime; // In Seconds

	// If this is true all participants of a meeting have to wait for the
	// moderator to come into the room
	@Column(name = "ismoderatedroom", nullable = false)
	@Element(name="isModeratedRoom", data = true, required = false)
	private boolean moderated;

	@Column(name = "allow_user_questions", nullable = false)
	@Element(data = true, required = false)
	private boolean allowUserQuestions;

	@Column(name = "is_audio_only", nullable = false)
	@Element(name = "isAudioOnly", data = true, required = false)
	private boolean audioOnly;

	@Column(name = "is_closed", nullable = false)
	@Element(data = true, required = false)
	private boolean closed;

	@Column(name = "redirect_url")
	@Element(data = true, required = false)
	private String redirectURL;

	@Column(name = "owner_id")
	@Element(name = "ownerid", data = true, required = false)
	private Long ownerId; // Those are the rooms from the myrooms section

	@Column(name = "wait_for_recording", nullable = false)
	@Element(data = true, required = false)
	private boolean waitForRecording; // Show warning that user has to start
										// recording

	@Column(name = "allow_recording", nullable = false)
	@Element(name = "allowRecording", data = true, required = false)
	private boolean allowRecording = true; // Show or show not the recording option in a conference room

	@Column(name = "chat_moderated", nullable = false)
	@Element(data = true, required = false)
	private boolean chatModerated;

	/**
	 * Layout of Room
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "hide_element")
	@CollectionTable(name = "room_hide_element", joinColumns = @JoinColumn(name = "room_id"))
	@Enumerated(EnumType.STRING)
	@ElementList(name="hide_element", data = true, required = false)
	private Set<RoomElement> hiddenElements = new HashSet<>();

	@Column(name = "chat_opened", nullable = false)
	@Element(data = true, required = false)
	private boolean chatOpened;

	@Column(name = "files_opened", nullable = false)
	@Element(data = true, required = false)
	private boolean filesOpened;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "roomId")
	@ForeignKey(enabled = true)
	@ElementList(name = "room_moderators", required = false)
	private List<RoomModerator> moderators = new ArrayList<>();

	@Column(name = "sip_enabled", nullable = false)
	@Element(data = true, required = false)
	private boolean sipEnabled;

	@Column(name = "confno")
	@Element(data = true, required = false)
	private String confno;

	@Column(name = "pin")
	@Element(data = true, required = false)
	private String pin;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_id", insertable = true, updatable = true)
	@ElementDependent
	@org.simpleframework.xml.Transient
	private List<RoomGroup> groups = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_id", insertable = true, updatable = true, nullable = false)
	@ElementDependent
	@org.simpleframework.xml.Transient
	private List<RoomFile> files = new ArrayList<>();

	@Transient
	private List<StreamClient> currentusers;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean getIspublic() {
		return ispublic;
	}

	public void setIspublic(boolean ispublic) {
		this.ispublic = ispublic;
	}

	public List<StreamClient> getCurrentusers() {
		return currentusers;
	}

	public void setCurrentusers(List<StreamClient> currentusers) {
		this.currentusers = currentusers;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public boolean isAppointment() {
		return appointment;
	}

	public void setAppointment(boolean appointment) {
		this.appointment = appointment;
	}

	public boolean isDemoRoom() {
		return demoRoom;
	}

	public void setDemoRoom(boolean demoRoom) {
		this.demoRoom = demoRoom;
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

	public boolean isAllowUserQuestions() {
		return allowUserQuestions;
	}

	public void setAllowUserQuestions(boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}

	public boolean isAudioOnly() {
		return audioOnly;
	}

	public void setAudioOnly(boolean audioOnly) {
		this.audioOnly = audioOnly;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public boolean isWaitForRecording() {
		return waitForRecording;
	}

	public void setWaitForRecording(boolean waitForRecording) {
		this.waitForRecording = waitForRecording;
	}

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public List<RoomModerator> getModerators() {
		return moderators;
	}

	public void setModerators(List<RoomModerator> moderators) {
		if (moderators != null) {
			this.moderators = moderators;
		}
	}

	public boolean isChatModerated() {
		return chatModerated;
	}

	public void setChatModerated(boolean chatModerated) {
		this.chatModerated = chatModerated;
	}

	public Set<RoomElement> getHiddenElements() {
		return hiddenElements;
	}

	public void setHiddenElements(Set<RoomElement> hiddenElements) {
		this.hiddenElements = hiddenElements;
	}

	public boolean isHidden(RoomElement e) {
		return hiddenElements != null && hiddenElements.contains(e);
	}

	public boolean hide(RoomElement e) {
		if (hiddenElements == null) {
			hiddenElements = new HashSet<>();
		}
		return hiddenElements.add(e);
	}

	public List<RoomGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<RoomGroup> groups) {
		this.groups = groups;
	}

	public boolean isChatOpened() {
		return chatOpened;
	}

	public void setChatOpened(boolean chatOpened) {
		this.chatOpened = chatOpened;
	}

	public boolean isFilesOpened() {
		return filesOpened;
	}

	public void setFilesOpened(boolean filesOpened) {
		this.filesOpened = filesOpened;
	}

	public boolean isSipEnabled() {
		return sipEnabled;
	}

	public void setSipEnabled(boolean sipEnabled) {
		this.sipEnabled = sipEnabled;
	}

	public String getConfno() {
		return confno;
	}

	public void setConfno(String confno) {
		this.confno = confno;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public List<RoomFile> getFiles() {
		return files;
	}

	public void setFiles(List<RoomFile> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
}
