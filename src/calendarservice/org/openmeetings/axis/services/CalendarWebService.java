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
package org.openmeetings.axis.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.beans.AppointmentDTO;
import org.openmeetings.app.data.calendar.beans.Day;
import org.openmeetings.app.data.calendar.beans.Week;
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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CalendarService contains methods to create, edit delete calendar meetings
 * 
 * @author sebawagner
 * @webservice CalendarService
 *
 */
public class CalendarWebService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			CalendarWebService.class, OpenmeetingsVariables.webAppRootKey);
	
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

	/**
	 * Load appointments by a start / end range for the current SID
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param starttime start time, yyyy-mm-dd
	 * @param endtime end time, yyyy-mm-dd
	 * @return
	 */
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
	
	/**
	 * Load appointments by a start / end range for the userId
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param userId the userId the calendar events should be loaded
	 * @param starttime start time, yyyy-mm-dd
	 * @param endtime end time, yyyy-mm-dd
	 * @return
	 */
	public List<Appointment> getAppointmentByRangeForUserId(String SID, long userId, Date starttime,
			Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

				return appointmentLogic.getAppointmentByRange(userId,
						starttime, endtime);
			}
		} catch (Exception err) {
			log.error("[getAppointmentByRangeForUserId]", err);
		}
		return null;
	}

	/**
	 * Get the next Calendar event for the current user of the SID
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @return
	 */
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
	
	/**
	 * Get the next Calendar event for userId
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @return
	 */
	public Appointment getNextAppointmentForUserId(String SID, long userId) {

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

	/**
	 * Search a calendar event for the current SID
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentName the search string
	 * @return
	 */
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

	/**
	 * Save an appointment
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentName name of the calendar event
	 * @param appointmentLocation location info text of the calendar event
	 * @param appointmentDescription description test of the calendar event
	 * @param appointmentstart start as Date yyyy-mm-dd
	 * @param appointmentend end as Date yyyy-mm-dd
	 * @param isDaily if the calendar event should be repeated daily (not implemented)
	 * @param isWeekly if the calendar event should be repeated weekly (not implemented)
	 * @param isMonthly if the calendar event should be repeated monthly (not implemented)
	 * @param isYearly if the calendar event should be repeated yearly (not implemented)
	 * @param categoryId the category id of the calendar event
	 * @param remind the reminder type of the calendar event
	 * @param mmClient List of clients
	 * @param roomType the room type for the calendar event
	 * @param baseUrl the base URL for the invitations
	 * @param language_id the language id of the calendar event
	 * @return
	 */
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
				
				//FIXME: Check if the event is also the event of the current SID

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

	/**
	 * Update an calendar event time only
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentId the calendar event that should be updated
	 * @param appointmentstart start yyyy-mm-dd
	 * @param appointmentend end yyyy-mm-dd
	 * @param baseurl the base URL for the invitations that will be send by email
	 * @param language_id the language id
	 * @return
	 */
	public Long updateAppointmentTimeOnly(String SID, Long appointmentId,
			Date appointmentstart, Date appointmentend, String baseurl,
			Long language_id) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {

				log.debug("updateAppointment");
				//FIXME: Check if the event is also the event of the current SID

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

	/**
	 * 
	 * Update a calendar event all attributes
	 * 
	 * @param SID
	 * @param appointmentId
	 * @param appointmentName
	 * @param appointmentLocation
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
	 * @param roomType
	 * @param baseurl
	 * @param language_id
	 * @return
	 */
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

	/**
	 * 
	 * delete a calendar event
	 * 
	 * @param SID
	 * @param appointmentId
	 * @param language_id
	 * @return
	 */
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

	/**
	 * 
	 * Load a calendar event by its room id
	 * 
	 * @param SID
	 * @param room_id
	 * @return
	 */
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
	
	/**
	 * Get all categories of calendar events
	 * 
	 * @param SID
	 * @return
	 */
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
	 * Get all reminder types for calendar events
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
	
	public static void main(String... args) {
		
		Calendar cal =Calendar.getInstance();
		cal.set(Calendar.MONTH, 1);
		
		new CalendarWebService().getAppointmentsByWeekCalendar(1, cal.getTime());
		
	}
	
	public List<Week> getAppointmentsByWeekCalendar(int firstDayInWeek, Date startDate) {
		
		// Calculate the first day of a calendar based on the first showing day
		// of the week
		List<Week> weeks = new ArrayList<Week>(6);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(startDate);
		currentDate.set(Calendar.DATE, 1);
		
		int currentWeekDay = currentDate.get(Calendar.DAY_OF_WEEK);
		
		Calendar startWeekDay = Calendar.getInstance();
		startWeekDay.setTimeInMillis((currentDate.getTimeInMillis() - ((currentWeekDay-1) * 86400000)));
		
		log.debug("startWeekDay 1" +startWeekDay.getTime());
		
		if (currentWeekDay == 1) {
			startWeekDay.setTimeInMillis(startWeekDay.getTimeInMillis() - ((7 - firstDayInWeek) * 86400000));
		} else {
			
			if (currentWeekDay > firstDayInWeek) {
				startWeekDay.setTimeInMillis(startWeekDay.getTimeInMillis() + (firstDayInWeek * 86400000));
			} else {
				startWeekDay.setTimeInMillis(startWeekDay.getTimeInMillis() - (firstDayInWeek * 86400000));
			}
			
		}
		
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startWeekDay.getTime());
		
		Calendar calEnd = Calendar.getInstance();
		// every month page in our calendar shows 42 days
		calEnd.setTime(new Date(startWeekDay.getTime().getTime()
				+ (42L * 86400000L)));
										
		
		List<Appointment> appointments = appointmentDao.getAppointmentsByRange(1L, calStart.getTime(), calEnd.getTime());
		
		log.debug("startWeekDay 2"+startWeekDay.getTime());
		log.debug("startWeekDay Number of appointments "+appointments.size());
		
		long z = 0;
		
		for (int k = 0; k < 6; k++) { // 6 weeks per monthly summary
			
			Week week = new Week();
			
			for (int i = 0; i < 7; i++) { // 7 days a week
				
				Calendar tCal = Calendar.getInstance();
				tCal.setTimeInMillis(startWeekDay.getTimeInMillis()
						+ (z * 86400000L));
				
				Day day = new Day(tCal.getTime());
				
				for (Appointment appointment : appointments) {
					if (appointment.appointmentStartAsCalendar().get(
							Calendar.MONTH) == tCal.get(Calendar.MONTH)
							&& appointment.appointmentStartAsCalendar().get(
									Calendar.DATE) == tCal.get(Calendar.DATE)) {
						day.getAppointments().add(
								new AppointmentDTO(appointment));
					}
				}
				
				
				week.getDays().add(day);
				z++;
			}
			
			weeks.add(week);
		}
		
		return weeks;
	}

}
