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
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Type</code> class encapsulates the lock type as defined by RFC 2518.
 */
public class Type implements XmlSerializable {

    private static Map<String, Type> types = new HashMap<String, Type>();

    public static final Type WRITE = Type.create(DavConstants.XML_WRITE, DavConstants.NAMESPACE);

    private final String localName;
    private final Namespace namespace;

    private int hashCode = -1;

    /**
     * Private constructor.
     *
     * @param name
     * @param namespace
     */
    private Type(String name, Namespace namespace) {
        this.localName = name;
        this.namespace = namespace;
    }

    /**
     * Returns the Xml representation of this lock <code>Type</code>.
     *
     * @return Xml representation
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element lockType = DomUtil.createElement(document, DavConstants.XML_LOCKTYPE, DavConstants.NAMESPACE);
        DomUtil.addChildElement(lockType, localName, namespace);
        return lockType;
    }

    @Override
    public int hashCode() {
        if (hashCode == -1) {
            StringBuilder b = new StringBuilder();
            b.append("LockType : {").append(namespace).append("}").append(localName);
            hashCode = b.toString().hashCode();         
        }
        return hashCode;
    }

    /**
     * Returns <code>true</code> if this Type is equal to the given one.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Type) {
            Type other = (Type) obj;
            return localName.equals(other.localName) && namespace.equals(other.namespace);
        }
        return false;
    }

    /**
     * Create a <code>Type</code> object from the given Xml element.
     *
     * @param lockType
     * @return <code>Type</code> object.
     */
    public static Type createFromXml(Element lockType) {
        if (lockType != null && DavConstants.XML_LOCKTYPE.equals(lockType.getLocalName())) {
            // we have the parent element and must retrieve the type first
            lockType = DomUtil.getFirstChildElement(lockType);
        }
        if (lockType == null) {
            throw new IllegalArgumentException("'null' is not valid lock type entry.");
        }
        Namespace namespace = Namespace.getNamespace(lockType.getPrefix(), lockType.getNamespaceURI());
        return create(lockType.getLocalName(), namespace);
    }

    /**
     * Create a <code>Type</code> object from the given localName and namespace.
     *
     * @param localName
     * @param namespace
     * @return <code>Type</code> object.
     */
    public static Type create(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (types.containsKey(key)) {
            return types.get(key);
        } else {
            Type type = new Type(localName, namespace);
            types.put(key, type);
            return type;
        }
    }
}
