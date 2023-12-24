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
package org.apache.jackrabbit.webdav.io;

import java.io.OutputStream;

/**
 * <code>OutputContext</code>...
 */
public interface OutputContext {

    /**
     * Return true if the given export context can provide an output stream
     */
    public boolean hasStream();

    /**
     * Return the output stream to be used for the export or <code>null</code>
     *
     * @return
     */
    public OutputStream getOutputStream();

    /**
     * Sets the content language.
     *
     * @param contentLanguage
     */
    public void setContentLanguage(String contentLanguage);

    /**
     * Sets the length of the data.
     *
     * @param contentLength the content length
     */
    public void setContentLength(long contentLength);

    /**
     * Set the content type for the resource content
     *
     * @param contentType
     */
    public void setContentType(String contentType);

    /**
     * Sets the modification time of the resource
     *
     * @param modificationTime the modification time
     */
    public void setModificationTime(long modificationTime);

    /**
     * Sets the ETag of the resource. A successful export command
     * may set this member.
     *
     * @param etag the ETag
     */
    public void setETag(String etag);

    /**
     * Allows to set additional properties that are not covered by an extra setter
     * method.
     *
     * @param propertyName
     * @param propertyValue
     */
    public void setProperty(String propertyName, String propertyValue);
}
