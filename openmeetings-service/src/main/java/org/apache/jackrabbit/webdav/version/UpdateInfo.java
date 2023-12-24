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
 * <code>UpdateInfo</code> encapsulates the request body of an UPDATE request.
 * RFC 3253 defines the request body as follows:
 * <pre>
 * &lt;!ELEMENT update ANY&gt;
 * ANY value: A sequence of elements with at most one DAV:label-name or
 * DAV:version element (but not both).
 * In addition at one DAV:prop element can be present.
 *
 * &lt;!ELEMENT version (href)&gt;
 * &lt;!ELEMENT label-name (#PCDATA)&gt; PCDATA value: string
 * prop: see RFC 2518, Section 12.11
 * </pre>
 *
 * In order to reflect the complete range of version restoring and updating
 * of nodes defined by JSR170 the definition has been extended:
 * <pre>
 * &lt;!ELEMENT update ( (version | label-name | workspace ) , (prop)?, (removeExisting)? ) &gt;
 * &lt;!ELEMENT version (href+) &gt;
 * &lt;!ELEMENT label-name (#PCDATA) &gt;
 * &lt;!ELEMENT workspace (href) &gt;
 * &lt;!ELEMENT prop ANY &gt;
 * &lt;!ELEMENT removeExisting EMPTY &gt;
 * </pre>
 */
public class UpdateInfo implements DeltaVConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(UpdateInfo.class);

    public static final int UPDATE_BY_VERSION = 0;
    public static final int UPDATE_BY_LABEL = 1;
    public static final int UPDATE_BY_WORKSPACE = 2;

    private Element updateElement;

    private DavPropertyNameSet propertyNameSet = new DavPropertyNameSet();
    private String[] source;
    private int type;

    public UpdateInfo(String[] updateSource, int updateType, DavPropertyNameSet propertyNameSet) {
        if (updateSource == null || updateSource.length == 0) {
            throw new IllegalArgumentException("Version href array must not be null and have a minimal length of 1.");
        }
        if (updateType < UPDATE_BY_VERSION || updateType > UPDATE_BY_WORKSPACE) {
            throw new IllegalArgumentException("Illegal type of UpdateInfo.");
        }
        this.type = updateType;
        this.source = (updateType == UPDATE_BY_VERSION) ? updateSource : new String[] {updateSource[0]};
        if (propertyNameSet != null) {
            this.propertyNameSet = propertyNameSet;
        }
    }

    /**
     * Create a new <code>UpdateInfo</code> object.
     *
     * @param updateElement
     * @throws DavException if the updateElement is <code>null</code>
     * or not a DAV:update element or if the element does not match the required
     * structure.
     */
    public UpdateInfo(Element updateElement) throws DavException {
        if (!DomUtil.matches(updateElement, XML_UPDATE, NAMESPACE)) {
            log.warn("DAV:update element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }

        boolean done = false;
        if (DomUtil.hasChildElement(updateElement, XML_VERSION, NAMESPACE)) {
            Element vEl = DomUtil.getChildElement(updateElement, XML_VERSION, NAMESPACE);
            ElementIterator hrefs = DomUtil.getChildren(vEl, DavConstants.XML_HREF, DavConstants.NAMESPACE);
            List<String> hrefList = new ArrayList<String>();
            while (hrefs.hasNext()) {
                hrefList.add(DomUtil.getText(hrefs.nextElement()));
            }
            source = hrefList.toArray(new String[hrefList.size()]);
            type = UPDATE_BY_VERSION;
            done = true;
        }

        // alternatively 'DAV:label-name' elements may be present.
        if (!done && DomUtil.hasChildElement(updateElement, XML_LABEL_NAME, NAMESPACE)) {
            source = new String[] {DomUtil.getChildText(updateElement, XML_LABEL_NAME, NAMESPACE)};
            type = UPDATE_BY_LABEL;
            done = true;
        }

        // last possibility: a DAV:workspace element
        if (!done) {
            Element wspElem = DomUtil.getChildElement(updateElement, XML_WORKSPACE, NAMESPACE);
            if (wspElem != null) {
                source = new String[] {DomUtil.getChildTextTrim(wspElem, DavConstants.XML_HREF, DavConstants.NAMESPACE)};
                type = UPDATE_BY_WORKSPACE;
            } else {
                log.warn("DAV:update element must contain either DAV:version, DAV:label-name or DAV:workspace child element.");
                throw new DavException(DavServletResponse.SC_BAD_REQUEST);
            }
        }

        // if property name set if present
        if (DomUtil.hasChildElement(updateElement, DavConstants.XML_PROP, DavConstants.NAMESPACE)) {
            Element propEl = DomUtil.getChildElement(updateElement, DavConstants.XML_PROP, DavConstants.NAMESPACE);
            propertyNameSet = new DavPropertyNameSet(propEl);
            updateElement.removeChild(propEl);
        } else {
            propertyNameSet = new DavPropertyNameSet();
        }
        this.updateElement = updateElement;
    }

    /**
     *
     * @return
     */
    public String[] getVersionHref() {
       return (type == UPDATE_BY_VERSION) ? source : null;
    }

    /**
     *
     * @return
     */
    public String[] getLabelName() {
       return (type == UPDATE_BY_LABEL) ? source : null;
    }

    /**
     *
     * @return
     */
    public String getWorkspaceHref() {
       return (type == UPDATE_BY_WORKSPACE) ? source[0] : null;
    }

    /**
     * Returns a {@link DavPropertyNameSet}. If the DAV:update element contains
     * a DAV:prop child element the properties specified therein are included
     * in the set. Otherwise an empty set is returned.
     * <p>
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
     *
     * @return
     */
    public Element getUpdateElement() {
        return updateElement;
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element elem;
        if (updateElement != null) {
            elem = (Element)document.importNode(updateElement, true);
        } else {
            elem = createUpdateElement(source, type, document);
        }
        if (!propertyNameSet.isEmpty()) {
            elem.appendChild(propertyNameSet.toXml(document));
        }
        return elem;
    }

    /**
     * Factory method to create the basic structure of an <code>UpdateInfo</code>
     * object.
     *
     * @param updateSource
     * @param updateType
     * @param factory
     * @return
     */
    public static Element createUpdateElement(String[] updateSource, int updateType, Document factory) {
        if (updateSource == null || updateSource.length == 0) {
            throw new IllegalArgumentException("Update source must specific at least a single resource used to run the update.");
        }

        Element elem = DomUtil.createElement(factory, XML_UPDATE, NAMESPACE);
        switch (updateType) {
            case UPDATE_BY_VERSION:
                Element vE = DomUtil.addChildElement(elem, XML_VERSION, NAMESPACE);
                for (String source : updateSource) {
                    vE.appendChild(DomUtil.hrefToXml(source, factory));
                }
                break;
            case UPDATE_BY_LABEL:
                DomUtil.addChildElement(elem, XML_LABEL_NAME, NAMESPACE, updateSource[0]);
                break;
            case UPDATE_BY_WORKSPACE:
                Element wspEl = DomUtil.addChildElement(elem, XML_WORKSPACE, NAMESPACE, updateSource[0]);
                wspEl.appendChild(DomUtil.hrefToXml(updateSource[0], factory));
                break;
            // no default.
            default:
                throw new IllegalArgumentException("Invalid update type: " + updateType);
        }
        return elem;
    }
}
