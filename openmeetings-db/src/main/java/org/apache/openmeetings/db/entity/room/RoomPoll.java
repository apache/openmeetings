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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
		@NamedQuery(name = "closePoll", query = "UPDATE RoomPoll rp SET rp.archived = :archived "
				+ "WHERE rp.room.id = :roomId"),
		@NamedQuery(name = "deletePoll", query = "DELETE FROM RoomPoll rp WHERE rp.id = :id"),
		@NamedQuery(name = "getPollById", query = "SELECT rp FROM RoomPoll rp WHERE rp.id = :id"),
		@NamedQuery(name = "getPoll", query = "SELECT rp FROM RoomPoll rp "
				+ "WHERE rp.room.id = :roomId AND rp.archived = :archived"),
		@NamedQuery(name = "getPollListBackup", query = "SELECT rp FROM RoomPoll rp ORDER BY rp.id"),
		@NamedQuery(name = "getArchivedPollList", query = "SELECT rp FROM RoomPoll rp "
				+ "WHERE rp.room.id = :roomId AND rp.archived = :archived ORDER BY rp.created DESC"),
		@NamedQuery(name = "hasPoll", query = "SELECT COUNT(rp) FROM RoomPoll rp "
				+ "WHERE rp.room.id = :roomId AND rp.archived = :archived") })
@Table(name = "room_poll")
@Root(name = "roompoll")
public class RoomPoll implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "poll_name")
	@Element(name = "pollname", data = true, required = false)
	private String name;

	@Column(name = "poll_question")
	@Element(name = "pollquestion", data = true, required = false)
	private String question;

	@Column(name = "created")
	@Element(data = true, required = false)
	private Date created;

	@Column(name = "archived")
	@Element(data = true, required = false)
	private boolean archived;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "poll_type_id")
	@ForeignKey(enabled = true)
	@Element(name = "polltypeid", data = true, required = false)
	private PollType type;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "users_id")
	@ForeignKey(enabled = true)
	@Element(name = "createdbyuserid", data = true, required = false)
	private User creator;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id")
	@ForeignKey(enabled = true)
	@Element(name = "roomid", data = true, required = false)
	private Room room;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_poll_id")
	@ElementList(name = "roompollanswers", required = false)
	private List<RoomPollAnswer> answers;

	/**
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
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
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question
	 *            the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the answers
	 */
	public List<RoomPollAnswer> getAnswers() {
		if (answers == null) {
			answers = new LinkedList<RoomPollAnswer>();
		}
		return answers;
	}

	/**
	 * @param answers
	 *            the answers to set
	 */
	public void setAnswers(List<RoomPollAnswer> answers) {
		this.answers = answers;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public PollType getType() {
		return type;
	}

	public void setType(PollType type) {
		this.type = type;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	/**
	 * @return the archived
	 */
	public boolean isArchived() {
		return archived;
	}

	/**
	 * @param archived
	 *            the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the pollName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
