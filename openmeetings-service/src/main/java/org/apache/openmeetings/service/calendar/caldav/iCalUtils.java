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
import net.fortuna.ical4j.model.Calendar;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

public class iCalUtils {

    private static final Logger log = Red5LoggerFactory.getLogger(iCalUtils.class, webAppRootKey);

    @Autowired
    private TimezoneUtil timezoneUtil;

    /**
     * Parses the Calendar from the CalDAV server, to Appointment.
     * Note: Hasn't been tested to acknowledge DST.
     * @param calendar
     * @param owner
     * @return
     */
    public List<Appointment> parseCalendartoAppointment(Calendar calendar, String etag, User owner){
        List<Appointment> appointments = new ArrayList<Appointment>();
        ComponentList events = calendar.getComponents(Component.VEVENT);
        TimeZone tz = parseTimeZone(calendar, owner);
        for(Iterator i = events.iterator(); i.hasNext();) {
            Component event = (Component) i.next();
            Appointment a = new Appointment();
            a.setId(null);
            a.setDeleted(false);
            Property dtstart = event.getProperty(Property.DTSTART),
                    dtend = event.getProperty(Property.DTEND),
                    uid = event.getProperty(Property.UID),
                    dtstamp = event.getProperty(Property.DTSTAMP),
                    description = event.getProperty(Property.DESCRIPTION),
                    summary = event.getProperty(Property.SUMMARY),
                    location = event .getProperty(Property.LOCATION),
                    lastmod = event.getProperty(Property.LAST_MODIFIED),
                    organizer = event.getProperty(Property.ORGANIZER);
            PropertyList attendies = event.getProperties(Property.ATTENDEE);

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

            a.setOwner(owner);

            //Add appointment to list of appointments.
            appointments.add(a);
        }

        return appointments;
    }

    public TimeZone parseTimeZone(Calendar calendar, User user){
        Component timezone = calendar.getComponent(Component.VTIMEZONE);
        if(timezone != null) {
            Property tzid = timezone.getProperty(Property.TZID);
            if(tzid != null)
                return timezoneUtil.getTimeZone(tzid.getValue());
        }
        return timezoneUtil.getTimeZone(user);
    }

    /**
     * Convenience function to parse date from {@link net.fortuna.ical4j.model.Property}
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
     * Adapted from DateUtils to support Timezones, and parse ical dates into {@link java.util.Date}
     * @param str
     * @param patterns
     * @param timeZone
     * @return Date representation of string.
     * @throws ParseException
     */
    public Date parseDate(String str, String[] patterns, TimeZone timeZone) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(true);

        if(str.contains("Z"))
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        else
            parser.setTimeZone(timeZone);

        ParsePosition pos = new ParsePosition(0);
        for(String pattern: patterns){
            parser.applyPattern(pattern);
            pos.setIndex(0);
            Date date = parser.parse(str, pos);
            if (date != null && pos.getIndex() == str.length()) {
                return date;
            }
        }
        log.error("Unable to parse the date: " + str, -1);
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public Date addDaytoDate(Date date){
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        c.add(java.util.Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
}