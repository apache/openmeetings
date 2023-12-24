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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The AclPrincipalReport report returns the requested property set
 * for all principals in the DAV:acl property, that are identified by http(s)
 * URLs or by a DAV:property principal.
 * <p>
 * The request body MUST be a DAV:acl-principal-prop-set XML element:
 * <pre>
 * &lt;!ELEMENT acl-principal-prop-set ANY&gt;
 * ANY value: a sequence of one or more elements, with at most one
 *            DAV:prop element.
 * prop: see RFC 2518, Section 12.11
 * </pre>
 * The response body MUST be a DAV:multistatus element containing a
 * DAV:response element for each principal identified by a http(s) URL listed
 * in a DAV:principal XML element of an ACE within the DAV:acl property of the
 * resource this report is requested for.
 */
public class AclPrincipalReport extends AbstractSecurityReport {

    public static final String REPORT_NAME = "acl-principal-prop-set";

    /**
     * The report type
     */
    public static final ReportType REPORT_TYPE = ReportType.register(REPORT_NAME, SecurityConstants.NAMESPACE, AclPrincipalReport.class);

    /**
     * @see Report#getType()
     */
    public ReportType getType() {
        return REPORT_TYPE;
    }

    /**
     * @see Report#init(DavResource, ReportInfo)
     */
    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        // build the DAV:responses objects.
        DavProperty<?> acl = resource.getProperty(SecurityConstants.ACL);
        if (!(acl instanceof AclProperty)) {
            throw new DavException(DavServletResponse.SC_INTERNAL_SERVER_ERROR, "DAV:acl property expected.");
        }

        DavResourceLocator loc = resource.getLocator();
        Map<String, MultiStatusResponse> respMap = new HashMap<String, MultiStatusResponse>();
        List<AclProperty.Ace> list = (List<AclProperty.Ace>) ((AclProperty)acl).getValue();
        for (AclProperty.Ace ace : list) {
            String href = normalizeResourceHref(ace.getPrincipal().getHref());
            if (href == null || respMap.containsKey(href)) {
                // ignore non-href principals and principals that have been listed before
                continue;
            }
            // href-principal that has not been found before
            DavResourceLocator princLocator = loc.getFactory().createResourceLocator(loc.getPrefix(), href);
            DavResource principalResource = resource.getFactory().createResource(princLocator, resource.getSession());
            respMap.put(href, new MultiStatusResponse(principalResource, info.getPropertyNameSet()));
        }
        this.responses = respMap.values().toArray(new MultiStatusResponse[respMap.size()]);
    }
}
