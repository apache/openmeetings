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

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.IcalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarMultiget;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.model.response.CalendarDataProperty;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

/**
 * Class used to sync a given list of hrefs and update or add new Appointments,
 * whenever feasible. This class cannot be used to update or delete Appointments,
 * which are handled seperately. We use the Calendar-Multiget Report Method to
 * handle this type of query.
 *
 * @see CalendarHandler
 */
public class MultigetHandler extends AbstractCalendarHandler {
	private static final Logger log = LoggerFactory.getLogger(MultigetHandler.class);

	private CalendarMultiget query;
	private boolean isMultigetDisabled = false, onlyEtag = false;

	public MultigetHandler(List<String> hrefs, boolean onlyEtag, String path,
			OmCalendar calendar, HttpClient client, HttpClientContext context, AppointmentDao appointmentDao,
			IcalUtils utils)
	{
		super(path, calendar, client, context, appointmentDao, utils);
		this.onlyEtag = onlyEtag;

		if (hrefs == null || hrefs.isEmpty() || calendar.getSyncType() == SyncType.NONE) {
			isMultigetDisabled = true;
		} else {
			DavPropertyNameSet properties = new DavPropertyNameSet();
			properties.add(DavPropertyName.GETETAG);

			CalendarData calendarData = null;
			if (!onlyEtag) {
				calendarData = new CalendarData();
			}
			CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
			vcalendar.addCompFilter(new CompFilter(Component.VEVENT));
			query = new CalendarMultiget(properties, calendarData, false, false);
			query.setHrefs(hrefs);
		}
	}

	public MultigetHandler(List<String> hrefs, String path, OmCalendar calendar,
			HttpClient client, HttpClientContext context, AppointmentDao appointmentDao, IcalUtils utils)
	{
		this(hrefs, false, path, calendar, client, context, appointmentDao, utils);
	}

	@Override
	BaseDavRequest internalSyncItems() throws IOException, DavException {
		Long ownerId = this.calendar.getOwner().getId();
		if (!isMultigetDisabled) {
			HttpCalDAVReportMethod method = new HttpCalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);

			HttpResponse httpResponse = client.execute(method, context);
			if (method.succeeded(httpResponse)) {
				//Map for each Href as key and Appointment as Value.
				Map<String, Appointment> map = listToMap(appointmentDao.getbyCalendar(calendar.getId()));

				for (MultiStatusResponse response : method.getResponseBodyAsMultiStatus(httpResponse).getResponses()) {
					if (response.getStatus()[0].getStatusCode() == SC_OK) {
						Appointment a = map.get(response.getHref());

						//Check if it's an updated Appointment
						if (a != null) {
							String origetag = a.getEtag(),
									currentetag = CalendarDataProperty.getEtagfromResponse(response);

							//If etag is modified
							if (!currentetag.equals(origetag)) {
								if (onlyEtag) {
									a.setEtag(currentetag);
								} else {
									Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
									a = utils.parseCalendartoAppointment(a, calendar, currentetag);
								}
								appointmentDao.update(a, ownerId);
							}
						} else if (!onlyEtag) {
							//Else it's a new Appointment
							// i.e. parse into a new Appointment
							// Only applicable when we get calendar data along with etag.
							String etag = CalendarDataProperty.getEtagfromResponse(response);
							Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
							Appointment appointments = utils.parseCalendartoAppointment(
									ical, response.getHref(), etag, calendar);
							appointmentDao.update(appointments, ownerId);
						}
					}
				}
			} else {
				log.error("Report Method return Status: {} for calId {}", httpResponse.getStatusLine().getStatusCode(), calendar.getId());
			}
			return method;
		}
		return null;
	}

	// Doesn't handle Creation, Updation and Deletion of events.
	// Don't call these for MultigetHandler.
	@Override
	public boolean updateItem(Appointment appointment) {
		return false;
	}

	@Override
	public boolean deleteItem(Appointment appointment) {
		return false;
	}
}
