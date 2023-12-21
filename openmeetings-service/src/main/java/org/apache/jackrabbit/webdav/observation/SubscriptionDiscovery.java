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

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.apache.jackrabbit.webdav.DavConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * <code>SubscriptionDiscovery</code> encapsulates the 'subscriptiondiscovery'
 * property of a webdav resource.
 */
public class SubscriptionDiscovery extends AbstractDavProperty<Subscription[]> {

    private final Subscription[] subscriptions;

    /**
     * Create a new <code>SubscriptionDiscovery</code> that lists the given
     * subscriptions.
     *
     * @param subscriptions
     */
    public SubscriptionDiscovery(Subscription[] subscriptions) {
        super(ObservationConstants.SUBSCRIPTIONDISCOVERY, true);
        if (subscriptions != null) {
            this.subscriptions = subscriptions;
        } else {
            this.subscriptions = new Subscription[0];
        }
    }

    /**
     * Create a new <code>SubscriptionDiscovery</code> that contains a single
     * subscription entry.
     *
     * @param subscription
     */
    public SubscriptionDiscovery(Subscription subscription) {
        super(ObservationConstants.SUBSCRIPTIONDISCOVERY, true);
        if (subscription != null) {
            this.subscriptions = new Subscription[]{subscription};
        } else {
            this.subscriptions = new Subscription[0];
        }
    }

    /**
     * Returns an array of {@link Subscription}s.
     *
     * @return an array of {@link Subscription}s
     * @see org.apache.jackrabbit.webdav.property.DavProperty#getValue()
     */
    public Subscription[] getValue() {
        return subscriptions;
    }

    /**
     * Returns the Xml representation of the subscription discovery.
     *
     * @return Xml representation
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    @Override
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);
        for (Subscription subscription : subscriptions) {
            elem.appendChild(subscription.toXml(document));
        }
        return elem;
    }

    //-----------------------------------------------------< static Factory >---
    public static SubscriptionDiscovery createFromXml(Element sDiscoveryElement) {
        if (!DomUtil.matches(sDiscoveryElement, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(), ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace())) {
            throw new IllegalArgumentException("'subscriptiondiscovery' element expected.");
        }

        List<Subscription> subscriptions = new ArrayList<Subscription>();
        ElementIterator it = DomUtil.getChildren(sDiscoveryElement, ObservationConstants.XML_SUBSCRIPTION, ObservationConstants.NAMESPACE);
        while (it.hasNext()) {
            final Element sb = it.nextElement();
            // anonymous inner class: Subscription interface
            Subscription s = new Subscription() {
                /**
                 * @see Subscription#getSubscriptionId()
                 */
                public String getSubscriptionId() {
                    Element ltEl = DomUtil.getChildElement(sb, ObservationConstants.XML_SUBSCRIPTIONID, ObservationConstants.NAMESPACE);
                    if (ltEl != null) {
                        return DomUtil.getChildText(sb, DavConstants.XML_HREF, DavConstants.NAMESPACE);
                    }
                    return null;
                }

                public boolean eventsProvideNodeTypeInformation() {
                    String t = DomUtil.getChildText(sb, ObservationConstants.XML_EVENTSWITHTYPES, ObservationConstants.NAMESPACE);
                    return t == null ? false : Boolean.parseBoolean(t);
                }

                public boolean eventsProvideNoLocalFlag() {
                    String t = DomUtil.getChildText(sb, ObservationConstants.XML_EVENTSWITHLOCALFLAG, ObservationConstants.NAMESPACE);
                    return t == null ? false : Boolean.parseBoolean(t);
                }

                /**
                 * @see XmlSerializable#toXml(Document)
                 */
                public Element toXml(Document document) {
                    return (Element) document.importNode(sb, true);
                }
            };
            subscriptions.add(s);
        }

        return new SubscriptionDiscovery(subscriptions.toArray(new Subscription[subscriptions.size()]));
    }
}