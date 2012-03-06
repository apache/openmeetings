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
package org.openmeetings.utils;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ImportHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ImportHelper.class, OpenmeetingsVariables.webAppRootKey);
	public static final int DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 1024; // 1GB

	public static final int getMaxUploadSize(
			Configurationmanagement cfgManagement) {
		return getMaxUploadSize(cfgManagement, 3L);
	}

	public static final int getMaxUploadSize(
			Configurationmanagement cfgManagement, Long userLevel) {
		Configuration cfg = cfgManagement.getConfKey(userLevel,
				"DEFAULT_MAX_UPLOAD_SIZE");
		int result = DEFAULT_MAX_UPLOAD_SIZE;
		if (cfg != null) {
			String val = cfg.getConf_value();
			try {
				result = (int) Math.min(Long.parseLong(val), Integer.MAX_VALUE);
			} catch (Exception e) {
				log.error("Invalid value saved for maxUploadSize: " + val, e);
			}
		}
		return result;
	}
}
