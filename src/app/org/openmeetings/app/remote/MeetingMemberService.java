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
package org.openmeetings.app.remote;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingMemberService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MeetingMemberService.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private MeetingMemberLogic meetingMemberLogic;

	public Long updateMeetingMember(String SID, Long meetingMemberId,
			String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid,
			String email) {

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return meetingMemberLogic.updateMeetingMember(
						meetingMemberId, firstname, lastname, memberStatus,
						appointmentStatus, appointmentId, userid, email);
			}
		} catch (Exception err) {
			log.error("[updateMeetingMember]", err);
		}
		return null;

	}

	public Long deleteMeetingMember(String SID, Long meetingMemberId,
			Long language_id) {
		log.debug("MeetingMemberService.deleteMeetingmember");

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return meetingMemberLogic.deleteMeetingMember(
						meetingMemberId, users_id, language_id);
			}
		} catch (Exception err) {
			log.error("[deleteMeetingMember]", err);
		}
		return null;

	}
}
