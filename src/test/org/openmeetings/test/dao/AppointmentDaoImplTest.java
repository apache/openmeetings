package org.openmeetings.test.dao;

import java.util.Date;

import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.test.dao.base.AbstractTestCase;


public class AppointmentDaoImplTest extends AbstractTestCase {
	
	public AppointmentDaoImplTest(String name){
		super(name);
	}
	
	final public void testAppointmentDaoImpl() throws Exception
	{
		AppointmentDaoImpl appDaoImpl = AppointmentDaoImpl.getInstance();
		assertNotNull("Cann't access to appointment dao implimentation", appDaoImpl);
		
		// add new appointment
		Appointment ap = new Appointment();
		
		ap.setAppointmentName("appointmentName");
		ap.setAppointmentLocation("appointmentLocation");
		
		Date appointmentstart = new Date();
		Date appointmentend = new Date();
		appointmentend.setTime(appointmentstart.getTime() + 3600);
		
		ap.setAppointmentStarttime(appointmentstart);
	 	ap.setAppointmentEndtime(appointmentend);
		ap.setAppointmentDescription("appointmentDescription");
		ap.setStarttime(new Date());
		ap.setDeleted("false");
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setIsPasswordProtected(false);

		ap.setUserId(UsersDaoImpl.getInstance().getUser(1L));
		ap.setIsConnectedEvent(false);
		Long id = appDaoImpl.addAppointmentObj(ap);
		assertNotNull("Cann't add appointment", id);
		
		// get appointment
		ap = appDaoImpl.getAppointmentById(id);
		assertNotNull("Cann't get appointment", ap);
		
		id = appDaoImpl.deleteAppointement(id);
		assertNotNull("Cann't delete appointment", id);
	}

}
