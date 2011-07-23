package org.openmeetings.app.data.calendar.management;

import java.util.List;

import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author o.becherer
 * Businesslogic for reminderTypes
 *
 */
public class AppointmentRemindertypeLogic {
	
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentCategoryLogic.class, ScopeApplicationAdapter.webAppRootKey);
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
