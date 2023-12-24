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

import java.util.List;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.RecordingDTOListWrapper;
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
 *
 * The Service contains methods to work with recordings
 *
 */
@Service("recordWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.RecordingWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "RecordingService")
@Path("/record")
public class RecordingWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(RecordingWebService.class);

	@Inject
	private RecordingDao recordingDao;

	/**
	 * Deletes a recording
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the id of the recording
	 *
	 * @return {@link ServiceResult} with result type
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{id}")
	@Operation(
			description = "Deletes a recording",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the id of the recording") @PathParam("id") @WebParam(name="id") Long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			recordingDao.delete(recordingDao.get(id));
			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}

	/**
	 * Gets a list of recordings created by particular external USER
	 *
	 * @param sid The SID of the User. This SID must be marked as Loggedin
	 * @param externalId the externalUserId
	 * @param externalType the externalUserType
	 *
	 * @return - list of recordings
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}/{externalid}")
	@Operation(
			description = "Gets a list of recordings created by particular external USER",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of recordings",
							content = @Content(schema = @Schema(implementation = RecordingDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<RecordingDTO> getExternal(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the externalUserId") @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @Parameter(required = true, description = "the externalUserType") @PathParam("externalid") @WebParam(name="externalid") String externalId
			) throws ServiceException
	{
		log.debug("getExternal:: type {}, id {}", externalType, externalId);
		return performCall(sid, User.Right.SOAP, sd -> RecordingDTO.list(recordingDao.getByExternalUser(externalId, externalType)));
	}

	/**
	 * Gets a list of recordings
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalType specified when creating the room
	 * @return - list of flv recordings
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}")
	@Operation(
			description = "Gets a list of recordings",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of recordings",
							content = @Content(schema = @Schema(implementation = RecordingDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<RecordingDTO> getExternalByType(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "externalType specified when creating the room") @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> RecordingDTO.list(recordingDao.getByExternalType(externalType)));
	}

	/**
	 * Get list of recordings
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 * @return - list of recordings
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/room/{roomid}")
	@Operation(
			description = "Gets a list of recordings",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of recordings",
							content = @Content(schema = @Schema(implementation = RecordingDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<RecordingDTO> getExternalByRoom(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the room id") @PathParam("roomid") @WebParam(name="roomid") Long roomId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> RecordingDTO.list(recordingDao.getByRoomId(roomId)));
	}
}
