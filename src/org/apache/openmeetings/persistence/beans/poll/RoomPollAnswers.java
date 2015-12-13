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
package org.apache.openmeetings.persistence.beans.poll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.user.User;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@NamedQueries({
	@NamedQuery(name = "hasVoted", query = "SELECT rpa FROM RoomPollAnswers rpa "
				+ "WHERE rpa.roomPoll.room.rooms_id = :room_id " +
				"AND rpa.votedUser.user_id = :userid AND rpa.roomPoll.archived = :archived")
})
@Table(name = "room_poll_answers")
@Root(name="roompollanswer")
public class RoomPollAnswers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "users_id")
	@ForeignKey(enabled = true)
	@Element(name="voteduserid", data=false, required=false)
	private User votedUser;
	
	@Column(name = "answer")
	@Element(data=false, required=false)
	private Boolean answer;
	
	@Column(name = "pointList")
	@Element(name="pointlist", data=false, required=false)
	private Integer pointList;
	
	@Column(name = "voteDate")
	@Element(name="votedate", data=false)
	private Date voteDate;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "room_poll_id")
	private RoomPoll roomPoll;
	
	public Long getId() {
		return id;
	}

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
	 * @return the votedClient
	 */
	public User getVotedUser() {
		return votedUser;
	}

	/**
	 * @param votedClient
	 *            the votedClient to set
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
