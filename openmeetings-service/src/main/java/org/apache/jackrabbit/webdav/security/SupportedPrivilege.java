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

import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>SupportedPrivilege</code>...
 */
public class SupportedPrivilege implements XmlSerializable {

    private static final String XML_SUPPORTED_PRIVILEGE = "supported-privilege";
    private static final String XML_ABSTRACT = "abstract";
    private static final String XML_DESCRIPTION = "description";

    private final Privilege privilege;
    private final boolean isAbstract;
    private final String description;
    private final String descriptionLanguage;
    private final SupportedPrivilege[] supportedPrivileges;

    /**
     *
     * @param privilege
     * @param description
     * @param descriptionLanguage
     * @param isAbstract
     * @param supportedPrivileges
     */
    public SupportedPrivilege(Privilege privilege, String description,
                              String descriptionLanguage, boolean isAbstract,
                              SupportedPrivilege[] supportedPrivileges) {
        if (privilege == null) {
            throw new IllegalArgumentException("DAV:supported-privilege element must contain a single privilege.");
        }
        this.privilege = privilege;
        this.description = description;
        this.descriptionLanguage = descriptionLanguage;
        this.isAbstract = isAbstract;
        this.supportedPrivileges = supportedPrivileges;
    }

    /**
     * @see XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        Element spElem = DomUtil.createElement(document, XML_SUPPORTED_PRIVILEGE, SecurityConstants.NAMESPACE);
        spElem.appendChild(privilege.toXml(document));
        if (isAbstract) {
            DomUtil.addChildElement(spElem, XML_ABSTRACT, SecurityConstants.NAMESPACE);
        }
        if (description != null) {
            Element desc = DomUtil.addChildElement(spElem, XML_DESCRIPTION, SecurityConstants.NAMESPACE, description);
            if (descriptionLanguage != null) {
                DomUtil.setAttribute(desc, "lang", Namespace.XML_NAMESPACE, descriptionLanguage);
            }
        }
        if (supportedPrivileges != null) {
            for (SupportedPrivilege supportedPrivilege : supportedPrivileges) {
                spElem.appendChild(supportedPrivilege.toXml(document));
            }
        }
        return spElem;
    }

    public Privilege getPrivilege() {
        return privilege;
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public SupportedPrivilege[] getSupportedPrivileges() {
        return supportedPrivileges;
    }
    
    /**
     * Factory method to create/retrieve a <code>SupportedPrivilege</code> from the given
     * DAV:privilege element.
     *
     * @param privilege
     * @return
     */
    static SupportedPrivilege getSupportedPrivilege(Element supportedPrivilege) throws DavException {
        if (!DomUtil.matches(supportedPrivilege, XML_SUPPORTED_PRIVILEGE, SecurityConstants.NAMESPACE)) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:supported-privilege element expected.");
        }
        boolean isAbstract = false;
        Privilege privilege = null;
        String description = null;
        String descriptionLanguage = null;
        List<SupportedPrivilege> sp = new ArrayList<SupportedPrivilege>();

        ElementIterator children = DomUtil.getChildren(supportedPrivilege);
        while (children.hasNext()) {
            Element child = children.next();
            if (child.getLocalName().equals(XML_ABSTRACT)) {
                isAbstract = true;
            } else if (child.getLocalName().equals(Privilege.XML_PRIVILEGE)) {
                privilege = Privilege.getPrivilege(child);
            } else if (child.getLocalName().equals(XML_DESCRIPTION)) {
                description = child.getLocalName();
                if (child.hasAttribute(descriptionLanguage)) {
                    descriptionLanguage = child.getAttribute(descriptionLanguage);
                }
            } else if (child.getLocalName().equals(XML_SUPPORTED_PRIVILEGE)) {
                sp.add(getSupportedPrivilege(child));
            }
        }
        return new SupportedPrivilege(privilege, description,
                                     descriptionLanguage, isAbstract,
                                     sp.toArray(new SupportedPrivilege[sp.size()]));
    }
}
