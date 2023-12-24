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
package org.apache.jackrabbit.webdav.property;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

/**
 * <code>DefaultDavProperty</code>...
 */
public class DefaultDavProperty<T> extends AbstractDavProperty<T> {

    private static Logger log = LoggerFactory.getLogger(DefaultDavProperty.class);

    /**
     * the value of the property
     */
    private final T value;

    /**
     * Creates a new WebDAV property with the given namespace, name and value.
     * If the property is intended to be protected the isProtected flag must
     * be set to true.
     *
     * @param name the name of the property
     * @param value the value of the property
     * @param namespace the namespace of the property
     * @param isInvisibleInAllprop A value of true, defines this property to be protected.
     * It will not be returned in a {@link org.apache.jackrabbit.webdav.DavConstants#PROPFIND_ALL_PROP DAV:allprop}
     * PROPFIND request and cannot be set/removed with a PROPPATCH request.
     */
    public DefaultDavProperty(String name, T value, Namespace namespace, boolean isInvisibleInAllprop) {
        super(DavPropertyName.create(name, namespace), isInvisibleInAllprop);
        this.value = value;
    }

    /**
     * Creates a new non-protected WebDAV property with the given namespace, name
     * and value.
     *
     * @param name the name of the property
     * @param value the value of the property
     * @param namespace the namespace of the property
     */
    public DefaultDavProperty(String name, T value, Namespace namespace) {
        this(name, value, namespace, false);
    }

    /**
     * Creates a new WebDAV property with the given <code>DavPropertyName</code>
     * and value. If the property is meant to be protected the 'isProtected'
     * flag must be set to true.
     *
     * @param name the name of the property
     * @param value the value of the property
     * @param isInvisibleInAllprop A value of true, defines this property to be protected.
     * It will not be returned in a {@link org.apache.jackrabbit.webdav.DavConstants#PROPFIND_ALL_PROP DAV:allprop}
     * PROPFIND request and cannot be set/removed with a PROPPATCH request.
     */
    public DefaultDavProperty(DavPropertyName name, T value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = value;
    }

    /**
     * Creates a new non- protected WebDAV property with the given
     * <code>DavPropertyName</code> and value.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public DefaultDavProperty(DavPropertyName name, T value) {
        this(name, value, false);
    }

    /**
     * Returns the value of this property
     *
     * @return the value of this property
     */
    public T getValue() {
        return value;
    }

    /**
     * Create a new <code>DefaultDavProperty</code> instance from the given Xml
     * element. Name and namespace of the element are building the {@link DavPropertyName},
     * while the element's content forms the property value. The following logic
     * is applied:
     * <pre>
     * - empty Element           -&gt; <code>null</code> value
     * - single Text content     -&gt; <code>String</code> value
     * - single non-Text content -&gt; Element.getContent(0) is used as value
     * - other: List obtained from Element.getContent() is used as value
     * </pre>
     *
     * @param propertyElement
     * @return
     */
    public static DefaultDavProperty<?> createFromXml(Element propertyElement) {
        if (propertyElement == null) {
            throw new IllegalArgumentException("Cannot create a new DavProperty from a 'null' element.");
        }
        DavPropertyName name = DavPropertyName.createFromXml(propertyElement);
        DefaultDavProperty<?> prop;
        
        if (!DomUtil.hasContent(propertyElement)) {
            prop = new DefaultDavProperty<String>(name, null, false);
        }  else {
            List<Node> c = DomUtil.getContent(propertyElement);
            if (c.size() == 1) {
                Node n = c.get(0);
                if (n instanceof Element) {
                    prop = new DefaultDavProperty<Element>(name, (Element) n, false);
                } else {
                    prop = new DefaultDavProperty<String>(name, n.getNodeValue(), false);
                }
            } else /* size > 1 */ {
                prop = new DefaultDavProperty<List<Node>>(name, c, false);
            }
        }
        return prop;
    }
}