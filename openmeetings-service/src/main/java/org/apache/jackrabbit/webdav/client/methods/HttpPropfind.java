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
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.PropfindInfo;

/**
 * Represents an HTTP PROPFIND request.
 * 
 * @see <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.1">RFC 4918, Section 9.1</a>
 * @since 2.13.6
 */
public class HttpPropfind extends BaseDavRequest {

    public HttpPropfind(URI uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
        super(uri);

        DepthHeader dh = new DepthHeader(depth);
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());

        PropfindInfo info = new PropfindInfo(propfindType, names);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpPropfind(URI uri, DavPropertyNameSet names, int depth) throws IOException {
        this(uri, DavConstants.PROPFIND_BY_PROPERTY, names, depth);
    }

    public HttpPropfind(URI uri, int propfindType, int depth) throws IOException {
        this(uri, propfindType, new DavPropertyNameSet(), depth);
    }

    public HttpPropfind(String uri, int propfindType, int depth) throws IOException {
        this(URI.create(uri), propfindType, depth);
    }

    public HttpPropfind(String uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
        this(URI.create(uri), propfindType, names, depth);
    }

    public HttpPropfind(String uri, DavPropertyNameSet names, int depth) throws IOException {
        this(URI.create(uri), names, depth);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_PROPFIND;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == DavServletResponse.SC_MULTI_STATUS;
    }
}