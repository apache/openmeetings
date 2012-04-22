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

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class MailThread {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MailHandler.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	protected Configurationmanagement cfgManagement;
	@Autowired
	protected TaskExecutor taskExecutor;

	public void doSend(String to, String subject, String body) {
		doSend(to, null, subject, body);
	}

	public void doSend(String to, String replyTo, String subject, String body) {
		taskExecutor.execute(new MailSenderTask(to, replyTo, subject, body));
	}

	protected class MailSenderTask implements Runnable {

		private final String recipients;
		private final String replyTo;
		private final String subject;
		private final String body;

		public MailSenderTask(String recipients, String replyTo, String subject, String body) {
			this.recipients = recipients;
			this.replyTo = replyTo;
			this.subject = subject;
			this.body = body;
		}

		public void run() {
			this.send();
		}

		protected MimeMessage getMessage() throws AddressException, MessagingException {
			log.debug("getMessage");

			// Evaluating Configuration Data
			String smtpServer = cfgManagement.getConfValue("smtp_server", String.class, null);
			String smtpPort = cfgManagement.getConfValue("smtp_port", String.class, "25");
			String from = cfgManagement.getConfValue("system_email_addr", String.class, null);
			String mailAuthUser = cfgManagement.getConfValue("email_username", String.class, null);
			String mailAuthPass = cfgManagement.getConfValue("email_userpass", String.class, null);

			Properties props = System.getProperties();

			props.put("mail.smtp.host", smtpServer);
			props.put("mail.smtp.port", smtpPort);

			if ("1".equals(cfgManagement.getConfValue("mail.smtp.starttls.enable", String.class, "0"))) {
				props.put("mail.smtp.starttls.enable", "true");
			}

			// Check for Authentification
			Session session = null;
			if (mailAuthUser != null && mailAuthUser.length() > 0
					&& mailAuthPass != null && mailAuthPass.length() > 0) {
				// use SMTP Authentication
				props.put("mail.smtp.auth", "true");
				session = Session.getDefaultInstance(props,
						new SmtpAuthenticator(mailAuthUser, mailAuthPass));
			} else {
				// not use SMTP Authentication
				session = Session.getDefaultInstance(props, null);
			}

			// Building MimeMessage
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(from));
			if (replyTo != null && "1".equals(cfgManagement.getConfValue("inviter.email.as.replyto", String.class, "1"))) {
				log.debug("setReplyTo "+replyTo);
				if (MailUtil.matches(replyTo)) {
					msg.setReplyTo(new InternetAddress[]{new InternetAddress(replyTo)});
				}
			}
			msg.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipients, false));
			
			return msg;
		}
		
		protected MimeMessage setMessageBody(MimeMessage msg) throws Exception {
			// -- Set the subject and body text --
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(
					body, "text/html; charset=\"utf-8\"")));

			// -- Set some other header information --
			msg.setHeader("X-Mailer", "XML-Mail");
			msg.setSentDate(new Date());
			
			return msg;
		}
		/**
		 * Sending a mail with given values.<br>
		 * If the parameter "emailUsername" and "emailUserpass" is exist, use
		 * SMTP Authentication.
		 * 
		 * @param smtpServer
		 * @param to
		 * @param from
		 * @param subject
		 * @param body
		 * @param emailUsername
		 * @param emailUserpass
		 * @return
		 */
		public String send() {
			try {
				log.debug("Message sending in progress");
				log.debug("  To: " + recipients);
				log.debug("  Subject: " + subject);

				// -- Send the message --
				Transport.send(setMessageBody(getMessage()));

				return "success";
			} catch (Exception ex) {
				log.error("[mail send] ", ex);
				return "Error" + ex;
			}
		}

	}

}
