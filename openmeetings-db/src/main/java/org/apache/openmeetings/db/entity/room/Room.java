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

import static org.apache.openmeetings.db.bind.Constants.ROOM_NODE;
import static org.apache.openmeetings.db.dao.room.RoomDao.GRP_FILES;
import static org.apache.openmeetings.db.dao.room.RoomDao.GRP_GROUPS;
import static org.apache.openmeetings.db.dao.room.RoomDao.GRP_MODERATORS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.IntAdapter;
import org.apache.openmeetings.db.bind.adapter.LongAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomElementAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomTypeAdapter;
import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.db.entity.user.Group;

@Entity
@FetchGroups({
	@FetchGroup(name = GRP_MODERATORS, attributes = { @FetchAttribute(name = "moderators") })
	, @FetchGroup(name = GRP_GROUPS, attributes = { @FetchAttribute(name = "groups") })
	, @FetchGroup(name = GRP_FILES, attributes = { @FetchAttribute(name = "files") })
})
@NamedQuery(name = "getNondeletedRooms", query = "SELECT r FROM Room r WHERE r.deleted = false")
@NamedQuery(name = "getPublicRooms", query = "SELECT r from Room r WHERE r.ispublic = true and r.deleted = false and r.type = :type")
@NamedQuery(name = "getRoomByOwnerAndTypeId", query = "SELECT r FROM Room as r WHERE r.ownerId = :ownerId "
				+ "AND r.type = :type AND r.deleted = false")
@NamedQuery(name = "selectMaxFromRooms", query = "SELECT COUNT(r.id) from Room r WHERE r.deleted = false AND r.name LIKE :search ")
@NamedQuery(name = "getExternalRoom", query = "SELECT rg.room FROM RoomGroup rg WHERE "
		+ "rg.group.deleted = false AND rg.group.external = true AND rg.group.name = :externalType "
		+ "AND rg.room.deleted = false AND rg.room.type = :type AND rg.room.externalId = :externalId")
@NamedQuery(name = "getExternalRoomNoType", query = "SELECT rg.room FROM RoomGroup rg WHERE "
		+ "rg.group.deleted = false AND rg.group.external = true AND rg.group.name = :externalType "
		+ "AND rg.room.deleted = false AND rg.room.externalId = :externalId")
@NamedQuery(name = "getPublicRoomsOrdered", query = "SELECT r from Room r WHERE r.ispublic= true AND r.deleted= false AND r.appointment = false ORDER BY r.name ASC")
@NamedQuery(name = "getRoomById", query = "SELECT r FROM Room r WHERE r.deleted = false AND r.id = :id")
@NamedQuery(name = "getRoomsByIds", query = "SELECT r FROM Room r WHERE r.deleted = false AND r.id IN :ids")
@NamedQuery(name = "getRoomByTag", query = "SELECT r FROM Room r WHERE r.deleted = false AND r.tag = :tag")
@NamedQuery(name = "getSipRoomIdsByIds", query = "SELECT r.id FROM Room r WHERE r.deleted = false AND r.sipEnabled = true AND r.id IN :ids")
@NamedQuery(name = "countRooms", query = "SELECT COUNT(r) FROM Room r WHERE r.deleted = false")
@NamedQuery(name = "getBackupRooms", query = "SELECT r FROM Room r ORDER BY r.id")
@NamedQuery(name = "getGroupRooms", query = "SELECT DISTINCT rg.room FROM RoomGroup rg LEFT JOIN FETCH rg.room "
		+ "WHERE rg.group.id = :groupId AND rg.room.deleted = false AND rg.room.appointment = false "
		+ "ORDER BY rg.room.name ASC")
@Table(name = "room", indexes = {
		@Index(name = "room_name_idx", columnList = "name")
})
@XmlRootElement(name = ROOM_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Room extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	public static final int CONFERENCE_TYPE_ID = 1;
	public static final int PRESENTATION_TYPE_ID = 3;
	public static final int INTERVIEW_TYPE_ID = 4;

	@XmlType(namespace="org.apache.openmeetings.room.right")
	public enum Right {
		SUPER_MODERATOR
		, MODERATOR
		, PRESENTER
		, WHITEBOARD
		, SHARE
		, REMOTE_CONTROL
		, AUDIO
		, VIDEO
		, MUTE_OTHERS
	}

	@XmlType(namespace="org.apache.openmeetings.room.element")
	public enum RoomElement {
		TOP_BAR
		, CHAT
		, ACTIVITIES
		, FILES
		, ACTION_MENU
		, POLL_MENU
		, SCREEN_SHARING
		, WHITEBOARD
		, MICROPHONE_STATUS
		, USER_COUNT
	}

	@XmlType(namespace="org.apache.openmeetings.room.type")
	public enum Type {
		CONFERENCE(CONFERENCE_TYPE_ID)
		, PRESENTATION(PRESENTATION_TYPE_ID)
		, INTERVIEW(INTERVIEW_TYPE_ID);
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
			Type rt = Type.CONFERENCE;
			switch (type) {
				case PRESENTATION_TYPE_ID:
					rt = Type.PRESENTATION;
					break;
				case INTERVIEW_TYPE_ID:
					rt = Type.INTERVIEW;
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
	@XmlElement(name = "rooms_id", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "name", required = false)
	private String name;

	@Column(name = "tag", length = 10)
	@XmlElement(name = "tag", required = false)
	private String tag;

	@Lob
	@Column(name = "comment")
	@XmlElement(name = "comment", required = false)
	private String comment;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "roomtypeId", required = false)
	@XmlJavaTypeAdapter(RoomTypeAdapter.class)
	private Type type = Type.CONFERENCE;

	@Column(name = "ispublic", nullable = false)
	@XmlElement(name = "ispublic", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean ispublic;

	@Column(name = "capacity")
	@XmlElement(name = "numberOfPartizipants", required = false)
	@XmlJavaTypeAdapter(value = LongAdapter.class, type = long.class)
	private long capacity = 4L;

	@Column(name = "appointment", nullable = false)
	@XmlElement(name = "appointment", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean appointment;

	// Vars to simulate external Room
	@Column(name = "external_id")
	@XmlElement(name = "externalId", required = false)
	private String externalId;

	@Column(name = "demo_room", nullable = false)
	@XmlElement(name = "isDemoRoom", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean demoRoom;

	@Column(name = "demo_time")
	@XmlElement(name = "demoTime", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer demoTime; // In Seconds

	// If this is true noone can automatically get additional rights
	@Column(name = "moderated", nullable = false)
	@XmlElement(name = "isModeratedRoom", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean moderated;

	// If this is true all participants of a meeting have to wait for the
	// moderator to come into the room
	@Column(name = "wait_moderator", nullable = false)
	@XmlElement(name = "waitModerator", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean waitModerator;

	@Column(name = "allow_user_questions", nullable = false)
	@XmlElement(name = "allowUserQuestions", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean allowUserQuestions;

	@Column(name = "audio_only", nullable = false)
	@XmlElement(name = "isAudioOnly", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean audioOnly;

	@Column(name = "closed", nullable = false)
	@XmlElement(name = "closed", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean closed;

	@Column(name = "redirect_url")
	@XmlElement(name = "redirectURL", required = false)
	private String redirectURL;

	@Column(name = "owner_id")
	@XmlElement(name = "ownerid", required = false)
	@XmlJavaTypeAdapter(LongAdapter.class)
	private Long ownerId; // Those are the rooms from the myrooms section

	@Column(name = "wait_for_recording", nullable = false)
	@XmlElement(name = "waitRecording", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean waitRecording; // Show warning that user has to start recording

	@Column(name = "allow_recording", nullable = false)
	@XmlElement(name = "allowRecording", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean allowRecording = true; // Show or show not the recording option in a conference room

	@Column(name = "chat_moderated", nullable = false)
	@XmlElement(name = "chatModerated", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean chatModerated;

	/**
	 * Layout of Room
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "hide_element")
	@CollectionTable(name = "room_hide_element", joinColumns = @JoinColumn(name = "room_id"))
	@Enumerated(EnumType.STRING)
	@XmlElementWrapper(name = "hide_element", required = false)
	@XmlElement(name = "roomElement")
	@XmlJavaTypeAdapter(RoomElementAdapter.class)
	private Set<RoomElement> hiddenElements = new HashSet<>();

	@Column(name = "chat_opened", nullable = false)
	@XmlElement(name = "chatOpened", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean chatOpened;

	@Column(name = "files_opened", nullable = false)
	@XmlElement(name = "filesOpened", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean filesOpened;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "roomId")
	@ForeignKey(enabled = true)
	@XmlElementWrapper(name = "room_moderators", required = false)
	@XmlElement(name = "room_moderator", required = false)
	private List<RoomModerator> moderators = new ArrayList<>();

	@Column(name = "sip_enabled", nullable = false)
	@XmlElement(name = "sipEnabled", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean sipEnabled;

	@Column(name = "confno")
	@XmlElement(name = "confno", required = false)
	private String confno;

	@Column(name = "pin")
	@XmlElement(name = "pin", required = false)
	private String pin;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_id", insertable = true, updatable = true)
	@ElementDependent
	@XmlTransient
	private List<RoomGroup> groups = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_id", insertable = true, updatable = true, nullable = false)
	@ElementDependent
	@XmlTransient
	private List<RoomFile> files = new ArrayList<>();

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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public boolean isInterview() {
		return Type.INTERVIEW == type;
	}

	public boolean getIspublic() {
		return ispublic;
	}

	public void setIspublic(boolean ispublic) {
		this.ispublic = ispublic;
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

	public boolean isWaitModerator() {
		return waitModerator;
	}

	public void setWaitModerator(boolean waitModerator) {
		this.waitModerator = waitModerator;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String externalType() {
		Optional<String> extType = groups == null
				? Optional.empty()
				: groups.stream().filter(rg -> rg.getGroup().isExternal()).findFirst()
				.map(gu -> gu.getGroup().getName());
		return extType.isPresent() ? extType.get() : null;
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

	public boolean isWaitRecording() {
		return waitRecording;
	}

	public void setWaitRecording(boolean waitRecording) {
		this.waitRecording = waitRecording;
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

	public void addGroup(Group g) {
		if (groups == null) {
			groups = new ArrayList<>();
		}
		groups.add(new RoomGroup(g, this));
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

	public boolean isOwner(Long userId) {
		return ownerId != null && ownerId.equals(userId);
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
}
