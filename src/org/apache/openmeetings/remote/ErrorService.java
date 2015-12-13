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
package org.apache.openmeetings.remote;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.dao.ErrorDao;
import org.apache.openmeetings.data.beans.basic.ErrorResult;
import org.apache.openmeetings.persistence.beans.basic.ErrorValue;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
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
	private FieldManager fieldManager;
	@Autowired
	private ErrorDao errorManagement;

	/**
	 * Gets an Error-Object by its id TODO: add error-code-handlers -20
	 * duplicate FileName -21 FileName too short (length = 0) and make the
	 * persistent in the DataBase
	 * 
	 * @param SID
	 * @param errorid
	 * @return - ErrorResult object with the id given
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		if (errorid < 0) {
			log.debug("errorid, language_id: " + errorid + "|" + language_id);
			ErrorValue eValues = errorManagement.getErrorValuesById(-1
					* errorid);
			if (eValues != null) {
				log.debug("eValues.getFieldvalues_id() = " + eValues.getFieldvalues_id());
				log.debug("eValues.getErrorType() = " + errorManagement.getErrorType(eValues.getErrortype_id()));
				Fieldlanguagesvalues errorValue = fieldManager
						.getFieldByIdAndLanguage(eValues.getFieldvalues_id(),
								language_id);
				Fieldlanguagesvalues typeValue = fieldManager
						.getFieldByIdAndLanguage(errorManagement.getErrorType(eValues.getErrortype_id())
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
