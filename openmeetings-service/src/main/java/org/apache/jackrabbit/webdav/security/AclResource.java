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
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

/**
 * <code>AclResource</code>...
 */
public interface AclResource extends DavResource {

    /**
     * The AclResource must support the ACL method and the REPORT method in order
     * to retrieve various security related reports.
     *
     * @see DeltaVResource#METHODS
     * @see org.apache.jackrabbit.webdav.DavResource#METHODS
     */
    public String METHODS = "ACL, REPORT";

    /**
     * Modify the DAV:acl property of this resource object.<br>
     * Note: RFC 3744 limits modification of access control elements (ACEs) to
     * elements that are neither inherited nor protected.
     *
     * @param aclProperty DAV:acl property listing the set of ACEs to be modified
     * by this call. This may be a subset of all access control elements
     * present on this resource object only.
     * @throws DavException If the request fails. RFC 3744 defines a set of
     * preconditions which must be met for a successful ACL request.
     * If these conditions are violated, this method must fail with
     * {@link DavServletResponse#SC_FORBIDDEN 403 (Forbidden)} or
     * {@link DavServletResponse#SC_CONFLICT 409 (Conflict)} and should provide
     * a detailed error condition in the response body. See
     * <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744 Section 8.1.1
     * (ACL Preconditions)</a> for further details.
     */
    public void alterAcl(AclProperty aclProperty) throws DavException;

    /**
     * Same as {@link DeltaVResource#getReport(ReportInfo)}.
     *
     * @param reportInfo specifying the report details retrieved from the REPORT
     * request.
     * @return the requested report.
     * @throws DavException in case an error occurred or if the specified
     * <code>ReportInfo</code> is either not valid or cannot be run by this
     * resource.
     */
    public Report getReport(ReportInfo reportInfo) throws DavException;
}
