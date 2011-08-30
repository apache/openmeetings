package org.openmeetings.utils.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class MailThread {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MailHandler.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;

	private final TaskExecutor taskExecutor;

	public MailThread(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void doSend(String smtpServer, String smtpPort, String to,
			String from, String subject, String body, String emailUsername,
			String emailUserpass) {

		this.taskExecutor.execute(new MailSenderTask(smtpServer, smtpPort, to,
				from, subject, body, emailUsername, emailUserpass));

	}

	private class MailSenderTask implements Runnable {

		private final String smtpServer;
		private final String smtpPort;
		private final String to;
		private final String from;
		private final String subject;
		private final String body;
		private final String emailUsername;
		private final String emailUserpass;

		public MailSenderTask(String smtpServer, String smtpPort, String to,
				String from, String subject, String body, String emailUsername,
				String emailUserpass) {
			this.smtpServer = smtpServer;
			this.smtpPort = smtpPort;
			this.to = to;
			this.from = from;
			this.subject = subject;
			this.body = body;
			this.emailUsername = emailUsername;
			this.emailUserpass = emailUserpass;
		}

		public void run() {
			this.send();
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
				log.debug("  From: " + from);
				log.debug("  To: " + to);
				log.debug("  Subject: " + subject);

				Properties props = System.getProperties();

				// -- Attaching to default Session, or we could start a new one
				// --
				// smtpPort 25 or 587
				props.put("mail.smtp.host", smtpServer);
				props.put("mail.smtp.port", smtpPort);

				Configuration conf = cfgManagement.getConfKey(3,
						"mail.smtp.starttls.enable");
				if (conf != null) {
					if (conf.getConf_value().equals("1")) {
						props.put("mail.smtp.starttls.enable", "true");
					}
				}

				Session session = null;
				if (emailUsername != null && emailUsername.length() > 0
						&& emailUserpass != null && emailUserpass.length() > 0) {
					// use SMTP Authentication
					props.put("mail.smtp.auth", "true");
					session = Session
							.getDefaultInstance(props, new SmtpAuthenticator(
									emailUsername, emailUserpass));
				} else {
					// not use SMTP Authentication
					session = Session.getDefaultInstance(props, null);
				}

				// -- Create a new message --
				Message msg = new MimeMessage(session);

				// -- Set the FROM and TO fields --
				msg.setFrom(new InternetAddress(from));
				msg.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(to, false));

				// -- We could include CC recipients too --
				// if (cc != null)
				// msg.setRecipients(Message.RecipientType.CC
				// ,InternetAddress.parse(cc, false));

				// -- Set the subject and body text --
				msg.setSubject(subject);
				msg.setDataHandler(new DataHandler(new ByteArrayDataSource(
						body, "text/html; charset=\"utf-8\"")));

				// -- Set some other header information --
				msg.setHeader("X-Mailer", "XML-Mail");
				msg.setSentDate(new Date());

				// -- Send the message --
				Transport.send(msg);

				return "success";
			} catch (Exception ex) {
				log.error("[mail send] ", ex);
				return "Error" + ex;
			}
		}

	}

}
