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
package org.apache.jackrabbit.webdav.bind;

import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>ParentElement</code> wraps en element of the parent set of a resource. A <code>java.util.Set</code> of
 * <code>ParentElement</code> objects may serve as the value object of the <code>ParentSet</code> DavProperty.
 */
public class ParentElement implements XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(ParentElement.class);

    private final String href;
    private final String segment;

    public ParentElement(String href, String segment) {
        this.href = href;
        this.segment = segment;
    }

    public String getHref() {
        return this.href;
    }

    public String getSegment() {
        return this.segment;
    }

    /**
     * Build an <code>ParentElement</code> object from an XML element DAV:parent
     *
     * @param root the DAV:parent element
     * @return a ParentElement object
     * @throws org.apache.jackrabbit.webdav.DavException if the DAV:parent element is malformed
     */
    public static ParentElement createFromXml(Element root) throws DavException {
        if (!DomUtil.matches(root, BindConstants.XML_PARENT, BindConstants.NAMESPACE)) {
            log.warn("DAV:paret element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        String href = null;
        String segment = null;
        ElementIterator it = DomUtil.getChildren(root);
        while (it.hasNext()) {
            Element elt = it.nextElement();
            if (DomUtil.matches(elt, BindConstants.XML_SEGMENT, BindConstants.NAMESPACE)) {
                if (segment == null) {
                    segment = DomUtil.getText(elt);
                } else {
                    log.warn("unexpected multiple occurrence of DAV:segment element");
                    throw new DavException(DavServletResponse.SC_BAD_REQUEST);
                }
            } else if (DomUtil.matches(elt, BindConstants.XML_HREF, BindConstants.NAMESPACE)) {
                if (href == null) {
                    href = DomUtil.getText(elt);
                } else {
                    log.warn("unexpected multiple occurrence of DAV:href element");
                    throw new DavException(DavServletResponse.SC_BAD_REQUEST);
                }
            } else  {
                log.warn("unexpected element " + elt.getLocalName());
                throw new DavException(DavServletResponse.SC_BAD_REQUEST);
            }
        }
        if (href == null) {
            log.warn("DAV:href element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        if (segment == null) {
            log.warn("DAV:segment element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        return new ParentElement(href, segment);
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(org.w3c.dom.Document)  
     */
    public Element toXml(Document document) {
        Element parentElt = DomUtil.createElement(document, BindConstants.XML_PARENT, BindConstants.NAMESPACE);
        Element hrefElt = DomUtil.createElement(document, BindConstants.XML_HREF, BindConstants.NAMESPACE, this.href);
        Element segElt = DomUtil.createElement(document, BindConstants.XML_SEGMENT, BindConstants.NAMESPACE, this.segment);
        parentElt.appendChild(hrefElt);
        parentElt.appendChild(segElt);
        return parentElt;
    }
}
