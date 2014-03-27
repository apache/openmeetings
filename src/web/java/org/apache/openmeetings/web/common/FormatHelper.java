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
package org.apache.openmeetings.web.common;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import org.apache.openmeetings.db.entity.user.User;

public class FormatHelper {
	
	// TODO check RIGHTS here (email might need to be hidden)
	public static String formatUser(User u, boolean isHTMLEscape) {
			return u == null ? "" : isHTMLEscape ? escapeHtml4(String.format("\"%s %s\" <%s>", u.getFirstname(), u.getLastname(), u.getAdresses().getEmail()))
											: String.format("\"%s %s\" <%s>", u.getFirstname(), u.getLastname(), u.getAdresses().getEmail());		
	}

}
