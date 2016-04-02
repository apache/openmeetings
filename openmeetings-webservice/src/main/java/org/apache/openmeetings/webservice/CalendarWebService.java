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
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CalendarService contains methods to create, edit delete calendar meetings
 * 
 * @author sebawagner
 * 
 */
@WebService(serviceName="org.apache.openmeetings.webservice.CalendarWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/calendar")
public class CalendarWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarWebService.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;

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
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/{start}/{end}")
	public List<AppointmentDTO> range(@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("start") @WebParam(name="start") Calendar start
			, @PathParam("end") @WebParam(name="end") Calendar end
			) throws ServiceException
	{
		log.debug("range : startdate - " + start.getTime() + ", enddate - " + end.getTime());
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return AppointmentDTO.list(appointmentDao.getInRange(userId, start.getTime(), end.getTime()));
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/{userid}/{start}/{end}")
	public List<AppointmentDTO> rangeForUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("userid") @WebParam(name="userid") long userid
			, @PathParam("start") @WebParam(name="start") Calendar start
			, @PathParam("end") @WebParam(name="end") Calendar end
			) throws ServiceException
	{
		log.debug("rangeForUser : startdate - " + start.getTime() + ", enddate - " + end.getTime());
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				return AppointmentDTO.list(appointmentDao.getInRange(userid, start.getTime(), end.getTime()));
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/next")
	public AppointmentDTO next(@QueryParam("sid") @WebParam(name="sid") String sid) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Appointment a = appointmentDao.getNext(userId, new Date());
				return a == null ? null : new AppointmentDTO(a);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param userid
	 *            the userId the calendar events should be loaded
	 *            
	 * @return - next Calendar event
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/next/{userid}")
	public AppointmentDTO nextForUser(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("userid") @WebParam(name="userid") long userid) throws ServiceException {
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				Appointment a = appointmentDao.getNext(userid, new Date());
				return a == null ? null : new AppointmentDTO(a);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 * @param roomid
	 * @return - calendar event by its room id
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/room/{roomid}")
	public AppointmentDTO getByRoom(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("roomid") @WebParam(name="roomid") long roomid) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Appointment app = appointmentDao.getByRoom(userId, roomid);
				if (app != null) {
					return new AppointmentDTO(app);
				}
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param title
	 *            the search string
	 *            
	 * @return - calendar event list
	 * @throws {@link ServiceException} in case of any error
	 */
	@GET
	@Path("/title/{title}")
	public List<AppointmentDTO> getByTitle(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("title") @WebParam(name="title") String title) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return AppointmentDTO.list(appointmentDao.searchAppointmentsByTitle(userId, title));
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointment
	 *            calendar event 
	 *            
	 * @return - appointment saved
	 * @throws {@link ServiceException} in case of any error
	 */
	@POST
	@Path("/") //TODO FIXME update is also here for now
	public AppointmentDTO save(@QueryParam("sid") @WebParam(name="sid") String sid, @QueryParam("appointment") @WebParam(name="appointment") AppointmentDTO appointment) throws ServiceException {
		//Seems to be create
		log.debug("save SID:" + sid);

		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("save userId:" + userId);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Appointment a = appointment.get(userDao, appointmentDao);
				return new AppointmentDTO(appointmentDao.update(a, userId));
			} else {
				log.error("save : wrong user level");
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[save]", err);
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
	 * @param sid
	 *            an authenticated SID
	 * @param id
	 *            the id to delete
	 * @throws {@link ServiceException} in case of any error
	 */
	@DELETE
	@Path("/{id}")
	public void delete(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			Set<Right> rights = userDao.getRights(userId);

			Appointment a = appointmentDao.get(id);
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
			appointmentDao.delete(a, userId);
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[delete]", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
