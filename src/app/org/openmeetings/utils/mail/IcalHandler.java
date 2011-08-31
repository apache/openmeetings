package org.openmeetings.utils.mail;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

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
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author o.becherer
 * 
 */
public class IcalHandler {
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;

	private static final Logger log = Red5LoggerFactory.getLogger(
			MailHandler.class, ScopeApplicationAdapter.webAppRootKey);

	/** ICal instance */
	private final net.fortuna.ical4j.model.Calendar icsCalendar;

	/** TimeZone */
	// private TimeZone timeZone;
	// private TimeZoneRegistry timeRegistry;

	/** Creation of a new Event */
	public final static String ICAL_METHOD_REQUEST = "REQUEST";
	public final static String ICAL_METHOD_CANCEL = "CANCEL";
	public final static String ICAL_METHOD_REFRESH = "REFRESH";

	/**
	 * Konstruktor with DefaultTimeZone
	 * 
	 * @param methodType
	 *            (@see IcalHandler Constants)
	 * @throws Exception
	 */
	public IcalHandler(OmTimeZoneDaoImpl omTimeZoneDaoImpl, String methodType) {
		log.debug("Icalhandler method type : " + methodType);
		this.omTimeZoneDaoImpl = omTimeZoneDaoImpl;
		
		icsCalendar = new net.fortuna.ical4j.model.Calendar();

		// timeRegistry =
		// TimeZoneRegistryFactory.getInstance().createRegistry();
		// timeZone =
		// timeRegistry.getTimeZone(java.util.TimeZone.getDefault().getID());

		icsCalendar.getProperties().add(
				new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		icsCalendar.getProperties().add(Version.VERSION_2_0);

		// Switch Method Type
		if (methodType.equals(ICAL_METHOD_REFRESH))
			icsCalendar.getProperties().add(Method.REFRESH);
		else if (methodType.equals(ICAL_METHOD_CANCEL))
			icsCalendar.getProperties().add(Method.CANCEL);
		else
			icsCalendar.getProperties().add(Method.REQUEST);

	}

	/**
	 * 
	 * @param startDate
	 *            use standard TimeZone!!
	 * @param endDate
	 *            use standard time zone!!
	 * @param name
	 *            meeting name
	 * @param attendees
	 *            List of attendees (use getAttendeeData to retrieve valid
	 *            records)
	 * @param description
	 *            containing the meeting description
	 * @param uid
	 *            (maybe null)
	 * @return UID of Meeting
	 */
	// ---------------------------------------------------------------------------------------
	public String addNewMeeting(GregorianCalendar startDate,
			GregorianCalendar endDate, String name,
			Vector<HashMap<String, String>> attendees, String description,
			HashMap<String, String> organizer, String uid, String jNametimeZone)
			throws Exception {

		log.debug("add new Meeting -1 " + jNametimeZone);
		log.debug("add new Meeting -2 "
				+ java.util.TimeZone.getDefault().getID());

		// timeZone = timeRegistry.getTimeZone(jNametimeZone);

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
				.createRegistry();

		OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jNametimeZone);

		TimeZone timeZone = registry.getTimeZone(omTimeZone.getIcal());

		startDate.setTimeZone(timeZone);
		endDate.setTimeZone(timeZone);

		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent(start, end, name);

		log.debug("DateTime start addNewMeeting " + start);
		log.debug("DateTime end addNewMeeting " + end);

		// add timezone info..
		VTimeZone tz = timeZone.getVTimeZone();

		meeting.getProperties().add(tz.getTimeZoneId());

		meeting.getProperties().add(new Description(description));

		meeting.getProperties().add(new Sequence(1));

		// generate unique identifier (if not submitted)
		Uid ui = null;
		if (uid == null || uid.length() < 1) {
			ui = new UidGenerator("uidGen").generateUid();
			log.debug("Generating Meeting UID : " + ui.getValue());
		} else {
			ui = new Uid(uid);
			log.debug("Using Meeting UID : " + ui.getValue());
		}

		meeting.getProperties().add(ui);

		for (int i = 0; i < attendees.size(); i++) {
			HashMap<String, String> oneAtt = attendees.get(i);

			Attendee uno = new Attendee(URI.create(oneAtt.get("uri")));

			String chair = oneAtt.get("chair");

			if (chair.equals("0"))
				uno.getParameters().add(Role.REQ_PARTICIPANT);
			else
				uno.getParameters().add(Role.CHAIR);

			uno.getParameters().add(new Cn(oneAtt.get("cn")));
			meeting.getProperties().add(uno);
		}

		Organizer orger = new Organizer(URI.create(organizer.get("uri")));
		orger.getParameters().add(new Cn(organizer.get("cn")));

		meeting.getProperties().add(orger);

		icsCalendar.getComponents().add(meeting);

		return ui.getValue();

	}

	// ---------------------------------------------------------------------------------------

	/**
	 * Use this function to build a valid record for the AttendeeList for
	 * addMeetings Generate a Attendee
	 */
	// ------------------------------------------------------------------------------------------
	public HashMap<String, String> getAttendeeData(String emailAdress,
			String displayName, Boolean chair) {

		HashMap<String, String> oneRecord = new HashMap<String, String>();
		oneRecord.put("uri", "mailto:" + emailAdress);
		oneRecord.put("cn", displayName);

		if (chair)
			oneRecord.put("chair", "1");
		else
			oneRecord.put("chair", "0");

		return oneRecord;

	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Write iCal to File
	 */
	// ------------------------------------------------------------------------------------------
	public void writeDataToFile(String filerPath) throws Exception {

		if (!filerPath.endsWith(".ics"))
			filerPath = filerPath + ".ics";

		FileOutputStream fout = new FileOutputStream(filerPath);

		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(icsCalendar, fout);

	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Get IcalBody as ByteArray
	 */
	// ------------------------------------------------------------------------------------------
	public byte[] getIcalAsByteArray() throws Exception {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(icsCalendar, bout);
		return bout.toByteArray();

	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Retrieving Data as String
	 */
	// ------------------------------------------------------------------------------------------
	public String getICalDataAsString() {
		return icsCalendar.toString();
	}
	// ------------------------------------------------------------------------------------------

}
