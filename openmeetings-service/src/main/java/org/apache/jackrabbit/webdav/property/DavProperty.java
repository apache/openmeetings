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

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * The <code>Property</code> class represents a Property of a WebDAV
 * resource. The {@link Object#hashCode()} and {@link Object#equals(Object)} methods are
 * overridden in a way, such that the name and value of the property are
 * respected. This means, a property is equal to another if the names
 * and values are equal.<br>
 * The XML representation of a <code>DavProperty</code>:
 * <pre>
 * new DavProperty("displayname", "WebDAV Directory").toXml
 * gives a element like:
 * &lt;D:displayname&gt;WebDAV Directory&lt;/D:displayname&gt;
 *
 * new DavProperty("resourcetype", new Element("collection")).toXml
 * gives a element like:
 * &lt;D:resourcetype&gt;&lt;D:collection/&gt;&lt;/D:resourcetype&gt;
 *
 * Element[] customVals = { new Element("bla", customNamespace), new Element("bli", customNamespace) };
 * new DavProperty("custom-property", customVals, customNamespace).toXml
 * gives an element like
 * &lt;Z:custom-property&gt;
 *    &lt;Z:bla/&gt;
 *    &lt;Z:bli/&gt;
 * &lt;/Z:custom-property&gt;
 * </pre>
 */
public interface DavProperty<T> extends XmlSerializable, DavConstants, PropEntry {

    /**
     * Returns the name of this property
     *
     * @return the name of this property
     */
    public DavPropertyName getName();

    /**
     * Returns the value of this property
     *
     * @return the value of this property
     */
    public T getValue();

    /**
     * Return <code>true</code> if this property should be suppressed
     * in a PROPFIND/{@link DavConstants#PROPFIND_ALL_PROP DAV:allprop} 
     * response. See RFC 4918, Section 9.1.
     *
     * @return true, if this property should be suppressed in a PROPFIND/allprop response
     */
    public boolean isInvisibleInAllprop();
}
