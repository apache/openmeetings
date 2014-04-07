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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.openmeetings.util.mail.SmtpAuthenticator;
import org.junit.Test;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 
 * @author swagner
 *
 */
public class TestMailSending {
	
	//Example GMail email server data
	private String smtpServer = "smtp.gmail.com";
	private String smtpPort = "25";
	private String from = "test@apache.org";
	private String mailAuthUser = "test@gmail.com";
	private String mailAuthPass = "*******";
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
		try{
			
			Transport.send(getMimeMessage());
			
		} catch (Exception err) {
			err.printStackTrace();
		}
		assertTrue(true);
	}
	
	private MimeMessage getMimeMessage() throws Exception {
		// Building MimeMessage
		MimeMessage msg = getBasicMimeMessage();
		msg.setSubject("getSubject()");
		msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("seba.wagner@gmail.com", false));
		
		return appendBody(msg);
	}
	
	private MimeMessage appendBody(MimeMessage msg) throws MessagingException, IOException {
		// -- Set the subject and body text --
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource("getBody", "text/html; charset=\"utf-8\"")));

		// -- Set some other header information --
		msg.setHeader("X-Mailer", "XML-Mail");
		msg.setSentDate(new Date());
		
		return msg;
	}
	
	private MimeMessage getBasicMimeMessage() throws Exception {
		System.out.println("getBasicMimeMessage");
		Properties props = new Properties(System.getProperties());

		props.put("mail.smtp.connectiontimeout", 30000); //default timeout is 30 seconds, javaMail default is "infinite"
		props.put("mail.smtp.timeout", 30000); //default timeout is 30 seconds, javaMail default is "infinite"
		
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpPort);
		
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
}
