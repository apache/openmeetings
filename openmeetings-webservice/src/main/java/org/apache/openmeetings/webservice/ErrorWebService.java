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

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.entity.server.Sessiondata;
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

@Service("errorWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.ErrorWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "ErrorService")
@Path("/error")
public class ErrorWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(ErrorWebService.class);

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
	@Operation(
			description = """
					Loads an Error-Object. If a Method returns a negative Result, its an
					 Error-id, it needs a languageId to specify in which language you want to
					 display/read the error-message. English has the Language-ID one, for
					 different one see the list of languages""",
			responses = {
					@ApiResponse(responseCode = "200", description = "error with the code given",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Server error")
			}
		)
	public ServiceResult get(
			@Parameter(required = true, description = "the error key for ex. `error.unknown`") @WebParam(name="key") @PathParam("key") String key
			, @Parameter(required = true, description = "The id of the language") @WebParam(name="lang") @PathParam("lang") long lang) {
		try {
			String eValue = LabelDao.getString(key, lang);
			return new ServiceResult(eValue, Type.SUCCESS);
		} catch (Exception err) {
			log.error("[get] ", err);
		}
		return null;
	}

	/**
	 * Logs an error to the log file for reporting
	 *
	 * @param sid The SID from getSession
	 * @param message The message to log
	 */
	@WebMethod
	@POST
	@Path("/report/")
	@Operation(
			description = "Logs an error to the log file for reporting",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public void report(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "The message to log") @WebParam(name="message") @QueryParam("message") String message) {
		if (sid != null && message != null) {
			Sessiondata sd = check(sid);
			if (sd.getId() != null) {
				log.error("[CLIENT MESSAGE] {}", message);
			}
		}
	}
}
