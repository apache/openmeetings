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
package org.apache.openmeetings.mail;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.basic.MailMessageDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.basic.MailMessage.Status;
import org.apache.openmeetings.util.mail.MailUtil;
import org.apache.openmeetings.util.mail.SmtpAuthenticator;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 
 * @author swagner
 * 
 * For a documentation about Javax mail please see fro example:
 * http://connector.sourceforge.net/doc-files/Properties.html
 * 
 */
public class MailHandler {
	private static final Logger log = Red5LoggerFactory.getLogger(MailHandler.class, webAppRootKey);
	private static final int MAIL_SEND_TIMEOUT = 60 * 60 * 1000; // 1 hour
	private static final int MAXIMUM_ERROR_COUNT = 5;
	
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private MailMessageDao mailMessageDao;
	private String smtpServer;
	private String smtpPort;
	private String from;
	private String mailAuthUser;
	private String mailAuthPass;
	private boolean mailTls;
	private boolean mailAddReplyTo;
	private int smtpConnectionTimeOut;
	private int smtpTimeOut;

	private void init() {
		smtpServer = cfgDao.getConfValue("smtp_server", String.class, null);
		smtpPort = cfgDao.getConfValue("smtp_port", String.class, "25");
		from = cfgDao.getConfValue("system_email_addr", String.class, null);
		mailAuthUser = cfgDao.getConfValue("email_username", String.class, null);
		mailAuthPass = cfgDao.getConfValue("email_userpass", String.class, null);
		mailTls = "1".equals(cfgDao.getConfValue("mail.smtp.starttls.enable", String.class, "0"));
		mailAddReplyTo = "1".equals(cfgDao.getConfValue("inviter.email.as.replyto", String.class, "1"));
		smtpConnectionTimeOut = cfgDao.getConfValue("mail.smtp.connection.timeout", Integer.class, "30000");
		smtpTimeOut = cfgDao.getConfValue("mail.smtp.timeout", Integer.class, "30000");
	}
	
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
		if (smtpServer == null) {
			init();
		}
		Properties props = new Properties(System.getProperties());

		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpPort);
		
		props.put("mail.smtp.connectiontimeout", smtpConnectionTimeOut); 
		props.put("mail.smtp.timeout", smtpTimeOut);

		if (mailTls) {
			props.put("mail.smtp.starttls.enable", "true");
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
		    sf.setTrustAllHosts(true);
		    props.put("mail.smtp.ssl.socketFactory", sf);
		}

		// Check for Authentication
		Session session = null;
		if (mailAuthUser != null && mailAuthUser.length() > 0
				&& mailAuthPass != null && mailAuthPass.length() > 0) {
			// use SMTP Authentication
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props, new SmtpAuthenticator(mailAuthUser, mailAuthPass));
		} else {
			// not use SMTP Authentication
			session = Session.getInstance(props, null);
		}

		// Building MimeMessage
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		return msg;
	}
	
	private MimeMessage getMimeMessage(MailMessage m) throws Exception {
		log.debug("getMimeMessage");
		// Building MimeMessage
		MimeMessage msg = getBasicMimeMessage();
		msg.setSubject(m.getSubject(), "UTF-8");
		String replyTo = m.getReplyTo();
		if (replyTo != null && mailAddReplyTo) {
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
		if (send) {
			if (m.getId() != null) {
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
						Transport.send(getMimeMessage(m));
						m.setLastError("");
						m.setStatus(Status.DONE);
					} catch (Exception e) {
						log.error("Error while sending message", e);
						m.setErrorCount(m.getErrorCount() + 1);
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						pw.close();
						m.setLastError(sw.getBuffer().toString());
						m.setStatus(m.getErrorCount() < MAXIMUM_ERROR_COUNT ? Status.NONE : Status.ERROR);
					}
					if (m.getId() != null) {
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
		init();
		log.debug("sendMails enter ...");
		List<MailMessage> list = mailMessageDao.get(0, 1);
		log.debug("Number of emails in init queue " + list.size());
		while (!list.isEmpty()) {
			send(list.get(0), true);
			list = mailMessageDao.get(0, 1);
		}
		log.debug("... sendMails done.");
	}
}
