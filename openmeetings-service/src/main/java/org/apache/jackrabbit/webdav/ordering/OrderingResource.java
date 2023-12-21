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
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

/**
 * <code>OrderingResource</code> extends the {@link DavResource} interface by
 * METHODS relating to ordering functionality defined by
 * <a href="http://www.ietf.org/rfc/rfc3648.txt">RFC 3648</a>.
 */
public interface OrderingResource extends DavResource {

    public String METHODS = "ORDERPATCH";

    /**
     * Returns true if this resources allows ordering of its internal members.
     *
     * @return true if internal members are orderable.
     */
    public boolean isOrderable();

    /**
     * Reorders the internal members of this resource according to the
     * instructions present in the specified {@link OrderPatch} object.
     *
     * @param orderPatch as present in the ORDERPATCH request body.
     * @throws DavException
     */
    public void orderMembers(OrderPatch orderPatch) throws DavException;

}