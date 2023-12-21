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
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Element;

/**
 * <code>PrincipalMatchReport</code> can be request for any collection resources.
 * The resulting report identifies member resources that either represent the
 * requesting principal ("principal resources") or contain a specified property
 * that matches the requesting principal in its value. For the first match
 * the request body must contain a DAV:self element, for the latter a
 * DAV:principal-property element which in turn specifies the property to
 * be examined.
 * <p>
 * The request body MUST be a DAV:principal-match XML element:
 * <pre>
 * &lt;!ELEMENT principal-match ((principal-property | self), prop?)&gt;
 * &lt;!ELEMENT principal-property ANY&gt;
 * ANY value: an element whose value identifies a property. The value of this
 * property typically contains an href element referring to a principal.
 * &lt;!ELEMENT self EMPTY&gt;
 * prop: see RFC 2518, Section 12.11
 * </pre>
 * The response body of a successful report must contain a DAV:multistatus
 * element. Each matching member is present with a separate DAV:response element.
 */
public class PrincipalMatchReport extends AbstractSecurityReport {

    public static final String XML_PRINCIPAL_PROPERTY = "principal-property";
    public static final String XML_SELF = "self";

    /**
     * The report name
     */
    public static final String REPORT_NAME = "principal-match";

    /**
     * The report type
     */
    public static final ReportType REPORT_TYPE = ReportType.register(REPORT_NAME, SecurityConstants.NAMESPACE, PrincipalMatchReport.class);

    private DavPropertyName principalPropertyName;

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
        if (info.containsContentElement(XML_PRINCIPAL_PROPERTY, SecurityConstants.NAMESPACE)) {
            Element pp = info.getContentElement(XML_PRINCIPAL_PROPERTY, SecurityConstants.NAMESPACE);
            principalPropertyName = DavPropertyName.createFromXml(DomUtil.getFirstChildElement(pp));
        } else if (info.containsContentElement(XML_SELF, SecurityConstants.NAMESPACE)) {
            principalPropertyName = SecurityConstants.PRINCIPAL_URL;
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:self or DAV:principal-property element required within report info.");
        }
    }

    //------------------------------------< implementation specific methods >---
    /**
     * Retrieve the property name that indicates which property must be search
     * for matching principals.<br>
     * Note, that the search result must be converted to {@link MultiStatusResponse}s
     * that must be returned back to this report.
     *
     * @return property name which should be check for match with the current
     * user.
     * @see #setResponses(MultiStatusResponse[])
     */
    public DavPropertyName getPrincipalPropertyName() {
        return principalPropertyName;
    }

    /**
     * Write the result(s) of the match back to the report.
     *
     * @param responses
     */
    public void setResponses(MultiStatusResponse[] responses) {
       this.responses = responses;
    }
}
