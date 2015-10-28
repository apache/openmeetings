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
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The Service contains methods to work with recordings
 * 
 * @author solomax
 * @webservice RecordingService
 * 
 */
@WebService(serviceName="org.apache.openmeetings.webservice.RecordingWebService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/record")
public class RecordingWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingWebService.class, webAppRootKey);
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RecordingDao recordingDao;

	/**
	 * Deletes a flv recording
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the id of the recording
	 *            
	 * @throws {@link ServiceException} in case of any error
	 */
	@DELETE
	@Path("/{id}")
	public void delete(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				recordingDao.delete(id);
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[delete] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param sid The SID of the User. This SID must be marked as Loggedin
	 * @param externalId the externalUserId
	 * @param externalType the externalUserType
	 *            
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}/{externalid}")
	public List<RecordingDTO> getExternal(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @PathParam("externalid") @WebParam(name="externalid") String externalId) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalId(externalId, externalType));
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getExternal] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalRoomType specified when creating the room
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/{externaltype}")
	public List<RecordingDTO> getExternalByType(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalType(externalType));
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getByExternalTypeByList] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Get list of recordings
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 * @return - list of recordings
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/room/{id}")
	public List<RecordingDTO> getExternalByRoom(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("id") @WebParam(name="id") Long roomId) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByRoomId(roomId));
			} else {
				throw new ServiceException("Not allowed to preform that action, Authenticate the SID first");
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getByRoomId] ", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
