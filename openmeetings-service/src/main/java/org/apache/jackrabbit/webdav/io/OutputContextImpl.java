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
package org.apache.jackrabbit.webdav.io;

import org.apache.jackrabbit.webdav.DavConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * <code>OutputContextImpl</code>...
 */
public class OutputContextImpl implements OutputContext {

    private static Logger log = LoggerFactory.getLogger(OutputContextImpl.class);

    private final HttpServletResponse response;
    private final OutputStream out;

    public OutputContextImpl(HttpServletResponse response, OutputStream out) {
        if (response == null) {
            throw new IllegalArgumentException("Response must not be null.");
        }

        this.response = response;
        this.out = out;
    }

    public boolean hasStream() {
        return out != null;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public void setContentLanguage(String contentLanguage) {
        if (contentLanguage != null) {
            response.setHeader(DavConstants.HEADER_CONTENT_LANGUAGE, contentLanguage);
        }
    }

    public void setContentLength(long contentLength) {
        if (contentLength >= 0) {
            response.setContentLengthLong(contentLength);
        } // else: negative content length -> ignore.
    }

    public void setContentType(String contentType) {
        if (contentType != null) {
            response.setContentType(contentType);
        }
    }

    public void setModificationTime(long modificationTime) {
        if (modificationTime >= 0) {
            response.addDateHeader(DavConstants.HEADER_LAST_MODIFIED, modificationTime);
        }
    }

    public void setETag(String etag) {
        if (etag != null) {
            response.setHeader(DavConstants.HEADER_ETAG, etag);
        }
    }

    public void setProperty(String propertyName, String propertyValue) {
        if (propertyName != null && propertyValue != null) {
            response.setHeader(propertyName, propertyValue);
        }
    }
}