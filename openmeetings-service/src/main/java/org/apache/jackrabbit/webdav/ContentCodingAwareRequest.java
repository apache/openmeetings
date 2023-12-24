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

import java.util.List;

import javax.xml.namespace.QName;

public interface ContentCodingAwareRequest {

    /**
     * Element name for signaling "must be supported content coding"
     */
    public final QName PRECONDITION_SUPPORTED = new QName("http://www.day.com/jcr/webdav/1.0", "supported-content-coding", "dcr");

    /**
     * @return value suitable for Accept response field
     */
    public String getAcceptableCodings();

    /**
     * @return content codings used in request
     */
    public List<String> getRequestContentCodings();
}
