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
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MultiStatus representing the content of a multistatus response body and
 * allows to retrieve the Xml representation.
 */
public class MultiStatus implements DavConstants, XmlSerializable {

    /**
     * Map collecting the responses for this multistatus, where every href must
     * only occur one single time.
     */
    private Map<String, MultiStatusResponse> responses = new LinkedHashMap<String, MultiStatusResponse>();

    /**
     * A general response description at the multistatus top level is used to
     * provide a general message describing the overarching nature of the response.
     * If this value is available an application may use it instead of
     * presenting the individual response descriptions contained within the
     * responses.
     */
    private String responseDescription;

    /**
     * Add response(s) to this multistatus, in order to build a multistatus for
     * responding to a PROPFIND request.
     *
     * @param resource The resource to add property from
     * @param propNameSet The requested property names of the PROPFIND request
     * @param propFindType
     * @param depth
     */
    public void addResourceProperties(DavResource resource, DavPropertyNameSet propNameSet,
                                      int propFindType, int depth) {
        addResponse(new MultiStatusResponse(resource, propNameSet, propFindType));
        if (depth > 0 && resource.isCollection()) {
            DavResourceIterator iter = resource.getMembers();
            while (iter.hasNext()) {
                addResourceProperties(iter.nextResource(), propNameSet, propFindType, depth-1);
            }
        }
    }

    /**
     * Add response(s) to this multistatus, in order to build a multistatus e.g.
     * in order to respond to a PROPFIND request. Please note, that in terms
     * of PROPFIND, this method would correspond to a
     * {@link DavConstants#PROPFIND_BY_PROPERTY} propfind type.
     *
     * @param resource The resource to add property from
     * @param propNameSet The requested property names of the PROPFIND request
     * @param depth
     * @see #addResourceProperties(DavResource, DavPropertyNameSet, int, int) for
     * the corresponding method that allows to specify the type.
     */
    public void addResourceProperties(DavResource resource, DavPropertyNameSet propNameSet,
                                      int depth) {
        addResourceProperties(resource, propNameSet, PROPFIND_BY_PROPERTY, depth);
    }

    /**
     * Add response(s) to this multistatus, in order to build a multistatus
     * as returned for COPY, MOVE, LOCK or DELETE requests resulting in an error
     * with a resource other than the resource identified in the Request-URI.
     *
     * @param resource
     * @param status
     * @param depth
     */
    public void addResourceStatus(DavResource resource, int status, int depth) {
        addResponse(new MultiStatusResponse(resource.getHref(), status));
        if (depth > 0 && resource.isCollection()) {
            DavResourceIterator iter = resource.getMembers();
            while (iter.hasNext()) {
                addResourceStatus(iter.nextResource(), status, depth-1);
            }
        }
    }

    /**
     * Add a <code>MultiStatusResponse</code> element to this <code>MultiStatus</code>
     * <p>
     * This method is synchronized to avoid the problem described in
     * <a href="https://issues.apache.org/jira/browse/JCR-2755">JCR-2755</a>.
     *
     * @param response
     */
    public synchronized void addResponse(MultiStatusResponse response) {
        responses.put(response.getHref(), response);
    }

    /**
     * Returns the multistatus responses present as array.
     * <p>
     * This method is synchronized to avoid the problem described in
     * <a href="https://issues.apache.org/jira/browse/JCR-2755">JCR-2755</a>.
     *
     * @return array of all {@link MultiStatusResponse responses} present in this
     * multistatus.
     */
    public synchronized MultiStatusResponse[] getResponses() {
        return responses.values().toArray(new MultiStatusResponse[responses.size()]);
    }

    /**
     * Set the response description.
     *
     * @param responseDescription
     */
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * Returns the response description.
     *
     * @return responseDescription
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * Return the Xml representation of this <code>MultiStatus</code>.
     *
     * @return Xml document
     * @param document
     */
    public Element toXml(Document document) {
        Element multistatus = DomUtil.createElement(document, XML_MULTISTATUS, NAMESPACE);
        for (MultiStatusResponse resp : getResponses()) {
            multistatus.appendChild(resp.toXml(document));
        }
        if (responseDescription != null) {
            Element respDesc = DomUtil.createElement(document, XML_RESPONSEDESCRIPTION, NAMESPACE, responseDescription);
            multistatus.appendChild(respDesc);
        }
        return multistatus;
    }

    /**
     * Build a <code>MultiStatus</code> from the specified xml element.
     *
     * @param multistatusElement
     * @return new <code>MultiStatus</code> instance.
     * @throws IllegalArgumentException if the given document is <code>null</code>
     * or does not provide the required element.
     */
    public static MultiStatus createFromXml(Element multistatusElement) {
        if (!DomUtil.matches(multistatusElement, XML_MULTISTATUS, NAMESPACE)) {
            throw new IllegalArgumentException("DAV:multistatus element expected.");
        }

        MultiStatus multistatus = new MultiStatus();

        ElementIterator it = DomUtil.getChildren(multistatusElement, XML_RESPONSE, NAMESPACE);
        while (it.hasNext()) {
            Element respElem = it.nextElement();
            MultiStatusResponse response = MultiStatusResponse.createFromXml(respElem);
            multistatus.addResponse(response);
        }

        // optional response description on the multistatus element
        multistatus.setResponseDescription(DomUtil.getChildText(multistatusElement, XML_RESPONSEDESCRIPTION, NAMESPACE));
        return multistatus;
    }
}
