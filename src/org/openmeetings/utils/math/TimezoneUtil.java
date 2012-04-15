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
package org.openmeetings.utils.math;

import java.util.Calendar;
import java.util.TimeZone;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.user.Users;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TimezoneUtil {

	private static final Logger log = Red5LoggerFactory.getLogger(
			TimezoneUtil.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;

	/**
	 * Returns the timezone based on the user profile, if not return the
	 * timezone from the server
	 * 
	 * @param user
	 * @return
	 */
	public TimeZone getTimezoneByUser(Users user) {

		if (user != null && user.getOmTimeZone() != null) {

			TimeZone timeZone = TimeZone.getTimeZone(user.getOmTimeZone()
					.getIcal());

			if (timeZone != null) {
				return timeZone;
			}

		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone.getTimeZone(omTimeZone
					.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}

	/**
	 * Return the timezone based on our internal jName
	 * 
	 * @param jName
	 * @return
	 */
	public TimeZone getTimezoneByInternalJName(String jName) {

		OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jName);
		
		if (omTimeZone == null) {
			log.error("There is not omTimeZone for this jName: "+jName);
			throw new RuntimeException("There is not omTimeZone for this jName: "+jName);
		}

		TimeZone timeZone = TimeZone.getTimeZone(omTimeZone.getIcal());

		if (timeZone != null) {
			return timeZone;
		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZoneDefault = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone
					.getTimeZone(omTimeZoneDefault.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}

	/**
	 * Return the timezone based Id from omTimeZone table
	 * 
	 * @param jName
	 * @return
	 */
	public TimeZone getTimezoneByOmTimeZoneId(Long omtimezoneId) {

		OmTimeZone omTimeZone = omTimeZoneDaoImpl
				.getOmTimeZoneById(omtimezoneId);

		TimeZone timeZone = TimeZone.getTimeZone(omTimeZone.getIcal());

		if (timeZone != null) {
			return timeZone;
		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZoneDefault = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone
					.getTimeZone(omTimeZoneDefault.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}
	
	public static long _getOffset(TimeZone timezone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timezone);
		return cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
	}

}
