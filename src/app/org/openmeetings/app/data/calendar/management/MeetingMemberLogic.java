package org.openmeetings.app.data.calendar.management;

import java.util.TimeZone;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.invitation.Invitations;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingMemberLogic {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MeetingMemberLogic.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private Invitationmanagement invitationManagement;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;

	/**
	 * This can be either an internal or an external user, internal users have a
	 * user id != null && > 0
	 * 
	 * jNameInternalTimeZone is needed for the mapping of the timezones
	 * available
	 * 
	 * @author obecherer,seba.wagner
	 * @param firstname
	 * @param lastname
	 * @param memberStatus
	 * @param appointmentStatus
	 * @param appointmentId
	 * @param userid
	 * @param email
	 * @param baseUrl
	 * @param meeting_organizer
	 * @param invitor
	 * @param language_id
	 * @param isPasswordProtected
	 * @param password
	 * @param timezone
	 * @param jNameInternalTimeZone
	 * @param invitorName
	 *            can be different from the current firstname/lastname of course
	 * @return
	 */
	public Long addMeetingMember(String firstname, String lastname,
			String memberStatus, String appointmentStatus, Long appointmentId,
			Long userid, String email, String baseUrl, Long meeting_organizer,
			Boolean invitor, Long language_id, Boolean isPasswordProtected,
			String password, TimeZone timezone, OmTimeZone omTimeZone,
			String invitorName) {

		try {
			Long memberId = meetingMemberDao.addMeetingMember(firstname,
					lastname, memberStatus, appointmentStatus, appointmentId,
					userid, email, invitor, omTimeZone, false);

			// DefaultInvitation
			Appointment point = appointmentLogic
					.getAppointMentById(appointmentId);

			MeetingMember member = getMemberById(memberId);
			Boolean isInvitor = member.getInvitor();

			Long invitationId = null;

			if (point.getRemind() == null) {
				log.error("Appointment has no assigned ReminderType!");
				return null;
			}

			log.debug(":::: addMeetingMember ..... "
					+ point.getRemind().getTypId());

			String subject = formatSubject(language_id, point, timezone);

			String message = formatMessage(language_id, point, timezone,
					invitorName);

			// point.getRemind().getTypId() == 1 will not receive emails

			if (point.getRemind().getTypId() == 2) {
				log.debug("Invitation for Appointment : simple email");

				Invitations invitation = invitationManagement
						.addInvitationLink(
								new Long(2), // userlevel
								firstname + " " + lastname, // username
								message,
								baseUrl, // baseURl
								email, // email
								subject, // subject
								point.getRoom().getRooms_id(), // room_id
								"public",
								isPasswordProtected, // passwordprotected
								password, // invitationpass
								2, // valid type
								point.getAppointmentStarttime(), // valid from
								point.getAppointmentEndtime(), // valid to
								meeting_organizer, // created by
								baseUrl,
								language_id,
								true, // really send mail sendMail
								point.getAppointmentStarttime(),
								point.getAppointmentEndtime(),
								point.getAppointmentId());

				invitationId = invitation.getInvitations_id();

			} else if (point.getRemind().getTypId() == 3) {
				log.debug("Reminder for Appointment : iCal mail");

				System.out.println("### SENDING iCAL EMAIL");

				invitationId = invitationManagement
						.addInvitationIcalLink(
								new Long(2), // userlevel
								firstname + " " + lastname, // username
								message,
								baseUrl, // baseURl
								email, // email
								subject, // subject
								point.getRoom().getRooms_id(), // room_id
								"public",
								isPasswordProtected, // passwordprotected
								password, // invitationpass
								2, // valid
								point.getAppointmentStarttime(), // valid from
								point.getAppointmentEndtime(), // valid to
								meeting_organizer, // created by
								point.getAppointmentId(), isInvitor,
								language_id, timezone,
								point.getAppointmentId());

			}

			// Setting InvitationId within MeetingMember

			if (invitationId != null) {
				Invitations invi = invitationManagement
						.getInvitationbyId(invitationId);

				member.setInvitation(invi);

				updateMeetingMember(member);

			}

			return memberId;

		} catch (Exception err) {
			log.error("[addMeetingMember]", err);
		}
		return null;
	}

	private String formatSubject(Long language_id, Appointment point,
			TimeZone timezone) {
		try {
			Fieldlanguagesvalues labelid1151 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(1151), language_id);

			String message = labelid1151.getValue() + " "
					+ point.getAppointmentName();

			message += " "
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentStarttime(), timezone);

			message += " - "
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentEndtime(), timezone);

			return message;
		} catch (Exception err) {
			log.error("Could not format Email message");
			return "Error formatSubject";
		}

	}

	private String formatMessage(Long language_id, Appointment point,
			TimeZone timezone, String invitorName) {
		try {
			Fieldlanguagesvalues labelid1151 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(1151), language_id);

			String message = labelid1151.getValue() + " "
					+ point.getAppointmentName();

			if (point.getAppointmentDescription().length() != 0) {

				Fieldlanguagesvalues labelid1152 = fieldmanagment
						.getFieldByIdAndLanguage(new Long(1152), language_id);
				message += labelid1152.getValue()
						+ point.getAppointmentDescription();

			}

			Fieldlanguagesvalues labelid1153 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(1153), language_id);
			Fieldlanguagesvalues labelid1154 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(1154), language_id);

			message += "<br/>"
					+ labelid1153.getValue()
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentStarttime(), timezone)
					+ "<br/>";

			message += labelid1154.getValue()
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentEndtime(), timezone) + "<br/>";

			Fieldlanguagesvalues labelid1156 = fieldmanagment
					.getFieldByIdAndLanguage(new Long(1156), language_id);

			message += labelid1156.getValue() + invitorName + "<br/>";

			return message;
		} catch (Exception err) {
			log.error("Could not format Email message");
			return "Error formatMessage";
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 
	 */
	// ------------------------------------------------------------------------------------------------------------------------------
	public Long updateMeetingMember(Long meetingMemberId, String firstname,
			String lastname, String memberStatus, String appointmentStatus,
			Long appointmentId, Long userid, String email) {

		log.debug("MeetingMemberLogic.updateMeetingMember");

		MeetingMember member = meetingMemberDao
				.getMeetingMemberById(meetingMemberId);

		if (member == null) {
			log.error("Couldnt retrieve Member for ID " + meetingMemberId);
			return null;
		}

		try {
			return meetingMemberDao.updateMeetingMember(meetingMemberId,
					firstname, lastname, memberStatus, appointmentStatus,
					appointmentId, userid, email);
		} catch (Exception err) {
			log.error("[updateMeetingMember]", err);
		}
		return null;
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * @author becherer
	 * @param member
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Long updateMeetingMember(MeetingMember member) {
		log.debug("updateMeetingMember");

		return meetingMemberDao.updateMeetingMember(member)
				.getMeetingMemberId();
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * @author becherer
	 * @param memberId
	 */
	// --------------------------------------------------------------------------------------------
	public MeetingMember getMemberById(Long memberId) {
		log.debug("getMemberById");

		return meetingMemberDao.getMeetingMemberById(memberId);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param meetingMemberId
	 * @param users_id
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Long deleteMeetingMember(Long meetingMemberId, Long users_id,
			Long language_id) {
		log.debug("meetingMemberLogic.deleteMeetingMember : " + meetingMemberId);

		try {

			MeetingMember member = meetingMemberDao
					.getMeetingMemberById(meetingMemberId);

			if (member == null) {
				log.error("could not find meeting member!");
				return null;
			}

			Appointment point = member.getAppointment();
			point = appointmentLogic.getAppointMentById(point
					.getAppointmentId());

			if (point == null) {
				log.error("could not retrieve appointment!");
				return null;
			}

			Users user = userManagement.getUserById(users_id);

			if (user == null) {
				log.error("could not retrieve user!");
				return null;
			}

			log.debug("before sending cancelMail");

			// cancel invitation
			invitationManagement.cancelInvitation(point, member, users_id,
					language_id);

			log.debug("after sending cancelmail");

			Long returnValue = meetingMemberDao
					.deleteMeetingMember(meetingMemberId);

			return returnValue;

		} catch (Exception err) {
			log.error("[deleteMeetingMember]", err);
		}
		return null;
	}
	// --------------------------------------------------------------------------------------------

	/*
	 * public List<Appointment> getAppointmentByRange(Long userId ,Date
	 * starttime, Date endtime){ try { return
	 * AppointmentDaoImpl.getInstance().getAppointmentsByRange(userId,
	 * starttime, endtime); }catch(Exception err){
	 * log.error("[getAppointmentByRange]",err); } return null; } //next
	 * appointment to current date public Appointment getNextAppointment(){ try{
	 * return AppointmentDaoImpl.getInstance().getNextAppointment(new Date());
	 * }catch(Exception err){ log.error("[getNextAppointmentById]",err); }
	 * return null; }
	 * 
	 * public List<Appointment> searchAppointmentByName(String appointmentName){
	 * try{ return
	 * AppointmentDaoImpl.getInstance().searchAppointmentsByName(appointmentName
	 * ) ; }catch(Exception err){ log.error("[searchAppointmentByName]",err); }
	 * return null; }
	 * 
	 * 
	 * 
	 * public Long deleteAppointment(Long appointmentId){ try{
	 * AppointmentDaoImpl.getInstance().deleteAppointement(appointmentId);
	 * return appointmentId; }catch(Exception err){
	 * log.error("[deleteAppointment]",err); } return null;
	 * 
	 * }
	 * 
	 * public Long updateAppointment(Long appointmentId, String
	 * appointmentName,Long userId, String appointmentDescription, Date
	 * appointmentstart, Date appointmentend, Boolean isDaily, Boolean isWeekly,
	 * Boolean isMonthly, Boolean isYearly, Long categoryId ){
	 * 
	 * try { return
	 * AppointmentDaoImpl.getInstance().updateAppointment(appointmentId,
	 * appointmentName, userId, appointmentDescription, appointmentstart,
	 * appointmentend, isDaily, isWeekly, isMonthly, isYearly, categoryId); }
	 * catch (Exception err) { log.error("[updateAppointment]",err); } return
	 * null; }
	 */
}
