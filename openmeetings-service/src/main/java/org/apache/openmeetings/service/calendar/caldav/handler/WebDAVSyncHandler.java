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


import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED;
import static org.apache.jackrabbit.webdav.DavServletResponse.SC_INSUFFICIENT_SPACE_ON_RESOURCE;

import java.io.IOException;
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
import org.apache.openmeetings.service.calendar.caldav.IcalUtils;
import org.apache.openmeetings.service.calendar.caldav.methods.SyncMethod;
import org.apache.openmeetings.service.calendar.caldav.methods.SyncReportInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.model.response.CalendarDataProperty;

import net.fortuna.ical4j.model.Calendar;

/**
 * Class used to sync events using WebDAV-Sync defined in RFC 6578.
 * This handles the additional HTTP Status Code 507, which specifies for further sync required.
 * For syncing, it gets a Sync Report as response, which specifies which files have been added,
 * modified or deleted.
 */
public class WebDAVSyncHandler extends AbstractCalendarHandler {
	private static final Logger log = LoggerFactory.getLogger(WebDAVSyncHandler.class);

	public static final DavPropertyName DNAME_SYNCTOKEN = DavPropertyName.create(SyncReportInfo.XML_SYNC_TOKEN,
			SyncReportInfo.NAMESPACE);

	public WebDAVSyncHandler(String path, OmCalendar calendar, HttpClient client,
			HttpClientContext context, AppointmentDao appointmentDao, IcalUtils utils)
	{
		super(path, calendar, client, context, appointmentDao, utils);
	}

	/**
	 * Sync using WebDAV-Sync.
	 * @throws IOException on error
	 * @throws DavException on error
	 */
	@Override
	BaseDavRequest internalSyncItems() throws IOException, DavException {
		Long ownerId = this.calendar.getOwner().getId();
		boolean additionalSyncNeeded = false;

		DavPropertyNameSet properties = new DavPropertyNameSet();
		properties.add(DavPropertyName.GETETAG);
		properties.add(CalDAVConstants.DNAME_CALENDAR_DATA); // To return Calendar Data.

		//Create report to get
		SyncReportInfo reportInfo = new SyncReportInfo(calendar.getToken(), properties, SyncReportInfo.SYNC_LEVEL_1);
		SyncMethod method = new SyncMethod(path, reportInfo);
		HttpResponse httpResponse = client.execute(method, context);

		if (method.succeeded(httpResponse)) {

			//Map of Href and the Appointments, belonging to it.
			Map<String, Appointment> map = listToMap(appointmentDao.getbyCalendar(calendar.getId()));

			for (MultiStatusResponse response : method.getResponseBodyAsMultiStatus(httpResponse).getResponses()) {
				int status = response.getStatus()[0].getStatusCode();
				if (status == SC_OK) {
					Appointment a = map.get(response.getHref());

					if (a != null) {
						//Old Event to get
						String origetag = a.getEtag(),
								currentetag = CalendarDataProperty
										.getEtagfromResponse(response);

						//If event modified, only then get it.
						if (!currentetag.equals(origetag)) {
							Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
							a = utils.parseCalendartoAppointment(a, calendar, currentetag);
							appointmentDao.update(a, ownerId);
						}
					} else {
						//New Event, to get
						String etag = CalendarDataProperty.getEtagfromResponse(response);
						Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
						Appointment appointments = utils.parseCalendartoAppointment(
								ical, response.getHref(), etag, calendar);
						appointmentDao.update(appointments, ownerId);
					}
				} else if (status == SC_NOT_FOUND) {
					//Delete the Appointments not found on the server.
					Appointment a = map.get(response.getHref());

					//Only if the event exists on the database, delete it.
					if (a != null) {
						appointmentDao.delete(a, calendar.getOwner().getId());
					}
				} else if (status == SC_INSUFFICIENT_SPACE_ON_RESOURCE) {
					additionalSyncNeeded = true;
				}
			}

			//Set the new token
			calendar.setToken(method.getResponseSynctoken(httpResponse));
		} else if (httpResponse.getStatusLine().getStatusCode() == SC_FORBIDDEN
				|| httpResponse.getStatusLine().getStatusCode() == SC_PRECONDITION_FAILED) {

			//Specific case where a server might sometimes forget the sync token
			//Thus requiring a full sync needed to be done.
			log.info("Sync Token not accepted by server. Doing a full sync again.");
			calendar.setToken(null);
			additionalSyncNeeded = true;
		} else {
			log.error("Error in Sync Method Response with status code {}", httpResponse.getStatusLine().getStatusCode());
		}
		if (additionalSyncNeeded) {
			releaseConnection(method);
			return internalSyncItems();
		}
		return method;
	}

	/**
	 * {@inheritDoc}
	 * <br/><br/>
	 * Note: This Uses EtagsHandler for Updating.
	 */
	@Override
	public boolean updateItem(Appointment appointment) {
		EtagsHandler etagsHandler = new EtagsHandler(path, calendar, client, context,
				appointmentDao, utils);
		return etagsHandler.updateItem(appointment);
	}

	/**
	 * {@inheritDoc}
	 * <br/><br/>
	 * Note: This Uses EtagsHandler for Deleting.<br/>
	 */
	@Override
	public boolean deleteItem(Appointment appointment) {
		EtagsHandler etagsHandler = new EtagsHandler(path, calendar, client, context,
				appointmentDao, utils);
		return etagsHandler.deleteItem(appointment);
	}
}
