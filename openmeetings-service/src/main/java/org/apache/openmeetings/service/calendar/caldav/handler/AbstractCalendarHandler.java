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

import org.apache.commons.httpclient.HttpClient;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.iCalUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Class which contains all the common code for all Handlers.
 */
public abstract class AbstractCalendarHandler implements CalendarHandler {

	//TODO: Check if protected is necessary.
	protected HttpClient client;
	protected OmCalendar calendar;
	protected String path;
	protected iCalUtils utils;

	protected AppointmentDao appointmentDao;

	public AbstractCalendarHandler(String path, OmCalendar calendar, HttpClient client,
			AppointmentDao appointmentDao, iCalUtils utils)
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
}
