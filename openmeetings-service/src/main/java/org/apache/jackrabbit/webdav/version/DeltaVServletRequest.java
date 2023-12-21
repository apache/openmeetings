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
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

/**
 * <code>DeltaVServletRequest</code> provides extension useful for functionality
 * related to RFC 3253.
 */
public interface DeltaVServletRequest extends DavServletRequest {

    /**
     * Returns the Label header or <code>null</code>
     *
     * @return label header or <code>null</code>
     * @see DeltaVConstants#HEADER_LABEL
     */
    public String getLabel();

    /**
     * Return the request body as <code>LabelInfo</code> object or <code>null</code>
     * if parsing the request body or the creation of the label info failed.
     *
     * @return <code>LabelInfo</code> object or <code>null</code>
     * @throws DavException in case of an invalid request body
     */
    public LabelInfo getLabelInfo() throws DavException;

    /**
     * Return the request body as <code>MergeInfo</code> object or <code>null</code>
     * if the creation failed due to invalid format.
     *
     * @return <code>MergeInfo</code> object or <code>null</code>
     * @throws DavException in case of an invalid request body
     */
    public MergeInfo getMergeInfo() throws DavException;

    /**
     * Parses the UPDATE request body a build the corresponding <code>UpdateInfo</code>
     * object. If the request body is missing or does not of the required format
     * <code>null</code> is returned.
     *
     * @return the parsed update request body or <code>null</code>
     * @throws DavException in case of an invalid request body
     */
    public UpdateInfo getUpdateInfo() throws DavException;

    /**
     * Returns the request body and the Depth header as <code>ReportInfo</code>
     * object. The default depth, if no {@link org.apache.jackrabbit.webdav.DavConstants#HEADER_DEPTH
     * Depth header}, is {@link org.apache.jackrabbit.webdav.DavConstants#DEPTH_0}.
     * If the request body could not be parsed into an {@link org.w3c.dom.Element}
     * <code>null</code> is returned.
     *
     * @return <code>ReportInfo</code> or <code>null</code>
     * @throws DavException in case of an invalid request body
     */
    public ReportInfo getReportInfo() throws DavException;

    /**
     * Returns the {@link OptionsInfo} present with the request or <code>null</code>.
     *
     * @return {@link OptionsInfo} or <code>null</code>
     * @throws DavException in case of an invalid request body
     */
    public OptionsInfo getOptionsInfo() throws DavException;
}