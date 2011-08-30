package org.openmeetings.app.data.calendar.management;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.invitation.Invitations;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentLogic {

	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentLogic.class, "openmeetings");

	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private Invitationmanagement invitationManagement;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;

	public List<Appointment> getAppointmentByRange(Long userId, Date starttime,
			Date endtime) {
		try {
			return appointmentDao.getAppointmentsByRange(userId, starttime,
					endtime);
		} catch (Exception err) {
			log.error("[getAppointmentByRange]", err);
		}
		return null;
	}

	public List<Appointment> getTodaysAppointmentsForUser(Long userId) {
		log.debug("getTodaysAppointmentsForUser");

		List<Appointment> points = appointmentDao
				.getTodaysAppoitmentsbyRangeAndMember(userId);

		log.debug("Count Appointments for Today : " + points.size());

		return points;

	}

	/**
	 * @author o.becherer
	 * @param room_id
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Appointment getAppointmentByRoom(Long room_id) throws Exception {
		log.debug("getAppointmentByRoom");

		Rooms room = roommanagement.getRoomById(room_id);

		if (room == null)
			throw new Exception("Room does not exist in database!");

		if (!room.getAppointment())
			throw new Exception("Room " + room.getName()
					+ " isnt part of an appointed meeting");

		return appointmentDao.getAppointmentByRoom(room_id);
	}

	// --------------------------------------------------------------------------------------------

	// next appointment to current date
	public Appointment getNextAppointment() {
		try {
			return appointmentDao.getNextAppointment(new Date());
		} catch (Exception err) {
			log.error("[getNextAppointmentById]", err);
		}
		return null;
	}

	public List<Appointment> searchAppointmentByName(String appointmentName) {
		try {
			return appointmentDao.searchAppointmentsByName(appointmentName);
		} catch (Exception err) {
			log.error("[searchAppointmentByName]", err);
		}
		return null;
	}

	public Long saveAppointment(String appointmentName, Long userId,
			String appointmentLocation, String appointmentDescription,
			Date appointmentstart, Date appointmentend, Boolean isDaily,
			Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
			Long categoryId, Long remind, List mmClient, Long roomType,
			String baseUrl, Long language_id) {

		log.debug("Appointmentlogic.saveAppointment");

		// create a Room
		// Long room_id = roommanagement.addRoom(
		// 3, // Userlevel
		// appointmentName, // name
		// roomType, // RoomType
		// "", // Comment
		// new Long(8), // Number of participants
		// true, // public
		// null, // Organisations
		// 270, // Video Width
		// 280, // Video height
		// 2, // Video X
		// 2, // Video Y
		// 400, // Modeartionpanel X
		// true, // Whiteboard
		// 276, // Whiteboard x
		// 2, // Whiteboard y
		// 592, // WB height
		// 660, // WB width
		// true, // Show Files Panel
		// 2, // Files X
		// 284, // Files Y
		// 310, // Files height
		// 270, // Files width
		// true, // Appointment
		// false, // Demo Room => Meeting Timer
		// null); // Meeting Timer time in seconds

		// TODO:Add this user as the default Moderator of the Room

		Long room_id = roommanagement.addRoom(3, // Userlevel
				appointmentName, // name
				roomType, // RoomType
				"", // Comment
				new Long(8), // Number of participants
				true, // public
				null, // Organisations
				true, // Appointment
				false, // Demo Room => Meeting Timer
				null, // Meeting Timer time in seconds
				false, // Is Moderated Room
				null, // Moderation List Room
				true, // Allow User Questions
				false, // isAudioOnly
				false, // isClosed
				"", // redirectURL
				"", // sipNumber
				"", // conferencePIN
				null, // ownerID
				null, null, false //
				);

		log.debug("Appointmentlogic.saveAppointment : Room - " + room_id);
		log.debug("Appointmentlogic.saveAppointment : Reminder - " + remind);

		Rooms room = roommanagement.getRoomById(room_id);

		if (room == null)
			log.error("Room " + room_id + " could not be found!");
		else
			log.debug("Room " + room_id + " ok!");

		try {

			// Adding Invitor as Meetingmember
			Users user = userManagement.getUserById(userId);

			Long id = appointmentDao.addAppointment(appointmentName, userId,
					appointmentLocation, appointmentDescription,
					appointmentstart, appointmentend, isDaily, isWeekly,
					isMonthly, isYearly, categoryId, remind, room, language_id,
					false, "", false, user.getOmTimeZone().getJname());

			String jNameMemberTimeZone = "";
			if (user.getOmTimeZone() != null) {
				jNameMemberTimeZone = user.getOmTimeZone().getJname();
			}

			String invitorName = user.getFirstname() + " " + user.getLastname()
					+ " [" + user.getAdresses().getEmail() + "]";

			MeetingMemberLogic.getInstance().addMeetingMember(
					user.getFirstname(), user.getLastname(), "", "", id,
					userId, user.getAdresses().getEmail(), baseUrl, userId,
					true, language_id, false, "", jNameMemberTimeZone,
					invitorName);

			// add items
			if (mmClient != null) {

				for (int i = 0; i < mmClient.size(); i++) {

					Map clientMember = (Map) mmClient.get(i);

					log.debug("clientMember.get('userId') "
							+ clientMember.get("userId"));

					Long sendToUserId = 0L;
					if (clientMember.get("userId") != null) {
						sendToUserId = Long.valueOf(
								clientMember.get("userId").toString())
								.longValue();
					}

					jNameMemberTimeZone = clientMember.get("jNameTimeZone")
							.toString();

					// Not In Remote List available - intern OR extern user
					MeetingMemberLogic.getInstance().addMeetingMember(
							clientMember.get("firstname").toString(),
							clientMember.get("lastname").toString(),
							"0",
							"0",
							id,
							sendToUserId, // sending To: External users have a 0
											// here
							clientMember.get("email").toString(), baseUrl,
							userId, new Boolean(false), language_id, false, "",
							jNameMemberTimeZone, invitorName);

				}
			}

			return id;
		} catch (Exception err) {
			log.error("[saveAppointment]", err);
		}
		return null;
	}

	/**
	 * 
	 * @param appointmentId
	 * @return
	 */
	// -------------------------------------------------------------------------------------
	public Long deleteAppointment(Long appointmentId, Long users_id,
			Long language_id) {
		log.debug("deleteAppointment : " + appointmentId);

		try {

			Appointment point = getAppointMentById(appointmentId);

			if (point == null) {
				log.error("No appointment found for ID " + appointmentId);
				return null;
			}

			if (point.getIsConnectedEvent() != null
					&& point.getIsConnectedEvent()) {
				List<Appointment> appointments = appointmentDao
						.getAppointmentsByRoomId(point.getRoom().getRooms_id());

				for (Appointment appointment : appointments) {

					if (!appointment.getAppointmentId().equals(appointmentId)) {

						appointmentDao.deleteAppointement(appointment
								.getAppointmentId());

					}

				}

			}

			Rooms room = point.getRoom();

			// Deleting/Notifing Meetingmembers
			List<MeetingMember> members = meetingMemberDao.getMeetingMemberByAppointmentId(appointmentId);

			if (members == null)
				log.debug("Appointment " + point.getAppointmentName()
						+ " has no meeting members");

			if (members != null) {
				for (int i = 0; i < members.size(); i++) {
					log.debug("deleting member " + members.get(i).getEmail());
					MeetingMemberLogic.getInstance().deleteMeetingMember(
							members.get(i).getMeetingMemberId(), users_id,
							language_id);
				}
			}

			// Deleting Appointment itself
			appointmentDao.deleteAppointement(appointmentId);

			// Deleting Room
			roommanagement.deleteRoom(room);

			return appointmentId;

		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
		}

		return null;

	}

	// -------------------------------------------------------------------------------------

	/**
	 * Retrieving Appointment by ID
	 */
	// ----------------------------------------------------------------------------------------------
	public Appointment getAppointMentById(Long appointment) {
		log.debug("getAppointMentById");

		return appointmentDao.getAppointmentById(appointment);
	}

	// ----------------------------------------------------------------------------------------------

	/**
	 * Sending Reminder in Simple mail format hour before Meeting begin
	 */
	// ----------------------------------------------------------------------------------------------
	public void doScheduledMeetingReminder() {
		// log.debug("doScheduledMeetingReminder");

		List<Appointment> points = appointmentDao
				.getTodaysAppointmentsForAllUsers();

		if (points == null || points.size() < 1) {
			// log.debug("doScheduledMeetingReminder : no Appointments today");
			return;
		}

		Calendar calInitial = Calendar.getInstance();
		int offsetInitial = calInitial.get(Calendar.ZONE_OFFSET)
				+ calInitial.get(Calendar.DST_OFFSET);

		// Check right time
		Date now = new Date(System.currentTimeMillis() - offsetInitial);

		log.debug("doScheduledMeetingReminder : UTC now " + now);

		Long language_id = Long.valueOf(
				cfgManagement.getConfKey(3, "default_lang_id").getConf_value())
				.longValue();

		Fieldlanguagesvalues labelid1158 = fieldmanagment
				.getFieldByIdAndLanguage(new Long(1158), language_id);
		Fieldlanguagesvalues labelid1153 = fieldmanagment
				.getFieldByIdAndLanguage(new Long(1153), language_id);
		Fieldlanguagesvalues labelid1154 = fieldmanagment
				.getFieldByIdAndLanguage(new Long(1154), language_id);

		for (int i = 0; i < points.size(); i++) {
			Appointment ment = points.get(i);

			// Checking ReminderType - only ReminderType simple mail is
			// concerned!
			if (ment.getRemind().getTypId() == 2
					|| ment.getRemind().getTypId() == 3) {

				log.debug("doScheduledMeetingReminder : Found appointment "
						+ ment.getAppointmentName());

				Date appStart = ment.getAppointmentStarttime();
				Date oneHourBeforeAppStart = new Date(
						System.currentTimeMillis());
				oneHourBeforeAppStart.setTime(appStart.getTime());
				// oneHourBeforeAppStart.setHours(appStart.getHours() -1);

				oneHourBeforeAppStart.setMinutes(appStart.getMinutes() - 5);

				// System.out.println("doScheduledMeetingReminder : Found appointment 1 "
				// +now);
				// System.out.println("doScheduledMeetingReminder : Found appointment 2 "
				// +appStart);
				// System.out.println("doScheduledMeetingReminder : Found appointment 3 "
				// +oneHourBeforeAppStart);
				// System.out.println("doScheduledMeetingReminder : Found appointment 4 "
				// +now.before(appStart));
				// System.out.println("doScheduledMeetingReminder : Found appointment 5 "
				// +now.after(oneHourBeforeAppStart));
				//
				if (now.before(appStart) && now.after(oneHourBeforeAppStart)) {
					log.debug("Meeting " + ment.getAppointmentName()
							+ " is in reminder range...");

					List<MeetingMember> members = meetingMemberDao.getMeetingMemberByAppointmentId(ment.getAppointmentId());

					if (members == null || members.size() < 1) {
						log.debug("doScheduledMeetingReminder : no members in meeting!");
						continue;
					}

					for (int y = 0; y < members.size(); y++) {
						MeetingMember mm = members.get(y);

						log.debug("doScheduledMeetingReminder : Member "
								+ mm.getEmail());

						Invitations inv = mm.getInvitation();

						if (inv == null) {
							log.error("Error retrieving Invitation for member "
									+ mm.getEmail() + " in Appointment "
									+ ment.getAppointmentName());
						} else {
							// Check if Invitation was updated last time
							Date updateTime = inv.getUpdatetime();

							if (updateTime != null
									&& updateTime.after(oneHourBeforeAppStart)) {
								log.debug("Member has been informed within one hour before Meeting start");
								continue;
							}

							if (inv.getBaseUrl() == null
									|| inv.getBaseUrl().length() < 1) {
								log.error("Error retrieving baseUrl from Invitation ID : "
										+ inv.getInvitations_id());
								continue;
							}

							// ment.getAppointmentStarttime().toLocaleString()

							Users us = ment.getUserId();

							String jNameTimeZone = null;
							if (us != null && us.getOmTimeZone() != null) {
								jNameTimeZone = us.getOmTimeZone().getJname();
							} else {
								Configuration conf = cfgManagement.getConfKey(
										3L, "default.timezone");
								if (conf != null) {
									jNameTimeZone = conf.getConf_value();
								}
							}

							OmTimeZone omTimeZone = omTimeZoneDaoImpl
									.getOmTimeZone(jNameTimeZone);

							String timeZoneName = omTimeZone.getIcal();

							Calendar cal = Calendar.getInstance();
							cal.setTimeZone(TimeZone.getTimeZone(timeZoneName));
							int offset = cal.get(Calendar.ZONE_OFFSET)
									+ cal.get(Calendar.DST_OFFSET);

							Date starttime = new Date(ment
									.getAppointmentStarttime().getTime()
									+ offset);
							Date endtime = new Date(ment
									.getAppointmentEndtime().getTime() + offset);

							// String message = "Meeting : " +
							// ment.getAppointmentName() + "<br>";
							// if(ment.getAppointmentDescription() != null &&
							// ment.getAppointmentDescription().length() > 0)
							// message += "(" + ment.getAppointmentDescription()
							// + ")<br>";
							// message += "Start : " + starttime + "<br>";
							// message += "End : " + endtime + "<br>";
							// message += "Timezone : " + omTimeZone.getIcal() +
							// "<br>";

							String message = labelid1158.getValue() + " "
									+ ment.getAppointmentName();

							if (ment.getAppointmentDescription().length() != 0) {

								Fieldlanguagesvalues labelid1152 = fieldmanagment
										.getFieldByIdAndLanguage(
												new Long(1152), language_id);
								message += labelid1152.getValue()
										+ ment.getAppointmentDescription();

							}

							message += "<br/>"
									+ labelid1153.getValue()
									+ ' '
									+ CalendarPatterns
											.getDateWithTimeByMiliSeconds(starttime)
									+ " (" + timeZoneName + ")" + "<br/>";

							message += labelid1154.getValue()
									+ ' '
									+ CalendarPatterns
											.getDateWithTimeByMiliSeconds(endtime)
									+ " (" + timeZoneName + ")" + "<br/>";

							// Fieldlanguagesvalues labelid1156 =
							// fieldmanagment.getFieldByIdAndLanguage(new
							// Long(1156), language_id);
							// message = labelid1156.getValue() + invitorName +
							// "<br/>";

							invitationManagement.sendInvitationReminderLink(
											message,
											inv.getBaseUrl(),
											mm.getEmail(),
											labelid1158.getValue() + " "
													+ ment.getAppointmentName(),
											inv.getHash());

							inv.setUpdatetime(now);
							invitationManagement.updateInvitation(inv);
						}

					}
				} else
					log.debug("Meeting is not in Reminder Range!");
			}
		}
	}

	// ----------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param appointmentId
	 * @param appointmentName
	 * @param appointmentDescription
	 * @param appointmentstart
	 * @param appointmentend
	 * @param isDaily
	 * @param isWeekly
	 * @param isMonthly
	 * @param isYearly
	 * @param categoryId
	 * @param remind
	 * @param mmClient
	 * @return
	 */
	public Long updateAppointment(Long appointmentId, String appointmentName,
			String appointmentDescription, Date appointmentstart,
			Date appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			List mmClient, Long user_id, String baseUrl, Long language_id,
			Boolean isPasswordProtected, String password, String iCalTimeZone) {

		try {

			return appointmentDao.updateAppointment(appointmentId,
					appointmentName, appointmentDescription, appointmentstart,
					appointmentend, isDaily, isWeekly, isMonthly, isYearly,
					categoryId, remind, mmClient, user_id, baseUrl,
					language_id, isPasswordProtected, password, iCalTimeZone);

		} catch (Exception err) {
			log.error("[updateAppointment]", err);
		}
		return null;
	}

	public Long updateAppointmentByTime(Long appointmentId,
			Date appointmentstart, Date appointmentend, Long user_id,
			String baseUrl, Long language_id, String iCalTimeZone) {

		try {
			return appointmentDao.updateAppointmentByTime(appointmentId,
					appointmentstart, appointmentend, user_id, baseUrl,
					language_id, iCalTimeZone);
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
		}
		return null;
	}

	/**
	 * Updating AppointMent object
	 */
	// ----------------------------------------------------------------------------------------------
	public Long updateAppointMent(Appointment point) {
		log.debug("AppointmentLogic.updateAppointment");

		return appointmentDao.updateAppointment(point);
	}
	// ----------------------------------------------------------------------------------------------

}
