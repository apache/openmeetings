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
package org.apache.openmeetings.test.calendar;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.calendar.daos.AppointmentCategoryDao;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentCategory;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureCategory extends AbstractOpenmeetingsSpringTest {

	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;

	private static final Logger log = Logger
			.getLogger(TestDatabaseStructureCategory.class);

	@Test
	public void testAddingGroup() {

		try {

			// appointmentCategoryDaoImpl.addAppointmentCategory(2L, "neu2",
			// "test");
			// appointmentCategoryDaoImpl.addAppointmentCategory("dritte");
			// appointmentCategoryDaoImpl.updateAppointmentCategory(2L, "alt");

			List<AppointmentCategory> appointmentCategory = appointmentCategoryDaoImpl
					.getAppointmentCategoryList();
			log.debug("Anzahl: " + appointmentCategory.size());

			for (int x = 0; x < appointmentCategory.size(); x++) {
				log.debug("id: " + appointmentCategory.get(x).getCategoryId());

			}

		} catch (Exception err) {

			log.error("[testAddingGroup]", err);

		}

	}

}
