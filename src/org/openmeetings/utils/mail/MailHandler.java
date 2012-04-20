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
package org.openmeetings.utils.mail;

import org.openmeetings.app.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class MailHandler {
	private static final Logger log = Red5LoggerFactory.getLogger(
			MailHandler.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private MailThread mailThread;

	/**
	 * send mail to address
	 * 
	 * @param toEmail
	 * @param subj
	 * @param message
	 * @return "success" or "Error: err description"
	 */
	public String sendMail(String toEmail, String subj, String message) {
		return sendMail(toEmail, null, subj, message);
	}
	
	/**
	 * send mail to address
	 * 
	 * @param toEmail
	 * @param replyTo
	 * @param subj
	 * @param message
	 * @return "success" or "Error: err description"
	 */
	public String sendMail(String toEmail, String replyTo, String subj, String message) {
		try {
			mailThread.doSend(toEmail, replyTo, subj, message);

			return "success";

		} catch (Exception ex) {
			log.error("[sendMail] ", ex);
			return "Error: " + ex;
		}
	}
}
