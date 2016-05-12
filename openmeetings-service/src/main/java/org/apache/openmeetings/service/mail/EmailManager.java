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
package org.apache.openmeetings.service.mail;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.service.mail.template.FeedbackTemplate;
import org.apache.openmeetings.service.mail.template.RegisterUserTemplate;
import org.apache.wicket.Application;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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

	public static String getString(long id) {
		return ((IApplication)Application.get(wicketApplicationName)).getOmString(id);
	}

	/**
	 * sends a mail address to the user with his account data
	 * 
	 * @param username
	 * @param userpass
	 * @param email
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String sendMail(String username, String userpass, String email, String hash, Boolean sendEmailWithVerficationCode, Long langId) {
		log.debug("sendMail:: username = {}, email = {}", username, email);
		Integer sendEmailAtRegister = configurationDao.getConfValue("sendEmailAtRegister", Integer.class, "0");

		String link = ((IApplication)Application.get(wicketApplicationName)).urlForActivatePage(new PageParameters().add("u",  hash));
		
		if (sendEmailAtRegister == 1) {
			ensureApplication(langId != null ? langId :
					configurationDao.getConfValue(CONFIG_DEFAULT_LANG_KEY, Long.class, "1"));
			mailHandler.send(email, getString(512)
				, RegisterUserTemplate.getEmail(username, userpass, email, sendEmailWithVerficationCode ? link : null));
		}
		return "success";
	}

	//FIXME, seems to be not used
	public void sendFeedback(String username, String email, String message) {
		mailHandler.send("user@openmeetings.apache.org", getString(499), FeedbackTemplate.getEmail(username, email, message));
	}
}
