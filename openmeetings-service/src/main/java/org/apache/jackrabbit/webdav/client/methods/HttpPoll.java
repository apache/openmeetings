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

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.header.PollTimeoutHeader;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;

/**
 * Represents an HTTP POLL request.
 * <p>
 * Note that "POLL" is a custom HTTP extension, not defined in a standards paper.
 * @since 2.13.6
 */
public class HttpPoll extends BaseDavRequest {

    public HttpPoll(URI uri, String subscriptionId, long timeout) {
        super(uri);
        super.setHeader(ObservationConstants.HEADER_SUBSCRIPTIONID, subscriptionId);
        if (timeout > 0) {
            PollTimeoutHeader th = new PollTimeoutHeader(timeout);
            super.setHeader(th.getHeaderName(), th.getHeaderValue());
        }
    }

    public HttpPoll(String uri, String subscriptionId, long timeout) {
        this(URI.create(uri), subscriptionId, timeout);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_POLL;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == DavServletResponse.SC_OK;
    }
}