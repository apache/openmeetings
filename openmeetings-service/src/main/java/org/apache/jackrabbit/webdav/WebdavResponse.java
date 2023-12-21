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
package org.apache.jackrabbit.webdav;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.jackrabbit.webdav.observation.ObservationDavServletResponse;

/**
 * The empty <code>WebdavResponse</code> interface collects the functionality
 * defined by {@link org.apache.jackrabbit.webdav.DavServletResponse}
 * encapsulating for the core WebDAV specification (RFC 2518) as well as the
 * various extensions used for observation and transaction support, ordering of
 * collections, search and versioning.
 */
public interface WebdavResponse extends DavServletResponse, ObservationDavServletResponse {

    // can be removed when we move to Servlet API 4.0
    public default void setTrailerFields(Supplier<Map<String, String>> supplier) {
        // nop
    }

    // can be removed when we move to Servlet API 4.0
    public default Supplier<Map<String, String>> getTrailerFields() {
        return null;
    }
}