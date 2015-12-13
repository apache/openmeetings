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

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.remote.ErrorService;
import org.openmeetings.app.remote.LanguageService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestErrorService extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private LanguageService languageService;
	@Autowired
	private ErrorService errorService;
	
	@Test
	public void getErrorByCode() {
		for (FieldLanguage lng : languageService.getLanguages()) {
			for (long i = -52; i < 0; ++i) {
				assertNotNull(
					"Not null error result should be returned: i : " + i + "; lng_id = " + lng.getLanguage_id()
					, errorService.getErrorByCode(null, i, lng.getLanguage_id())
					);
			}
		}
	}
}
