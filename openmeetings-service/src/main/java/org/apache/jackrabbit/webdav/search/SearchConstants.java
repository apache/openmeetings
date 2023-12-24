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
package org.apache.jackrabbit.webdav.search;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>SearchConstants</code> interface provide constants for request
 * and response headers, Xml elements and property names used for WebDAV
 * search.
 */
public interface SearchConstants {

    /**
     * Namespace definition.<br>
     * NOTE: For convenience reasons, the namespace is defined to be the default
     * {@link  DavConstants#NAMESPACE DAV:} namespace. This is not correct for the
     * underlying specification is still in a draft state. See also the editorial
     * note inside the
     * <a href="http://greenbytes.de/tech/webdav/draft-reschke-webdav-search-latest.html#rfc.section.1.5">Internet Draft WebDAV Search</a>
     * document.
     */
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

    /**
     * Predefined basic query grammer.
     */
    public static final String BASICSEARCH = NAMESPACE.getPrefix()+"basicsearch";

    //---< Headers >------------------------------------------------------------
    /**
     * The DASL response header specifying the query languages supported by
     * the requested resource.
     */
    public static final String HEADER_DASL = "DASL";

    //---< XML Element, Attribute Names >---------------------------------------
    /**
     * Xml element name for a single query grammar element inside
     * the {@link #QUERY_GRAMMER_SET supported-query-grammer-set property}.
     */
    public static final String XML_QUERY_GRAMMAR = "supported-query-grammar";

    /**
     * Name constant for the 'DAV:grammar' element, which is used inside the
     * {@link #XML_QUERY_GRAMMAR} element.
     */
    public static final String XML_GRAMMER = "grammar";

    /**
     * Xml element name for the required request body of a SEARCH request.
     *
     * @see SearchInfo
     * @see SearchResource#search(SearchInfo)
     */
    public static final String XML_SEARCHREQUEST = "searchrequest";

    /**
     * Optional Xml element name used in the SEARCH request body instead of {@link #XML_SEARCHREQUEST}
     * in order to access a given query schema.
     */
    public static final String XML_QUERY_SCHEMA_DISCOVERY = "query-schema-discovery";

    //---< Property Names >-----------------------------------------------------
    /**
     * Property indicating the set of query languages the given resource is
     * able deal with. The property has the following definition:<br>
     * <pre>
     * &lt;!ELEMENT supported-query-grammar-set (supported-query-grammar*)&gt;
     * &lt;!ELEMENT supported-query-grammar grammar&gt;
     * &lt;!ELEMENT grammar ANY&gt;
     * </pre>
     */
    public static final DavPropertyName QUERY_GRAMMER_SET = DavPropertyName.create("supported-query-grammar-set", NAMESPACE);
}