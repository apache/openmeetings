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
package org.apache.openmeetings.test.calendar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.apache.openmeetings.util.mail.ByteArrayDataSource;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

public class TestSendIcalMessage extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestSendIcalMessage.class, webAppRootKey);

	@Autowired
	private MailHandler mailHandler;

	private byte[] iCalMimeBody;

	String subject = "test iCal";
	String recipients = "seba.wagner@gmail.com";
	String htmlBody = "test";

	
	public void simpleInvitionIcalLink() {
		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone("America/Mexico_City");
		VTimeZone tz = timezone.getVTimeZone();

		// Start Date is on: April 1, 2008, 9:00 am
		java.util.Calendar startDate = new GregorianCalendar();
		startDate.setTimeZone(timezone);
		startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		startDate.set(java.util.Calendar.YEAR, 2008);
		startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
		startDate.set(java.util.Calendar.MINUTE, 0);
		startDate.set(java.util.Calendar.SECOND, 0);

		// End Date is on: April 1, 2008, 13:00
		java.util.Calendar endDate = new GregorianCalendar();
		endDate.setTimeZone(timezone);
		endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
		endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		endDate.set(java.util.Calendar.YEAR, 2008);
		endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
		endDate.set(java.util.Calendar.MINUTE, 0);
		endDate.set(java.util.Calendar.SECOND, 0);

		// Create the event
		String eventName = "Progress Meeting";
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent(start, end, eventName);

		// add timezone info..
		meeting.getProperties().add(tz.getTimeZoneId());

		// generate unique identifier..
		UidGenerator ug;
		try {
			ug = new UidGenerator("uidGen");

			Uid uid = ug.generateUid();
			meeting.getProperties().add(uid);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			log.error("Error", e);
		}

		// add attendees..
		Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
		dev1.getParameters().add(Role.REQ_PARTICIPANT);
		dev1.getParameters().add(new Cn("Developer 1"));
		meeting.getProperties().add(dev1);

		Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
		dev2.getParameters().add(Role.OPT_PARTICIPANT);
		dev2.getParameters().add(new Cn("Developer 2"));
		meeting.getProperties().add(dev2);

		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(
				new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		icsCalendar.getProperties().add(Version.VERSION_2_0);

		// Add the event and print
		icsCalendar.getComponents().add(meeting);
		
		Organizer orger = new Organizer(URI.create("seba.wagner@gmail.com"));
		orger.getParameters().add(new Cn("Sebastian Wagner"));
		meeting.getProperties().add(orger);
		
		icsCalendar.getProperties().add(Method.REQUEST);
		
		log.debug(icsCalendar.toString());
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		try {
			outputter.output(icsCalendar, bout);
			iCalMimeBody = bout.toByteArray();
			
			sendIcalMessage();
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	
	@Test
	public void sendInvitionIcalLink() {
		try {
			String email = "hans@webbase-design.de";
			String username = "shans";
			boolean invitor = false;

			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST);

			// Transforming Meeting Members

			Map<String, String> attendeeList = handler.getAttendeeData(email, username, invitor);
			Map<String, String> organizerAttendee = handler.getAttendeeData(recipients, "seba-test", true);

			Vector<Map<String, String>> atts = new Vector<Map<String, String>>();
			atts.add(attendeeList);

			// Create ICal Message
			String meetingId = handler.addNewMeeting(start.getTime(), end.getTime(), "test event",
					atts, "localhost:5080/link_openmeetings",
					organizerAttendee, "", TimeZone.getDefault().getID());

			log.debug("meetingId " + meetingId);

			iCalMimeBody = handler.getIcalAsByteArray();

			sendIcalMessage();

			// return MailHandler.sendMail(email, subject, template);

		} catch (Exception err) {
			log.error("sendInvitionIcalLink", err);
		}
	}

	private void sendIcalMessage() throws Exception {
		log.debug("sendIcalMessage");

		// Building MimeMessage
		MimeMessage mimeMessage = mailHandler.getBasicMimeMessage();
		mimeMessage.setSubject(subject);
		mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));

		// -- Create a new message --
		BodyPart msg = new MimeBodyPart();
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlBody,
				"text/html; charset=\"utf-8\"")));

		Multipart multipart = new MimeMultipart();

		BodyPart iCalAttachment = new MimeBodyPart();
		iCalAttachment.setDataHandler(new DataHandler(
				new javax.mail.util.ByteArrayDataSource(
						new ByteArrayInputStream(iCalMimeBody),
						"text/calendar;method=REQUEST;charset=\"UTF-8\"")));
		iCalAttachment.setFileName("invite.ics");

		multipart.addBodyPart(iCalAttachment);
		multipart.addBodyPart(msg);

		mimeMessage.setSentDate(new Date());
		mimeMessage.setContent(multipart);

		// -- Set some other header information --
		// mimeMessage.setHeader("X-Mailer", "XML-Mail");
		// mimeMessage.setSentDate(new Date());

		// Transport trans = session.getTransport("smtp");
		Transport.send(mimeMessage);
	}
}
