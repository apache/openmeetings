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

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.DEFAUT_LANG_KEY;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.templates.RegisterUserTemplate;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * FIXME: This class should be <i>not</i> {@link Transactional}
 * 
 * @author swagner
 *
 */
@Transactional
public class EmailManager {
	private static final Logger log = Red5LoggerFactory.getLogger(EmailManager.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private RegisterUserTemplate registerUserTemplate;

	/**
	 * sends a mail adress to the user with his account data
	 * 
	 * @param Username
	 * @param Userpass
	 * @param EMail
	 * @return
	 * @throws Exception
	 */
	public String sendMail(String Username, String Userpass, String EMail,
			String link, Boolean sendEmailWithVerficationCode) {
		log.debug("sendMail:: username = {}, email = {}", Username, EMail);
		Integer sendEmailAtRegister = configurationDao.getConfValue("sendEmailAtRegister", Integer.class, "0");

		if (sendEmailAtRegister == 1) {

			Long default_lang_id = configurationDao.getConfValue(DEFAUT_LANG_KEY, Long.class, "1");

			String template = null;
			if (sendEmailWithVerficationCode) {
				String verification_url = link;

				template = registerUserTemplate
						.getRegisterUserWithVerificationTemplate(Username,
								Userpass, EMail, default_lang_id,
								verification_url);
			} else {
				template = registerUserTemplate
						.getRegisterUserTemplate(Username, Userpass, EMail,
								default_lang_id);
			}
			mailHandler.send(EMail, fieldManager.getString(512L, default_lang_id), template);
		}
		return "success";
	}

	public String addEmailCon(String EMail, int CONTACT_ID) {
		String succ = "invalid email";
		return succ;
	}
}
