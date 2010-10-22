package org.openmeetings.app.remote;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.hibernate.beans.rooms.RoomTypes;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class CalendarService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			CalendarService.class, ScopeApplicationAdapter.webAppRootKey);

	private static CalendarService instance = null;

	public static synchronized CalendarService getInstance() {
		if (instance == null) {
			instance = new CalendarService();
		}

		return instance;
	}

	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				return AppointmentLogic.getInstance().getAppointmentByRange(
						users_id, starttime, endtime);
			}
		} catch (Exception err) {
			log.error("[getAppointmentByRange]", err);
		}
		return null;
	}

	public Appointment getNextAppointment(String SID) {

		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				return AppointmentLogic.getInstance().getNextAppointment();
			}
		} catch (Exception err) {
			log.error("[getNextAppointmentById]", err);
		}
		return null;

	}

	public List<Appointment> searchAppointmentByName(String SID,
			String appointmentName) {

		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				return AppointmentLogic.getInstance().searchAppointmentByName(
						appointmentName);
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
			Long categoryId, Long remind, List mmClient, 
			Long roomType, String baseUrl, Long language_id) {

		log.debug("saveAppointMent SID:" + SID + ", baseUrl : " + baseUrl);

		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			log.debug("saveAppointMent users_id:" + users_id);
			
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);

			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				Long id = AppointmentLogic.getInstance().saveAppointment(
						appointmentName, users_id, appointmentLocation,
						appointmentDescription, appointmentstart,
						appointmentend, isDaily, isWeekly, isMonthly, isYearly,
						categoryId, remind, mmClient, roomType, baseUrl, language_id);

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

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				log.debug("appointmentId " + appointmentId);

				Appointment app = AppointmentLogic.getInstance()
						.getAppointMentById(appointmentId);

				Users user = Usermanagement.getInstance().getUserById(users_id);
				
				return AppointmentLogic.getInstance().updateAppointmentByTime(
						appointmentId, 
						appointmentstart, appointmentend, 
						users_id, baseurl, language_id, 
						user.getOmTimeZone().getIcal());
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
			List mmClient, Long roomType, String baseurl, Long language_id) {
		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				RoomTypes rt = Roommanagement.getInstance().getRoomTypesById(
						roomType);

				Appointment app = AppointmentLogic.getInstance()
						.getAppointMentById(appointmentId);

				Rooms room = app.getRoom();
				if (room != null) {

					room.setComment(appointmentDescription);
					room.setName(appointmentName);
					room.setRoomtype(rt);

					Roommanagement.getInstance().updateRoomObject(room);
				}

				Users user = Usermanagement.getInstance().getUserById(users_id);
				
				return AppointmentLogic.getInstance().updateAppointment(
						appointmentId, appointmentName, appointmentDescription,
						appointmentstart, appointmentend, isDaily, isWeekly,
						isMonthly, isYearly, categoryId, remind, mmClient,
						users_id, baseurl, language_id, false, "", 
						user.getOmTimeZone().getIcal());
			}
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			err.printStackTrace();
		}
		return null;

	}

	public Long deleteAppointment(String SID, Long appointmentId, Long language_id) {

		log.debug("deleteAppointment : " + appointmentId);

		try {

			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {

				Appointment app = AppointmentLogic.getInstance()
						.getAppointMentById(appointmentId);
				Roommanagement.getInstance().deleteRoom(app.getRoom());

				return AppointmentLogic.getInstance().deleteAppointment(
						appointmentId, users_id, language_id);
			}

		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
		}
		return null;

	}

}
