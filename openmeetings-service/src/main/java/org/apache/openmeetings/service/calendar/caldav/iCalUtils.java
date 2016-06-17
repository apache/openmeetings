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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which provides iCalendar Utilities.
 * This class's functions could be made static, as they are not instantiated anyway.
 */
public class iCalUtils {

    private static final Logger log = Red5LoggerFactory.getLogger(iCalUtils.class, webAppRootKey);

    @Autowired
    private TimezoneUtil timezoneUtil;

    /**
     * Parses the Calendar from the CalDAV server, to a new Appointment.
     * @param calendar
     * @return
     */
    public Appointment parseCalendartoAppointment(Calendar calendar, String href, String etag,
                                                        OmCalendar omCalendar){

        //Note: By RFC 4791 only one event can be stored in one href.

        Appointment a = new Appointment();
        a.setId(null);
        a.setDeleted(false);
        a.setHref(href);
        a.setCalendar(omCalendar);
        a.setOwner(omCalendar.getOwner());


        return this.parseCalendartoAppointment(a, calendar, etag);
    }

    /**
     * Updating Appointments which already exist, by parsing the Calendar. And updating etag.
     * Doesn't work with Recurrences.
     * Note: Hasn't been tested to acknowledge DST, timezones should acknowledge this.
     * @param a
     * @param calendar
     * @param etag
     * @return
     */
    public Appointment parseCalendartoAppointment(Appointment a, Calendar calendar,
                                                  String etag){
        Component event = calendar.getComponent(Component.VEVENT);
        TimeZone tz = parseTimeZone(calendar, a.getOwner());

        Property dtstart = event.getProperty(Property.DTSTART),
                dtend = event.getProperty(Property.DTEND),
                uid = event.getProperty(Property.UID),
                dtstamp = event.getProperty(Property.DTSTAMP),
                description = event.getProperty(Property.DESCRIPTION),
                summary = event.getProperty(Property.SUMMARY),
                location = event .getProperty(Property.LOCATION),
                lastmod = event.getProperty(Property.LAST_MODIFIED),
                organizer = event.getProperty(Property.ORGANIZER);
        PropertyList attendees = event.getProperties(Property.ATTENDEE);

        a.setEtag(etag);

        if(uid != null)
            a.setIcalId(uid.getValue());
        try {
            Date d = parseDate(dtstart, tz);
            a.setStart(d);
            if(dtend == null)
                a.setEnd(addDaytoDate(d));
            else
                a.setEnd(parseDate(dtend, tz));

            a.setInserted(parseDate(dtstamp, tz));
            if(lastmod != null)
                a.setUpdated(parseDate(lastmod, tz));

        } catch (ParseException e) {
            log.error("Error parsing DATE-TIME components for ical.");
        }

        if(description != null)
            a.setDescription(description.getValue());

        if(summary != null)
            a.setTitle(summary.getValue());

        if(location != null)
            a.setLocation(location.getValue());

        return a;
    }

    public TimeZone parseTimeZone(Calendar calendar, User owner){
        if(calendar != null) {
            Component timezone = calendar.getComponent(Component.VTIMEZONE);
            if (timezone != null) {
                Property tzid = timezone.getProperty(Property.TZID);
                if (tzid != null)
                    return timezoneUtil.getTimeZone(tzid.getValue());
            }
        }
        return timezoneUtil.getTimeZone(owner);
    }

    /**
     * Convenience function to parse date from {@link net.fortuna.ical4j.model.Property} to
     * {@link Date}
     * @param dt
     * @param timeZone
     * @return
     * @throws ParseException
     */
    public Date parseDate(Property dt, TimeZone timeZone) throws ParseException {
        if(dt == null || dt.getValue().equals(""))
            return null;

        String[] acceptedFormats = { "yyyyMMdd'T'HHmmss", "yyyyMMdd'T'HHmmss'Z'", "yyyyMMdd"};
        Parameter tzid = dt.getParameter(Parameter.TZID);
        if(tzid == null)
            return parseDate(dt.getValue(), acceptedFormats, timeZone);
        else
            return parseDate(dt.getValue(), acceptedFormats, timezoneUtil.getTimeZone(tzid.getValue()));
    }

    /**
     * Adapted from DateUtils to support Timezones, and parse ical dates into {@link java.util.Date}.
     * Note: Replace FastDateFormat to java.time, when shifting to Java 8 or higher.
     * @param str
     * @param patterns
     * @param timeZone
     * @return Date representation of string.
     * @throws ParseException
     */
    public Date parseDate(String str, String[] patterns, TimeZone timeZone) throws ParseException {
        FastDateFormat parser = null;

        if(str.contains("Z"))
            timeZone = TimeZone.getTimeZone("UTC");

        ParsePosition pos = new ParsePosition(0);
        for(String pattern: patterns){
            parser = FastDateFormat.getInstance(pattern, timeZone);
            pos.setIndex(0);
            Date date = parser.parse(str, pos);
            if (date != null && pos.getIndex() == str.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public Date addDaytoDate(Date date){
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        c.add(java.util.Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    //Methods to parse Appointment to Calendar
    public Calendar parseAppointmenttoCalendar(Appointment appointment) throws Exception {
        String tzid = parseTimeZone(null, appointment.getOwner()).getID();

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

        net.fortuna.ical4j.model.TimeZone timeZone = registry.getTimeZone(tzid);
        if (timeZone == null) {
            throw new Exception("Unable to get time zone by id provided: " + tzid);
        }

        Calendar icsCalendar = new Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//Apache Openmeetings//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        DateTime start = new DateTime(appointment.getStart()), end = new DateTime(appointment.getEnd());
        start.setTimeZone(timeZone);
        end.setTimeZone(timeZone);

        VEvent meeting = new VEvent(start, end, appointment.getTitle());

        if(appointment.getLocation() != null)
            meeting.getProperties().add(new Location(appointment.getLocation()));

        meeting.getProperties().add(new Description(appointment.getDescription()));
        meeting.getProperties().add(new Sequence(0));
        meeting.getProperties().add(Transp.OPAQUE);

        String uid = appointment.getIcalId();
        Uid ui = null;
        if (uid == null || uid.length() < 1) {
            UUID uuid = UUID.randomUUID();
            appointment.setIcalId(uuid.toString());
            ui = new Uid(uuid.toString());
        } else {
            ui = new Uid(uid);
        }

        meeting.getProperties().add(ui);

        if(appointment.getMeetingMembers() != null) {
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

        icsCalendar.getComponents().add(timeZone.getVTimeZone());
        icsCalendar.getComponents().add(meeting);
        return icsCalendar;
    }
}