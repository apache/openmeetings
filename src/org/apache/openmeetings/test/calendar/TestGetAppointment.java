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
package org.apache.openmeetings.test.calendar;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestGetAppointment extends
AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestGetAppointment.class);

	@Autowired
	private AppointmentDao appointmentDao;
	
	@Test
	public void getAppoinment() {
		log.debug("getAppoinment enter");
		Long userId = 1L;
		
		Calendar starttime = GregorianCalendar.getInstance();

		starttime.set(Calendar.MONTH, starttime.get(Calendar.MONTH-1));

		Calendar endtime = GregorianCalendar.getInstance();
		
		appointmentDao.getAppointmentsByRange(userId, starttime.getTime(),
				endtime.getTime());
		
		assertTrue(true);
		
	}
	
}
