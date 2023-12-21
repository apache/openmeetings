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
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>Privilege</code>
 */
public class Privilege implements XmlSerializable {

    public static final String XML_PRIVILEGE = "privilege";

    /**
     * Map for registered privileges
     */
    private static final Map<String, Privilege> REGISTERED_PRIVILEGES = new HashMap<String, Privilege>();

    //-------------------------------------< Privileges defined by RFC 3744 >---
    /**
     * The read privilege controls methods that return information about the
     * state of the resource, including the resource's properties. Affected
     * methods include GET and PROPFIND and OPTIONS.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.1. DAV:read Privilege</a>
     */
    public static final Privilege PRIVILEGE_READ = getPrivilege("read", SecurityConstants.NAMESPACE);
    /**
     * The write privilege controls methods that lock a resource or modify the
     * content, dead properties, or (in the case of a collection) membership of
     * the resource, such as PUT and PROPPATCH.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.2. DAV:write Privilege</a>
     */
    public static final Privilege PRIVILEGE_WRITE = getPrivilege("write", SecurityConstants.NAMESPACE);
    /**
     * The DAV:write-properties privilege controls methods that modify the dead
     * properties of the resource, such as PROPPATCH. Whether this privilege may
     * be used to control access to any live properties is determined by the
     * implementation.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.3. DAV:write-properties Privilege</a>
     */
    public static final Privilege PRIVILEGE_WRITE_PROPERTIES = getPrivilege("write-properties", SecurityConstants.NAMESPACE);
    /**
     * The DAV:write-content privilege controls methods that modify the content
     * of an existing resource, such as PUT.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.4. DAV:write-content Privilege</a>
     */
    public static final Privilege PRIVILEGE_WRITE_CONTENT = getPrivilege("write-content", SecurityConstants.NAMESPACE);
    /**
     * The DAV:unlock privilege controls the use of the UNLOCK method by a
     * principal other than the lock owner (the principal that created a lock
     * can always perform an UNLOCK).
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.5. DAV:unlock Privilege</a>
     */
    public static final Privilege PRIVILEGE_UNLOCK = getPrivilege("unlock", SecurityConstants.NAMESPACE);
    /**
     * The DAV:read-acl privilege controls the use of PROPFIND to retrieve the
     * DAV:acl property of the resource.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.6. DAV:read-acl Privilege</a>
     */
    public static final Privilege PRIVILEGE_READ_ACL = getPrivilege("read-acl", SecurityConstants.NAMESPACE);
    /**
     * The DAV:read-current-user-privilege-set privilege controls the use of
     * PROPFIND to retrieve the DAV:current-user-privilege-set property of the
     * resource.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.7. DAV:"read-current-user-privilege-set Privilege</a>
     */
    public static final Privilege PRIVILEGE_READ_CURRENT_USER_PRIVILEGE_SET = getPrivilege("read-current-user-privilege-set", SecurityConstants.NAMESPACE);
    /**
     * The DAV:write-acl privilege controls use of the ACL method to modify the
     * DAV:acl property of the resource.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.8. DAV:write-acl Privilege</a>
     */
    public static final Privilege PRIVILEGE_WRITE_ACL = getPrivilege("write-acl", SecurityConstants.NAMESPACE);
    /**
     * The DAV:bind privilege allows a method to add a new member URL to the
     * specified collection (for example via PUT or MKCOL). It is ignored for
     * resources that are not collections.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.9. DAV:bind Privilege</a>
     */
    public static final Privilege PRIVILEGE_BIND = getPrivilege("bind", SecurityConstants.NAMESPACE);
    /**
     * The DAV:unbind privilege allows a method to remove a member URL from the
     * specified collection (for example via DELETE or MOVE). It is ignored for
     * resources that are not collections.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.10. DAV:unbind Privilege</a>
     */
    public static final Privilege PRIVILEGE_UNBIND = getPrivilege("unbind", SecurityConstants.NAMESPACE);
    /**
     * DAV:all is an aggregate privilege that contains the entire set of
     * privileges that can be applied to the resource.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 3.11. DAV:all Privilege</a>
     */
    public static final Privilege PRIVILEGE_ALL = getPrivilege("all", SecurityConstants.NAMESPACE);

    private final String privilege;
    private final Namespace namespace;

    /**
     * Private constructor
     *
     * @param privilege
     * @param namespace
     */
    private Privilege(String privilege, Namespace namespace) {
        this.privilege = privilege;
        this.namespace = namespace;
    }

    /**
     * @return The local name of this <code>Privilege</code>.
     */
    public String getName() {
        return privilege;
    }

    /**
     * @return The namespace of this <code>Privilege</code>.
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * @see XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element privEl =  DomUtil.createElement(document, XML_PRIVILEGE, SecurityConstants.NAMESPACE);
        DomUtil.addChildElement(privEl, privilege, namespace);
        return privEl;
    }

    /**
     * Factory method to create/retrieve a <code>Privilege</code>.
     *
     * @param privilege
     * @param namespace
     * @return
     */
    public static Privilege getPrivilege(String privilege, Namespace namespace) {
        if (privilege == null) {
            throw new IllegalArgumentException("'null' is not a valid privilege.");
        }
        if (namespace == null) {
            namespace = Namespace.EMPTY_NAMESPACE;
        }
        String key = "{" + namespace.getURI() + "}" + privilege;
        if (REGISTERED_PRIVILEGES.containsKey(key)) {
            return REGISTERED_PRIVILEGES.get(key);
        } else {
            Privilege p = new Privilege(privilege, namespace);
            REGISTERED_PRIVILEGES.put(key, p);
            return p;
        }
    }

    /**
     * Factory method to create/retrieve a <code>Privilege</code> from the given
     * DAV:privilege element.
     *
     * @param privilege
     * @return
     */
    public static Privilege getPrivilege(Element privilege) throws DavException {
        if (!DomUtil.matches(privilege, XML_PRIVILEGE, SecurityConstants.NAMESPACE)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:privilege element expected.");
        }
        Element el = DomUtil.getFirstChildElement(privilege);
        return getPrivilege(el.getLocalName(), DomUtil.getNamespace(el));
    }
}
