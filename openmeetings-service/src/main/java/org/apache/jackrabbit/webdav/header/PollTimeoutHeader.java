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
package org.apache.jackrabbit.webdav.header;

import org.apache.jackrabbit.webdav.observation.ObservationConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <code>PollTimeoutHeader</code> implements a timeout header for subscription
 * polling.
 */
public class PollTimeoutHeader extends TimeoutHeader {

    public PollTimeoutHeader(long timeout) {
        super(timeout);
    }

    @Override
    public String getHeaderName() {
        return ObservationConstants.HEADER_POLL_TIMEOUT;
    }

    /**
     * Parses the request timeout header and converts it into a new
     * <code>PollTimeoutHeader</code> object.<br>The default value is used as
     * fallback if the String is not parseable.
     *
     * @param request
     * @param defaultValue
     * @return a new PollTimeoutHeader object.
     */
    public static PollTimeoutHeader parseHeader(HttpServletRequest request, long defaultValue) {
        String timeoutStr = request.getHeader(ObservationConstants.HEADER_POLL_TIMEOUT);
        long timeout = parse(timeoutStr, defaultValue);
        return new PollTimeoutHeader(timeout);
    }
}
