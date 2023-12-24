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
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.ResourceType;

/**
 * <code>VersionHistoryResource</code> represents a collection that has
 * all versions of a given version-controlled resource as members.
 * <p>
 * RFC 3253 defines the following required properties for a version history:
 * <ul>
 * <li>DAV:version-set</li>
 * <li>DAV:root-version</li>
 * <li>all DeltaV-compliant resource properties.</li>
 * </ul>
 * <p>
 * In addition a version history resource must support the following METHODS:
 * <ul>
 * <li>all DeltaV-compliant resource METHODS.</li>
 * </ul>
 *
 * NOTE: the {@link org.apache.jackrabbit.webdav.DavConstants#PROPERTY_RESOURCETYPE DAV:resourcetype}
 * property must indicate 'DAV:version-history'.
 *
 * @see DeltaVResource
 * @see ResourceType#VERSION_HISTORY
 */
public interface VersionHistoryResource extends DeltaVResource {

    // supported METHODS: same as DeltaVResource > set to empty string
    public static final String METHODS = "";

    /**
     * Computed (protected) property identifying the root version of this version
     * history.
     */
    public static final DavPropertyName ROOT_VERSION = DavPropertyName.create("root-version", DeltaVConstants.NAMESPACE);

    /**
     * The protected property DAV:version-set identifies each version of this
     * version history.
     *
     * @see #getVersions()
     */
    public static final DavPropertyName VERSION_SET = DavPropertyName.create("version-set", DeltaVConstants.NAMESPACE);

    /**
     * Returns an array of <code>VersionResource</code>s containing all versions
     * that are a member of this resource.
     *
     * @return all <code>VersionResource</code>s that belong to this version history.
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see #VERSION_SET
     */
    public VersionResource[] getVersions() throws DavException;
}