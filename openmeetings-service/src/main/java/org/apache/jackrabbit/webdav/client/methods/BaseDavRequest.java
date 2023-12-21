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
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Base class for HTTP request classes defined in this package.
 */
public abstract class BaseDavRequest extends HttpEntityEnclosingRequestBase {

    private static Logger log = LoggerFactory.getLogger(BaseDavRequest.class);

    public BaseDavRequest(URI uri) {
        super();
        super.setURI(uri);
    }

    /**
     * Gets a {@link Document} representing the response body.
     * @return document or {@code null} for null entity
     * @throws IOException in case of I/O or XMP pasting problems 
     */
    public Document getResponseBodyAsDocument(HttpEntity entity) throws IOException {

        if (entity == null) {
            return null;
        } else {
            // read response and try to build a xml document
            InputStream in = entity.getContent();
            try {
                return DomUtil.parseDocument(in);
            } catch (ParserConfigurationException ex) {
                throw new IOException("XML parser configuration error", ex);
            } catch (SAXException ex) {
                throw new IOException("XML parsing error", ex);
            } finally {
                in.close();
            }
        }
    }

    /**
     * Return response body as {@link MultiStatus} object.
     * @throws IllegalStateException when response does not represent a {@link MultiStatus}
     * @throws DavException for failures in obtaining/parsing the response body
     */
    public MultiStatus getResponseBodyAsMultiStatus(HttpResponse response) throws DavException {
        try {
            Document doc = getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            return MultiStatus.createFromXml(doc.getDocumentElement());
        } catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), ex);
        }
    }

    /**
     * Return response body as {@link LockDiscovery} object.
     * @throws IllegalStateException when response does not represent a {@link LockDiscovery}
     * @throws DavException for failures in obtaining/parsing the response body
     */
    public LockDiscovery getResponseBodyAsLockDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            Element root = doc.getDocumentElement();

            if (!DomUtil.matches(root, DavConstants.XML_PROP, DavConstants.NAMESPACE)
                    && DomUtil.hasChildElement(root, DavConstants.PROPERTY_LOCKDISCOVERY, DavConstants.NAMESPACE)) {
                throw new DavException(response.getStatusLine().getStatusCode(),
                        "Missing DAV:prop response body in LOCK response.");
            }

            Element lde = DomUtil.getChildElement(root, DavConstants.PROPERTY_LOCKDISCOVERY, DavConstants.NAMESPACE);
            if (!DomUtil.hasChildElement(lde, DavConstants.XML_ACTIVELOCK, DavConstants.NAMESPACE)) {
                throw new DavException(response.getStatusLine().getStatusCode(),
                        "The DAV:lockdiscovery must contain a least a single DAV:activelock in response to a successful LOCK request.");
            }

            return LockDiscovery.createFromXml(lde);
        } catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), ex);
        }
    }

    /**
     * Return response body as {@link SubscriptionDiscovery} object.
     * @throws IllegalStateException when response does not represent a {@link SubscriptionDiscovery}
     * @throws DavException for failures in obtaining/parsing the response body
     */
    public SubscriptionDiscovery getResponseBodyAsSubscriptionDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            Element root = doc.getDocumentElement();

            if (!DomUtil.matches(root, DavConstants.XML_PROP, DavConstants.NAMESPACE)
                    && DomUtil.hasChildElement(root, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(),
                            ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace())) {
                throw new DavException(response.getStatusLine().getStatusCode(),
                        "Missing DAV:prop response body in SUBSCRIBE response.");
            }

            Element sde = DomUtil.getChildElement(root, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(),
                    ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace());
            SubscriptionDiscovery sd = SubscriptionDiscovery.createFromXml(sde);
            if (((Subscription[]) sd.getValue()).length > 0) {
                return sd;
            } else {
                throw new DavException(response.getStatusLine().getStatusCode(),
                        "Missing 'subscription' elements in SUBSCRIBE response body. At least a single subscription must be present if SUBSCRIBE was successful.");
            }
        } catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), ex);
        }
    }

    /**
     * Return response body as {@link EventDiscovery} object.
     * @throws IllegalStateException when response does not represent a {@link EventDiscovery}
     * @throws DavException for failures in obtaining/parsing the response body
     */
    public EventDiscovery getResponseBodyAsEventDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            return EventDiscovery.createFromXml(doc.getDocumentElement());
        } catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), ex);
        }
    }

    /**
     * Check the response and throw when it is considered to represent a failure.
     */
    public void checkSuccess(HttpResponse response) throws DavException {
        if (!succeeded(response)) {
            throw getResponseException(response);
        }
    }

    /**
     * Obtain a {@link DavException} representing the response.
     * @throws IllegalStateException when the response is considered to be successful
     */
    public DavException getResponseException(HttpResponse response) {
        if (succeeded(response)) {
            String msg = "Cannot retrieve exception from successful response.";
            log.warn(msg);
            throw new IllegalStateException(msg);
        }

        StatusLine st = response.getStatusLine();
        Element responseRoot = null;
        try {
            responseRoot = getResponseBodyAsDocument(response.getEntity()).getDocumentElement();
        } catch (IOException e) {
            // non-parseable body -> use null element
        }

        return new DavException(st.getStatusCode(), st.getReasonPhrase(), null, responseRoot);
    }

    /**
     * Check the provided {@link HttpResponse} for successful execution. The default implementation treats all
     * 2xx status codes (<a href="http://webdav.org/specs/rfc7231.html#rfc.section.6.3">RFC 7231, Section 6.3</a>).
     * Implementations can further restrict the accepted range of responses (or even check the response body).
     */
    public boolean succeeded(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status <= 299;
    }
}
