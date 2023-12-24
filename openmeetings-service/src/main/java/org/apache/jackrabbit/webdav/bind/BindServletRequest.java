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
package org.apache.jackrabbit.webdav.bind;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;

/**
 * <code>BindServletRequest</code> provides extension useful for functionality
 * related to BIND specification.
 */
public interface BindServletRequest {

    /**
     * Returns the {@link RebindInfo} present with the request
     *
     * @return {@link RebindInfo} object
     * @throws org.apache.jackrabbit.webdav.DavException in case of an invalid or missing request body
     */
    public RebindInfo getRebindInfo() throws DavException;

    /**
     * Returns the {@link UnbindInfo} present with the request
     *
     * @return {@link UnbindInfo} object
     * @throws org.apache.jackrabbit.webdav.DavException in case of an invalid or missing request body
     */
    public UnbindInfo getUnbindInfo() throws DavException;

    /**
     * Returns the {@link BindInfo} present with the request
     *
     * @return {@link BindInfo} object
     * @throws org.apache.jackrabbit.webdav.DavException in case of an invalid or missing request body
     */
    public BindInfo getBindInfo() throws DavException;

    /**
     * Parses a href and returns the path of the resource.
     *
     * @return path of the resource identified by the href.
     */
    public DavResourceLocator getHrefLocator(String href) throws DavException;

    /**
     * Returns the path of the member resource of the request resource which is identified by the segment parameter.
     *
     * @return path of internal member resource.
     */
    public DavResourceLocator getMemberLocator(String segment);
}
