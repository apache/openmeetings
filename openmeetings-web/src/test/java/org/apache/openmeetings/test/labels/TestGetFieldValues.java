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
package org.apache.openmeetings.test.labels;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.label.FieldValueDao;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebawagner
 * 
 */
public class TestGetFieldValues extends AbstractJUnitDefaults {
	@Autowired
	private FieldLanguageDao langDao;
	@Autowired
	private FieldValueDao langValueDao;
	@Autowired
	private FieldLanguagesValuesDao labelDao;

	@Test
	public void testCount() throws Exception {
		int langCount = langDao.get().size();
		assertTrue("Languages should not be empty: " + langCount, langCount > 0);
		Fieldvalues fv = langValueDao.get(1L);
		assertNotNull("Fieldvalues should not be null", fv);
		
		Fieldlanguagesvalues flv = labelDao.get(1L);
		assertNotNull("Fieldlanguagesvalues should not be null", flv);
		assertNotNull("Fieldvalues of Fieldlanguagesvalues should not be null", flv.getFieldvalues());
	}
}
