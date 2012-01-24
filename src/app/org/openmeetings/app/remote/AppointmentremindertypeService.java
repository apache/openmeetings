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

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author o.becherer
 * 
 */
public class AppointmentremindertypeService {
	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentremindertypeService.class,
			ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	@Autowired
	private AuthLevelmanagement authLevelmanagement;

	/**
	 * 
	 * @param SID
	 * @return
	 */
	// ---------------------------------------------------------------------------------------------------------
	public List<AppointmentReminderTyps> getAppointmentReminderTypList(
			String SID) {
		log.debug("getAppointmentReminderTypList");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelmanagement.checkUserLevel(user_level)) {

				List<AppointmentReminderTyps> res = appointmentReminderTypDaoImpl
						.getAppointmentReminderTypList();

				if (res == null || res.size() < 1) {
					log.debug("no remindertyps found!");
				} else {
					for (int i = 0; i < res.size(); i++) {
						log.debug("found reminder " + res.get(i).getName());
					}
				}

				return res;
			} else
				log.debug("getAppointmentReminderTypList  :error - wrong authlevel!");
		} catch (Exception err) {
			log.error("[getAppointmentReminderTypList]", err);
		}
		return null;
	}
	// ---------------------------------------------------------------------------------------------------------
}
