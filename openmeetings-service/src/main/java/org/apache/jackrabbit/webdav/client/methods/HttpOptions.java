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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.header.FieldValueParser;
import org.apache.jackrabbit.webdav.search.SearchConstants;

/**
 * Represents an HTTP OPTIONS request.
 * 
 * @see <a href="http://webdav.org/specs/rfc7231.html#rfc.section.4.3.7">RFC 7231, Section 4.3.7</a>
 * @since 2.13.6
 */
public class HttpOptions extends org.apache.http.client.methods.HttpOptions {

    public HttpOptions(URI uri) {
        super(uri);
    }

    public HttpOptions(String uri) {
        super(URI.create(uri));
    }

    /**
     * Compute the set of compliance classes returned in the "dav" header field
     */
    public Set<String> getDavComplianceClasses(HttpResponse response) {
        Header[] headers = response.getHeaders(DavConstants.HEADER_DAV);
        return parseTokenOrCodedUrlheaderField(headers, false);
    }

    /**
     * Compute set of search grammars returned in the "dasl" header field
     */
    public Set<String> getSearchGrammars(HttpResponse response) {
        Header[] headers = response.getHeaders(SearchConstants.HEADER_DASL);
        return parseTokenOrCodedUrlheaderField(headers, true);
    }

    private Set<String> parseTokenOrCodedUrlheaderField(Header[] headers, boolean removeBrackets) {
        if (headers == null) {
            return Collections.emptySet();
        }
        else {
            Set<String> result = new HashSet<String>();
            for (Header h : headers) {
                for (String s : FieldValueParser.tokenizeList(h.getValue())) {
                    if (removeBrackets && s.startsWith("<") && s.endsWith(">")) {
                        s = s.substring(1, s.length() - 1);
                    }
                    result.add(s.trim());
                }
            }
            return Collections.unmodifiableSet(result);
        }
    }
}
