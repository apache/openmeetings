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

import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.util.EncodeUtil;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>LabelHeader</code>...
 */
public class LabelHeader implements Header {

    private static Logger log = LoggerFactory.getLogger(LabelHeader.class);

    private final String label;

    public LabelHeader(String label) {
        if (label == null) {
            throw new IllegalArgumentException("null is not a valid label.");
        }
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getHeaderName() {
        return DeltaVConstants.HEADER_LABEL;
    }

    public String getHeaderValue() {
        return EncodeUtil.escape(label);
    }

    public static LabelHeader parse(WebdavRequest request) {
        String hv = request.getHeader(DeltaVConstants.HEADER_LABEL);
        if (hv == null) {
            return null;
        } else {
            return new LabelHeader(EncodeUtil.unescape(hv));
        }
    }
}