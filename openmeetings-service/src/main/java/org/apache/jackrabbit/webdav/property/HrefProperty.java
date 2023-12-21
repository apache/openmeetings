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
package org.apache.jackrabbit.webdav.property;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>HrefProperty</code> is an extension to the common {@link DavProperty}.
 * The String representation of the property value is always displayed as text
 * inside an extra 'href' element. If the value is a String array each array
 * element is added as text to a separate 'href' element.
 *
 * @see org.apache.jackrabbit.webdav.DavConstants#XML_HREF
 * @see org.apache.jackrabbit.webdav.property.DavProperty#getValue()
 */
public class HrefProperty extends AbstractDavProperty<String[]> {

    private static Logger log = LoggerFactory.getLogger(HrefProperty.class);

    private final String[] value;

    /**
     * Creates a new WebDAV property with the given <code>DavPropertyName</code>
     *
     * @param name the name of the property
     * @param value the value of the property
     * @param isInvisibleInAllprop A value of true, defines this property to be invisible in PROPFIND/allprop
     * It will not be returned in a {@link org.apache.jackrabbit.webdav.DavConstants#PROPFIND_ALL_PROP DAV:allprop}
     * PROPFIND request.
     */
    public HrefProperty(DavPropertyName name, String value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = new String[]{value};
    }

    /**
     * Creates a new WebDAV property with the given <code>DavPropertyName</code>
     *
     * @param name the name of the property
     * @param value the value of the property
     * @param isInvisibleInAllprop A value of true, defines this property to be invisible in PROPFIND/allprop
     * It will not be returned in a {@link org.apache.jackrabbit.webdav.DavConstants#PROPFIND_ALL_PROP DAV:allprop}
     * PROPFIND request.
     */
    public HrefProperty(DavPropertyName name, String[] value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = value;
    }

    /**
     * Create a new <code>HrefProperty</code> from the specified property.
     * Please note, that the property must have a <code>List</code> value
     * object, consisting of {@link #XML_HREF href} <code>Element</code> entries.
     *
     * @param prop
     */
    public HrefProperty(DavProperty<?> prop) {
        super(prop.getName(), prop.isInvisibleInAllprop());
        if (prop instanceof HrefProperty) {
            // already an HrefProperty: no parsing required
            this.value = ((HrefProperty)prop).value;
        } else {
            // assume property has be built from xml
            ArrayList<String> hrefList = new ArrayList<String>();
            Object val = prop.getValue();
            if (val instanceof List) {
                for (Object entry : ((List<?>) val)) {
                    if (entry instanceof Element && XML_HREF.equals(((Element) entry).getLocalName())) {
                        String href = DomUtil.getText((Element) entry);
                        if (href != null) {
                            hrefList.add(href);
                        } else {
                            log.warn("Valid DAV:href element expected instead of " + entry.toString());
                        }
                    } else {
                        log.warn("DAV: href element expected in the content of " + getName().toString());
                    }
                }
            } else if (val instanceof Element && XML_HREF.equals(((Element)val).getLocalName())) {
                String href = DomUtil.getTextTrim((Element)val);
                if (href != null) {
                    hrefList.add(href);
                } else {
                    log.warn("Valid DAV:href element expected instead of " + val.toString());
                }
            }
            value = hrefList.toArray(new String[hrefList.size()]);
        }
    }

    /**
     * Returns an Xml element with the following form:
     * <pre>
     * &lt;Z:name&gt;
     *    &lt;DAV:href&gt;value&lt;/DAV:href/&gt;
     * &lt;/Z:name&gt;
     * </pre>
     * where Z: represents the prefix of the namespace defined with the initial
     * webdav property name.
     *
     * @return Xml representation
     * @see org.apache.jackrabbit.webdav.xml.DomUtil#hrefToXml(String,org.w3c.dom.Document)
     * @param document
     */
    @Override
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);
        String[] value = getValue();
        if (value != null) {
            for (String href : value) {
                elem.appendChild(DomUtil.hrefToXml(href, document));
            }
        }
        return elem;
    }

    /**
     * Returns an array of String.
     *
     * @return an array of String.
     * @see DavProperty#getValue()
     */
    public String[] getValue() {
        return value;
    }

    /**
     * Return an list of String containing the text of those DAV:href elements
     * that would be returned as child elements of this property on
     * {@link org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)}
     *
     * @return list of href String
     */
    public List<String> getHrefs() {
        return (value != null) ? Arrays.asList(value) : new ArrayList<String>();
    }
}
