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
package org.apache.jackrabbit.webdav.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <code>HttpDateFormat</code>...
 */
public class HttpDateFormat extends SimpleDateFormat {

    private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    /**
     * Pattern for the modification date as defined by RFC 1123
     */
    public static final String MODIFICATION_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Simple date format pattern for the creation date ISO representation (partial).
     */
    public static final String CREATION_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public HttpDateFormat(String pattern) {
        super(pattern, Locale.ENGLISH);
        super.setTimeZone(GMT_TIMEZONE);
    }

    /**
     * Creates a new HttpDateFormat using the
     * {@link #MODIFICATION_DATE_PATTERN modifcation date pattern}.
     *
     * @return a new HttpDateFormat.
     */
    public static HttpDateFormat modificationDateFormat() {
        return new HttpDateFormat(MODIFICATION_DATE_PATTERN);
    }

    /**
     * Creates a new HttpDateFormat using the
     * {@link #CREATION_DATE_PATTERN creation date pattern}.
     *
     * @return a new HttpDateFormat.
     */
    public static HttpDateFormat creationDateFormat() {
        return new HttpDateFormat(CREATION_DATE_PATTERN);
    }
}