package org.openmeetings.test.calendar;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.remote.CalendarService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
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

