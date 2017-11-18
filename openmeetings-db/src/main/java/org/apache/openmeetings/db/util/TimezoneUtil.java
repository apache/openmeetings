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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_TIMEZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.TimeZone;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimezoneUtil {
	private static final Logger log = Red5LoggerFactory.getLogger(TimezoneUtil.class, getWebAppRootKey());

	@Autowired
	private ConfigurationDao cfgDao;

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
	public TimeZone getTimeZone(String timeZoneId) {
		if (Strings.isEmpty(timeZoneId)) {
			return getDefaultTimeZone();
		}
		return TimeZone.getTimeZone(timeZoneId);
	}

	/**
	 * @return The current server configured time zone in the table configuration key: {@link OpenmeetingsVariables#CONFIG_DEFAULT_TIMEZONE}
	 */
	public TimeZone getDefaultTimeZone() {
		String defaultTzName = cfgDao.getString(CONFIG_DEFAULT_TIMEZONE, "Europe/Berlin");

		TimeZone timeZoneByOmTimeZone = TimeZone.getTimeZone(defaultTzName);

		if (timeZoneByOmTimeZone != null) {
			return timeZoneByOmTimeZone;
		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();

	}

	/**
	 * Returns the timezone based on the user profile, if not return the timezone from the server
	 *
	 * @param user
	 * @return
	 */
	public TimeZone getTimeZone(User user) {
		if (user != null && user.getTimeZoneId() != null) {

			TimeZone timeZone = TimeZone.getTimeZone(user.getTimeZoneId());

			if (timeZone != null) {
				return timeZone;
			}

		}
		// if user has not time zone get one from the server configuration
		return getDefaultTimeZone();
	}
}
