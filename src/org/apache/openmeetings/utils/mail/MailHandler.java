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
package org.apache.openmeetings.utils.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.MailMessageDao;
import org.apache.openmeetings.persistence.beans.basic.MailMessage;
import org.apache.openmeetings.persistence.beans.basic.MailMessage.Status;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 * 
 * @author swagner
 * 
 */
public class MailHandler {
	private static final Logger log = Red5LoggerFactory.getLogger(
			MailHandler.class, OpenmeetingsVariables.webAppRootKey);
	private static final int MAIL_SEND_TIMEOUT = 60 * 60 * 1000; // 1 hour
	
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private MailMessageDao mailMessageDao;

	protected MimeMessage appendIcsBody(MimeMessage msg, MailMessage m) throws Exception {
		log.debug("setMessageBody for iCal message");
		// -- Create a new message --
		Multipart multipart = new MimeMultipart();

		Multipart multiBody = new MimeMultipart("alternative");
		BodyPart html = new MimeBodyPart();
		html.setDataHandler(new DataHandler(new ByteArrayDataSource(m.getBody(), "text/html; charset=UTF-8")));
		multiBody.addBodyPart(html);

		BodyPart iCalContent = new MimeBodyPart();
		iCalContent.addHeader("content-class", "urn:content-classes:calendarmessage");
		iCalContent.setDataHandler(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(m.getIcs()),
				"text/calendar; charset=UTF-8; method=REQUEST")));
		multiBody.addBodyPart(iCalContent);
		BodyPart body = new MimeBodyPart();
		body.setContent(multiBody);
		multipart.addBodyPart(body);
		
		BodyPart iCalAttachment = new MimeBodyPart();
		iCalAttachment.setDataHandler(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(m.getIcs()),
				"application/ics")));
		iCalAttachment.removeHeader("Content-Transfer-Encoding");
		iCalAttachment.addHeader("Content-Transfer-Encoding", "base64");
		iCalAttachment.removeHeader("Content-Type");
		iCalAttachment.addHeader("Content-Type", "application/ics");
		iCalAttachment.setFileName("invite.ics");
		multipart.addBodyPart(iCalAttachment);

		msg.setContent(multipart);
		return msg;
	}
	
	private MimeMessage appendBody(MimeMessage msg, MailMessage m) throws MessagingException, IOException {
		// -- Set the subject and body text --
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(m.getBody(), "text/html; charset=\"utf-8\"")));

		// -- Set some other header information --
		msg.setHeader("X-Mailer", "XML-Mail");
		msg.setSentDate(new Date());
		
		return msg;
	}
	
	private MimeMessage getBasicMimeMessage() throws Exception {
		log.debug("getBasicMimeMessage");

		// Evaluating Configuration Data
		String smtpServer = cfgDao.getConfValue("smtp_server", String.class, null);
		String smtpPort = cfgDao.getConfValue("smtp_port", String.class, "25");
		String from = cfgDao.getConfValue("system_email_addr", String.class, null);
		String mailAuthUser = cfgDao.getConfValue("email_username", String.class, null);
		String mailAuthPass = cfgDao.getConfValue("email_userpass", String.class, null);

		Properties props = System.getProperties();

		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpPort);

		if ("1".equals(cfgDao.getConfValue("mail.smtp.starttls.enable", String.class, "0"))) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		// Check for Authentication
		Session session = null;
		if (mailAuthUser != null && mailAuthUser.length() > 0
				&& mailAuthPass != null && mailAuthPass.length() > 0) {
			// use SMTP Authentication
			props.put("mail.smtp.auth", "true");
			session = Session.getDefaultInstance(props, new SmtpAuthenticator(mailAuthUser, mailAuthPass));
		} else {
			// not use SMTP Authentication
			session = Session.getDefaultInstance(props, null);
		}

		// Building MimeMessage
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		return msg;
	}
	
	private MimeMessage getMimeMessage(MimeMessage base, MailMessage m) throws Exception {
		log.debug("getMimeMessage");
		// Building MimeMessage
		MimeMessage msg = base == null ? getBasicMimeMessage() : new MimeMessage(base);
		msg.setSubject(m.getSubject());
		String replyTo = m.getReplyTo();
		if (replyTo != null && "1".equals(cfgDao.getConfValue("inviter.email.as.replyto", String.class, "1"))) {
			log.debug("setReplyTo " + replyTo);
			if (MailUtil.matches(replyTo)) {
				msg.setReplyTo(new InternetAddress[]{new InternetAddress(replyTo)});
			}
		}
		msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(m.getRecipients(), false));
		
		return m.getIcs() == null ? appendBody(msg, m) : appendIcsBody(msg, m);
	}
	
	public void send(String toEmail, String subj, String message) {
		send(toEmail, null, subj, message);
	}
	
	public void send(String toEmail, String replyTo, String subj, String message) {
		send(new MailMessage(toEmail, replyTo, subj, message));
	}
	
	public void send(MailMessage m) {
		send(m, false);
	}
	
	public void send(final MailMessage m, boolean send) {
		send(null, m, send);
	}
	
	public void send(final MimeMessage base, final MailMessage m, boolean send) {
		if (send) {
			if (m.getId() != 0) {
				m.setStatus(Status.SENDING);
				mailMessageDao.update(m, null);
			}
			taskExecutor.execute(new Runnable() {
				public void run() {
					log.debug("Message sending in progress");
					log.debug("  To: " + m.getRecipients());
					log.debug("  Subject: " + m.getSubject());

					// -- Send the message --
					try {
						Transport.send(getMimeMessage(base, m));
						m.setStatus(Status.DONE);
					} catch (Exception e) {
						log.error("Error while sending message", e);
						m.setStatus(Status.NONE);
					}
					if (m.getId() != 0) {
						mailMessageDao.update(m, null);
					}
				}
			});
		} else {
			m.setStatus(Status.NONE);
			mailMessageDao.update(m, null);
		}
	}
	
	public void resetSendingStatus() {
		log.debug("resetSendingStatus enter ...");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, -MAIL_SEND_TIMEOUT);
		mailMessageDao.resetSendingStatus(c);
		log.debug("... resetSendingStatus done.");
	}
	
	public void sendMails() throws Exception {
		MimeMessage base = getBasicMimeMessage();
		log.debug("sendMails enter ...");
		List<MailMessage> list = mailMessageDao.get(0, 1);
		while (!list.isEmpty()) {
			send(base, list.get(0), true);
			list = mailMessageDao.get(0, 1);
		}
		log.debug("... sendMails done.");
	}
}
