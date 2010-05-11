package org.openmeetings.app.remote;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.AppointmentCategoryLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentCategory;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class AppointmentCategoryService {
	 
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentCategoryService.class, ScopeApplicationAdapter.webAppRootKey);
	
	private static AppointmentCategoryService instance = null;

	public static synchronized AppointmentCategoryService getInstance() {
		if (instance == null) {
			instance = new AppointmentCategoryService();
		}

		return instance;
	}

	public List<AppointmentCategory> getAppointmentCategoryList(String SID){
		log.debug("AppointmenetCategoryService.getAppointmentCategoryList SID : " + SID);
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					
	        	List<AppointmentCategory> res =  AppointmentCategoryLogic.getInstance().getAppointmentCategoryList();
	        	
	        	if(res == null || res.size() < 1)
	        		log.debug("no AppointmentCategories found");
	        	else{
	        		for(int i = 0; i < res.size(); i++){
	        			AppointmentCategory ac = res.get(i);
	        			log.debug("found appCategory : " + ac.getName());
	        		}
	        	}
	        	
	        	return res;
	        }
	        else{
	        	log.error("AppointmenetCategoryService.getAppointmentCategoryList : UserLevel Error");
	        }
		} catch (Exception err) {
			log.error("[getAppointmentCategory]",err);
		}
		return null;
			
		}
	
/*	public Appointment getNextAppointment(String SID){
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        	return AppointmentLogic.getInstance().getNextAppointment();
	        }
		} catch (Exception err) {
			log.error("[getNextAppointmentById]",err);
		}
		return null;
			
		}
	
	public List<Appointment> searchAppointmentByName(String SID, String appointmentName){
			
			try{
				
				Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
		        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
						        	
		        	return AppointmentLogic.getInstance().searchAppointmentByName(appointmentName);
		        }
			} catch (Exception err) {
				log.error("[searchAppointmentByName]",err);
			}
			return null;
				
			}
	
	public void saveAppointment(String SID, String appointmentName,Long userId, String appointmentLocation,String appointmentDescription, 
			Date appointmentstart, Date appointmentend, 
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, Long categoryId){
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        	 AppointmentLogic.getInstance().saveAppointment(appointmentName, userId, appointmentLocation, 
	        			appointmentDescription, appointmentstart, appointmentend, isDaily, isWeekly, isMonthly, 
	        			isYearly, categoryId);
	        }
		} catch (Exception err) {
			log.error("[saveAppointment]",err);
		}
		
			
		}
	
	public void updateAppointment(String SID,Long appointmentId ,String appointmentName, Long userId, String appointmentLocation,String appointmentDescription, 
			Date appointmentstart, Date appointmentend, 
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, Long categoryId){
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        	 AppointmentLogic.getInstance().updateAppointment(appointmentId, appointmentName, userId, 
	        			 appointmentDescription, appointmentstart, appointmentend, isDaily, isWeekly, isMonthly, 
	        			 isYearly, categoryId);
	        }
		} catch (Exception err) {
			log.error("[updateAppointment]",err);
		}
		
			
		}
	
	public void deleteAppointment(String SID,Long appointmentId){
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        	 AppointmentLogic.getInstance().deleteAppointment(appointmentId);
	        }
		} catch (Exception err) {
			log.error("[deleteAppointment]",err);
		}
		
			
		}
		
	*/
	}
