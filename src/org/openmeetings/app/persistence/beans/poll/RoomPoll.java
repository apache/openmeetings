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
package org.openmeetings.app.persistence.beans.poll;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "room_polls")
@Root(name="roompoll")
public class RoomPoll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long roomPollId;
	
	@Column(name = "poll_name")
	@Element(data=true)
	private String pollName;
	
	@Column(name = "poll_question")
	@Element(name="pollquestion", data=true)
	private String pollQuestion;
	
	@Element(data=true)
	@Column(name = "created")
	private Date created;
	
	@Column(name = "archived")
	@Element(data=true)
	private boolean archived;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "poll_type_id")
	@Element(name="polltypeid", data=true, required=false)
	private PollType pollType;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "users_id")
	@Element(name="createdbyuserid", data=true, required=false)
	private Users createdBy;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rooms_id")
	@Element(name="roomid", data=true, required=false)
	private Rooms room;
	
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "room_poll_id")
	@ElementList(name="roompollanswers", required=false)
	private List<RoomPollAnswers> roomPollAnswerList;

	/**
	 * @return the createdBy
	 */
	public Users getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the pollQuestion
	 */
	public String getPollQuestion() {
		return pollQuestion;
	}

	/**
	 * @param pollQuestion
	 *            the pollQuestion to set
	 */
	public void setPollQuestion(String pollQuestion) {
		this.pollQuestion = pollQuestion;
	}

	/**
	 * @return the roomPollAnswerList
	 */
	public List<RoomPollAnswers> getRoomPollAnswerList() {
		if (roomPollAnswerList == null) {
			roomPollAnswerList = new LinkedList<RoomPollAnswers>();
		}
		return roomPollAnswerList;
	}

	/**
	 * @param roomPollAnswerList
	 *            the roomPollAnswerList to set
	 */
	public void setRoomPollAnswerList(List<RoomPollAnswers> roomPollAnswerList) {
		this.roomPollAnswerList = roomPollAnswerList;
	}

	/**
	 * @return the roomPollId
	 */
	public Long getRoomPollId() {
		return roomPollId;
	}

	/**
	 * @param roomPollId
	 *            the roomPollId to set
	 */
	public void setRoomPollId(Long roomPollId) {
		this.roomPollId = roomPollId;
	}

	/**
	 * @return the pollTypeId
	 */
	public PollType getPollType() {
		return pollType;
	}

	public void setPollType(PollType pollType) {
		this.pollType = pollType;
	}

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	/**
	 * @return the archived
	 */
	public boolean isArchived() {
		return archived;
	}

	/**
	 * @param archived the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the pollName
	 */
	public String getPollName() {
		return pollName;
	}

	/**
	 * @param pollName the pollName to set
	 */
	public void setPollName(String pollName) {
		this.pollName = pollName;
	}

}
