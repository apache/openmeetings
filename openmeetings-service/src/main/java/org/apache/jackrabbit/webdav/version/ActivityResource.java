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

/**
 * An activity is a resource that selects a set of versions that are on a single
 * "line of descent", where a line of descent is a sequence of versions connected
 * by successor relationships. If an activity selects versions from multiple
 * version histories, the versions selected in each version history must be on a
 * single line of descent.
 * <p>
 * RFC 3253 defines the following required live properties for an Activity
 * resource.
 * <ul>
 * <li>{@link #ACTIVITY_VERSION_SET DAV:activity-version-set}</li>
 * <li>{@link #ACTIVITY_CHECKOUT_SET DAV:activity-checkout-set}</li>
 * <li>{@link #SUBACTIVITY_SET DAV:subactivity-set}</li>
 * <li>{@link #CURRENT_WORKSPACE_SET DAV:current-workspace-set}</li>
 * <li>all DeltaV-compliant resource properties}.</li>
 * <li>Note, that the {@link org.apache.jackrabbit.webdav.DavConstants#PROPERTY_RESOURCETYPE DAV:resourcetype}
 * property returned by an Activity resource must be
 * {@link org.apache.jackrabbit.webdav.property.ResourceType#ACTIVITY DAV:activity}</li>
 * </ul>
 * <p>
 * The Activity resource must support all methods defined for a
 * {@link DeltaVResource DeltaV-compliant resource}. Since no additional methods
 * are required for an activity this interface mainly acts as marker.
 * <p>
 * Please refer to <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
 * Section 13 for a complete description of this resource type.
 */
public interface ActivityResource extends DeltaVResource {

    /**
     * The computed DAV:activity-version-set property identifies each version
     * whose DAV:activity-set property identifies this activity. Multiple
     * versions of a single version history can be selected by an activity's
     * DAV:activity-version-set property, but all DAV:activity-version-set
     * versions from a given version history must be on a single line of descent
     * from the root version of that version history.
     * <p>
     * Note that the DAV:activity-version-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName ACTIVITY_VERSION_SET = DavPropertyName.create("activity-version-set", DeltaVConstants.NAMESPACE);

    /**
     * The computed DAV:activity-checkout-set property identifies each
     * checked-out resource whose DAV:activity-set identifies this activity.
     * <p>
     * Note that the DAV:activity-checkout-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName ACTIVITY_CHECKOUT_SET = DavPropertyName.create("activity-checkout-set", DeltaVConstants.NAMESPACE);

    /**
     * The DAV:subactivity-set property identifies each activity that forms a
     * part of the logical change being captured by this activity. An activity
     * behaves as if its DAV:activity-version-set is extended by the
     * DAV:activity-version-set of each activity identified in the
     * DAV:subactivity-set. In particular, the versions in this extended set
     * MUST be on a single line of descent, and when an activity selects a version
     * for merging, the latest version in this extended set is the one that will
     * be merged.
     * <p>
     * A server MAY reject attempts to modify the DAV:subactivity-set of an activity.
     *
     * Note that the DAV:subactivity-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName SUBACTIVITY_SET = DavPropertyName.create("subactivity-set", DeltaVConstants.NAMESPACE);

    /**
     * The computed DAV:current-workspace-set property identifies identifies
     * each workspace whose DAV:current-activity-set identifies this activity.
     * <p>
     * Note that the DAV:current-workspace-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName CURRENT_WORKSPACE_SET = DavPropertyName.create("current-workspace-set", DeltaVConstants.NAMESPACE);

}