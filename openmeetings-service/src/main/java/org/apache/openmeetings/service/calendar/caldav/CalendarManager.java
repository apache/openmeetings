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
package org.apache.openmeetings.service.calendar.caldav;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.CalendarConstants.Scheme;
import org.apache.openmeetings.service.calendar.caldav.handler.*;
import org.apache.wicket.util.string.Strings;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.methods.PropFindMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which does syncing for one Calendar.
 * TODO: Do all the syncing for all calendars in this.
 */
public class CalendarManager {
    private static final Logger log = Red5LoggerFactory.getLogger(CalendarManager.class, webAppRootKey);

    private String path = null;
    private HostConfiguration hostConfig = null;

    private Scheme scheme = Scheme.BASIC;

    private HttpClient client = null;
    private Long calId;

    @Autowired
    private OmCalendarDao calendarDao;
    @Autowired
    private AppointmentDao appointmentDao;

    /*
    TODO: Modify to use DAO for Calendar instead of Passsword and Username
     i.e. wherever after the creation of a calendar, add a user and Pass, should be sufficient.
      */
    public CalendarManager(Long calId) throws URISyntaxException {
        this.calId = calId;

        URI temp = new URI(calendarDao.getCalendarbyId(calId).getHref());

        path = temp.getPath();

        int port = temp.getPort() != -1 ? temp.getPort() : (temp.getScheme().endsWith("s") ? 443: 80);
        hostConfig = new HostConfiguration();
        hostConfig.setHost(temp.getHost(), port, temp.getScheme());

        initCalendar();
    }

    private HttpClient getHttpClient(){
        if(client != null)
            return client;

        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        int maxHostConnections = 20;
        params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
        connectionManager.setParams(params);
        client = new HttpClient(connectionManager);
        client.setHostConfiguration(hostConfig);
        client.getState().setCredentials(AuthScope.ANY, getCredentials());
        return client;
    }

    private Credentials getCredentials(){
        Credentials credentials = null;
        if(scheme == Scheme.BASIC) {
            credentials = new UsernamePasswordCredentials(calendarDao.getCalendarbyId(calId).getLogin(),
                    calendarDao.getCalendarbyId(calId).getPassword());
        }

        return credentials;
    }

    /**
     * Function which when called performs syncing based on the type of Syncing detected.
     */
    public void updateItems(){
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);

        if(calendar.getSyncType() != SyncType.NONE) {

            SyncHandler syncHandler;

            switch (calendar.getSyncType()) {
                case WEBDAV_SYNC:
                    syncHandler = new WebDAVSyncHandler(path, calendar, getHttpClient(), appointmentDao);
                    break;
                case CTAG:
                    syncHandler = new CtagHandler(path, calendar, getHttpClient(), appointmentDao);
                    break;
                case ETAG:
                default: //Default is the EtagsHandler.
                    syncHandler = new EtagsHandler(path, calendar, getHttpClient(), appointmentDao);
                    break;
            }


            syncHandler.updateItems();
//            calendarDao.update(calendar);
        }
    }

    /**
     * Function to initialize the Calendar on the type of syncing and whether it can be used or not.
     */
    public void initCalendar(){
        HttpClient client =  getHttpClient();
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);
        if(calendar.getToken() == null || calendar.getSyncType() == SyncType.NONE){

            PropFindMethod propFindMethod = null;

            try {
                DavPropertyNameSet properties = new DavPropertyNameSet();
                properties.add(CtagHandler.DNAME_GETCTAG);
                properties.add(WebDAVSyncHandler.DNAME_SYNCTOKEN);

                propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
                client.executeMethod(propFindMethod);

                if(propFindMethod.succeeded()){

                    for(MultiStatusResponse response: propFindMethod.getResponseBodyAsMultiStatus().getResponses()){
                        DavPropertySet set = response.getProperties(CaldavStatus.SC_OK);
                        DavProperty ctag = set.get(CtagHandler.DNAME_GETCTAG),
                            syncToken = set.get(WebDAVSyncHandler.DNAME_SYNCTOKEN);
                        if(syncToken != null){
                            calendar.setSyncType(SyncType.WEBDAV_SYNC);
                        }
                        else if(ctag != null) {
                            calendar.setSyncType(SyncType.CTAG);
                        }
                        else
                            calendar.setSyncType(SyncType.ETAG);
                    }
                } else {
                    log.error("Error executing PROPFIND Method, with status Code: "
                            + propFindMethod.getStatusCode());
                    calendar.setSyncType(SyncType.NONE);
                }

            } catch (Exception e) {
                log.error("Error in doing initial Sync using PROPFIND Method.");
                calendar.setSyncType(SyncType.NONE);
            } finally {
                if(propFindMethod != null)
                    propFindMethod.releaseConnection();
            }
//            calendarDao.update(calendar);
        }
    }

    /**
     * Convenience function to create/update a single Item.
     * @param appointment
     */
    public void updateSingleItem(Appointment appointment){
        this.updateMultipleItems(Collections.singletonList(appointment));
    }

    /**
     * Function for create/updating multiple appointment on the server. Performs modification alongside of creation
     * new events on the server.
     * @param appointments
     */
    public void updateMultipleItems(List<Appointment> appointments){
        //Local Variables
        iCalUtils utils = new iCalUtils();
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        Calendar ical = null;
        List<String> hrefs = new ArrayList<String>();

        //Methods
        PutMethod putMethod = null;
        HttpClient client = getHttpClient();

        //Store new Appointments on the server
        for(Appointment appointment: appointments){

            if(appointment.getCalendar() == null || appointment.getCalendar().getSyncType() == SyncType.NONE)
                continue;

            try {
                ical = utils.parseAppointmenttoCalendar(appointment);

                putMethod = new PutMethod();
                putMethod.setRequestBody(ical);
                putMethod.setCalendarOutputter(calendarOutputter);

                if (Strings.isEmpty(appointment.getHref())) {
                    String temp = path + "/" + appointment.getIcalId() + ".ics";
                    temp = UrlUtils.removeDoubleSlashes(temp);
                    appointment.setHref(temp);
                    putMethod.setPath(temp);
                    putMethod.setIfNoneMatch(true);
                    putMethod.setAllEtags(true);
                } else {
                    putMethod.setPath(appointment.getHref());
                    putMethod.setIfMatch(true);
                    putMethod.addEtag(appointment.getEtag());
                }

                client.executeMethod(putMethod);

                if(putMethod.getStatusCode() == DavServletResponse.SC_CREATED ||
                        putMethod.getStatusCode() == DavServletResponse.SC_NO_CONTENT) {
                    Header etagh = putMethod.getResponseHeader("ETag");
                    if(etagh == null)
                        hrefs.add(appointment.getHref());
                    else
                        appointment.setEtag(etagh.getValue());
                }

            } catch (Exception e) {
                log.error("Unable to store the Appointment on the CalDAV server.");
            } finally {
                if(putMethod != null) {
                    putMethod.releaseConnection();
                    putMethod = null;
                }
            }
        }

        //Get new etags for the ones which didn't return an ETag header
        MultigetHandler multigetHandler = new MultigetHandler(hrefs, true, path,
                calendarDao.getCalendarbyId(calId), client, appointmentDao);
        multigetHandler.updateItems();
    }

    public void deleteSingleItem(Appointment appointment){
        this.deleteMultipleItems(Collections.singletonList(appointment));
    }

    public void deleteMultipleItems(List<Appointment> appointments){

        //HTTPClient Methods
        DeleteMethod deleteMethod = new DeleteMethod("");
        HttpClient client = getHttpClient();

        for(Appointment appointment : appointments){
            if(Strings.isEmpty(appointment.getHref())) return;

            try {
                deleteMethod.setPath(appointment.getHref());
                client.executeMethod(deleteMethod);
                if(deleteMethod.getStatusCode() == DavServletResponse.SC_NO_CONTENT)
                    log.info("Successfully deleted appointment with id: " + appointment.getId());

                //TODO: To correct the User Id.
                appointmentDao.delete(appointment, appointment.getOwner().getId());
            } catch (Exception e) {
                log.error("Unable to execute DELETE Method on: " + appointment.getHref());
            } finally {
                deleteMethod.releaseConnection();
            }
        }
    }


    public static String getTokenfromProperty(DavProperty property){
        if(property == null) return null;
        return property.getValue().toString();
    }
}
