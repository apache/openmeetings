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
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;

/**
 * Represents an HTTP SUBSCRIBE request.
 * <p>
 * Note that "SUBSCRIBE" is a custom HTTP extension, not defined in a standards paper.
 * @since 2.13.6
 */
public class HttpSubscribe extends BaseDavRequest {

    public HttpSubscribe(URI uri, SubscriptionInfo info, String subscriptionId) throws IOException {
        super(uri);
        if (info == null) {
            throw new IllegalArgumentException("SubscriptionInfo must not be null.");
        }
        // optional subscriptionId (only required to modify an existing
        // subscription)
        if (subscriptionId != null) {
            CodedUrlHeader h = new CodedUrlHeader(ObservationConstants.HEADER_SUBSCRIPTIONID, subscriptionId);
            super.setHeader(h.getHeaderName(), h.getHeaderValue());
        }
        // optional timeout header
        long to = info.getTimeOut();
        if (to != DavConstants.UNDEFINED_TIMEOUT) {
            TimeoutHeader h = new TimeoutHeader(info.getTimeOut());
            super.setHeader(h.getHeaderName(), h.getHeaderValue());
        }
        // always set depth header since value is boolean flag
        DepthHeader dh = new DepthHeader(info.isDeep());
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        super.setEntity(XmlEntity.create(info));
    }

    public HttpSubscribe(String uri, SubscriptionInfo info, String subscriptionId) throws IOException {
        this(URI.create(uri), info, subscriptionId);
    }

    public String getSubscriptionId(HttpResponse response) {
        org.apache.http.Header sbHeader = response.getFirstHeader(ObservationConstants.HEADER_SUBSCRIPTIONID);
        if (sbHeader != null) {
            CodedUrlHeader cuh = new CodedUrlHeader(ObservationConstants.HEADER_SUBSCRIPTIONID, sbHeader.getValue());
            return cuh.getCodedUrl();
        }
        else {
            return null;
        }
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_SUBSCRIBE;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == DavServletResponse.SC_OK;
    }
}