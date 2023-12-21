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
package org.apache.jackrabbit.webdav.search;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;

/**
 * <code>SearchResource</code> defines METHODS required in order to handle
 * a SEARCH request.
 */
public interface SearchResource {

    /**
     * The 'SEARCH' method
     */
    public String METHODS = "SEARCH";


    /**
     * Returns the protected DAV:supported-method-set property which is defined
     * mandatory by RTF 3253. This method call is a shortcut for
     * <code>DavResource.getProperty(SearchConstants.QUERY_GRAMMER_SET)</code>.
     *
     * @return the DAV:supported-query-grammer-set
     * @see SearchConstants#QUERY_GRAMMER_SET
     */
    public QueryGrammerSet getQueryGrammerSet();

    /**
     * Runs a search with the language and query defined in the {@link SearchInfo}
     * object specified and returns a {@link MultiStatus} object listing the
     * results.
     *
     * @param sInfo <code>SearchInfo</code> element encapsulating the SEARCH
     * request body.
     * @return <code>MultiStatus</code> object listing the results.
     * @throws DavException
     */
    public MultiStatus search(SearchInfo sInfo) throws DavException;
}