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
import org.apache.jackrabbit.webdav.version.MergeInfo;

/**
 * Represents an HTTP MERGE request.
 * 
 * @see <a href="http://webdav.org/specs/rfc3253.html#rfc.section.11.2">RFC 3253, Section 11.2</a>
 * @since 2.13.6
 */
public class HttpMerge extends BaseDavRequest {

    public HttpMerge(URI uri, MergeInfo mergeInfo) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(mergeInfo));
    }

    public HttpMerge(String uri, MergeInfo mergeInfo) throws IOException {
        this(URI.create(uri), mergeInfo);
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_MERGE;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        // TODO: is this correct?
        return statusCode == DavServletResponse.SC_MULTI_STATUS;
    }
}
