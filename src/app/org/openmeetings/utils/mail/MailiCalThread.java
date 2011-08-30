package org.openmeetings.utils.mail;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class MailiCalThread extends Thread {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MailiCalThread.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;

	private final TaskExecutor taskExecutor;

	public MailiCalThread(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void doSend(String recipients, String subject, byte[] iCalMimeBody,
			String htmlBody) {

		this.taskExecutor.execute(new MailSenderTask(recipients, subject,
				iCalMimeBody, htmlBody));

	}

	private class MailSenderTask implements Runnable {

		private final String recipients;
		private final String subject;
		private final byte[] iCalMimeBody;
		private final String htmlBody;

		public MailSenderTask(String recipients, String subject,
				byte[] iCalMimeBody, String htmlBody) {
			super();
			this.recipients = recipients;
			this.subject = subject;
			this.iCalMimeBody = iCalMimeBody;
			this.htmlBody = htmlBody;
		}

		public void run() {
			try {

				this.sendIcalMessage();

			} catch (Exception err) {
				log.error("MailSenderTask ", err);
			}
		}

		/**
		 * @author o.becherer
		 * @param recipients
		 *            (List of valid mail adresses)
		 * @param subject
		 *            mailSubject
		 * @param iCalMimeBody
		 *            byte[] containing Icaldata Sending Ical Invitation
		 */
		// ---------------------------------------------------------------------------------------------
		public void sendIcalMessage() throws Exception {
			log.debug("sendIcalMessage");

			// Evaluating Configuration Data
			String smtpServer = cfgManagement.getConfKey(3, "smtp_server")
					.getConf_value();
			String smtpPort = cfgManagement.getConfKey(3, "smtp_port")
					.getConf_value();
			// String from = "openmeetings@xmlcrm.org";
			String from = cfgManagement.getConfKey(3, "system_email_addr")
					.getConf_value();

			String emailUsername = cfgManagement
					.getConfKey(3, "email_username").getConf_value();
			String emailUserpass = cfgManagement
					.getConfKey(3, "email_userpass").getConf_value();

			Properties props = System.getProperties();

			props.put("mail.smtp.host", smtpServer);
			props.put("mail.smtp.port", smtpPort);

			Configuration conf = cfgManagement.getConfKey(3,
					"mail.smtp.starttls.enable");
			if (conf != null) {
				if (conf.getConf_value().equals("1")) {
					props.put("mail.smtp.starttls.enable", "true");
				}
			}

			// Check for Authentification
			Session session = null;
			if (emailUsername != null && emailUsername.length() > 0
					&& emailUserpass != null && emailUserpass.length() > 0) {
				// use SMTP Authentication
				props.put("mail.smtp.auth", "true");
				session = Session.getDefaultInstance(props,
						new SmtpAuthenticator(emailUsername, emailUserpass));
			} else {
				// not use SMTP Authentication
				session = Session.getDefaultInstance(props, null);
			}

			// Building MimeMessage
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress(from));
			mimeMessage.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipients, false));

			// -- Create a new message --
			BodyPart msg = new MimeBodyPart();
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(
					htmlBody, "text/html; charset=\"utf-8\"")));

			Multipart multipart = new MimeMultipart();

			BodyPart iCalAttachment = new MimeBodyPart();
			iCalAttachment.setDataHandler(new DataHandler(
					new javax.mail.util.ByteArrayDataSource(
							new ByteArrayInputStream(iCalMimeBody),
							"text/calendar;method=REQUEST;charset=\"UTF-8\"")));

			multipart.addBodyPart(iCalAttachment);
			multipart.addBodyPart(msg);

			mimeMessage.setContent(multipart);

			// -- Set some other header information --
			// mimeMessage.setHeader("X-Mailer", "XML-Mail");
			// mimeMessage.setSentDate(new Date());

			// Transport trans = session.getTransport("smtp");
			Transport.send(mimeMessage);

		}

	}
}
