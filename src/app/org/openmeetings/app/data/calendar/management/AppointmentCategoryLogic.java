
package org.openmeetings.app.data.calendar.management;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentCategory;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.User;

public class AppointmentCategoryLogic {
	
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentCategoryLogic.class, ScopeApplicationAdapter.webAppRootKey);
	private static AppointmentCategoryLogic instance = null;

	public static synchronized AppointmentCategoryLogic getInstance() {
		if (instance == null) {
			instance = new AppointmentCategoryLogic();
		}

		return instance;
	}
	
	public List<AppointmentCategory> getAppointmentCategoryList( ){
		log.debug("AppointmentCategoryLogic.getAppointmentCategoryList");
		try {	
			return AppointmentCategoryDaoImpl.getInstance().getAppointmentCategoryList();
		}catch(Exception err){
			log.error("[getAppointmentCategory]",err);
		}
		return null;
	}
	
	
	/**
	 * 
	 * Creating an AppointmentCategory
	 */
	//---------------------------------------------------------------------------------------------
	public void createAppointmentCategory(String name, String comment, Long userid){
		log.debug("AppointmentCategoryLogic.createAppointmentCategory");
		
		AppointmentCategoryDaoImpl.getInstance().addAppointmentCategory(userid, name, comment);
		
	}
	//--------------------------------------------------------------------------------------------
	
	
	
/*	
	//next appointment to current date
	public Appointment getNextAppointment(){
		try{
		return AppointmentDaoImpl.getInstance().getNextAppointment(new Date());
		}catch(Exception err){
			log.error("[getNextAppointmentById]",err);
		}	
		return null;
	}
	
	public List<Appointment> searchAppointmentByName(String appointmentName){
		try{
		return AppointmentDaoImpl.getInstance().searchAppointmentsByName(appointmentName) ;
		}catch(Exception err){
			log.error("[searchAppointmentByName]",err);	
		}
		return null;
	}
	
	public void saveAppointment(String appointmentName, Long userId, String appointmentLocation,String appointmentDescription, 
			Date appointmentstart, Date appointmentend, 
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, Long categoryId){
		
		try{
		AppointmentDaoImpl.getInstance().addAppointment(appointmentName, userId, appointmentLocation, appointmentDescription,
				appointmentstart, appointmentend, isDaily, isWeekly, isMonthly, isYearly, categoryId);
		}catch(Exception err){
			log.error("[saveAppointment]",err);
		}
	}
	
	public void deleteAppointment(Long appointmentId){
		try{
		AppointmentDaoImpl.getInstance().deleteAppointement(appointmentId);
		}catch(Exception err){
		log.error("[deleteAppointment]",err);	
		}
		
	}
	
	public void updateAppointment(Long appointmentId, String appointmentName,Long userId, String appointmentDescription, 
			Date appointmentstart, Date appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, Long categoryId ){
		
		try {
			AppointmentDaoImpl.getInstance().updateAppointment(appointmentId,
					appointmentName, userId, appointmentDescription, appointmentstart,
					appointmentend, isDaily, isWeekly, isMonthly, isYearly,
					categoryId);
		} catch (Exception err) {
			log.error("[updateAppointment]",err);
		}
	} 
	*/
}
