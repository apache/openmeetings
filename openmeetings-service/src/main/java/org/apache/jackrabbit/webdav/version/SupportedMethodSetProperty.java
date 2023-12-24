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

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The <code>SupportedMethodSetProperty</code>
 */
public class SupportedMethodSetProperty extends AbstractDavProperty<String[]> implements DeltaVConstants {

    private static Logger log = LoggerFactory.getLogger(SupportedMethodSetProperty.class);

    private final String[] methods;

    /**
     * Create a new <code>SupportedMethodSetProperty</code> property.
     *
     * @param methods that are supported by the resource having this property.
     */
    public SupportedMethodSetProperty(String[] methods) {
        super(DeltaVConstants.SUPPORTED_METHOD_SET, true);
        this.methods = methods;
    }

    public String[] getValue() {
        return methods;
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    @Override
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);
        for (String method : methods) {
            Element methodElem = DomUtil.addChildElement(elem, XML_SUPPORTED_METHOD, DeltaVConstants.NAMESPACE);
            DomUtil.setAttribute(methodElem, "name", DeltaVConstants.NAMESPACE, method);
        }
        return elem;
    }

}
