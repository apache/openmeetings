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

import java.io.ByteArrayInputStream;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.openmeetings.app.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class MailiCalThread extends MailThread {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MailiCalThread.class, OpenmeetingsVariables.webAppRootKey);

	public void doSend(String recipients, String replyTo, String subject, byte[] iCalMimeBody,
			String htmlBody) {
		taskExecutor.execute(new MailSenderTask(recipients, replyTo, subject,
				iCalMimeBody, htmlBody));

	}

	protected class MailSenderTask extends MailThread.MailSenderTask {
		private final byte[] iCalMimeBody;
		private final String htmlBody;

		public MailSenderTask(String recipients, String replyTo, String subject,
				byte[] iCalMimeBody, String htmlBody) {
			super(recipients, replyTo, subject, null);
			this.iCalMimeBody = iCalMimeBody;
			this.htmlBody = htmlBody;
		}

		@Override
		protected MimeMessage setMessageBody(MimeMessage msg) throws Exception {
			log.debug("setMessageBody for iCal message");
			// -- Create a new message --
			Multipart multipart = new MimeMultipart();

			BodyPart html = new MimeBodyPart();
			html.setDataHandler(new DataHandler(new ByteArrayDataSource(
					htmlBody, "text/html; charset=\"utf-8\"")));

			BodyPart iCalAttachment = new MimeBodyPart();
			iCalAttachment.setDataHandler(new DataHandler(
					new ByteArrayDataSource(
							new ByteArrayInputStream(iCalMimeBody),
							"text/calendar;method=REQUEST;charset=\"UTF-8\"")));

			multipart.addBodyPart(html);
			multipart.addBodyPart(iCalAttachment);

			msg.setContent(multipart);
			return msg;
		}
	}
}
