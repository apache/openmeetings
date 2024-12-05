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
package org.apache.openmeetings.util;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.wicket.util.string.Strings;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class OMContextListener implements ServletContextListener {
	private static final String LOG_DIR_PROP = "current_openmeetings_log_dir";
	private static final String CTX_NAME_PROP = "current_openmeetings_context_name";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String ctx = pathToName(event);
		System.setProperty(CTX_NAME_PROP, ctx);
		if (Strings.isEmpty(System.getProperty(LOG_DIR_PROP))) {
			System.setProperty(LOG_DIR_PROP, "logs");
		}
		System.setProperty("webapp.contextPath", "/" + ctx);
		try {
			LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			tryConfigure(configurator);
		} catch (Exception e) {
			// no-op
		}
	}

	private void tryConfigure(JoranConfigurator configurator) throws JoranException, IOException {
		boolean configured = false;
		try (InputStream cfgIs = getClass().getResourceAsStream("/logback-test.xml")) {
			if (cfgIs != null) {
				configurator.doConfigure(cfgIs);
				configured = true;
			}
		} catch (Exception e) {
			// no-op
		}
		if (!configured) {
			try (InputStream cfgIs = getClass().getResourceAsStream("/logback-config.xml")) {
				configurator.doConfigure(cfgIs);
			}
		}
	}

	private static String pathToName(ServletContextEvent event) {
		String contextName = event.getServletContext().getContextPath().replace("/", "");
		if ("".equals(contextName)) {
			contextName = "root";
		}
		return contextName;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//no-op by default
	}
}
