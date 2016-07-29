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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.text.ParsePosition;
import java.util.*;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which provides iCalendar Utilities.
 * This class's functions could be made static, as they are not instantiated anyway.
 */
public class iCalUtils {

    private static final Logger log = Red5LoggerFactory.getLogger(iCalUtils.class, webAppRootKey);

    @Autowired
    private TimezoneUtil timezoneUtil;
    @Autowired
    private UserDao userDao;

    /**
     * Parses the Calendar from the CalDAV server, to a new Appointment.
     * @param calendar iCalendar Representation.
     * @param href Location of the Calendar on the server
     * @param etag ETag of the calendar.
     * @param omCalendar The Parent OmCalendar, to which the Appointment belongs.
     * @return Appointment after parsing.
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
        a.setRoom(createDefaultRoom());
        a.setReminder(Appointment.Reminder.none);


        return this.parseCalendartoAppointment(a, calendar, etag);
    }

    /**
     * Updating Appointments which already exist, by parsing the Calendar. And updating etag.
     * Doesn't work with complex Recurrences.
     * Note: Hasn't been tested to acknowledge DST, timezones should acknowledge this.
     * @param a Appointment to be updated.
     * @param calendar iCalendar Representation.
     * @param etag The ETag of the calendar.
     * @return Updated Appointment.
     */
    public Appointment parseCalendartoAppointment(Appointment a, Calendar calendar,
                                                  String etag){
        if(calendar == null) return a;
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
                organizer = event.getProperty(Property.ORGANIZER),
                recur = event.getProperty(Property.RRULE);
        PropertyList attendees = event.getProperties(Property.ATTENDEE);

        a.setEtag(etag);

        if(uid != null)
            a.setIcalId(uid.getValue());

        Date d = parseDate(dtstart, tz);
        a.setStart(d);
        if(dtend == null)
            a.setEnd(addTimetoDate(d, java.util.Calendar.HOUR_OF_DAY, 1));
        else
            a.setEnd(parseDate(dtend, tz));

        a.setInserted(parseDate(dtstamp, tz));

        if(lastmod != null)
            a.setUpdated(parseDate(lastmod, tz));

        if(description != null)
            a.setDescription(description.getValue());

        if(summary != null)
            a.setTitle(summary.getValue());

        if(location != null)
            a.setLocation(location.getValue());

        if(recur != null){
            Parameter freq = recur.getParameter("FREQ");
            if(freq != null){
                if(freq.getValue().equals(Recur.DAILY))
                    a.setIsDaily(true);
                else if(freq.getValue().equals(Recur.WEEKLY))
                    a.setIsWeekly(true);
                else if(freq.getValue().equals(Recur.MONTHLY))
                    a.setIsMonthly(true);
                else if(freq.getValue().equals(Recur.YEARLY))
                    a.setIsYearly(true);
            }
        }

        List<MeetingMember> attList = a.getMeetingMembers() == null ?
                new ArrayList<MeetingMember>() : a.getMeetingMembers();

        //Note this value can be repeated in attendees as well.
        if(organizer != null){
            URI uri = URI.create(organizer.getValue());

            //If the value of the organizer is an email
            if(uri.getScheme().equals("mailto")) {
                String email = uri.getSchemeSpecificPart();
                //Contact or exist and owner
                User org = userDao.getByEmail(email);
                if (org == null) {
                    org = userDao.getContact(email, a.getOwner());
                    attList.add(createMeetingMember(a, org));
                } else if (org.getId() != a.getOwner().getId()) {
                    attList.add(createMeetingMember(a, org));
                }
            }
        }

        if(attendees != null && !attendees.isEmpty()){
            for(Property attendee: attendees){
                URI uri = URI.create(attendee.getValue());
                if(uri.getScheme().equals("mailto")){
                    String email = uri.getSchemeSpecificPart();
                    User u = userDao.getByEmail(email);
                    if(u == null)
                        u = userDao.getContact(email, a.getOwner());
                    attList.add(createMeetingMember(a, u));
                }
            }
        }

        a.setMeetingMembers(attList.isEmpty() ? null : attList);

        return a;
    }

    private MeetingMember createMeetingMember(Appointment a, User u){
        MeetingMember mm = new MeetingMember();
        mm.setUser(u);
        mm.setDeleted(false);
        mm.setInserted(a.getInserted());
        mm.setUpdated(a.getUpdated());
        mm.setAppointment(a);
        return mm;
    }

    private Room createDefaultRoom() {
        Room r = new Room();
        r.setAppointment(true);
        if (r.getType() == null) {
            r.setType(Room.Type.conference);
        }
        return r;
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
     * @param dt DATE-TIME Property from which we parse.
     * @param timeZone Timezone of the Date.
     * @return {@link java.util.Date} representation of the iCalendar value.
     */
    public Date parseDate(Property dt, TimeZone timeZone) {
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
     * @param str Date representation in String.
     * @param patterns Patterns to parse the date against
     * @param timeZone Timezone of the Date.
     * @return <code>java.util.Date</code> representation of string or
     * <code>null</code> if the Date could not be parsed.
     */
    public Date parseDate(String str, String[] patterns, TimeZone timeZone) {
        FastDateFormat parser = null;

        if(str.endsWith("Z"))
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
        log.error("Unable to parse the date: " + str + " at " + -1);
        return null;
    }

    public Date addTimetoDate(Date date, int field, int amount){
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        c.add(field, amount);
        return c.getTime();
    }

    /**
     * Methods to parse Appointment to iCalendar according RFC 2445
     * @param appointment to be converted to iCalendar
     * @return iCalendar representation of the Appointment
     * @throws Exception
     */
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

/*        RRule rRule = null;
        if(appointment.getIsDaily() != null)
            rRule = new RRule("FREQ=DAILY");
        else if(appointment.getIsWeekly() != null)
            rRule = new RRule("FREQ=WEEKLY");
        else if (appointment.getIsMonthly() != null)
            rRule = new RRule("FREQ=MONTHLY");
        else if(appointment.getIsYearly() != null)
            rRule = new RRule("FREQ=YEARLY");

        if(rRule != null)
            meeting.getProperties().add(rRule);*/

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