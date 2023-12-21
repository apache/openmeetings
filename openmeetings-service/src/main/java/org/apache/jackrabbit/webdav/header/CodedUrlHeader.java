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
package org.apache.jackrabbit.webdav.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <code>CodedUrlHeader</code>...
 */
public class CodedUrlHeader implements Header {

    private static Logger log = LoggerFactory.getLogger(CodedUrlHeader.class);

    private final String headerName;
    private final String headerValue;

    public CodedUrlHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        if (headerValue != null && !(headerValue.startsWith("<") && headerValue.endsWith(">"))) {
            headerValue = "<" + headerValue + ">";
        }
        this.headerValue = headerValue;
    }

    /**
     * Return the name of the header
     *
     * @return header name
     * @see Header#getHeaderName()
     */
    public String getHeaderName() {
	return headerName;
    }

    /**
     * Return the value of the header
     *
     * @return value
     * @see Header#getHeaderValue()
     */
    public String getHeaderValue() {
        return headerValue;
    }

    /**
     * Returns the token present in the header value or <code>null</code>.
     * If the header contained multiple tokens separated by ',' the first value
     * is returned.
     *
     * @return token present in the CodedURL header or <code>null</code> if
     * the header is not present.
     * @see #getCodedUrls()
     */
    public String getCodedUrl() {
        String[] codedUrls = getCodedUrls();
        return (codedUrls != null) ? codedUrls[0] : null;
    }

    /**
     * Return an array of coded urls as present in the header value or <code>null</code> if
     * no value is present.
     *
     * @return array of coded urls
     */
    public String[] getCodedUrls() {
        String[] codedUrls = null;
        if (headerValue != null) {
            String[] values = headerValue.split(",");
            codedUrls = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                int p1 = values[i].indexOf('<');
                if (p1<0) {
                    throw new IllegalArgumentException("Invalid CodedURL header value:" + values[i]);
                }
                int p2 = values[i].indexOf('>', p1);
                if (p2<0) {
                    throw new IllegalArgumentException("Invalid CodedURL header value:" + values[i]);
                }
                codedUrls[i] = values[i].substring(p1+1, p2);
            }
	}
        return codedUrls;
    }

    /**
     * Retrieves the header with the given name and builds a new <code>CodedUrlHeader</code>.
     *
     * @param request
     * @param headerName
     * @return new <code>CodedUrlHeader</code> instance
     */
    public static CodedUrlHeader parse(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        return new CodedUrlHeader(headerName, headerValue);
    }
}