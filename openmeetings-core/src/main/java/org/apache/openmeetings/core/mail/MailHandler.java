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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMailFrom;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpConnectionTimeOut;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpPass;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpPort;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpServer;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpTimeOut;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getSmtpUser;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMailAddReplyTo;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSmtpUseSsl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSmtpUseTls;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.openmeetings.db.dao.basic.MailMessageDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.basic.MailMessage.Status;
import org.apache.openmeetings.util.mail.ByteArrayDataSource;
import org.apache.openmeetings.util.mail.MailUtil;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.activation.DataHandler;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 *
 * @author swagner
 *
 * For a documentation about Javax mail please see for example:
 * http://connector.sourceforge.net/doc-files/Properties.html
 *
 */
@Component("mailHandler")
public class MailHandler {
	private static final Logger log = LoggerFactory.getLogger(MailHandler.class);
	private static final int MAIL_SEND_TIMEOUT = 60 * 60 * 1000; // 1 hour
	private static final int MAXIMUM_ERROR_COUNT = 5;

	@Inject
	private TaskExecutor taskExecutor;
	@Inject
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
				"text/calendar; charset=UTF-8; method=" + m.getIcsMethod())));
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

	private MimeMessage appendBody(MimeMessage msg, MailMessage m) throws MessagingException {
		return appendBody(msg, m.getBody());
	}

	public MimeMessage appendBody(MimeMessage msg, String body) throws MessagingException {
		// -- Set the subject and body text --
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html; charset=\"utf-8\"")));

		// -- Set some other header information --
		msg.setHeader("X-Mailer", "XML-Mail");
		msg.setSentDate(new Date());

		return msg;
	}

	// this method should be public for tests
	public MimeMessage getBasicMimeMessage() throws Exception {
		log.debug("getBasicMimeMessage");
		if (getSmtpServer() == null) {
			throw new IllegalStateException("SMTP settings were not provided");
		}
		Properties props = new Properties(System.getProperties());

		props.put("mail.smtp.host", getSmtpServer());
		props.put("mail.smtp.port", getSmtpPort());
		if (isSmtpUseTls() || isSmtpUseSsl()) {
			props.put("mail.smtp.ssl.trust", getSmtpServer());
		}
		if (isSmtpUseTls() && isSmtpUseSsl()) {
			log.warn("Both SSL and TLS are enabled, TLS will be started");
		}
		props.put("mail.smtp.starttls.enable", isSmtpUseTls());
		props.put("mail.smtp.ssl.enable", isSmtpUseSsl());
		props.put("mail.smtp.connectiontimeout", getSmtpConnectionTimeOut());
		props.put("mail.smtp.timeout", getSmtpTimeOut());

		// Check for Authentication
		Session session;
		if (!Strings.isEmpty(getSmtpUser()) && !Strings.isEmpty(getSmtpPass())) {
			// use SMTP Authentication
			props.put("mail.smtp.auth", true);
			session = Session.getDefaultInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(getSmtpUser(), getSmtpPass());
				}
			});
		} else {
			// not use SMTP Authentication
			session = Session.getInstance(props, null);
		}

		// Building MimeMessage
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(getMailFrom()));
		return msg;
	}

	private MimeMessage getMimeMessage(MailMessage m) throws Exception {
		log.debug("getMimeMessage");
		// Building MimeMessage
		MimeMessage msg = getBasicMimeMessage();
		msg.setSubject(m.getSubject(), UTF_8.name());
		String replyTo = m.getReplyTo();
		if (replyTo != null && isMailAddReplyTo()) {
			log.debug("setReplyTo {}", replyTo);
			if (MailUtil.isValid(replyTo)) {
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
		if (send) {
			if (m.getId() != null) {
				m.setStatus(Status.SENDING);
				mailMessageDao.update(m, null);
			}
			taskExecutor.execute(() -> {
				log.debug("Message sending in progress");
				log.debug("  To: {}", m.getRecipients());
				log.debug("  Subject: {}", m.getSubject());

				// -- Send the message --
				try {
					Transport.send(getMimeMessage(m));
					m.setLastError("");
					m.setStatus(Status.DONE);
				} catch (Exception e) {
					log.error("Error while sending message", e);
					m.setErrorCount(m.getErrorCount() + 1);
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					m.setLastError(sw.getBuffer().toString());
					m.setStatus(m.getErrorCount() < MAXIMUM_ERROR_COUNT ? Status.NONE : Status.ERROR);
				}
				if (m.getId() != null) {
					mailMessageDao.update(m, null);
				}
			});
		} else {
			m.setStatus(Status.NONE);
			mailMessageDao.update(m, null);
		}
	}

	public void resetSendingStatus() {
		log.trace("resetSendingStatus enter ...");
		if (!isInitComplete()) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, -MAIL_SEND_TIMEOUT);
		mailMessageDao.resetSendingStatus(c);
		log.trace("... resetSendingStatus done.");
	}

	public void sendMails() {
		log.trace("sendMails enter ...");
		List<MailMessage> list = mailMessageDao.get(0, 1, MailMessage.Status.NONE);
		if (!list.isEmpty()) {
			log.debug("Number of emails in init queue {}", list.size());
			while (!list.isEmpty()) {
				send(list.get(0), true);
				list = mailMessageDao.get(0, 1, MailMessage.Status.NONE);
			}
			log.debug("... sendMails done.");
		}
	}
}
