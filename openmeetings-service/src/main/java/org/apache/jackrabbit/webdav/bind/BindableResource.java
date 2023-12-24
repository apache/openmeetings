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

import java.util.Set;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public interface BindableResource {

    /**
     * Will add a new binding to the given collection referencing this resource.
     *
     * @param collection the collection to create the new binding in.
     * @param newBinding the new binding
     */
    public void bind(DavResource collection, DavResource newBinding) throws DavException;

    /**
     * Will rebind the resource to the given collection. By definition, this is
     * an atomic move operation.
     *
     * @param collection the collection to create the new binding in.
     * @param newBinding the new binding
     */
    public void rebind(DavResource collection, DavResource newBinding) throws DavException;

    /**
     * Will retrieve a collection of parent elements of the bindable resource
     * representing the parent set.
     *
     * @return newBinding the new binding
     */
    public Set<ParentElement> getParentElements();
}
