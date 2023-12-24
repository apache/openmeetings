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
package org.apache.openmeetings.webservice.util;

import static org.apache.openmeetings.util.CalendarPatterns.ISO8601_FULL_FORMAT;

import java.util.Calendar;
import java.util.TimeZone;

import jakarta.ws.rs.ext.ParamConverter;

import org.apache.wicket.util.string.Strings;

public class CalendarParamConverter implements ParamConverter<Calendar> {
	public static Calendar get(String val, String tzId) {
		Calendar c = Strings.isEmpty(tzId) ? Calendar.getInstance() : Calendar.getInstance(TimeZone.getTimeZone(tzId));
		c.setTime(DateParamConverter.get(val));
		return c;
	}

	@Override
	public Calendar fromString(String val) {
		if (Strings.isEmpty(val)) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(DateParamConverter.get(val));
		return c;
	}

	@Override
	public String toString(Calendar val) {
		return ISO8601_FULL_FORMAT.format(val);
	}
}
