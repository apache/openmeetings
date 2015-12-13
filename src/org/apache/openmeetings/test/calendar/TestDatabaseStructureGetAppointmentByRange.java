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

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.remote.CalendarService;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



public class TestDatabaseStructureGetAppointmentByRange extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureGetAppointmentByRange.class);
	@Autowired
	private CalendarService calendarService;
	
	@Test
	public void testAddingGroup(){

		try {
			List<Appointment> listAppoints = calendarService.getAppointmentByRange("SID",Calendar.getInstance().getTime(), Calendar.getInstance().getTime());  

			//List<Appointment> listAppoints = AppointmentDaoImpl.getInstance().getAppointmentsByRange(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
			
			for (Appointment appoints : listAppoints) {
				log.debug(""+appoints);
			}
			
			for (Iterator<Appointment> iter = listAppoints.iterator();iter.hasNext();) {
				log.debug(""+iter.next());
			}

		} catch (Exception err) {

			log.error("[testAddingGroup]",err);

		}

		

		

	}



}

