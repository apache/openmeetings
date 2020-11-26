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
package org.apache.openmeetings.service.calendar.caldav.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.IcalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Class which contains all the common code for all Handlers.
 */
public abstract class AbstractCalendarHandler implements CalendarHandler {
	private static final Logger log = LoggerFactory.getLogger(AbstractCalendarHandler.class);

	protected HttpClient client;
	protected HttpClientContext context;
	protected OmCalendar calendar;
	protected String path;
	protected IcalUtils utils;

	protected AppointmentDao appointmentDao;

	protected AbstractCalendarHandler(String path, OmCalendar calendar, HttpClient client,
			HttpClientContext context, AppointmentDao appointmentDao, IcalUtils utils)
	{
		this.path = path;
		this.calendar = calendar;
		this.client = client;
		this.context = context;
		this.appointmentDao = appointmentDao;
		this.utils = utils;
	}

	/**
	 * Converts a list of appointments to a {@link HashMap} with the Href as the key
	 * @param appointments Appointments to map
	 * @return Map of Hrefs to Appointments
	 */
	static Map<String, Appointment> listToMap(List<Appointment> appointments) {
		return appointments.stream()
				.collect(Collectors.toMap(Appointment::getHref, Function.identity()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OmCalendar syncItems() {
		BaseDavRequest method = null;
		try {
			method = internalSyncItems();
		} catch (IOException | DavException e) {
			log.error("Error during the execution of calendar-multiget Report.", e);
		} catch (Exception e) {
			log.error("Severe Error during the execution of calendar-multiget Report.", e);
		} finally {
			releaseConnection(method);
		}
		return calendar;
	}

	/**
	 * Resets the Method for reusablility.
	 * @param method Method to reset.
	 */
	void releaseConnection(HttpRequestBase method) {
		if (method != null) {
			method.reset();
		}
	}

	/**
	 * Abstract method for syncing, this is implemented by subclasses to
	 * perform the actual syncing.
	 * @return Method which performed the execution.
	 * @throws IOException on error
	 * @throws DavException on error
	 */
	abstract BaseDavRequest internalSyncItems() throws IOException, DavException;
}
