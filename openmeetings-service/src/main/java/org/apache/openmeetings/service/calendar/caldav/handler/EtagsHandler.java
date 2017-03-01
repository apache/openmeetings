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

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.iCalUtils;
import org.apache.wicket.util.string.Strings;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.response.CalendarDataProperty;
import org.osaf.caldav4j.util.UrlUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

/**
 * Class which handles the Syncing through the use of Etags.
 * First it sends a Calendar-query, and checks all the etags for events.
 * Then, for each href and etag, we get, something like key-value pair.
 * <p>
 * We then check for three things:
 * <li>Addition of a new event.</li>
 * <li>Modification of an existing event.</li>
 * <li>Deletion of events which are not in the response.</li>
 */
public class EtagsHandler extends AbstractCalendarHandler {
	private static final Logger log = Red5LoggerFactory.getLogger(EtagsHandler.class, webAppRootKey);

	public EtagsHandler(String path, OmCalendar calendar, HttpClient client, AppointmentDao appointmentDao, iCalUtils utils) {
		super(path, calendar, client, appointmentDao, utils);
	}

	@Override
	public OmCalendar syncItems() {
		Long ownerId = this.calendar.getOwner().getId();
		Map<String, Appointment> map = listToMap(appointmentDao.getHrefsbyCalendar(calendar.getId()),
				appointmentDao.getbyCalendar(calendar.getId()));

		DavPropertyNameSet properties = new DavPropertyNameSet();
		properties.add(DavPropertyName.GETETAG);

		CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
		vcalendar.addCompFilter(new CompFilter(Component.VEVENT));

		CalDAVReportMethod reportMethod = null;
		try {
			CalendarQuery query = new CalendarQuery(properties, vcalendar, map.isEmpty() ? new CalendarData() : null, false, false);
			reportMethod = new CalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);
			client.executeMethod(reportMethod);
			if (reportMethod.succeeded()) {
				MultiStatusResponse[] multiStatusResponses = reportMethod.getResponseBodyAsMultiStatus().getResponses();
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
					List<String> currenthrefs = new ArrayList<>();

					for (MultiStatusResponse response : multiStatusResponses) {
						if (response.getStatus()[0].getStatusCode() == SC_OK) {
							Appointment appointment = map.get(response.getHref());

							//Event updated
							if (appointment != null) {
								String origetag = appointment.getEtag(),
										currentetag = CalendarDataProperty.getEtagfromResponse(response);

								//If etag is modified
								if (!currentetag.equals(origetag)) {
									currenthrefs.add(appointment.getHref());
								}
								map.remove(response.getHref());
							} else {
								// The orig list of events doesn't contain this event.
								currenthrefs.add(response.getHref());
							}
						}
					}

					//Remaining Events have been deleted on the server, thus delete them
					for (Map.Entry<String, Appointment> entry : map.entrySet()) {
						appointmentDao.delete(entry.getValue(), ownerId);
					}

					//Get the rest of the events through a Multiget Handler.
					MultigetHandler multigetHandler = new MultigetHandler(currenthrefs, path,
							calendar, client, appointmentDao, utils);
					return multigetHandler.syncItems();
				}
			} else {
				log.error("Report Method return Status: {} for calId {} ", reportMethod.getStatusCode(), calendar.getId());
			}
		} catch (IOException | DavException e) {
			log.error("Error during the execution of calendar-multiget Report.", e);
		} catch (Exception e) {
			log.error("Severe Error during the execution of calendar-multiget Report.", e);
		} finally {
			if (reportMethod != null) {
				reportMethod.releaseConnection();
			}
		}

		return calendar;
	}

	@Override
	public boolean updateItem(Appointment appointment) {
		OmCalendar calendar = appointment.getCalendar();
		String href;

		if (calendar != null && calendar.getSyncType() != SyncType.NONE) {

			//Store new Appointment on the server
			PutMethod putMethod = null;
			try {
				List<String> hrefs = null;
				CalendarOutputter calendarOutputter = new CalendarOutputter();

				Calendar ical = utils.parseAppointmenttoCalendar(appointment);

				putMethod = new PutMethod();
				putMethod.setRequestBody(ical);
				putMethod.setCalendarOutputter(calendarOutputter);

				if (Strings.isEmpty(appointment.getHref())) {
					String temp = path + appointment.getIcalId() + ".ics";
					temp = UrlUtils.removeDoubleSlashes(temp);
					putMethod.setPath(temp);
					putMethod.setIfNoneMatch(true);
					putMethod.setAllEtags(true);
				} else {
					putMethod.setPath(appointment.getHref());
					putMethod.setIfMatch(true);
					putMethod.addEtag(appointment.getEtag());
				}

				client.executeMethod(putMethod);

				if (putMethod.getStatusCode() == SC_CREATED ||
						putMethod.getStatusCode() == SC_NO_CONTENT) {
					href = putMethod.getPath();
					appointment.setHref(href);

					//Check if the ETag header was returned.
					Header etagh = putMethod.getResponseHeader("ETag");
					if (etagh == null)
						hrefs = Collections.singletonList(appointment.getHref());
					else {
						appointment.setEtag(etagh.getValue());
						appointmentDao.update(appointment, appointment.getOwner().getId());
					}
				} else {
					//Appointment not created on the server
					return false;
				}

				//Get new etags for the ones which didn't return an ETag header
				MultigetHandler multigetHandler = new MultigetHandler(hrefs, true, path,
						calendar, client, appointmentDao, utils);
				multigetHandler.syncItems();
				return true;
			} catch (IOException e) {
				log.error("Error executing OptionsMethod during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing OptionsMethod during testConnection.", e);
			} finally {
				if (putMethod != null) {
					putMethod.releaseConnection();
				}
			}
		}

		return false;
	}

	/**
	 * @see CalendarHandler#deleteItem(Appointment)
	 */
	@Override
	public boolean deleteItem(Appointment appointment) {

		if (calendar != null && calendar.getSyncType() != SyncType.NONE && !Strings.isEmpty(appointment.getHref())) {
			DeleteMethod deleteMethod = null;
			try {
				deleteMethod = new DeleteMethod(appointment.getHref(), appointment.getEtag());

				log.info("Deleting at location: {} with ETag: {}", appointment.getHref(), appointment.getEtag());

				client.executeMethod(deleteMethod);

				int status = deleteMethod.getStatusCode();
				if (status == SC_NO_CONTENT || status == SC_OK || status == SC_NOT_FOUND) {
					log.info("Successfully deleted appointment with id: " + appointment.getId());
					return true;
				} else {
					// Appointment Not deleted
				}
			} catch (IOException e) {
				log.error("Error executing OptionsMethod during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing OptionsMethod during testConnection.", e);
			} finally {
				if (deleteMethod != null) {
					deleteMethod.releaseConnection();
				}
			}
		}
		return false;
	}
}
