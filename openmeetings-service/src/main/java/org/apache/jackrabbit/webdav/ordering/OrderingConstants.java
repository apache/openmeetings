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

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>OrderingConstants</code> provide constants for request and response
 * headers, Xml elements and property names defined by
 * <a href="http://www.ietf.org/rfc/rfc3648.txt">RFC 3648</a>.
 */
public interface OrderingConstants {

    /**
     * The namespace
     */
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

    /**
     * Constant representing the DAV:custom ordering type URI, which indicates
     * that the collection is not ordered.
     */
    public static final String ORDERING_TYPE_CUSTOM = "DAV:custom";

    /**
     * Constant representing the DAV:unordered ordering type URI, which indicates
     * that the collection is to be ordered, but the semantics of the ordering
     * is not being advertised.
     */
    public static final String ORDERING_TYPE_UNORDERED = "DAV:unordered";

    //---< Headers >------------------------------------------------------------
    /**
     * The "Ordering-Type" request header.
     */
    public static final String HEADER_ORDERING_TYPE = "Ordering-Type";

    /**
     * When a new member is added to a collection with a client-maintained
     * ordering (for example, with PUT, COPY, or MKCOL), its position in the
     * ordering can be set with the new Position header.<br><br>
     * <code>
     * Position = "Position" ":" ("first" | "last" | (("before" | "after") segment))
     * </code>
     * <br><br>NOTE: segment is defined in section 3.3 of RFC2396.
     */
    public static final String HEADER_POSITION = "Position";

    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * Xml elements used for reordering internal members of a collection.
     */
    public static final String XML_ORDERPATCH = "orderpatch";
    public static final String XML_ORDERING_TYPE = "ordering-type";
    public static final String XML_ORDER_MEMBER = "order-member";
    public static final String XML_POSITION = "position";
    public static final String XML_SEGMENT = "segment";

    public static final String XML_FIRST = "first";
    public static final String XML_LAST = "last";
    public static final String XML_BEFORE = "before";
    public static final String XML_AFTER = "after";

    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * The DAV:ordering-type property indicates whether the collection is
     * ordered and, if so, uniquely identifies the semantics of the ordering.
     *
     * @see OrderingType
     */
    public static final DavPropertyName ORDERING_TYPE = DavPropertyName.create("ordering-type", DavConstants.NAMESPACE);

    /**
     * Required live property for resources that honor the 'ordered-collections'
     * compliance class defined by RFC 3648.<br>
     * The supported-method-set property has been introduced with RFC 3253.
     *
     * @see org.apache.jackrabbit.webdav.version.DeltaVConstants#SUPPORTED_METHOD_SET
     */
    public static final DavPropertyName SUPPORTED_METHOD_SET = DavPropertyName.create("supported-method-set", DavConstants.NAMESPACE);

    /**
     * Required live property for resources that honor the 'ordered-collections'
     * compliance class defined by RFC 3648.<br>
     * The supported-live-property-set property has been introduced with RFC 3253.
     *
     * @see org.apache.jackrabbit.webdav.version.DeltaVConstants#SUPPORTED_LIVE_PROPERTY_SET
     */
    public static final DavPropertyName SUPPORTED_LIVE_PROPERTY_SET = DavPropertyName.create("supported-live-property-set", DavConstants.NAMESPACE);
}