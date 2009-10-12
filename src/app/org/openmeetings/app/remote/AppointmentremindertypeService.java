package org.openmeetings.app.remote;

import java.util.List;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.calendar.management.AppointmentRemindertypeLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentReminderTyps;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Logger;

/**
 * 
 * @author o.becherer
 *
 */
public class AppointmentremindertypeService {
private static final Logger log = Logger.getLogger(AppointmentremindertypeService.class);
	
	private static AppointmentremindertypeService instance = null;

	public static synchronized AppointmentremindertypeService getInstance() {
		if (instance == null) {
			instance = new AppointmentremindertypeService();
		}

		return instance;
	}
	
	/**
	 * 
	 * @param SID
	 * @return
	 */
	//---------------------------------------------------------------------------------------------------------
	public List<AppointmentReminderTyps> getAppointmentReminderTypList(String SID) {
		log.debug("getAppointmentReminderTypList");
		
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        	List<AppointmentReminderTyps> res =  AppointmentRemindertypeLogic.getInstance().getAppointmentReminderTypList();
	        
	        	if(res == null || res.size() < 1){
	        		log.debug("no remindertyps found!");
	        	}
	        	else{
	        		for(int i = 0; i < res.size(); i++){
	        			log.debug("found reminder " + res.get(i).getName());
	        		}
	        	}
	        	
	        	return res;
	        }
	        else
	        	log.debug("getAppointmentReminderTypList  :error - wrong authlevel!");
		} catch (Exception err) {
			log.error("[getAppointmentReminderTypList]",err);
		}
		return null;
	}
	//---------------------------------------------------------------------------------------------------------
}
