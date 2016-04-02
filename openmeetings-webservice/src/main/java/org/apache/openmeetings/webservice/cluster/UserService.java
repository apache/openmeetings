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
package org.apache.openmeetings.webservice.cluster;

import static org.apache.openmeetings.webservice.Constants.TNS;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_NAME;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_PORT_NAME;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.dto.user.ExternalUserDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.webservice.error.ServiceException;

/**
 * This interface is required for RestClient
 */
@WebService(serviceName = USER_SERVICE_NAME, targetNamespace = TNS, portName = USER_SERVICE_PORT_NAME)
public interface UserService {

	/**
	 * @param user - login or email of Openmeetings user with admin or SOAP-rights
	 * @param pass - password
	 *            
	 * @return - {@link ServiceResult} with error code or SID and userId
	 */
	ServiceResult login(@WebParam(name="user") @QueryParam("user") String user, @WebParam(name="pass") @QueryParam("pass") String pass);

	/**
	 * Lists all users in the system!
	 * 
	 * @param sid
	 *            The SID from getSession
	 *            
	 * @return - list of users
	 * @throws ServiceException
	 */
	List<UserDTO> get(@WebParam(name="sid") @QueryParam("sid") String sid) throws ServiceException;
	
	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account To do SSO see the methods to create a hash and use those ones!
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param user
	 *            user object
	 * @param confirm
	 *            whatever or not to send email, leave empty for auto-send
	 *            
	 * @return - id of the user added or error code
	 * @throws ServiceException
	 */
	UserDTO add(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @QueryParam("user") UserDTO user
			, @WebParam(name="confirm") @QueryParam("confirm") Boolean confirm
			) throws ServiceException;

	/**
	 * 
	 * Delete a certain user by its id
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param id
	 *            the openmeetings user id
	 *            
	 * @return - id of the user deleted, error code otherwise
	 * @throws ServiceException
	 */
	ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException;

	/**
	 * 
	 * Delete a certain user by its external user id
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param externalId
	 *            externalUserId
	 * @param externalType
	 *            externalUserId
	 *            
	 * @return - id of user deleted, or error code
	 * @throws ServiceException
	 */
	ServiceResult deleteExternal(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @WebParam(name="externalid") @PathParam("externalid") String externalId
			) throws ServiceException;

	/**
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room. ...
	 * Session-Hashs are deleted 15 minutes after the creation if not used.
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param user
	 *            user details to set
	 * @param options
	 *            room options to set
	 *            
	 * @return - secure hash or error code
	 * @throws ServiceException
	 */
	ServiceResult getRoomHash(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @FormParam("user") ExternalUserDTO user
			, @WebParam(name="options") @FormParam("options") RoomOptionsDTO options
			) throws ServiceException;

	/**
	 * Kick a user by its public SID
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param publicSID
	 *            the publicSID (you can get it from the call to get users in a
	 *            room)
	 * @return - <code>true</code> if user was kicked
	 */
	ServiceResult kick(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="publicsid") @PathParam("publicsid") String publicSID) throws ServiceException;

	/**
	 * Returns the count of users currently in the Room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param sid The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 */
	int count(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="roomid") @PathParam("roomid") Long roomId);
}
