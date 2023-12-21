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

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>LockDiscovery</code> class encapsulates the webdav lock discovery
 * property that is sent in the request body (PROPFIND and LOCK) and received
 * in a LOCK response body.
 */
public class LockDiscovery extends AbstractDavProperty<List<ActiveLock>> {

    /**
     * Listing of existing locks applied to the resource this discovery was
     * requested for. Each entry reveals, who has a lock, what type of lock he has,
     * the timeout type and the time remaining on the timeout, and the lock-type.
     * NOTE, that any of the information listed may be not available for the
     * server is free to withhold any or all of this information.
     */
    private List<ActiveLock> activeLocks = new ArrayList<ActiveLock>();

    /**
     * Creates a new empty LockDiscovery property
     */
    public LockDiscovery() {
        super(DavPropertyName.LOCKDISCOVERY, false);
    }

    /**
     * Create a new LockDiscovery property
     *
     * @param lock
     */
    public LockDiscovery(ActiveLock lock) {
        super(DavPropertyName.LOCKDISCOVERY, false);
        addActiveLock(lock);
    }

    /**
     * Create a new LockDiscovery property
     *
     * @param locks
     */
    public LockDiscovery(ActiveLock[] locks) {
        super(DavPropertyName.LOCKDISCOVERY, false);
        for (ActiveLock lock : locks) {
            addActiveLock(lock);
        }
    }

    private void addActiveLock(ActiveLock lock) {
        if (lock != null) {
            activeLocks.add(lock);
        }
    }

    /**
     * Returns the list of active locks.
     *
     * @return list of active locks
     * @see org.apache.jackrabbit.webdav.property.DavProperty#getValue()
     */
    public List<ActiveLock> getValue() {
        return activeLocks;
    }

    /**
     * Creates a <code>&lt;lockdiscovery&gt;</code> element in response
     * to a LOCK request or to the lockdiscovery property of a PROPFIND request.<br>
     * NOTE: if the {@link #activeLocks} list is empty an empty lockdiscovery
     * property is created ( <code>&lt;lockdiscovery/&gt;</code>)
     * @return A <code>&lt;lockdiscovery&gt;</code> element.
     * @param document
     */
    @Override
    public Element toXml(Document document) {
        Element lockdiscovery = getName().toXml(document);
        for (ActiveLock lock : activeLocks) {
            lockdiscovery.appendChild(lock.toXml(document));
        }
        return lockdiscovery;
    }

    //---------------------------------------------------< factory from xml >---
    /**
     * Builds a new <code>LockDiscovery</code> object from the given xml element.
     *
     * @param lockDiscoveryElement
     * @return
     * @throws IllegalArgumentException if the given xml element is not a
     * DAV:lockdiscovery element.
     */
    public static LockDiscovery createFromXml(Element lockDiscoveryElement) {
        if (!DomUtil.matches(lockDiscoveryElement, PROPERTY_LOCKDISCOVERY, NAMESPACE)) {
            throw new IllegalArgumentException("DAV:lockdiscovery element expected.");
        }

        List<ActiveLock> activeLocks = new ArrayList<ActiveLock>();
        ElementIterator it = DomUtil.getChildren(lockDiscoveryElement, XML_ACTIVELOCK, NAMESPACE);
        while (it.hasNext()) {
            Element al = it.nextElement();
            activeLocks.add(new ALockImpl(al));
        }

        return new LockDiscovery(activeLocks.toArray(new ActiveLock[activeLocks.size()]));
    }

    //------< inner class >-----------------------------------------------------
    /**
     * Simple implementation of <code>ActiveLock</code> interface, that retrieves
     * the values from the DAV:activelock XML element.<br>
     * Note, that all set-methods as well as {@link #isExpired()} are not
     * implemented.
     */
    private static class ALockImpl implements ActiveLock {

        private final Element alElement;

        private ALockImpl(Element alElement) {
            if (!DomUtil.matches(alElement, XML_ACTIVELOCK, NAMESPACE)) {
                throw new IllegalArgumentException("DAV:activelock element expected.");
            }
            this.alElement = alElement;
        }

        public boolean isLockedByToken(String lockToken) {
            String lt = getToken();
            if (lt == null) {
                return false;
            } else {
                return lt.equals(lockToken);
            }
        }

        public boolean isExpired() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public String getToken() {
            Element ltEl = DomUtil.getChildElement(alElement, XML_LOCKTOKEN, NAMESPACE);
            if (ltEl != null) {
                return DomUtil.getChildText(ltEl, XML_HREF, NAMESPACE);
            }
            return null;
        }

        public String getOwner() {
            String owner = null;
            Element ow = DomUtil.getChildElement(alElement, XML_OWNER, NAMESPACE);
            if (ow != null) {
                if (DomUtil.hasChildElement(ow, XML_HREF, NAMESPACE)) {
                    owner = DomUtil.getChildTextTrim(ow, XML_HREF, NAMESPACE);
                } else {
                    owner = DomUtil.getTextTrim(ow);
                }
            }
            return owner;
        }

        public void setOwner(String owner) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public long getTimeout() {
            // get timeout string. if no DAV:timeout element is present,
            // 't' will be 'null' and the undefined timeout will be returned.
            String t = DomUtil.getChildTextTrim(alElement, XML_TIMEOUT, NAMESPACE);
            return TimeoutHeader.parse(t, UNDEFINED_TIMEOUT);
        }

        public void setTimeout(long timeout) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public boolean isDeep() {
            String depth = DomUtil.getChildTextTrim(alElement, XML_DEPTH, NAMESPACE);
            return DEPTH_INFINITY_S.equalsIgnoreCase(depth);
        }

        public void setIsDeep(boolean isDeep) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public String getLockroot() {
            Element root = DomUtil.getChildElement(alElement, XML_LOCKROOT, NAMESPACE);
            if (root != null) {
                return DomUtil.getChildTextTrim(root, XML_HREF, NAMESPACE);
            }
            // no lockroot element
            return null;
        }

        public void setLockroot(String lockroot) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Type getType() {
            return Type.createFromXml(DomUtil.getChildElement(alElement, XML_LOCKTYPE, NAMESPACE));
        }

        public Scope getScope() {
            return Scope.createFromXml(DomUtil.getChildElement(alElement, XML_LOCKSCOPE, NAMESPACE));
        }

        public Element toXml(Document document) {
            return (Element) document.importNode(alElement, true);
        }
    }
}
