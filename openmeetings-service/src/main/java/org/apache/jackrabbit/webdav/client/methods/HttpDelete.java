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

import org.apache.jackrabbit.webdav.DavMethods;

/**
 * Represents an HTTP DELETE request.
 * 
 * @see <a href="http://webdav.org/specs/rfc7231.html#rfc.section.4.3.5">RFC 7231, Section 4.3.5</a>
 * @since 2.13.6
 */
public class HttpDelete extends BaseDavRequest {

    public HttpDelete(URI uri){
        super(uri);
    }

    public HttpDelete(String uri) {
        this(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_DELETE;
    }
}