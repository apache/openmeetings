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
package org.apache.openmeetings.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Get some of the default values for the upload
 *
 * @author swagner
 *
 */
public class ImportHelper {
	private ImportHelper() {}

	/**
	 * returns a list of all timezones as a {@link Map}
	 *
	 * @param tzList
	 *            - timezones to put to resulting {@link Map}
	 * @return {@link Map} with passed time zones as key and value
	 */
	public static Map<String, String> getAllTimeZones(String ... tzList) {
		Map<String, String> result = new LinkedHashMap<>();

		for (String omTimeZone : tzList) {
			result.put(omTimeZone, omTimeZone);
		}

		return result;
	}
}
