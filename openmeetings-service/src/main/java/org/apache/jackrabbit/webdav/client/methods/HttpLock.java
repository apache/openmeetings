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
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.header.IfHeader;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an HTTP LOCK request.
 * 
 * @see <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.10">RFC 4918, Section 9.10</a>
 * @since 2.13.6
 */
public class HttpLock extends BaseDavRequest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpLock.class);

    private final boolean isRefresh;

    public HttpLock(URI uri, LockInfo lockInfo) throws IOException {
        super(uri);

        TimeoutHeader th = new TimeoutHeader(lockInfo.getTimeout());
        super.setHeader(th.getHeaderName(), th.getHeaderValue());
        DepthHeader dh = new DepthHeader(lockInfo.isDeep());
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());

        super.setEntity(XmlEntity.create(lockInfo));

        isRefresh = false;
    }

    public HttpLock(String uri, LockInfo lockInfo) throws IOException {
        this(URI.create(uri), lockInfo);
    }

    public HttpLock(URI uri, long timeout, String[] lockTokens) {
        super(uri);

        TimeoutHeader th = new TimeoutHeader(timeout);
        super.setHeader(th.getHeaderName(), th.getHeaderValue());
        IfHeader ifh = new IfHeader(lockTokens);
        super.setHeader(ifh.getHeaderName(), ifh.getHeaderValue());
        isRefresh = true;
    }

    public HttpLock(String uri, long timeout, String[] lockTokens) {
        this(URI.create(uri), timeout, lockTokens);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_LOCK;
    }

    public String getLockToken(HttpResponse response) {
        Header[] ltHeader = response.getHeaders(DavConstants.HEADER_LOCK_TOKEN);
        if (ltHeader == null || ltHeader.length == 0) {
            return null;
        } else if (ltHeader.length != 1) {
            LOG.debug("Multiple 'Lock-Token' header fields in response for " + getURI() + ": " + Arrays.asList(ltHeader));
            return null;
        } else {
            String v = ltHeader[0].getValue().trim();
            if (!v.startsWith("<") || !v.endsWith(">")) {
                LOG.debug("Invalid 'Lock-Token' header field in response for " + getURI() + ": " + Arrays.asList(ltHeader));
                return null;
            } else {
                return v.substring(1, v.length() - 1);
            }
        }
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        boolean lockTokenHeaderOk = isRefresh || null != getLockToken(response);
        return lockTokenHeaderOk && (statusCode == DavServletResponse.SC_OK || statusCode == DavServletResponse.SC_CREATED);
    }
}