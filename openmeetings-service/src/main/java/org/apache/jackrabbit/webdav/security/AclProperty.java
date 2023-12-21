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
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>AclProperty</code> defines a protected property that specifies the list
 * of access control entries (ACEs). The set of ACEs define the privileges granted
 * or denied to principals on the resource a given property instance belongs to.
 * <br>
 * RFC 3744 defines the following format for this property:
 * <pre>
 * &lt;!ELEMENT acl (ace*) &gt;
 * &lt;!ELEMENT ace ((principal | invert), (grant|deny), protected?, inherited?)&gt;
 * &lt;!ELEMENT principal (href | all | authenticated | unauthenticated | property | self)&gt;
 * &lt;!ELEMENT invert (principal)&gt;
 * &lt;!ELEMENT grant (privilege+)&gt;
 * &lt;!ELEMENT deny (privilege+)&gt;
 * &lt;!ELEMENT protected EMPTY&gt;
 * &lt;!ELEMENT inherited (href)&gt;
 * </pre>
 *
 * @see Principal for details regarding DAV:principal
 * @see Privilege for details regarding DAV:privilege
 */
public class AclProperty extends AbstractDavProperty<List<AclProperty.Ace>> {

    private final List<Ace> aces;

    /**
     * Create a new <code>AclProperty</code> from the given ACEs.
     *
     * @see AclProperty#createGrantAce(Principal, Privilege[], boolean, boolean, AclResource) for a factory method to create a grant ACE.
     * @see AclProperty#createDenyAce(Principal, Privilege[], boolean, boolean, AclResource) for a factory method to create a deny ACE.
     */
    public AclProperty(Ace[] accessControlElements) {
        this((accessControlElements == null) ? new ArrayList<Ace>() : Arrays.asList(accessControlElements));
    }

    private AclProperty(List<Ace> aces) {
        super(SecurityConstants.ACL, true);
        this.aces = aces;
    }

    /**
     * @return a List of <code>Ace</code> object. If this property defines no ACEs
     * an empty list is returned.
     * @see DavProperty#getValue()
     */
    public List<Ace> getValue() {
        return aces;
    }

    /**
     * Build a new <code>AclProperty</code> object from the request body of the
     * ACL method call.
     *
     * @param aclElement
     * @return new <code>AclProperty</code>
     * @throws DavException
     */
    public static AclProperty createFromXml(Element aclElement) throws DavException {
        if (!DomUtil.matches(aclElement, SecurityConstants.ACL.getName(), SecurityConstants.ACL.getNamespace())) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "ACL request requires a DAV:acl body.");
        }
        List<Ace> aces = new ArrayList<Ace>();
        ElementIterator it = DomUtil.getChildren(aclElement, Ace.XML_ACE, SecurityConstants.NAMESPACE);
        while (it.hasNext()) {
            Element aceElem = it.nextElement();
            aces.add(Ace.createFromXml(aceElem));
        }
        return new AclProperty(aces);
    }

    public static Ace createGrantAce(Principal principal, Privilege[] privileges, boolean invert, boolean isProtected, AclResource inheritedFrom) {
        return new Ace(principal, invert, privileges, true, isProtected, inheritedFrom);
    }

    public static Ace createDenyAce(Principal principal, Privilege[] privileges, boolean invert, boolean isProtected, AclResource inheritedFrom) {
        return new Ace(principal, invert, privileges, false, isProtected, inheritedFrom);
    }

    //--------------------------------------------------------< inner class >---
    /**
     * Simple WebDAV ACE implementation
     */
    public static class Ace implements XmlSerializable, SecurityConstants {

        private static final String XML_ACE = "ace";
        private static final String XML_INVERT = "invert";
        private static final String XML_GRANT = "grant";
        private static final String XML_DENY = "deny";
        private static final String XML_PROTECTED = "protected";
        private static final String XML_INHERITED = "inherited";

        private final Principal principal;
        private final boolean invert;
        private final Privilege[] privileges;
        private final boolean grant;
        private final boolean isProtected;
        private final String inheritedHref;

        /**
         * Private constructor
         *
         * @param principal
         * @param invert
         * @param privileges
         * @param grant
         * @param isProtected
         * @param inherited
         */
        private Ace(Principal principal, boolean invert, Privilege[] privileges,
                    boolean grant, boolean isProtected, AclResource inherited) {
            this(principal, invert, privileges, grant, isProtected,
                    ((inherited != null) ? inherited.getHref() : null));
        }

        /**
         * Private constructor
         *
         * @param principal
         * @param invert
         * @param privileges
         * @param grant
         * @param isProtected
         * @param inheritedHref
         */
        private Ace(Principal principal, boolean invert, Privilege[] privileges,
                    boolean grant, boolean isProtected, String inheritedHref) {
            if (principal == null) {
                throw new IllegalArgumentException("Cannot create a new ACE with 'null' principal.");
            }
            if (privileges == null || privileges.length == 0) {
                throw new IllegalArgumentException("Cannot create a new ACE: at least a single privilege must be specified.");
            }
            this.principal = principal;
            this.invert = invert;
            this.privileges = privileges;
            this.grant = grant;
            this.isProtected = isProtected;
            this.inheritedHref = inheritedHref;
        }

        public Principal getPrincipal() {
            return principal;
        }

        public boolean isInvert() {
            return invert;
        }

        public Privilege[] getPrivileges() {
            return privileges;
        }

        public boolean isGrant() {
            return grant;
        }

        public boolean isDeny() {
            return !grant;
        }

        public boolean isProtected() {
            return isProtected;
        }

        public String getInheritedHref() {
            return inheritedHref;
        }

        /**
         * @see XmlSerializable#toXml(Document)
         */
        public Element toXml(Document document) {
            Element ace = DomUtil.createElement(document, XML_ACE, SecurityConstants.NAMESPACE);
            if (invert) {
                Element inv = DomUtil.addChildElement(ace, XML_INVERT, SecurityConstants.NAMESPACE);
                inv.appendChild(principal.toXml(document));
            } else {
                ace.appendChild(principal.toXml(document));
            }
            Element gd = DomUtil.addChildElement(ace, ((grant) ? XML_GRANT : XML_DENY), SecurityConstants.NAMESPACE);
            for (Privilege privilege : privileges) {
                gd.appendChild(privilege.toXml(document));
            }
            if (isProtected) {
                DomUtil.addChildElement(ace, XML_PROTECTED, SecurityConstants.NAMESPACE);
            }
            if (inheritedHref != null) {
                Element inh = DomUtil.addChildElement(ace, XML_INHERITED, SecurityConstants.NAMESPACE);
                inh.appendChild(DomUtil.hrefToXml(inheritedHref, document));
            }
            return ace;
        }

        private static Ace createFromXml(Element aceElement) throws DavException {
            boolean invert = DomUtil.hasChildElement(aceElement, XML_INVERT, NAMESPACE);
            Element pe;
            if (invert) {
                Element invertE = DomUtil.getChildElement(aceElement, XML_INVERT, NAMESPACE);
                pe = DomUtil.getChildElement(invertE, Principal.XML_PRINCIPAL, NAMESPACE);
            } else {
                pe = DomUtil.getChildElement(aceElement, Principal.XML_PRINCIPAL, SecurityConstants.NAMESPACE);
            }
            Principal principal = Principal.createFromXml(pe);

            boolean grant = DomUtil.hasChildElement(aceElement, Ace.XML_GRANT, SecurityConstants.NAMESPACE);
            Element gdElem;
            if (grant) {
                gdElem = DomUtil.getChildElement(aceElement, XML_GRANT, NAMESPACE);
            } else {
                gdElem = DomUtil.getChildElement(aceElement, XML_DENY, NAMESPACE);
            }
            List<Privilege> privilegeList = new ArrayList<Privilege>();
            ElementIterator privIt = DomUtil.getChildren(gdElem, Privilege.XML_PRIVILEGE, NAMESPACE);
            while (privIt.hasNext()) {
               Privilege pv = Privilege.getPrivilege(privIt.nextElement());
               privilegeList.add(pv);
            }
            Privilege[] privileges = privilegeList.toArray(new Privilege[privilegeList.size()]);

            boolean isProtected = DomUtil.hasChildElement(aceElement, XML_PROTECTED, NAMESPACE);
            String inheritedHref = null;
            if (DomUtil.hasChildElement(aceElement, XML_INHERITED, NAMESPACE)) {
                Element inhE = DomUtil.getChildElement(aceElement, XML_INHERITED, NAMESPACE);
                inheritedHref = DomUtil.getChildText(inhE, DavConstants.XML_HREF, DavConstants.NAMESPACE);
            }

            return new Ace(principal, invert, privileges, grant,  isProtected, inheritedHref);
        }
    }
}
