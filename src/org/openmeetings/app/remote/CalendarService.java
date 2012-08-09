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

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.persistence.beans.rooms.RoomTypes;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.utils.math.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class CalendarService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			CalendarService.class, OpenmeetingsVariables.webAppRootKey);

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
	@Autowired
	private AppointmentCategoryDaoImpl appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	@Autowired
	private TimezoneUtil timezoneUtil;

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
			String appointmentstartStr, String appointmentendStr, Boolean isDaily,
			Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
			Long categoryId, Long remind, @SuppressWarnings("rawtypes") List mmClient, Long roomType,
			String baseUrl, Long language_id, Boolean isPasswordProtected,
			String password, long roomId) {

		log.debug("saveAppointMent SID:" + SID + ", baseUrl : " + baseUrl);

		try {
			Long users_id = sessionManagement.checkSession(SID);
			log.debug("saveAppointMent users_id:" + users_id);

			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {
				
				Users us = userManagement.getUserById(users_id);
				
				// Refactor the given time ignoring the Date is always UTC!
				TimeZone timezone = timezoneUtil.getTimezoneByUser(us);
				
				//Transform the user time in a local time of the server 
				//machines locale timeZone and store that to the db
				Date appointmentstart = new Date(TimezoneUtil.getCalendarInTimezone(appointmentstartStr, timezone).getTime().getTime());
				Date appointmentend = new Date(TimezoneUtil.getCalendarInTimezone(appointmentendStr, timezone).getTime().getTime());

				log.debug("timezone "+timezone);
				log.debug("appointmentstartStr "+appointmentstartStr);
				log.debug("appointmentendStr "+appointmentendStr);
				log.debug("appointmentstart "+appointmentstart);
				log.debug("appointmentend   "+appointmentend);
				
				Long id = appointmentLogic.saveAppointment(appointmentName,
						users_id, appointmentLocation, appointmentDescription,
						appointmentstart, appointmentend, isDaily, isWeekly,
						isMonthly, isYearly, categoryId, remind, mmClient,
						roomType, baseUrl, language_id, isPasswordProtected, password, roomId);

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
			String appointmentstartStr, String appointmentendStr, String baseurl,
			Long language_id) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				log.debug("appointmentId " + appointmentId);
				
				Users us = userManagement.getUserById(users_id);
				
				// Refactor the given time ignoring the Date is always UTC!
				TimeZone timezone = timezoneUtil.getTimezoneByUser(us);
				
				//Transform the user time in a local time of the server 
				//machines locale timeZone and store that to the db
				Date appointmentstart = new Date(TimezoneUtil.getCalendarInTimezone(appointmentstartStr, timezone).getTime().getTime());
				Date appointmentend = new Date(TimezoneUtil.getCalendarInTimezone(appointmentendStr, timezone).getTime().getTime());

				log.debug("up timezone "+timezone);
				log.debug("up appointmentstartStr "+appointmentstartStr);
				log.debug("up appointmentendStr "+appointmentendStr);
				log.debug("up appointmentstart "+appointmentstart);
				log.debug("up appointmentend   "+appointmentend);

				appointmentLogic
						.getAppointMentById(appointmentId);

				Users user = userManagement.getUserById(users_id);
				
				return appointmentDao.updateAppointmentByTime(appointmentId,
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
			String appointmentName, 
			String appointmentLocation, String appointmentDescription,
			String appointmentstartStr, String appointmentendStr, Boolean isDaily,
			Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
			Long categoryId, Long remind, @SuppressWarnings("rawtypes") List mmClient, Long roomType,
			String baseUrl, Long language_id, Boolean isPasswordProtected,
			String password, long roomId) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				log.debug("updateAppointment");

				Appointment app = appointmentLogic.getAppointMentById(appointmentId);
				Rooms room = app.getRoom();
				if (roomId > 0) {
					if ( room.getRooms_id() != roomId) {
						app.setRoom(roommanagement.getRoomById(roomId));
						appointmentDao.updateAppointment(app);
						boolean isAppRoom = room.getAppointment();
						if (isAppRoom) {
							roommanagement.deleteRoom(room);
						}
					}
				} else {
					RoomTypes rt = roommanagement.getRoomTypesById(roomType);
	
					if (room != null) {
	
						room.setComment(appointmentDescription);
						room.setName(appointmentName);
						room.setRoomtype(rt);
	
						roommanagement.updateRoomObject(room);
					}
				}
				Users user = userManagement.getUserById(users_id);
				
				// Refactor the given time ignoring the Date is always UTC!
				TimeZone timezone = timezoneUtil.getTimezoneByUser(user);
				
				//Transform the user time in a local time of the server 
				//machines locale timeZone and store that to the db
				Date appointmentstart = new Date(TimezoneUtil.getCalendarInTimezone(appointmentstartStr, timezone).getTime().getTime());
				Date appointmentend = new Date(TimezoneUtil.getCalendarInTimezone(appointmentendStr, timezone).getTime().getTime());

				log.debug("up2 timezone "+timezone);
				log.debug("up2 appointmentstartStr "+appointmentstartStr);
				log.debug("up2 appointmentendStr "+appointmentendStr);
				log.debug("up2 appointmentstart "+appointmentstart);
				log.debug("up2 appointmentend "+appointmentend);

				return appointmentDao.updateAppointment(appointmentId,
						appointmentName, appointmentDescription,
						appointmentstart, appointmentend, isDaily, isWeekly,
						isMonthly, isYearly, categoryId, remind, mmClient,
						users_id, baseUrl, language_id, isPasswordProtected, password, user
								.getOmTimeZone().getIcal(), appointmentLocation);
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
	
	public List<AppointmentCategory> getAppointmentCategoryList(String SID) {
		log.debug("AppointmenetCategoryService.getAppointmentCategoryList SID : "
				+ SID);

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				List<AppointmentCategory> res = appointmentCategoryDaoImpl
						.getAppointmentCategoryList();

				if (res == null || res.size() < 1)
					log.debug("no AppointmentCategories found");
				else {
					for (int i = 0; i < res.size(); i++) {
						AppointmentCategory ac = res.get(i);
						log.debug("found appCategory : " + ac.getName());
					}
				}

				return res;
			} else {
				log.error("AppointmenetCategoryService.getAppointmentCategoryList : UserLevel Error");
			}
		} catch (Exception err) {
			log.error("[getAppointmentCategory]", err);
		}
		return null;

	}
	
	/**
	 * 
	 * @param SID
	 * @return
	 */
	public List<AppointmentReminderTyps> getAppointmentReminderTypList(
			String SID) {
		log.debug("getAppointmentReminderTypList");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				List<AppointmentReminderTyps> res = appointmentReminderTypDaoImpl
						.getAppointmentReminderTypList();

				if (res == null || res.size() < 1) {
					log.debug("no remindertyps found!");
				} else {
					for (int i = 0; i < res.size(); i++) {
						log.debug("found reminder " + res.get(i).getName());
					}
				}

				return res;
			} else
				log.debug("getAppointmentReminderTypList  :error - wrong authlevel!");
		} catch (Exception err) {
			log.error("[getAppointmentReminderTypList]", err);
		}
		return null;
	}

}
