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

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.entity.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * The Service contains methods to work with recordings
 *
 * @author solomax
 *
 */
@Service("recordWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.RecordingWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/record")
public class RecordingWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingWebService.class, getWebAppRootKey());

	private static RecordingDao getDao() {
		return getBean(RecordingDao.class);
	}
	/**
	 * Deletes a flv recording
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the id of the recording
	 *
	 * @return {@link ServiceResult} with result type
	 */
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) {
		return performCall(sid, User.Right.Soap, sd -> {
			RecordingDao dao = getDao();
			dao.delete(dao.get(id));
			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}

	/**
	 * Gets a list of flv recordings
	 *
	 * @param sid The SID of the User. This SID must be marked as Loggedin
	 * @param externalId the externalUserId
	 * @param externalType the externalUserType
	 *
	 * @return - list of flv recordings
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}/{externalid}")
	public List<RecordingDTO> getExternal(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @PathParam("externalid") @WebParam(name="externalid") String externalId) {
		log.debug("getExternal:: type {}, id {}", externalType, externalId);
		return performCall(sid, User.Right.Soap, sd -> RecordingDTO.list(getDao().getByExternalId(externalId, externalType)));
	}

	/**
	 * Gets a list of flv recordings
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalType specified when creating the room
	 * @return - list of flv recordings
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}")
	public List<RecordingDTO> getExternalByType(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType) {
		return performCall(sid, User.Right.Soap, sd -> RecordingDTO.list(getDao().getByExternalType(externalType)));
	}

	/**
	 * Get list of recordings
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 * @return - list of recordings
	 */
	@WebMethod
	@GET
	@Path("/room/{roomid}")
	public List<RecordingDTO> getExternalByRoom(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("roomid") @WebParam(name="roomid") Long roomId) {
		return performCall(sid, User.Right.Soap, sd -> RecordingDTO.list(getDao().getByRoomId(roomId)));
	}
}
