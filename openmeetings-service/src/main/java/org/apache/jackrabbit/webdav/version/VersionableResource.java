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
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;

/**
 * <code>VersionableResource</code> represents an extension to the basic
 * {@link DeltaVResource}, that allows to adding version-control support. By
 * calling {@link #addVersionControl()} resource is put under version control,
 * thus the versionable resource turns into a version controlled resource.
 * <p>
 * RFC 3253 defines the following required properties for a versionable resource:
 * <ul>
 * <li>{@link DeltaVConstants#WORKSPACE DAV:workspace} (workspace feature)</li>
 * <li>DAV:version-controlled-configuration (baseline feature)</li>
 * <li>all DeltaV-compliant resource properties</li>
 * </ul>
 * <p>
 * In addition a versionable resource must support the following METHODS:
 * <ul>
 * <li>VERSION-CONTROL</li>
 * <li>all DeltaV-compliant resource METHODS.</li>
 * </ul>
 *
 * @see DeltaVResource
 */
public interface VersionableResource extends DeltaVResource {

    /**
     * The versionable resource defines one additional method VERSION-CONTROL.
     *
     * @see DeltaVResource#METHODS
     * @see org.apache.jackrabbit.webdav.DavResource#METHODS
     */
    public String METHODS = "VERSION-CONTROL";

    /**
     * Converts this versionable resource into a version-controlled resource. If
     * this resource is already version-controlled this resource is not affected.
     * If however, this resource is not versionable an <code>DavException</code>
     * (error code: {@link org.apache.jackrabbit.webdav.DavServletResponse#SC_METHOD_NOT_ALLOWED}
     * is thrown. The same applies, if this resource is versionable but its
     * current state does not allow to made it a version-controlled one or
     * if any other error occurs.
     *
     * @throws org.apache.jackrabbit.webdav.DavException
     */
    public void addVersionControl() throws DavException;
}