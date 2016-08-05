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
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.handler.*;
import org.apache.wicket.util.string.Strings;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.util.UrlUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which does syncing and provides respective API's required for performing CalDAV Operations.
 */
public class AppointmentManager {
    private static final Logger log = Red5LoggerFactory.getLogger(AppointmentManager.class, webAppRootKey);

    private String path = null;

    private HttpClient client;

    @Autowired
    private OmCalendarDao calendarDao;
    @Autowired
    private AppointmentDao appointmentDao;
    @Autowired
    private iCalUtils utils;

    /**
     * Returns the HttpClient which already exists, or creates a new one, if null.
     * @return HttpClient object that was created.
     */
    public void createHttpClient() {
        if(client == null) {
            HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            int maxHostConnections = 6;
            params.setDefaultMaxConnectionsPerHost(maxHostConnections);
            connectionManager.setParams(params);
            client = new HttpClient(connectionManager);
        }
    }

    public void getHttpClient(OmCalendar calendar) {
        createHttpClient();

        URI temp = URI.create(calendar.getHref());
        path = temp.getPath();
        client.getHostConfiguration().setHost(temp.getHost(), temp.getPort(), temp.getScheme());
    }

    private String ensureTrailingSlash(String str){
        if(str.endsWith("/") || str.endsWith("\\"))
            return str;
        else
            return str + "/";
    }

    public void provideCredentials(OmCalendar calendar, Credentials credentials) {
        if(!Strings.isEmpty(calendar.getHref()) && credentials != null){
            createHttpClient();

            URI temp = URI.create(calendar.getHref());
            path = temp.getPath();
            client.getHostConfiguration().setHost(temp.getHost(), temp.getPort(), temp.getScheme());
            client.getState().setCredentials(new AuthScope(temp.getHost(), temp.getPort()),
                    credentials);
        }
    }

    public boolean testConnection(OmCalendar calendar){
        OptionsMethod optionsMethod = null;
        try {
            getHttpClient(calendar);
            optionsMethod = new OptionsMethod(path);
            optionsMethod.setRequestHeader("Accept", "*/*");
            client.executeMethod(optionsMethod);
            int status = optionsMethod.getStatusCode();
            if(status == DavServletResponse.SC_OK || status == DavServletResponse.SC_NO_CONTENT)
                return true;
        } catch (Exception e) {
            log.error("Error executing OptionsMethod during testConnection.");
        } finally {
            if(optionsMethod != null)
                optionsMethod.releaseConnection();
        }
        return false;
    }

    /**
     * Create or Update calendar on the database.
     * @param calendar Calendar to update or create
     */
    public void createCalendar(OmCalendar calendar){
        if (calendar.getId() == null)
            discoverCalendars(calendar);
        else
            calendarDao.update(calendar);
    }

    /**
     * Deletes the calendar from the local database.
     * @param calendar Calendar to delete
     */
    public void deleteCalendar(OmCalendar calendar){
        calendarDao.delete(calendar);
    }

    public List<OmCalendar> getCalendars(){ return calendarDao.get(); }

    public List<OmCalendar> getCalendars(Long userid) { return calendarDao.get(userid); }

    /**
     * Function which when called performs syncing based on the type of Syncing detected.
     * @param calendar Calendar who's sync has to take place
     */
    public void syncItem(OmCalendar calendar) {

        if(calendar.getSyncType() != SyncType.NONE) {

            SyncHandler syncHandler;
            getHttpClient(calendar);

            switch (calendar.getSyncType()) {
                case WEBDAV_SYNC:
                    syncHandler = new WebDAVSyncHandler(path, calendar, client, appointmentDao, utils);
                    break;
                case CTAG:
                    syncHandler = new CtagHandler(path, calendar, client, appointmentDao, utils);
                    break;
                case ETAG:
                default: //Default is the EtagsHandler.
                    syncHandler = new EtagsHandler(path, calendar, client, appointmentDao, utils);
                    break;
            }


            syncHandler.updateItems();
            calendarDao.update(calendar);
        }
    }

    /**
     * Syncs all the calendars currrently present on the DB.
     */
    public void syncItems(Long userId){
        List<OmCalendar> calendars = getCalendars(userId);
        for(OmCalendar calendar : calendars){
            syncItem(calendar);
        }
        cleanupHttpClient();
    }

    /**
     * Function which finds all the calendars of the Principal URL of the calendar
     */
    private void discoverCalendars(OmCalendar calendar) {

        if (calendar.getSyncType() == SyncType.NONE) {
            PropFindMethod propFindMethod = null;
            String userPath = null, homepath = null;

            DavPropertyName curUserPrincipal = DavPropertyName.create("current-user-principal"),
                    calHomeSet = DavPropertyName.create("calendar-home-set", CalDAVConstants.NAMESPACE_CALDAV),
                    suppCalCompSet = DavPropertyName.create("supported-calendar-component-set", CalDAVConstants.NAMESPACE_CALDAV);

            //Find out whether it's a calendar or if we can find the calendar-home or current-user url
            try {
                getHttpClient(calendar);

                DavPropertyNameSet properties = new DavPropertyNameSet();
                properties.add(curUserPrincipal);
                properties.add(calHomeSet);
                properties.add(DavPropertyName.RESOURCETYPE);

                propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
                client.executeMethod(propFindMethod);

                if (propFindMethod.succeeded()) {
                    for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
                        DavPropertySet set = response.getProperties(DavServletResponse.SC_OK);
                        DavProperty calhome = set.get(calHomeSet), curPrinci = set.get(curUserPrincipal),
                                resourcetype = set.get(DavPropertyName.RESOURCETYPE);

                        if (checkCalendarResourceType(resourcetype)) {
                            //This is a calendar and thus initialize and return
                            initCalendar(calendar);
                            return;
                        }

                        //Else find all the calendars on the Principal and return.
                        if (calhome != null) {
                            //Calendar Home Path
                            homepath = getTextValuefromProperty(calhome);

                            break;
                        } else if (curPrinci != null) {

                            //Current User Principal Path
                            userPath = getTextValuefromProperty(curPrinci);
                            break;
                        }
                    }
                } else return;

                if (homepath == null && userPath != null) {

                    //If calendar home path wasn't set, then we get it
                    DavPropertyNameSet props = new DavPropertyNameSet();
                    props.add(calHomeSet);
                    propFindMethod = new PropFindMethod(userPath, props, DavConstants.DEPTH_0);
                    client.executeMethod(propFindMethod);

                    if (propFindMethod.succeeded()) {
                        for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
                            DavPropertySet set = response.getProperties(DavServletResponse.SC_OK);
                            DavProperty calhome = set.get(calHomeSet);

                            if (calhome != null) {
                                homepath = getTextValuefromProperty(calhome);
                                break;
                            }
                        }
                    } else
                        return;
                }

                if (homepath != null) {
                    DavPropertyNameSet props = new DavPropertyNameSet();
                    props.add(DavPropertyName.RESOURCETYPE);
                    props.add(suppCalCompSet);
                    props.add(DavPropertyName.DISPLAYNAME);

                    propFindMethod = new PropFindMethod(homepath, props, DavConstants.DEPTH_1);

                    client.executeMethod(propFindMethod);

                    if (propFindMethod.succeeded()) {
                        for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
                            boolean isVevent = false, isCalendar;

                            DavPropertySet set = response.getProperties(DavServletResponse.SC_OK);
                            DavProperty p = set.get(suppCalCompSet),
                                    resourcetype = set.get(DavPropertyName.RESOURCETYPE),
                                    displayname = set.get(DavPropertyName.DISPLAYNAME);

                            isCalendar = checkCalendarResourceType(resourcetype);

                            if (p != null) {
                                for (Object o : (Collection) p.getValue()) {
                                    if (o instanceof Element) {
                                        Element e = (Element) o;
                                        String name = DomUtil.getAttribute(e, "name", null);
                                        if (name.equals("VEVENT"))
                                            isVevent = true;
                                    }
                                }
                            }

                            if (isCalendar && isVevent) {
                                //Get New Calendar
                                OmCalendar tempCalendar = new OmCalendar();

                                if (displayname != null)
                                    tempCalendar.setTitle(displayname.getValue().toString());

                                tempCalendar.setHref(client.getHostConfiguration().getHostURL() + response.getHref());

                                tempCalendar.setDeleted(false);
                                tempCalendar.setOwner(calendar.getOwner());

                                calendarDao.update(tempCalendar);
                                initCalendar(tempCalendar);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Error executing PROPFIND Method, during Initialization of Calendar.");
                calendar.setSyncType(SyncType.NONE);
            } finally {
                if (propFindMethod != null)
                    propFindMethod.releaseConnection();
                cleanupHttpClient();
            }
        }
    }

    private String getTextValuefromProperty(DavProperty property){
        String value = null;

        if (property != null) {

            for (Object o : (Collection) property.getValue()) {
                if (o instanceof Element) {
                    Element e = (Element) o;
                    value = DomUtil.getTextTrim(e);
                    break;
                }
            }
        }

        return value;
    }

    /**
     * Returns true if the resourcetype Property has a Calendar Element under it.
     * @param resourcetype ResourceType Property
     * @return True if, resource is Calendar, else false.
     */
    private boolean checkCalendarResourceType(DavProperty resourcetype){
        boolean isCalendar = false;

        if(resourcetype != null){
            DavPropertyName calProp = DavPropertyName.create("calendar", CalDAVConstants.NAMESPACE_CALDAV);

            for(Object o : (Collection) resourcetype.getValue()){
                if(o instanceof Element){
                    Element e = (Element) o;
                    if(e.getLocalName().equals(calProp.getName()))
                        isCalendar = true;
                }
            }
        }

        return isCalendar;
    }

    /**
     * Function to initialize the Calendar on the type of syncing and whether it can be used or not.
     */
    private void initCalendar(OmCalendar calendar) {

        if(calendar.getToken() == null || calendar.getSyncType() == SyncType.NONE){
            calendarDao.update(calendar);

            PropFindMethod propFindMethod = null;

            try {
                getHttpClient(calendar);

                DavPropertyNameSet properties = new DavPropertyNameSet();
                properties.add(DavPropertyName.RESOURCETYPE);
                properties.add(DavPropertyName.DISPLAYNAME);
                properties.add(CtagHandler.DNAME_GETCTAG);
                properties.add(WebDAVSyncHandler.DNAME_SYNCTOKEN);

                propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
                client.executeMethod(propFindMethod);

                if(propFindMethod.succeeded()){

                    for(MultiStatusResponse response: propFindMethod.getResponseBodyAsMultiStatus().getResponses()){
                        DavPropertySet set = response.getProperties(DavServletResponse.SC_OK);

                        if(calendar.getTitle() == null) {
                            DavProperty property = set.get(DavPropertyName.DISPLAYNAME);
                            calendar.setTitle(property == null? null : property.getValue().toString());
                        }

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

                    syncItem(calendar);
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
        }
    }

    /**
     * Function for create/updating multiple appointment on the server.
     * Performs modification alongside of creation new events on the server.
     * @param appointment Appointment to create/update.
     */
    public String updateItem(Appointment appointment) {
        //Local Variables
        OmCalendar calendar = appointment.getCalendar();
        String href = null;

        if (calendar != null && calendar.getSyncType() != SyncType.NONE) {

            //Store new Appointment on the server
            PutMethod putMethod = null;
            try {
                getHttpClient(calendar);

                List<String> hrefs = null;
                CalendarOutputter calendarOutputter = new CalendarOutputter();

                Calendar ical = utils.parseAppointmenttoCalendar(appointment);

                putMethod = new PutMethod();
                putMethod.setRequestBody(ical);
                putMethod.setCalendarOutputter(calendarOutputter);

                if (Strings.isEmpty(appointment.getHref())) {
                    String temp = path + appointment.getIcalId() + ".ics";
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

                if (putMethod.getStatusCode() == DavServletResponse.SC_CREATED ||
                        putMethod.getStatusCode() == DavServletResponse.SC_NO_CONTENT) {
                    href = putMethod.getPath();

                    //Check if the ETag header was returned.
                    Header etagh = putMethod.getResponseHeader("ETag");
                    if (etagh == null)
                        hrefs = Collections.singletonList(appointment.getHref());
                    else {
                        appointment.setEtag(etagh.getValue());
                        appointmentDao.update(appointment, appointment.getOwner().getId());
                    }
                }

                //Get new etags for the ones which didn't return an ETag header
                MultigetHandler multigetHandler = new MultigetHandler(hrefs, true, path,
                        calendar, client, appointmentDao, utils);
                multigetHandler.updateItems();

            } catch (Exception e) {
                log.error("Unable to store the Appointment on the CalDAV server.");
            } finally {
                if (putMethod != null)
                    putMethod.releaseConnection();
                cleanupHttpClient();
            }
        }

        return href;
    }

    /**
     * Delete Appointment on the CalDAV server.
     * Delete's on the Server only if the ETag of the Appointment is the one on the server,
     * i.e. only if the Event hasn't changed on the Server.
     * @param appointment Appointment to Delete
     */
    public void deleteItem(Appointment appointment){
        OmCalendar calendar = appointment.getCalendar();

        if (calendar != null && calendar.getSyncType() != SyncType.NONE
                && !Strings.isEmpty(appointment.getHref())) {
            DeleteMethod deleteMethod = null;
            try {
                getHttpClient(calendar);

                deleteMethod = new DeleteMethod(appointment.getHref(), appointment.getEtag());

                log.info("Deleting at location: " + appointment.getHref() + " with ETag: " + appointment.getEtag());

                client.executeMethod(deleteMethod);

                int status = deleteMethod.getStatusCode();
                if(status == DavServletResponse.SC_NO_CONTENT
                        || status == DavServletResponse.SC_OK
                        || status == DavServletResponse.SC_NOT_FOUND)
                    log.info("Successfully deleted appointment with id: " + appointment.getId());

            } catch (Exception e) {
                log.error("Unable to execute DELETE Method on: " + appointment.getHref());
            } finally {
                if(deleteMethod != null)
                    deleteMethod.releaseConnection();
                cleanupHttpClient();
            }
        }
    }

    /**
     * Returns the String value of the property, else null.
     * @param property Property who's string value is to be returned.
     * @return String representation of the Property Value.
     */
    public static String getTokenFromProperty(DavProperty property){
        if(property == null) return null;
        return property.getValue().toString();
    }

    /**
     * Clean up HttpClient session, since we use HttpClient Commons, this method has to be called.
     * Shutsdown the connections and also the MultiThreadedHttpConnectionManager thread, before we remove the reference.
     */
    public void cleanupHttpClient() {
        if (client != null) {
            MultiThreadedHttpConnectionManager.shutdownAll();
            client = null;
        }
    }
}