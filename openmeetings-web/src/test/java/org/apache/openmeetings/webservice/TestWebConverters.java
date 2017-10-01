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
package org.apache.openmeetings.webservice;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.webservice.util.CalendarParamConverter;
import org.apache.openmeetings.webservice.util.DateParamConverter;
import org.junit.Test;

import com.github.openjson.JSONObject;

public class TestWebConverters {
	@Test
	public void testDateConverter() {
		assertEquals("Null date should be parsed", null, DateParamConverter.get(null));
		assertEquals("Date should be parsed"
				, Date.from(LocalDate.of(2017, 01, 15).atStartOfDay(ZoneId.systemDefault()).toInstant())
				, DateParamConverter.get("2017-01-15"));
		assertEquals("Date should be parsed"
				, Date.from(ZonedDateTime.of(2017, 01, 20, 20, 30, 03, 0, ZoneId.of("Europe/Moscow")).toInstant())
				, DateParamConverter.get("2017-01-20T20:30:03+0300"));
	}

	@Test
	public void testCalendarConverter() {
		CalendarParamConverter c = new CalendarParamConverter();
		assertEquals("Null calendar should be parsed", null, c.fromString(null));
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(LocalDate.of(2017, 01, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertEquals("Calendar should be parsed", cal, c.fromString("2017-01-15"));
	}

	@Test
	public void testUserConverter() {
		assertEquals("Null UserDTO should be parsed", null, UserDTO.get((JSONObject)null));
	}
}
