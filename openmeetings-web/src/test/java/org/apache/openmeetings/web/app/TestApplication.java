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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setApplicationName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.junit.jupiter.api.Test;

class TestApplication extends AbstractOmServerTest {
	@Test
	void testMissing() {
		assertEquals("[Missing]", app.getOmString("909", Locale.ENGLISH));
	}

	@Test
	void testLblWithApp() {
		final String newAppName = "AAAAA";
		final String prevAppName = getApplicationName();
		try {
			setApplicationName(newAppName);
			List<Locale> locales = LabelDao.getLanguages().stream().map(Entry::getValue).toList();
			for (Locale l : locales) {
				for (String key : Application.STRINGS_WITH_APP) {
					final String str = app.getOmString(key, l);
					assertNotNull(str, "Label with key '" + key + "' for locale " + l + " should exist");
					assertTrue(str.contains(newAppName), "Label with key '" + key + "' for locale " + l + " is invalid: " + str);
				}
			}
		} finally {
			setApplicationName(prevAppName);
		}
	}
}
