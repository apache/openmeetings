package org.openmeetings.app.remote;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingMemberService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MeetingMemberService.class, ScopeApplicationAdapter.webAppRootKey);
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
