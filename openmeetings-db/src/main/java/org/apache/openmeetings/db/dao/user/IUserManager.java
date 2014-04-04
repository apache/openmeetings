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
package org.apache.openmeetings.db.dao.user;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.db.entity.room.Client;
import org.red5.server.api.IClient;

//FIXME HACK to bypass cross project compilation
public interface IUserManager {
	Long registerUserInit(long user_level, long level_id, int availible,
			int status, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, boolean sendWelcomeMessage,
			List<Long> organisations, String phone, boolean sendSMS, String baseURL,
			Boolean sendConfirmation,
			TimeZone timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts, String activatedHash) throws Exception;

	Long registerUserNoEmail(String login, String Userpass,
			String lastname, String firstname, String email, Date age,
			String street, String additionalname, String fax, String zip,
			long states_id, String town, long language_id, String phone, boolean sendSMS, 
			boolean generateSipUserData, String jNameTimeZone);
	
	Object loginUser(String SID, String userOrEmail, String userpass,
			Client currentClient, IClient client, Boolean storePermanent);
}
