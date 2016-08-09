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
import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The Service contains methods to get localized errors
 * 
 * @author solomax
 * 
 */
@WebService(serviceName="org.apache.openmeetings.webservice.ErrorWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/error")
public class ErrorWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(ErrorWebService.class, webAppRootKey);

	@Autowired
	private ErrorDao errorDao;
	@Autowired
	private LabelDao labelDao;
	@Autowired
	private SessiondataDao sessionDao;

	/**
	 * loads an Error-Object. If a Method returns a negative Result, its an
	 * Error-id, it needs a languageId to specify in which language you want to
	 * display/read the error-message. English has the Language-ID one, for
	 * different one see the list of languages
	 * 
	 * @param id
	 *            the error id (negative Value here!)
	 * @param lang
	 *            The id of the language
	 *            
	 * @return - error with the code given
	 */
	@WebMethod
	@GET
	@Path("/{id}/{lang}")
	public ServiceResult get(@WebParam(name="id") @PathParam("id") long id, @WebParam(name="lang") @PathParam("lang") long lang) {
		try {
			if (id < 0) {
				ErrorValue eValues = errorDao.get(-1 * id);
				if (eValues != null) {
					log.debug("eValues.getLabelId() = " + eValues.getLabelId());
					log.debug("eValues.getErrorType() = " + eValues.getType());
					String eValue = labelDao.getString(eValues.getLabelId(), lang);
					String tValue = labelDao.getString("error.type." + eValues.getType().name(), lang);
					if (eValue != null) {
						return new ServiceResult(id, eValue, tValue);
					}
				}
			} else {
				return new ServiceResult(id, "Error ... please check your input", "Error");
			}
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
			Sessiondata sd = sessionDao.check(sid);
			if (sd.getId() != null) {
				log.error("[CLIENT MESSAGE] " + message);
			}
		}
	}
}
