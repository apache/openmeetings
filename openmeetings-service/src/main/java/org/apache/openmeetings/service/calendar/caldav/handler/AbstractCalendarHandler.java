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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.IcalUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Abstract Class which contains all the common code for all Handlers.
 */
public abstract class AbstractCalendarHandler implements CalendarHandler {
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractCalendarHandler.class, getWebAppRootKey());

	protected HttpClient client;
	protected OmCalendar calendar;
	protected String path;
	protected IcalUtils utils;

	protected AppointmentDao appointmentDao;

	public AbstractCalendarHandler(String path, OmCalendar calendar, HttpClient client,
			AppointmentDao appointmentDao, IcalUtils utils)
	{
		this.path = path;
		this.calendar = calendar;
		this.client = client;
		this.appointmentDao = appointmentDao;
		this.utils = utils;
	}

	public static Map<String, Appointment> listToMap(List<String> keys, List<Appointment> values) {
		Map<String, Appointment> map = new HashMap<>();
		for (int i = 0; i < keys.size(); ++i) {
			map.put(keys.get(i), values.get(i));
		}
		return map;
	}

	@Override
	public OmCalendar syncItems() {
		DavMethodBase method = null;
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

	void releaseConnection(DavMethodBase method) {
		if (method != null) {
			method.releaseConnection();
		}
	}

	abstract DavMethodBase internalSyncItems() throws IOException, DavException;
}
