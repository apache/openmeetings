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
package org.apache.openmeetings.calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOmCalendar extends AbstractJUnitDefaults {
	@Autowired
	private OmCalendarDao calendarDao;

	@Test
	public void saveCalendar() {
		OmCalendar calendar = new OmCalendar();
		Long userId = 1L;
		User owner = userDao.get(userId);
		String title = "Calendar Title", href = "http://caldav.example.com/principals/user";

		calendar.setOwner(owner);
		calendar.setTitle(title);
		calendar.setHref(href);
		calendar.setSyncType(OmCalendar.SyncType.ETAG);

		calendar = calendarDao.update(calendar);

		assertTrue("Saved calendar should have valid id: " + calendar.getId(),
				calendar.getId() != null && calendar.getId() > 0);

		OmCalendar c = calendarDao.get(calendar.getId());
		assertNotNull("Failed to find Calendar by id", c);
	}
}
