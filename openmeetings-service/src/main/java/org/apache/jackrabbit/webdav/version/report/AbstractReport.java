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
package org.apache.jackrabbit.webdav.version.report;

import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestContext;
import org.apache.jackrabbit.webdav.server.WebdavRequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract <code>Report</code>.
 */
public abstract class AbstractReport implements Report {

    private static Logger log = LoggerFactory.getLogger(AbstractReport.class);

    /**
     * Normalize the resource {@code href}. For example, remove contextPath prefix if found.
     * @param href resource href
     * @return normalized resource {@code href}
     */
    protected String normalizeResourceHref(final String href) {
        if (href == null) {
            return href;
        }

        final WebdavRequestContext requestContext = WebdavRequestContextHolder.getContext();
        final WebdavRequest request = (requestContext != null) ? requestContext.getRequest() : null;

        if (request == null) {
            log.error("WebdavRequest is unavailable in the current execution context.");
            return href;
        }

        final String contextPath = request.getContextPath();

        if (!contextPath.isEmpty() && href.startsWith(contextPath)) {
            return href.substring(contextPath.length());
        }

        return href;
    }

}