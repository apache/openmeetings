package org.openmeetings.test.calendar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

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

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
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

import org.junit.Test;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.openmeetings.utils.mail.ByteArrayDataSource;
import org.openmeetings.utils.mail.IcalHandler;
import org.openmeetings.utils.mail.SmtpAuthenticator;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestSendIcalMessage extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Red5LoggerFactory.getLogger(
			TestAppointmentSchedulerTask.class,
			ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;

	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;

	private byte[] iCalMimeBody;

	String subject = "test iCal";
	String recipients = "seba.wagner@gmail.com";
	String htmlBody = "test";

	
	public void simpleInvitionIcalLink() {
		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
				.createRegistry();
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
			e.printStackTrace();
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
		
		System.out.println(icsCalendar);
		
		
		

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		try {
			outputter.output(icsCalendar, bout);
			iCalMimeBody = bout.toByteArray();
			
			sendIcalMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	
	@Test
	public void sendInvitionIcalLink() {
		try {

			String email = "hans@webbase-design.de";
			String username = "shans";
			boolean invitor = false;

			String jNametimeZone = "Etc/GMT+1";

			GregorianCalendar start = new GregorianCalendar();
			Calendar start1 = Calendar.getInstance();
			start.setTimeInMillis(start1.getTimeInMillis() + 900000);

			GregorianCalendar end = new GregorianCalendar();
			Calendar end1 = Calendar.getInstance();
			end.setTimeInMillis(end1.getTimeInMillis() + 1800000);

			IcalHandler handler = new IcalHandler(omTimeZoneDaoImpl,
					IcalHandler.ICAL_METHOD_REQUEST);

			// Transforming Meeting Members

			HashMap<String, String> attendeeList = handler.getAttendeeData(
					email, username, invitor);

			HashMap<String, String> organizerAttendee = handler
					.getAttendeeData(recipients, "seba-test", true);

			Vector<HashMap<String, String>> atts = new Vector<HashMap<String, String>>();
			atts.add(attendeeList);

			// Create ICal Message
			String meetingId = handler.addNewMeeting(start, end, "test event",
					atts, "localhost:5080/link_openmeetings",
					organizerAttendee, "", jNametimeZone);

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

		// Evaluating Configuration Data
		String smtpServer = cfgManagement.getConfKey(3, "smtp_server")
				.getConf_value();
		String smtpPort = cfgManagement.getConfKey(3, "smtp_port")
				.getConf_value();
		// String from = "openmeetings@xmlcrm.org";
		String from = cfgManagement.getConfKey(3, "system_email_addr")
				.getConf_value();

		String emailUsername = cfgManagement.getConfKey(3, "email_username")
				.getConf_value();
		String emailUserpass = cfgManagement.getConfKey(3, "email_userpass")
				.getConf_value();

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
			session = Session.getDefaultInstance(props, new SmtpAuthenticator(
					emailUsername, emailUserpass));
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
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlBody,
				"text/html; charset=\"utf-8\"")));

		Multipart multipart = new MimeMultipart();

		BodyPart iCalAttachment = new MimeBodyPart();
		iCalAttachment.setDataHandler(new DataHandler(
				new javax.mail.util.ByteArrayDataSource(
						new ByteArrayInputStream(iCalMimeBody),
						"text/calendar;method=REQUEST;charset=\"UTF-8\"")));

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
