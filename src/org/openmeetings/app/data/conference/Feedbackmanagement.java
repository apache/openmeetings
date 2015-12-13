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
package org.openmeetings.app.data.conference;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.templates.FeedbackTemplate;
import org.openmeetings.utils.mail.MailHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Feedbackmanagement {
	private static final Logger log = Red5LoggerFactory.getLogger(
			Feedbackmanagement.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private FeedbackTemplate feedbackTemplate;

	public String sendFeedback(String username, String email, String message) {
		try {
			Long default_lang_id = cfgManagement.getConfValue("default_lang_id", Long.class, "1");

			String template = feedbackTemplate.getFeedBackTemplate(username,
					email, message, default_lang_id);

			return mailHandler.sendMail("openmeetings-user@googlegroups.com",
					fieldmanagment.getString(499L, default_lang_id), template);

		} catch (Exception err) {
			log.error("sendInvitationLink", err);
		}
		return null;
	}
}
