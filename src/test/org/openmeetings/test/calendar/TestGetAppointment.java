package org.openmeetings.test.calendar;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestGetAppointment extends
AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger.getLogger(TestGetAppointment.class);

	@Autowired
	private AppointmentDaoImpl appointmentDao;
	
	@Test
	public void getAppoinment() {
		
		Long userId = 1L;
		
		Calendar starttime = GregorianCalendar.getInstance();

		starttime.set(Calendar.MONTH, starttime.get(Calendar.MONTH-1));

		Calendar endtime = GregorianCalendar.getInstance();
		
		appointmentDao.getAppointmentsByRange(userId, starttime.getTime(),
				endtime.getTime());
		
		assertTrue(true);
		
	}
	
}
