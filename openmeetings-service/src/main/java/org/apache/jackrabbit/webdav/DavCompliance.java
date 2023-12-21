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

/**
 * <code>DavCompliance</code> defines constants for the various compliance
 * classes defined RFC 2518, RFC 4918 and it's extensions.
 */
public final class DavCompliance {

    /**
     * Avoid instantiation
     */
    private DavCompliance() {}

    // RFC 2518
    public static final String _1_ = "1";
    public static final String _2_ = "2";

    // RFC 4918
    public static final String _3_ = "3";

    // RFC 3253
    public static final String ACTIVITY = "activity";
    public static final String BASELINE = "baseline";
    public static final String CHECKOUT_IN_PLACE = "checkout-in-place";
    public static final String LABEL = "label";
    public static final String MERGE = "merge";
    public static final String UPDATE = "update";
    public static final String VERSION_CONTROL = "version-control";
    public static final String VERSION_CONTROLLED_COLLECTION = "version-controlled-collection";
    public static final String VERSION_HISTORY = "version-history";
    public static final String WORKING_RESOURCE = "working-resource";
    public static final String WORKSPACE = "workspace";

    // RFC 3648
    public static final String ORDERED_COLLECTIONS = "ordered-collections";

    // RFC 3744
    public static final String ACCESS_CONTROL = "access-control";

    // RFC 5842
    public static final String BIND = "bind";
    
    // no RFC
    public static final String OBSERVATION = "observation";

    public static String concatComplianceClasses(String[] complianceClasses) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < complianceClasses.length; i++) {
            if (i > 0) {
                b.append(",");
            }
            b.append(complianceClasses[i]);
        }
        return b.toString();
    }
}