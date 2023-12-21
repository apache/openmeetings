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

/**
 * <code>SubscriptionManager</code> interface.
 */
public interface SubscriptionManager {

    /**
     * Retrieve the {@link org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery} object for the given
     * resource. Note, that the discovery object will be empty if there are
     * no subscriptions present.
     *
     * @param resource
     */
    public SubscriptionDiscovery getSubscriptionDiscovery(ObservationResource resource);

    /**
     * Create a new <code>Subscription</code> or update an existing <code>Subscription</code>..
     *
     * @param info
     * @param subscriptionId
     * @param resource
     * @return <code>Subscription</code> that has been created or updated
     * @throws DavException if the subscription fails
     */
    public Subscription subscribe(SubscriptionInfo info, String subscriptionId,
                                  ObservationResource resource)
            throws DavException;

    /**
     * Unsubscribe the <code>Subscription</code> with the given id.
     *
     * @param subscriptionId
     * @param resource
     * @throws DavException
     */
    public void unsubscribe(String subscriptionId, ObservationResource resource)
            throws DavException;

    /**
     * Retrieve the list of events that occurred since the last poll.
     *
     * @param subscriptionId identifier for the subscription
     * @param timeout        the time in milliseconds to wait at most for events
     *                       if none is present currently.
     * @param resource
     * @return
     */
    public EventDiscovery poll(String subscriptionId, long timeout, ObservationResource resource)
            throws DavException;
}