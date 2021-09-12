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
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dto.basic.Health;
import org.apache.openmeetings.db.dto.basic.Info;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 *
 * The Service contains methods to get info about the system
 *
 * @author solomax
 *
 */
@Service("infoWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.InfoWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "InfoService")
@Path("/info")
public class InfoWebService {
	/**
	 * Method to get current OpenMeetings version
	 *
	 * @return - version
	 */
	@WebMethod
	@GET
	@Path("/version")
	@Operation(
			description = "Method to get current OpenMeetings version",
			responses = {
					@ApiResponse(responseCode = "200", description = "Current version", content = @Content(schema = @Schema(implementation = Info.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of server error")
			}
		)
	public Info getVersion() {
		return new Info();
	}

	/**
	 * Method to get health report for this OpenMeetings instance
	 *
	 * @return - health report
	 */
	@WebMethod
	@GET
	@Path("/health")
	@Operation(
			description = "Method to get health report for this OpenMeetings instance",
			responses = {
					@ApiResponse(responseCode = "200", description = "health report", content = @Content(schema = @Schema(implementation = Health.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of server error")
			}
		)
	public Health getHealth() {
		return Health.INSTANCE;
	}
}
