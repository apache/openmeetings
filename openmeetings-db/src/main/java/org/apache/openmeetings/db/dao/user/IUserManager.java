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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.Userdata;

//FIXME HACK to bypass cross project compilation
public interface IUserManager {
	
	Long registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long stateId,
			String town, long languageId, String phone, boolean sendSMS,
			boolean generateSipUserData, String jNameTimeZone, Boolean sendConfirmation);
	
	Long registerUserInit(Set<Right> rights, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long stateId,
			String town, long languageId, boolean sendWelcomeMessage,
			List<Long> organisations, String phone, boolean sendSMS, Boolean sendConfirmation,
			TimeZone timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts, String activatedHash) throws Exception;

	Long getLanguage(Locale loc);
	State getCountry(Locale loc);
	User loginOAuth(Map<String, String> params, long serverId) throws IOException, NoSuchAlgorithmException;

	/** TODO FIXME seems to be unused **/
	Boolean kickUserByStreamId(String SID, Long roomId);
	
	Boolean kickUserByPublicSID(String SID, String publicSID);
	
	Long logout(String SID, long userId);
	
	List<Userdata> getUserdataDashBoard(Long userId);
}
