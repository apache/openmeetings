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

import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.xml.Namespace;

import java.text.DateFormat;

/**
 * <code>DavConstants</code> provide constants for request and response
 * headers, XML elements and property names defined by
 * <a href="http://www.webdav.org/specs/rfc2518.html">RFC 2518</a>. In addition,
 * common date formats (creation date and modification time) are included.
 */
public interface DavConstants {

    /**
     * Default Namespace constant
     */
    public static final Namespace NAMESPACE = Namespace.getNamespace("D", "DAV:");

    //--------------------------------< Headers (Names and Value Constants) >---
    public static final String HEADER_DAV = "DAV";
    public static final String HEADER_DESTINATION = "Destination";
    public static final String HEADER_IF = "If";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_LANGUAGE = "Content-Language";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";

    //--------------------------------------------------< Lock-Token Header >---
    public static final String HEADER_LOCK_TOKEN = "Lock-Token";
    public static final String OPAQUE_LOCK_TOKEN_PREFIX = "opaquelocktoken:";

    //-----------------------------------------------------< Timeout Header >---
    public static final String HEADER_TIMEOUT = "Timeout";
    public static final String TIMEOUT_INFINITE = "Infinite";
    // RFC 2518: timeout value for TimeType "Second" MUST NOT be greater than 2^32-1
    public static final long INFINITE_TIMEOUT = Integer.MAX_VALUE;
    public static final long UNDEFINED_TIMEOUT = Integer.MIN_VALUE;

    //---------------------------------------------------< Overwrite Header >---
    public static final String HEADER_OVERWRITE = "Overwrite";

    //-------------------------------------------------------< Depth Header >---
    public static final String HEADER_DEPTH = "Depth";
    public static final String DEPTH_INFINITY_S = "infinity";
    public static final int DEPTH_INFINITY = Integer.MAX_VALUE;
    public static final int DEPTH_0 = 0;
    public static final int DEPTH_1 = 1;

    //---< XML Element, Attribute Names >---------------------------------------
    public static final String XML_ALLPROP = "allprop";
    public static final String XML_COLLECTION = "collection";
    public static final String XML_DST = "dst";
    public static final String XML_HREF = "href";
    public static final String XML_INCLUDE = "include";
    public static final String XML_KEEPALIVE = "keepalive";
    public static final String XML_LINK = "link";
    public static final String XML_MULTISTATUS = "multistatus";
    public static final String XML_OMIT = "omit";
    public static final String XML_PROP = "prop";
    public static final String XML_PROPERTYBEHAVIOR = "propertybehavior";
    public static final String XML_PROPERTYUPDATE = "propertyupdate";
    public static final String XML_PROPFIND = "propfind";
    public static final String XML_PROPNAME = "propname";
    public static final String XML_PROPSTAT = "propstat";
    public static final String XML_REMOVE = "remove";
    public static final String XML_RESPONSE = "response";
    public static final String XML_RESPONSEDESCRIPTION = "responsedescription";
    public static final String XML_SET = "set";
    public static final String XML_SOURCE = "source";
    public static final String XML_STATUS = "status";

    //------------------------------------------------------------< locking >---
    public static final String XML_ACTIVELOCK = "activelock";
    public static final String XML_DEPTH = "depth";
    public static final String XML_LOCKTOKEN = "locktoken";
    public static final String XML_TIMEOUT = "timeout";
    public static final String XML_LOCKSCOPE = "lockscope";
    public static final String XML_EXCLUSIVE = "exclusive";
    public static final String XML_SHARED = "shared";
    public static final String XML_LOCKENTRY = "lockentry";
    public static final String XML_LOCKINFO = "lockinfo";
    public static final String XML_LOCKTYPE = "locktype";
    public static final String XML_WRITE = "write";
    public static final String XML_OWNER = "owner";
    /**
     * The <code>lockroot</code> XML element
     * @see <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_lockroot">RFC 4918</a>
     */
    public static final String XML_LOCKROOT = "lockroot";

    //-----------------------------------------------------< Property Names >---
    /*
     * Webdav property names as defined by RFC 2518<br>
     * Note: Microsoft webdav clients as well as Webdrive request additional
     * property (e.g. href, name, owner, isRootLocation, isCollection)  within the
     * default namespace, which are are ignored by this implementation, except
     * for the 'isCollection' property, needed for XP built-in clients.
     */
    public static final String PROPERTY_CREATIONDATE = "creationdate";
    public static final String PROPERTY_DISPLAYNAME = "displayname";
    public static final String PROPERTY_GETCONTENTLANGUAGE = "getcontentlanguage";
    public static final String PROPERTY_GETCONTENTLENGTH = "getcontentlength";
    public static final String PROPERTY_GETCONTENTTYPE = "getcontenttype";
    public static final String PROPERTY_GETETAG = "getetag";
    public static final String PROPERTY_GETLASTMODIFIED = "getlastmodified";
    public static final String PROPERTY_LOCKDISCOVERY = "lockdiscovery";
    public static final String PROPERTY_RESOURCETYPE = "resourcetype";
    public static final String PROPERTY_SOURCE = "source";
    public static final String PROPERTY_SUPPORTEDLOCK = "supportedlock";

    //-------------------------------------------------< PropFind Constants >---
    public static final int PROPFIND_BY_PROPERTY = 0;
    public static final int PROPFIND_ALL_PROP = 1;
    public static final int PROPFIND_PROPERTY_NAMES = 2;
    public static final int PROPFIND_ALL_PROP_INCLUDE = 3; // RFC 4918, Section 9.1

    //----------------------------------------------< Date Format Constants >---
    /**
     * Marker for undefined modification or creation time.
     */
    public static long UNDEFINED_TIME = -1;

    /**
     * modificationDate date format per RFC 1123.<br>
     * NOTE: Access to <code>DateFormat</code> isn't thread save.
     */
    public static DateFormat modificationDateFormat = HttpDateFormat.modificationDateFormat();

    /**
     * Simple date format for the creation date ISO representation (partial).<br>
     * NOTE: Access to <code>DateFormat</code> isn't thread save.
     */
    public static DateFormat creationDateFormat = HttpDateFormat.creationDateFormat();
}
