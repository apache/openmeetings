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
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>OptionsResponse</code> encapsulates the DAV:options-response element
 * present in the response body of a successful OPTIONS request (with body).
 * <br>
 * The DAV:options-response element is defined to have the following format.
 *
 * <pre>
 * &lt;!ELEMENT options-response ANY&gt;
 * ANY value: A sequence of elements
 * </pre>
 *
 * Please note, that <code>OptionsResponse</code> represents a simplified implementation
 * of the given structure. We assume, that there may only entries that consist
 * of a qualified name and a set of href child elements.
 *
 * @see DeltaVConstants#XML_ACTIVITY_COLLECTION_SET
 * @see DeltaVConstants#XML_VH_COLLECTION_SET
 * @see DeltaVConstants#XML_WSP_COLLECTION_SET
 */
public class OptionsResponse implements DeltaVConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(OptionsResponse.class);

    private final Map<String, Entry> entries = new HashMap<String, Entry>();

    /**
     * Add a new entry to this <code>OptionsResponse</code> and make each
     * href present in the String array being a separate {@link org.apache.jackrabbit.webdav.DavConstants#XML_HREF DAV:href}
     * element within the entry.
     *
     * @param localName
     * @param namespace
     * @param hrefs
     */
    public void addEntry(String localName, Namespace namespace, String[] hrefs) {
        Entry entry = new Entry(localName, namespace, hrefs);
        entries.put(DomUtil.getExpandedName(localName, namespace), entry);
    }

    /**
     *
     * @param localName
     * @param namespace
     * @return
     */
    public String[] getHrefs(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (entries.containsKey(key)) {
            return entries.get(key).hrefs;
        } else {
            return new String[0];
        }
    }

    /**
     * Return the Xml representation.
     *
     * @return Xml representation.
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element optionsResponse = DomUtil.createElement(document, XML_OPTIONS_RESPONSE, NAMESPACE);
        for (Entry entry : entries.values()) {
            Element elem = DomUtil.addChildElement(optionsResponse, entry.localName, entry.namespace);
            for (String href : entry.hrefs) {
                elem.appendChild(DomUtil.hrefToXml(href, document));
            }
        }
        return optionsResponse;
    }

    /**
     * Build a new <code>OptionsResponse</code> object from the given xml element.
     *
     * @param orElem
     * @return a new <code>OptionsResponse</code> object
     * @throws IllegalArgumentException if the specified element is <code>null</code>
     * or if its name is other than 'DAV:options-response'.
     */
    public static OptionsResponse createFromXml(Element orElem) {
        if (!DomUtil.matches(orElem, XML_OPTIONS_RESPONSE, NAMESPACE)) {
            throw new IllegalArgumentException("DAV:options-response element expected");
        }
        OptionsResponse oResponse = new OptionsResponse();
        ElementIterator it = DomUtil.getChildren(orElem);
        while (it.hasNext()) {
            Element el = it.nextElement();
            List<String> hrefs = new ArrayList<String>();
            ElementIterator hrefIt = DomUtil.getChildren(el, DavConstants.XML_HREF, DavConstants.NAMESPACE);
            while (hrefIt.hasNext()) {
                hrefs.add(DomUtil.getTextTrim(hrefIt.nextElement()));
            }
            oResponse.addEntry(el.getLocalName(), DomUtil.getNamespace(el), hrefs.toArray(new String[hrefs.size()]));
        }
        return oResponse;
    }

    private static class Entry {

        private final String localName;
        private final Namespace namespace;
        private final String[] hrefs;

        private Entry(String localName, Namespace namespace, String[] hrefs) {
            this.localName = localName;
            this.namespace = namespace;
            this.hrefs = hrefs;
        }
    }
}
