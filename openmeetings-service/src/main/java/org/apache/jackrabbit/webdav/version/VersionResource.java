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

/**
 * <code>VersionResource</code> is a resource that contains a copy of a particular
 * state of a version-controlled resource. A new version resource is created whenever
 * a checked-out version-controlled resource is checked-in. The server allocates
 * a distinct new URL for each new version, and this URL will never be used to
 * identify any resource other than that version. The content and dead properties
 * of a version never change.
 * <p>
 * RFC 3253 defines the following required properties for a version resource:
 * <ul>
 * <li>DAV:predecessor-set  (protected)</li>
 * <li>DAV:successor-set  (computed)</li>
 * <li>DAV:checkout-set</li>
 * <li>DAV:version-name</li>
 * <li>DAV:checkout-fork (in-place-checkout or working resource)</li>
 * <li>DAV:checkin-fork (in-place-checkout or working resource)</li>
 * <li>DAV:version-history (version-history)</li>
 * <li>DAV:label-name-set (label)</li>
 * <li>DAV:activity-set (activity)</li>
 * <li>all DeltaV-compliant resource properties.</li>
 * </ul>
 *
 * If the 'Version-Controlled-Collection Feature' is supported (see section 14
 * of RFC 3253) the following protected property is defined for a version
 * resource associated with a version-controlled collection.
 * <ul>
 * <li>DAV:version-controlled-binding-set</li>
 * </ul>
 *
 * <p>
 * In addition a version resource must support the following METHODS:
 * <ul>
 * <li>LABEL (label)</li>
 * <li>CHECKOUT (working-resource)</li>
 * <li>all DeltaV-compliant resource METHODS.</li>
 * </ul>
 *
 * @see DeltaVResource
 */
public interface VersionResource extends DeltaVResource {

    /**
     * The version resource defines one additional method LABEL.
     *
     * @see DeltaVResource#METHODS
     * @see org.apache.jackrabbit.webdav.DavResource#METHODS
     */
    public String METHODS = "LABEL";

    /**
     * Required protected property 'DAV:label-name-set' for a version of a webdav
     * resource introduced with the 'LabelInfo' feature.
     * This property contains the labels that currently select this version.<br>
     * Property structure is defined as follows:<br>
     * <pre>
     * &lt;!ELEMENT label-name-set (label-name*)&gt;
     * &lt;!ELEMENT label-name (#PCDATA)&gt;
     * PCDATA value: string
     * </pre>
     */
    public static final DavPropertyName LABEL_NAME_SET = DavPropertyName.create("label-name-set", DeltaVConstants.NAMESPACE);

    /**
     * The protected DAV:predecessor property identifies each predecessor of
     * this version. Except for the root version, which has no predecessors,
     * each version has at least one predecessor.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT predecessor-set (href*)&gt;
     * </pre>
     */
    public static final DavPropertyName PREDECESSOR_SET = DavPropertyName.create("predecessor-set", DeltaVConstants.NAMESPACE);

    /**
     * The computed property DAV:successor-set identifies each version whose
     * DAV:predecessor-set identifies this version.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT successor-set (href*)&gt;
     * </pre>
     *
     */
    public static final DavPropertyName SUCCESSOR_SET = DavPropertyName.create("successor-set", DeltaVConstants.NAMESPACE);

    /**
     * The computed property  DAV:checkout-set identifies each checked-out
     * resource whose DAV:checked-out property identifies this version.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT checkout-set (href*)&gt;
     * </pre>
     *
     * @see VersionControlledResource#CHECKED_OUT
     */
    public static final DavPropertyName CHECKOUT_SET = DavPropertyName.create("checkout-set", DeltaVConstants.NAMESPACE);

    /**
     * The protected property DAV:version-name defines a human readable id for
     * this version. The id defined to be unique within the version-history this
     * version belongs to.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT version-name (#PCDATA)&gt;
     * PCDATA value: string
     * </pre>
     */
    public static final DavPropertyName VERSION_NAME = DavPropertyName.create("version-name", DeltaVConstants.NAMESPACE);

    /**
     * The computed property DAV:version-history identifies the version history
     * that contains this version.<br>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT version-history (href)&gt;
     * </pre>
     */
    public static final DavPropertyName VERSION_HISTORY = DavPropertyName.create("version-history", DeltaVConstants.NAMESPACE);

    /**
     * This property controls the behavior of CHECKOUT when a version already
     * is checked out or has a successor.
     */
    public static final DavPropertyName CHECKOUT_FORK = DavPropertyName.create("checkout-fork", DeltaVConstants.NAMESPACE);

    /**
     * This property controls the behavior of CHECKIN when a version already
     * has a successor.
     */
    public static final DavPropertyName CHECKIN_FORK = DavPropertyName.create("checkin-fork", DeltaVConstants.NAMESPACE);

    /**
     * DAV:activity-set is a required property for a version resource, if the
     * server supports the activity feature.<br>
     * It identifies the activities that determine to which logical changes this
     * version contributes, and on which lines of descent this version appears.
     * A server MAY restrict the DAV:activity-set to identify a single activity.
     * A server MAY refuse to allow the value of the DAV:activity-set property
     * of a version to be modified.
     * <p>
     * The property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT activity-set (href*)&gt;
     * </pre>
     * Note that the DAV:activity-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}
     */
    public static final DavPropertyName ACTIVITY_SET = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);

    /**
     * If the 'Version-Controlled-Collection Feature' is supported the
     * DAV:version-controlled-binding-set property identifies the name and the
     * version history of all version-controlled internal members of the
     * collection this version resource belongs to.
     * <p>
     * This property is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT version-controlled-binding-set (version-controlled-binding*)&gt;
     * &lt;!ELEMENT version-controlled-binding (binding-name, version-history)&gt;
     * &lt;!ELEMENT binding-name (#PCDATA)&gt;
     * PCDATA value: URL segment
     * &lt;!ELEMENT version-history (href)&gt;
     * </pre>
     *
     * @see VersionControlledResource#ECLIPSED_SET
     */
    public static final DavPropertyName VERSION_CONTROLLED_BINDING_SET = DavPropertyName.create("version-controlled-binding-set", DeltaVConstants.NAMESPACE);

    /**
     * Modify the labels of this version resource. The modifications (SET, ADD or
     * REMOVE) are listed in the specified <code>LabelInfo</code> object.<br>
     * The case of a label name must be preserved when it is stored and retrieved.
     * <br>If the type of modification is ADD, then the label must not yet occur on
     * any other version within the same version history. In contrast a SET
     * modification will move the indicated label to this version, if it existed
     * with another version before. After a successful LABEL request the label
     * must not appear with any other version in the same version history.
     *
     * @param labelInfo
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see LabelInfo
     */
    public void label(LabelInfo labelInfo) throws DavException;

    /**
     * Returns the <code>VersionHistoryResource</code>, that is referenced in the
     * {@link #VERSION_HISTORY DAV:version-history} property.
     *
     * @return
     * @throws DavException
     */
    public VersionHistoryResource getVersionHistory() throws DavException;
}