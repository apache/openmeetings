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

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;

//HACK to bypass cross project compilation
public interface IUserManager {
	Object registerUser(String login, String userpass, String lastname,
			String firstname, String email, String country,
			long languageId, String tzId);

	Object registerUser(User u, String password, String hash) throws OmException, NoSuchAlgorithmException;

	Long getLanguage(Locale loc);

	boolean kickExternal(Long roomId, String externalType, String externalId);
	boolean kickUsersByRoomId(Long roomId);
}
