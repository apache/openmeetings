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
package org.apache.openmeetings.webservice.util;

import jakarta.ws.rs.ext.ParamConverter;

import org.apache.openmeetings.db.dto.user.UserDTO;

import com.github.openjson.JSONObject;

public class UserParamConverter implements ParamConverter<UserDTO> {
	public static final String ROOT = "userDTO";

	@Override
	public UserDTO fromString(String val) {
		JSONObject o = new JSONObject(val);
		if (o.has(ROOT)) {
			o = o.getJSONObject(ROOT);
		}
		return UserDTO.get(o);
	}

	public static JSONObject json(UserDTO val) {
		return new JSONObject(val);
	}

	@Override
	public String toString(UserDTO val) {
		return json(val).toString();
	}
}
