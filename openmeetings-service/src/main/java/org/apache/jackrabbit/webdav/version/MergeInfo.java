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
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>MergeInfo</code> encapsulates the information present in the DAV:merge
 * element, that forms the mandatory request body of a MERGE request.<br>
 * The DAV:merge element is specified to have the following form.
 * <pre>
 * &lt;!ELEMENT merge ANY&gt;
 * ANY value: A sequence of elements with one DAV:source element, at most one
 * DAV:no-auto-merge element, at most one DAV:no-checkout element, at most one
 * DAV:prop element, and any legal set of elements that can occur in a DAV:checkout
 * element.
 * &lt;!ELEMENT source (href+)&gt;
 * &lt;!ELEMENT no-auto-merge EMPTY&gt;
 * &lt;!ELEMENT no-checkout EMPTY&gt;
 * prop: see <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518, Section 12.11</a>
 * </pre>
 */
public class MergeInfo implements DeltaVConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(MergeInfo.class);

    private final Element mergeElement;
    private final DavPropertyNameSet propertyNameSet;

    /**
     * Create a new <code>MergeInfo</code>
     *
     * @param mergeElement
     * @throws DavException if the mergeElement is <code>null</code>
     * or not a DAV:merge element.
     */
    public MergeInfo(Element mergeElement) throws DavException {
        if (!DomUtil.matches(mergeElement, XML_MERGE, NAMESPACE)) {
            log.warn("'DAV:merge' element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }

        // if property name set if present
        Element propElem = DomUtil.getChildElement(mergeElement, DavConstants.XML_PROP, DavConstants.NAMESPACE);
        if (propElem != null) {
            propertyNameSet = new DavPropertyNameSet(propElem);
            mergeElement.removeChild(propElem);
        } else {
            propertyNameSet = new DavPropertyNameSet();
        }
        this.mergeElement = mergeElement;
    }

    /**
     * Returns the URL specified with the DAV:source element or <code>null</code>
     * if no such child element is present in the DAV:merge element.
     *
     * @return href present in the DAV:source child element or <code>null</code>.
     */
    public String[] getSourceHrefs() {
        List<String> sourceHrefs = new ArrayList<String>();
        Element srcElem = DomUtil.getChildElement(mergeElement, DavConstants.XML_SOURCE, DavConstants.NAMESPACE);
        if (srcElem != null) {
            ElementIterator it = DomUtil.getChildren(srcElem, DavConstants.XML_HREF, DavConstants.NAMESPACE);
            while (it.hasNext()) {
                String href = DomUtil.getTextTrim(it.nextElement());
                if (href != null) {
                    sourceHrefs.add(href);
                }
            }
        }
        return sourceHrefs.toArray(new String[sourceHrefs.size()]);
    }

    /**
     * Returns true if the DAV:merge element contains a DAV:no-auto-merge child element.
     *
     * @return true if the DAV:merge element contains a DAV:no-auto-merge child.
     */
    public boolean isNoAutoMerge() {
        return DomUtil.hasChildElement(mergeElement, XML_N0_AUTO_MERGE, NAMESPACE);
    }

    /**
     * Returns true if the DAV:merge element contains a DAV:no-checkout child element.
     *
     * @return true if the DAV:merge element contains a DAV:no-checkout child
     */
    public boolean isNoCheckout() {
        return DomUtil.hasChildElement(mergeElement, XML_N0_CHECKOUT, NAMESPACE);
    }

    /**
     * Returns a {@link DavPropertyNameSet}. If the DAV:merge element contains
     * a DAV:prop child element the properties specified therein are included
     * in the set. Otherwise an empty set is returned.<br>
     *
     * <b>WARNING:</b> modifying the DavPropertyNameSet returned by this method does
     * not modify this <code>UpdateInfo</code>.
     *
     * @return set listing the properties specified in the DAV:prop element indicating
     * those properties that must be reported in the response body.
     */
    public DavPropertyNameSet getPropertyNameSet() {
        return propertyNameSet;
    }

    /**
     * Returns the DAV:merge element used to create this <code>MergeInfo</code>
     * object.
     *
     * @return DAV:merge element
     */
    public Element getMergeElement() {
        return mergeElement;
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element elem = (Element)document.importNode(mergeElement, true);
        if (!propertyNameSet.isEmpty()) {
            elem.appendChild(propertyNameSet.toXml(document));
        }
        return elem;
    }


    /**
     * Factory method to create a minimal DAV:merge element to create a new
     * <code>MergeInfo</code> object.
     *
     * @param mergeSource
     * @param isNoAutoMerge
     * @param isNoCheckout
     * @param factory
     * @return
     */
    public static Element createMergeElement(String[] mergeSource, boolean isNoAutoMerge, boolean isNoCheckout, Document factory) {
        Element mergeElem = DomUtil.createElement(factory, XML_MERGE, NAMESPACE);
        Element source = DomUtil.addChildElement(mergeElem, DavConstants.XML_SOURCE, DavConstants.NAMESPACE);
        for (String ms : mergeSource) {
            source.appendChild(DomUtil.hrefToXml(ms, factory));
        }
        if (isNoAutoMerge) {
            DomUtil.addChildElement(mergeElem, XML_N0_AUTO_MERGE, NAMESPACE);
        }
        if (isNoCheckout) {
            DomUtil.addChildElement(mergeElem, XML_N0_CHECKOUT, NAMESPACE);
        }
        return mergeElem;
    }
}
