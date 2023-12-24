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

import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * <code>Subscription</code> represents public representation of the event
 * listener created (or modified) by a successful SUBSCRIBE request.<br>
 * Please note that this interface extends the <code>XmlSerializable</code>
 * interface. The Xml representation of a <code>Subscription</code> is
 * returned in the response to a successful SUBSCRIBE request as well
 * as in a PROPFIND request. In both cases the subscription is packed into
 * a {@link SubscriptionDiscovery} property object.
 */
public interface Subscription extends XmlSerializable {

    /**
     * Returns the id of this subscription, that must be used for un-subscribing
     * as well as for event discovery later on.
     *
     * @return subscriptionId
     */
    public String getSubscriptionId();

    /**
     * @return whether events will be returned with node type information
     */
    public boolean eventsProvideNodeTypeInformation();

    /**
     * @return whether events will be returned with the "noLocal" flag
     */
    public boolean eventsProvideNoLocalFlag();
}