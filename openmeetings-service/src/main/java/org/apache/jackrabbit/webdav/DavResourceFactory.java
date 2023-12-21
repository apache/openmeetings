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

/**
 * <code>DavResourceFactory</code> interface defines a single method for creating
 * {@link DavResource} objects.
 */
public interface DavResourceFactory {

    /**
     * Create a {@link DavResource} object from the given locator, request and response
     * objects.
     *
     * @param locator locator of the resource
     * @param request
     * @param response
     * @return a new <code>DavResource</code> object.
     * @throws DavException
     */
    public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response) throws DavException;

    /**
     * Create a new {@link DavResource} object from the given locator and session.
     *
     * @param locator
     * @param session
     * @return a new <code>DavResource</code> object.
     * @throws DavException
     */
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException;
}