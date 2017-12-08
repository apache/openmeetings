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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;

import java.util.TimeZone;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;

public class TimezoneUtil {
	private TimezoneUtil() {}
	/**
	 *
	 * @param timeZoneId
	 *            the ID for a TimeZone, either an abbreviation such as "PST", a
	 *            full name such as "America/Los_Angeles", or a custom ID such as
	 *            "GMT-8:00". Note that the support of abbreviations is for JDK
	 *            1.1.x compatibility only and full names should be used.
	 * @return the specified TimeZone, or the GMT zone if the given ID cannot be
	 *         understood.
	 */
	public static TimeZone getTimeZone(String timeZoneId) {
		return TimeZone.getTimeZone(Strings.isEmpty(timeZoneId) ? getDefaultTimezone() : timeZoneId);
	}

	/**
	 * Returns the timezone based on the user profile, if not return the timezone
	 * from the server
	 *
	 * @param user
	 *            to get timezone for
	 * @return {@link TimeZone} of given user
	 */
	public static TimeZone getTimeZone(User user) {
		return getTimeZone(user == null ? null : user.getTimeZoneId());
	}
}
