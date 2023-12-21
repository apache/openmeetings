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
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;

/**
 * Represents an HTTP ORDERPATCH request.
 * 
 * @see <a href="http://webdav.org/specs/rfc3648.html#rfc.section.5">RFC 3648, Section 5</a>
 * @since 2.13.6
 */
public class HttpOrderpatch extends BaseDavRequest {

    public HttpOrderpatch(URI uri, OrderPatch info) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpOrderpatch(String uri, OrderPatch info) throws IOException {
        this(URI.create(uri), info);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_ORDERPATCH;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == DavServletResponse.SC_OK;
    }
}