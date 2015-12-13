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
package org.apache.openmeetings.axis.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.calendar.beans.AppointmentDTO;
import org.apache.openmeetings.data.calendar.beans.Day;
import org.apache.openmeetings.data.calendar.beans.Week;
import org.apache.openmeetings.data.calendar.daos.AppointmentCategoryDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UserContactsDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentCategory;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.UserContact;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.math.TimezoneUtil;
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
	private AppointmentDao appointmentDao;
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private UserContactsDao userContactsDaoImpl;

	/**
	 * Load appointments by a start / end range for the current SID
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param starttime
	 *            start time, yyyy-mm-dd
	 * @param endtime
	 *            end time, yyyy-mm-dd
	 *            
	 * @return - list of appointments in range
	 */
	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param userId
	 *            the userId the calendar events should be loaded
	 * @param starttime
	 *            start time, yyyy-mm-dd
	 * @param endtime
	 *            end time, yyyy-mm-dd
	 *            
	 * @return - list of appointments in range
	 */
	public List<Appointment> getAppointmentByRangeForUserId(String SID,
			long userId, Date starttime, Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @return - next Calendar event
	 */
	public Appointment getNextAppointment(String SID) {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            
	 * @return - next Calendar event
	 */
	public Appointment getNextAppointmentForUserId(String SID, long userId) {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentName
	 *            the search string
	 *            
	 * @return - calendar event list
	 */
	public List<Appointment> searchAppointmentByName(String SID,
			String appointmentName) {

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentName
	 *            name of the calendar event
	 * @param appointmentLocation
	 *            location info text of the calendar event
	 * @param appointmentDescription
	 *            description test of the calendar event
	 * @param appointmentstart
	 *            start as Date yyyy-mm-ddThh:mm:ss
	 * @param appointmentend
	 *            end as Date yyyy-mm-ddThh:mm:ss
	 * @param isDaily
	 *            if the calendar event should be repeated daily (not
	 *            implemented)
	 * @param isWeekly
	 *            if the calendar event should be repeated weekly (not
	 *            implemented)
	 * @param isMonthly
	 *            if the calendar event should be repeated monthly (not
	 *            implemented)
	 * @param isYearly
	 *            if the calendar event should be repeated yearly (not
	 *            implemented)
	 * @param categoryId
	 *            the category id of the calendar event
	 * @param remind
	 *            the reminder type of the calendar event
	 * @param mmClient
	 *            List of clients, comma separated string, <br/>
	 *            sample: 1,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1
	 *            to add multiple clients you can use the same GET parameter in
	 *            the URL multiple times, for example:
	 *            &amp;mmClient=1,firstname,lastname,hans
	 *            .tier@gmail.com,1,Etc/GMT+1&amp;mmClient
	 *            =2,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1
	 * @param roomType
	 *            the room type for the calendar event
	 * @param baseUrl
	 *            the base URL for the invitations
	 * @param languageId
	 *            the language id of the calendar event, notification emails
	 *            will be send in this language
	 * @param isPasswordProtected
	 *            if the room is password protected
	 * @param password
	 *            the password for the room
	 *            
	 * @return - id of appointment saved
	 */
	public Long saveAppointment(String SID, String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind, String[] mmClient,
			Long roomType, String baseUrl, Long languageId,
			Boolean isPasswordProtected, String password, long roomId) {

		log.debug("saveAppointMent SID:" + SID + ", baseUrl : " + baseUrl);

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			log.debug("saveAppointMent users_id:" + users_id);

			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Map<String, String>> newList = new ArrayList<Map<String, String>>();

				for (String singleClient : mmClient) {
					String[] params = singleClient.split(",");
					Map<String, String> map = new HashMap<String, String>();
					map.put("meetingMemberId", params[0]);
					map.put("firstname", params[1]);
					map.put("lastname", params[2]);
					map.put("email", params[3]);
					map.put("userId", params[4]);
					map.put("jNameTimeZone", params[5]);
					newList.add(map);
				}

				Long id = appointmentLogic.saveAppointment(appointmentName,
						users_id, appointmentLocation, appointmentDescription,
						appointmentstart.getTime(), appointmentend.getTime(),
						isDaily, isWeekly, isMonthly, isYearly, categoryId,
						remind, newList, roomType, baseUrl, languageId,
						isPasswordProtected, password, roomId);

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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentId
	 *            the calendar event that should be updated
	 * @param appointmentstart
	 *            start yyyy-mm-dd
	 * @param appointmentend
	 *            end yyyy-mm-dd
	 * @param baseurl
	 *            the base URL for the invitations that will be send by email
	 * @param languageId
	 *            the language id
	 *            
	 * @return - id of appointment updated
	 */
	public Long updateAppointmentTimeOnly(String SID, Long appointmentId,
			Date appointmentstart, Date appointmentend, String baseurl,
			Long languageId) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("appointmentId " + appointmentId);

				appointmentLogic.getAppointMentById(appointmentId);

				User user = userManager.getUserById(users_id);

				return appointmentDao.updateAppointmentByTime(appointmentId,
						appointmentstart, appointmentend, users_id, baseurl,
						languageId, user.getOmTimeZone().getIcal());
			}
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			err.printStackTrace();
		}
		return null;

	}

	/**
	 * Save an appointment
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointmentId
	 *            the id to update
	 * @param appointmentName
	 *            name of the calendar event
	 * @param appointmentLocation
	 *            location info text of the calendar event
	 * @param appointmentDescription
	 *            description test of the calendar event
	 * @param appointmentstart
	 *            start as Date yyyy-mm-ddThh:mm:ss
	 * @param appointmentend
	 *            end as Date yyyy-mm-ddThh:mm:ss
	 * @param isDaily
	 *            if the calendar event should be repeated daily (not
	 *            implemented)
	 * @param isWeekly
	 *            if the calendar event should be repeated weekly (not
	 *            implemented)
	 * @param isMonthly
	 *            if the calendar event should be repeated monthly (not
	 *            implemented)
	 * @param isYearly
	 *            if the calendar event should be repeated yearly (not
	 *            implemented)
	 * @param categoryId
	 *            the category id of the calendar event
	 * @param remind
	 *            the reminder type of the calendar event
	 * @param mmClient
	 *            List of clients, comma separated string, <br/>
	 *            sample: 1,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1
	 *            to add multiple clients you can use the same GET parameter in
	 *            the URL multiple times, for example:
	 *            &amp;mmClient=1,firstname,lastname,hans
	 *            .tier@gmail.com,1,Etc/GMT+1&amp;mmClient
	 *            =2,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1
	 * @param roomType
	 *            the room type for the calendar event
	 * @param baseUrl
	 *            the base URL for the invitations
	 * @param languageId
	 *            the language id of the calendar event, notification emails
	 *            will be send in this language
	 * @param isPasswordProtected
	 *            if the room is password protected
	 * @param password
	 *            the password for the room
	 *            
	 * @return - id of appointment updated
	 */
	public Long updateAppointment(String SID, Long appointmentId,
			String appointmentName, String appointmentLocation,
			String appointmentDescription, Calendar appointmentstart,
			Calendar appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			String[] mmClient, Long roomType, String baseurl, Long languageId,
			Boolean isPasswordProtected, String password) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {
				// check if the appointment belongs to the current user
				Appointment appointment = appointmentLogic
						.getAppointMentById(appointmentId);
				if (!appointment.getUserId().getUser_id().equals(users_id)) {
					throw new AxisFault(
							"The Appointment cannot be updated by the given user");
				}
			} else if (authLevelUtil.checkUserLevel(user_level)) {
				// fine
			} else {
				throw new AxisFault(
						"Not allowed to preform that action, Authenticate the SID first");
			}

			List<Map<String, String>> newList = new ArrayList<Map<String, String>>();

			for (String singleClient : mmClient) {
				String[] params = singleClient.split(",");
				Map<String, String> map = new HashMap<String, String>();
				map.put("meetingMemberId", params[0]);
				map.put("firstname", params[1]);
				map.put("lastname", params[2]);
				map.put("email", params[3]);
				map.put("userId", params[4]);
				map.put("jNameTimeZone", params[5]);
				newList.add(map);
			}

			log.debug("updateAppointment");

			RoomType rt = roomManager.getRoomTypesById(roomType);

			Appointment app = appointmentLogic
					.getAppointMentById(appointmentId);

			Room room = app.getRoom();
			if (room != null) {

				room.setComment(appointmentDescription);
				room.setName(appointmentName);
				room.setRoomtype(rt);

				roomDao.update(room, users_id);
			}

			User user = userManager.getUserById(users_id);

			return appointmentDao.updateAppointment(appointmentId,
					appointmentName, appointmentDescription, appointmentstart
							.getTime(), appointmentend.getTime(), isDaily,
					isWeekly, isMonthly, isYearly, categoryId, remind, newList,
					users_id, baseurl, languageId, isPasswordProtected,
					password, user.getOmTimeZone().getIcal(),
					appointmentLocation);

		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * 
	 * delete a calendar event
	 * 
	 * If the given SID is from an Administrator or Web-Service user, the user
	 * can delete any appointment.<br/>
	 * If the SID is assigned to a simple user, he can only delete appointments
	 * where he is also the owner/creator of the appointment
	 * 
	 * @param SID
	 *            an authenticated SID
	 * @param appointmentId
	 *            the id to delete
	 * @param language_id
	 *            the language id in which the notifications for the deleted
	 *            appointment are send
	 * @return - id of appointment deleted
	 */
	public Long deleteAppointment(String SID, Long appointmentId,
			Long language_id) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				return appointmentLogic.deleteAppointment(appointmentId,
						users_id, language_id);

			} else if (authLevelUtil.checkUserLevel(user_level)) {

				Appointment appointment = appointmentLogic
						.getAppointMentById(appointmentId);

				if (!appointment.getUserId().getUser_id().equals(users_id)) {
					throw new AxisFault(
							"The Appointment cannot be deleted by the given user");
				}

				return appointmentLogic.deleteAppointment(appointmentId,
						users_id, language_id);

			}

		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
			throw new AxisFault(err.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * Load a calendar event by its room id
	 * 
	 * @param SID
	 * @param room_id
	 * @return - calendar event by its room id
	 */
	public Appointment getAppointmentByRoomId(String SID, Long room_id) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @return - all categories of calendar events
	 */
	public List<AppointmentCategory> getAppointmentCategoryList(String SID) {
		log.debug("AppointmenetCategoryService.getAppointmentCategoryList SID : "
				+ SID);

		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

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
	 * @return - all reminder types for calendar events
	 */
	public List<AppointmentReminderTyps> getAppointmentReminderTypList(
			String SID) {
		log.debug("getAppointmentReminderTypList");

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

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

	/**
	 * Get the appointments (calendar events) for the given requestUserId <br/>
	 * The TimeZone can be either given by the Id of the timezone in the table
	 * "om_timezone" with the param timeZoneIdA <br/>
	 * Or with the java name of the TimeZone in the param javaTimeZoneName
	 * 
	 * @param SID
	 *            a valid user id
	 * @param firstDayInWeek
	 *            the first dayin week, 0=Sunday, 1=Monday, ...
	 * @param startDate
	 *            the date it should start with
	 * @param requestUserId
	 *            the user id the calendar events are requested, if it is not
	 *            the user id of the SID then the SID's user needs to have the
	 *            right to watch those events
	 * @param omTimeZoneId
	 *            the id of the timezone (alternativly use javaTimeZoneName)
	 * @param javaTimeZoneName
	 *            the name of the java time zone, see <a
	 *            href="http://en.wikipedia.org/wiki/Time_zone#Java"
	 *            target="_blank"
	 *            >http://en.wikipedia.org/wiki/Time_zone#Java</a>
	 *            
	 * @return - appointments grouped by weeks
	 * @throws AxisFault
	 */
	public List<Week> getAppointmentsByWeekCalendar(String SID,
			int firstDayInWeek, Date startDate, Long requestUserId,
			Long omTimeZoneId, String javaTimeZoneName) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {
				
				if (!requestUserId.equals(users_id)) {
					UserContact userContacts = userContactsDaoImpl
							.getUserContactByShareCalendar(requestUserId, true,
									users_id);
					if (userContacts == null) {
						throw new Exception(
								"Your are not allowed to see this calendar");
					}
				}

				TimeZone timezone = null;

				if (javaTimeZoneName != null && !javaTimeZoneName.isEmpty()) {
					timezone = TimeZone.getTimeZone(javaTimeZoneName);
					if (timezone == null) {
						throw new Exception("Invalid javaTimeZoneName given");
					}
				}

				if (omTimeZoneId > 0) {
					timezone = timezoneUtil
							.getTimezoneByOmTimeZoneId(omTimeZoneId);
				}
				
				if (timezone == null) {
					throw new Exception("No timeZone given");
				}

				// Calculate the first day of a calendar based on the first
				// showing day of the week
				List<Week> weeks = new ArrayList<Week>(6);
				Calendar currentDate = Calendar.getInstance();
				currentDate.setTime(startDate);
				currentDate.set(Calendar.HOUR_OF_DAY, 12); // set to 12 to prevent timezone issues
				currentDate.set(Calendar.DATE, 1);

				int currentWeekDay = currentDate.get(Calendar.DAY_OF_WEEK);

				Calendar startWeekDay = Calendar.getInstance();

				log.debug("currentWeekDay -- " + currentWeekDay);
				log.debug("firstDayInWeek -- " + firstDayInWeek);

				if (currentWeekDay == firstDayInWeek) {

					log.debug("ARE equal currentWeekDay -- ");

					startWeekDay.setTime(currentDate.getTime());

				} else {

					startWeekDay
							.setTimeInMillis((currentDate.getTimeInMillis() - ((currentWeekDay - 1) * 86400000)));

					if (currentWeekDay > firstDayInWeek) {
						startWeekDay.setTimeInMillis(startWeekDay
								.getTimeInMillis()
								+ (firstDayInWeek * 86400000));
					} else {
						startWeekDay.setTimeInMillis(startWeekDay
								.getTimeInMillis()
								- (firstDayInWeek * 86400000));
					}

				}

				Calendar calStart = Calendar.getInstance(timezone);
				calStart.setTime(startWeekDay.getTime());

				Calendar calEnd = Calendar.getInstance(timezone);
				// every month page in our calendar shows 42 days
				calEnd.setTime(new Date(startWeekDay.getTime().getTime()
						+ (42L * 86400000L)));

				List<Appointment> appointments = appointmentDao
						.getAppointmentsByRange(requestUserId,
								calStart.getTime(), calEnd.getTime());

				log.debug("startWeekDay 2" + startWeekDay.getTime());
				log.debug("startWeekDay Number of appointments "
						+ appointments.size());

				long z = 0;

				for (int k = 0; k < 6; k++) { // 6 weeks per monthly summary

					Week week = new Week();

					for (int i = 0; i < 7; i++) { // 7 days a week

						Calendar tCal = Calendar.getInstance(timezone);
						tCal.setTimeInMillis(startWeekDay.getTimeInMillis()
								+ (z * 86400000L));
						
						Day day = new Day(tCal.getTime());
						
						for (Appointment appointment : appointments) {
							if (appointment
									.appointmentStartAsCalendar(timezone).get(
											Calendar.MONTH) == tCal
									.get(Calendar.MONTH)
									&& appointment.appointmentStartAsCalendar(
											timezone).get(Calendar.DATE) == tCal
											.get(Calendar.DATE)) {
								day.getAppointments().add(
										new AppointmentDTO(appointment,
												timezone));
							}
						}

						week.getDays().add(day);
						z++;
					}

					weeks.add(week);
				}

				return weeks;

			}

		} catch (Exception err) {
			log.error("[getAppointmentReminderTypList]", err);
			throw new AxisFault(err.getMessage());
		}
		return null;
	}

}
