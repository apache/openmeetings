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
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ProppatchInfo;

/**
 * Represents an HTTP PROPPATCH request.
 * 
 * @see <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.2">RFC 4918, Section 9.2</a>
 * @since 2.13.6
 */
public class HttpProppatch extends BaseDavRequest {

    // private DavPropertyNameSet propertyNames;

    public HttpProppatch(URI uri, ProppatchInfo info) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(info));
        // this.propertyNames = info.getAffectedProperties();
    }

    public HttpProppatch(URI uri, List<? extends PropEntry> changeList) throws IOException {
        this(uri, new ProppatchInfo(changeList));
    }

    public HttpProppatch(URI uri, DavPropertySet setProperties, DavPropertyNameSet removeProperties) throws IOException {
        this(uri, new ProppatchInfo(setProperties, removeProperties));
    }

    public HttpProppatch(String uri, List<? extends PropEntry> changeList) throws IOException {
        this(URI.create(uri), new ProppatchInfo(changeList));
    }

    public HttpProppatch(String uri, DavPropertySet setProperties, DavPropertyNameSet removeProperties) throws IOException {
        this(URI.create(uri), new ProppatchInfo(setProperties, removeProperties));
    }

    @Override
    public String getMethod() {
        return DavMethods.METHOD_PROPPATCH;
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == DavServletResponse.SC_MULTI_STATUS;

        // disabled code that fails for current PROPPATCH behavior of Jackrabbit
//        MultiStatusResponse responses[] = super.getResponseBodyAsMultiStatus(response).getResponses();
//        if (responses.length != 1) {
//            throw new DavException(DavServletResponse.SC_MULTI_STATUS,
//                    "PROPPATCH failed: Expected exactly one multistatus response element, but got " + responses.length);
//        }
//        DavPropertyNameSet okSet = responses[0].getPropertyNames(DavServletResponse.SC_OK);
//        if (okSet.isEmpty()) {
//            throw new DavException(DavServletResponse.SC_MULTI_STATUS,
//                    "PROPPATCH failed: No 'OK' response found for resource " + responses[0].getHref());
//        } else {
//            DavPropertyNameIterator it = propertyNames.iterator();
//            while (it.hasNext()) {
//                DavPropertyName pn = it.nextPropertyName();
//                boolean success = okSet.remove(pn);
//                if (!success) {
//                    throw new DavException(DavServletResponse.SC_MULTI_STATUS,
//                            "PROPPATCH failed: Property name not present in multistatus response: " + pn);
//                }
//            }
//        }
//        if (!okSet.isEmpty()) {
//            StringBuilder b = new StringBuilder();
//            DavPropertyNameIterator it = okSet.iterator();
//            while (it.hasNext()) {
//                b.append(it.nextPropertyName().toString()).append("; ");
//            }
//            throw new DavException(DavServletResponse.SC_MULTI_STATUS,
//                    "PROPPATCH failed: The following properties outside of the original request where set or removed: " + b.toString());
//        }
//        return true;
    }
}