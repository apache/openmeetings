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

import java.util.Collection;

/**
 * <code>PropContainer</code>...
 */
public abstract class PropContainer implements XmlSerializable, DavConstants {

    private static Logger log = LoggerFactory.getLogger(PropContainer.class);

    /**
     * Tries to add the specified object to the <code>PropContainer</code> and
     * returns a boolean indicating whether the content could be added to the
     * internal set/map.
     *
     * @param contentEntry
     * @return true if the object could be added; false otherwise
     * @deprecated Use {@link #addContent(PropEntry)} instead.
     */
	@Deprecated
    public boolean addContent(Object contentEntry) {
        if (contentEntry instanceof PropEntry) {
            return addContent((PropEntry) contentEntry);
        } else {
            return false;
        }
    }

    /**
     * Tries to add the specified entry to the <code>PropContainer</code> and
     * returns a boolean indicating whether the content could be added to the
     * internal set/map.
     *
     * @param contentEntry
     * @return true if the object could be added; false otherwise
     */
    public abstract boolean addContent(PropEntry contentEntry);

    /**
     * Returns true if the PropContainer does not yet contain any content elements.
     *
     * @return true if this container is empty.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the number of property related content elements that are present
     * in this <code>PropContainer</code>.
     *
     * @return number of content elements
     */
    public abstract int getContentSize();

    /**
     * Returns the collection that contains all the content elements of this
     * <code>PropContainer</code>.
     *
     * @return collection representing the contents of this <code>PropContainer</code>.
     */
    public abstract Collection<? extends PropEntry> getContent();

    /**
     * Returns true if this <code>PropContainer</code> contains a content element
     * that matches the given <code>DavPropertyName</code>.
     *
     * @param name
     * @return true if any of the content elements (be it a DavProperty or a
     * DavPropertyName only) matches the given name.
     */
    public abstract boolean contains(DavPropertyName name);

    /**
     * Returns the xml representation of a property related set with the
     * following format:
     * <pre>
     * &lt;!ELEMENT prop (ANY) &gt;
     * where ANY consists of a list of elements each reflecting the xml
     * representation of the entries returned by {@link #getContent()}.
     * </pre>
     *
     * @see XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element prop = DomUtil.createElement(document, XML_PROP, NAMESPACE);
        for (Object content : getContent()) {
            if (content instanceof XmlSerializable) {
                prop.appendChild(((XmlSerializable) content).toXml(document));
            } else {
                log.debug("Unexpected content in PropContainer: should be XmlSerializable.");
            }
        }
        return prop;
    }

}
