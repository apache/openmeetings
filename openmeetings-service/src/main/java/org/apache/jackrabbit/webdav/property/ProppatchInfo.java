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

import java.util.List;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProppatchInfo implements XmlSerializable {

    private final List<? extends PropEntry> changeList;
    private final DavPropertySet setProperties;
    private final DavPropertyNameSet removeProperties;

    private final DavPropertyNameSet propertyNames = new DavPropertyNameSet();

    public ProppatchInfo(List<? extends PropEntry> changeList) {
        if (changeList == null || changeList.isEmpty()) {
            throw new IllegalArgumentException("PROPPATCH cannot be executed without properties to be set or removed.");
        }
        this.changeList = changeList;
        this.setProperties = null;
        this.removeProperties = null;
        for (PropEntry entry : changeList) {
            if (entry instanceof DavPropertyName) {
                // DAV:remove
                this.propertyNames.add((DavPropertyName) entry);
            } else if (entry instanceof DavProperty) {
                // DAV:set
                DavProperty<?> setProperty = (DavProperty<?>) entry;
                this.propertyNames.add(setProperty.getName());
            } else {
                throw new IllegalArgumentException("ChangeList may only contain DavPropertyName and DavProperty elements.");
            }
        }
    }

    public ProppatchInfo(DavPropertySet setProperties, DavPropertyNameSet removeProperties) {
        if (setProperties == null || removeProperties == null) {
            throw new IllegalArgumentException("Neither setProperties nor removeProperties must be null.");
        }
        if (setProperties.isEmpty() && removeProperties.isEmpty()) {
            throw new IllegalArgumentException("Either setProperties or removeProperties can be empty; not both of them.");
        }
        this.changeList = null;
        this.setProperties = setProperties;
        this.removeProperties = removeProperties;
        this.propertyNames.addAll(removeProperties);
        for (DavPropertyName setName : setProperties.getPropertyNames()) {
            this.propertyNames.add(setName);
        }
    }

    public DavPropertyNameSet getAffectedProperties() {
        if (this.propertyNames.isEmpty()) {
            throw new IllegalStateException("must be called after toXml()");
        }
        return this.propertyNames;
    }

    @Override
    public Element toXml(Document document) {
        Element proppatch = DomUtil.createElement(document, DavConstants.XML_PROPERTYUPDATE, DavConstants.NAMESPACE);

        if (changeList != null) {
            Element propElement = null;
            boolean isSet = false;
            for (Object entry : changeList) {
                if (entry instanceof DavPropertyName) {
                    // DAV:remove
                    DavPropertyName removeName = (DavPropertyName) entry;
                    if (propElement == null || isSet) {
                        isSet = false;
                        propElement = getPropElement(proppatch, false);
                    }
                    propElement.appendChild(removeName.toXml(document));
                } else if (entry instanceof DavProperty) {
                    // DAV:set
                    DavProperty<?> setProperty = (DavProperty<?>) entry;
                    if (propElement == null || !isSet) {
                        isSet = true;
                        propElement = getPropElement(proppatch, true);
                    }
                    propElement.appendChild(setProperty.toXml(document));
                } else {
                    throw new IllegalArgumentException("ChangeList may only contain DavPropertyName and DavProperty elements.");
                }
            }
        } else {
            // DAV:set
            if (!setProperties.isEmpty()) {
                Element set = DomUtil.addChildElement(proppatch, DavConstants.XML_SET, DavConstants.NAMESPACE);
                set.appendChild(setProperties.toXml(document));
            }
            // DAV:remove
            if (!removeProperties.isEmpty()) {
                Element remove = DomUtil.addChildElement(proppatch, DavConstants.XML_REMOVE, DavConstants.NAMESPACE);
                remove.appendChild(removeProperties.toXml(document));
            }
        }

        return proppatch;
    }

    private Element getPropElement(Element propUpdate, boolean isSet) {
        Element updateEntry = DomUtil.addChildElement(propUpdate, isSet ? DavConstants.XML_SET : DavConstants.XML_REMOVE,
                DavConstants.NAMESPACE);
        return DomUtil.addChildElement(updateEntry, DavConstants.XML_PROP, DavConstants.NAMESPACE);
    }
}
