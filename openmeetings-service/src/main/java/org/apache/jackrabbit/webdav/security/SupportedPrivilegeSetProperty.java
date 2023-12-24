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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

/**
 * <code>SupportedPrivilegeSetProperty</code> defines the
 * {@link SecurityConstants#SUPPORTED_PRIVILEGE_SET} property, used to identify
 * the privileges defined for the resource. RFC 3744 defines the the following
 * structure for this property:
 * <pre>
 * &lt;!ELEMENT supported-privilege-set (supported-privilege*)&gt;
 * &lt;!ELEMENT supported-privilege (privilege, abstract?, description, supported-privilege*)&gt;
 * &lt;!ELEMENT privilege ANY&gt;
 * &lt;!ELEMENT abstract EMPTY&gt;
 * &lt;!ELEMENT description #PCDATA&gt;
 * </pre>
 *
 * @see SupportedPrivilege
 * @see Privilege
 */
public class SupportedPrivilegeSetProperty extends AbstractDavProperty<List<SupportedPrivilege>> {

    private final SupportedPrivilege[] supportedPrivileges;

    /**
     * Create a new <code>SupportedPrivilegeSetProperty</code>.
     *
     * @param supportedPrivileges
     */
    public SupportedPrivilegeSetProperty(SupportedPrivilege[] supportedPrivileges) {
        super(SecurityConstants.SUPPORTED_PRIVILEGE_SET, true);
        this.supportedPrivileges = supportedPrivileges;
    }

    public SupportedPrivilegeSetProperty(DavProperty<?> p) throws DavException {
        super(SecurityConstants.SUPPORTED_PRIVILEGE_SET, true);
        if (!SecurityConstants.SUPPORTED_PRIVILEGE_SET.equals(getName())) {
            throw new DavException(DavServletResponse.SC_BAD_REQUEST, "DAV:supported-privilege-set expected.");
        }

        List<SupportedPrivilege> supportedPrivs = new ArrayList<SupportedPrivilege>();
        
        for (Object obj : Collections.singletonList(p.getValue())) {
            if (obj instanceof Element) {
                supportedPrivs.add(SupportedPrivilege.getSupportedPrivilege((Element) obj));
            } else if (obj instanceof Collection) {
                for (Object entry : ((Collection<?>) obj)) {
                    if (entry instanceof Element) {
                        supportedPrivs.add(SupportedPrivilege.getSupportedPrivilege((Element) entry));
                    }
                }
            }
        }
        supportedPrivileges = supportedPrivs.toArray(new SupportedPrivilege[supportedPrivs.size()]);
    }

    /**
     * @return List of {@link SupportedPrivilege}s.
     */
    public List<SupportedPrivilege> getValue() {
        List<SupportedPrivilege> l;
        if (supportedPrivileges == null) {
            l = Collections.emptyList();
        } else {
            l = Arrays.asList(supportedPrivileges);
        }
        return l;
    }
}