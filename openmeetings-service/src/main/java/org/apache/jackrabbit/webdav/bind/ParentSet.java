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

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;

import java.util.Collection;

/**
 * <code>ParentSet</code> represents a DAV:parent-set property.
 */
public class ParentSet extends AbstractDavProperty<Collection<ParentElement>> {

    private final Collection<ParentElement> parents;

    /**
     * Creates a new ParentSet from a collection of <code>ParentElement</code> objects.
     * @param parents
     */
    public ParentSet(Collection<ParentElement> parents) {
        super(BindConstants.PARENTSET, true);
        this.parents = parents;
    }

    /**
     * @see org.apache.jackrabbit.webdav.property.AbstractDavProperty#getValue() 
     */
    public Collection<ParentElement> getValue() {
        return this.parents;
    }
}
