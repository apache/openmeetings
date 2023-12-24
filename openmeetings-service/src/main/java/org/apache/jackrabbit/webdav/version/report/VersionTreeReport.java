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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>VersionTreeReport</code> encapsulates the DAV:version-tree report.
 * It describes the requested properties of all the versions in the version
 * history of a version. The DAV:version-tree report must be supported by all
 * version resources and all version-controlled resources.
 */
public class VersionTreeReport extends AbstractReport implements DeltaVConstants {

    private static Logger log = LoggerFactory.getLogger(VersionTreeReport.class);

    private ReportInfo info;
    private DavResource resource;

    /**
     * Returns {@link ReportType#VERSION_TREE}
     *
     * @return {@link ReportType#VERSION_TREE}
     * @see Report#getType()
     */
    public ReportType getType() {
        return ReportType.VERSION_TREE;
    }

    /**
     * Always returns <code>true</code>.
     *
     * @return true
     * @see org.apache.jackrabbit.webdav.version.report.Report#isMultiStatusReport()
     */
    public boolean isMultiStatusReport() {
        return true;
    }

    /**
     * Validates the specified resource and info objects.
     *
     * @param resource
     * @param info
     * @throws org.apache.jackrabbit.webdav.DavException
     * @see Report#init(DavResource, ReportInfo)
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        setResource(resource);
        setInfo(info);
    }

    /**
     * Set the <code>DeltaVResource</code> used to register this report.
     *
     * @param resource
     * @throws DavException if the given resource is neither
     * {@link VersionControlledResource} nor {@link VersionResource}.
     */
    private void setResource(DavResource resource) throws DavException {
        if (resource != null && (resource instanceof VersionControlledResource || resource instanceof VersionResource)) {
            this.resource = resource;
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:version-tree report can only be created for version-controlled resources and version resources.");
        }
    }

    /**
     * Set the <code>ReportInfo</code> as specified by the REPORT request body,
     * that defines the details for this report.
     *
     * @param info
     * @throws DavException if the given <code>ReportInfo</code>
     * does not contain a DAV:version-tree element.
     */
    private void setInfo(ReportInfo info) throws DavException {
        if (!getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:version-tree element expected.");
        }
        this.info = info;
    }

    /**
     * Runs the DAV:version-tree report.
     *
     * @return Xml <code>Document</code> representing the report in the required
     * format.
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        return getMultiStatus().toXml(document);
    }

    /**
     * Retrieve the <code>MultiStatus</code> that is returned in response to a locate-by-history
     * report request.
     *
     * @return
     * @throws NullPointerException if info or resource is <code>null</code>.
     */
    private MultiStatus getMultiStatus() {
        if (info == null || resource == null) {
            throw new NullPointerException("Error while running DAV:version-tree report");
        }

        MultiStatus ms = new MultiStatus();
        buildResponse(resource, info.getPropertyNameSet(), info.getDepth(), ms);
        return ms;
    }

    /**
     *
     * @param res
     * @param propNameSet
     * @param depth
     * @param ms
     */
    private void buildResponse(DavResource res, DavPropertyNameSet propNameSet,
                               int depth, MultiStatus ms) {
        try {
            for (VersionResource version : getVersions(res)) {
                if (propNameSet.isEmpty()) {
                    ms.addResourceStatus(version, DavServletResponse.SC_OK, 0);
                } else {
                    ms.addResourceProperties(version, propNameSet, 0);
                }
            }
        } catch (DavException e) {
            log.error(e.toString());
        }
        if (depth > 0 && res.isCollection()) {
            DavResourceIterator it = res.getMembers();
            while (it.hasNext()) {
                buildResponse(it.nextResource(), propNameSet, depth-1, ms);
            }
        }
    }

    /**
     * Retrieve all versions from the version history associated with the given
     * resource. If the versions cannot be retrieved from the given resource
     * an exception is thrown.
     *
     * @param res
     * @return array of {@link VersionResource}s or an empty array if the versions
     * could not be retrieved.
     * @throws DavException if the version history could not be retrieved from
     * the given resource or if an error occurs while accessing the versions
     * from the version history resource.
     */
    private static VersionResource[] getVersions(DavResource res) throws DavException {
        VersionResource[] versions = new VersionResource[0];
        if (res instanceof VersionControlledResource) {
            versions = ((VersionControlledResource)res).getVersionHistory().getVersions();
        } else if (res instanceof VersionResource) {
            versions = ((VersionResource)res).getVersionHistory().getVersions();
        }
        return versions;
    }
}
