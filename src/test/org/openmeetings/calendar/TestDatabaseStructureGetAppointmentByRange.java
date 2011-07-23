package org.openmeetings.calendar;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.remote.CalendarService;



public class TestDatabaseStructureGetAppointmentByRange extends TestCase {

	private static final Logger log = Logger.getLogger(TestDatabaseStructureGetAppointmentByRange.class);

	public TestDatabaseStructureGetAppointmentByRange(String testname){

		super(testname);

	}

	

	public void testAddingGroup(){

		try {

			
			
			List<Appointment> listAppoints = CalendarService.getInstance().getAppointmentByRange("SID",Calendar.getInstance().getTime(), Calendar.getInstance().getTime());  

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

