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
package org.openmeetings.app.remote;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.persistence.beans.basic.ErrorValue;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ErrorService {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ErrorService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private ErrorManagement errorManagement;

	/**
	 * Gets an Error-Object by its id TODO: add error-code-handlers -20
	 * duplicate FileName -21 FileName too short (length = 0) and make the
	 * persistent in the DataBase
	 * 
	 * @param SID
	 * @param errorid
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
		// long user_level =
		// Usermanagement.getInstance().getUserLevelByID(users_id);

		if (errorid < 0) {
			log.debug("errorid, language_id: " + errorid + "|" + language_id);
			ErrorValue eValues = errorManagement.getErrorValuesById(-1
					* errorid);
			if (eValues != null) {
				log.debug("eValues.getFieldvalues_id() = " + eValues.getFieldvalues_id());
				// log.debug(eValues.getFieldvalues().getFieldvalues_id());
				log.debug("eValues.getErrorType() = " + errorManagement.getErrorType(eValues.getType().getId()));
				// log.debug(eValues.getErrorType().getErrortype_id());
				// log.debug(eValues.getErrorType().getFieldvalues());
				// log.debug(eValues.getErrorType().getFieldvalues().getFieldvalues_id());
				Fieldlanguagesvalues errorValue = fieldmanagment
						.getFieldByIdAndLanguage(eValues.getFieldvalues_id(),
								language_id);
				Fieldlanguagesvalues typeValue = fieldmanagment
						.getFieldByIdAndLanguage(errorManagement.getErrorType(eValues.getType().getId())
								.getFieldvalues_id(), language_id);
				if (errorValue != null) {
					return new ErrorResult(errorid, errorValue.getValue(),
							typeValue.getValue());
				}
			}
		} else {
			return new ErrorResult(errorid,
					"Error ... please check your input", "Error");
		}

		return null;
	}

}
