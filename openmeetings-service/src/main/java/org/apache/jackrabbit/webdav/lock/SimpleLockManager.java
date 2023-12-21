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
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple manager for webdav locks.<br>
 */
public class SimpleLockManager implements LockManager {

    /** map of locks */
    private Map<String, ActiveLock> locks = new HashMap<String, ActiveLock>();

    /**
     *
     * @param lockToken
     * @param resource
     * @return
     * @see LockManager#hasLock(String, org.apache.jackrabbit.webdav.DavResource)
     */
    public boolean hasLock(String lockToken, DavResource resource) {
        ActiveLock lock = locks.get(resource.getResourcePath());
        if (lock != null && lock.getToken().equals(lockToken)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the lock applying to the given resource or <code>null</code> if
     * no lock can be found.
     *
     * @param type
     * @param scope
     * @param resource
     * @return lock that applies to the given resource or <code>null</code>.
     */
    public synchronized ActiveLock getLock(Type type, Scope scope, DavResource resource) {
        if (!(Type.WRITE.equals(type) && Scope.EXCLUSIVE.equals(scope))) {
            return null;
        }
        return getLock(resource.getResourcePath());
    }

    /**
     * Looks for a valid lock at the given path or a deep lock present with
     * a parent path.
     *
     * @param path
     * @return
     */
    private ActiveLock getLock(String path) {
        ActiveLock lock = locks.get(path);
        if (lock != null) {
            // check if not expired
            if (lock.isExpired()) {
                lock = null;
            }
        }
        if (lock == null) {
            // check, if child of deep locked parent
            if (!path.equals("/")) {
                ActiveLock parentLock = getLock(getParentPath(path));
                if (parentLock != null && parentLock.isDeep()) {
                    lock = parentLock;
                }
            }
        }
        return lock;
    }

    /**
     * Adds the lock for the given resource, replacing any existing lock.
     *
     * @param lockInfo
     * @param resource being the lock holder
     */
    public synchronized ActiveLock createLock(LockInfo lockInfo,
                                              DavResource resource)
            throws DavException {
        if (lockInfo == null || resource == null) {
            throw new IllegalArgumentException("Neither lockInfo nor resource must be null.");
        }

        String resourcePath = resource.getResourcePath();
        // test if there is already a lock present on this resource
        ActiveLock lock = locks.get(resourcePath);
        if (lock != null && lock.isExpired()) {
            locks.remove(resourcePath);
            lock = null;
        }
        if (lock != null) {
            throw new DavException(DavServletResponse.SC_LOCKED, "Resource '" + resource.getResourcePath() + "' already holds a lock.");
        }
        // test if the new lock would conflict with any lock inherited from the
        // collection or with a lock present on any member resource.
        for (String key : locks.keySet()) {
            // TODO: is check for lock on internal-member correct?
            if (isDescendant(key, resourcePath)) {
                ActiveLock l = locks.get(key);
                if (l.isDeep() || (key.equals(getParentPath(resourcePath)) && !resource.isCollection())) {
                    throw new DavException(DavServletResponse.SC_LOCKED, "Resource '" + resource.getResourcePath() + "' already inherits a lock by its collection.");
                }
            } else if (isDescendant(resourcePath, key)) {
                if (lockInfo.isDeep() || isInternalMember(resource, key)) {
                    throw new DavException(DavServletResponse.SC_CONFLICT, "Resource '" + resource.getResourcePath() + "' cannot be locked due to a lock present on the member resource '" + key + "'.");
                }

            }
        }
        lock = new DefaultActiveLock(lockInfo);
        locks.put(resource.getResourcePath(), lock);
        return lock;
    }

    /**
     *
     * @param lockInfo
     * @param lockToken
     * @param resource
     * @return
     * @throws DavException
     * @see DavResource#refreshLock(org.apache.jackrabbit.webdav.lock.LockInfo, String)
     */
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken, DavResource resource)
            throws DavException {
        ActiveLock lock = getLock(lockInfo.getType(), lockInfo.getScope(), resource);
        if (lock == null) {
            throw new DavException(DavServletResponse.SC_PRECONDITION_FAILED);
        } else if (!lock.getToken().equals(lockToken)) {
            throw new DavException(DavServletResponse.SC_LOCKED);
        }
        lock.setTimeout(lockInfo.getTimeout());
        return lock;
    }

    /**
     * Remove the lock hold by the given resource.
     *
     * @param lockToken
     * @param resource that is the lock holder
     */
    public synchronized void releaseLock(String lockToken, DavResource resource)
            throws DavException {
        if (!locks.containsKey(resource.getResourcePath())) {
            throw new DavException(DavServletResponse.SC_PRECONDITION_FAILED);
        }
        ActiveLock lock = locks.get(resource.getResourcePath());
        if (lock.getToken().equals(lockToken)) {
            locks.remove(resource.getResourcePath());
        } else {
            throw new DavException(DavServletResponse.SC_LOCKED);
        }
    }

    /**
     * Return true, if the resource with the given memberPath is a internal
     * non-collection member of the given resource, thus affected by a
     * non-deep lock present on the resource.
     *
     * @param resource
     * @param memberPath
     * @return
     */
    private static boolean isInternalMember(DavResource resource, String memberPath) {
        if (resource.getResourcePath().equals(getParentPath(memberPath))) {
            // find the member with the given path
            DavResourceIterator it = resource.getMembers();
            while (it.hasNext()) {
                DavResource member = it.nextResource();
                if (member.getResourcePath().equals(memberPath)) {
                    // return true if that member is not a collection
                    return !member.isCollection();
                }
            }
        }
        return false;
    }

    /**
     * @param path Path to retrieve the parent path for.
     * @return empty string if the specified path contains no '/', "/" if the
     * last index of '/' is zero. Otherwise the last segment is cut off the
     * specified path.
     */
    private static String getParentPath(String path) {
        int idx = path.lastIndexOf('/');
        switch (idx) {
            case -1:
                return "";
            case 0:
                return "/";
            default:
                return path.substring(0, idx);
        }
    }

    /**
     * Determines if the <code>descendant</code> path is hierarchical a
     * descendant of <code>path</code>.
     *
     * @param path     the current path
     * @param descendant the potential descendant
     * @return <code>true</code> if the <code>descendant</code> is a descendant;
     *         <code>false</code> otherwise.
     */
    private static boolean isDescendant(String path, String descendant) {
        String pattern = path.endsWith("/") ? path : path + "/";
        return !pattern.equals(descendant) && descendant.startsWith(pattern);
    }
}

