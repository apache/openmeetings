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

import static java.text.DateFormat.SHORT;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.user.User;

public class FormatHelper {
	private FormatHelper() {}

	public static String formatUser(User u) {
		return formatUser(u, false);
	}

	public static String formatUser(User u, boolean isHTMLEscape) {
		String user = "";
		if (u != null) {
			if (User.Type.CONTACT == u.getType() && u.getAddress() != null) {
				user = String.format("\"%s\" <%s>", u.getDisplayName(), u.getAddress().getEmail());
			} else {
				user = String.format("\"%s\" [%s]", u.getDisplayName(), u.getLogin());
			}
			user = isHTMLEscape ? escapeHtml4(user) : user;
		}
		return user;
	}

	public static FastDateFormat getDateFormat(User u) {
		return FastDateFormat.getDateInstance(SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}

	public static FastDateFormat getTimeFormat(User u) {
		return FastDateFormat.getTimeInstance(SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}

	public static FastDateFormat getDateTimeFormat(User u) {
		return FastDateFormat.getDateTimeInstance(SHORT, SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}
}
