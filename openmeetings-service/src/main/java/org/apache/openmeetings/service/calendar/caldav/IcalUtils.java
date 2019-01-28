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
package org.apache.openmeetings.service.calendar.caldav;

import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.net.URI;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

/**
 * Class which provides iCalendar Utilities.
 * This class's functions could be made static, as they are not instantiated anyway.
 */
@org.springframework.stereotype.Component
public class IcalUtils {
	private static final Logger log = Red5LoggerFactory.getLogger(IcalUtils.class, getWebAppRootKey());
	public static final String PROD_ID = "-//Events Calendar//Apache Openmeetings//EN";

	@Autowired
	private UserDao userDao;

	/**
	 * Parses the Calendar from the CalDAV server, to a new Appointment.
	 *
	 * @param calendar   iCalendar Representation.
	 * @param href       Location of the Calendar on the server
	 * @param etag       ETag of the calendar.
	 * @param omCalendar The Parent OmCalendar, to which the Appointment belongs.
	 * @return Appointment after parsing.
	 */
	public Appointment parseCalendartoAppointment(Calendar calendar, String href, String etag, OmCalendar omCalendar) {
		//Note: By RFC 4791 only one event can be stored in one href.

		Appointment a = new Appointment();
		a.setId(null);
		a.setDeleted(false);
		a.setHref(href);
		a.setCalendar(omCalendar);
		a.setOwner(omCalendar.getOwner());
		a.setRoom(createDefaultRoom());
		a.setReminder(Appointment.Reminder.none);

		return this.parseCalendartoAppointment(a, calendar, etag);
	}

	/**
	 * Parses a Calendar with multiple VEvents into Appointments
	 *
	 * @param calendar Calendar to Parse
	 * @param ownerId  Owner of the Appointments
	 * @return <code>List</code> of Appointments
	 */
	public List<Appointment> parseCalendartoAppointments(Calendar calendar, Long ownerId) {
		List<Appointment> appointments = new ArrayList<>();
		ComponentList<CalendarComponent> events = calendar.getComponents(Component.VEVENT);
		User owner = userDao.get(ownerId);

		for (CalendarComponent event : events) {
			Appointment a = new Appointment();
			a.setOwner(owner);
			a.setDeleted(false);
			a.setRoom(createDefaultRoom());
			a.setReminder(Appointment.Reminder.none);
			a = addVEventPropertiestoAppointment(a, event);
			appointments.add(a);
		}
		return appointments;
	}

	/**
	 * Updating Appointments which already exist, by parsing the Calendar. And updating etag.
	 * Doesn't work with complex Recurrences.
	 * Note: Hasn't been tested to acknowledge DST, timezones should acknowledge this.
	 *
	 * @param a        Appointment to be updated.
	 * @param calendar iCalendar Representation.
	 * @param etag     The ETag of the calendar.
	 * @return Updated Appointment.
	 */
	public Appointment parseCalendartoAppointment(Appointment a, Calendar calendar, String etag) {
		if (calendar == null) {
			return a;
		}
		CalendarComponent event = calendar.getComponent(Component.VEVENT);
		if (event != null) {
			a.setEtag(etag);
			a = addVEventPropertiestoAppointment(a, event);
		}
		return a;
	}

	/**
	 * Add properties from the Given VEvent Component to the Appointment
	 *
	 * @param a     Appointment to which the properties are to be added
	 * @param event VEvent to parse properties from.
	 * @return Updated Appointment
	 */
	private Appointment addVEventPropertiestoAppointment(Appointment a, CalendarComponent event) {
		DateProperty dtstart = (DateProperty)event.getProperty(Property.DTSTART)
				, dtend = (DateProperty)event.getProperty(Property.DTEND)
				, dtstamp = (DateProperty)event.getProperty(Property.DTSTAMP)
				, lastmod = (DateProperty)event.getProperty(Property.LAST_MODIFIED);
		Property uid = event.getProperty(Property.UID),
				description = event.getProperty(Property.DESCRIPTION),
				summary = event.getProperty(Property.SUMMARY),
				location = event.getProperty(Property.LOCATION),
				organizer = event.getProperty(Property.ORGANIZER),
				recur = event.getProperty(Property.RRULE);
		PropertyList<Attendee> attendees = event.getProperties(Property.ATTENDEE);

		if (uid != null) {
			a.setIcalId(uid.getValue());
		}

		Date d = dtstart.getDate();
		a.setStart(d);
		if (dtend == null) {
			a.setEnd(addTimetoDate(d, java.util.Calendar.HOUR_OF_DAY, 1));
		} else {
			a.setEnd(dtend.getDate());
		}

		a.setInserted(dtstamp.getDate());

		if (lastmod != null) {
			a.setUpdated(lastmod.getDate());
		}

		if (description != null) {
			a.setDescription(description.getValue());
		}

		if (summary != null) {
			a.setTitle(summary.getValue());
		}

		if (location != null) {
			a.setLocation(location.getValue());
		}

		if (recur != null) {
			Parameter freq = recur.getParameter("FREQ");
			if (freq != null) {
				if (freq.getValue().equals(Frequency.DAILY.name())) {
					a.setIsDaily(true);
				} else if (freq.getValue().equals(Frequency.WEEKLY.name())) {
					a.setIsWeekly(true);
				} else if (freq.getValue().equals(Frequency.MONTHLY.name())) {
					a.setIsMonthly(true);
				} else if (freq.getValue().equals(Frequency.YEARLY.name())) {
					a.setIsYearly(true);
				}
			}
		}

		Set<MeetingMember> attList = a.getMeetingMembers() == null ? new HashSet<>()
				: new HashSet<>(a.getMeetingMembers());
		String organizerEmail = null;

		//Note this value can be repeated in attendees as well.
		if (organizer != null) {
			URI uri = URI.create(organizer.getValue());

			//If the value of the organizer is an email
			if ("mailto".equals(uri.getScheme())) {
				String email = uri.getSchemeSpecificPart();

				organizerEmail = email;
				if (!email.equals(a.getOwner().getAddress().getEmail())) {
					//Contact or exist and owner
					User org = userDao.getByEmail(email);
					if (org == null) {
						org = userDao.getContact(email, a.getOwner());
						attList.add(createMeetingMember(a, org));
					} else if (!org.getId().equals(a.getOwner().getId())) {
						attList.add(createMeetingMember(a, org));
					}
				}
			}
		}

		if (attendees != null && !attendees.isEmpty()) {
			for (Property attendee : attendees) {
				URI uri = URI.create(attendee.getValue());
				if ("mailto".equals(uri.getScheme())) {
					String email = uri.getSchemeSpecificPart();

					Role role = attendee.getParameter(Role.CHAIR.getName());
					if (role != null && role.getValue().equals(Role.CHAIR.getValue())
							&& email.equals(organizerEmail)) {
						continue;
					}

					User u = userDao.getByEmail(email);
					if (u == null) {
						u = userDao.getContact(email, a.getOwner());
					}
					attList.add(createMeetingMember(a, u));

				}
			}
		}

		a.setMeetingMembers(attList.isEmpty() ? null : new ArrayList<>(attList));

		return a;
	}

	private static MeetingMember createMeetingMember(Appointment a, User u) {
		MeetingMember mm = new MeetingMember();
		mm.setUser(u);
		mm.setDeleted(false);
		mm.setInserted(a.getInserted());
		mm.setUpdated(a.getUpdated());
		mm.setAppointment(a);
		return mm;
	}

	private static Room createDefaultRoom() {
		Room r = new Room();
		r.setAppointment(true);
		if (r.getType() == null) {
			r.setType(Room.Type.conference);
		}
		return r;
	}

	/**
	 * Parses the VTimezone Component of the given Calendar. If no, VTimezone component is found the
	 * User Timezone is used
	 *
	 * @param calendar Calendar to parse
	 * @param owner    Owner of the Calendar
	 * @return Parsed TimeZone
	 */
	public TimeZone parseTimeZone(Calendar calendar, User owner) {
		if (calendar != null) {
			Component timezone = calendar.getComponent(Component.VTIMEZONE);
			if (timezone != null) {
				Property tzid = timezone.getProperty(Property.TZID);
				if (tzid != null) {
					return getTimeZone(tzid.getValue());
				}
			}
		}
		return getTimeZone(owner);
	}

	/**
	 * Convenience function to parse date from {@link net.fortuna.ical4j.model.Property} to
	 * {@link Date}
	 *
	 * @param dt       DATE-TIME Property from which we parse.
	 * @param timeZone Timezone of the Date.
	 * @return {@link java.util.Date} representation of the iCalendar value.
	 */
	public Date parseDate(Property dt, TimeZone timeZone) {
		if (dt == null || Strings.isEmpty(dt.getValue())) {
			return null;
		}

		String[] acceptedFormats = {"yyyyMMdd'T'HHmmss", "yyyyMMdd'T'HHmmss'Z'", "yyyyMMdd"};
		Parameter tzid = dt.getParameter(Parameter.TZID);
		if (tzid == null) {
			return parseDate(dt.getValue(), acceptedFormats, timeZone);
		} else {
			return parseDate(dt.getValue(), acceptedFormats, getTimeZone(tzid.getValue()));
		}
	}

	/**
	 * Adapted from DateUtils to support Timezones, and parse ical dates into {@link java.util.Date}.
	 * Note: Replace FastDateFormat to java.time, when shifting to Java 8 or higher.
	 *
	 * @param str      Date representation in String.
	 * @param patterns Patterns to parse the date against
	 * @param _timeZone Timezone of the Date.
	 * @return <code>java.util.Date</code> representation of string or
	 * <code>null</code> if the Date could not be parsed.
	 */
	public Date parseDate(String str, String[] patterns, TimeZone _timeZone) {
		FastDateFormat parser;
		Locale locale = WebSession.get().getLocale();

		TimeZone timeZone = str.endsWith("Z") ? TimeZone.getTimeZone("UTC") : _timeZone;

		ParsePosition pos = new ParsePosition(0);
		for (String pattern : patterns) {
			parser = FastDateFormat.getInstance(pattern, timeZone, locale);
			pos.setIndex(0);
			Date date = parser.parse(str, pos);
			if (date != null && pos.getIndex() == str.length()) {
				return date;
			}
		}
		log.error("Unable to parse the date: " + str + " at " + -1);
		return null;
	}

	/**
	 * Adds a specified amount of time to a Date.
	 *
	 * @param date   Date to which time is added
	 * @param field  Date Field to which the Amount is added
	 * @param amount Amount to be Added
	 * @return New Date
	 */
	public Date addTimetoDate(Date date, int field, int amount) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		c.add(field, amount);
		return c.getTime();
	}

	/**
	 * Methods to parse Appointment to iCalendar according RFC 2445
	 *
	 * @param appointment to be converted to iCalendar
	 * @return iCalendar representation of the Appointment
	 */
	public Calendar parseAppointmenttoCalendar(Appointment appointment) {
		String tzid = parseTimeZone(null, appointment.getOwner()).getID();

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

		net.fortuna.ical4j.model.TimeZone timeZone = registry.getTimeZone(tzid);
		if (timeZone == null) {
			throw new NoSuchElementException("Unable to get time zone by id provided: " + tzid);
		}

		Calendar icsCalendar = new Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Events Calendar//Apache Openmeetings//EN"));
		icsCalendar.getProperties().add(Version.VERSION_2_0);
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		icsCalendar.getComponents().add(timeZone.getVTimeZone());

		DateTime start = new DateTime(appointment.getStart()), end = new DateTime(appointment.getEnd());

		VEvent meeting = new VEvent(start, end, appointment.getTitle());
		meeting = addVEventpropsfromAppointment(appointment, meeting);
		icsCalendar.getComponents().add(meeting);

		return icsCalendar;
	}

	/**
	 * Adds the Appointment Properties to the given VEvent
	 *
	 * @param appointment Appointment whose properties are taken
	 * @param meeting     VEvent of the Appointment
	 * @return Updated VEvent
	 */
	private static VEvent addVEventpropsfromAppointment(Appointment appointment, VEvent meeting) {

		if (appointment.getLocation() != null) {
			meeting.getProperties().add(new Location(appointment.getLocation()));
		}

		meeting.getProperties().add(new Description(appointment.getDescription()));
		meeting.getProperties().add(new Sequence(0));
		meeting.getProperties().add(Transp.OPAQUE);

		String uid = appointment.getIcalId();
		Uid ui;
		if (uid == null || uid.length() < 1) {
			UUID uuid = UUID.randomUUID();
			appointment.setIcalId(uuid.toString());
			ui = new Uid(uuid.toString());
		} else {
			ui = new Uid(uid);
		}

		meeting.getProperties().add(ui);

		if (appointment.getMeetingMembers() != null) {
			for (MeetingMember meetingMember : appointment.getMeetingMembers()) {
				Attendee attendee = new Attendee(URI.create("mailto:" +
						meetingMember.getUser().getAddress().getEmail()));
				attendee.getParameters().add(Role.REQ_PARTICIPANT);
				attendee.getParameters().add(new Cn(meetingMember.getUser().getLogin()));
				meeting.getProperties().add(attendee);
			}
		}
		URI orgUri = URI.create("mailto:" + appointment.getOwner().getAddress().getEmail());
		Attendee orgAtt = new Attendee(orgUri);
		orgAtt.getParameters().add(Role.CHAIR);
		Cn orgCn = new Cn(appointment.getOwner().getLogin());
		orgAtt.getParameters().add(orgCn);
		meeting.getProperties().add(orgAtt);

		Organizer organizer = new Organizer(orgUri);
		organizer.getParameters().add(orgCn);
		meeting.getProperties().add(organizer);

		return meeting;
	}

	/**
	 * Parses a List of Appointments into a VCALENDAR component.
	 *
	 * @param appointments List of Appointments for the Calendar
	 * @param ownerId      Owner of the Appointments
	 * @return VCALENDAR representation of the Appointments
	 */
	public Calendar parseAppointmentstoCalendar(List<Appointment> appointments, Long ownerId) {
		String tzid = parseTimeZone(null, userDao.get(ownerId)).getID();

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

		net.fortuna.ical4j.model.TimeZone timeZone = registry.getTimeZone(tzid);
		if (timeZone == null) {
			throw new NoSuchElementException("Unable to get time zone by id provided: " + tzid);
		}

		Calendar icsCalendar = new Calendar();
		icsCalendar.getProperties().add(new ProdId(PROD_ID));
		icsCalendar.getProperties().add(Version.VERSION_2_0);
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		icsCalendar.getComponents().add(timeZone.getVTimeZone());

		for (Appointment appointment : appointments) {
			DateTime start = new DateTime(appointment.getStart()), end = new DateTime(appointment.getEnd());

			VEvent meeting = new VEvent(start, end, appointment.getTitle());
			meeting = addVEventpropsfromAppointment(appointment, meeting);
			icsCalendar.getComponents().add(meeting);
		}
		return icsCalendar;
	}
}
