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
 * <code>DavLocatorFactory</code>...
 */
public interface DavLocatorFactory {

    /**
     * Create a new <code>DavResourceLocator</code>.
     *
     * @param prefix String consisting of  [scheme:][//authority][path] where
     * path defines the (imaginary) path to the {@link DavResourceLocator#isRootLocation root location}.
     * @param href of the resource to be created. The given string may start with
     * the 'prefix'. Please note, that in contrast to
     * {@link DavLocatorFactory#createResourceLocator(String, String, String)} the
     * href is expected to be URL encoded.
     * @return a new resource locator.
     */
    public DavResourceLocator createResourceLocator(String prefix, String href);

    /**
     * Create a new <code>DavResourceLocator</code>. This methods corresponds to
     * {@link DavLocatorFactory#createResourceLocator(String, String, String, boolean)}
     * with the flag set to true.
     *
     * @param prefix String consisting of  [scheme:][//authority][path] where
     * path defines the path to the {@link DavResourceLocator#isRootLocation root location}.
     * @param workspacePath the first segment of the URIs path indicating the
     * workspace. The implementation may allow a empty String if workspaces
     * are not supported.
     * @param resourcePath the URL decoded resource path.
     * @return a new resource locator.
     */
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String resourcePath);

    /**
     *
     * @param prefix String consisting of  [scheme:][//authority][path] where
     * path defines the path to the {@link DavResourceLocator#isRootLocation root location}.
     * @param workspacePath the first segment of the URIs path indicating the
     * workspace. The implementation may allow a empty String if workspaces
     * are not supported.
     * @param path the URL decoded path.
     * @param isResourcePath If true this method returns the same as
     * {@link DavLocatorFactory#createResourceLocator(String, String, String)},
     * otherwise the given path is treated as internal repository path.
     * The implementation may choose to implement a conversion of the repository
     * path to a valid resource path, e.g. (un)escaping of certain characters, due
     * to incompatibility with the URI definition (or vice versa). Note that
     * {@link DavResourceLocator#getRepositoryPath()} should in this case implement
     * the reverse operation.
     * @return a new resource locator.
     * @see DavResourceLocator#getRepositoryPath()
     */
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String path, boolean isResourcePath);
}