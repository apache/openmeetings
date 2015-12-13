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
package org.apache.openmeetings.templates;

import java.io.StringWriter;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RequestContactTemplate extends VelocityLoader {

	private static final String templateName = "requestcontact.vm";

	private static final Logger log = Red5LoggerFactory
			.getLogger(RequestContactTemplate.class,
					OpenmeetingsVariables.webAppRootKey);

	public String getRequestContactTemplate(String message, String accept_link,
			String deny_link, String openmeetings_link) {
		try {

			super.init();

			/* lets make a Context and put data into it */

			VelocityContext context = new VelocityContext();

			context.put("message", message);
			context.put("accept_link", accept_link);
			context.put("deny_link", deny_link);
			context.put("openmeetings_link", openmeetings_link);

			/* lets render a template */

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(templateName, "UTF-8", context, w);

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}
}
