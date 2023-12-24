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

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavException;

/**
 * <code>BaselineResource</code> represents the 'version' of a configuration
 * which is represented by a 'version-controlled-configuration' (VCC) resource.
 * Such as new versions are created by CHECKIN of a version-controlled
 * resource, a new baseline is created, whenever the VCC resource, that
 * represents a set of resources rather than a single resource, is checked-in.
 * <p>
 * Since the baseline behaves like a <code>VersionResource</code> and only is
 * defined to provide additional protected properties, this interface only adds
 * a convenience method that allows to retrieve the baseline collection.
 * <p>
 * Supported live properties:
 * <pre>
 * DAV:baseline-collection
 * DAV:subbaseline-set
 * all version properties.
 * </pre>
 *
 * Supported methods:
 * <pre>
 * all version methods.
 * </pre>
 */
public interface BaselineResource extends VersionResource {

    /**
     * The protected DAV:baseline-collection property identifies a distinct
     * collection that lists as members all version-controlled resources of
     * the configuration this baseline belongs to (the baseline being one
     * version of the corresponding vc-configuration-resource). In other words:
     * each member in the list must correspond to a member of the baseline-controlled
     * collection at the time this baseline (version) was created.
     * <p>
     *
     * Note that the DAV:baseline-collection represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName BASELINE_COLLECTION = DavPropertyName.create("baseline-collection", DeltaVConstants.NAMESPACE);

    /**
     * The protected DAV:subbaseline-set property identifies a set of baseline
     * resources. Note however, that the subbaselines of this resource are
     * not only formed from the baseline resources listed in this property
     * but also includes all subbaseline resources of the latter.
     *
     * Note that the DAV:subbaseline-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName SUBBASELINE_SET = DavPropertyName.create("subbaseline-set", DeltaVConstants.NAMESPACE);

    /**
     * Return the resource that represents the baseline-collection of this
     * baseline, which is identified the href present in the {@link #BASELINE_COLLECTION}
     * property.
     *
     * @return baseline collection
     */
    public DavResource getBaselineCollection() throws DavException;
}