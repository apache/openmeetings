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
package org.apache.openmeetings.service.calendar.caldav.handler;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;

/**
 * SyncHandler Interface specifies if the class is Sync Handler.
 * The classes which implement this class can be used to
 * update items depending on the type of syncing necessary.
 * <p>
 * This is a helper class to help Syncing of CalDAV calendars.
 * <p>
 * There are currently three types of Sync Handlers. Namely:
 * <ul>
 *   <li>ETAGs Sync</li>
 *   <li>CTAGs Sync (Similar to ETAGs but an additional step is necessary)</li>
 *   <li>WebDAV-Sync Report</li>
 *   <li>Calendar-Multiget Report</li>
 * </ul>
 */
public interface CalendarHandler {

	/**
	 * Function to update all items in the CalDAV calendar. The owner of the appointments
	 * created are the same as the owner of the calendar.
	 * @return Returns the updated calendar after updation of all events.
	 */
	OmCalendar syncItems();

	/**
	 * Function for create/updating multiple appointment on the server.
	 * Performs modification alongside of creation new events on the server.
	 * @param appointment Appointment to create/update.
	 * @return <code>True</code> when the updation is a success else <code>False</code>
	 */
	boolean updateItem(Appointment appointment);

	/**
	 * Delete Appointment on the server.
	 * @param appointment Appointment to delete
	 * @return <code>True</code> when the deletion is a success else <code>False</code>
	 */
	boolean deleteItem(Appointment appointment);
}
