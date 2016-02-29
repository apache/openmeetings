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
package org.apache.openmeetings.test.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertTrue;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.openmeetings.core.mail.MailHandler;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 *
 */
public class TestMailSending {
	private static final Logger log = Red5LoggerFactory.getLogger(TestMailSending.class, webAppRootKey);
	
	//Example GMail email server data
	private String smtpServer = "smtp.gmail.com";
	private String smtpPort = "587";
	private String from = "test-app@apache.org";
	private String mailAuthUser = "test-app@gmail.com";
	private String mailAuthPass = "test-pass";
	private boolean mailTls = true;

	/**
	 * @Test
	 * 
	 * It does not make a lot of send to test this in every test suite, it is more for manual testing.
	 * Handy to check your server and the JavaMail setting properties
	 * 
	 */
	@Test
	public void doTestSendEmail() {
		try {
			Transport.send(getMimeMessage());
		} catch (Exception err) {
			log.error("Error", err);
		}
		assertTrue(true);
	}
	
	private MimeMessage getMimeMessage() throws Exception {
		MailHandler h = new MailHandler();
		h.init(smtpServer, smtpPort, from, mailAuthUser, mailAuthPass, mailTls, true);
		// Building MimeMessage
		MimeMessage msg = h.getBasicMimeMessage();
		msg.setSubject("getSubject()");
		msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("test-recipient@gmail.com", false));
		
		return h.appendBody(msg, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
	}
}
