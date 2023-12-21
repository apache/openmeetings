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

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>DeltaVConstants</code> defines the following headers and properties
 * required for any resource that is compliant to
 * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>:<br><br>
 *
 * Headers:
 * <pre>
 * Label
 * </pre>
 *
 * Properties:
 * <pre>
 * DAV:comment
 * DAV:creator-displayname
 * DAV:supported-method-set
 * DAV:supported-live-property-set
 * DAV:supported-report-set
 * </pre>
 *
 * Some additional resource properties are defined by the various advanced
 * version features:
 * <pre>
 * DAV:workspace (workspace feature)
 * DAV:version-controlled-configuration (baseline)
 * </pre>
 */
public interface DeltaVConstants {

    /**
     * The DAV: namespace.
     */
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

    //---< Headers >------------------------------------------------------------
    /**
     * For certain METHODS, if the request-URL identifies a version-controlled
     * resource, a label can be specified in a LabelInfo request header to cause the
     * method to be applied to the version selected by that label.<br>
     * LabelInfo header MUST have no effect on a request whose request-URL does not
     * identify a version-controlled resource. In particular, it MUST have no
     * effect on a request whose request-URL identifies a version or a version
     * history.
     */
    public static final String HEADER_LABEL = "Label";

    /**
     * Location header as defined by
     * <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a>. In the versioning
     * context it is used to indicate the location of the new version created by a
     * successful checkin in the response.<br><br>
     * From RFC 2616:<br>
     * The Location response-header field is used to redirect the recipient to a
     * location other than the Request-URI for completion of the request or
     * identification of a new resource.<br>
     * For 201 (Created) responses, the Location is that of the new resource
     * which was created by the request.
     */
    public static final String HEADER_LOCATION = "Location";

    //---< Property Names >-----------------------------------------------------
    /**
     * The "DAV:comment" property is used to track a brief comment about a resource that is
     * suitable for presentation to a user. The DAV:comment of a version can be
     * used to indicate why that version was created.
     */
    public static final DavPropertyName COMMENT = DavPropertyName.create("comment", NAMESPACE);

    /**
     * The "DAV:creator-displayname" property contains a description of the creator of
     * the resource that is suitable for presentation to a user. The
     * DAV:creator-displayname of a version can be used to indicate who created
     * that version.
     */
    public static final DavPropertyName CREATOR_DISPLAYNAME = DavPropertyName.create("creator-displayname", NAMESPACE);

    /**
     * Required protected live property for any resources being compliant with
     * RFC 3253. Clients should classify a resource by examine the values of the
     * DAV:supported-method-set and DAV:supported-live-property-set
     * properties of that resource.<br>
     * Property structure:
     * <pre>
     * &lt;!ELEMENT supported-method-set (supported-method*)&gt;
     * &lt;!ELEMENT supported-method ANY&gt;
     * &lt;!ATTLIST supported-method name NMTOKEN #REQUIRED&gt;
     * name value: a method name
     * </pre>
     *
     * @see #SUPPORTED_LIVE_PROPERTY_SET
     */
    public static final DavPropertyName SUPPORTED_METHOD_SET = DavPropertyName.create("supported-method-set", NAMESPACE);

    /**
     * Required protected live property for any resources being compliant with
     * RFC 3253. Clients should classify a resource by examine the values of the
     * DAV:supported-method-set and DAV:supported-live-property-set
     * properties of that resource.<br>
     * Property structure:
     * <pre>
     * &lt;!ELEMENT supported-live-property-set (supported-live-property*)&gt;
     * &lt;!ELEMENT supported-live-property name&gt;
     * &lt;!ELEMENT prop ANY&gt;
     * ANY value: a property element type
     * </pre>
     *
     * @see #SUPPORTED_METHOD_SET
     */
    public static final DavPropertyName SUPPORTED_LIVE_PROPERTY_SET = DavPropertyName.create("supported-live-property-set", NAMESPACE);

    /**
     * Protected "supported-report-set" property identifies the reports that are
     * supported by the resource.
     *
     * @see #SUPPORTED_REPORT_SET
     */
    public static final DavPropertyName SUPPORTED_REPORT_SET = DavPropertyName.create("supported-report-set", NAMESPACE);

    /**
     * Protected "workspace" property indicating the workspace of a resource.
     * This property is required for all resources if (but only if) the workspace
     * feature is supported.
     * <p>
     * Note that the DAV:activity-version-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}.
     * It is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT workspace (href)&gt;
     * </pre>
     *
     * @see WorkspaceResource
     */
    public static final DavPropertyName WORKSPACE = DavPropertyName.create("workspace", NAMESPACE);

    /**
     * The Baseline feature introduces the computed DAV:version-controlled-configuration
     * property for all resources that are member of a version-controlled
     * configuration. This may be the case if the resource is a collection under
     * baseline control or is a member of a collection under baseline control.
     * <p>
     * Note that the DAV:activity-version-set represents a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty HrefProperty}.
     * It is defined to have the following format:
     * <pre>
     * &lt;!ELEMENT version-controlled-configuration (href)&gt;
     * </pre>
     */
    public static final DavPropertyName VERSION_CONTROLLED_CONFIGURATION = DavPropertyName.create("version-controlled-configuration", NAMESPACE);


    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * Xml elements
     */
    public static final String XML_ACTIVITY = "activity";
    public static final String XML_BASELINE = "baseline";

    public static final String XML_SUPPORTED_METHOD = "supported-method";
    public static final String XML_VERSION_HISTORY = "version-history";
    public static final String XML_VERSION = "version";
    public static final String XML_WORKSPACE = "workspace";

    // options
    /**
     * If the OPTIONS request contains a body, i must start with an DAV:options
     * element.
     *
     * @see OptionsInfo
     * @see #XML_VH_COLLECTION_SET
     * @see #XML_WSP_COLLECTION_SET
     * @see #XML_ACTIVITY_COLLECTION_SET
     */
    public static final String XML_OPTIONS = "options";

    /**
     * If an XML response body for a successful request is included, it must be
     * a DAV:options-response XML element.
     *
     * @see OptionsResponse
     */
    public static final String XML_OPTIONS_RESPONSE = "options-response";

    /**
     * A DAV:version-history-collection-set element may be included in the OPTIONS
     * request  body to identify collections that may contain version history
     * resources.<br>
     * The response body for a successful request must in consequence contain a
     * DAV:version-history-collection-set element identifying collections that
     * may contain version histories. An identified collection may be the root
     * collection of a tree of collections, all of which may contain version
     * histories.
     *
     * <pre>
     * &lt;!ELEMENT version-history-collection-set (href*)&gt;
     * </pre>
     */
    public static final String XML_VH_COLLECTION_SET = "version-history-collection-set";

    /**
     * A DAV:workspace-collection-set element may be included in the OPTIONS request
     * body to identify collections that may contain workspace resources.<br>
     * The response body for a successful request must contain a
     * DAV:workspace-collection-set element identifying collections that may
     * contain workspaces. An identified collection may be the root collection
     * of a tree of collections, all of which may contain workspaces.
     *
     * <pre>
     * &lt;!ELEMENT workspace-collection-set (href*)&gt;
     * </pre>
     */
    public static final String XML_WSP_COLLECTION_SET = "workspace-collection-set";

    /**
     * A DAV:workspace-collection-set element may be included in the OPTIONS request
     * body to identify collections that may contain activity resources.<br>
     * The response body for a successful request must contain a
     * DAV:workspace-collection-set element identifying collections that may
     * contain activity resources. An identified collection may be the root collection
     * of a tree of collections, all of which may contain activity resources.
     *
     * <pre>
     * &lt;!ELEMENT activity-collection-set (href*)&gt;
     * </pre>
     */
    public static final String XML_ACTIVITY_COLLECTION_SET = "activity-collection-set";

    /**
     * Name of Xml element contained in the {@link #SUPPORTED_REPORT_SET} property.
     *
     * @see #SUPPORTED_REPORT_SET
     * @see org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty
     */
    public static final String XML_SUPPORTED_REPORT = "supported-report";

    /**
     * Name of Xml child elements of {@link #XML_SUPPORTED_REPORT}.
     *
     * @see org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty
     */
    public static final String XML_REPORT = "report";

    /**
     * Top element for the 'DAV:version-tree' report
     */
    public static final String XML_VERSION_TREE = "version-tree";

    /**
     * Top element for the 'DAV:expand-property' report
     */
    public static final String XML_EXPAND_PROPERTY = "expand-property";

    /**
     * 'DAV:property' element to be used inside the 'DAV:expand-property' element.
     *
     * @see #XML_EXPAND_PROPERTY
     */
    public static final String XML_PROPERTY = "property";

    /**
     * 'DAV:name' attribute for the property element
     *
     * @see #XML_PROPERTY
     */
    public static final String ATTR_NAME = "name";

    /**
     * 'DAV:namespace' attribute for the property element
     *
     * @see #XML_PROPERTY
     */
    public static final String ATTR_NAMESPACE = "namespace";

    /**
     * Top element for the 'DAV:locate-by-history' report
     */
    public static final String XML_LOCATE_BY_HISTORY = "locate-by-history";

    /**
     * 'DAV:version-history-set' to be used inside the 'DAV:locate-by-history'
     * element
     *
     * @see #XML_LOCATE_BY_HISTORY
     */
    public static final String XML_VERSION_HISTORY_SET = "version-history-set";


    /**
     * Xml element representing the mandatory root element of a LABEL request
     * body.
     *
     * @see #XML_LABEL_NAME
     * @see #XML_LABEL_ADD
     * @see #XML_LABEL_REMOVE
     * @see #XML_LABEL_SET
     * @see LabelInfo
     */
    public static final String XML_LABEL = "label";
    public static final String XML_LABEL_NAME = "label-name";
    public static final String XML_LABEL_ADD = "add";
    public static final String XML_LABEL_REMOVE = "remove";
    public static final String XML_LABEL_SET = "set";

    /**
     * Xml element defining the top element in the UPDATE request body. RFC 3253
     * defines the following structure for the 'update' element.
     * <pre>
     * &lt;!ELEMENT update ANY&gt;
     * ANY value: A sequence of elements with at most one DAV:version element
     * and at most one DAV:prop element.
     * &lt;!ELEMENT version (href)&gt;
     * prop: see RFC 2518, Section 12.11
     * </pre>
     */
    public static final String XML_UPDATE = "update";

    // auto-version
    /**
     * Value for the DAV:auto-version property indicating that any modification
     * (such as PUT/PROPPATCH) applied to a checked-in version-controlled
     * resource will automatically be preceded by a checkout and followed by a
     * checkin operation.<br>
     * See also <a href="http://www.webdav.org/specs/rfc3253.html#PROPERTY_auto-version">RFC 3253 DAV:auto-version</a>
     */
    public static final String XML_CHECKOUT_CHECKIN = "checkout-checkin";
    /**
     * Value for the DAV:auto-version property indicating that any modification
     * (such as PUT/PROPPATCH) applied to a checked-in version-controlled
     * resource will automatically be preceded by a checkout operation.
     * If the resource is not write-locked, the request is automatically
     * followed by a checkin operation.<br>
     * See also <a href="http://www.webdav.org/specs/rfc3253.html#PROPERTY_auto-version">RFC 3253 DAV:auto-version</a>
     */
    public static final String XML_CHECKOUT_UNLOCK_CHECKIN = "checkout-unlocked-checkin";
    /**
     * Value for the DAV:auto-version property indicating that any modification
     * (such as PUT/PROPPATCH) applied to a checked-in version-controlled
     * resource will automatically be preceded by a checkout operation.<br>
     * See also <a href="http://www.webdav.org/specs/rfc3253.html#PROPERTY_auto-version">RFC 3253 DAV:auto-version</a>
     */
    public static final String XML_CHECKOUT = "checkout";
    /**
     * Value for the DAV:auto-version property indicating that any modification
     * (such as PUT/PROPPATCH) applied to a write-locked checked-in version-controlled
     * resource will automatically be preceded by a checkout operation.<br>
     * See also <a href="http://www.webdav.org/specs/rfc3253.html#PROPERTY_auto-version">RFC 3253 DAV:auto-version</a>
     */
    public static final String XML_LOCKED_CHECKIN = "locked-checkout";

    // merge
    public static final String XML_MERGE = "merge";
    public static final String XML_N0_AUTO_MERGE = "no-auto-merge";
    public static final String XML_N0_CHECKOUT = "no-checkout";
}