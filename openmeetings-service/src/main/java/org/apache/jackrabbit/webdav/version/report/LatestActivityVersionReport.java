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
package org.apache.jackrabbit.webdav.version.report;

import java.util.List;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.ActivityResource;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>LatestActivityVersionReport</code> is applied to a version history to
 * identify the latest version that is selected from that version history by a
 * given activity.
 */
public class LatestActivityVersionReport extends AbstractReport {

    private static Logger log = LoggerFactory.getLogger(LatestActivityVersionReport.class);

    private static final String XML_LATEST_ACTIVITY_VERSION = "latest-activity-version";
    private static final String XML_LATEST_ACTIVITY_VERSION_REPORT = "latest-activity-version-report";

    public static final ReportType LATEST_ACTIVITY_VERSION = ReportType.register(XML_LATEST_ACTIVITY_VERSION, DeltaVConstants.NAMESPACE, LatestActivityVersionReport.class);

    private VersionHistoryResource vhResource;
    private DavResource activity;

    /**
     * Returns {@link #LATEST_ACTIVITY_VERSION}.
     *
     * @see Report#getType()
     */
    public ReportType getType() {
        return LATEST_ACTIVITY_VERSION;
    }

    /**
     * Always returns <code>false</code>.
     *
     * @return false
     * @see Report#isMultiStatusReport()
     */
    public boolean isMultiStatusReport() {
        return false;
    }

    /**
     * Check all the preconditions for this report.
     *
     * @throws DavException if a precondition is not met.
     * @see Report#init(DavResource, ReportInfo)
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        // validate info
        if (!getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:latest-activity-version element expected.");
        }

        // make sure the report is applied to a vh-resource
        if (resource != null && (resource instanceof VersionHistoryResource)) {
            vhResource = (VersionHistoryResource) resource;
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:latest-activity-version report can only be created for a version history resource.");
        }

        // make sure the DAV:href element inside the request body points to
        // an activity resource (precondition for this report).
        String activityHref = normalizeResourceHref(DomUtil.getText(info.getContentElement(DavConstants.XML_HREF, DavConstants.NAMESPACE)));
        DavResourceLocator vhLocator = resource.getLocator();
        DavResourceLocator activityLocator = vhLocator.getFactory().createResourceLocator(vhLocator.getPrefix(), activityHref);

        activity = resource.getFactory().createResource(activityLocator, resource.getSession());
        if (!(activity instanceof ActivityResource)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:latest-activity-version report: The DAV:href in the request body MUST identify an activity.");
        }
    }

    /**
     *
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        String latestVersionHref = getLatestVersionHref();

        Element el = DomUtil.createElement(document, XML_LATEST_ACTIVITY_VERSION_REPORT, DeltaVConstants.NAMESPACE);
        el.appendChild(DomUtil.hrefToXml(latestVersionHref, document));
        return el;
    }

    /**
     * The latest-version-href MUST identify the version of the given
     * version history that is a member of the DAV:activity-version-set of the
     * given activity and has no descendant that is a member of the
     * DAV:activity-version-set of that activity.
     *
     * @return href of the latest version or ""
     */
    private String getLatestVersionHref() {
        String latestVersionHref = ""; // not found (TODO: check if this valid according to the RFC)
        try {
            List<String> versionHrefs = new HrefProperty(activity.getProperty(ActivityResource.ACTIVITY_VERSION_SET)).getHrefs();
            
            for (VersionResource vr : vhResource.getVersions()) {
                String href = vr.getHref();
                if (versionHrefs.contains(href)) {
                    if ("".equals(latestVersionHref)) {
                        // shortcut
                        latestVersionHref = href;
                    } else {
                        // if this vr is a descendant of the one already found, set latestVersion again
                        List<String> predecessors = new HrefProperty(vr.getProperty(VersionResource.PREDECESSOR_SET)).getHrefs();
                        if (predecessors.contains(latestVersionHref)) {
                            // version is a descendant of the vr identified by latestVersionHref
                            latestVersionHref = href;
                        } // else: version is predecessor -> nothing to do.
                    }
                }
            }

        } catch (DavException e) {
            log.error("Unexpected error while retrieving href of latest version.", e);
        }
        return latestVersionHref;
    }
}
