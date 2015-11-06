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
package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ErrorService {
	private static final Logger log = Red5LoggerFactory.getLogger(ErrorService.class, webAppRootKey);

	@Autowired
	private LabelDao labelDao;
	@Autowired
	private ErrorDao errorDao;

	/**
	 * Gets an Error-Object by its id TODO: add error-code-handlers -20
	 * duplicate FileName -21 FileName too short (length = 0) and make the
	 * persistent in the DataBase
	 * 
	 * @param SID
	 * @param errorid
	 * @return - ErrorResult object with the id given
	 */
	public ServiceResult getErrorByCode(String SID, Long errorid, Long langId) {
		if (errorid < 0) {
			log.debug("errorid, language_id: " + errorid + "|" + langId);
			ErrorValue eValues = errorDao.get(-1 * errorid);
			if (eValues != null) {
				log.debug("eValues.getFieldvalues_id() = " + eValues.getLabelId());
				String eValue = labelDao.getString(eValues.getLabelId(), langId);
				if (eValue != null) {
					return new ServiceResult(errorid, eValue, eValues.getType().name());
				}
			}
		} else {
			return new ServiceResult(errorid, "Error ... please check your input", "Error");
		}
		return null;
	}

}
