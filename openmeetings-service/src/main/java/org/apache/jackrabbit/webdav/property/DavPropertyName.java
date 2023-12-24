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
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>DavPropertyName</code> class reflects a WebDAV property name. It
 * holds together the local name of the property and its namespace.
 */
public class DavPropertyName implements DavConstants, XmlSerializable, PropEntry {

    /** internal 'cache' of created property names */
    private static final Map<Namespace, Map<String, DavPropertyName>> cache = new HashMap<Namespace, Map<String, DavPropertyName>>();

    /* some standard webdav property (that have #PCDATA) */
    public static final DavPropertyName CREATIONDATE = DavPropertyName.create(PROPERTY_CREATIONDATE);
    public static final DavPropertyName DISPLAYNAME = DavPropertyName.create(PROPERTY_DISPLAYNAME);
    public static final DavPropertyName GETCONTENTLANGUAGE = DavPropertyName.create(PROPERTY_GETCONTENTLANGUAGE);
    public static final DavPropertyName GETCONTENTLENGTH = DavPropertyName.create(PROPERTY_GETCONTENTLENGTH);
    public static final DavPropertyName GETCONTENTTYPE = DavPropertyName.create(PROPERTY_GETCONTENTTYPE);
    public static final DavPropertyName GETETAG = DavPropertyName.create(PROPERTY_GETETAG);
    public static final DavPropertyName GETLASTMODIFIED = DavPropertyName.create(PROPERTY_GETLASTMODIFIED);

    /* some standard webdav property (that have other elements) */
    public static final DavPropertyName LOCKDISCOVERY = DavPropertyName.create(PROPERTY_LOCKDISCOVERY);
    public static final DavPropertyName RESOURCETYPE = DavPropertyName.create(PROPERTY_RESOURCETYPE);
    public static final DavPropertyName SOURCE = DavPropertyName.create(PROPERTY_SOURCE);
    public static final DavPropertyName SUPPORTEDLOCK = DavPropertyName.create(PROPERTY_SUPPORTEDLOCK);

    /* property use by microsoft that are not specified in the RFC 2518 */
    public static final DavPropertyName ISCOLLECTION = DavPropertyName.create("iscollection");

    /** the name of the property */
    private final String name;

    /** the namespace of the property */
    private final Namespace namespace;

    /**
     * Creates a new <code>DavPropertyName</code> with the given name and
     * Namespace.
     *
     * @param name The local name of the new property name
     * @param namespace The namespace of the new property name
     *
     * @return The WebDAV property name
     */
    public synchronized static DavPropertyName create(String name, Namespace namespace) {

        // get (or create) map for the given namespace
        Map<String, DavPropertyName> map = cache.get(namespace);
        if (map == null) {
            map = new HashMap<String, DavPropertyName>();
            cache.put(namespace, map);
        }
        // get (or create) property name object
        DavPropertyName ret = map.get(name);
        if (ret == null) {
            if (namespace.equals(NAMESPACE)) {
                // ensure prefix for default 'DAV:' namespace
                namespace = NAMESPACE;
            }
            ret = new DavPropertyName(name, namespace);
            map.put(name, ret);
        }
        return ret;
    }

    /**
     * Creates a new <code>DavPropertyName</code> with the given local name
     * and the default WebDAV {@link DavConstants#NAMESPACE namespace}.
     *
     * @param name The local name of the new property name
     *
     * @return The WebDAV property name
     */
    public synchronized static DavPropertyName create(String name) {
        return create(name, NAMESPACE);
    }

    /**
     * Create a new <code>DavPropertyName</code> with the name and namespace
     * of the given Xml element.
     *
     * @param nameElement
     * @return <code>DavPropertyName</code> instance
     */
    public synchronized static DavPropertyName createFromXml(Element nameElement) {
        if (nameElement == null) {
            throw new IllegalArgumentException("Cannot build DavPropertyName from a 'null' element.");
        }
        String ns = nameElement.getNamespaceURI();
        if (ns == null) {
            return create(nameElement.getLocalName(), Namespace.EMPTY_NAMESPACE);
        } else {
            return create(nameElement.getLocalName(), Namespace.getNamespace(nameElement.getPrefix(), ns));
        }
    }

    /**
     * Creates a new <code>DavPropertyName</code> with the given name and
     * Namespace.
     *
     * @param name The local name of the new property name
     * @param namespace The namespace of the new property name
     */
    private DavPropertyName(String name, Namespace namespace) {
        if (name == null || namespace == null) {
            throw new IllegalArgumentException("Name and namespace must not be 'null' for a DavPropertyName.");
        }
        this.name = name;
        this.namespace = namespace;
    }

    /**
     * Return the name of this <code>DavPropertyName</code>.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the namespace of this <code>DavPropertyName</code>.
     *
     * @return namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * Computes the hash code using this properties name and namespace.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return (name.hashCode() + namespace.hashCode()) % Integer.MAX_VALUE;
    }

    /**
     * Checks if this property has the same name and namespace as the
     * given one.
     *
     * @param obj the object to compare to
     *
     * @return <code>true</code> if the 2 objects are equal;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DavPropertyName) {
            DavPropertyName propName = (DavPropertyName) obj;
            return  name.equals(propName.name) && namespace.equals(propName.namespace);
        }
        return false;
    }

    /**
     * Returns a string representation of this property suitable for debugging
     *
     * @return a human readable string representation
     */
    @Override
    public String toString() {
        return DomUtil.getExpandedName(name, namespace);
    }

    /**
     * Creates a element with the name and namespace of this
     * <code>DavPropertyName</code>.
     *
     * @return A element with the name and namespace of this
     * <code>DavPropertyName</code>.
     * @param document
     */
    public Element toXml(Document document) {
        return DomUtil.createElement(document, name, namespace);
    }
}

