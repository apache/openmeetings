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
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

/**
 * <code>Position</code> encapsulates the position in ordering information
 * contained in a Webdav request. This includes both the
 * {@link OrderingConstants#HEADER_POSITION Position header} and the position
 * Xml element present in the request body of an ORDERPATCH request.
 *
 * @see OrderingConstants#HEADER_POSITION
 * @see OrderingConstants#XML_POSITION
 * @see OrderPatch
 */
public class Position implements OrderingConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(Position.class);

    private static final Set<String> VALID_TYPES = new HashSet<String>();
    static {
        VALID_TYPES.add(XML_FIRST);
        VALID_TYPES.add(XML_LAST);
        VALID_TYPES.add(XML_AFTER);
        VALID_TYPES.add(XML_BEFORE);
    }

    private final String type;
    private final String segment;

    /**
     * Create a new <code>Position</code> object with the specified type.
     * Since any type except for {@link #XML_FIRST first} and {@link #XML_LAST last}
     * must be combined with a segment, only the mentioned types are valid
     * arguments.
     *
     * @param type {@link #XML_FIRST first} or {@link #XML_LAST last}
     * @throws IllegalArgumentException if the given type is other than {@link #XML_FIRST}
     * or {@link #XML_LAST}.
     */
    public Position(String type) {
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (!(XML_FIRST.equals(type) || XML_LAST.equals(type))) {
            throw new IllegalArgumentException("If type is other than 'first' or 'last' a segment must be specified");
        }
        this.type = type;
        this.segment = null;
    }

    /**
     * Create a new <code>Position</code> object with the specified type and
     * segment.
     *
     * @param type
     * @param segment
     * @throws IllegalArgumentException if the specified type and segment do not
     * form a valid pair.
     */
    public Position(String type, String segment) {
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if ((XML_AFTER.equals(type) || XML_BEFORE.equals(type)) && (segment == null || "".equals(segment))) {
            throw new IllegalArgumentException("If type is other than 'first' or 'last' a segment must be specified");
        }
        this.type = type;
        this.segment = segment;
    }

    /**
     * Return the type of this <code>Position</code> object, which may be any
     * of the following valid types: {@link #XML_FIRST first},
     * {@link #XML_LAST last}, {@link #XML_AFTER after}, {@link #XML_BEFORE before}
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the segment used to create this <code>Position</code> object or
     * <code>null</code> if no segment is present with the type.
     *
     * @return segment or <code>null</code>
     * @see #getType()
     */
    public String getSegment() {
        return segment;
    }

    //------------------------------------------< XmlSerializable interface >---
    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element positionElement = DomUtil.createElement(document, XML_POSITION, NAMESPACE);
        Element typeElement = DomUtil.addChildElement(positionElement, type, NAMESPACE);
        if (segment != null) {
            DomUtil.addChildElement(typeElement, XML_SEGMENT, NAMESPACE, segment);
        }
        return positionElement;
    }

    //-----------------------------------------------------< static methods >---
    /**
     * Create a new <code>Position</code> object from the specified position
     * element. The element must fulfill the following structure:<br>
     * <pre>
     * &lt;!ELEMENT position (first | last | before | after) &gt;
     * &lt;!ELEMENT segment (#PCDATA) &gt;
     * &lt;!ELEMENT first EMPTY &gt;
     * &lt;!ELEMENT last EMPTY &gt;
     * &lt;!ELEMENT before segment &gt;
     * &lt;!ELEMENT after segment &gt;
     * </pre>
     *
     * @param positionElement Xml element defining the position.
     * @throws IllegalArgumentException if the given Xml element is not valid.
     */
    public static Position createFromXml(Element positionElement) {
        if (!DomUtil.matches(positionElement, XML_POSITION, NAMESPACE)) {
            throw new IllegalArgumentException("The 'DAV:position' element required.");
        }
        ElementIterator it = DomUtil.getChildren(positionElement);
        if (it.hasNext()) {
            Element el = it.nextElement();
            String type = el.getLocalName();
            // read the text of DAV:segment child element inside the type
            String segmentText = DomUtil.getChildText(el, XML_SEGMENT, NAMESPACE);
            // stop after the first iteration
            return new Position(type, segmentText);
        } else {
            throw new IllegalArgumentException("The 'DAV:position' element required with exact one child indicating the type.");
        }
    }
}
