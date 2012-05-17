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
package org.openmeetings.app.data.calendar.beans;

import org.openmeetings.app.persistence.beans.calendar.MeetingMember;

public class MeetingMemberDTO {

	private long meetingMemberId;
	private Boolean invitor;
	private String email;
	private String memberStatus;
	private String firstname;
	private String lastname;
	private Long userid;
	private String jNameTimeZone;

	public MeetingMemberDTO(MeetingMember meetingMemberItem) {
		meetingMemberId = meetingMemberItem.getMeetingMemberId();
		invitor = meetingMemberItem.getInvitor();
		email = meetingMemberItem.getEmail();
		memberStatus = meetingMemberItem.getMemberStatus();
		firstname = meetingMemberItem.getFirstname();
		lastname = meetingMemberItem.getLastname();
		userid = (meetingMemberItem.getUserid() != null) ? meetingMemberItem
				.getUserid().getUser_id() : 0;
		jNameTimeZone = (meetingMemberItem.getOmTimeZone() != null) ? meetingMemberItem
				.getOmTimeZone().getJname() : "";
	}

	public long getMeetingMemberId() {
		return meetingMemberId;
	}

	public void setMeetingMemberId(long meetingMemberId) {
		this.meetingMemberId = meetingMemberId;
	}

	public Boolean getInvitor() {
		return invitor;
	}

	public void setInvitor(Boolean invitor) {
		this.invitor = invitor;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getjNameTimeZone() {
		return jNameTimeZone;
	}

	public void setjNameTimeZone(String jNameTimeZone) {
		this.jNameTimeZone = jNameTimeZone;
	}

}
