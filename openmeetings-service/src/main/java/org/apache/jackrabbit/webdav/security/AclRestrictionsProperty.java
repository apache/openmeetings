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

import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>AclRestrictionsProperty</code> as defined by RFC 3744 Section 5.6.
 * DAV:acl-restrictions:
 * <pre>
 * &lt;!ELEMENT acl-restrictions (grant-only?, no-invert?, deny-before-grant?, required-principal?)&gt;
 * &lt;!ELEMENT grant-only EMPTY&gt;
 * &lt;!ELEMENT no-invert EMPTY&gt;
 * &lt;!ELEMENT deny-before-grant EMPTY&gt;
 * &lt;!ELEMENT required-principal (all? | authenticated? | unauthenticated? | self? | href* | property*)&gt;
 * </pre>
 *
 * @see Principal
 * @see AclProperty
 */
public class AclRestrictionsProperty extends AbstractDavProperty {

    // TODO: RFC 3744 defines a distinct structure for required-principal
    // than for principal. Current 'normal' principal is used instead.

    private static final String XML_GRANT_ONLY = "grant-only";
    private static final String XML_NO_INVERT = "no-invert";
    private static final String XML_DENY_BEFORE_GRANT = "deny-before-grant";

    private final boolean grantOnly;
    private final boolean noInvert;
    private final boolean denyBeforeGrant;
    private final Principal requiredPrincipal;

    public AclRestrictionsProperty(boolean grantOnly, boolean noInvert, boolean denyBeforeGrant, Principal requiredPrincipal) {
        super(SecurityConstants.ACL_RESTRICTIONS, true);
        this.grantOnly = grantOnly;
        this.noInvert = noInvert;
        this.denyBeforeGrant = denyBeforeGrant;
        this.requiredPrincipal = requiredPrincipal;
    }

    /**
     * Not implemented.
     *
     * @throws UnsupportedOperationException
     */
    public Object getValue() {
        throw new UnsupportedOperationException("Not implemented. Use the property specific methods instead.");
    }

   /**
     * @see DavProperty#toXml(Document)
     */
    @Override
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);
        if (grantOnly) {
            DomUtil.addChildElement(elem, XML_GRANT_ONLY, SecurityConstants.NAMESPACE);
        }
        if (noInvert) {
            DomUtil.addChildElement(elem, XML_NO_INVERT, SecurityConstants.NAMESPACE);
        }
        if (denyBeforeGrant) {
            DomUtil.addChildElement(elem, XML_DENY_BEFORE_GRANT, SecurityConstants.NAMESPACE);
        }
        if (requiredPrincipal != null) {
            elem.appendChild(requiredPrincipal.toXml(document));
        }
        return elem;
    }

    public boolean isGrantOnly() {
        return grantOnly;
    }

    public boolean isNoInvert() {
        return noInvert;
    }

    public boolean isDenyBeforeGrant() {
        return denyBeforeGrant;
    }

    public Principal getRequiredPrincipal() {
        // TODO: check of should be replaced by specific required-principal...
        return requiredPrincipal;
    }
}
