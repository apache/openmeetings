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

import org.apache.jackrabbit.webdav.observation.ObservationDavServletRequest;
import org.apache.jackrabbit.webdav.ordering.OrderingDavServletRequest;
import org.apache.jackrabbit.webdav.transaction.TransactionDavServletRequest;
import org.apache.jackrabbit.webdav.version.DeltaVServletRequest;
import org.apache.jackrabbit.webdav.bind.BindServletRequest;

/**
 * The empty <code>WebdavRequest</code> interface collects the functionality
 * defined by {@link org.apache.jackrabbit.webdav.DavServletRequest} encapsulating
 * the core Webdav specification (RFC 2518) as well as the various extensions
 * used for observation and transaction support, ordering of collections, search
 * and versioning.
 */
public interface WebdavRequest extends DavServletRequest,
        ObservationDavServletRequest, OrderingDavServletRequest,
        TransactionDavServletRequest, DeltaVServletRequest,
        BindServletRequest {
}
