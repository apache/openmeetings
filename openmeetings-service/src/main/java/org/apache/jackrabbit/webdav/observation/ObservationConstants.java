/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.webdav.observation;

import javax.xml.namespace.QName;

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>ObservationConstants</code> interface provide constants for request
 * and response headers, Xml elements and property names used for handling
 * observation over WebDAV. There exists no public standard for this
 * functionality.
 */
public interface ObservationConstants {

    /**
     * The namespace
     */
    public static final Namespace NAMESPACE = Namespace.getNamespace("dcr", "http://www.day.com/jcr/webdav/1.0");

    //---< Headers >------------------------------------------------------------
    /**
     * The SubscriptionId request header<br>
     */
    public static final String HEADER_SUBSCRIPTIONID = "SubscriptionId";

    /**
     * The PollTimeout request header.
     */
    public static final String HEADER_POLL_TIMEOUT = "PollTimeout";

    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * subscription Xml element<br>
     * Mandatory element inside the {@link #SUBSCRIPTIONDISCOVERY subscriptiondiscovery}
     * property indicating the event listeners present for this session.<br>
     * NOTE, that this will not reveal any subscription made by another session.
     */
    public static final String XML_SUBSCRIPTION = "subscription";

    /**
     * Xml elements
     */
    public static final String XML_SUBSCRIPTIONINFO = "subscriptioninfo";

    public static final String XML_EVENTTYPE = "eventtype";
    public static final String XML_NOLOCAL = "nolocal";
    public static final String XML_FILTER = "filter";
    public static final String XML_SUBSCRIPTIONID = "subscriptionid";
    public static final String XML_EVENTSWITHTYPES = "eventswithnodetypes";
    public static final String XML_EVENTSWITHLOCALFLAG = "eventswithlocalflag";
    public static final String XML_UUID = "uuid";
    public static final String XML_NODETYPE_NAME = "nodetype-name";

    public static final String XML_EVENTDISCOVERY = "eventdiscovery";
    public static final String XML_EVENTBUNDLE = "eventbundle";
    public static final String XML_EVENT_TRANSACTION_ID = "transactionid";
    public static final String XML_EVENT_LOCAL = "local";
    public static final String XML_EVENT = "event";
    public static final String XML_EVENTUSERID = "eventuserid";
    public static final String XML_EVENTUSERDATA = "eventuserdata";
    public static final String XML_EVENTDATE = "eventdate";
    public static final String XML_EVENTIDENTIFIER = "eventidentifier";
    public static final String XML_EVENTINFO = "eventinfo";
    public static final String XML_EVENTPRIMARNODETYPE = "eventprimarynodetype";
    public static final String XML_EVENTMIXINNODETYPE = "eventmixinnodetype";

    public static final QName N_EVENT = new QName(NAMESPACE.getURI(), XML_EVENT);
    public static final QName N_EVENTBUNDLE = new QName(NAMESPACE.getURI(), XML_EVENTBUNDLE);
    public static final QName N_EVENTDATE = new QName(NAMESPACE.getURI(), XML_EVENTDATE);
    public static final QName N_EVENTDISCOVERY = new QName(NAMESPACE.getURI(), XML_EVENTDISCOVERY);
    public static final QName N_EVENTINFO = new QName(NAMESPACE.getURI(), XML_EVENTINFO);
    public static final QName N_EVENTMIXINNODETYPE = new QName(NAMESPACE.getURI(), XML_EVENTMIXINNODETYPE);
    public static final QName N_EVENTPRIMARYNODETYPE = new QName(NAMESPACE.getURI(), XML_EVENTPRIMARNODETYPE);
    public static final QName N_EVENTTYPE = new QName(NAMESPACE.getURI(), XML_EVENTTYPE);
    public static final QName N_EVENTUSERDATA = new QName(NAMESPACE.getURI(), XML_EVENTUSERDATA);
    public static final QName N_EVENTUSERID = new QName(NAMESPACE.getURI(), XML_EVENTUSERID);

    //---< Property Names >-----------------------------------------------------
    /**
     * The protected subscription discovery property is used to find out about
     * existing subscriptions present on the specified resource.
     */
    public static final DavPropertyName SUBSCRIPTIONDISCOVERY = DavPropertyName.create("subscriptiondiscovery", NAMESPACE);
}
