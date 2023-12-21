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

import java.io.InputStream;

/**
 * <code>InputContext</code>...
 */
public interface InputContext {

    /**
     * Return true, if there are any data to be imported (and not only properties)
     *
     * @return
     */
    public boolean hasStream();

    /**
     * Returns the input stream of the resource to import.
     *
     * @return the input stream.
     */
    public InputStream getInputStream();

    /**
     * Returns the modification time of the resource or the current time if
     * the modification time has not been set.
     *
     * @return the modification time.
     */
    public long getModificationTime();

    /**
     * Returns the content language or <code>null</code>
     *
     * @return contentLanguage
     */
    public String getContentLanguage();

    /**
     * Returns the length of the data or -1 if the contentlength could not be
     * determined.
     *
     * @return the content length
     */
    public long getContentLength();

    /**
     * Return the content type or <code>null</code>
     *
     * @return
     */
    public String getContentType();

    /**
     * Returns the value of the given property or <code>null</code> if this property does
     * not exist.
     *
     * @param propertyName
     * @return String property value or <code>null</code>
     */
    public String getProperty(String propertyName);
}
