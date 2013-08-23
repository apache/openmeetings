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
package org.apache.openmeetings.remote.util;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IClient;
import org.slf4j.Logger;

public class SessionVariablesUtil {

	private static final Logger log = Red5LoggerFactory.getLogger(
			SessionVariablesUtil.class, OpenmeetingsVariables.webAppRootKey);

	private enum SESSION_VARIABLES {
		isAVClient, // if the client connection is a audio-video only connection
		isScreenClient, // if the client connection is from a Java Web-Start application
		PUBLIC_SID, // the public SID of the client
		USER_ID, // the userId of the user that is using this connection (if there is any)
	}

	public static void initClient(IClient client, boolean isAVClient, String publicSID) {
		client.setAttribute(SESSION_VARIABLES.isAVClient.toString(), isAVClient);
		client.setAttribute(SESSION_VARIABLES.PUBLIC_SID.toString(), publicSID);
	}
	
	public static boolean isAVClient(IClient client) {
		try {
			if (client.getAttribute(SESSION_VARIABLES.isAVClient.toString()) == null) {
				throw new Exception("Connection has no publicSID client: "+client);
			}
			return (Boolean) client.getAttribute(SESSION_VARIABLES.isAVClient.toString());
		} catch (Exception err) {
			log.error("[isAVClient]", err);
		}
		return false;
	}
	
	public static String getPublicSID(IClient client) {
		try {
			if (client.getAttribute(SESSION_VARIABLES.PUBLIC_SID.toString()) == null) {
				throw new Exception("Connection has no publicSID client: "+client);
			}
			return (String) client.getAttribute(SESSION_VARIABLES.PUBLIC_SID.toString());
		} catch (Exception err) {
			log.error("[getPublicSID]", err);
		}
		return null;
	}

	public static void setIsScreenClient(IClient client) {
		client.setAttribute(SESSION_VARIABLES.isScreenClient.toString(), true);
	}

	public static boolean isScreenClient(IClient client) {
		try {
			if (client
					.getAttribute(SESSION_VARIABLES.isScreenClient.toString()) == null) {
				return false;
			}
			if ((Boolean) client.getAttribute(SESSION_VARIABLES.isScreenClient
					.toString())) {
				return true;
			}
		} catch (Exception err) {
			log.error("[isScreenClient]", err);
		}
		return false;
	}
	
	public static void setUserId(IClient client, Long userId) {
		client.setAttribute(SESSION_VARIABLES.USER_ID.toString(), userId);
	}

	/**
	 * if there is no user id set, it will return Long.MIN_VALUE
	 * @param client
	 * @return - user id set or Long.MIN_VALUE or null if any exception happens
	 */
	public static Long getUserId(IClient client) {
		try {
			if (client.getAttribute(SESSION_VARIABLES.USER_ID.toString()) == null) {
				return Long.MIN_VALUE;
			}
			return (Long) client.getAttribute(SESSION_VARIABLES.USER_ID
					.toString());
		} catch (Exception err) {
			log.error("[getUserId]", err);
		}
		return null;
	}
	
}
