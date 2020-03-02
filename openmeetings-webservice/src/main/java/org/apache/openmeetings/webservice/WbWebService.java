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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IWhiteboardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WbService contains methods to manipulate whiteboard contents
 *
 */
@Service("wbWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.WbWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/wb")
public class WbWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(WbWebService.class);

	@Autowired
	private IWhiteboardManager wbManager;

	/**
	 * This method will remove all whiteboards from given room
	 * and create empty one(s) for room files specified
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - id of the room to clean
	 * @return - serviceResult object with the result
	 */
	@WebMethod
	@GET
	@Path("/resetwb/{id}")
	public ServiceResult resetWb(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			)
	{
		log.debug("[resetWb] room id {}", id);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.reset(id, sd.getUserId());
			return new ServiceResult("", Type.SUCCESS);
		});
	}

	/**
	 * This method will do the same as clean WB in the room (except for there will be no UNDO)
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - id of the room to clean
	 * @param wbId - id of the white board to clean
	 * @return - serviceResult object with the result
	 */
	@WebMethod
	@GET
	@Path("/cleanwb/{roomid}/{wbid}")
	public ServiceResult cleanWb(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @WebParam(name="wbid") @PathParam("wbid") long wbId
			)
	{
		log.debug("[cleanWb] room id {}, wb id {}", roomId, wbId);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.clearAll(roomId, wbId, null);
			return new ServiceResult("", Type.SUCCESS);
		});
	}

	/**
	 * This method will do the same as clean slide in the room (except for there will be no UNDO)
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - id of the room to clean
	 * @param wbId - id of the white board to clean
	 * @param slide - slide number (zero based)
	 * @return - serviceResult object with the result
	 */
	@WebMethod
	@GET
	@Path("/cleanslide/{roomid}/{wbid}/{slide}")
	public ServiceResult cleanSlide(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="roomid") @PathParam("roomid") long roomId
			, @WebParam(name="wbid") @PathParam("wbid") long wbId
			, @WebParam(name="slide") @PathParam("slide") int slide
			)
	{
		log.debug("[cleanSlide] room id {}, wb id {}, slide {}", roomId, wbId, slide);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.cleanSlide(roomId, wbId, slide, null);
			return new ServiceResult("", Type.SUCCESS);
		});
	}
}
