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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ImportHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ImportHelper.class, OpenmeetingsVariables.webAppRootKey);
	public static final int DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 1024; // 1GB

	public static final int getMaxUploadSize(
			Configurationmanagement cfgManagement) {
		int result = DEFAULT_MAX_UPLOAD_SIZE;
		String maxSize = cfgManagement.getConfValue("max_upload_size", String.class, "" + result);
		try {
			result = (int) Math.min(Long.parseLong(maxSize), Integer.MAX_VALUE);
		} catch (Exception e) {
			log.error("Invalid value saved for maxUploadSize: " + maxSize, e);
		}
		return result;
	}
	
	public static Map<String, String> getAllTimeZones(List<OmTimeZone> tzList) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		
		log.debug("omTimeZoneList :: " + tzList.size());
		for (OmTimeZone omTimeZone : tzList) {
			String labelName = omTimeZone.getLabel() + " ("
					+ omTimeZone.getJname() + ")";
			log.debug("labelName :: " + labelName);
			result.put(omTimeZone.getIcal(), labelName);
		}
		
		return result;
	}
}
