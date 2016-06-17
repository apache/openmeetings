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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import org.apache.commons.httpclient.HttpClient;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.iCalUtils;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.response.CalendarDataProperty;
import org.osaf.caldav4j.util.CaldavStatus;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

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
public class EtagsHandler extends AbstractSyncHandler{
    private static final Logger log = Red5LoggerFactory.getLogger(EtagsHandler.class, webAppRootKey);

    private iCalUtils utils = new iCalUtils();

    public EtagsHandler(String path, OmCalendar calendar, HttpClient client, AppointmentDao appointmentDao){
        super(path, calendar, client, appointmentDao);
    }

    @Override
    public OmCalendar updateItems() {
        Long ownerId = this.calendar.getOwner().getId();
        List<Appointment> origAppointments = new ArrayList<Appointment>(appointmentDao.getAppointmentsinCalendar(calendar.getId()));

        if(origAppointments.isEmpty()){
            //Initializing the Calendar for the first time.

            DavPropertyNameSet properties = new DavPropertyNameSet();
            properties.add(DavPropertyName.GETETAG);

            CalendarData calendarData = new CalendarData();
            CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
            vcalendar.addCompFilter(new CompFilter(Component.VEVENT));

            CalendarQuery query = new CalendarQuery(properties, vcalendar, calendarData, false, false);

            CalDAVReportMethod reportMethod = null;

            try {
                reportMethod = new CalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);
                client.executeMethod(reportMethod);
                if(reportMethod.succeeded()){
                    MultiStatusResponse[] multiStatusResponses = reportMethod.getResponseBodyAsMultiStatus().getResponses();

                    //Parse the responses into Appointments
                    for(MultiStatusResponse response: multiStatusResponses){
                        if(response.getStatus()[0].getStatusCode() == CaldavStatus.SC_OK) {
                            String etag = CalendarDataProperty.getEtagfromResponse(response);
                            Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
                            Appointment appointments = utils.parseCalendartoAppointment(
                                    ical, response.getHref(), etag, calendar);

                            appointmentDao.update(appointments, ownerId);
                        }
                    }
                }
                else log.error("Report Method return Status: " + reportMethod.getStatusCode()
                        + " for calId" + calendar.getId());

            } catch (IOException e) {
                log.error("Error parsing the calendar-query Report.");
            } catch (DavException e) {
                log.error("Error getting Responses from REPORT with Status Code");
            } finally {
                if(reportMethod != null)
                    reportMethod.releaseConnection();
            }
        }
        else {
            //Calendar has been inited before

            DavPropertyNameSet properties = new DavPropertyNameSet();
            properties.add(DavPropertyName.GETETAG);

            CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
            vcalendar.addCompFilter(new CompFilter(Component.VEVENT));

            CalDAVReportMethod reportMethod = null;

            try {

                CalendarQuery calendarQuery = new CalendarQuery(properties, vcalendar, null, false, false);

                reportMethod = new
                        CalDAVReportMethod(path, calendarQuery, CalDAVConstants.DEPTH_1);
                client.executeMethod(reportMethod);
                if(reportMethod.succeeded()){
                    List<String> currenthrefs = new ArrayList<String>();
                    List<String> orighrefs = new ArrayList<String>(appointmentDao.getAppointmentHrefsinCalendar(calendar.getId()));

                    MultiStatusResponse[] multiStatusResponses = reportMethod.getResponseBodyAsMultiStatus().getResponses();

                    for(MultiStatusResponse response: multiStatusResponses){
                        if(response.getStatus()[0].getStatusCode() == CaldavStatus.SC_OK) {
                            int index = orighrefs.indexOf(response.getHref());

                            //Event updated
                            if (index != -1) {

                                Appointment a = origAppointments.get(index);
                                String origetag = a.getEtag(),
                                        currentetag = CalendarDataProperty.getEtagfromResponse(response);

                                //If etag is modified
                                if (!origetag.equals(currentetag)) {
                                    currenthrefs.add(a.getHref());
                                }
                                origAppointments.remove(index);
                                orighrefs.remove(index);
                            }

                            // The orig list of events doesn't contain this event.
                            else {
                                currenthrefs.add(response.getHref());
                            }
                        }
                    }

                    //Remaining Events have been deleted on the server, thus delete them
                    for(Appointment origAppointment: origAppointments){
                        appointmentDao.delete(origAppointment, ownerId);
                    }

                    //Get the rest of the events through a Multiget Handler.
                    MultigetHandler multigetHandler = new MultigetHandler(currenthrefs, path,
                            calendar, client, appointmentDao);
                    return multigetHandler.updateItems();
                }
                else {
                    log.error("Report Method return Status: " + reportMethod.getStatusCode()
                            + " for calId" + calendar.getId());
                }

            } catch (IOException e) {
                log.error("Error parsing the calendar-query Report.");
            } catch (DavException e) {
                log.error("Error getting Responses from REPORT Method");
            } finally {
                if(reportMethod != null)
                    reportMethod.releaseConnection();
            }
        }

        return calendar;
    }
}
