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
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;

import java.io.IOException;
import java.util.List;

/**
 * <code>DavResource</code> provides standard WebDAV functionality as specified
 * by <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>.
 */
public interface DavResource {

    /**
     * String constant representing the WebDAV 1 and 2 method set.
     */
    public static final String METHODS = "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";

    /**
     * Returns a comma separated list of all compliance classes the given
     * resource is fulfilling.
     *
     * @return compliance classes
     */
    public String getComplianceClass();

    /**
     * Returns a comma separated list of all METHODS supported by the given
     * resource.
     *
     * @return METHODS supported by this resource.
     */
    public String getSupportedMethods();

    /**
     * Returns true if this webdav resource represents an existing repository item.
     *
     * @return true, if the resource represents an existing repository item.
     */
    public boolean exists();

    /**
     * Returns true if this webdav resource has the resourcetype 'collection'.
     *
     * @return true if the resource represents a collection resource.
     */
    public boolean isCollection();

    /**
     * Returns the display name of this resource.
     *
     * @return display name.
     */
    public String getDisplayName();

    /**
     * Returns the {@link DavResourceLocator locator} object for this webdav resource,
     * which encapsulates the information for building the complete 'href'.
     *
     * @return the locator for this resource.
     * @see #getResourcePath()
     * @see #getHref()
     */
    public DavResourceLocator getLocator();

    /**
     * Returns the path of the hierarchy element defined by this <code>DavResource</code>.
     * This method is a shortcut for <code>DavResource.getLocator().getResourcePath()</code>.
     *
     * @return path of the element defined by this <code>DavResource</code>.
     */
    public String getResourcePath();

    /**
     * Returns the absolute href of this resource as returned in the
     * multistatus response body.
     *
     * @return href
     */
    public String getHref();

    /**
     * Return the time of the last modification or -1 if the modification time
     * could not be retrieved.
     *
     * @return time of last modification or -1.
     */
    public long getModificationTime();

    /**
     * Spools the resource properties and ev. content to the specified context
     * (e.g. to respond to a 'GET' or 'HEAD' request). The context could e.g.
     * wrap the servlet response.
     *
     * @param outputContext The output context.
     * @throws IOException If an error occurs.
     */
    public void spool(OutputContext outputContext) throws IOException;

    /**
     * Returns an array of all {@link DavPropertyName property names} available
     * on this resource.
     *
     * @return an array of property names.
     */
    public DavPropertyName[] getPropertyNames();

    /**
     * Return the webdav property with the specified name.
     *
     * @param name name of the webdav property
     * @return the {@link DavProperty} with the given name or <code>null</code>
     * if the property does not exist.
     */
    public DavProperty<?> getProperty(DavPropertyName name);

    /**
     * Returns all webdav properties present on this resource that will be
     * return upon a {@link DavConstants#PROPFIND_ALL_PROP} request. The
     * implementation may in addition expose other (protected or calculated)
     * properties which should be marked accordingly (see also
     * {@link org.apache.jackrabbit.webdav.property.DavProperty#isInvisibleInAllprop()}.
     *
     * @return a {@link DavPropertySet} containing at least all properties
     * of this resource that are exposed in 'allprop' PROPFIND request.
     */
    public DavPropertySet getProperties();

    /**
     * Add/Set the specified property on this resource.
     *
     * @param property
     * @throws DavException if an error occurs
     */
    public void setProperty(DavProperty<?> property) throws DavException;

    /**
     * Remove the specified property from this resource.
     *
     * @param propertyName
     * @throws DavException if an error occurs
     */
    public void removeProperty(DavPropertyName propertyName) throws DavException;

    /**
     * Set/add and remove the specified properties from this resource.
     *
     * @param changeList list containing {@link DavPropertyName} objects (for
     * properties to be removed) and {@link DavProperty} objects (for
     * properties to be added/set).
     * @return multistatus response listing the status resulting from
     * setting and/or removing the specified properties, in order to allow a
     * detailed multistatus response.
     * @throws DavException if an error occurred. This may be the case if the
     * general state of the resource prevents any properties to be set or removed
     * (e.g. due to a lock).
     */
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException;

    /**
     * Retrieve the resource this resource is internal member of.
     *
     * @return resource this resource is an internal member of. In case this resource
     * is the root <code>null</code> is returned.
     */
    public DavResource getCollection();

    /**
     * Add the given resource as an internal member to this resource.
     *
     * @param resource {@link DavResource} to be added as internal member.
     * @param inputContext Context providing the properties and content for the
     * internal member to be created or replaced.
     * @throws DavException
     */
    public void addMember(DavResource resource, InputContext inputContext) throws DavException;

    /**
     * Returns an iterator over all internal members.
     *
     * @return a {@link DavResourceIterator} over all internal members.
     */
    public DavResourceIterator getMembers();

    /**
     * Removes the specified member from this resource.
     *
     * @throws DavException
     */
    public void removeMember(DavResource member) throws DavException;

    /**
     * Move this DavResource to the given destination resource
     *
     * @param destination
     * @throws DavException
     */
    public void move(DavResource destination) throws DavException;

    /**
     * Copy this DavResource to the given destination resource
     *
     * @param destination
     * @param shallow
     * @throws DavException
     */
    public void copy(DavResource destination, boolean shallow) throws DavException;

    /**
     * Returns true, if the this resource allows locking. NOTE, that this method
     * does not define, whether a lock/unlock can be successfully executed.
     *
     * @return true, if this resource supports any locking.
     * @param type
     * @param scope
     */
    public boolean isLockable(Type type, Scope scope);

    /**
     * Returns true if a lock applies to this resource. This may be either a
     * lock on this resource itself or a deep lock inherited from a collection
     * above this resource.<br>
     * Note, that true is returned whenever a lock applies to that resource even
     * if the lock is expired or not effective due to the fact that the request
     * provides the proper lock token.
     *
     * @return true if a lock applies to this resource.
     * @param type
     */
    public boolean hasLock(Type type, Scope scope);

    /**
     * Return the lock present on this webdav resource or <code>null</code>
     * if the resource is either not locked or not lockable at all. Note, that
     * a resource may have a lock that is inherited by a deep lock enforced on
     * one of its 'parent' resources.
     *
     * @return lock information of this resource or <code>null</code> if this
     * resource has no lock applying it. If an error occurs while retrieving the
     * lock information <code>null</code> is returned as well.
     * @param type
     */
    public ActiveLock getLock(Type type, Scope scope) ;

    /**
     * Returns an array of all locks applied to the given resource.
     *
     * @return array of locks. The array is empty if there are no locks applied
     * to this resource.
     */
    public ActiveLock[] getLocks();

    /**
     * Lock this webdav resource with the information retrieve from the request
     * and return the resulting lockdiscovery object.
     *
     * @param reqLockInfo lock info as retrieved from the request.
     * @return lockdiscovery object to be returned in the response. If the lock
     * could not be obtained a <code>DavException</code> is thrown.
     * @throws DavException if the lock could not be obtained.
     */
    public ActiveLock lock(LockInfo reqLockInfo) throws DavException;

    /**
     * Refresh an existing lock by resetting the timeout.
     *
     * @param reqLockInfo lock info as retrieved from the request.
     * @param lockToken identifying the lock to be refreshed.
     * @return lockdiscovery object to be returned in the response body. If the lock
     * could not be refreshed a <code>DavException</code> is thrown.
     * @throws DavException if the lock could not be refreshed.
     */
    public ActiveLock refreshLock(LockInfo reqLockInfo, String lockToken) throws DavException;

    /**
     * Remove the lock identified by the included lock token from this resource.
     * This method will return false if the unlocking did not succeed.
     *
     * @param lockToken identifying the lock to be removed.
     * @throws DavException if the lock could not be removed.
     */
    public void unlock(String lockToken) throws DavException;

    /**
     * Add an external {@link LockManager} to this resource. This method may
     * throw {@link UnsupportedOperationException} if the resource does handle
     * locking itself.
     *
     * @param lockmgr
     * @see LockManager
     */
    public void addLockManager(LockManager lockmgr);

    /**
     * Return the <code>DavResourceFactory</code> that created this resource.
     *
     * @return the factory that created this resource.
     */
    public DavResourceFactory getFactory();

    /**
     * Retrieve the <code>DavSession</code> associated with this resource.
     *
     * @return session object associated with this resource.
     */
    public DavSession getSession();
}

