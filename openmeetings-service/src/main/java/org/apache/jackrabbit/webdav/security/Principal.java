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
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>Principal</code> encapsulates the DAV:principal element which identifies
 * the principal to which this ACE applies.
 * RFC 3744 defines the following structure for this element:
 * <pre>
 * &lt;!ELEMENT principal (href | all | authenticated | unauthenticated | property | self)&gt;
 * </pre>
 */
public class Principal implements XmlSerializable, SecurityConstants {

    public static final String XML_PRINCIPAL = "principal";

    private static final String XML_ALL = "all";
    private static final String XML_AUTHENTICATED  = "authenticated";
    private static final String XML_UNAUTHENTICATED = "unauthenticated";
    private static final String XML_SELF = "self";
    private static final String XML_PROPERTY = "property";

    private static final int TYPE_ALL = 0;
    private static final int TYPE_AUTHENTICATED = 1;
    private static final int TYPE_UNAUTHENTICATED = 2;
    private static final int TYPE_SELF = 3;
    private static final int TYPE_PROPERTY = 4;
    private static final int TYPE_HREF = 5;

    private static final Principal ALL_PRINCIPAL = new Principal(TYPE_ALL);
    private static final Principal AUTHENTICATED_PRINCIPAL = new Principal(TYPE_AUTHENTICATED);
    private static final Principal UNAUTHENTICATED_PRINCIPAL = new Principal(TYPE_UNAUTHENTICATED);
    private static final Principal SELF_PRINCIPAL = new Principal(TYPE_SELF);

    private static final Map<DavPropertyName, Principal> PROP_PRINCIPALS = new HashMap<DavPropertyName, Principal>();

    private final int type;
    private DavPropertyName propertyName;
    private String href;

    private Principal(int type) {
        this.type = type;
    }

    private Principal(DavPropertyName propertyName) {
        this.type = TYPE_PROPERTY;
        this.propertyName = propertyName;
    }

    private Principal(String href) {
        this.type = TYPE_HREF;
        this.href = href;
    }

    /**
     * @return href if this Principal is a href-typed principal, <code>null</code>
     * otherwise.
     */
    public String getHref() {
        return href;
    }

    /**
     * @return propertyName if this Principal is a property-principal,
     * <code>null</code> otherwise.
     */
    public DavPropertyName getPropertyName() {
        return propertyName;
    }

    /**
     * @see XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element pEl = DomUtil.createElement(document, XML_PRINCIPAL, NAMESPACE);
        switch (type) {
            case TYPE_ALL:
                DomUtil.addChildElement(pEl, XML_ALL, NAMESPACE);
                break;
            case TYPE_AUTHENTICATED:
                DomUtil.addChildElement(pEl, XML_AUTHENTICATED, NAMESPACE);
                break;
            case TYPE_UNAUTHENTICATED:
                DomUtil.addChildElement(pEl, XML_UNAUTHENTICATED, NAMESPACE);
                break;
            case TYPE_SELF:
                DomUtil.addChildElement(pEl, XML_SELF, NAMESPACE);
                break;
            case TYPE_PROPERTY:
                Element prop = DomUtil.addChildElement(pEl, XML_PROPERTY, NAMESPACE);
                prop.appendChild(propertyName.toXml(document));
                break;
            case TYPE_HREF:
                Element hrefEl = DomUtil.hrefToXml(href, document);
                pEl.appendChild(hrefEl);
                break;
            // no default
        }
        return pEl;
    }

    public static Principal getAllPrincipal() {
        return ALL_PRINCIPAL;
    }

    public static Principal getAuthenticatedPrincipal() {
        return AUTHENTICATED_PRINCIPAL;
    }

    public static Principal getUnauthenticatedPrincipal() {
        return UNAUTHENTICATED_PRINCIPAL;
    }

    public static Principal getSelfPrincipal() {
        return SELF_PRINCIPAL;
    }

    public static Principal getPropertyPrincipal(DavPropertyName propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Property-Principal must contain a valid property name.");
        }
        // there is a limited amount of properties, that can be used
        // for a Property-Principal
        if (PROP_PRINCIPALS.containsKey(propertyName)) {
            return PROP_PRINCIPALS.get(propertyName);
        } else {
            Principal p = new Principal(propertyName);
            PROP_PRINCIPALS.put(propertyName, p);
            return p;
        }
    }

    public static Principal getHrefPrincipal(String href) {
        if (href == null) {
            throw new IllegalArgumentException("Href-Principal must contain a valid href.");
        }
        return new Principal(href);
    }

    public static Principal createFromXml(Element principalElement) throws DavException {
        if (!DomUtil.matches(principalElement, XML_PRINCIPAL, NAMESPACE)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:principal element expected.");
        }
        if (DomUtil.hasChildElement(principalElement, XML_ALL, NAMESPACE)) {
            return ALL_PRINCIPAL;
        } else if (DomUtil.hasChildElement(principalElement, XML_SELF, NAMESPACE)) {
            return SELF_PRINCIPAL;
        } else if (DomUtil.hasChildElement(principalElement, XML_AUTHENTICATED, NAMESPACE)) {
            return AUTHENTICATED_PRINCIPAL;
        } else if (DomUtil.hasChildElement(principalElement, XML_UNAUTHENTICATED, NAMESPACE)) {
            return UNAUTHENTICATED_PRINCIPAL;
        } else if (DomUtil.hasChildElement(principalElement, DavConstants.XML_HREF, DavConstants.NAMESPACE)) {
            String href = DomUtil.getChildText(principalElement, DavConstants.XML_HREF, DavConstants.NAMESPACE);
            return getHrefPrincipal(href);
        } else if (DomUtil.hasChildElement(principalElement, XML_PROPERTY, NAMESPACE)) {
            Element propEl = DomUtil.getChildElement(principalElement, XML_PROPERTY, NAMESPACE);
            DavPropertyName pn = DavPropertyName.createFromXml(DomUtil.getFirstChildElement(propEl));
            return getPropertyPrincipal(pn);
        } else {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "Invalid structure inside DAV:principal element.");
        }
    }
}
