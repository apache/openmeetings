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
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>Filter</code>...
 */
public class Filter implements XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(Filter.class);

    private final String filterName;
    private final Namespace filterNamespace;
    private final String filterValue;

    public Filter(String filterName, Namespace filterNamespace, String filterValue) {
        if (filterName == null) {
            throw new IllegalArgumentException("filterName must not be null.");
        }
        this.filterName = filterName;
        this.filterNamespace = filterNamespace;
        this.filterValue = filterValue;
    }

    public Filter(Element filterElem) {
        filterName = filterElem.getLocalName();
        filterNamespace = DomUtil.getNamespace(filterElem);
        filterValue = DomUtil.getTextTrim(filterElem);
    }

    public String getName() {
        return filterName;
    }

    public Namespace getNamespace() {
        return filterNamespace;
    }

    public String getValue() {
        return filterValue;
    }

    public boolean isMatchingFilter(String localName, Namespace namespace) {
        boolean matchingNsp = (filterNamespace == null) ? namespace == null : filterNamespace.equals(namespace);
        return filterName.equals(localName) && matchingNsp;
    }

    public Element toXml(Document document) {
        return DomUtil.createElement(document, filterName, filterNamespace, filterValue);
    }

}