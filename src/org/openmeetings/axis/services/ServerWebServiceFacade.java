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
package org.openmeetings.axis.services;

import javax.servlet.ServletContext;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.basic.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ServerWebServiceFacade {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ServerWebServiceFacade.class, OpenmeetingsVariables.webAppRootKey);

	private ServletContext getServletContext() throws Exception {
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}

	private ServerWebService getServerServiceProxy() {
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			return context.getBean("serverWebService", ServerWebService.class);
		} catch (Exception err) {
			log.error("[getServerServiceProxy]", err);
		}
		return null;
	}

	/**
	 * Proxy method please see {@link ServerWebService#getServers(String, int, int)}
	 */
	public Server[] getServers(String SID, int start, int max) {
		return getServerServiceProxy().getServers(SID, start, max);
	}

	/**
	 * Proxy method please see {@link ServerWebService#getServerCount(String)}
	 */
	public int getServerCount(String SID) {
		return getServerServiceProxy().getServerCount(SID);
	}

	/**
	 * Proxy method please see {@link ServerWebService#saveServer(String, long, String, String)}
	 */
	public long saveServer(String SID, long id, String name, String address) {
		return getServerServiceProxy().saveServer(SID, id, name, address);
	}

	/**
	 * Proxy method please see {@link ServerWebService#deleteServer(String, long)}
	 */
	public boolean deleteServer(String SID, long id) {
		return getServerServiceProxy().deleteServer(SID, id);
	}
}
