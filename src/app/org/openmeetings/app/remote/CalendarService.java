package org.openmeetings.app.remote;

import java.util.Date;
import java.util.List;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.rooms.RoomTypes;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class CalendarService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			CalendarService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;

	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return appointmentLogic.getAppointmentByRange(users_id,
						starttime, endtime);
			}
		} catch (Exception err) {
			log.error("[getAppointmentByRange]", err);
		}
		return null;
	}

	public Appointment getNextAppointment(String SID) {

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return appointmentLogic.getNextAppointment();
			}
		} catch (Exception err) {
			log.error("[getNextAppointmentById]", err);
		}
		return null;

	}

	public List<Appointment> searchAppointmentByName(String SID,
			String appointmentName) {

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return appointmentLogic
						.searchAppointmentByName(appointmentName);
			}
		} catch (Exception err) {
			log.error("[searchAppointmentByName]", err);
		}
		return null;

	}

	public Long saveAppointment(String SID, String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Date appointmentstart, Date appointmentend, Boolean isDaily,
			Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
			Long categoryId, Long remind, @SuppressWarnings("rawtypes") List mmClient, Long roomType,
			String baseUrl, Long language_id) {

		log.debug("saveAppointMent SID:" + SID + ", baseUrl : " + baseUrl);

		try {
			Long users_id = sessionManagement.checkSession(SID);
			log.debug("saveAppointMent users_id:" + users_id);

			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				Long id = appointmentLogic.saveAppointment(appointmentName,
						users_id, appointmentLocation, appointmentDescription,
						appointmentstart, appointmentend, isDaily, isWeekly,
						isMonthly, isYearly, categoryId, remind, mmClient,
						roomType, baseUrl, language_id);

				return id;
			} else {
				log.error("saveAppointment : wrong user level");
			}
		} catch (Exception err) {
			log.error("[saveAppointment]", err);
		}
		return null;

	}

	public Long updateAppointmentTimeOnly(String SID, Long appointmentId,
			Date appointmentstart, Date appointmentend, String baseurl,
			Long language_id) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				log.debug("appointmentId " + appointmentId);

				appointmentLogic
						.getAppointMentById(appointmentId);

				Users user = userManagement.getUserById(users_id);

				return appointmentLogic.updateAppointmentByTime(appointmentId,
						appointmentstart, appointmentend, users_id, baseurl,
						language_id, user.getOmTimeZone().getIcal());
			}
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			err.printStackTrace();
		}
		return null;

	}

	public Long updateAppointment(String SID, Long appointmentId,
			String appointmentName, String appointmentLocation,
			String appointmentDescription, Date appointmentstart,
			Date appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			List<?> mmClient, Long roomType, String baseurl, Long language_id) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				RoomTypes rt = roommanagement.getRoomTypesById(roomType);

				Appointment app = appointmentLogic
						.getAppointMentById(appointmentId);

				Rooms room = app.getRoom();
				if (room != null) {

					room.setComment(appointmentDescription);
					room.setName(appointmentName);
					room.setRoomtype(rt);

					roommanagement.updateRoomObject(room);
				}

				Users user = userManagement.getUserById(users_id);

				return appointmentLogic.updateAppointment(appointmentId,
						appointmentName, appointmentDescription,
						appointmentstart, appointmentend, isDaily, isWeekly,
						isMonthly, isYearly, categoryId, remind, mmClient,
						users_id, baseurl, language_id, false, "", user
								.getOmTimeZone().getIcal());
			}
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			err.printStackTrace();
		}
		return null;

	}

	public Long deleteAppointment(String SID, Long appointmentId,
			Long language_id) {

		log.debug("deleteAppointment : " + appointmentId);

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				return appointmentLogic.deleteAppointment(appointmentId,
						users_id, language_id);

			}

		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
		}
		return null;

	}

	public Appointment getAppointmentByRoomId(String SID, Long room_id) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				Appointment appointment = new Appointment();

				Appointment appStored = appointmentDao.getAppointmentByRoomId(
						users_id, room_id);

				appointment.setAppointmentStarttime(appStored
						.getAppointmentStarttime());
				appointment.setAppointmentEndtime(appStored
						.getAppointmentEndtime());

				return appointment;
			}

		} catch (Exception err) {
			log.error("[getAppointmentByRoomId]", err);
		}
		return null;
	}

}
