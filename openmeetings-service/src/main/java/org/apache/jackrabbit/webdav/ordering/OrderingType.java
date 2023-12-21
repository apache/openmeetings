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

import org.apache.jackrabbit.webdav.property.HrefProperty;

/**
 * <code>OrderingType</code> represents the {@link #ORDERING_TYPE
 * DAV:ordering-type} property as defined by
 * <a href="http://www.ietf.org/rfc/rfc3648.txt">RFC 3648</a>. This property is
 * protected cannot be set using PROPPATCH. Its value may only be set by
 * including the Ordering-Type header with a MKCOL request or by submitting an
 * ORDERPATCH request.
 *
 * @see org.apache.jackrabbit.webdav.property.DavProperty#isInvisibleInAllprop()
 */
public class OrderingType extends HrefProperty implements OrderingConstants {

    /**
     * Creates a <code>OrderingType</code> with the default type (e.g. default
     * value). The default value is specified to be {@link #ORDERING_TYPE_UNORDERED}.
     */
    public OrderingType() {
        this(null);
    }

    /**
     * Create an <code>OrderingType</code> with the given ordering.<br>
     * NOTE: the ordering-type property is defined to be protected.
     *
     * @param href
     * @see org.apache.jackrabbit.webdav.property.DavProperty#isInvisibleInAllprop()
     */
    public OrderingType(String href) {
        // spec requires that the default value is 'DAV:unordered'
        super(ORDERING_TYPE, (href != null) ? href : ORDERING_TYPE_UNORDERED, true);
    }
}
