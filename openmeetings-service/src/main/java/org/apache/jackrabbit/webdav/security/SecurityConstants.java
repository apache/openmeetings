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
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * <code>SecurityConstants</code> interface lists constants defined by
 * <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744</a> (WebDAV Access
 * Control Protocol).
 */
public interface SecurityConstants {

    /**
     * Default Namespace constant
     */
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;

    /**
     * Principal resources must define DAV:principal XML element in the value
     * of the DAV:resourcetype property.
     */
    public static int PRINCIPAL_RESOURCETYPE = ResourceType.registerResourceType("principal", NAMESPACE);

    //---< Property Names for Principal Resource >------------------------------
    /**
     * Protected href property DAV:principal-URL for principal resources.
     * @see HrefProperty
     */
    public static final DavPropertyName PRINCIPAL_URL = DavPropertyName.create("principal-URL", NAMESPACE);
    /**
     * Protected href property DAV:alternate-URI-set for principal resources.
     * @see HrefProperty
     */
    public static final DavPropertyName ALTERNATE_URI_SET = DavPropertyName.create("alternate-URI-set", NAMESPACE);
    /**
     * Protected href property DAV:group-member-set for principal resources.
     * @see HrefProperty
     */
    public static final DavPropertyName GROUP_MEMBER_SET = DavPropertyName.create("group-member-set", NAMESPACE);
    /**
     * Protected href property DAV:group-membership for principal resources.
     * @see HrefProperty
     */
    public static final DavPropertyName GROUP_MEMBERSHIP = DavPropertyName.create("group-membership", NAMESPACE);

    //---< Property Names for DavResource >-------------------------------------
    /**
     * Protected href property DAV:owner
     * @see HrefProperty
     */
    public static final DavPropertyName OWNER = DavPropertyName.create("owner", NAMESPACE);
    /**
     * Protected href property DAV:group
     * @see HrefProperty
     */
    public static final DavPropertyName GROUP = DavPropertyName.create("group", NAMESPACE);
    /**
     * Protected property DAV:supported-privilege-set
     * @see CurrentUserPrivilegeSetProperty
     */
    public static final DavPropertyName SUPPORTED_PRIVILEGE_SET = DavPropertyName.create("supported-privilege-set", NAMESPACE);
    /**
     * Protected property DAV:current-user-privilege-set
     * @see CurrentUserPrivilegeSetProperty
     */
    public static final DavPropertyName CURRENT_USER_PRIVILEGE_SET = DavPropertyName.create("current-user-privilege-set", NAMESPACE);
    /**
     * Protected property DAV:acl. Note, that DAV:acl property may be altered
     * with a ACL request.
     *
     * @see AclProperty
     * @see DavMethods#METHOD_ACL
     */
    public static final DavPropertyName ACL = DavPropertyName.create("acl", NAMESPACE);
    /**
     * Protected property DAV:acl-restrictions
     * @see AclRestrictionsProperty
     */
    public static final DavPropertyName ACL_RESTRICTIONS = DavPropertyName.create("acl-restrictions", NAMESPACE);
    /**
     * Protected href property DAV:inherited-acl-set
     * @see HrefProperty
     */
    public static final DavPropertyName INHERITED_ACL_SET = DavPropertyName.create("inherited-acl-set", NAMESPACE);
    /**
     * Protected href property DAV:principal-collection-set
     * @see HrefProperty
     */
    public static final DavPropertyName PRINCIPAL_COLLECTION_SET = DavPropertyName.create("principal-collection-set", NAMESPACE);
}
