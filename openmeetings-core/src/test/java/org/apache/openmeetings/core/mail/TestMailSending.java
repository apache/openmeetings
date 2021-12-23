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
package org.apache.openmeetings.core.mail;

import static org.apache.openmeetings.util.OpenmeetingsVariables.setMailFrom;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSmtpPass;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSmtpPort;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSmtpServer;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSmtpUseTls;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSmtpUser;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 *
 * @author swagner
 *
 */
class TestMailSending {
	private static final Logger log = LoggerFactory.getLogger(TestMailSending.class);

	//Example GMail email server data
	private String smtpServer = "smtp.gmail.com";
	private int smtpPort = 587;
	private String from = "test-app@apache.org";
	private String mailAuthUser = "test-app@gmail.com";
	private String mailAuthPass = "test-pass";
	private boolean mailTls = true;

	/**
	 *
	 * It does not make a lot of send to test this in every test suite, it is more for manual testing.
	 * Handy to check your server and the JavaMail setting properties
	 *
	 */
	@Test
	void doTestSendEmail() {
		try {
			Transport.send(getMimeMessage());
		} catch (Exception err) {
			log.error("Error", err);
		}
		assertTrue(true);
	}

	private MimeMessage getMimeMessage() throws Exception {
		MailHandler h = new MailHandler();
		setSmtpServer(smtpServer);
		setSmtpPort(smtpPort);
		setSmtpUseTls(mailTls);
		setSmtpUser(mailAuthUser);
		setSmtpPass(mailAuthPass);
		setMailFrom(from);
		// Building MimeMessage
		MimeMessage msg = h.getBasicMimeMessage();
		msg.setSubject("getSubject()");
		msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("test-recipient@gmail.com", false));

		return h.appendBody(msg, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
	}
}
