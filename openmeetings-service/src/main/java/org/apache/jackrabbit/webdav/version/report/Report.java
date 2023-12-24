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

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * The <code>Report</code> interface defines METHODS needed in order to respond
 * to a REPORT request. The REPORT method is a required feature to all
 * DeltaV resources.
 *
 * @see DeltaVResource#getReport(ReportInfo)
 */
public interface Report extends XmlSerializable {

    /**
     * Returns the registered type of this report.
     *
     * @return the type of this report.
     */
    public ReportType getType();

    /**
     * Returns true if this <code>Report</code> will send a <code>MultiStatus</code>
     * response.<br>
     * Please note that RFC 3253 that the the response must be a 207 Multi-Status,
     * if a Depth request header is present.
     *
     * @return
     */
    public boolean isMultiStatusReport();

    /**
     * Set the <code>DeltaVResource</code> for which this report was requested
     * and the <code>ReportInfo</code> as specified by the REPORT request body,
     * that defines the details for this report.<br>
     * Please note that this methods should perform basic validation checks
     * in order to prevent exceptional situations during the xml serialization.
     *
     * @param resource
     * @param info
     * @throws DavException
     */
    public void init(DavResource resource, ReportInfo info) throws DavException;
}