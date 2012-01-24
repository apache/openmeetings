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
package org.openmeetings.test.calendar;

import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.math.TimezoneUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class TestSaveAppointment extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger
			.getLogger(TestSaveAppointment.class);

	@Autowired
	private Usermanagement userManagement;

	@Autowired
	private TimezoneUtil timezoneUtil;

	@Test
	public void testSaveAppointment() {

		Long userId = 1L;
		Date appointmentstartLocal = new Date();
		Date appointmentendLocal = new Date(
				appointmentstartLocal.getTime() + 3600000);

		log.debug("appointmentstartLocal "
				+ CalendarPatterns
						.getDateWithTimeByMiliSecondsWithZone(appointmentstartLocal));

		// Adding Invitor as Meetingmember
		Users user = userManagement.getUserById(userId);

		// Refactor the given time ignoring the Date is always UTC!
		TimeZone timezone = timezoneUtil.getTimezoneByUser(user);
		Date appointmentstart = TimezoneUtil.reCalcDateToTimezonCalendarObj(
				appointmentstartLocal, timezone).getTime();
		Date appointmentend = TimezoneUtil.reCalcDateToTimezonCalendarObj(
				appointmentendLocal, timezone).getTime();

		log.debug("appointmentstart "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
						appointmentstart, timezone));
		log.debug("appointmentend  "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
						appointmentend, timezone));

	}

}
