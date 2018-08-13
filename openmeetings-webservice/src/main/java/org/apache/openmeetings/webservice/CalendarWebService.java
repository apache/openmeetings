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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 * CalendarService contains methods to create, edit delete calendar meetings
 *
 * @author sebawagner
 *
 */
@Service("calendarWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.CalendarWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/calendar")
public class CalendarWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarWebService.class, getWebAppRootKey());

	private static AppointmentDao getDao() {
		return getBean(AppointmentDao.class);
	}
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
	public List<AppointmentDTO> range(@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("start") @WebParam(name="start") Calendar start
			, @PathParam("end") @WebParam(name="end") Calendar end
			)
	{
		log.debug("range : startdate - {} , enddate - {}"
				, start == null ? "" : start.getTime()
				, end == null ? "" : end.getTime());
		return performCall(sid, User.Right.Room
				, sd -> AppointmentDTO.list(getDao().getInRange(sd.getUserId(), start.getTime(), end.getTime())));
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
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("userid") @WebParam(name="userid") long userid
			, @PathParam("start") @WebParam(name="start") Calendar start
			, @PathParam("end") @WebParam(name="end") Calendar end
			)
	{
		log.debug("rangeForUser : startdate - {} , enddate - {}"
				, start == null ? "" : start.getTime()
				, end == null ? "" : end.getTime());
		return performCall(sid, User.Right.Soap
				, sd -> AppointmentDTO.list(getDao().getInRange(userid, start.getTime(), end.getTime())));
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
	public AppointmentDTO next(@QueryParam("sid") @WebParam(name="sid") String sid) {
		return performCall(sid, User.Right.Room, sd -> {
			Appointment a = getDao().getNext(sd.getUserId(), new Date());
			return a == null ? null : new AppointmentDTO(a);
		});
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
	 */
	@GET
	@Path("/next/{userid}")
	public AppointmentDTO nextForUser(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("userid") @WebParam(name="userid") long userid) {
		return performCall(sid, User.Right.Soap, sd -> {
			Appointment a = getDao().getNext(userid, new Date());
			return a == null ? null : new AppointmentDTO(a);
		});
	}

	/**
	 *
	 * Load a calendar event by its room id
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomid
	 *            id of appointment special room
	 * @return - calendar event by its room id
	 */
	@GET
	@Path("/room/{roomid}")
	public AppointmentDTO getByRoom(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("roomid") @WebParam(name="roomid") long roomid) {
		return performCall(sid, User.Right.Room, sd -> {
			Appointment a = getDao().getByRoom(sd.getUserId(), roomid);
			return a == null ? null : new AppointmentDTO(a);
		});
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
	 */
	@GET
	@Path("/title/{title}")
	public List<AppointmentDTO> getByTitle(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("title") @WebParam(name="title") String title) {
		return performCall(sid, User.Right.Room, sd -> AppointmentDTO.list(getDao().searchByTitle(sd.getUserId(), title)));
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
	 */
	@WebMethod
	@POST
	@Path("/")
	public AppointmentDTO save(@QueryParam("sid") @WebParam(name="sid") String sid, @FormParam("appointment") @WebParam(name="appointment") AppointmentDTO appointment) {
		//Seems to be create
		log.debug("save SID: {}", sid);

		UserDao userDao = getUserDao();
		return performCall(sid, sd -> {
				User u = userDao.get(sd.getUserId());
				if (!AuthLevelUtil.hasUserLevel(u.getRights())) {
					log.error("save: not authorized");
					return false;
				}
				return AuthLevelUtil.hasWebServiceLevel(u.getRights())
						|| appointment.getOwner() == null
						|| appointment.getOwner().getId().equals(u.getId());
			}, sd -> {
				User u = userDao.get(sd.getUserId());
				AppointmentDao dao = getDao();
				Appointment a = appointment.get(userDao, getFileDao(), dao, u);
				if (a.getRoom().getId() != null) {
					if (a.getRoom().isAppointment()) {
						a.getRoom().setIspublic(false);
					} else {
						a.setRoom(getRoomDao().get(a.getRoom().getId()));
					}
				}
				return new AppointmentDTO(dao.update(a, u.getId()));
			});
	}

	/**
	 *
	 * delete a calendar event
	 *
	 * If the given sid is from an Administrator or Web-Service user, the user
	 * can delete any appointment.
	 * If the sid is assigned to a regular user, he can only delete appointments
	 * where he is also the owner/creator of the appointment
	 *
	 * @param sid
	 *            an authenticated SID
	 * @param id
	 *            the id to delete
	 * @return {@link ServiceResult} with result type
	 */
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) {
		AppointmentDao dao = getDao();
		Appointment a = dao.get(id);
		return performCall(sid, sd -> {
				Set<Right> rights = getRights(sd.getUserId());
				if (AuthLevelUtil.hasWebServiceLevel(rights) || AuthLevelUtil.hasAdminLevel(rights)) {
					return true;
					// fine
				}
				if (AuthLevelUtil.hasUserLevel(rights) && a.getOwner().getId().equals(sd.getUserId())) {
					return true;
				}
				return false;
			}, sd -> {
				if (a == null) {
					throw new ServiceException("Bad id");
				}
				dao.delete(a, sd.getUserId());
				return new ServiceResult("Deleted", Type.SUCCESS);
			});
	}
}
