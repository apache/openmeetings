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

import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.BaselineResource;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>CompareBaselineReport</code>...
 */
public class CompareBaselineReport extends AbstractReport {

    private static Logger log = LoggerFactory.getLogger(CompareBaselineReport.class);

    private static final String XML_COMPARE_BASELINE = "compare-baseline";
    private static final String XML_COMPARE_BASELINE_REPORT = "compare-baseline-report";
    private static final String XML_ADDED_VERSION = "added-version";
    private static final String XML_DELETED_VERSION = "deleted-version";
    private static final String XML_CHANGED_VERSION = "changed-version";

    public static final ReportType COMPARE_BASELINE = ReportType.register(XML_COMPARE_BASELINE, DeltaVConstants.NAMESPACE, CompareBaselineReport.class);

    private BaselineResource requestBaseline;
    private BaselineResource compareBaseline;

    /**
     * Returns {@link #COMPARE_BASELINE}.
     *
     * @see Report#getType()
     */
    public ReportType getType() {
        return COMPARE_BASELINE;
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
     *
     * @param resource
     * @param info
     * @throws DavException
     * @see Report#init(DavResource, ReportInfo)
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        // validate info
        if (!getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:compare-baseline element expected.");
        }

        // make sure the report is applied to a version history resource
        if (resource != null && (resource instanceof BaselineResource)) {
            this.requestBaseline = (BaselineResource) resource;
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:compare-baseline report can only be created for a baseline resource.");
        }

        // make sure the DAV:href element inside the request body points to
        // an baseline resource (precondition for this report).
        String compareHref = normalizeResourceHref(DomUtil.getText(info.getContentElement(DavConstants.XML_HREF, DavConstants.NAMESPACE)));
        DavResourceLocator locator = resource.getLocator();
        DavResourceLocator compareLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), compareHref);

        DavResource compRes = resource.getFactory().createResource(compareLocator, resource.getSession());
        if (compRes instanceof BaselineResource) {
            compareBaseline = (BaselineResource) compRes;
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:latest-activity-version report: The DAV:href in the request body MUST identify an activity.");
        }

        // TODO: eventually add check for 'same-baseline-history' (RFC: "A server MAY require that the baselines being compared be from the same baseline history.")
    }

    /**
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element el = DomUtil.createElement(document, XML_COMPARE_BASELINE_REPORT, DeltaVConstants.NAMESPACE);
        try {
            // TODO: check if correct
            List<VersionResource> requestVs = new ArrayList<VersionResource>();
            getVersions(requestBaseline.getBaselineCollection(), requestVs);

            List<VersionResource> compareVs = new ArrayList<VersionResource>();
            getVersions(compareBaseline.getBaselineCollection(), compareVs);

            for (VersionResource requestV : requestVs) {
                if (!compareVs.remove(requestV)) {
                    // check if another version of the same vh is present (change)
                    VersionResource changedV = findChangedVersion(requestV, compareVs);
                    if (changedV != null) {
                        // found a 'changed-version' entry
                        Element cv = DomUtil.addChildElement(el, XML_CHANGED_VERSION, DeltaVConstants.NAMESPACE);
                        cv.appendChild(DomUtil.hrefToXml(requestV.getHref(), document));
                        cv.appendChild(DomUtil.hrefToXml(changedV.getHref(), document));
                    } else {
                        // no corresponding version => 'deleted-version'
                        Element cv = DomUtil.addChildElement(el, XML_DELETED_VERSION, DeltaVConstants.NAMESPACE);
                        cv.appendChild(DomUtil.hrefToXml(requestV.getHref(), document));
                    }

                } // else: both baseline contain a vc-resource with the same checked-in version
            }

            // all remaining versions from the 'compare-baseline' can be considered
            // to be added-versions.
            for (VersionResource addedV : compareVs) {
                Element cv = DomUtil.addChildElement(el, XML_ADDED_VERSION, DeltaVConstants.NAMESPACE);
                cv.appendChild(DomUtil.hrefToXml(addedV.getHref(), document));
            }
        } catch (DavException e) {
            log.error("Internal error while building report", e);
        }
        return el;
    }

    private void getVersions(DavResource collection, List<VersionResource> vList) throws DavException {
        DavResourceIterator it = collection.getMembers();
        while (it.hasNext()) {
            DavResource member = it.nextResource();
            if (member instanceof VersionControlledResource) {
                String href = new HrefProperty(member.getProperty(VersionControlledResource.CHECKED_IN)).getHrefs().get(0);
                DavResourceLocator locator = member.getLocator();
                DavResourceLocator vLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), href);

                DavResource v = member.getFactory().createResource(vLocator, member.getSession());
                if (v instanceof VersionResource) {
                    vList.add((VersionResource) v);
                } else {
                    log.error("Internal error: DAV:checked-in property must point to a VersionResource.");
                }
            }
            if (member.isCollection()) {
                getVersions(member, vList);
            }
        }
    }

    private VersionResource findChangedVersion(VersionResource requestV, List<VersionResource> compareVs) throws DavException {
        VersionResource[] vs = requestV.getVersionHistory().getVersions();
        for (VersionResource v : vs) {
            if (compareVs.remove(v)) {
                // another version of the same versionhistory is present among
                // the compare-baseline versions.
                return v;
            }
        }
        return null;
    }
}
