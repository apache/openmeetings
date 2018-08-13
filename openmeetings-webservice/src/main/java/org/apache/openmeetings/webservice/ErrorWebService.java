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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * The Service contains methods to get localized errors
 *
 * @author solomax
 *
 */
@Service("errorWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.ErrorWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/error")
public class ErrorWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(ErrorWebService.class, getWebAppRootKey());

	/**
	 * loads an Error-Object. If a Method returns a negative Result, its an
	 * Error-id, it needs a languageId to specify in which language you want to
	 * display/read the error-message. English has the Language-ID one, for
	 * different one see the list of languages
	 *
	 * @param key
	 *            the error key for ex. `error.unknown`
	 * @param lang
	 *            The id of the language
	 *
	 * @return - error with the code given
	 */
	@WebMethod
	@GET
	@Path("/{key}/{lang}")
	public ServiceResult get(@WebParam(name="key") @PathParam("key") String key, @WebParam(name="lang") @PathParam("lang") long lang) {
		try {
			String eValue = LabelDao.getString(key, lang);
			return new ServiceResult(eValue, Type.SUCCESS);
		} catch (Exception err) {
			log.error("[get] ", err);
		}
		return null;
	}

	@WebMethod
	@POST
	@Path("/report/")
	public void report(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="message") @QueryParam("message") String message) {
		if (sid != null && message != null) {
			Sessiondata sd = check(sid);
			if (sd.getId() != null) {
				log.error("[CLIENT MESSAGE] " + message);
			}
		}
	}
}
