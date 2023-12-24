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

import static org.apache.openmeetings.db.bind.Constants.POLL_NODE;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.PollTypeAdapter;
import org.apache.openmeetings.db.bind.adapter.RoomAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "closePoll", query = "UPDATE RoomPoll rp SET rp.archived = :archived "
		+ "WHERE rp.room.id = :roomId")
@NamedQuery(name = "deletePoll", query = "DELETE FROM RoomPoll rp WHERE rp.id = :id")
@NamedQuery(name = "getPollById", query = "SELECT rp FROM RoomPoll rp WHERE rp.id = :id")
@NamedQuery(name = "getPoll", query = "SELECT rp FROM RoomPoll rp "
		+ "WHERE rp.room.id = :roomId AND rp.archived = false")
@NamedQuery(name = "getPollListBackup", query = "SELECT rp FROM RoomPoll rp ORDER BY rp.id")
@NamedQuery(name = "getArchivedPollList", query = "SELECT rp FROM RoomPoll rp "
		+ "WHERE rp.room.id = :roomId AND rp.archived = true ORDER BY rp.created DESC")
@NamedQuery(name = "hasPoll", query = "SELECT COUNT(rp) FROM RoomPoll rp "
		+ "WHERE rp.room.id = :roomId AND rp.archived = :archived")
@Table(name = "room_poll")
@XmlRootElement(name = POLL_NODE)
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomPoll implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	public static final int YES_NO_TYPE_ID = 1;
	public static final int NUMERIC_TYPE_ID = 2;

	@XmlType(namespace="org.apache.openmeetings.room.poll.type")
	public enum Type {
		YES_NO(YES_NO_TYPE_ID)
		, NUMERIC(NUMERIC_TYPE_ID);
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
			return type == NUMERIC_TYPE_ID ? Type.NUMERIC : Type.YES_NO;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@Column(name = "name")
	@XmlElement(name = "pollname", required = false)
	private String name;

	@Column(name = "question")
	@XmlElement(name = "pollquestion", required = false)
	private String question;

	@Column(name = "created")
	@XmlElement(name = "created", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date created;

	@Column(name = "archived", nullable = false)
	@XmlElement(name = "archived", required = false)
	@XmlJavaTypeAdapter(value = BooleanAdapter.class, type = boolean.class)
	private boolean archived;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "polltypeid", required = false)
	@XmlJavaTypeAdapter(PollTypeAdapter.class)
	private Type type;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "createdbyuserid", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User creator;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "roomid", required = false)
	@XmlJavaTypeAdapter(RoomAdapter.class)
	private Room room;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "poll_id")
	@XmlElementWrapper(name = "roompollanswers", required = false)
	@XmlElement(name = "roompollanswer", required = false)
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
			answers = new LinkedList<>();
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
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
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
	 * @param archived the archived to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
