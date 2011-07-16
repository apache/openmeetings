package org.openmeetings.test.dao;

import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentCategory;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class AppointmentCategoryDaoImplTest extends AbstractTestCase {
	
	public AppointmentCategoryDaoImplTest(String name){
		super(name);
	}

	final public void testAppointmentCategoryDaoImpl() throws Exception{

		Long id = AppointmentCategoryDaoImpl.getInstance().addAppointmentCategory(1L, "Category", "comment");
		assertNotNull("Cann't add Appointment category", id);

		AppointmentCategory ac = AppointmentCategoryDaoImpl.getInstance().getAppointmentCategoryById(id); 	
		assertNotNull("Cann't get Appointment Category", ac);
		
		id = AppointmentCategoryDaoImpl.getInstance().deleteAppointmentCategory(id);
		assertNotNull("Cann't delete Appointment Category", id);
	}
}
