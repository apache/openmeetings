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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.CalendarHelper.getZoneDateTime;
import static org.apache.openmeetings.util.mail.IcalHandler.TZ_REGISTRY;
import static org.apache.openmeetings.util.mail.MailUtil.MAILTO;
import static org.apache.openmeetings.util.mail.MailUtil.SCHEME_MAILTO;

import java.net.URI;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableTransp;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.transform.recurrence.Frequency;

/**
 * Class which provides iCalendar Utilities.
 * This class's functions could be made static, as they are not instantiated anyway.
 */
@org.springframework.stereotype.Component
public class IcalUtils {
	private static final Logger log = LoggerFactory.getLogger(IcalUtils.class);
	private static final List<String> acceptedFormats = List.of("yyyyMMdd'T'HHmmss", "yyyyMMdd'T'HHmmss'Z'", "yyyyMMdd");
	public static final String PROD_ID = "-//Events Calendar//Apache Openmeetings//EN";

	@Inject
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
		a.setReminder(Appointment.Reminder.NONE);

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
		List<CalendarComponent> events = calendar.getComponents(Component.VEVENT);
		User owner = userDao.get(ownerId);

		for (CalendarComponent event : events) {
			Appointment a = new Appointment();
			a.setOwner(owner);
			a.setDeleted(false);
			a.setRoom(createDefaultRoom());
			a.setReminder(Appointment.Reminder.NONE);
			appointments.add(addVEventPropertiestoAppointment(a, event));
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
		calendar.getComponent(Component.VEVENT).ifPresent(event -> {
			a.setEtag(etag);
			addVEventPropertiestoAppointment(a, event);
		});
		return a;
	}

	private Date getDate(CalendarComponent event, String prop) {
		return getDate(event.getProperty(prop).orElse(null));
	}

	@SuppressWarnings("unchecked")
	private Date getDate(Property prop) {
		return prop == null ? null : Date.from(Instant.from(((DateProperty<? extends Temporal>)prop).getDate()));
	}

	/**
	 * Add properties from the Given VEvent Component to the Appointment
	 *
	 * @param a     Appointment to which the properties are to be added
	 * @param event VEvent to parse properties from.
	 * @return Updated Appointment
	 */
	private Appointment addVEventPropertiestoAppointment(Appointment a, CalendarComponent event) {
		event.getProperty(Property.UID).ifPresent(uid -> a.setIcalId(uid.getValue()));

		Date d = getDate(event, Property.DTSTART);
		a.setStart(getDate(event, Property.DTSTART));
		event.getProperty(Property.DTEND).ifPresentOrElse(dtend -> a.setEnd(getDate(dtend))
				, () -> a.setEnd(addTimetoDate(d, java.util.Calendar.HOUR_OF_DAY, 1)));

		event.getProperty(Property.DTSTAMP).ifPresent(dtstamp -> a.setInserted(getDate(dtstamp)));
		event.getProperty(Property.LAST_MODIFIED).ifPresent(lastmod -> a.setUpdated(getDate(lastmod)));
		event.getProperty(Property.DESCRIPTION).ifPresent(description -> a.setDescription(description.getValue()));
		event.getProperty(Property.SUMMARY).ifPresent(summary -> a.setTitle(summary.getValue()));
		event.getProperty(Property.LOCATION).ifPresent(location -> a.setLocation(location.getValue()));

		event.getProperty(Property.RRULE).ifPresent(recur ->
			recur.getParameter("FREQ").ifPresent(freq -> {
				if (freq.getValue().equals(Frequency.DAILY.name())) {
					a.setIsDaily(true);
				} else if (freq.getValue().equals(Frequency.WEEKLY.name())) {
					a.setIsWeekly(true);
				} else if (freq.getValue().equals(Frequency.MONTHLY.name())) {
					a.setIsMonthly(true);
				} else if (freq.getValue().equals(Frequency.YEARLY.name())) {
					a.setIsYearly(true);
				}
			})
		);

		Set<MeetingMember> attList = a.getMeetingMembers() == null ? new HashSet<>()
				: new HashSet<>(a.getMeetingMembers());
		AtomicReference<String> organizerEmail = new AtomicReference<>();

		//Note this value can be repeated in attendees as well.
		event.getProperty(Property.ORGANIZER).ifPresent(organizer -> {
			URI uri = URI.create(organizer.getValue());

			//If the value of the organizer is an email
			if (SCHEME_MAILTO.equals(uri.getScheme())) {
				String email = uri.getSchemeSpecificPart();

				organizerEmail.set(email);
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
		});

		event.getProperties(Property.ATTENDEE).forEach(attendee -> {
			URI uri = URI.create(attendee.getValue());
			if (SCHEME_MAILTO.equals(uri.getScheme())) {
				String email = uri.getSchemeSpecificPart();

				Optional<Role> role = attendee.getParameter(Role.CHAIR.getName());
				if (role.isPresent() && role.get().getValue().equals(Role.CHAIR.getValue()) && email.equals(organizerEmail.get())) {
					return;
				}

				User u = userDao.getByEmail(email);
				if (u == null) {
					u = userDao.getContact(email, a.getOwner());
				}
				attList.add(createMeetingMember(a, u));

			}
		});

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
			r.setType(Room.Type.CONFERENCE);
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
		return Optional.ofNullable(calendar)
				.map(cal -> cal.getComponent(Component.VTIMEZONE))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(timezone -> timezone.getProperty(Property.TZID))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(tzid -> getTimeZone(tzid.getValue()))
				.orElse(getTimeZone(owner));
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

		return dt.getParameter(Parameter.TZID)
				.map(tzid -> parseDate(dt.getValue(), getTimeZone(tzid.getValue())))
				.orElse(parseDate(dt.getValue(), timeZone));
	}

	/**
	 * Adapted from DateUtils to support Timezones, and parse ical dates into {@link java.util.Date}.
	 * Note: Replace FastDateFormat to java.time, when shifting to Java 8 or higher.
	 *
	 * @param str      Date representation in String.
	 * @param inTimeZone Timezone of the Date.
	 * @return <code>java.util.Date</code> representation of string or
	 * <code>null</code> if the Date could not be parsed.
	 */
	public Date parseDate(String str, TimeZone inTimeZone) {
		FastDateFormat parser;
		Locale locale = WebSession.get().getLocale();

		TimeZone timeZone = str.endsWith("Z") ? TimeZone.getTimeZone("UTC") : inTimeZone;

		ParsePosition pos = new ParsePosition(0);
		for (String pattern : acceptedFormats) {
			parser = FastDateFormat.getInstance(pattern, timeZone, locale);
			pos.setIndex(0);
			Date date = parser.parse(str, pos);
			if (date != null && pos.getIndex() == str.length()) {
				return date;
			}
		}
		log.error("Unable to parse the date: {} at {}", str, -1);
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

	private net.fortuna.ical4j.model.TimeZone getTimazone(String tzid) {
		net.fortuna.ical4j.model.TimeZone timeZone = TZ_REGISTRY.getTimeZone(tzid);
		if (timeZone == null) {
			throw new NoSuchElementException("Unable to get time zone by id provided: " + tzid);
		}
		return timeZone;
	}

	private Calendar getCalendar(net.fortuna.ical4j.model.TimeZone timeZone, List<CalendarComponent> events) {
		List<CalendarComponent> comps = new ArrayList<>(events);
		comps.add(0, timeZone.getVTimeZone());
		return new Calendar(
				new PropertyList(List.of(new ProdId(PROD_ID), ImmutableVersion.VERSION_2_0, ImmutableCalScale.GREGORIAN))
				, new ComponentList<>(comps));
	}

	private VEvent parseAppointment(Appointment appointment, net.fortuna.ical4j.model.TimeZone timeZone) {
		ZonedDateTime start = getZoneDateTime(appointment.getStart(), timeZone.getID());
		ZonedDateTime end = getZoneDateTime(appointment.getEnd(), timeZone.getID());

		VEvent meeting = new VEvent(start, end, appointment.getTitle());
		List<Property> mProperties = new ArrayList<>(meeting.getProperties());
		if (appointment.getLocation() != null) {
			mProperties.add(new Location(appointment.getLocation()));
		}

		mProperties.add(new Description(appointment.getDescription()));
		mProperties.add(new Sequence(0));
		mProperties.add(ImmutableTransp.OPAQUE);

		String uid = appointment.getIcalId();
		Uid ui;
		if (uid == null || uid.length() < 1) {
			UUID uuid = randomUUID();
			appointment.setIcalId(uuid.toString());
			ui = new Uid(uuid.toString());
		} else {
			ui = new Uid(uid);
		}

		mProperties.add(ui);

		if (appointment.getMeetingMembers() != null) {
			for (MeetingMember meetingMember : appointment.getMeetingMembers()) {
				mProperties.add(new Attendee(
						new ParameterList(List.of(Role.REQ_PARTICIPANT, new Cn(meetingMember.getUser().getLogin())))
						, URI.create(MAILTO + meetingMember.getUser().getAddress().getEmail())));
			}
		}
		URI orgUri = URI.create(MAILTO + appointment.getOwner().getAddress().getEmail());
		Cn orgCn = new Cn(appointment.getOwner().getLogin());
		mProperties.add(new Attendee(new ParameterList(List.of(Role.CHAIR, orgCn)), orgUri));

		mProperties.add(new Organizer(new ParameterList(List.of(orgCn)), orgUri));

		meeting.setPropertyList(new PropertyList(mProperties));
		return meeting;
	}

	/**
	 * Methods to parse Appointment to iCalendar according RFC 2445
	 *
	 * @param appointment to be converted to iCalendar
	 * @return iCalendar representation of the Appointment
	 */
	public Calendar parseAppointmenttoCalendar(Appointment appointment) {
		net.fortuna.ical4j.model.TimeZone timeZone = getTimazone(parseTimeZone(null, appointment.getOwner()).getID());

		return getCalendar(timeZone, List.of(parseAppointment(appointment, timeZone)));
	}

	/**
	 * Parses a List of Appointments into a VCALENDAR component.
	 *
	 * @param appointments List of Appointments for the Calendar
	 * @param ownerId      Owner of the Appointments
	 * @return VCALENDAR representation of the Appointments
	 */
	public Calendar parseAppointmentstoCalendar(List<Appointment> appointments, Long ownerId) {
		net.fortuna.ical4j.model.TimeZone timeZone = getTimazone(parseTimeZone(null, userDao.get(ownerId)).getID());

		return getCalendar(timeZone, appointments.stream()
				.map(appointment -> (CalendarComponent)parseAppointment(appointment, timeZone))
				.toList());
	}
}
