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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class UnbindInfo implements XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(UnbindInfo.class);

    private String segment;

    private UnbindInfo() {}

    public UnbindInfo(String segment) {
        this.segment = segment;
    }

    public String getSegment() {
        return this.segment;
    }

    /**
     * Build an <code>UnbindInfo</code> object from the root element present
     * in the request body.
     *
     * @param root the root element of the request body
     * @return a UnbindInfo object containing a segment identifier
     * @throws org.apache.jackrabbit.webdav.DavException if the UNBIND request is malformed
     */
    public static UnbindInfo createFromXml(Element root) throws DavException {
        if (!DomUtil.matches(root, BindConstants.XML_UNBIND, BindConstants.NAMESPACE)) {
            log.warn("DAV:unbind element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
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
            } else  {
                log.warn("unexpected element " + elt.getLocalName());
                throw new DavException(DavServletResponse.SC_BAD_REQUEST);
            }
        }
        if (segment == null) {
            log.warn("DAV:segment element expected");
            throw new DavException(DavServletResponse.SC_BAD_REQUEST);
        }
        return new UnbindInfo(segment);
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(org.w3c.dom.Document)
     */
    public Element toXml(Document document) {
        Element unbindElt = DomUtil.createElement(document, BindConstants.XML_UNBIND, BindConstants.NAMESPACE);
        Element segElt = DomUtil.createElement(document, BindConstants.XML_SEGMENT, BindConstants.NAMESPACE, this.segment);
        unbindElt.appendChild(segElt);
        return unbindElt;
    }
}
