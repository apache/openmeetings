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
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private boolean isMultigetDisabled = false, onlyEtag = false;

    public MultigetHandler(List<String> hrefs, boolean onlyEtag, String path, OmCalendar calendar, HttpClient client,
                           AppointmentDao appointmentDao, iCalUtils utils){
        super(path, calendar, client, appointmentDao, utils);
        this.onlyEtag = onlyEtag;

        if(hrefs == null || hrefs.isEmpty() || calendar.getSyncType() == SyncType.NONE)
            isMultigetDisabled =  true;
        else {
            DavPropertyNameSet properties = new DavPropertyNameSet();
            properties.add(DavPropertyName.GETETAG);

            CalendarData calendarData = null;
            if(!onlyEtag)
                calendarData = new CalendarData();
            CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
            vcalendar.addCompFilter(new CompFilter(Component.VEVENT));
            query = new CalendarMultiget(properties, calendarData, false, false);
            query.setHrefs(hrefs);
        }
    }

    public MultigetHandler(List<String> hrefs, String path, OmCalendar calendar, HttpClient client, AppointmentDao appointmentDao,
                           iCalUtils utils){
        this(hrefs, false, path, calendar, client, appointmentDao, utils);
    }

    public OmCalendar updateItems(){
        Long ownerId = this.calendar.getOwner().getId();
        if(!isMultigetDisabled){

            CalDAVReportMethod reportMethod = null;

            try {
                reportMethod = new CalDAVReportMethod(path, query, CalDAVConstants.DEPTH_1);

                client.executeMethod(reportMethod);

                if (reportMethod.succeeded()) {
                    //Map for each Href as key and Appointment as Value.
                    Map<String, Appointment> map = listToMap(appointmentDao.getAppointmentHrefsinCalendar(calendar.getId()),
                                                            appointmentDao.getAppointmentsinCalendar(calendar.getId()));

                    for (MultiStatusResponse response : reportMethod.getResponseBodyAsMultiStatus().getResponses()) {
                        if (response.getStatus()[0].getStatusCode() == DavServletResponse.SC_OK) {
                            Appointment a = map.get(response.getHref());

                            //Check if it's an updated Appointment
                            if (a != null) {
                                String origetag = a.getEtag(),
                                        currentetag = CalendarDataProperty.getEtagfromResponse(response);

                                //If etag is modified
                                if (!currentetag.equals(origetag)) {
                                    if(onlyEtag){
                                        a.setEtag(currentetag);
                                    } else {
                                        Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
                                        a = utils.parseCalendartoAppointment(a, calendar, currentetag);
                                    }
                                    appointmentDao.update(a, ownerId);
                                }
                            }

                            //Else it's a new Appointment
                            // i.e. parse into a new Appointment
                            // Only applicable when we get calendar data along with etag.
                            else if(!onlyEtag) {
                                String etag = CalendarDataProperty.getEtagfromResponse(response);
                                Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);
                                Appointment appointments = utils.parseCalendartoAppointment(
                                        ical, response.getHref(), etag, calendar);
                                appointmentDao.update(appointments, ownerId);
                            }
                        }
                    }
                }
                else {
                    log.error("Report Method return Status: " + reportMethod.getStatusCode()
                            + " for calId" + calendar.getId());
                }
            } catch (IOException | DavException e) {
                log.error("Error during the execution of calendar-multiget Report.");
            } catch (Exception e) {
                log.error("Severe Error during the execution of calendar-multiget Report.");
            } finally {
                if(reportMethod != null)
                    reportMethod.releaseConnection();
            }
        }

        return calendar;
    }

}
