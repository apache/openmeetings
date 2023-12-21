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

import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>LocateByHistoryReport</code> encapsulates the DAV:locate-by-hisotry
 * report, that may be used to locate a version-controlled resource for that
 * version history. The DAV:locate-by-history report can be applied to a collection
 * to locate the collection member that is a version-controlled resource for a
 * specified version history resource.
 *
 * <pre>
 * &lt;!ELEMENT locate-by-history (version-history-set, prop)&gt;
 * &lt;!ELEMENT version-history-set (href+)&gt;
 * </pre>
 */
public class LocateByHistoryReport extends AbstractReport implements DeltaVConstants {

    private static Logger log = LoggerFactory.getLogger(LocateByHistoryReport.class);

    private ReportInfo info;
    private Set<String> vhHrefSet = new HashSet<String>();
    private DavResource resource;

    /**
     * Returns {@link ReportType#LOCATE_BY_HISTORY}.
     *
     * @see Report#getType()
     */
    public ReportType getType() {
        return ReportType.LOCATE_BY_HISTORY;
    }

    /**
     * @see Report#init(DavResource, ReportInfo)
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (resource == null || !(resource instanceof VersionControlledResource)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:version-tree report can only be created for version-controlled resources and version resources.");
        }
        this.resource = resource;
        setInfo(info);
    }

    /**
     * Set the <code>ReportInfo</code>
     *
     * @param info
     * @throws DavException if the given <code>ReportInfo</code>
     * does not contain a DAV:version-tree element.
     */
    private void setInfo(ReportInfo info) throws DavException {
        if (info == null || !getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:locate-by-history element expected.");
        }
        Element versionHistorySet = info.getContentElement(XML_VERSION_HISTORY_SET, NAMESPACE);
        if (versionHistorySet == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "The DAV:locate-by-history element must contain a DAV:version-history-set child.");
        }
        ElementIterator it = DomUtil.getChildren(versionHistorySet, DavConstants.XML_HREF, DavConstants.NAMESPACE);
        while (it.hasNext()) {
            String href = DomUtil.getText(it.nextElement());
            if (href != null) {
                vhHrefSet.add(href);
            }
        }
        this.info = info;
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
     * Run the report.
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
        MultiStatus ms = new MultiStatus();
        buildResponse(resource, info.getPropertyNameSet(), info.getDepth(), ms);
        return ms;
    }

    /**
     * Fill the <code>MultiStatus</code> with the <code>MultiStatusResponses</code>
     * generated for the specified resource and its members according to the
     * depth value.
     *
     * @param res
     * @param propNameSet
     * @param depth
     * @param ms
     */
    private void buildResponse(DavResource res, DavPropertyNameSet propNameSet,
                               int depth, MultiStatus ms) {
        // loop over members first, since this report only list members
        DavResourceIterator it = res.getMembers();
        while (!vhHrefSet.isEmpty() && it.hasNext()) {
            DavResource childRes = it.nextResource();
            if (childRes instanceof VersionControlledResource) {
                try {
                    VersionHistoryResource vhr = ((VersionControlledResource)childRes).getVersionHistory();
                    if (vhHrefSet.remove(vhr.getHref())) {
                        if (propNameSet.isEmpty()) {
                            ms.addResourceStatus(childRes, DavServletResponse.SC_OK, 0);
                        } else {
                            ms.addResourceProperties(childRes, propNameSet, 0);
                        }
                    }
                } catch (DavException e) {
                    log.info(e.getMessage());
                }
            }
            // traverse subtree
            if (depth > 0) {
                buildResponse(it.nextResource(), propNameSet, depth-1, ms);
            }
        }
    }
}
