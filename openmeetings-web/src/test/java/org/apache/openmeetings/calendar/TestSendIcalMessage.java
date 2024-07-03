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
package org.apache.openmeetings.calendar;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.CalendarHelper.getZoneDateTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.util.mail.ByteArrayDataSource;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.activation.DataHandler;
import jakarta.inject.Inject;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

class TestSendIcalMessage extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestSendIcalMessage.class);

	@Inject
	private MailHandler mailHandler;

	String subject = "test iCal";
	String recipients = "seba.wagner@gmail.com";
	String htmlBody = "test";


	@Test
	void sendInvitionIcalLink() throws Exception {
		String email = "hans@webbase-design.de";
		String username = "shans";
		boolean invitor = false;

		Date start = Calendar.getInstance().getTime();
		Calendar endCal = Calendar.getInstance();
		endCal.add(Calendar.HOUR_OF_DAY, 1);
		Date end = endCal.getTime();
		String tzid = TimeZone.getDefault().getID();
		IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST)
				.createVEvent(getZoneDateTime(start, tzid), getZoneDateTime(end, tzid), "test event")
				.setLocation("")
				.setDescription("localhost:5080/link_openmeetings")
				.setUid(randomUUID().toString())
				.setSequence(0)
				.addOrganizer(recipients, "seba-test")
				.addAttendee(email, username, invitor)
				.build();

		log.debug("ICS: {}", handler);
		assertNotNull(handler.toString(), "Valid ICS should be created");

		sendIcalMessage(handler.toByteArray());
	}

	private void sendIcalMessage(byte[] iCalMimeBody) throws Exception {
		log.debug("sendIcalMessage");

		// Building MimeMessage
		MimeMessage mimeMessage = mailHandler.getBasicMimeMessage();
		mimeMessage.setSubject(subject);
		mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));

		// -- Create a new message --
		BodyPart msg = new MimeBodyPart();
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlBody, "text/html; charset=\"utf-8\"")));

		Multipart multipart = new MimeMultipart();

		BodyPart iCalAttachment = new MimeBodyPart();
		iCalAttachment.setDataHandler(new DataHandler(new jakarta.mail.util.ByteArrayDataSource(new ByteArrayInputStream(iCalMimeBody), "text/calendar;method=REQUEST;charset=\"UTF-8\"")));
		iCalAttachment.setFileName("invite.ics");

		multipart.addBodyPart(iCalAttachment);
		multipart.addBodyPart(msg);

		mimeMessage.setSentDate(new Date());
		mimeMessage.setContent(multipart);

		assertNotNull(mimeMessage, "Valid MIME message should be created");
	}
}
