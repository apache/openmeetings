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
package org.apache.openmeetings.data.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.mail.template.FeedbackTemplate;
import org.apache.openmeetings.web.mail.template.RegisterUserTemplate;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author swagner
 *
 */
public class EmailManager {
	private static final Logger log = Red5LoggerFactory.getLogger(EmailManager.class, webAppRootKey);

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private MailHandler mailHandler;

	/**
	 * sends a mail adress to the user with his account data
	 * 
	 * @param username
	 * @param userpass
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public String sendMail(String username, String userpass, String email, String link, Boolean sendEmailWithVerficationCode, Long langId) {
		log.debug("sendMail:: username = {}, email = {}", username, email);
		Integer sendEmailAtRegister = configurationDao.getConfValue("sendEmailAtRegister", Integer.class, "0");

		if (sendEmailAtRegister == 1) {
			RegisterUserTemplate.ensureApplication(langId);
			mailHandler.send(email, langId == null ? Application.getString(512) : Application.getString(512, langId)  
				, RegisterUserTemplate.getEmail(username, userpass, email, sendEmailWithVerficationCode ? link : null));
		}
		return "success";
	}

	//FIXME, seems to be not used
	public void sendFeedback(String username, String email, String message) {
		mailHandler.send("user@openmeetings.apache.org", Application.getString(499), FeedbackTemplate.getEmail(username, email, message));
	}
	
	public String addEmailCon(String EMail, int CONTACT_ID) {
		String succ = "invalid email";
		return succ;
	}
}
