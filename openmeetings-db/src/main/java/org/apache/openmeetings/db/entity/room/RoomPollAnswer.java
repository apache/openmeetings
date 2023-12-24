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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.bind.adapter.BooleanAdapter;
import org.apache.openmeetings.db.bind.adapter.DateAdapter;
import org.apache.openmeetings.db.bind.adapter.IntAdapter;
import org.apache.openmeetings.db.bind.adapter.UserAdapter;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;

@Entity
@NamedQuery(name = "notVoted", query = "SELECT rpa FROM RoomPollAnswer rpa WHERE rpa.roomPoll.room.id = :roomId "
		+ "AND rpa.votedUser.id = :userId AND rpa.roomPoll.archived = false")
@Table(name = "room_poll_answer")
@XmlRootElement(name = "roompollanswer")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomPollAnswer implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlTransient
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@ForeignKey(enabled = true)
	@XmlElement(name = "voteduserid", required = false)
	@XmlJavaTypeAdapter(UserAdapter.class)
	private User votedUser;

	@Column(name = "answer")
	@XmlElement(name = "answer", required = false)
	@XmlJavaTypeAdapter(BooleanAdapter.class)
	private Boolean answer;

	@Column(name = "pointList")
	@XmlElement(name = "pointlist", required = false)
	@XmlJavaTypeAdapter(IntAdapter.class)
	private Integer pointList;

	@Column(name = "voteDate")
	@XmlElement(name = "votedate", required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date voteDate;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "poll_id")
	@XmlTransient
	private RoomPoll roomPoll;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the answer
	 */
	public Boolean getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Boolean answer) {
		this.answer = answer;
	}

	/**
	 * @return the pointList
	 */
	public Integer getPointList() {
		return pointList;
	}

	/**
	 * @param pointList
	 *            the pointList to set
	 */
	public void setPointList(Integer pointList) {
		this.pointList = pointList;
	}

	/**
	 * @return the voteDate
	 */
	public Date getVoteDate() {
		return voteDate;
	}

	/**
	 * @param voteDate
	 *            the voteDate to set
	 */
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}

	/**
	 * @return the voted {@link User}
	 */
	public User getVotedUser() {
		return votedUser;
	}

	/**
	 * @param votedUser
	 *            the voted {@link User} to set
	 */
	public void setVotedUser(User votedUser) {
		this.votedUser = votedUser;
	}

	public RoomPoll getRoomPoll() {
		return roomPoll;
	}

	public void setRoomPoll(RoomPoll roomPoll) {
		this.roomPoll = roomPoll;
	}
}
