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


import org.apache.commons.httpclient.HttpClient;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.methods.SyncMethod;
import org.apache.openmeetings.service.calendar.caldav.methods.SyncReportInfo;
import org.osaf.caldav4j.model.response.CalendarDataProperty;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class used to sync events using WebDAV-Sync defined in RFC 6578.
 * This handles the additional HTTP Status Code 507, which specifies for further sync required.
 * For syncing, it gets a Sync Report as response, which specifies which files have been added,
 * modified or deleted.
 */
public class WebDAVSyncHandler extends AbstractSyncHandler {
    private static final Logger log = Red5LoggerFactory.getLogger(WebDAVSyncHandler.class, webAppRootKey);

    public static final DavPropertyName DNAME_SYNCTOKEN = DavPropertyName.create(SyncReportInfo.XML_SYNC_TOKEN,
            SyncReportInfo.NAMESPACE);

    private List<String> currenthrefs = new ArrayList<>();

    public WebDAVSyncHandler(String path, OmCalendar calendar, HttpClient client){
        super(path, calendar, client);
    }

    public OmCalendar updateItems(Long ownerId){
        boolean additionalSyncNeeded = false;

        SyncMethod syncMethod = null;

        try {
            DavPropertyNameSet properties = new DavPropertyNameSet();
            properties.add(DavPropertyName.GETETAG);

            //Create report to get
            SyncReportInfo reportInfo = new SyncReportInfo(calendar.getToken(), properties,
                    SyncReportInfo.SYNC_LEVEL_1);
            syncMethod = new SyncMethod(path, reportInfo);
            client.executeMethod(syncMethod);

            if(syncMethod.succeeded()){

                //Set the new token
                calendar.setToken(syncMethod.getResponseSynctoken());

                List<Appointment> origAppointments = appointmentDao.getAppointmentsinCalendar(calendar.getId());
                List<String> orighrefs = new ArrayList<>(appointmentDao.getAppointmentHrefsinCalendar(calendar.getId()));

                for(MultiStatusResponse response: syncMethod.getResponseBodyAsMultiStatus().getResponses()){
                    int status = response.getStatus()[0].getStatusCode();
                    if(status == DavServletResponse.SC_OK){
                        int index = orighrefs.indexOf(response.getHref());
                        //Old Event to get
                        if(index != -1){
                            Appointment a = origAppointments.get(index);
                            String origetag = a.getEtag(),
                                    currentetag = CalendarDataProperty.getEtagfromResponse(response);

                            //If event modified, only then get it.
                            if(!origetag.equals(currentetag))
                                currenthrefs.add(response.getHref());
                        }
                        //New Event, to get
                        else
                            currenthrefs.add(response.getHref());
                    }
                    else if(status == DavServletResponse.SC_NOT_FOUND){
                        int index = orighrefs.indexOf(response.getHref());

                        //Only if the event exists on the database, delete it.
                        if(index != -1) {
                            Appointment a = origAppointments.get(index);
                            appointmentDao.delete(a, calendar.getOwner().getId());
                        }
                    }
                    else if (status == DavServletResponse.SC_INSUFFICIENT_SPACE_ON_RESOURCE){
                        additionalSyncNeeded = true;
                    }
                }

                if(!additionalSyncNeeded) {
                    MultigetHandler multigetHandler = new MultigetHandler(currenthrefs, path,
                            calendar, client);
                    return multigetHandler.updateItems(ownerId);
                }
            } else {
                log.error("Error in Sync Method Response with status code" + syncMethod.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error while executing the SyncMethod Report.");
        } finally {
            if(syncMethod != null)
                syncMethod.releaseConnection();
        }

        if(additionalSyncNeeded)
            return updateItems(ownerId);
        else
            return calendar;
    }
}
