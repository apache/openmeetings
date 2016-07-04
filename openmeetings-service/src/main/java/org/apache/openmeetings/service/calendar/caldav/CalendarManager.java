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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which does syncing for one Calendar.
 */
public class CalendarManager {
    private static final Logger log = Red5LoggerFactory.getLogger(CalendarManager.class, webAppRootKey);

    private String path = null;

    private HttpClient client = null;

    @Autowired
    private OmCalendarDao calendarDao;
    @Autowired
    private AppointmentDao appointmentDao;

    /**
     * Returns <code>HttpClient </code> which already exists, does not set <code>HostConfiguration</code>.
     * @return HttpClient object that was created.
     */
    private HttpClient getHttpClient(OmCalendar calendar){
        if(client != null)
            return client;

        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        client = new HttpClient(connectionManager);
        client.getState().setCredentials(AuthScope.ANY, getCredentials(calendar));
        return client;
    }

    private HttpClient getHttpClient(String href, OmCalendar calendar) throws URISyntaxException {
        client = getHttpClient(calendar);

        URI temp = new URI(href);
        path = ensureTrailingSlash(temp.getPath());

        int port = temp.getPort() != -1 ? temp.getPort() : (temp.getScheme().endsWith("s") ? 443: 80);
        client.getHostConfiguration().setHost(temp.getHost(), port, temp.getScheme());

        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        int maxHostConnections = 5;
        params.setMaxConnectionsPerHost(client.getHostConfiguration(), maxHostConnections);
        client.getHttpConnectionManager().setParams(params);

        return client;
    }

    private String ensureTrailingSlash(String str){
        if(str.endsWith("/") || str.endsWith("\\"))
            return str;
        else
            return str + "/";
    }

    private Credentials getCredentials(OmCalendar calendar){
        return new UsernamePasswordCredentials(calendar.getLogin(),
                calendar.getPassword());
    }

    /**
     * Create or Update calendar on the database.
     * @param calendar Calendar to update or create
     */
    public void createCalendar(OmCalendar calendar){
        calendarDao.update(calendar);
        discoverCalendars(calendar);
    }

    /**
     * Deletes the calendar from the local database.
     * @param calendar Calendar to delete
     */
    public void deleteCalendar(OmCalendar calendar){
        calendarDao.delete(calendar);
    }

    /**
     * Function which when called performs syncing based on the type of Syncing detected.
     * @param calendar Calendar who's sync has to take place
     */
    public void syncItem(OmCalendar calendar){
        try {
            getHttpClient(calendar.getHref(), calendar);
        } catch (URISyntaxException e){
            log.error("Unable to parse href for Calendar ID: " + calendar.getId());
            return;
        }

        if(calendar.getSyncType() != SyncType.NONE) {

            SyncHandler syncHandler;

            switch (calendar.getSyncType()) {
                case WEBDAV_SYNC:
                    syncHandler = new WebDAVSyncHandler(path, calendar, client, appointmentDao);
                    break;
                case CTAG:
                    syncHandler = new CtagHandler(path, calendar, client, appointmentDao);
                    break;
                case ETAG:
                default: //Default is the EtagsHandler.
                    syncHandler = new EtagsHandler(path, calendar, client, appointmentDao);
                    break;
            }


            syncHandler.updateItems();
            calendarDao.update(calendar);
        }
    }

    /**
     * Syncs all the calendars currrently present on the DB.
     */
    public void syncItems(){
        List<OmCalendar> calendars = calendarDao.get();
        for(OmCalendar calendar : calendars){
            syncItem(calendar);
        }
    }

    /**
     * Function which finds all the calendars of the Principal URL of the calendar
     */
    public void discoverCalendars(OmCalendar calendar) {
        try {
            getHttpClient(calendar.getHref(), calendar);
        } catch (URISyntaxException e){
            log.error("Unable to parse the href of the calendar");
            return;
        }

        if (calendar.getSyncType() == SyncType.NONE) {
            PropFindMethod propFindMethod = null;
            String userPath = null, homepath = null;

            DavPropertyName curUserPrincipal = DavPropertyName.create("current-user-principal"),
                    calHomeSet = DavPropertyName.create("calendar-home-set", CalDAVConstants.NAMESPACE_CALDAV),
                    suppCalCompSet = DavPropertyName.create("supported-calendar-component-set", CalDAVConstants.NAMESPACE_CALDAV);


            //Find out whether it's a calendar or if we can find the calendar-home or current-user url
            try {
                DavPropertyNameSet properties = new DavPropertyNameSet();
                properties.add(curUserPrincipal);
                properties.add(calHomeSet);
                properties.add(DavPropertyName.RESOURCETYPE);

                propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
                client.executeMethod(propFindMethod);

                if(propFindMethod.succeeded()){
                    for(MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()){
                        DavPropertySet set = response.getProperties(DavServletResponse.SC_OK);
                        DavProperty calhome = set.get(calHomeSet), curPrinci = set.get(curUserPrincipal),
                                    resourcetype = set.get(DavPropertyName.RESOURCETYPE);

                        if(checkCalendarResourceType(resourcetype)){
                            //This is a calendar and thus initialize and return
                            initCalendar(calendar);
                            return;
                        }

                        //Else find all the calendars on the Principal and return.
                        if(calhome != null) {
                            //Calendar Home Path
                            for(Object o : (Collection) calhome.getValue()){
                                if(o instanceof Element){
                                    Element e = (Element) o;
                                    homepath = DomUtil.getTextTrim(e);
                                }
                            }

                            break;
                        } else if(curPrinci != null) {

                            //Current User Principal Path
                            for(Object o : (Collection) curPrinci.getValue()){
                                if(o instanceof Element){
                                    Element e = (Element) o;
                                    userPath = DomUtil.getTextTrim(e);
                                }
                            }
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Error executing PROPFIND Method, during Initializtion of Calendar.");
                calendar.setSyncType(SyncType.NONE);
            } finally {
                if(propFindMethod != null)
                    propFindMethod.releaseConnection();
            }

            try{
                if(homepath == null && userPath != null) {

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

                                for (Object o : (Collection) calhome.getValue()) {
                                    if (o instanceof Element) {
                                        Element e = (Element) o;
                                        homepath = DomUtil.getTextTrim(e);
                                    }
                                }

                                break;
                            }
                        }
                    } else
                        return;
                }
            } catch (Exception e) {
                log.error("Error running PROPFIND method.");
            } finally {
                if(propFindMethod != null)
                    propFindMethod.releaseConnection();
            }

            try {
                if(homepath != null) {
                    DavPropertyNameSet props = new DavPropertyNameSet();
                    props.add(DavPropertyName.RESOURCETYPE);
                    props.add(suppCalCompSet);
                    props.add(DavPropertyName.DISPLAYNAME);

                    propFindMethod = new PropFindMethod(homepath, props, DavConstants.DEPTH_1);

                    client.executeMethod(propFindMethod);

                    if (propFindMethod.succeeded()) {
                        for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
                            boolean isVevent = false, isCalendar;

                            DavPropertySet properties = response.getProperties(DavServletResponse.SC_OK);
                            DavProperty p = properties.get(suppCalCompSet),
                                    resourcetype = properties.get(DavPropertyName.RESOURCETYPE),
                                    displayname = properties.get(DavPropertyName.DISPLAYNAME);

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

                                tempCalendar.setLogin(calendar.getLogin());
                                tempCalendar.setPassword(calendar.getPassword());
                                tempCalendar.setDeleted(false);
                                tempCalendar.setOwner(calendar.getOwner());

                                if (!calendar.isDeleted())
                                    calendarDao.delete(calendar);

                                calendarDao.update(tempCalendar);
                                initCalendar(tempCalendar);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Error executing PROPFIND method.");
            } finally {
                if(propFindMethod != null)
                    propFindMethod.releaseConnection();
            }
        }
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
    public void initCalendar(OmCalendar calendar){
        if(calendar.getToken() == null || calendar.getSyncType() == SyncType.NONE){

            PropFindMethod propFindMethod = null;

            try {
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
            calendarDao.update(calendar);
        }
    }

    /**
     * Convenience function to create/update a single Item.
     * @param appointment Appointment to create/update.
     */
    public void updateSingleItem(Appointment appointment, Long calId){
        this.updateMultipleItems(Collections.singletonList(appointment), calId);
    }

    /**
     * Function for create/updating multiple appointment on the server. Performs modification alongside of creation
     * new events on the server.
     * @param appointments List of Appointments to create/update.
     */
    public void updateMultipleItems(List<Appointment> appointments, Long calId){
        //Local Variables
        iCalUtils utils = new iCalUtils();
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        Calendar ical;
        List<String> hrefs = new ArrayList<String>();
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);

        //Methods
        PutMethod putMethod = null;
        try {
            getHttpClient(calendar.getHref(), calendar);
        } catch (URISyntaxException e) {
            log.error("Unable to parse href for calendar");
            return;
        }

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

                if(putMethod.getStatusCode() == DavServletResponse.SC_CREATED ||
                        putMethod.getStatusCode() == DavServletResponse.SC_NO_CONTENT) {
                    //Check if the ETag header was returned.
                    Header etagh = putMethod.getResponseHeader("ETag");
                    if(etagh == null)
                        hrefs.add(appointment.getHref());
                    else
                        appointment.setEtag(etagh.getValue());
                }

                //TODO: check the Owner part.
                appointmentDao.update(appointment, appointment.getOwner().getId());

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

    /**
     * Convenience function to delete single item from the calendar specified.
     * @param appointment Appointment to Delete
     */
    public void deleteSingleItem(Appointment appointment, Long calId){
        this.deleteMultipleItems(Collections.singletonList(appointment), calId);
    }

    /**
     * Delete multiple Appointments from the Calendar specified on the server,
     * as well as on the OM database.
     * @param appointments List of Appointments to delete
     */
    public void deleteMultipleItems(List<Appointment> appointments, Long calId){

        //HTTPClient Methods
        DeleteMethod deleteMethod = new DeleteMethod("");
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);
        try {
            getHttpClient(calendar.getHref(), calendar);
        } catch (URISyntaxException e) {
            log.error("Unable to parse href for calendar.");
            return;
        }

        for(Appointment appointment : appointments){
            if(Strings.isEmpty(appointment.getHref())) return;

            try {
                deleteMethod.setPath(appointment.getHref());
                deleteMethod.setETag(appointment.getEtag());

                log.info("Deleting at location: " + appointment.getHref() + " with ETag: " + appointment.getEtag());

                client.executeMethod(deleteMethod);

                int status = deleteMethod.getStatusCode();
                if(status == DavServletResponse.SC_NO_CONTENT
                   || status == DavServletResponse.SC_OK
                   || status == DavServletResponse.SC_NOT_FOUND)
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

    /**
     * Returns the String value of the property, else null.
     * @param property Property who's string value is to be returned.
     * @return String representation of the Property Value.
     */
    public static String getTokenfromProperty(DavProperty property){
        if(property == null) return null;
        return property.getValue().toString();
    }
}
