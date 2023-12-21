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
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyName;

/**
 * The <code>VersionControlledResource</code> represents in contrast to the
 * <code>VersionableResource</code> a resource, that has already been put
 * under version-control. This resource can be checked-in, checked-out and
 * has its own {@link VersionHistoryResource version history}.
 * <p>
 * RFC 3253 defines the following required properties for a
 * version-controlled resource (vc-resource):
 * <ul>
 * <li>DAV:auto-version</li>
 * <li>DAV:version-history (version-history)</li>
 * <li>DAV:workspace (workspace)</li>
 * <li>DAV:version-controlled-configuration (baseline)</li>
 * <li>all DeltaV-compliant resource properties.</li>
 * </ul>
 *
 * checked-in vc-resource:
 * <ul>
 * <li>DAV:checked-in</li>
 * </ul>
 *
 * checked-out vc-resource:
 * <ul>
 * <li>DAV:checked-out</li>
 * <li>DAV:predecessor-set</li>
 * <li>DAV:checkout-fork (in-place-checkout or working resource)</li>
 * <li>DAV:checkin-fork (in-place-checkout or working resource)</li>
 * <li>DAV:merge-set (merge)</li>
 * <li>DAV:auto-merge-set (merge)</li>
 * <li>DAV:unreserved (activity)</li>
 * <li>DAV:activity-set (activity)</li>
 * </ul>
 *
 * If the Version-Controlled-Collection feature is supported (see section 14
 * of RFC 3253) the following computed property is required:
 * <ul>
 * <li>DAV:eclipsed-set</li>
 * </ul>
 *
 * If the Baseline feature is supported (see section 12 of RFC 3253), a version-
 * controlled resource may represent a 'configuration' rather than a single
 * resource. In this case the RFC defines the following required properties:
 * <ul>
 * <li>DAV:baseline-controlled-collection</li>
 * <li>DAV:subbaseline-set (if the configuration resource is checked-out)</li>
 * </ul>
 *
 * <p>
 * In addition a version-controlled resource must support the following METHODS:
 * <ul>
 * <li>VERSION-CONTROL</li>
 * <li>MERGE (merge)</li>
 * <li>all DeltaV-compliant resource METHODS.</li>
 * </ul>
 *
 * checked-in vc-resource:
 * <ul>
 * <li>CHECKOUT (checkout-in-place)</li>
 * <li>UPDATE (update)</li>
 * <li>all version-controlled resource METHODS.</li>
 * </ul>
 *
 * checked-out vc-resource:
 * <ul>
 * <li>CHECKIN (checkout-in-place or working-resource)</li>
 * <li>UNCHECKOUT (checkout-in-place)</li>
 * <li>all DeltaV-compliant resource METHODS.</li>
 * </ul>
 *
 * @see DeltaVResource
 * @see VersionableResource
 */
public interface VersionControlledResource extends VersionableResource {

    /**
     * Methods defined for a checked-in version-controlled resource: CHECKOUT, UPDATE, MERGE, LABEL
     */
    public String methods_checkedIn = "CHECKOUT, UPDATE, MERGE, LABEL";
    /**
     * Methods defined for a checked-out version-controlled resource: CHECKIN, MERGE
     */
    public String methods_checkedOut = "CHECKIN, MERGE";

    /**
     * The DAV:auto-version property determines how it responds to a method that
     * attempts to modify its content or dead properties. Possible responses
     * include various combinations of automated checkout, write lock and checkin
     * as well as failure until the resource is explicitly checked-out.<br>
     * See <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a> for a detailed
     * description.
     */
    public static final DavPropertyName AUTO_VERSION = DavPropertyName.create("auto-version", DeltaVConstants.NAMESPACE);

    /**
     * The computed property DAV:version-history identifies the version history
     * resource for the DAV:checked-in or DAV:checked-out version of this
     * version-controlled resource.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT version-history (href)&gt;
     * </pre>
     */
    public static final DavPropertyName VERSION_HISTORY = DavPropertyName.create("version-history", DeltaVConstants.NAMESPACE);

    /**
     * The DAV:checked-in property appears on a checked-in version-controlled
     * resource, and identifies the base version of this version-controlled
     * resource. This property is removed when the resource is checked out, and
     * then added back (identifying a new version) when the resource is checked
     * back in.<br>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT checked-in (href)&gt;
     * </pre>
     */
    public static final DavPropertyName CHECKED_IN = DavPropertyName.create("checked-in", DeltaVConstants.NAMESPACE);

    /**
     * The DAV:checked-out property identifies the base version of this resource.
     * It is the same that was identified by the DAV:checked-in property at the
     * time the resource was checked out. This property is removed when the
     * resource is checked in.<br>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT checked-out (href)&gt;
     * </pre>
     *
     * @see #CHECKED_IN
     */
    public static final DavPropertyName CHECKED_OUT = DavPropertyName.create("checked-out", DeltaVConstants.NAMESPACE);

    /**
     * The DAV:predecessor-set property of a version-controlled resource points
     * to those version resources, that are scheduled to become the predecessors
     * of this resource when it is back checked-in. This property is not
     * protected, however a server may reject attempts to modify the
     * DAV:predecessor-set of a version-controlled resource.<br>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT predecessor-set (href+)&gt;
     * </pre>
     *
     * @see #checkin()
     * @see VersionResource#PREDECESSOR_SET
     */
    public static final DavPropertyName PREDECESSOR_SET = DavPropertyName.create("predecessor-set", DeltaVConstants.NAMESPACE);

    /**
     *  This property determines the DAV:checkin-fork property of the version
     * that results from checking in this resource.
     */
    public static final DavPropertyName CHECKIN_FORK = DavPropertyName.create("checkin-fork", DeltaVConstants.NAMESPACE);

    /**
     * This property determines the DAV:checkout-fork property of the version
     * that results from checking in this resource.
     */
    public static final DavPropertyName CHECKOUT_FORK = DavPropertyName.create("checkout-fork", DeltaVConstants.NAMESPACE);

    /**
     * This property identifies each version that is to be merged into this
     * checked-out resource. This property is set, whenever a MERGE request
     * with the DAV:no-auto-merge flag succeeded. The client then must confirm
     * each single merge by removing the version from the DAV:merge-set or
     * moving it the the versions DAV:predecessor-set.<br>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT merge-set (href*)&gt;
     * </pre>
     *
     * @see #merge(MergeInfo)
     */
    public static final DavPropertyName MERGE_SET = DavPropertyName.create("merge-set", DeltaVConstants.NAMESPACE);

    /**
     * The DAV:auto-merge-set property identifies each version that the server
     * has merged into this checked-out resource. The client should confirm that
     * the merge has been performed correctly before moving a URL from the
     * DAV:auto-merge-set to the DAV:predecessor-set of a checked-out resource.<br>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT auto-merge-set (href*)&gt;
     * </pre>
     *
     * @see #merge(MergeInfo)
     */
    public static final DavPropertyName AUTO_MERGE_SET = DavPropertyName.create("auto-merge-set", DeltaVConstants.NAMESPACE);

    /**
     * DAV:unreserved is a property for a checked-out resource, if the server
     * supports the activity feature.<br>
     * It indicates whether the DAV:activity-set of another checked-out resource
     * associated with the version history of this version-controlled resource
     * can have an activity that is in the DAV:activity-set property of this
     * checked-out resource.
     * <br>
     * A result of the requirement that an activity must form a single line of
     * descent through a given version history is that if multiple checked-out
     * resources for a given version history are checked out unreserved into a
     * single activity, only the first CHECKIN will succeed. Before another of
     * these checked-out resources can be checked in, the user will first have
     * to merge into that checked-out resource the latest version selected by
     * that activity from that version history, and then modify the
     * DAV:predecessor-set of that checked-out resource to identify that version.
     * <p>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT unreserved (#PCDATA)&gt;
     * PCDATA value: boolean
     * </pre>
     * @see VersionResource#ACTIVITY_SET
     */
    public static final DavPropertyName UNRESERVED = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);

    /**
     * DAV:activity-set is a property for a checked-out resource, if the
     * server supports the activity feature.<br>
     * This property determines the DAV:activity-set property of the version that
     * results from checking in this resource.
     *
     * @see VersionResource#ACTIVITY_SET
     */
    public static final DavPropertyName ACTIVITY_SET = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);

    /**
     * If the 'Version-Controlled-Collection Feature' is supported the
     * DAV:eclipsed-set property present on a collection identifies all
     * internal members that are not version-controlled and hide a vc internal
     * member with the same name.
     * <p>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT eclipsed-set (binding-name*)&gt;
     * &lt;!ELEMENT binding-name (#PCDATA)&gt;
     * PCDATA value: URL segment
     * </pre>
     *
     * @see VersionResource#VERSION_CONTROLLED_BINDING_SET
     */
    public static final DavPropertyName ECLIPSED_SET = DavPropertyName.create("eclipsed-set", DeltaVConstants.NAMESPACE);

    /**
     * If the 'Baseline' feature is supported, DAV:baseline-controlled-collection
     * is a required property of any version-controlled resource, that represents
     * a 'configuration'. It identifies the collection that contains the
     * version-controlled resources whose versions are tracked by this
     * configuration.
     * <p>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT baseline-controlled-collection (href)&gt;
     * </pre>
     * Note that the DAV:baseline-controlled-collection represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     *
     * @see DeltaVConstants#VERSION_CONTROLLED_CONFIGURATION for the corresponding
     * property, that is required for all resources that are contained in this
     * version-controlled-configuration.
     */
    public static final DavPropertyName BASELINE_CONTROLLED_COLLECTION = DavPropertyName.create("baseline-controlled-collection", DeltaVConstants.NAMESPACE);

    /**
     * This property is mandatory for all checked-out version-controlled-configuration
     * resources. It determines the DAV:subbaseline-set property of the baseline
     * that results from checking in this resource.
     * <p>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT subbaseline-set (href*)&gt;
     * </pre>
     * Note that the DAV:baseline-controlled-collection represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     * @see BaselineResource#SUBBASELINE_SET
     */
    public static final DavPropertyName SUBBASELINE_SET = DavPropertyName.create("subbaseline-set", DeltaVConstants.NAMESPACE);

    /**
     * Perform a checkin on the version controlled resource.
     *
     * @return String representing the location of the version created by the
     * checkin.
     * @throws DavException if an error occurs.
     */
    public String checkin() throws DavException;

    /**
     * Perform a checkout on the version controlled resource.
     *
     * @throws DavException
     */
    public void checkout() throws DavException;

    /**
     * Perform an uncheckout on the version controlled resource.
     *
     * @throws DavException
     */
    public void uncheckout() throws DavException;

    /**
     * Perform an update on this resource using the specified {@link UpdateInfo}.
     *
     * @param updateInfo
     * @return <code>MultiStatus</code> containing the list of resources that
     * have been modified by this update call.
     * @throws DavException
     */
    public MultiStatus update(UpdateInfo updateInfo) throws DavException;

    /**
     * Perform a merge on this resource using the specified {@link MergeInfo}.
     *
     * @param mergeInfo
     * @return <code>MultiStatus</code> containing the list of resources that
     * have been modified.
     * @throws DavException
     */
    public MultiStatus merge(MergeInfo mergeInfo) throws DavException;

    /**
     * Modify the labels of the version referenced by the DAV:checked-in property
     * of this checked-in version-controlled resource. If the resource is not
     * checked-in the request must fail.
     *
     * @param labelInfo
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see LabelInfo
     * @see VersionResource#label(LabelInfo) for the pre- and postcondition of
     * a successful LABEL request.
     */
    public void label(LabelInfo labelInfo) throws DavException;

    /**
     * Returns the <code>VersionHistoryResource</code>, that is referenced in the
     * '{@link #VERSION_HISTORY version-history}' property.
     *
     * @return
     * @throws DavException
     */
    public VersionHistoryResource getVersionHistory() throws DavException;
}