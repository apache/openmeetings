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
package org.apache.openmeetings.util.mail;

import static org.apache.openmeetings.util.mail.MailUtil.MAILTO;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

/**
 *
 * @author o.becherer, seba.wagner
 *
 */
public class IcalHandler {
	private static final Logger log = LoggerFactory.getLogger(IcalHandler.class);
	static {
		System.setProperty("net.fortuna.ical4j.timezone.update.enabled", "false");
		System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
	}

	/** ICal instance */
	private final Calendar icsCalendar;
	private TimeZone timeZone;
	private VEvent meeting;
	private Method method;

	/** Creation of a new Event */
	public static final Method ICAL_METHOD_REQUEST = Method.REQUEST;
	public static final Method ICAL_METHOD_CANCEL = Method.CANCEL;
	public static final Method ICAL_METHOD_REFRESH = Method.REFRESH;

	/**
	 * Constructor
	 *
	 * @param method
	 *            (@see IcalHandler) constants
	 */
	public IcalHandler(Method method) {
		this.method = method;
		log.debug("Icalhandler method type : {}", method);

		icsCalendar = new Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Apache Openmeetings//OM Calendar//EN"));
		icsCalendar.getProperties().add(Version.VERSION_2_0);
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		icsCalendar.getProperties().add(method);
	}

	public IcalHandler createVEvent(String tz, Date startDate, Date endDate, String name) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

		timeZone = registry.getTimeZone(tz);
		if (timeZone == null) {
			throw new NoSuchElementException("Unable to get time zone by id provided: " + tz);
		}

		DateTime start = new DateTime(startDate);
		start.setTimeZone(timeZone);
		DateTime end = new DateTime(endDate);
		end.setTimeZone(timeZone);

		meeting = new VEvent(start, end, name);
		meeting.getProperties().add(Transp.OPAQUE);
		meeting.getProperties().add(Status.VEVENT_CONFIRMED);
		return this;
	}

	public IcalHandler setCreated(Date date) {
		meeting.getProperties().add(new Created(new DateTime(date)));
		return this;
	}

	public IcalHandler setModified(Date date) {
		meeting.getProperties().add(new LastModified(new DateTime(date == null ? new Date() : date)));
		return this;
	}

	public IcalHandler setDescription(String description) {
		meeting.getProperties().add(new Description(description));
		return this;
	}

	public IcalHandler setLocation(String location) {
		meeting.getProperties().add(new Location(location));
		return this;
	}

	public IcalHandler setSequence(int seq) {
		meeting.getProperties().add(new Sequence(seq));
		return this;
	}

	public IcalHandler setUid(String uid) {
		meeting.getProperties().add(new Uid(uid));
		return this;
	}

	private static URI getMailto(String email) {
		return URI.create(MAILTO + email);
	}

	public IcalHandler addOrganizer(String email, String name) {
		Organizer orger = new Organizer(getMailto(email));
		orger.getParameters().add(new Cn(name));
		meeting.getProperties().add(orger);
		return this;
	}

	public IcalHandler addAttendee(String email, String display, boolean chair) {
		Attendee uno = new Attendee(URI.create(MAILTO + email));
		uno.getParameters().add(CuType.INDIVIDUAL);
		uno.getParameters().add(chair ? Role.CHAIR : Role.REQ_PARTICIPANT);
		if (Method.CANCEL == method) {
			uno.getParameters().add(PartStat.DECLINED);
		} else {
			uno.getParameters().add(chair ? PartStat.ACCEPTED : PartStat.NEEDS_ACTION);
			uno.getParameters().add(Rsvp.TRUE);
		}
		uno.getParameters().add(new XParameter("X-NUM-GUESTS", "0"));
		uno.getParameters().add(new Cn(display));
		meeting.getProperties().add(uno);
		return this;
	}

	public IcalHandler build() {
		icsCalendar.getComponents().add(timeZone.getVTimeZone());
		icsCalendar.getComponents().add(meeting);
		return this;
	}

	public Method getMethod() {
		return method;
	}

	/**
	 * Write iCal to File
	 *
	 * @param inFilerPath
	 *            - path to '*.ics' file
	 * @throws Exception
	 *             - in case of error during writing to the file
	 */
	public void toFile(String inFilerPath) throws Exception {
		String filerPath = inFilerPath.endsWith(".ics") ? inFilerPath
				: String.format("%s.ics", inFilerPath);

		try (FileOutputStream fout = new FileOutputStream(filerPath)) {
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(icsCalendar, fout);
		}
	}

	/**
	 * Get IcalBody as ByteArray
	 *
	 * @return - calendar in ICS format as byte[]
	 * @throws IOException
	 *             - in case of error during writing to byte array
	 * @throws ValidationException
	 *             - in case of invalid calendar properties
	 */
	public byte[] toByteArray() throws ValidationException, IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(icsCalendar, bout);
		return bout.toByteArray();
	}

	/**
	 * Retrieving Data as String
	 */
	@Override
	public String toString() {
		return icsCalendar == null ? null : icsCalendar.toString();
	}
}
