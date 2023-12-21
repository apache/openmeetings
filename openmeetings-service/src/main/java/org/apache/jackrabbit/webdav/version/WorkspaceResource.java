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
 * A workspace resource is a collection whose members are related
 * version-controlled and non-version-controlled resources.
 *
 * <p>
 * RFC 3253 defines the following required live properties for an Workspace
 * resource.
 * <ul>
 * <li>all DeltaV-compliant resource properties</li>
 * <li>{@link #WORKSPACE_CHECKOUT_SET DAV:workspace-checkout-set}</li>
 * <li>{@link #BASELINE_CONTROLLED_COLLECTION_SET DAV:baseline-controlled-collection-set} (baseline)</li>
 * <li>{@link #CURRENT_ACTIVITY_SET DAV:current-activity-set} (activity)</li>
 * </ul>
 * Note, that RFC 3253 doesn't define a separate resource type for a workspace.
 * <p>
 * The workspace resource must support all methods defined for a DeltaV-compliant
 * collection. Since no additional methods are required for a workspace this
 * interface mainly acts as marker.
 * <p>
 * Please refer to <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
 * Section 6 for a complete description of this resource type.
 */
public interface WorkspaceResource extends DeltaVResource {

    /**
     * The DAV:workspace-checkout-set property is the only required property
     * which is additionally added to a workspace resource.<br>
     * This computed property identifies each checked-out resource whose
     * DAV:workspace property identifies this workspace.
     * <p>
     * Note that the DAV:workspace-checkout-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}.
     */
    public static final DavPropertyName WORKSPACE_CHECKOUT_SET = DavPropertyName.create("workspace-checkout-set", DeltaVConstants.NAMESPACE);

    /**
     * @deprecated Use {@link #CURRENT_ACTIVITY_SET} instead.
     */
    public static final DavPropertyName CUURENT_ACTIVITY_SET = DavPropertyName.create("current-activity-set", DeltaVConstants.NAMESPACE);

    /**
     * DAV:current-activity-set is a required property for a workspace resource,
     * if the server supports the activity feature.<br>
     * It identifies the activities that currently are being performed in this
     * workspace. When a member of this workspace is checked out, if no activity
     * is specified in the checkout request, the DAV:current-activity-set will
     * be used. This allows an activity-unaware client to update a workspace in
     * which activity tracking is required. The DAV:current-activity-set MAY be
     * restricted to identify at most one activity.
     * <p>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT current-activity-set (href*)&gt;
     * </pre>
     * Note that the DAV:current-activity-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName CURRENT_ACTIVITY_SET = DavPropertyName.create("current-activity-set", DeltaVConstants.NAMESPACE);

    /**
     * The Baseline feature (section 12) defines the following computed property
     * for a workspace resource: DAV:baseline-controlled-collection-set lists
     * all collections of this workspace, that are under baseline control. This
     * list may include the workspace itself.
     * <p>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT baseline-controlled-collection-set (href*)&gt;
     * </pre>
     * Note that the DAV:baseline-controlled-collection-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName BASELINE_CONTROLLED_COLLECTION_SET = DavPropertyName.create("baseline-controlled-collection-set", DeltaVConstants.NAMESPACE);
}