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
 * The <code>Scope</code> class abstracts the lock scope as defined by RFC 2518.
 */
public class Scope implements XmlSerializable {

    private static final Map<String, Scope> scopes = new HashMap<String, Scope>();

    public static final Scope EXCLUSIVE = Scope.create(DavConstants.XML_EXCLUSIVE, DavConstants.NAMESPACE);
    public static final Scope SHARED = Scope.create(DavConstants.XML_SHARED, DavConstants.NAMESPACE);

    private final String localName;
    private final Namespace namespace;

    /**
     * Private constructor
     *
     * @param localName
     * @param namespace
     */
    private Scope(String localName, Namespace namespace) {
        this.localName = localName;
        this.namespace = namespace;
    }

    /**
     * Return the Xml representation of the lock scope object as present in
     * the LOCK request and response body and in the {@link LockDiscovery}.
     *
     * @return Xml representation
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element lockScope = DomUtil.createElement(document, DavConstants.XML_LOCKSCOPE, DavConstants.NAMESPACE);
        DomUtil.addChildElement(lockScope, localName, namespace);
        return lockScope;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + localName.hashCode();
        result = prime * result + namespace.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Scope) {
            Scope other = (Scope) obj;
            return localName.equals(other.localName) && namespace.equals(other.namespace);
        } else {
            return false;
        }
    }

    /**
     * Create a <code>Scope</code> object from the given Xml element.
     *
     * @param lockScope
     * @return Scope object.
     */
    public static Scope createFromXml(Element lockScope) {
        if (lockScope != null && DavConstants.XML_LOCKSCOPE.equals(lockScope.getLocalName())) {
            // we have the parent element and must retrieve the scope first
            lockScope = DomUtil.getFirstChildElement(lockScope);
        }
        if (lockScope == null) {
            throw new IllegalArgumentException("'null' is not a valid lock scope entry.");
        }
        Namespace namespace = Namespace.getNamespace(lockScope.getPrefix(), lockScope.getNamespaceURI());
        return create(lockScope.getLocalName(), namespace);
    }

    /**
     * Create a <code>Scope</code> object from the given name and namespace.
     *
     * @param localName
     * @param namespace
     * @return Scope object.
     */
    public static Scope create(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (scopes.containsKey(key)) {
            return scopes.get(key);
        } else {
            Scope scope = new Scope(localName, namespace);
            scopes.put(key, scope);
            return scope;
        }
    }
}
