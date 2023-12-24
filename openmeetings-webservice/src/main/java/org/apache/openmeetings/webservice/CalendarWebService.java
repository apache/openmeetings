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

import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.mapper.CalendarMapper;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.AppointmentDTOListWrapper;
import org.apache.openmeetings.webservice.schema.AppointmentDTOWrapper;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "CalendarService")
@Path("/calendar")
public class CalendarWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(CalendarWebService.class);

	@Inject
	private AppointmentDao dao;
	@Inject
	private CalendarMapper calMapper;

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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/{start}/{end}")
	@Operation(
			description = "Load appointments by a start / end range for the current SID",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of appointments in range",
						content = @Content(schema = @Schema(implementation = AppointmentDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<AppointmentDTO> range(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "start time") @PathParam("start") @WebParam(name="start") Calendar start
			, @Parameter(required = true, description = "end time") @PathParam("end") @WebParam(name="end") Calendar end
			) throws ServiceException
	{
		log.debug("range : startdate - {} , enddate - {}", start.getTime(), end.getTime());
		return performCall(sid, User.Right.ROOM
				, sd -> AppointmentDTO.list(dao.getInRange(sd.getUserId(), start.getTime(), end.getTime())));
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/{userid}/{start}/{end}")
	@Operation(
			description = "Load appointments by a start / end range for the userId",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of appointments in range",
							content = @Content(schema = @Schema(implementation = AppointmentDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<AppointmentDTO> rangeForUser(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the userId the calendar events should be loaded") @PathParam("userid") @WebParam(name="userid") long userid
			, @Parameter(required = true, description = "start time") @PathParam("start") @WebParam(name="start") Calendar start
			, @Parameter(required = true, description = "end time") @PathParam("end") @WebParam(name="end") Calendar end
			) throws ServiceException
	{
		log.debug("rangeForUser : startdate - {} , enddate - {}", start.getTime(), end.getTime());
		return performCall(sid, User.Right.SOAP
				, sd -> AppointmentDTO.list(dao.getInRange(userid, start.getTime(), end.getTime())));
	}

	/**
	 * Get the next Calendar event for the current USER of the SID
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @return - next Calendar event
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/next")
	@Operation(
			description = "Get the next Calendar event for the current USER of the SID",
			responses = {
					@ApiResponse(responseCode = "200", description = "next Calendar event",
						content = @Content(schema = @Schema(implementation = AppointmentDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public AppointmentDTO next(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			) throws ServiceException {
		return performCall(sid, User.Right.ROOM, sd -> {
			Appointment a = dao.getNext(sd.getUserId(), new Date());
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/next/{userid}")
	@Operation(
			description = "Get the next Calendar event for userId",
			responses = {
					@ApiResponse(responseCode = "200", description = "next Calendar event",
						content = @Content(schema = @Schema(implementation = AppointmentDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public AppointmentDTO nextForUser(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the userId the calendar events should be loaded") @PathParam("userid") @WebParam(name="userid") long userid
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Appointment a = dao.getNext(userid, new Date());
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/room/{roomid}")
	@Operation(
			description = "Load a calendar event by its room id",
			responses = {
					@ApiResponse(responseCode = "200", description = "calendar event by its room id",
						content = @Content(schema = @Schema(implementation = AppointmentDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public AppointmentDTO getByRoom(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "id of appointment special room") @PathParam("roomid") @WebParam(name="roomid") long roomid
			) throws ServiceException
	{
		return performCall(sid, User.Right.ROOM, sd -> {
			Appointment a = dao.getByRoom(sd.getUserId(), roomid);
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/title/{title}")
	@Operation(
			description = "Search a calendar event for the current SID",
			responses = {
					@ApiResponse(responseCode = "200", description = "calendar event list",
							content = @Content(schema = @Schema(implementation = AppointmentDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<AppointmentDTO> getByTitle(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the search string") @PathParam("title") @WebParam(name="title") String title
			) throws ServiceException
	{
		return performCall(sid, User.Right.ROOM, sd -> AppointmentDTO.list(dao.searchByTitle(sd.getUserId(), title)));
	}

	/**
	 * Create an appointment
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param appointment
	 *            calendar event
	 *
	 * @return - appointment saved
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/")
	@Operation(
			description = "Create an appointment",
			responses = {
					@ApiResponse(responseCode = "200", description = "appointment saved",
						content = @Content(schema = @Schema(implementation = AppointmentDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public AppointmentDTO save(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "calendar event") @FormParam("appointment") @WebParam(name="appointment") AppointmentDTO appointment
			) throws ServiceException
	{
		//Seems to be create
		log.debug("save SID: {}", sid);

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
				Appointment a = calMapper.get(appointment, u);
				if (a.getRoom().getId() != null) {
					if (a.getRoom().isAppointment()) {
						a.getRoom().setIspublic(false);
					} else {
						a.setRoom(roomDao.get(a.getRoom().getId()));
					}
				}
				return new AppointmentDTO(dao.update(a, u.getId()));
			});
	}

	/**
	 *
	 * delete a calendar event
	 *
	 * If the given sid is from an Administrator or Web-Service USER, the USER
	 * can delete any appointment.
	 * If the sid is assigned to a regular USER, he can only delete appointments
	 * where he is also the owner/creator of the appointment
	 *
	 * @param sid
	 *            an authenticated SID
	 * @param id
	 *            the id to delete
	 * @return {@link ServiceResult} with result type
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{id}")
	@Operation(
			description = """
					Delete a calendar event

					 If the given sid is from an Administrator or Web-Service USER, the USER
					 can delete any appointment.
					 If the sid is assigned to a regular USER, he can only delete appointments
					 where he is also the owner/creator of the appointment""",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the id to delete") @PathParam("id") @WebParam(name="id") Long id
			) throws ServiceException
	{
		Appointment a = dao.get(id);
		return performCall(sid, sd -> {
				Set<Right> rights = getRights(sd.getUserId());
				if (AuthLevelUtil.hasWebServiceLevel(rights) || AuthLevelUtil.hasAdminLevel(rights)) {
					return true;
					// fine
				}
				return AuthLevelUtil.hasUserLevel(rights) && a.isOwner(sd.getUserId());
			}, sd -> {
				if (a == null) {
					throw new InternalServiceException("Bad id");
				}
				dao.delete(a, sd.getUserId());
				return new ServiceResult("Deleted", Type.SUCCESS);
			});
	}
}
