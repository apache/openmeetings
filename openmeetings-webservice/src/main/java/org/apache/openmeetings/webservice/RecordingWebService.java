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

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
@WebService(name = "RecordingService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param recordingId
	 *            the id of the recording
	 *            
	 * @return - true if recording was deleted
	 * @throws ServiceException
	 */
	public boolean delete(String SID, Long recordingId)
			throws ServiceException {
		try {

			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return recordingDao.delete(recordingId);
			}

		} catch (Exception err) {
			log.error("[delete] ", err);
			throw new ServiceException(err.getMessage());
		}

		return false;
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param externalId the externalUserId
	 * @param externalType the externalUserType
	 *            
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getByExternalId(String SID, String externalId, String externalType) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalId(externalId, externalType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getByExternalId] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalRoomType specified when creating the room
	 * @param insertedBy
	 *            the userId that created the recording
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getByExternalTypeAndCreator(String SID, String externalType, Long insertedBy) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalTypeAndCreator(externalType, insertedBy));
			}
			return null;
		} catch (Exception err) {
			log.error("[getByExternalTypeAndCreator] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalRoomType specified when creating the room
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getByExternalTypeByList(String SID, String externalType) throws ServiceException {
		try {

			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalType(externalType));

			}

			return null;
		} catch (Exception err) {
			log.error("[getByExternalTypeByList] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged-in
	 * @param externalType
	 *            externalType specified when creating room or user
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public List<RecordingDTO> getRecordingsByExternalType(String SID, String externalType) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalType(externalType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getRecordingsByExternalType] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalType
	 *            externalRoomType specified when creating the room
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getByExternalType(String SID, String externalType) throws ServiceException {
		try {

			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByExternalType(externalType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getByExternalType] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Get list of recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 * @return - list of recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getByRoomId(String SID, Long roomId)
			throws ServiceException {
		try {

			Long userId = sessionDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(recordingDao.getByRoomId(roomId));
			}

			return null;
		} catch (Exception err) {
			log.error("[getByRoomId] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

}
