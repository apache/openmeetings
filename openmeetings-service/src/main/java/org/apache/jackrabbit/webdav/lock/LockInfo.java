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
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>LockInfo</code> is a simple utility class encapsulating the information
 * passed with a LOCK request. It combines both the request body (which if present
 * is required to by a 'lockinfo' Xml element) and the lock relevant request
 * headers '{@link DavConstants#HEADER_TIMEOUT Timeout}' and
 * '{@link DavConstants#HEADER_DEPTH Depth}'.<br>
 * Note that is class is not intended to perform any validation of the information
 * given, since this left to those objects responsible for the lock creation
 * on the requested resource.
 */
public class LockInfo implements DavConstants, XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(LockInfo.class);

    private Type type;
    private Scope scope;
    private String owner;
    private boolean isDeep;
    private long timeout;

    private boolean isRefreshLock;

    /**
     * Create a new <code>LockInfo</code> used for refreshing an existing lock.
     *
     * @param timeout
     */
    public LockInfo(long timeout) {
        this.timeout = (timeout > 0) ? timeout : INFINITE_TIMEOUT;
        this.isRefreshLock = true;
    }

    /**
     * Create a new <code>LockInfo</code>
     *
     * @param scope
     * @param type
     * @param owner
     * @param timeout
     * @param isDeep
     */
    public LockInfo(Scope scope, Type type, String owner, long timeout, boolean isDeep) {
        this.timeout = (timeout > 0) ? timeout : INFINITE_TIMEOUT;
        this.isDeep = isDeep;

        if (scope == null || type == null) {
            this.isRefreshLock = true;
        } else {
            this.scope = scope;
            this.type = type;
            this.owner = owner;
        }
    }

    /**
     * Create a new <code>LockInfo</code> object from the given information. If
     * <code>liElement</code> is <code>null</code> this lockinfo is assumed to
     * be issued from a 'Refresh Lock' request.
     *
     * @param liElement 'lockinfo' element present in the request body of a LOCK request
     * or <code>null</code> if the request was intended to refresh an existing lock.
     * @param timeout Requested timespan until the lock should expire. A LOCK
     * request MUST contain a '{@link DavConstants#HEADER_TIMEOUT Timeout}'
     * according to RFC 2518.
     * @param isDeep boolean value indicating whether the lock should be applied
     * with depth infinity or only to the requested resource.
     * @throws DavException if the <code>liElement</code> is not
     * <code>null</code> but does not start with an 'lockinfo' element.
     */
    public LockInfo(Element liElement, long timeout, boolean isDeep) throws DavException {
        this.timeout = (timeout > 0) ? timeout : INFINITE_TIMEOUT;
        this.isDeep = isDeep;

        if (liElement != null) {
            if (!DomUtil.matches(liElement, XML_LOCKINFO, NAMESPACE)) {
                log.warn("'DAV:lockinfo' element expected.");
                throw new DavException(DavServletResponse.SC_BAD_REQUEST);
            }

            ElementIterator it = DomUtil.getChildren(liElement);
            while (it.hasNext()) {
                Element child = it.nextElement();
                String childName = child.getLocalName();
                if (XML_LOCKTYPE.equals(childName)) {
                    type = Type.createFromXml(child);
                } else if (XML_LOCKSCOPE.equals(childName)) {
                    scope = Scope.createFromXml(child);
                } else if (XML_OWNER.equals(childName)) {
                    // first try if 'owner' is inside a href element
                    owner = DomUtil.getChildTextTrim(child, XML_HREF, NAMESPACE);
                    if (owner==null) {
                        // otherwise: assume owner is a simple text element
                        owner = DomUtil.getTextTrim(child);
                    }
                }
            }
            isRefreshLock = false;
        } else {
            isRefreshLock = true;
        }
    }

    /**
     * Returns the lock type or <code>null</code> if no 'lockinfo' element was
     * passed to the constructor or did not contain an 'type' element and the
     * type has not been set otherwise.
     *
     * @return type or <code>null</code>
     */
    public Type getType() {
        return type;
    }

    /**
     * Set the lock type.
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Return the lock scope or <code>null</code> if no 'lockinfo' element was
     * passed to the constructor or did not contain an 'scope' element and the
     * scope has not been set otherwise.
     *
     * @return scope or <code>null</code>
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Set the lock scope.
     *
     * @param scope
     */
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /**
     * Return the owner indicated by the corresponding child element from the
     * 'lockinfo' element or <code>null</code> if no 'lockinfo' element was
     * passed to the constructor or did not contain an 'owner' element.
     *
     * @return owner or <code>null</code>
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns true if the lock must be applied with depth infinity.
     *
     * @return true if a deep lock must be created.
     */
    public boolean isDeep() {
        return isDeep;
    }

    /**
     * Returns the time until the lock is requested to expire.
     *
     * @return time until the lock should expire.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Returns true if this <code>LockInfo</code> was created for a LOCK
     * request intended to refresh an existing lock rather than creating a
     * new one.
     *
     * @return true if the corresponding LOCK request was intended to refresh
     * an existing lock.
     */
    public boolean isRefreshLock() {
        return isRefreshLock;
    }

    /**
     * Returns the xml representation of this lock info.<br>
     * NOTE however, that the depth and the timeout are not included
     * in the xml. They will be passed to the server using the corresponding
     * request headers.
     *
     * @param document
     * @return xml representation of this lock info.
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        if (isRefreshLock) {
            return null;
        } else {
            Element lockInfo = DomUtil.createElement(document, XML_LOCKINFO, NAMESPACE);
            lockInfo.appendChild(scope.toXml(document));
            lockInfo.appendChild(type.toXml(document));
            if (owner != null) {
                DomUtil.addChildElement(lockInfo, XML_OWNER, NAMESPACE, owner);
            }
            return lockInfo;
        }
    }

}