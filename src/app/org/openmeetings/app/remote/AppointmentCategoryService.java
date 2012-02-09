/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.remote;

import java.util.List;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentCategoryService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentCategoryService.class,
			OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private AppointmentCategoryDaoImpl appointmentCategoryDaoImpl;
	@Autowired
	private AuthLevelmanagement authLevelmanagement;

	public List<AppointmentCategory> getAppointmentCategoryList(String SID) {
		log.debug("AppointmenetCategoryService.getAppointmentCategoryList SID : "
				+ SID);

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelmanagement.checkUserLevel(user_level)) {

				List<AppointmentCategory> res = appointmentCategoryDaoImpl
						.getAppointmentCategoryList();

				if (res == null || res.size() < 1)
					log.debug("no AppointmentCategories found");
				else {
					for (int i = 0; i < res.size(); i++) {
						AppointmentCategory ac = res.get(i);
						log.debug("found appCategory : " + ac.getName());
					}
				}

				return res;
			} else {
				log.error("AppointmenetCategoryService.getAppointmentCategoryList : UserLevel Error");
			}
		} catch (Exception err) {
			log.error("[getAppointmentCategory]", err);
		}
		return null;

	}

	/*
	 * public Appointment getNextAppointment(String SID){
	 * 
	 * try{
	 * 
	 * Long users_id = Sessionmanagement.getInstance().checkSession(SID); Long
	 * user_level = Usermanagement.getInstance().getUserLevelByID(users_id); if
	 * (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
	 * 
	 * return AppointmentLogic.getInstance().getNextAppointment(); } } catch
	 * (Exception err) { log.error("[getNextAppointmentById]",err); } return
	 * null;
	 * 
	 * }
	 * 
	 * public List<Appointment> searchAppointmentByName(String SID, String
	 * appointmentName){
	 * 
	 * try{
	 * 
	 * Long users_id = Sessionmanagement.getInstance().checkSession(SID); Long
	 * user_level = Usermanagement.getInstance().getUserLevelByID(users_id); if
	 * (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
	 * 
	 * return
	 * AppointmentLogic.getInstance().searchAppointmentByName(appointmentName);
	 * } } catch (Exception err) { log.error("[searchAppointmentByName]",err); }
	 * return null;
	 * 
	 * }
	 * 
	 * public void saveAppointment(String SID, String appointmentName,Long
	 * userId, String appointmentLocation,String appointmentDescription, Date
	 * appointmentstart, Date appointmentend, Boolean isDaily, Boolean isWeekly,
	 * Boolean isMonthly, Boolean isYearly, Long categoryId){
	 * 
	 * try{
	 * 
	 * Long users_id = Sessionmanagement.getInstance().checkSession(SID); Long
	 * user_level = Usermanagement.getInstance().getUserLevelByID(users_id); if
	 * (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
	 * 
	 * AppointmentLogic.getInstance().saveAppointment(appointmentName, userId,
	 * appointmentLocation, appointmentDescription, appointmentstart,
	 * appointmentend, isDaily, isWeekly, isMonthly, isYearly, categoryId); } }
	 * catch (Exception err) { log.error("[saveAppointment]",err); }
	 * 
	 * 
	 * }
	 * 
	 * public void updateAppointment(String SID,Long appointmentId ,String
	 * appointmentName, Long userId, String appointmentLocation,String
	 * appointmentDescription, Date appointmentstart, Date appointmentend,
	 * Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
	 * Long categoryId){
	 * 
	 * try{
	 * 
	 * Long users_id = Sessionmanagement.getInstance().checkSession(SID); Long
	 * user_level = Usermanagement.getInstance().getUserLevelByID(users_id); if
	 * (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
	 * 
	 * AppointmentLogic.getInstance().updateAppointment(appointmentId,
	 * appointmentName, userId, appointmentDescription, appointmentstart,
	 * appointmentend, isDaily, isWeekly, isMonthly, isYearly, categoryId); } }
	 * catch (Exception err) { log.error("[updateAppointment]",err); }
	 * 
	 * 
	 * }
	 * 
	 * public void deleteAppointment(String SID,Long appointmentId){
	 * 
	 * try{
	 * 
	 * Long users_id = Sessionmanagement.getInstance().checkSession(SID); Long
	 * user_level = Usermanagement.getInstance().getUserLevelByID(users_id); if
	 * (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
	 * 
	 * AppointmentLogic.getInstance().deleteAppointment(appointmentId); } }
	 * catch (Exception err) { log.error("[deleteAppointment]",err); }
	 * 
	 * 
	 * }
	 */
}
