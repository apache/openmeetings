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
package org.apache.openmeetings.webservice.util;

import java.util.List;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.openmeetings.webservice.NetTestWebService;
import org.apache.openmeetings.webservice.NetTestWebService.TestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RateLimited
public class RateLimitRequestFilter implements ContainerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(RateLimitRequestFilter.class);
	private static final String ATTR_LAST_ACCESS_TIME = "LAST_ACCESS_TIME";
	private static final long ALLOWED_TIME = 3000;

	@Context
	private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext context) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			context.abortWith(Response.status(Status.FORBIDDEN).build());
			return;
		}
		List<String> typeList = context.getUriInfo().getQueryParameters().get("type");
		if (typeList != null && !typeList.isEmpty()) {
			TestType type = NetTestWebService.getTypeByString(typeList.get(0));
			if (TestType.PING == type || TestType.JITTER == type) {
				return;
			}
		}
		if (NetTestWebService.CLIENT_COUNT.get() > NetTestWebService.getMaxClients()) {
			log.error("Download: Max client count reached");
			context.abortWith(Response.status(Status.TOO_MANY_REQUESTS).build());
			return;
		}
		Long lastAccessed = (Long)session.getAttribute(ATTR_LAST_ACCESS_TIME);
		session.setAttribute(ATTR_LAST_ACCESS_TIME, System.currentTimeMillis());
		if (lastAccessed != null && System.currentTimeMillis() - lastAccessed.longValue() < ALLOWED_TIME) {
			context.abortWith(Response.status(Status.TOO_MANY_REQUESTS).build());
		}
	}
}
