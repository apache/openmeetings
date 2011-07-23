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

public class MailThread extends Thread {
	
	private static final Logger log = Red5LoggerFactory.getLogger(MailHandler.class, ScopeApplicationAdapter.webAppRootKey);

	private String smtpServer; 
	private String smtpPort;  
	private String to;  
	private String from; 
	private String subject; 
	private String body;  
	private String emailUsername;  
	private String emailUserpass;
	
	public MailThread(String smtpServer, String smtpPort, String to,
			String from, String subject, String body, String emailUsername,
			String emailUserpass) {
		super();
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
		try {

			this.send();

		} catch (Exception err) {
			log.error("MailThread ", err);
		}
	}
	
	/**
	 * Sending a mail with given values.<br>
	 * If the parameter "emailUsername" and "emailUserpass" is exist, use SMTP Authentication.
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

			// -- Attaching to default Session, or we could start a new one --
			//smtpPort 25 or 587
			props.put("mail.smtp.host", smtpServer);
			props.put("mail.smtp.port", smtpPort);
			
			Configuration conf = Configurationmanagement.getInstance().getConfKey(3, "mail.smtp.starttls.enable");
			if (conf != null) {
				if (conf.getConf_value().equals("1")){
					props.put("mail.smtp.starttls.enable","true");
				}
			}

			Session session = null;
			if (emailUsername != null && emailUsername.length() > 0
					&& emailUserpass != null && emailUserpass.length() > 0) {
				//use SMTP Authentication
				props.put("mail.smtp.auth", "true");
				session = Session.getDefaultInstance(props,
						new SmtpAuthenticator());
			}else{
				//not use SMTP Authentication
				session = Session.getDefaultInstance(props, null);
			}


			// -- Create a new message --
			Message msg = new MimeMessage(session);

			// -- Set the FROM and TO fields --
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
					to, false));

			// -- We could include CC recipients too --
			// if (cc != null)
			// msg.setRecipients(Message.RecipientType.CC
			// ,InternetAddress.parse(cc, false));

			// -- Set the subject and body text --
			msg.setSubject(subject);
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(body,
					"text/html; charset=\"utf-8\"")));

			// -- Set some other header information --
			msg.setHeader("X-Mailer", "XML-Mail");
			msg.setSentDate(new Date());

			// -- Send the message --
			Transport.send(msg);

			return "success";
		} catch (Exception ex) {
			log.error("[mail send] " ,ex);
			return "Error" + ex;
		}
	}
	
}
