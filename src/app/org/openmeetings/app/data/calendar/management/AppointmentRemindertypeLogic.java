package org.openmeetings.app.data.calendar.management;

import java.util.List;

import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentReminderTyps;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;

/**
 * 
 * @author o.becherer
 * Businesslogic for reminderTypes
 *
 */
public class AppointmentRemindertypeLogic {
	
	private static final Logger log = Logger.getLogger(AppointmentCategoryLogic.class);
	private static AppointmentRemindertypeLogic instance = null;

	public static synchronized AppointmentRemindertypeLogic getInstance() {
		if (instance == null) {
			instance = new AppointmentRemindertypeLogic();
		}

		return instance;
	}
	
	
	public List<AppointmentReminderTyps> getAppointmentReminderTypList(){
		try {	
			return AppointmentReminderTypDaoImpl.getInstance().getAppointmentReminderTypList();
		}catch(Exception err){
			log.error("[getAppointmentReminderTypList]",err);
		}
		return null;
	}
	
	
	public void createAppointMentReminderType(Long user_id, String name, String comment){
		
		try{
			AppointmentReminderTypDaoImpl.getInstance().addAppointmentReminderTyps(user_id, name, comment);
		}catch(Exception e){
			log.error("Error creating ReminderTyp");
		}
	}
	
}
