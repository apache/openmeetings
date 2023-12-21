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
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>LabelInfo</code> encapsulates the request body of a LABEL request
 * used to add, set or remove a label from the requested version resource or
 * from that version specified with the Label header in case the requested resource
 * is a version-controlled resource.<br><br>
 * The request body (thus the 'labelElement' passed to the constructor must be
 * a DAV:label element:
 * <pre>
 * &lt;!ELEMENT label ANY&gt;
 * ANY value: A sequence of elements with at most one DAV:add,
 * DAV:set, or DAV:remove element.
 * &lt;!ELEMENT add (label-name)&gt;
 * &lt;!ELEMENT set (label-name)&gt;
 * &lt;!ELEMENT remove (label-name)&gt;
 * &lt;!ELEMENT label-name (#PCDATA)&gt;
 * PCDATA value: string
 * </pre>
 * Please note, that the given implementation only recognizes the predefined elements 'add',
 * 'set' and 'remove'.
 */
public class LabelInfo implements DeltaVConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(LabelInfo.class);

    public static final int TYPE_SET = 0;
    public static final int TYPE_REMOVE = 1;
    public static final int TYPE_ADD = 2;

    public static String[] typeNames = new String[] { XML_LABEL_SET , XML_LABEL_REMOVE, XML_LABEL_ADD};

    private final int depth;
    private final int type;
    private final String labelName;

    public LabelInfo(String labelName, String type) {
        if (labelName == null) {
            throw new IllegalArgumentException("Label name must not be null.");
        }
        boolean validType = false;
        int i = 0;
        while (i < typeNames.length) {
            if (typeNames[i].equals(type)) {
                validType = true;
                break;
            }
            i++;
        }
        if (!validType) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = i;
        this.labelName = labelName;
        this.depth = DavConstants.DEPTH_0;
    }

    public LabelInfo(String labelName, int type) {
        this(labelName, type, DavConstants.DEPTH_0);
    }

    public LabelInfo(String labelName, int type, int depth) {
        if (labelName == null) {
            throw new IllegalArgumentException("Label name must not be null.");
        }
        if (type < TYPE_SET || type > TYPE_ADD) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.labelName = labelName;
        this.type = type;
        this.depth = depth;
    }

    /**
     * Create a new <code>LabelInfo</code> from the given element and depth
     * integer. If the specified Xml element does have a {@link DeltaVConstants#XML_LABEL}
     * root element or no label name is specified with the action to perform
     * the creation will fail.
     *
     * @param labelElement
     * @param depth
     * @throws DavException if the specified element does not
     * start with a {@link DeltaVConstants#XML_LABEL} element or if the DAV:label
     * element contains illegal instructions e.g. contains multiple DAV:add, DAV:set
     * or DAV:remove elements.
     */
    public LabelInfo(Element labelElement, int depth) throws DavException {
        if (!DomUtil.matches(labelElement, DeltaVConstants.XML_LABEL, DeltaVConstants.NAMESPACE)) {
            log.warn("DAV:label element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }

        String label = null;
        int type = -1;
        for (int i = 0; i < typeNames.length && type == -1; i++) {
            if (DomUtil.hasChildElement(labelElement, typeNames[i], NAMESPACE)) {
                type = i;
                Element el = DomUtil.getChildElement(labelElement, typeNames[i], NAMESPACE);
                label = DomUtil.getChildText(el, XML_LABEL_NAME, NAMESPACE);
            }
        }
        if (label == null) {
            log.warn("DAV:label element must contain at least one set, add or remove element defining a label-name.");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        this.labelName = label;
        this.type = type;
        this.depth = depth;
    }

    /**
     * Create a new <code>LabelInfo</code> from the given element. As depth
     * the default value 0 is assumed.
     *
     * @param labelElement
     * @throws DavException
     * @see #LabelInfo(org.w3c.dom.Element, int)
     */
    public LabelInfo(Element labelElement) throws DavException {
        this(labelElement, 0);
    }

    /**
     * Return the text present inside the 'DAV:label-name' element or <code>null</code>
     *
     * @return 'label-name' or <code>null</code>
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * Return the type of the LABEL request. This might either be {@link #TYPE_SET},
     * {@link #TYPE_ADD} or {@link #TYPE_REMOVE}.
     *
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Return the depth
     *
     * @return depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element label = DomUtil.createElement(document, XML_LABEL, NAMESPACE);
        Element typeElem = DomUtil.addChildElement(label, typeNames[type], NAMESPACE);
        DomUtil.addChildElement(typeElem, XML_LABEL_NAME, NAMESPACE, labelName);
        return label;
    }

}