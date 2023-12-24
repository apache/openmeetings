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
package org.apache.jackrabbit.webdav.security.report;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>AbstractSecurityReport</code> covers basic validation and utilities
 * common to the majority of the reports defined within RFC 3744.
 */
public abstract class AbstractSecurityReport extends AbstractReport {

    protected MultiStatusResponse[] responses;

    /**
     * Always returns true.
     *
     * @return true
     */
    public boolean isMultiStatusReport() {
        return true;
    }

    /**
     * Checks if the given resource and report info are not <code>null</code>,
     * that the requested report type matches this implementation and that no
     * other Depth header than 0 is present.
     *
     * @param resource
     * @param info
     * @throws DavException
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (resource == null || info == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "Unable to run report: WebDAV Resource and ReportInfo must not be null.");
        }
        if (!getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "Expected report type: '" + getType().getReportName() + "', found: '" + info.getReportName() + ";" + "'.");
        }
        if (info.getDepth() > DavConstants.DEPTH_0) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "Invalid Depth header: " + info.getDepth());
        }
    }

    /**
     * @return DAV:multistatus element listing the matching resources.
     * @see Report#toXml(Document)
     */
    public Element toXml(Document document) {
        MultiStatus ms = new MultiStatus();
        if (responses != null) {
            for (MultiStatusResponse response : responses) {
                ms.addResponse(response);
            }
        }
        return ms.toXml(document);
    }
}
