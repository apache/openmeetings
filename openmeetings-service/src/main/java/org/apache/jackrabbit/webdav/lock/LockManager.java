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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

/**
 * The <code>LockManager</code> interface.
 */
public interface LockManager {

    /**
     * Create a new lock for the given {@link org.apache.jackrabbit.webdav.DavResource resource}.
     *
     * @param lockInfo
     * @param resource
     * @return
     * @throws DavException
     */
    public ActiveLock createLock(LockInfo lockInfo, DavResource resource)
            throws DavException;

    /**
     * Refresh the lock identified by the given lockToken and initially created
     * on the specified resource. The update information can be retrieved from
     * the lockInfo object passes.
     *
     * @param lockInfo
     * @param lockToken
     * @param resource
     * @return
     * @throws DavException
     */
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken, DavResource resource)
            throws DavException;

    /**
     * Release the lock identified by the given lockToken and initially created
     * on the specified resource.
     *
     * @param lockToken
     * @param resource
     * @throws DavException
     */
    public void releaseLock(String lockToken, DavResource resource)
            throws DavException;

    /**
     * Retrieve the lock with the given type and scope that is applied to the
     * given resource. The lock may be either initially created on this resource
     * or might be inherited from an ancestor resource that hold a deep lock.
     * If no such lock applies to the given resource <code>null</code> must be
     * returned.
     *
     * @param type
     * @param scope
     * @param resource
     * @return lock with the given type and scope applied to the resource or
     * <code>null</code> if no lock applies.
     */
    public ActiveLock getLock(Type type, Scope scope, DavResource resource);

    /**
     * Returns true, if the the manager contains a lock for the given
     * resource, that is hold by the specified token.
     *
     * @param lockToken
     * @param resource
     * @return true if the resource is locked by the specified token.
     */
    public boolean hasLock(String lockToken, DavResource resource);
}