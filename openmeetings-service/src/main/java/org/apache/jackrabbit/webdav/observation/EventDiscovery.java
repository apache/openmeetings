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

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>EventDiscovery</code> represents the request body of a successful
 * POLL request. It reveals all events that occurred since the last POLL. The
 * definition what events that particular subscription is interested in was
 * specified with the initial SUBSCRIPTION that started the event listening.
 */
public class EventDiscovery implements ObservationConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(EventDiscovery.class);

    private final List<EventBundle> bundles = new ArrayList<EventBundle>();

    /**
     * Add the Xml representation of an single 'eventBundle' listing the
     * events that resulted from a change in the server, filtered by the
     * restrictions present in the corresponding subscription.
     *
     * @param eventBundle
     * @see Subscription
     */
    public void addEventBundle(EventBundle eventBundle) {
        if (eventBundle != null) {
            bundles.add(eventBundle);
        }
    }

    /**
     * Returns an iterator over the {@link EventBundle event bundles} currently
     * present on this discovery.
     *
     * @return iterator over event bundles present.
     */
    public Iterator<EventBundle> getEventBundles() {
        return bundles.iterator();
    }

    /**
     * Returns true, if this event discovery does not report any events (thus
     * {@link #getEventBundles()} would return an empty iterator.
     *
     * @return true if {@link #getEventBundles()} would return an empty iterator,
     * false otherwise.
     */
    public boolean isEmpty() {
        return bundles.isEmpty();
    }

    /**
     * Returns the Xml representation of this <code>EventDiscovery</code> as
     * being present in the POLL response body.
     *
     * @return Xml representation
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element ed = DomUtil.createElement(document, XML_EVENTDISCOVERY, NAMESPACE);
        for (EventBundle bundle : bundles) {
            ed.appendChild(bundle.toXml(document));
        }
        return ed;
    }

    /**
     * Build a <code>EventDiscovery</code> from the specified xml element.
     *
     * @param eventDiscoveryElement
     * @return new <code>EventDiscovery</code> instance.
     * @throws IllegalArgumentException if the given document is <code>null</code>
     * or does not provide the required element.
     */
    public static EventDiscovery createFromXml(Element eventDiscoveryElement) {
        if (!DomUtil.matches(eventDiscoveryElement, XML_EVENTDISCOVERY, ObservationConstants.NAMESPACE)) {
            throw new IllegalArgumentException(
                    "{" + ObservationConstants.NAMESPACE + "}" + XML_EVENTDISCOVERY + " element expected, but got: {"
                            + eventDiscoveryElement.getNamespaceURI() + "}" + eventDiscoveryElement.getLocalName());
        }
        EventDiscovery eventDiscovery = new EventDiscovery();
        ElementIterator it = DomUtil.getChildren(eventDiscoveryElement, XML_EVENTBUNDLE, ObservationConstants.NAMESPACE);
        while (it.hasNext()) {
            final Element ebElement = it.nextElement();
            EventBundle eb = new EventBundle() {
                public Element toXml(Document document) {
                    return (Element) document.importNode(ebElement, true);
                }
            };
            eventDiscovery.addEventBundle(eb);
        }
        return eventDiscovery;
    }
}
