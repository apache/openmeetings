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
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;

/**
 * Represents an HTTP COPY request.
 * 
 * @see <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.8">RFC 4918, Section 9.8</a>
 * @since 2.13.6
 */
public class HttpCopy extends BaseDavRequest {

    public HttpCopy(URI uri, URI dest, boolean overwrite, boolean shallow) {
        super(uri);
        super.setHeader(DavConstants.HEADER_DESTINATION, dest.toASCIIString());
        if (!overwrite) {
            super.setHeader(DavConstants.HEADER_OVERWRITE, "F");
        }
        if (shallow) {
            super.setHeader("Depth", "0");
        }
    }

    public HttpCopy(String uri, String dest, boolean overwrite, boolean shallow) {
        this(URI.create(uri), URI.create(dest), overwrite, shallow);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_COPY;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == DavServletResponse.SC_CREATED || statusCode == DavServletResponse.SC_NO_CONTENT;
    }
}
