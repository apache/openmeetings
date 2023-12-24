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

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
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
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpDeleteMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.UrlUtils;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

/**
 * Class which handles the Syncing through the use of Etags.
 * First it sends a Calendar-query, and checks all the etags for events.
 * Then, for each href and etag, we get, something like key-value pair.
 * <p>
 * We then check for three things:
 * <ul>
 *   <li>Addition of a new event.</li>
 *   <li>Modification of an existing event.</li>
 *   <li>Deletion of events which are not in the response.</li>
 * </ul>
 */
public class EtagsHandler extends AbstractCalendarHandler {
	private static final Logger log = LoggerFactory.getLogger(EtagsHandler.class);

	/**
	 * @param uri URI to provide the host and scheme
	 * @param path Path to append to host
	 * @return Returns the full path, based on the URI as host and the path provided
	 */
	private static String getFullPath(URI uri, String path) {
		return uri.getScheme() + "://" + uri.getAuthority() + path;
	}

	public EtagsHandler(String path, OmCalendar calendar, HttpClient client,
			HttpClientContext context, AppointmentDao appointmentDao, IcalUtils utils)
	{
		super(path, calendar, client, context, appointmentDao, utils);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	BaseDavRequest internalSyncItems() throws IOException, DavException {
		Long ownerId = this.calendar.getOwner().getId();
		Map<String, Appointment> map = listToMap(appointmentDao.getbyCalendar(calendar.getId()));

		DavPropertyNameSet properties = new DavPropertyNameSet();
		properties.add(DavPropertyName.GETETAG);

		CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
		vcalendar.addCompFilter(new CompFilter(Component.VEVENT));

		CalendarQuery query = new CalendarQuery(properties, vcalendar, new CalendarData(), false, false);
		HttpCalDAVReportMethod method = new HttpCalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);
		HttpResponse httpResponse = client.execute(method, context);
		if (method.succeeded(httpResponse)) {
			MultiStatusResponse[] multiStatusResponses = method.getResponseBodyAsMultiStatus(httpResponse).getResponses();
			if (map.isEmpty()) {
				//Initializing the Calendar for the first time.

				//Parse the responses into Appointments
				for (MultiStatusResponse response : multiStatusResponses) {
					if (response.getStatus()[0].getStatusCode() == SC_OK) {
						String etag = CalendarDataProperty.getEtagfromResponse(response);
						Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
						Appointment appointments = utils.parseCalendartoAppointment(
								ical, response.getHref(), etag, calendar);

						appointmentDao.update(appointments, ownerId);
					}
				}
			} else {
				//Calendar has been inited before

				for (MultiStatusResponse response : multiStatusResponses) {
					if (response.getStatus()[0].getStatusCode() == SC_OK) {
						Appointment appointment = map.get(response.getHref());

						//Event updated
						if (appointment != null) {
							String origetag = appointment.getEtag(),
									currentetag = CalendarDataProperty.getEtagfromResponse(response);

							//If etag is modified
							if (!currentetag.equals(origetag)) {
								Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
								appointment = utils.parseCalendartoAppointment(appointment, calendar, currentetag);
								appointmentDao.update(appointment, ownerId);
							}
							map.remove(response.getHref());
						} else {
							// The orig list of events doesn't contain this event.
							String etag = CalendarDataProperty.getEtagfromResponse(response);
							Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
							Appointment appointments = utils.parseCalendartoAppointment(
									ical, response.getHref(), etag, calendar);

							appointmentDao.update(appointments, ownerId);
						}
					}
				}

				//Remaining Events have been deleted on the server, thus delete them
				for (Map.Entry<String, Appointment> entry : map.entrySet()) {
					appointmentDao.delete(entry.getValue(), ownerId);
				}
			}
		} else {
			log.error("Report Method return Status: {} for calId {} ", httpResponse.getStatusLine().getStatusCode(), calendar.getId());
		}
		return method;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateItem(Appointment appointment) {
		OmCalendar calendar = appointment.getCalendar();
		String href;

		if (calendar != null && calendar.getSyncType() != SyncType.NONE) {

			//Store new Appointment on the server
			HttpPutMethod putMethod = null;
			try {
				List<String> hrefs = null;
				CalendarOutputter calendarOutputter = new CalendarOutputter();

				String temp = null;
				Calendar ical = utils.parseAppointmenttoCalendar(appointment);
				CalendarRequest cr = new CalendarRequest(ical);

				if (Strings.isEmpty(appointment.getHref())) {
					temp = this.path + appointment.getIcalId() + ".ics";
					temp = UrlUtils.removeDoubleSlashes(temp);
					cr.setIfNoneMatch(true);
					cr.setAllEtags(true);
				} else {
					temp = getFullPath(URI.create(this.path), appointment.getHref());
					cr.setIfMatch(true);
					cr.addEtag(appointment.getEtag());
				}

				putMethod = new HttpPutMethod(temp, cr, calendarOutputter);

				HttpResponse httpResponse =  client.execute(putMethod, context);

				if (putMethod.succeeded(httpResponse)) {
					href = putMethod.getURI().getPath(); // Set the href as the path
					appointment.setHref(href);

					//Check if the ETag header was returned.
					Header etagh = putMethod.getFirstHeader("ETag");
					if (etagh == null) {
						hrefs = Collections.singletonList(appointment.getHref());
					} else {
						appointment.setEtag(etagh.getValue());
						appointmentDao.update(appointment, appointment.getOwner().getId());
					}
				} else {
					//Appointment not created on the server
					return false;
				}

				//Get new etags for the ones which didn't return an ETag header
				MultigetHandler multigetHandler = new MultigetHandler(hrefs, true, path,
						calendar, client, context, appointmentDao, utils);
				multigetHandler.syncItems();
				return true;
			} catch (IOException e) {
				log.error("Error executing OptionsMethod during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing OptionsMethod during testConnection.", e);
			} finally {
				releaseConnection(putMethod);
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteItem(Appointment appointment) {

		if (calendar != null && calendar.getSyncType() != SyncType.NONE) {
			HttpDeleteMethod deleteMethod = null;
			try {

				String fullPath;
				if (Strings.isEmpty(appointment.getHref())) {
					// Make sure to set HREF just in case, if calendar exists but no href does.
					fullPath = this.path + appointment.getIcalId() + ".ics";
				} else {
					fullPath = getFullPath(URI.create(this.path), appointment.getHref());
				}

				deleteMethod = new HttpDeleteMethod(fullPath, appointment.getEtag());

				log.info("Deleting at location: {} with ETag: {}", fullPath, appointment.getEtag());

				HttpResponse response = client.execute(deleteMethod, context);

				int status = response.getStatusLine().getStatusCode();
				if (status == SC_NO_CONTENT || status == SC_OK || status == SC_NOT_FOUND) {
					log.info("Successfully deleted appointment with id: {}", appointment.getId());
					return true;
				} else {
					// Appointment Not deleted
				}
			} catch (IOException e) {
				log.error("Error executing OptionsMethod during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing OptionsMethod during testConnection.", e);
			} finally {
				releaseConnection(deleteMethod);
			}
		}
		return false;
	}
}
