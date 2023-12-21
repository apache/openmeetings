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
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

/**
 * The <code>DeltaVResource</code> encapsulates the functionality common to all
 * DeltaV compliant resources.
 * <p>
 * RFC 3253 defines the following required properties:
 * <ul>
 * <li>{@link DeltaVConstants#COMMENT DAV:comment}</li>
 * <li>{@link DeltaVConstants#CREATOR_DISPLAYNAME DAV:creator-displayname}</li>
 * <li>{@link DeltaVConstants#SUPPORTED_METHOD_SET DAV:supported-method-set}</li>
 * <li>{@link DeltaVConstants#SUPPORTED_LIVE_PROPERTY_SET DAV:supported-live-property-set}</li>
 * <li>{@link DeltaVConstants#SUPPORTED_REPORT_SET DAV:supported-report-set}</li>
 * <li>all properties defined in WebDAV [RFC2518].</li>
 * </ul>
 * <p>
 * In addition a DeltaV compliant resource must support the following METHODS:
 * <ul>
 * <li>REPORT</li>
 * <li>all METHODS defined in WebDAV [RFC2518]</li>
 * <li>all METHODS defined in HTTP/1.1 [RFC2616].</li>
 * </ul>
 *
 * @see DavResource
 */
public interface DeltaVResource extends DavResource {

    /**
     * The generic deltaV compliant resource defines one additional method REPORT.
     *
     * @see org.apache.jackrabbit.webdav.DavResource#METHODS
     */
    public String METHODS = "REPORT";

    /**
     * If the server support the Workspace featured defined by RFC 3253 certain
     * <code>DeltaVResource</code>s may also support the MKWORKSPACE method.
     *
     * @see #addWorkspace(DavResource)
     */
    public String METHODS_INCL_MKWORKSPACE = "REPORT, MKWORKSPACE";

    /**
     * Retrieves the information requested in the OPTIONS request body and
     * returns the corresponding values.
     *
     * @param optionsInfo
     * @return object to be included to the OPTIONS response body or <code>null</code>
     * if the specified optionsInfo was <code>null</code> or empty.
     */
    public OptionsResponse getOptionResponse(OptionsInfo optionsInfo);

    /**
     * Runs the report specified by the given <code>ReportInfo</code>.
     *
     * @param reportInfo
     * @return the requested report.
     * @throws DavException in case an error occurred or if the specified <code>ReportInfo</code>
     * is either not valid or cannot be run by the given resource.
     */
    public Report getReport(ReportInfo reportInfo) throws DavException;

    /**
     * Add a new member to this resource, that represents a workspace.<br>
     * Please note that no resource must exist at the location of the new workspace.
     *
     * @param workspace resource representing the new workspace to be created as
     * member of this resource.
     * @throws DavException if creating the new workspace fails.
     */
    // TODO: MKWORKSPACE may include an xml request body...
    public void addWorkspace(DavResource workspace) throws DavException;

    /**
     * Returns an array of <code>DavResource</code> objects that are referenced
     * by the {@link org.apache.jackrabbit.webdav.property.HrefProperty} with
     * the specified {@link DavPropertyName name}.
     *
     * @param hrefPropertyName
     * @return An array of <code>DavResource</code>s
     * @throws DavException if the given hrefPropertyName does point to an
     * unknown property or does not represent the name of a
     * {@link org.apache.jackrabbit.webdav.property.HrefProperty href property}.
     * Finally the exception may be caused if the property contains the href
     * of a non-existing resource, which cannot be resolved.
     * @see org.apache.jackrabbit.webdav.property.HrefProperty
     */
    public DavResource[] getReferenceResources(DavPropertyName hrefPropertyName) throws DavException;
}