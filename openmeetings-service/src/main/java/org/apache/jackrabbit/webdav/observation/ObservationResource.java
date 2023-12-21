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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

/**
 * <code>ObservationResource</code> extends the {@link DavResource} interface by
 * observation relevant METHODS.
 */
public interface ObservationResource extends DavResource {

    public String METHODS = "SUBSCRIBE, UNSUBSCRIBE, POLL";

    /**
     * Initializes this resource.
     *
     * @param subsMgr subscription manager object
     */
    public void init(SubscriptionManager subsMgr);

    /**
     * Subscribe this resource for event listening defined by the specified
     * subscription info. A subscriptionId may be specified in case an existing
     * subscription should be modified.
     *
     * @param info <code>SubscriptionInfo</code> object as defined by the
     * request body and headers.
     * @param subscriptionId or <code>null</code> if the
     * {@link ObservationConstants#HEADER_SUBSCRIPTIONID SubscriptionId} header
     * is missing.
     * @return <code>Subscription</code> object in case the subscription was
     * successful.
     */
    public Subscription subscribe(SubscriptionInfo info, String subscriptionId) throws DavException;

    /**
     * Unsubscribe the event listener with the given SubscriptionId.
     *
     * @param subscriptionId as present in the {@link ObservationConstants#HEADER_SUBSCRIPTIONID
     * SubscriptionId} header.
     */
    public void unsubscribe(String subscriptionId) throws DavException;

    /**
     * Retrieve the list of events that where recorded for the event listener
     * with the given SubscriptionId.
     *
     * @param subscriptionId as present in the
     * {@link ObservationConstants#HEADER_SUBSCRIPTIONID SubscriptionId} header.
     * @param timeout as present in the
     * {@link ObservationConstants#HEADER_POLL_TIMEOUT} header or 0 (zero) if
     * none is present.
     * @return <code>EventDiscovery</code> object
     */
    public EventDiscovery poll(String subscriptionId, long timeout) throws DavException;
}