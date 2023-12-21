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
import java.util.Iterator;
import java.util.List;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>ExpandPropertyReport</code> encapsulates the DAV:expand-property report,
 * that provides a mechanism for retrieving in one request the properties from
 * the resources identified by those DAV:href elements. It should be supported by
 * all resources that support the REPORT method.
 * <p>
 * RFC 3253 specifies the following required format for the request body:
 * <pre>
 * &lt;!ELEMENT expand-property (property*)&gt;
 * &lt;!ELEMENT property (property*)&gt;
 * &lt;!ATTLIST property name NMTOKEN #REQUIRED&gt;
 * name value: a property element type
 * &lt;!ATTLIST property namespace NMTOKEN "DAV:"&gt;
 * namespace value: an XML namespace
 * </pre>
 * NOTE: any DAV:property elements defined in the request body, that does not
 * represent {@link HrefProperty} is treated as in a common PROPFIND request.
 *
 * @see DeltaVConstants#XML_EXPAND_PROPERTY
 * @see DeltaVConstants#XML_PROPERTY
 */
public class ExpandPropertyReport extends AbstractReport implements DeltaVConstants {

    private static Logger log = LoggerFactory.getLogger(ExpandPropertyReport.class);

    private DavResource resource;
    private ReportInfo info;
    private Iterator<Element> propertyElements;

    /**
     * Returns {@link ReportType#EXPAND_PROPERTY}.
     *
     * @return
     * @see Report#getType()
     */
    public ReportType getType() {
        return ReportType.EXPAND_PROPERTY;
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
     * @see Report#init(DavResource, ReportInfo)
     */
    public void init(DavResource resource, ReportInfo info) throws DavException {
        setResource(resource);
        setInfo(info);
    }

    /**
     * Set the target resource.
     *
     * @param resource
     * @throws DavException if the specified resource is <code>null</code>
     */
    private void setResource(DavResource resource) throws DavException {
        if (resource == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "The resource specified must not be null.");
        }
        this.resource = resource;
    }

    /**
     * Set the <code>ReportInfo</code>.
     *
     * @param info
     * @throws DavException if the given <code>ReportInfo</code>
     * does not contain a DAV:expand-property element.
     */
    private void setInfo(ReportInfo info) throws DavException {
        if (info == null) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "The report info specified must not be null.");
        }
        if (!getType().isRequestedReportType(info)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:expand-property element expected.");
        }
        this.info = info;
        propertyElements = info.getContentElements(XML_PROPERTY, NAMESPACE).iterator();
    }

    /**
     * Run the report
     *
     * @return Xml <code>Document</code> as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     * @param document
     */
    public Element toXml(Document document) {
        return getMultiStatus().toXml(document);
    }

    /**
     * Retrieve the multistatus object that is returned in response to the
     * expand-property report request.
     *
     * @return
     * @throws NullPointerException if info and resource have not been set.
     */
    private MultiStatus getMultiStatus() {
        MultiStatus ms = new MultiStatus();
        addResponses(resource, info.getDepth(), ms);
        return ms;
    }

    /**
     * Fills the specified <code>MultiStatus</code> object by generating a
     * <code>MultiStatusResponse</code> for the given resource (and
     * its member according to the depth value).
     *
     * @param res
     * @param depth
     * @param ms
     * @see #getResponse(DavResource, Iterator)
     */
    private void addResponses(DavResource res, int depth, MultiStatus ms) {
        MultiStatusResponse response = getResponse(res, propertyElements);
        ms.addResponse(response);
        if (depth > 0 && res.isCollection()) {
            DavResourceIterator it = res.getMembers();
            while (it.hasNext()) {
                addResponses(it.nextResource(), depth-1, ms);
            }
        }
    }

    /**
     * Builds a <code>MultiStatusResponse</code> for the given resource respecting
     * the properties specified. Any property that represents a {@link HrefProperty}
     * is expanded: It's name equals the name of a valid {@link HrefProperty}.
     * However the value of that given property (consisting of one or multiple DAV:href elements)
     * is replaced by the Xml representation of a separate
     * {@link MultiStatusResponse multistatus responses} for the
     * resource referenced by the given DAV:href elements. The responses may
     * themselves have properties, which are defined by the separate list.
     *
     * @param res
     * @param propertyElements
     * @return <code>MultiStatusResponse</code> for the given resource.
     * @see ExpandProperty
     */
    private MultiStatusResponse getResponse(DavResource res, Iterator<Element> propertyElements) {
        MultiStatusResponse resp = new MultiStatusResponse(res.getHref(), null);
        while (propertyElements.hasNext()) {
            Element propertyElem = propertyElements.next();
            // retrieve the localName present in the "name" attribute
            String nameAttr = propertyElem.getAttribute(ATTR_NAME);
            if (nameAttr == null || "".equals(nameAttr)) {
                // NOTE: this is not valid according to the DTD
                continue;
            }
            // retrieve the namespace present in the "namespace" attribute
            // NOTE: if this attribute is missing the DAV: namespace represents the default.
            String namespaceAttr = propertyElem.getAttribute(ATTR_NAMESPACE);
            Namespace namespace = (namespaceAttr != null) ? Namespace.getNamespace(namespaceAttr) : NAMESPACE;

            DavPropertyName propName = DavPropertyName.create(nameAttr, namespace);
            DavProperty<?> p = res.getProperty(propName);
            if (p != null) {
                if (p instanceof HrefProperty && res instanceof DeltaVResource) {
                    ElementIterator it = DomUtil.getChildren(propertyElem, XML_PROPERTY, NAMESPACE);
                    resp.add(new ExpandProperty((DeltaVResource)res, (HrefProperty)p, it));
                } else {
                    resp.add(p);
                }
            } else {
                resp.add(propName, DavServletResponse.SC_NOT_FOUND);
            }
        }
        return resp;
    }

    //--------------------------------------------------------< inner class >---
    /**
     * <code>ExpandProperty</code> extends <code>DavProperty</code>. It's name
     * equals the name of a valid {@link HrefProperty}. However the value of
     * that given property (consisting of one or multiple DAV:href elements)
     * is replaced by the Xml representation of a separate
     * {@link MultiStatusResponse multistatus responses} for the
     * resource referenced to by the given DAV:href elements. The responses may
     * themselves have properties, which are defined by the separate list.
     */
    private class ExpandProperty extends AbstractDavProperty<List<MultiStatusResponse>> {

        private List<MultiStatusResponse> valueList = new ArrayList<MultiStatusResponse>();

        /**
         * Create a new <code>ExpandProperty</code>.
         *
         * @param hrefProperty
         * @param elementIter
         */
        private ExpandProperty(DeltaVResource deltaVResource, HrefProperty hrefProperty, ElementIterator elementIter) {
            super(hrefProperty.getName(), hrefProperty.isInvisibleInAllprop());
            try {
                DavResource[] refResource = deltaVResource.getReferenceResources(hrefProperty.getName());
                for (DavResource res : refResource) {
                    MultiStatusResponse resp = getResponse(res, elementIter);
                    valueList.add(resp);
                }
            } catch (DavException e) {
                // invalid references or unknown property
                log.error(e.getMessage());
            }
        }

        /**
         * Returns a List of {@link MultiStatusResponse} objects.
         *
         * @return
         */
        public List<MultiStatusResponse> getValue() {
            return valueList;
        }
    }
}
