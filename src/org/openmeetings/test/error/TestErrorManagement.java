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
package org.openmeetings.test.error;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.basic.ErrorType;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.LanguageService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestErrorManagement extends AbstractOpenmeetingsSpringTest {
	private static final long START_ERRORVALUES_ID = 666;
	@Autowired
	private LanguageService languageService;
	@Autowired
	private ErrorManagement errorManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;

	private Long getAvailableErrorValuesId() {
		ErrorValues ev = null;
		long result = START_ERRORVALUES_ID;
		while (true) {
			ev = errorManagement.getErrorValuesById(++result);
			System.err.println("result = " + result + "; ev == null ? "
					+ (ev == null));
			if (ev == null) {
				break;
			}
		}
		return result;
	}

	@Test
	public void createErrorValueAndTest() {
		List<ErrorType> types = errorManagement.getErrorTypes();
		List<Fieldlanguagesvalues> flv = fieldmanagment
				.getAllFieldsByLanguage(languageService.getDefaultLanguage()
						.longValue());
		Long errorTypeId = types.get(0).getErrortype_id();
		Long fieldValuesId = flv.get(0).getFieldvalues_id();
		Long errorValuesId = errorManagement.addErrorValues(
				getAvailableErrorValuesId(), errorTypeId, fieldValuesId);
		assertNotNull("Errorvalues Id should persists", errorValuesId);

		ErrorValues ev = errorManagement.getErrorValuesById(errorValuesId);
		assertNotNull("Error type id should not be null", ev.getErrortype_id());
		assertEquals("Error type should persists", errorTypeId,
				ev.getErrortype_id());
		assertNotNull("Fieldvalues should not be null", ev.getFieldvalues_id());
		assertEquals("Fieldvalues should persists", fieldValuesId,
				ev.getFieldvalues_id());
	}
}
