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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PropfindInfo implements XmlSerializable {

    private final int propfindType;
    private final DavPropertyNameSet propNameSet;

    public PropfindInfo(int propfindType, DavPropertyNameSet propNameSet) {
        this.propfindType = propfindType;
        this.propNameSet = propNameSet;
    }

    @Override
    public Element toXml(Document document) {
        Element propfind = DomUtil.createElement(document, DavConstants.XML_PROPFIND, DavConstants.NAMESPACE);

        // fill the propfind element
        switch (propfindType) {
            case DavConstants.PROPFIND_ALL_PROP:
                propfind.appendChild(DomUtil.createElement(document, DavConstants.XML_ALLPROP, DavConstants.NAMESPACE));
                break;

            case DavConstants.PROPFIND_PROPERTY_NAMES:
                propfind.appendChild(DomUtil.createElement(document, DavConstants.XML_PROPNAME, DavConstants.NAMESPACE));
                break;

            case DavConstants.PROPFIND_BY_PROPERTY:
                if (propNameSet == null) {
                    // name set missing, ask for a property that is known to
                    // exist
                    Element prop = DomUtil.createElement(document, DavConstants.XML_PROP, DavConstants.NAMESPACE);
                    Element resourcetype = DomUtil.createElement(document, DavConstants.PROPERTY_RESOURCETYPE,
                            DavConstants.NAMESPACE);
                    prop.appendChild(resourcetype);
                    propfind.appendChild(prop);
                } else {
                    propfind.appendChild(propNameSet.toXml(document));
                }
                break;

            case DavConstants.PROPFIND_ALL_PROP_INCLUDE:
                propfind.appendChild(DomUtil.createElement(document, DavConstants.XML_ALLPROP, DavConstants.NAMESPACE));
                if (propNameSet != null && !propNameSet.isEmpty()) {
                    Element include = DomUtil.createElement(document, DavConstants.XML_INCLUDE, DavConstants.NAMESPACE);
                    Element prop = propNameSet.toXml(document);
                    for (Node c = prop.getFirstChild(); c != null; c = c.getNextSibling()) {
                        // copy over the children of <prop> to <include>
                        // element
                        include.appendChild(c.cloneNode(true));
                    }
                    propfind.appendChild(include);
                }
                break;

            default:
                throw new IllegalArgumentException("unknown propfind type");
        }

        return propfind;
    }
}
