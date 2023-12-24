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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>SupportedLock</code> class encapsulates the lock capabilities
 * of a resource. It is mainly responsible for generating the &lt;supportedlock&gt;
 * property.
 */
public class SupportedLock extends AbstractDavProperty<List<LockEntry>> {

    /** the list of lock entries */
    private final List<LockEntry> entries = new ArrayList<LockEntry>();

    /**
     * Creates a new empty SupportedLock property.
     */
    public SupportedLock() {
        super(DavPropertyName.SUPPORTEDLOCK, false);
    }

    /**
     * Adds a capability to this lock support.
     *
     * @param type Can currently only be 'write'
     * @param scope Can currently only be 'exclusive' or 'shared'
     *
     * @throws IllegalArgumentException If an argument contains invalid string
     */
    public void addEntry(Type type, Scope scope) {
        entries.add(new WriteLockEntry(type, scope));
    }

    /**
     * Adds a capability to this lock support.
     *
     * @param entry specifying the type of lock that is supported by this entry.
     * @see LockEntry
     */
    public void addEntry(LockEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("The lock entry cannot be null.");
        }
        entries.add(entry);
    }

    /**
     * Returns true if this a lock with the given type and scope is supported.
     *
     * @param type
     * @param scope
     * @return true if applying a lock with the given type and scope is basically
     * supported.
     */
    public boolean isSupportedLock(Type type, Scope scope) {
        for (LockEntry le : entries) {
            if (le.getType().equals(type) && le.getScope().equals(scope)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an iterator over all supported locks.
     *
     * @return an iterator over all supported locks
     */
    public Iterator<LockEntry> getSupportedLocks() {
        return entries.iterator();
    }

    /**
     * Creates an XML element that represents the &lt;supportedlock&gt; tag.
     *
     * @return An XML element of this lock support.
     * @param document
     */
    @Override
    public Element toXml(Document document) {
        Element support = getName().toXml(document);
        for (LockEntry le : entries) {
            support.appendChild(le.toXml(document));
        }
        return support;
    }

    /**
     * Returns the list of supported lock entries.
     *
     * @return list of supported lock.
     * @see org.apache.jackrabbit.webdav.property.DavProperty#getValue()
     */
    public List<LockEntry> getValue() {
        return entries;
    }

    /**
     * Class representing the default lock entries defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>.
     */
    private final static class WriteLockEntry extends AbstractLockEntry {

        /** the lock scope */
        private final Scope scope;

        /**
         * Creates a new WriteLockEntry
         *
         * @param type Can currently only be
         * {@link Type#WRITE write}
         * @param scope Can currently only be {@link Scope#EXCLUSIVE exclusive}
         * or {@link Scope#SHARED shared}.
         *
         * @throws IllegalArgumentException If an argument contains invalid string
         */
        WriteLockEntry(Type type, Scope scope) {
            if (!Type.WRITE.equals(type)) {
                throw new IllegalArgumentException("Invalid Type:" + type);
            }
            if (!Scope.EXCLUSIVE.equals(scope) && !Scope.SHARED.equals(scope)) {
                throw new IllegalArgumentException("Invalid scope:" +scope);
            }
            this.scope = scope;
        }

        /**
         * @return always returns {@link Type#WRITE write}.
         * @see LockEntry#getType()
         */
        public Type getType() {
            return Type.WRITE;
        }

        /**
         * @return returns {@link Scope#EXCLUSIVE} or {@link Scope#SHARED}.
         * @see LockEntry#getScope()
         */
        public Scope getScope() {
            return scope;
        }
    }
}
