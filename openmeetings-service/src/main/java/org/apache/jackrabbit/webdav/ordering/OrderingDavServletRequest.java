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
import org.apache.jackrabbit.webdav.DavServletRequest;

/**
 * <code>OrderingDavServletRequest</code> provides extensions to the
 * {@link DavServletRequest} interface used for ordering members of orderable
 * collections.
 */
public interface OrderingDavServletRequest extends DavServletRequest {

    /**
     * Returns the {@link OrderingConstants#HEADER_ORDERING_TYPE Ordering-Type header}.
     *
     * @return the String value of the {@link OrderingConstants#HEADER_ORDERING_TYPE Ordering-Type header}.
     */
    public String getOrderingType();

    /**
     * Return a <code>Position</code> object encapsulating the {@link OrderingConstants#HEADER_POSITION
     * Position header} field or <code>null</code> if no Position header is present
     * or does not contain a valid format.
     *
     * @return <code>Position</code> object encapsulating the {@link OrderingConstants#HEADER_POSITION
     * Position header}
     */
    public Position getPosition();

    /**
     * Return a <code>OrderPatch</code> object encapsulating the request body
     * of an ORDERPATCH request or <code>null</code> if the request body was
     * either missing or could not be parsed.
     *
     * @return <code>OrderPatch</code> object encapsulating the request body.
     */
    public OrderPatch getOrderPatch() throws DavException;

}