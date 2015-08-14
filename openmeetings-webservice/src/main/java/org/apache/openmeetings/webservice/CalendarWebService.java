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
package org.apache.openmeetings.webservice;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypeDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.calendar.AppointmentReminderTypeDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.AppointmentCategory;
import org.apache.openmeetings.db.entity.calendar.AppointmentReminderType;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.webservice.error.ServiceException;
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
@WebService
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/calendar")
public class CalendarWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarWebService.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDao;
	@Autowired
	private AppointmentReminderTypeDao reminderTypeDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private RoomTypeDao roomTypeDao;

	/**
	 * Load appointments by a start / end range for the current SID
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param start
	 *            start time
	 * @param end
	 *            end time
	 *            
	 * @return - list of appointments in range
	 */
	@GET
	@Path("/{start}/{end}")
	public List<AppointmentDTO> range(@QueryParam("sid") @WebParam String sid, @PathParam("start") @WebParam Calendar start, @PathParam("end") @WebParam Calendar end) throws ServiceException {
		log.debug("range : startdate - " + start.getTime() + ", enddate - " + end.getTime());
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return AppointmentDTO.list(appointmentDao.getAppointmentsByRange(userId, start.getTime(), end.getTime()));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[range]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Load appointments by a start / end range for the userId
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param userid
	 *            the userId the calendar events should be loaded
	 * @param start
	 *            start time
	 * @param end
	 *            end time
	 *            
	 * @return - list of appointments in range
	 */
	@GET
	@Path("/{userid}/{start}/{end}")
	public List<AppointmentDTO> rangeForUser(
			@QueryParam("sid") @WebParam String sid
			, @PathParam("userid") @WebParam long userid
			, @PathParam("start") @WebParam Calendar start
			, @PathParam("end") @WebParam Calendar end) throws ServiceException
	{
		log.debug("rangeForUser : startdate - " + start.getTime() + ", enddate - " + end.getTime());
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				return AppointmentDTO.list(appointmentDao.getAppointmentsByRange(userid, start.getTime(), end.getTime()));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[rangeForUser]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Get the next Calendar event for the current user of the SID
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @return - next Calendar event
	 */
	@GET
	@Path("/next")
	public AppointmentDTO next(@QueryParam("sid") @WebParam String sid) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return new AppointmentDTO(appointmentDao.getNextAppointment(userId, new Date()));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[next]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Get the next Calendar event for userId
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            
	 * @return - next Calendar event
	 */
	@GET
	@Path("/next/{userid}")
	public AppointmentDTO nextForUser(@QueryParam("sid") @WebParam String sid, @PathParam("userid") @WebParam long userid) throws ServiceException {
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				return new AppointmentDTO(appointmentDao.getNextAppointment(userid, new Date()));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[nextForUser]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * 
	 * Load a calendar event by its room id
	 * 
	 * @param SID
	 * @param roomId
	 * @return - calendar event by its room id
	 */
	@GET
	@Path("/room/{roomid}")
	public AppointmentDTO getByRoom(@QueryParam("sid") @WebParam String sid, @PathParam("roomid") @WebParam long roomid) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Appointment app = appointmentDao.getAppointmentByOwnerRoom(userId, roomid);
				if (app != null) {
					return new AppointmentDTO(app);
				}
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getByRoom]", err);
			throw new ServiceException(err.getMessage());
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
	@GET
	@Path("/title/{title}")
	public List<AppointmentDTO> getByTitle(@QueryParam("sid") @WebParam String sid, @PathParam("title") @WebParam String title) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return AppointmentDTO.list(appointmentDao.searchAppointmentsByTitle(userId, title));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getByTitle]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Save an appointment
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointment
	 *            calendar event 
	 *            
	 * @return - appointment saved
	 */
	@POST
	@Path("/") //TODO FIXME update is also here for now
	public AppointmentDTO save(@QueryParam("sid") @WebParam String sid, @QueryParam("appointment") @WebParam AppointmentDTO appointment) throws ServiceException {
		//Seems to be create
		log.debug("save SID:" + sid);

		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("save userId:" + userId);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Appointment a = appointment.get(userDao, appointmentDao, reminderTypeDao, roomTypeDao);
				return new AppointmentDTO(appointmentDao.update(a, userId));
			} else {
				log.error("save : wrong user level");
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[save]", err);
			throw new ServiceException(err.getMessage());
		}
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
	 *            sample: '1,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1'
	 *            to add multiple clients you can use the same GET parameter in
	 *            the URL multiple times, for example:
	 *            &amp;mmClient='1,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1'&amp;mmClient='2,firstname,lastname,hans.tier@gmail.com,1,Etc/GMT+1'
	 *             (Please NOTE mmClient value is enclosed in single quotes)
	 * @param roomType
	 *            the room type for the calendar event
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
			String[] mmClient, Long roomType, Long languageId,
			Boolean isPasswordProtected, String password) throws ServiceException {
		try {

			Long userId = sessionDao.checkSession(SID);
			Set<Right> rights = userDao.getRights(userId);

			if (AuthLevelUtil.hasWebServiceLevel(rights) || AuthLevelUtil.hasAdminLevel(rights)) {
				// fine
			} else if (AuthLevelUtil.hasUserLevel(rights)) {
				// check if the appointment belongs to the current user
				Appointment a = appointmentDao.get(appointmentId);
				if (!a.getOwner().getId().equals(userId)) {
					throw new ServiceException("The Appointment cannot be updated by the given user");
				}
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}

			Appointment a = appointmentDao.get(appointmentId);
			a.setTitle(appointmentName);
			a.setLocation(appointmentLocation);
			a.setDescription(appointmentDescription);
			a.setStart(appointmentstart.getTime());
			a.setEnd(appointmentend.getTime());
			a.setIsDaily(isDaily);
			a.setIsWeekly(isWeekly);
			a.setIsMonthly(isMonthly);
			a.setIsYearly(isYearly);
			a.setCategory(appointmentCategoryDao.get(categoryId));
			a.setRemind(reminderTypeDao.get(remind));
			a.getRoom().setComment(appointmentDescription);
			a.getRoom().setName(appointmentName);
			a.getRoom().setRoomtype(roomTypeDao.get(roomType));
			a.setOwner(userDao.get(userId));
			a.setPasswordProtected(isPasswordProtected);
			a.setPassword(password);
			a.setMeetingMembers(new ArrayList<MeetingMember>());
			for (String singleClient : mmClient) {
				MeetingMember mm = appointmentLogic.getMeetingMember(userId, languageId, singleClient);
				mm.setAppointment(a);
				a.getMeetingMembers().add(mm);
			}
			return appointmentDao.update(a, userId).getId();
		} catch (Exception err) {
			log.error("[updateAppointment]", err);
			throw new ServiceException(err.getMessage());
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
	 * @param languageId
	 *            the language id in which the notifications for the deleted
	 *            appointment are send
	 * @return - id of appointment deleted
	 */
	public Long deleteAppointment(String SID, Long appointmentId, Long languageId) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);
			Set<Right> rights = userDao.getRights(userId);

			Appointment a = appointmentDao.get(appointmentId);
			if (AuthLevelUtil.hasWebServiceLevel(rights) || AuthLevelUtil.hasAdminLevel(rights)) {
				// fine
			} else if (AuthLevelUtil.hasUserLevel(rights)) {
				// check if the appointment belongs to the current user
				if (!a.getOwner().getId().equals(userId)) {
					throw new ServiceException("The Appointment cannot be updated by the given user");
				}
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}
			appointmentDao.delete(a, userId); //FIXME check this !!!!
			return a.getId();
		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Get all categories of calendar events
	 * 
	 * @param SID
	 * @return - all categories of calendar events
	 */
	public List<AppointmentCategory> getAppointmentCategoryList(String SID) {
		log.debug("AppointmenetCategoryService.getAppointmentCategoryList SID : " + SID);

		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				List<AppointmentCategory> res = appointmentCategoryDao.getAppointmentCategoryList();

				if (res == null || res.size() < 1) {
					log.debug("no AppointmentCategories found");
				} else {
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

	private List<AppointmentReminderTypeDTO> getReminders(List<AppointmentReminderType> list) {
		List<AppointmentReminderTypeDTO> result = new ArrayList<>(list.size());
		for (AppointmentReminderType rt : list) {
			result.add(new AppointmentReminderTypeDTO(rt));
		}
		return result;
	}
	/**
	 * Get all reminder types for calendar events
	 * 
	 * @param SID
	 * @return - all reminder types for calendar events
	 */
	public List<AppointmentReminderTypeDTO> getAppointmentReminderTypList(String SID) {
		log.debug("getAppointmentReminderTypList");

		try {
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return getReminders(reminderTypeDao.get());
			} else
				log.debug("getAppointmentReminderTypList  :error - wrong authlevel!");
		} catch (Exception err) {
			log.error("[getAppointmentReminderTypList]", err);
		}
		return null;
	}

	/**
	 * Adds a conference room that is only available for a period of time
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypesId
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 * @param validFromDate
	 *            valid from as Date format: dd.MM.yyyy
	 * @param validFromTime
	 *            valid to as time format: mm:hh
	 * @param validToDate
	 *            valid to Date format: dd.MM.yyyy
	 * @param validToTime
	 *            valid to time format: mm:hh
	 * @param isPasswordProtected
	 *            If the links send via EMail to invited people is password
	 *            protected
	 * @param password
	 *            Password for Invitations send via Mail
	 * @param reminderTypeId
	 *            1=none, 2=simple mail, 3=ICAL
	 * @param redirectURL
	 *            URL Users will be lead to if the Conference Time is elapsed
	 *            
	 * @return - id of the room in case of success, error code otherwise
	 * @throws ServiceException
	 *//*
	public Long addRoomWithModerationAndExternalTypeAndStartEnd(String SID,
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Boolean isPasswordProtected, String password, Long reminderTypeId,
			String redirectURL) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				int validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
				int validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();

				int validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
				int validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();

				log.info("validFromHour: " + validFromHour);
				log.info("validFromMinute: " + validFromMinute);

				Date fromDate = CalendarPatterns.parseDateBySeparator(validFromDate); // dd.MM.yyyy
				Date toDate = CalendarPatterns.parseDateBySeparator(validToDate); // dd.MM.yyyy

				if (fromDate == null || toDate == null) {
					throw new ServiceException("Invalid dates are passed");
				}
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(fromDate);
				calFrom.set(calFrom.get(Calendar.YEAR),
						calFrom.get(Calendar.MONTH),
						calFrom.get(Calendar.DATE), validFromHour,
						validFromMinute, 0);

				Calendar calTo = Calendar.getInstance();
				calTo.setTime(toDate);
				calTo.set(calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH),
						calTo.get(Calendar.DATE), validToHour, validToMinute, 0);

				Date dFrom = calFrom.getTime();
				Date dTo = calTo.getTime();

				log.info("validFromDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
				log.info("validToDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));

				Long roomId = roomManager.addExternalRoom(name,
						roomtypesId, comment, numberOfPartizipants, ispublic,
						null, appointment, isDemoRoom, demoTime,
						isModeratedRoom, null, null, externalRoomType, false, // allowUserQuestions
						false, // isAudioOnly
						true,  // allowFontStyles
						false, // isClosed
						redirectURL, false, true, false);

				if (roomId <= 0) {
					return roomId;
				}

				Appointment a = new Appointment();
				a.setTitle("appointmentName");
				a.setOwner(userDao.get(userId));
				a.setLocation("appointmentLocation");
				a.setDescription("appointmentDescription");
				a.setStart(dFrom);
				a.setEnd(dTo);
				a.setCategory(appointmentCategoryDao.get(1L));
				a.setRemind(reminderTypeDao.get(reminderTypeId));
				a.setRoom(roomDao.get(roomId));
				a.setPasswordProtected(isPasswordProtected);
				a.setPassword(password);
				a.setLanguageId(1L); //TODO check
				appointmentDao.update(a, userId); //FIXME verify !!!

				return roomId;

			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);

			throw new ServiceException(err.getMessage());
		}
		// return new Long(-1);
		// return numberOfPartizipants;
	}
*/
	/**
	 * Add a meeting member to a certain room. This is the same as adding an
	 * external user to a event in the calendar.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param languageId
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws ServiceException
	 */
	public Long addMeetingMemberRemindToRoom(String SID, Long roomId,
			String firstname, String lastname, String email, Long languageId) throws ServiceException {
		return addExternalMeetingMemberRemindToRoom(SID, roomId, firstname, lastname, email, languageId, null, null);
	}

	/**
	 * Add a meeting member to a certain room. This is the same as adding an
	 * external user to a event in the calendar. with a certain time zone
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param languageId
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 * @param jNameTimeZone
	 *            name of the timezone
	 * @param invitorName
	 *            name of invitation creators
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws ServiceException
	 */
	public Long addExternalMeetingMemberRemindToRoom(String SID, Long roomId,
			String firstname, String lastname, String email, Long languageId, String jNameTimeZone, String invitorName)
			throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Appointment a = appointmentDao.getAppointmentByRoom(roomId);

				if (email == null || a == null) {
					return -1L;
				}
				for (MeetingMember mm : a.getMeetingMembers()) {
					if (email.equals(mm.getUser().getAddress().getEmail())) {
						return mm.getId();
					}
				}
				MeetingMember mm = new MeetingMember();
				mm.setAppointment(a);
				mm.setUser(userDao.getContact(email, firstname, lastname, languageId, jNameTimeZone, userId));
				a.getMeetingMembers().add(mm);
				appointmentDao.update(a, userId);

				return mm.getId(); //FIXME check to return ID
			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addExternalMeetingMemberRemindToRoom] ", err);

			throw new ServiceException(err.getMessage());
		}
	}
}
