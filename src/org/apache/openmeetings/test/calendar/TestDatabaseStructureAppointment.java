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



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



public class TestDatabaseStructureAppointment extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureAppointment.class);
	@Autowired
	private AppointmentDao appointmentDao;

	@Test
	public void testAddingGroup(){

		try {
			
				Calendar cal = Calendar.getInstance();
				cal.set(2008, 9, 2);
				cal.get(Calendar.DAY_OF_MONTH);
				cal.getTime();
				
				SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
				Date date = format.parse( "2008-17-08" );
				Date date2 = format.parse( "2008-18-08" );
		
				List<Appointment> listAppoints =	appointmentDao.getAppointmentsByRange(1L, date, date2);
			//List<Appointment> listAppoints = AppointmentDaoImpl.getInstance().searchAppointmentsByName("%");
			//AppointmentDaoImpl.getInstance().getNextAppointmentById(1L);
			//AppointmentDaoImpl.getInstance().addAppointment("mezo",1L, "Pforzheim", "zweiter", Calendar.getInstance().getTime() , 
				//date, null, true, null, null, 1L,1L);
			//AppointmentDaoImpl.getInstance().addAppointment("testap", "erster Test",Calendar.getInstance().getTime() , 
					///Calendar.getInstance().getTime(), true, false, false, false, new Long(1), 1L);
			log.debug("Anzahl: "+listAppoints.size());
			
			
			for (Appointment appoints : listAppoints) {
				log.debug("Termin: "+appoints.getAppointmentName()+" startDate: "+appoints.getAppointmentStarttime()+ " endDate: "+appoints.getAppointmentEndtime());
				log.debug("MeetingMembers: "+appoints.getMeetingMember().size());
			}
			
			for (Iterator<Appointment> iter = listAppoints.iterator();iter.hasNext();) {
				log.debug(""+iter.next());
			}
			
			//AppointmentDaoImpl.getInstance().updateAppointment(1L,"neu", "erster Test",Calendar.getInstance().getTime() , 
					//Calendar.getInstance().getTime(), true, false, false, false, new Long(1));
			//log.debug("AppointmentReminderTypDaoImpl: "+appointmentReminderTypDaoImpl.getAppointmentReminderTypById(1L));
		} catch (Exception err) {

			log.error("[testAddingGroup]",err);

		}

		

		

	}



}

