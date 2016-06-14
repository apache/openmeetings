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
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.iCalUtils;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarMultiget;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.response.CalendarDataProperty;
import org.osaf.caldav4j.util.CaldavStatus;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class used to sync a given list of hrefs and update or add new Appointments, whenever feasible.
 * This class cannot be used to delete Appointments, which are handled seperately.
 * We use the Calendar-Multiget Report Method to handle this type of query.
 *
 * @see org.apache.openmeetings.service.calendar.caldav.handler.SyncHandler
 */
public class MultigetHandler extends AbstractSyncHandler {
    private static final Logger log = Red5LoggerFactory.getLogger(MultigetHandler.class, webAppRootKey);

    private CalendarMultiget query;
    private boolean isMultigetDisabled = false;
    private iCalUtils utils = new iCalUtils();

    public MultigetHandler(List<String> hrefs, String path, OmCalendar calendar, HttpClient client){
        super(path, calendar, client);

        if(hrefs == null || hrefs.isEmpty() || calendar.getSyncType() == SyncType.NONE)
            isMultigetDisabled =  true;
        else {
            DavPropertyNameSet properties = new DavPropertyNameSet();
            properties.add(DavPropertyName.GETETAG);

            CalendarData calendarData = new CalendarData();
            CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
            vcalendar.addCompFilter(new CompFilter(Component.VEVENT));
            query = new CalendarMultiget(properties, calendarData, false, false);
            query.setHrefs(hrefs);
        }
    }

    public OmCalendar updateItems(Long ownerId){
        if(!isMultigetDisabled){

            CalDAVReportMethod reportMethod = null;

            try {
                reportMethod = new CalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);

                client.executeMethod(reportMethod);

                if (reportMethod.succeeded()) {
                    List<Appointment> origAppointments = appointmentDao.getAppointmentsinCalendar(calendar.getId());
                    List<String> orighrefs = appointmentDao.getAppointmentHrefsinCalendar(calendar.getId());

                    for (MultiStatusResponse response : reportMethod.getResponseBodyAsMultiStatus().getResponses()) {
                        if (response.getStatus()[0].getStatusCode() == CaldavStatus.SC_OK) {
                            int index = orighrefs.indexOf(response.getHref());

                            //Check if it's an updated Appointment
                            if (index != -1) {

                                Appointment a = origAppointments.get(index);
                                String origetag = a.getEtag(),
                                        currentetag = CalendarDataProperty.getEtagfromResponse(response);

                                //If etag is modified
                                if (!origetag.equals(currentetag)) {
                                    Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
                                    a = utils.parseCalendartoAppointment(a, calendar, currentetag);
                                    appointmentDao.update(a, ownerId);
                                }
                            }

                            //Else it's a new Appointment
                            // i.e. parse into a new Appointment
                            else {
                                String etag = CalendarDataProperty.getEtagfromResponse(response);
                                Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
                                Appointment appointments = utils.parseCalendartoAppointment(
                                        ical, response.getHref(), etag, ownerId, calendar);
                                appointmentDao.update(appointments, ownerId);
                            }
                        }
                    }
                }
                else {
                    log.error("Report Method return Status: " + reportMethod.getStatusCode()
                            + " for calId" + calendar.getId());
                }
            } catch (IOException e) {
                log.error("Error parsing the calendar-multiget Report.");
            } catch (DavException e) {
                log.error("Error getting Responses from calendar-multiget REPORT Method");
            } finally {
                if(reportMethod != null)
                    reportMethod.releaseConnection();
            }
        }

        return calendar;
    }

}
