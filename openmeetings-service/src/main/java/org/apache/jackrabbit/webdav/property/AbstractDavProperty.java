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

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;

/**
 * <code>AbstractDavProperty</code> provides generic METHODS used by various
 * implementations of the {@link DavProperty} interface.
 */
public abstract class AbstractDavProperty<T> implements DavProperty<T> {

    private static Logger log = LoggerFactory.getLogger(AbstractDavProperty.class);

    private final DavPropertyName name;
    private final boolean isInvisibleInAllprop;

    /**
     * Create a new <code>AbstractDavProperty</code> with the given {@link DavPropertyName}
     * and a boolean flag indicating whether this property should be suppressed
     * in PROPFIND/allprop responses.
     */
    public AbstractDavProperty(DavPropertyName name, boolean isInvisibleInAllprop) {
        this.name = name;
        this.isInvisibleInAllprop = isInvisibleInAllprop;
    }

    /**
     * Computes the hash code using this property's name and value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hashCode = getName().hashCode();
        if (getValue() != null) {
            hashCode += getValue().hashCode();
        }
        return hashCode % Integer.MAX_VALUE;
    }

    /**
     * Checks if this property has the same {@link DavPropertyName name}
     * and value as the given one.
     *
     * @param obj the object to compare to
     * @return <code>true</code> if the 2 objects are equal;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DavProperty) {
            DavProperty<?> prop = (DavProperty<?>) obj;
            boolean equalName = getName().equals(prop.getName());
            boolean equalValue = (getValue() == null) ? prop.getValue() == null : getValue().equals(prop.getValue());
            return equalName && equalValue;
        }
        return false;
    }


    /**
     * Return a XML element representation of this property. The value of the
     * property will be added as text or as child element.
     * <pre>
     * new DavProperty("displayname", "WebDAV Directory").toXml
     * gives a element like:
     * &lt;D:displayname&gt;WebDAV Directory&lt;/D:displayname&gt;
     *
     * new DavProperty("resourcetype", new Element("collection")).toXml
     * gives a element like:
     * &lt;D:resourcetype&gt;&lt;D:collection/&gt;&lt;/D:resourcetype&gt;
     *
     * Element[] customVals = { new Element("bla", customNamespace), new Element("bli", customNamespace) };
     * new DavProperty("custom-property", customVals, customNamespace).toXml
     * gives an element like
     * &lt;Z:custom-property&gt;
     *    &lt;Z:bla/&gt;
     *    &lt;Z:bli/&gt;
     * &lt;/Z:custom-property&gt;
     * </pre>
     *
     * @return a XML element of this property
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);
        T value = getValue();
        // todo: improve....
        if (value != null) {
            if (value instanceof XmlSerializable) {
                elem.appendChild(((XmlSerializable)value).toXml(document));
            } else if (value instanceof Node) {
                Node n = document.importNode((Node)value, true);
                elem.appendChild(n);
            } else if (value instanceof Node[]) {
                for (int i = 0; i < ((Node[])value).length; i++) {
                    Node n = document.importNode(((Node[])value)[i], true);
                    elem.appendChild(n);
                }
            } else if (value instanceof Collection) {
                for (Object entry : ((Collection<?>) value)) {
                    if (entry instanceof XmlSerializable) {
                        elem.appendChild(((XmlSerializable) entry).toXml(document));
                    } else if (entry instanceof Node) {
                        Node n = document.importNode((Node) entry, true);
                        elem.appendChild(n);
                    } else {
                        DomUtil.setText(elem, entry.toString());
                    }
                }
            } else {
                DomUtil.setText(elem, value.toString());
            }
        }
        return elem;
    }

    /**
     * Returns the name of this property.
     *
     * @return name
     * @see DavProperty#getName()
     */
    public DavPropertyName getName() {
        return name;
    }

    /**
     * Return <code>true</code> if this property should be suppressed
     * in a PROPFIND/{@link DavConstants#PROPFIND_ALL_PROP DAV:allprop} 
     * response. See RFC 4918, Section 9.1.
     *
     * @see org.apache.jackrabbit.webdav.property.DavProperty#isInvisibleInAllprop()
     */
    public boolean isInvisibleInAllprop() {
        return isInvisibleInAllprop;
    }
}
