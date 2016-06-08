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

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.CalendarConstants.Scheme;
import org.apache.openmeetings.service.calendar.caldav.handler.CtagHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.EtagsHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.SyncHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.WebDAVSyncHandler;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.PropFindMethod;
import org.osaf.caldav4j.util.CaldavStatus;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

/**
 * Class which does syncing for one Calendar.
 * TODO: Do all the syncing for each class in this. Checkup on Disabling the calendar.
 */
public class CalendarManager {
    private static final Logger log = Red5LoggerFactory.getLogger(CalendarManager.class, webAppRootKey);

    private String path = null;
    private HostConfiguration hostConfig = null;

    private Scheme scheme = Scheme.BASIC;

    private boolean readOnly = false;
    private Long calId;

    @Autowired
    private OmCalendarDao calendarDao;

    /*
    TODO: Modify to use DAO for Calendar instead of Passsword and Username
     i.e. wherever after the creation of a calendar, add a user and Pass, should be sufficient.

     Add OAuth, support.
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

    private HttpClient createHttpClient(){
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        int maxHostConnections = 20;
        params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
        connectionManager.setParams(params);
        HttpClient client = new HttpClient(connectionManager);
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

    public void updateItems(){
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);

        if(calendar.getSyncType() != SyncType.NONE) {

            SyncHandler syncHandler;

            switch (calendar.getSyncType()) {
                case WEBDAV_SYNC:
                    syncHandler = new WebDAVSyncHandler(path, calendar, createHttpClient());
                    break;
                case CTAG:
                    syncHandler = new CtagHandler(path, calendar, createHttpClient());
                    break;
                case ETAG:
                default: //Default is the EtagsHandler.
                    syncHandler = new EtagsHandler(path, calendar, createHttpClient());
                    break;
            }


            calendar = syncHandler.updateItems(calendar.getOwner().getId());
//            calendarDao.update(calendar);
        }
    }

    public void initCalendar(){
        HttpClient client =  createHttpClient();
        OmCalendar calendar = calendarDao.getCalendarbyId(calId);
        if(calendar.getToken() == null || calendar.getSyncType() == SyncType.NONE){
            try {
                DavPropertyNameSet properties = new DavPropertyNameSet();
                properties.add(CtagHandler.DNAME_GETCTAG);
                properties.add(WebDAVSyncHandler.DNAME_SYNCTOKEN);

                PropFindMethod propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
                client.executeMethod(propFindMethod);

                if(propFindMethod.succeeded()){

                    for(MultiStatusResponse response: propFindMethod.getResponseBodyAsMultiStatusResponse()){
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
            }
            calendarDao.update(calendar);
        }
    }

    public static String getTokenfromProperty(DavProperty property){
        return property.getValue().toString().replaceAll("^\"|\"$", "");
    }
}
