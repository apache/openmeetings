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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

/**
 * <code>OptionsInfo</code> represents the Xml request body, that may be present
 * with a OPTIONS request.
 * <br>
 * The DAV:options element is specified to have the following form.
 *
 * <pre>
 * &lt;!ELEMENT options ANY&gt;
 * ANY value: A sequence of elements each at most once.
 * </pre>
 *
 * Note, that this is a simplified implementation of the very generic
 * definition: We assume that the DAV:options element only contains empty child
 * elements, such as e.g. {@link DeltaVConstants#XML_VH_COLLECTION_SET DAV:version-history-collection-set}
 * or {@link DeltaVConstants#XML_WSP_COLLECTION_SET DAV:workspace-collection-set}.
 *
 * @see DeltaVConstants#XML_VH_COLLECTION_SET
 * @see DeltaVConstants#XML_WSP_COLLECTION_SET
 * @see DeltaVConstants#XML_ACTIVITY_COLLECTION_SET
 */
public class OptionsInfo implements XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(OptionsInfo.class);

    private final Set<String> entriesLocalNames = new HashSet<String>();

    /**
     * Create a new OptionsInfo with the specified entries. Each entry will
     * be converted to an empty Xml element when calling <code>toXml</code>.
     * As namespace {@link DeltaVConstants#NAMESPACE} is used.
     *
     * @param entriesLocalNames
     */
    public OptionsInfo(String[] entriesLocalNames) {
       if (entriesLocalNames != null) {
           this.entriesLocalNames.addAll(Arrays.asList(entriesLocalNames));
       }
    }

    /**
     * Private constructor used to create an OptionsInfo from Xml.
     */
    private OptionsInfo() {}

    /**
     * Returns true if a child element with the given name and namespace is present.
     *
     * @param localName
     * @param namespace
     * @return true if such a child element exists in the options element.
     */
    public boolean containsElement(String localName, Namespace namespace) {
        if (DeltaVConstants.NAMESPACE.equals(namespace)) {
            return entriesLocalNames.contains(localName);
        }
        return false;
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        Element optionsElem = DomUtil.createElement(document, DeltaVConstants.XML_OPTIONS, DeltaVConstants.NAMESPACE);
        for (String localName : entriesLocalNames) {
            DomUtil.addChildElement(optionsElem, localName, DeltaVConstants.NAMESPACE);
        }
        return optionsElem;
    }

    /**
     * Build an <code>OptionsInfo</code> object from the root element present
     * in the request body.
     *
     * @param optionsElement
     * @return
     * @throws DavException if the optionsElement is <code>null</code>
     * or not a DAV:options element.
     */
    public static OptionsInfo createFromXml(Element optionsElement) throws DavException {
        if (!DomUtil.matches(optionsElement, DeltaVConstants.XML_OPTIONS, DeltaVConstants.NAMESPACE)) {
            log.warn("DAV:options element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        OptionsInfo oInfo = new OptionsInfo();
        ElementIterator it = DomUtil.getChildren(optionsElement);
        while (it.hasNext()) {
            // todo: not correct since assuming its the deltaV-namespace
            oInfo.entriesLocalNames.add(it.nextElement().getLocalName());
        }
        return oInfo;
    }
}
