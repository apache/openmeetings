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

import java.util.UUID;

import org.apache.jackrabbit.webdav.DavConstants;

/**
 * <code>DefaultActiveLock</code> implements the <code>ActiveLock</code> interface
 * and represents an exclusive write lock with a random uuid lock token.
 * Lock owner, timeout and depth is retrieved from the {@link LockInfo} object
 * passed in the constructor. If the lockinfo is null, the following default
 * values are set:<pre>
 * - timeout is set to infinity.
 * - isDeep is set to true.
 * </pre>
 */
public class DefaultActiveLock extends AbstractActiveLock {

    private final String token = DavConstants.OPAQUE_LOCK_TOKEN_PREFIX + UUID.randomUUID();
    private String owner;
    private boolean isDeep = true; // deep by default
    private long expirationTime = DavConstants.INFINITE_TIMEOUT; // never expires by default;

    /**
     * Create a new <code>DefaultActiveLock</code> with default values.
     */
    public DefaultActiveLock() {
    }

    /**
     * Create a new lock
     *
     * @param lockInfo
     * @throws IllegalArgumentException if either scope or type is invalid.
     */
    public DefaultActiveLock(LockInfo lockInfo) {
        if (lockInfo != null) {
            if (!(Type.WRITE.equals(lockInfo.getType()) && Scope.EXCLUSIVE.equals(lockInfo.getScope()))) {
                throw new IllegalArgumentException("Only 'exclusive write' lock is allowed scope/type pair.");
            }
            owner = lockInfo.getOwner();
            isDeep = lockInfo.isDeep();
            setTimeout(lockInfo.getTimeout());
        }
    }

    /**
     * @see ActiveLock#isLockedByToken(String)
     */
    public boolean isLockedByToken(String lockToken) {
        return (token != null) && token.equals(lockToken);
    }

    /**
     * @see ActiveLock#isExpired()
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * @see ActiveLock#getToken()
     */
    public String getToken() {
        return token;
    }

    /**
     * @see ActiveLock#getOwner()
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @see ActiveLock#setOwner(String)
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @see ActiveLock#getTimeout()
     */
    public long getTimeout() {
        return expirationTime - System.currentTimeMillis();
    }

    /**
     * @see ActiveLock#setTimeout(long)
     */
    public void setTimeout(long timeout) {
        if (timeout > 0) {
            expirationTime = System.currentTimeMillis() + timeout;
        }
    }

    /**
     * @see ActiveLock#isDeep()
     */
    public boolean isDeep() {
        return isDeep;
    }

    /**
     * @see ActiveLock#setIsDeep(boolean)
     */
    public void setIsDeep(boolean isDeep) {
        this.isDeep = isDeep;
    }

    /**
     * This is always a write lock.
     *
     * @return the lock type
     * @see Type#WRITE
     */
    public Type getType() {
        return Type.WRITE;
    }

    /**
     * This is always an exclusive lock.
     *
     * @return the lock scope.
     * @see Scope#EXCLUSIVE
     */
    public Scope getScope() {
        return Scope.EXCLUSIVE;
    }
}
