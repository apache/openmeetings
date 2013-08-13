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
package org.apache.openmeetings.utils;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.persistence.beans.basic.Configuration.MAX_UPLOAD_SIZE_KEY;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * Get some of the default values for the upload
 * 
 * @author solomax, swagner
 * 
 */
public class ImportHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(ImportHelper.class, webAppRootKey);
	public static final long DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 1024; // 1GB

	/**
	 * returns the max upload size configured by max_upload_size config key
	 * 
	 * @param configurationDao
	 * @return
	 */
	public static final long getMaxUploadSize(ConfigurationDao configurationDao) {
		try {
			return configurationDao.getConfValue(MAX_UPLOAD_SIZE_KEY,
					Long.class, "" + DEFAULT_MAX_UPLOAD_SIZE);
		} catch (Exception e) {
			log.error("Invalid value saved for max_upload_size conf key: ", e);
		}
		return DEFAULT_MAX_UPLOAD_SIZE;
	}

	/**
	 * returns a list of all timezones as a {@link Map}
	 * 
	 * @param tzList
	 * @return
	 */
	public static Map<String, String> getAllTimeZones(String ... tzList) {
		Map<String, String> result = new LinkedHashMap<String, String>();

		for (String omTimeZone : tzList) {
			result.put(omTimeZone, omTimeZone);
		}

		return result;
	}
}
