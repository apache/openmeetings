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
package org.apache.openmeetings.utils;

import javax.servlet.ServletContextEvent;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.red5.logging.ContextLoggingListener;

public class OMContextListener extends ContextLoggingListener {
	
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	public void contextInitialized(ServletContextEvent event) {
		OpenmeetingsVariables.webAppRootKey = pathToName(event);
		OpenmeetingsVariables.webAppRootPath = "/" + OpenmeetingsVariables.webAppRootKey;
		System.setProperty("current_openmeetings_context_name", OpenmeetingsVariables.webAppRootKey);
		System.setProperty("webapp.contextPath", OpenmeetingsVariables.webAppRootPath);
		System.setProperty("logback.configurationFile", "logback-config.xml");
		super.contextInitialized(event);
	}

	private String pathToName(ServletContextEvent event) {
		String contextName = event.getServletContext().getContextPath().replaceAll("/", "");
		if ("".equals(contextName)) {
			contextName = "root";
		}
		return contextName;
	}
}
