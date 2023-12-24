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
 * <code>DavResourceLocator</code>...
 */
public interface DavResourceLocator {

    /**
     * Return the prefix used to build the complete href of the resource as
     * required for the {@link DavConstants#XML_HREF href Xml} element.
     * This includes scheme and host information as well as constant prefixes.
     * However, this must not include workspace prefix.
     *
     * @return prefix needed in order to build the href from a resource path.
     * @see #getResourcePath()
     */
    public String getPrefix();

    /**
     * Return the resource path.
     *
     * @return resource path
     */
    public String getResourcePath();

    /**
     * Return the path of the workspace the resource identified by this
     * locator is member of.
     *
     * @return path of the workspace
     */
    public String getWorkspacePath();

    /**
     * Return the name of the workspace the resource identified by this
     * locator is member of.
     *
     * @return workspace name
     */
    public String getWorkspaceName();

    /**
     * Returns true if the specified locator refers to a resource within the
     * same workspace.
     *
     * @param locator
     * @return true if both paths are in the same workspace.
     */
    public boolean isSameWorkspace(DavResourceLocator locator);

    /**
     * Returns true if the specified workspace name equals to the workspace
     * name defined with this locator.
     *
     * @param workspaceName
     * @return true if workspace names are equal.
     */
    public boolean isSameWorkspace(String workspaceName);

    /**
     * Return the 'href' representation of this locator object. The implementation
     * should perform an URL encoding of the resource path.
     *
     * @param isCollection
     * @return 'href' representation of this path
     * @see DavConstants#XML_HREF
     * @see DavResource#getHref()
     */
    public String getHref(boolean isCollection);

    /**
     * Returns true if this <code>DavResourceLocator</code> represents the root
     * locator that would be requested with 'hrefPrefix'+'pathPrefix' with or
     * without a trailing '/'.
     *
     * @return true if this locator object belongs to the root resource.
     */
    public boolean isRootLocation();

    /**
     * Return the locator factory that created this locator.
     *
     * @return the locator factory
     */
    public DavLocatorFactory getFactory();

    /**
     * An implementation may choose to circumvent the incompatibility of a
     * repository path with the URI path by applying an appropriate conversion.
     * This utility method allows to retrieve this transformed repository path.
     * By default this method should return the same as {@link #getResourcePath()}
     *
     * @return a repository compatible form if the resource path.
     * @see DavLocatorFactory#createResourceLocator(String, String, String, boolean)
     * that allows to build a valid <code>DavResourceLocator</code> from a given
     * repository path.
     */
    public String getRepositoryPath();
}