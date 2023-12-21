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

import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>SearchablePropertyReport</code> identifies those properties that may be
 * searched using the {@link PrincipalSearchReport DAV:principal-property-search REPORT}.
 * This report must be supported on all collections identified in the value of
 * a DAV:principal-collection-set property.
 * <p>
 * The request body MUST be an empty DAV:principal-search-property-set element.
 * <p>
 * The response body MUST be a DAV:principal-search-property-set XML element
 * with the following structure:
 * <pre>
 *  &lt;!ELEMENT principal-search-property-set (principal-search-property*) &gt;
 *  &lt;!ELEMENT principal-search-property (prop, description) &gt;
 *  prop: see RFC 2518, Section 12.11
 *  &lt;!ELEMENT description #PCDATA &gt;
 *  Human readable description. Note, that the language of the description must
 *  be indicated by the xml:lang attribute.
 * </pre>
 *
 * Note that a DAV:principal-search-property XML element is required for each
 * property that may be searched with the DAV:principal-property-search REPORT.
 */
public class SearchablePropertyReport extends AbstractReport {

    /**
     * The report name
     */
    public static final String REPORT_NAME = "principal-search-property-set";

    /**
     * The report type
     */
    public static final ReportType REPORT_TYPE = ReportType.register(REPORT_NAME, SecurityConstants.NAMESPACE, SearchablePropertyReport.class);

    /**
     * Constant used for the DAV:principal-search-property-set response element.
     */
    public static final String XML_PRINCIPAL_SEARCH_PROPERTY_SET = "principal-search-property-set";

    /**
     * Set collecting the DAV:principal-search-property entries.
     */
    private final Set<PrincipalSearchProperty> searchPropertySet = new HashSet<PrincipalSearchProperty>();

    /**
     * @see Report#getType()
     */
    public ReportType getType() {
        return REPORT_TYPE;
    }

    /**
     * @return false Status code of after a successful completion must be
     * {@link DavServletResponse#SC_OK 200 (ok)}.
     * @see Report#isMultiStatusReport()
     */
    public boolean isMultiStatusReport() {
        return false;
    }

    /**
     * @see Report#init(DavResource, ReportInfo)
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
     * @see Report#toXml(Document)
     */
    public Element toXml(Document document) {
        Element rootElem = DomUtil.createElement(document, XML_PRINCIPAL_SEARCH_PROPERTY_SET, SecurityConstants.NAMESPACE);
        for (PrincipalSearchProperty psp : searchPropertySet) {
            rootElem.appendChild(psp.toXml(document));
        }
        return rootElem;
    }

    //-----------------------------------------------------< implementation >---
    /**
     * Add a property name that should be listed in the DAV:principal-search-property-set.
     *
     * @param propName a property name that may be searched in the {@link PrincipalSearchReport}.
     * @param description Human readable description of the searchable property.
     * @param language defines in which language the description is written.
     * @throws IllegalArgumentException if propName is <code>null</code>.
     */
    public void addPrincipalSearchProperty(DavPropertyName propName,
                                           String description, String language) {
        searchPropertySet.add(new PrincipalSearchProperty(propName, description, language));
    }

    //--------------------------------------------------------< inner class >---
    /**
     * Inner class encapsulating the DAV:principal-search-property
     */
    private class PrincipalSearchProperty implements XmlSerializable {

        private static final String XML_PRINCIPAL_SEARCH_PROPERTY = "principal-search-property";
        private static final String XML_DESCRIPTION = "description";
        private static final String ATTR_LANG = "lang";

        private final DavPropertyName propName;
        private final String description;
        private final String language;
        private final int hashCode;

        private PrincipalSearchProperty(DavPropertyName propName,
                                        String description,
                                        String language) {
            if (propName == null) {
                throw new IllegalArgumentException("null is not a valid DavPropertyName for the DAV:principal-search-property.");
            }
            this.propName = propName;
            this.description = description;
            this.language = language;
            hashCode = propName.hashCode();
        }

        /**
         * @see XmlSerializable#toXml(Document)
         */
        public Element toXml(Document document) {
            Element psElem = DomUtil.createElement(document, XML_PRINCIPAL_SEARCH_PROPERTY, SecurityConstants.NAMESPACE);
            // create property set from the single property name
            DavPropertyNameSet pnSet = new DavPropertyNameSet();
            pnSet.add(propName);
            psElem.appendChild(pnSet.toXml(document));
            // append description if present
            if (description != null) {
                Element desc = DomUtil.addChildElement(psElem, XML_DESCRIPTION, SecurityConstants.NAMESPACE, description);
                if (language != null) {
                    DomUtil.setAttribute(desc, ATTR_LANG, Namespace.XML_NAMESPACE, language);
                }
            }
            return psElem;
        }

        //---------------------------------------------------------< Object >---
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof PrincipalSearchProperty) {
                PrincipalSearchProperty other = (PrincipalSearchProperty)obj;
                // ignore the optional description/language
                return hashCode == other.hashCode;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
