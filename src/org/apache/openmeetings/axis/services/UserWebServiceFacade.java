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

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.data.beans.basic.ErrorResult;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.user.User;

public class UserWebServiceFacade extends BaseWebService {

	/**
	 * load this session id before doing anything else
	 * 
	 * @return Sessiondata-Object
	 */
	public Sessiondata getSession() throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).getSession();
	}

	/**
	 * auth function, use the SID you get by getSession
	 * 
	 * @param SID
	 * @param Username
	 * @param Userpass
	 * @return positive means Loggedin, if negativ its an ErrorCode, you have to
	 *         invoke the Method getErrorByCode to get the Text-Description of
	 *         that ErrorCode
	 */
	public Long loginUser(String SID, String username, String userpass)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).loginUser(SID, username, userpass);
	}

	/**
	 * Gets the Error-Object
	 * 
	 * @param SID
	 * @param errorid
	 * @param language_id
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).getErrorByCode(SID, errorid, language_id);
	}

	public Long addNewUser(String SID, String username, String userpass,
			String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).addNewUser(SID, username, userpass,
				lastname, firstname, email, additionalname, street, zip, fax,
				states_id, town, language_id, baseURL);
	}

	public Long addNewUserWithTimeZone(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL,
			String jNameTimeZone) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).addNewUserWithTimeZone(SID, username,
				userpass, lastname, firstname, email, additionalname, street,
				zip, fax, states_id, town, language_id, baseURL, jNameTimeZone);

	}

	/**
	 * 
	 * Adds a user with an externalUserId and type, but checks if the user/type
	 * does already exist
	 * 
	 * @param SID
	 * @param username
	 * @param userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param additionalname
	 * @param street
	 * @param zip
	 * @param fax
	 * @param states_id
	 * @param town
	 * @param language_id
	 * @param jNameTimeZone
	 * @param externalUserId
	 * @param externalUserType
	 * @return
	 * @throws AxisFault
	 */
	public Long addNewUserWithExternalType(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id,
			String jNameTimeZone, String externalUserId, String externalUserType)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).addNewUserWithExternalType(SID, username,
				userpass, lastname, firstname, email, additionalname, street,
				zip, fax, states_id, town, language_id, jNameTimeZone,
				externalUserId, externalUserType);

	}

	/**
	 * 
	 * delete a user by its id
	 * 
	 * @param SID
	 * @param userId
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserById(String SID, Long userId) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).deleteUserById(SID, userId);
	}

	/**
	 * 
	 * delete a user by its external user id and type
	 * 
	 * @param SID
	 * @param externalUserId
	 * @param externalUserType
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserByExternalUserIdAndType(String SID,
			String externalUserId, String externalUserType) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).deleteUserByExternalUserIdAndType(SID,
				externalUserId, externalUserType);
	}

	/**
	 * 
	 * @param SID
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @return
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObject(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserObject(SID, username, firstname,
				lastname, profilePictureUrl, email);
	}

	/**
	 * This is the advanced technique to set the User Object + simulate a User
	 * from the external system, this is needed cause you can that always
	 * simulate to same user in openmeetings
	 * 
	 * @param SID
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param profilePictureUrl
	 * @param email
	 * @param externalUserId
	 *            the User Id of the external System
	 * @param externalUserType
	 *            the Name of the external system, for example you can run
	 *            several external system and one meeting server
	 * @return
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObjectWithExternalUser(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserObjectWithExternalUser(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType);
	}

	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserObjectAndGenerateRoomHash(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType, room_id,
				becomeModeratorAsInt, showAudioVideoTestAsInt);
	}

	public String setUserObjectAndGenerateRoomHashByURL(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, String externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserObjectAndGenerateRoomHashByURL(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType, room_id,
				becomeModeratorAsInt, showAudioVideoTestAsInt);
	}

	public String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, String externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext())
				.setUserObjectAndGenerateRoomHashByURLAndRecFlag(SID, username,
						firstname, lastname, profilePictureUrl, email,
						externalUserId, externalUserType, room_id,
						becomeModeratorAsInt, showAudioVideoTestAsInt,
						allowRecording);
	}

	public String setUserObjectMainLandingZone(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserObjectMainLandingZone(SID,
				username, firstname, lastname, profilePictureUrl, email,
				externalUserId, externalUserType);
	}

	public String setUserAndNickName(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).setUserAndNickName(SID, username,
				firstname, lastname, profilePictureUrl, email, externalUserId,
				externalUserType, room_id, becomeModeratorAsInt,
				showAudioVideoTestAsInt, showNickNameDialogAsInt);
	}

	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			String externalUserId, String externalUserType, Long recording_id)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext())
				.setUserObjectAndGenerateRecordingHashByURL(SID, username,
						firstname, lastname, externalUserId, externalUserType,
						recording_id);
	}

	public Long addUserToOrganisation(String SID, Long user_id,
			Long organisation_id, Long insertedby)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).addUserToOrganisation(SID, user_id,
				organisation_id, insertedby);
	}

	public SearchResult<User> getUsersByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).getUsersByOrganisation(SID,
				organisation_id, start, max, orderby, asc);
	}

	public Boolean kickUserByPublicSID(String SID, String publicSID)
			throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).kickUserByPublicSID(SID, publicSID);
	}
	
	public Long addOrganisation(String SID, String name) throws AxisFault {
		return getBeanUtil().getBean(UserWebService.class, getServletContext()).addOrganisation(SID, name);
	}

}
