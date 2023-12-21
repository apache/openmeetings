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

import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;

/**
 * <code>BindConstants</code> provide constants for request and response
 * headers, Xml elements and property names defined by
 * the BIND specification.
 */
public interface BindConstants {

    /**
     * The namespace
     */
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

    /**
     * local names of XML elements used in the request bodies of the methods BIND, REBIND and UNBIND.
     */
    public static final String XML_BIND = "bind";
    public static final String XML_REBIND = "rebind";
    public static final String XML_UNBIND = "unbind";
    public static final String XML_SEGMENT = "segment";
    public static final String XML_HREF = "href";
    public static final String XML_PARENT = "parent";

    public static final String METHODS = "BIND, REBIND, UNBIND";

    /*
     * Webdav properties defined by the BIND specification. 
     */
    public static final DavPropertyName RESOURCEID = DavPropertyName.create("resource-id");
    public static final DavPropertyName PARENTSET = DavPropertyName.create("parent-set");
}
