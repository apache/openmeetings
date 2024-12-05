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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.mail.MailUtil.MAILTO;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
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
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableMethod;
import net.fortuna.ical4j.model.property.immutable.ImmutableStatus;
import net.fortuna.ical4j.model.property.immutable.ImmutableTransp;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.validate.ValidationException;

/**
 *
 * @author o.becherer, seba.wagner
 *
 */
public class IcalHandler {
	private static final Logger log = LoggerFactory.getLogger(IcalHandler.class);
	public static final TimeZoneRegistry TZ_REGISTRY = TimeZoneRegistryFactory.getInstance().createRegistry();

	/** ICal instance */
	private final Calendar icsCalendar;
	private TimeZone timeZone;
	private VEvent meeting;
	private List<Property> meetingProperties = new ArrayList<>();
	private Method method;

	/** Creation of a new Event */
	public static final Method ICAL_METHOD_REQUEST = ImmutableMethod.REQUEST;
	public static final Method ICAL_METHOD_CANCEL = ImmutableMethod.CANCEL;
	public static final Method ICAL_METHOD_REFRESH = ImmutableMethod.REFRESH;

	/**
	 * Constructor
	 *
	 * @param method
	 *            (@see IcalHandler) constants
	 */
	public IcalHandler(Method method) {
		this.method = method;
		log.debug("Icalhandler method type : {}", method);

		icsCalendar = new Calendar(new PropertyList(List.of(
				new ProdId("-//Apache Openmeetings//OM Calendar//EN")
				, ImmutableVersion.VERSION_2_0
				, ImmutableCalScale.GREGORIAN
				, method
				)), new ComponentList<>());
	}

	public IcalHandler createVEvent(ZonedDateTime start, ZonedDateTime end, String name) {
		timeZone = TZ_REGISTRY.getTimeZone(start.getZone().getId());
		if (timeZone == null) {
			throw new NoSuchElementException("Unable to get time zone by id provided: " + start.getZone());
		}

		meeting = new VEvent(start, end, name);
		meetingProperties.addAll(meeting.getProperties());
		meetingProperties.addAll(List.of(ImmutableTransp.OPAQUE, ImmutableStatus.VEVENT_CONFIRMED));
		return this;
	}

	public IcalHandler setCreated(ZonedDateTime date) {
		meetingProperties.add(new Created(date.toInstant()));
		return this;
	}

	public IcalHandler setModified(ZonedDateTime date) {
		meetingProperties.add(new LastModified((date == null ? ZonedDateTime.now() : date).toInstant()));
		return this;
	}

	public IcalHandler setDescription(String description) {
		meetingProperties.add(new Description(description));
		return this;
	}

	public IcalHandler setLocation(String location) {
		meetingProperties.add(new Location(location));
		return this;
	}

	public IcalHandler setSequence(int seq) {
		meetingProperties.add(new Sequence(seq));
		return this;
	}

	public IcalHandler setUid(String uid) {
		meetingProperties.add(new Uid(uid));
		return this;
	}

	private static URI getMailto(String email) {
		return URI.create(MAILTO + email);
	}

	public IcalHandler addOrganizer(String email, String name) {
		meetingProperties.add(new Organizer(new ParameterList(List.of(new Cn(name))), getMailto(email)));
		return this;
	}

	public IcalHandler addAttendee(String email, String display, boolean chair) {
		List<Parameter> params = new ArrayList<>(List.of(
				CuType.INDIVIDUAL
				, chair ? Role.CHAIR : Role.REQ_PARTICIPANT
				, new XParameter("X-NUM-GUESTS", "0")
				, new Cn(display)));
		if (ImmutableMethod.CANCEL.equals(method)) {
			params.add(PartStat.DECLINED);
		} else {
			params.add(chair ? PartStat.ACCEPTED : PartStat.NEEDS_ACTION);
			params.add(Rsvp.TRUE);
		}
		meetingProperties.add(new Attendee(new ParameterList(params), getMailto(email)));
		return this;
	}

	public IcalHandler build() {
		meeting.setPropertyList(new PropertyList(meetingProperties));
		icsCalendar.setComponentList(new ComponentList<>(List.of(timeZone.getVTimeZone(), meeting)));
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
		String filerPath = inFilerPath.endsWith(".ics") ? inFilerPath : inFilerPath + ".ics";

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
	public byte[] toByteArray() throws IOException {
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
		if (icsCalendar == null) {
			return "";
		}
		try {
			return new String(toByteArray(), UTF_8);
		} catch (IOException e) {
			log.error("Unexpected error", e);
		}
		return "";
	}
}
