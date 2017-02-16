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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.db.util.LocaleHelper.getCountries;
import static org.apache.openmeetings.db.util.LocaleHelper.validateCountry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class TestLocaleHelper {
	private static final String COUNTRY_IT_LC = "it";
	private static final String COUNTRY_IT = "IT";
	private static final String COUNTRY_ITALY = "Italy";

	@Test
	public void testCountry() {
		List<String> list = getCountries();
		assertNotNull("Country list shouldn't be null", list);
		assertFalse("Country list shouldn't be empty", list.isEmpty());
		assertEquals("Check for valid", COUNTRY_IT, validateCountry(COUNTRY_IT));
		assertEquals("Check for valid (lower case)", COUNTRY_IT, validateCountry(COUNTRY_IT_LC));
		assertNotEquals("Check for invalid (null)", COUNTRY_IT, validateCountry(null));
		assertNotEquals("Check for invalid", COUNTRY_IT, validateCountry(COUNTRY_ITALY));
	}
}
