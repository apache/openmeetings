package org.openmeetings.test.dao;

import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class AppointmentReminderTypDaoImplTest extends AbstractTestCase {
	
	public AppointmentReminderTypDaoImplTest(String name) {
		super(name);
	}

	final public void testAppointmentReminderTypDaoImpl() throws Exception {
		
		Long id = AppointmentReminderTypDaoImpl.getInstance().addAppointmentReminderTyps(1L, "Reminder", "comment");
		assertNotNull("Cann't add Reminder Typ", id);
		
		AppointmentReminderTyps reminderTyps = 	AppointmentReminderTypDaoImpl.getInstance().getAppointmentReminderTypById(id);	
		assertNotNull("Cann't get Reminder Typ", reminderTyps);
		
		id = AppointmentReminderTypDaoImpl.getInstance().deleteAppointmentReminderTyp(id);
		assertNotNull("Cann't delete Reminder Typ", id);
	}
}
