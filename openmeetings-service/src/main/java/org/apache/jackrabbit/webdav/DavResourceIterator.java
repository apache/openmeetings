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

import java.util.Iterator;

/**
 * DavResourceIterator extends the <code>Iterator</code> interface. Additional
 * METHODS allow to retrieve the next {@link DavResource} from the iterator
 * and the iterators size.
 */
public interface DavResourceIterator extends Iterator<DavResource> {

    /**
     * Returns the next {@link DavResource} in the iterator
     * @return the next {@link DavResource}
     */
    public DavResource nextResource();

    /**
     * Return the number of {@link DavResource}s in the iterator.
     * @return number of elements in the iterator.
     */
    public int size();
}
