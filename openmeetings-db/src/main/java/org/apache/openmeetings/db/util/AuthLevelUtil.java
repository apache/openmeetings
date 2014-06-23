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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Set;

import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class AuthLevelUtil {
	private static final Logger log = Red5LoggerFactory.getLogger(AuthLevelUtil.class, webAppRootKey);
	
	private static boolean check(Set<Right> rights, Right level) {
		boolean result = rights.contains(level);
		log.debug(String.format("Level %s :: %s", level, result ? "[GRANTED]" : "[DENIED]"));
		return result;
	}
	
	public static boolean hasUserLevel(Set<Right> rights) {
		return check(rights, Right.Room);
	}

	public static boolean hasModLevel(User u, Long orgId) {
		boolean result = hasAdminLevel(u.getRights());
		if (!result && orgId != null) {
			for (Organisation_Users ou : u.getOrganisation_users()) {
				if (orgId.equals(ou.getOrganisation().getId())) {
					if (Boolean.TRUE.equals(ou.getIsModerator())) {
						result = true;
					}
					break;
				}
			}
		}
		return result;
	}

	public static boolean hasAdminLevel(Set<Right> rights) {
		return check(rights, Right.Admin);
	}

	public static boolean hasWebServiceLevel(Set<Right> rights) {
		return check(rights, Right.Soap);
	}

	public static boolean hasLoginLevel(Set<Right> rights) {
		return check(rights, Right.Login);
	}
}
