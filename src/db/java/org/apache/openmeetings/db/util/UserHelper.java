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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_LOGIN_MIN_LENGTH_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PASS_MIN_LENGTH_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;

public class UserHelper {
	public static int getMinLoginLength(ConfigurationDao cfgDao) {
		return cfgDao.getConfValue(CONFIG_LOGIN_MIN_LENGTH_KEY, Integer.class, "" + USER_LOGIN_MINIMUM_LENGTH);
	}
	
	public static int getMinPasswdLength(ConfigurationDao cfgDao) {
		return cfgDao.getConfValue(CONFIG_PASS_MIN_LENGTH_KEY, Integer.class, "" + USER_PASSWORD_MINIMUM_LENGTH);
	}
	
	public static boolean invalidPassword(String pass, ConfigurationDao cfgDao) {
		return (pass == null || pass.length() < getMinPasswdLength(cfgDao));
	}
}
