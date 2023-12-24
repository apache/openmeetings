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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <code>DavMethods</code> defines constants for the WebDAV METHODS.
 */
public final class DavMethods {

    /**
     * Avoid instantiation
     */
    private DavMethods() {}

    /**
     * A map of WebDAV METHODS
     */
    private static Map<String, Integer> methodMap = new HashMap<String, Integer>();

    /**
     * An array of method codes that are affected by a Label header
     * @see org.apache.jackrabbit.webdav.version.DeltaVConstants#HEADER_LABEL
     */
    private static int[] labelMethods;

    /**
     * An array of method codes defined by RFC 3253 (deltaV)
     */
    private static int[] deltaVMethods;

    /**
     * The webdav OPTIONS method and public constant
     */
    public static final int DAV_OPTIONS = 1;
    public static final String METHOD_OPTIONS = "OPTIONS";

    /**
     * The webdav GET method and public constant
     */
    public static final int DAV_GET = DAV_OPTIONS + 1;
    public static final String METHOD_GET = "GET";

    /**
     * The webdav HEAD method and public constant
     */
    public static final int DAV_HEAD = DAV_GET + 1;
    public static final String METHOD_HEAD = "HEAD";


    /**
     * The webdav POST method and public constant
     */
    public static final int DAV_POST = DAV_HEAD + 1;
    public static final String METHOD_POST = "POST";


    /** The webdav DELETE method and public constant */
    public static final int DAV_DELETE = DAV_POST + 1;
    public static final String METHOD_DELETE = "DELETE";


    /** The webdav PUT method and public constant */
    public static final int DAV_PUT = DAV_DELETE + 1;
    public static final String METHOD_PUT = "PUT";


    /**
     * The webdav PROPFIND method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>.
     */
    public static final int DAV_PROPFIND = DAV_PUT + 1;
    public static final String METHOD_PROPFIND = "PROPFIND";


    /**
     * The webdav PROPPATCH method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_PROPPATCH = DAV_PROPFIND + 1;
    public static final String METHOD_PROPPATCH = "PROPPATCH";


    /**
     * The webdav MKCOL (make collection) method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_MKCOL = DAV_PROPPATCH + 1;
    public static final String METHOD_MKCOL = "MKCOL";


    /**
     * The webdav COPY method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_COPY = DAV_MKCOL + 1;
    public static final String METHOD_COPY = "COPY";


    /**
     * The webdav MOVE method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_MOVE = DAV_COPY + 1;
    public static final String METHOD_MOVE = "MOVE";


    /**
     * The webdav LOCK method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_LOCK = DAV_MOVE + 1;
    public static final String METHOD_LOCK = "LOCK";


    /**
     * The webdav UNLOCK method and public constant as defined by
     * <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>
     */
    public static final int DAV_UNLOCK = DAV_LOCK + 1;
    public static final String METHOD_UNLOCK = "UNLOCK";


    /**
     * The webdav ORDERPATCH method and public constant
     * defined by <a href="http://www.ietf.org/rfc/rfc3648.txt">RFC 3648</a>.
     */
    public static final int DAV_ORDERPATCH = DAV_UNLOCK + 1;
    public static final String METHOD_ORDERPATCH = "ORDERPATCH";


    /**
     * The webdav SUBSCRIBE method and public constant.<br>
     * NOTE: This method is not defined by any of the Webdav RFCs
     */
    public static final int DAV_SUBSCRIBE = DAV_ORDERPATCH + 1;
    public static final String METHOD_SUBSCRIBE = "SUBSCRIBE";


    /**
     * The webdav UNSUBSCRIBE method and public constant<br>
     * NOTE: This method is not defined by any of the Webdav RFCs
     */
    public static final int DAV_UNSUBSCRIBE = DAV_SUBSCRIBE + 1;
    public static final String METHOD_UNSUBSCRIBE = "UNSUBSCRIBE";


    /**
     * The webdav POLL method and public constant<br>
     * NOTE: This method is not defined by any of the Webdav RFCs
     */
    public static final int DAV_POLL = DAV_UNSUBSCRIBE + 1;
    public static final String METHOD_POLL = "POLL";


    /**
     * The webdav SEARCH method and public constant as defined by the
     * Webdav Search internet draft.
     */
    public static final int DAV_SEARCH = DAV_POLL + 1;
    public static final String METHOD_SEARCH = "SEARCH";


    /**
     * The webdav REPORT method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_REPORT = DAV_SEARCH + 1;
    public static final String METHOD_REPORT = "REPORT";


    /**
     * The webdav VERSION-CONTROL method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_VERSION_CONTROL = DAV_REPORT + 1;
    public static final String METHOD_VERSION_CONTROL = "VERSION-CONTROL";

    /**
     * The webdav CHECKIN method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_CHECKIN = DAV_VERSION_CONTROL + 1;
    public static final String METHOD_CHECKIN = "CHECKIN";

    /**
     * The webdav CHECKOUT method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_CHECKOUT = DAV_CHECKIN + 1;
    public static final String METHOD_CHECKOUT = "CHECKOUT";

    /**
     * The webdav UNCHECKOUT method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_UNCHECKOUT = DAV_CHECKOUT + 1;
    public static final String METHOD_UNCHECKOUT = "UNCHECKOUT";

    /**
     * The webdav LABEL method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_LABEL = DAV_UNCHECKOUT + 1;
    public static final String METHOD_LABEL = "LABEL";

    /**
     * The webdav MERGE method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_MERGE = DAV_LABEL + 1;
    public static final String METHOD_MERGE = "MERGE";

    /**
     * The webdav UPDATE method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_UPDATE = DAV_MERGE + 1;
    public static final String METHOD_UPDATE = "UPDATE";

    /**
     * The webdav MKWORKSPACE method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_MKWORKSPACE = DAV_UPDATE + 1;
    public static final String METHOD_MKWORKSPACE = "MKWORKSPACE";

    /**
     * The webdav BASELINE-CONTROL method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_BASELINE_CONTROL = DAV_MKWORKSPACE + 1;
    public static final String METHOD_BASELINE_CONTROL = "BASELINE-CONTROL";

    /**
     * The webdav MKACTIVITY method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     */
    public static final int DAV_MKACTIVITY = DAV_BASELINE_CONTROL + 1;
    public static final String METHOD_MKACTIVITY = "MKACTIVITY";

    /**
     * The webdav ACL method and public constant defined by
     * <a href="http://www.ietf.org/rfc/rfc3744.txt">RFC 3744</a>
     */
    public static final int DAV_ACL = DAV_MKACTIVITY + 1;
    public static final String METHOD_ACL = "ACL";

    /**
     * The webdav REBIND method and public constant defined by
     * the BIND specification
     */
    public static final int DAV_REBIND = DAV_ACL + 1;
    public static final String METHOD_REBIND = "REBIND";

    /**
     * The webdav UNBIND method and public constant defined by
     * the BIND specification
     */
    public static final int DAV_UNBIND = DAV_REBIND + 1;
    public static final String METHOD_UNBIND = "UNBIND";

    /**
     * The webdav BIND method and public constant defined by
     * the BIND specification
     */
    public static final int DAV_BIND = DAV_UNBIND + 1;
    public static final String METHOD_BIND = "BIND";

    /**
     * Returns webdav method type code, error result &lt;= 0
     * Valid type codes &gt; 0
     */
    public static int getMethodCode(String method) {
        Integer code = methodMap.get(method.toUpperCase(Locale.ROOT));
        if (code != null) {
            return code;
        }
        return 0;
    }

    /**
     * Static initializer for methodTable map
     */
    private static void addMethodCode(String method, int code) {
        methodMap.put(method, code);
    }

    /**
     *  Webdav Method table
     */
    static {
        addMethodCode(METHOD_OPTIONS, DAV_OPTIONS);
        addMethodCode(METHOD_GET, DAV_GET);
        addMethodCode(METHOD_HEAD, DAV_HEAD);
        addMethodCode(METHOD_POST, DAV_POST);
        addMethodCode(METHOD_PUT, DAV_PUT);
        addMethodCode(METHOD_DELETE, DAV_DELETE);
        addMethodCode(METHOD_PROPFIND, DAV_PROPFIND);
        addMethodCode(METHOD_PROPPATCH, DAV_PROPPATCH);
        addMethodCode(METHOD_MKCOL, DAV_MKCOL);
        addMethodCode(METHOD_COPY, DAV_COPY);
        addMethodCode(METHOD_MOVE, DAV_MOVE);
        addMethodCode(METHOD_LOCK, DAV_LOCK);
        addMethodCode(METHOD_UNLOCK, DAV_UNLOCK);
        addMethodCode(METHOD_ORDERPATCH, DAV_ORDERPATCH);
        addMethodCode(METHOD_SUBSCRIBE, DAV_SUBSCRIBE);
        addMethodCode(METHOD_UNSUBSCRIBE, DAV_UNSUBSCRIBE);
        addMethodCode(METHOD_POLL, DAV_POLL);
        addMethodCode(METHOD_SEARCH, DAV_SEARCH);
        addMethodCode(METHOD_REPORT, DAV_REPORT);
        addMethodCode(METHOD_VERSION_CONTROL, DAV_VERSION_CONTROL);
        addMethodCode(METHOD_CHECKIN, DAV_CHECKIN);
        addMethodCode(METHOD_CHECKOUT, DAV_CHECKOUT);
        addMethodCode(METHOD_UNCHECKOUT, DAV_UNCHECKOUT);
        addMethodCode(METHOD_LABEL, DAV_LABEL);
        addMethodCode(METHOD_MERGE, DAV_MERGE);
        addMethodCode(METHOD_UPDATE, DAV_UPDATE);
        addMethodCode(METHOD_MKWORKSPACE, DAV_MKWORKSPACE);
        addMethodCode(METHOD_BASELINE_CONTROL, DAV_BASELINE_CONTROL);
        addMethodCode(METHOD_MKACTIVITY, DAV_MKACTIVITY);
        addMethodCode(METHOD_ACL, DAV_ACL);
        addMethodCode(METHOD_REBIND, DAV_REBIND);
        addMethodCode(METHOD_UNBIND, DAV_UNBIND);
        addMethodCode(METHOD_BIND, DAV_BIND);

        labelMethods = new int[] { DAV_GET, DAV_HEAD, DAV_OPTIONS, DAV_PROPFIND,
                                   DAV_LABEL, DAV_COPY };

        deltaVMethods = new int[] { DAV_REPORT, DAV_VERSION_CONTROL, DAV_CHECKIN,
                                    DAV_CHECKOUT, DAV_UNCHECKOUT, DAV_LABEL,
                                    DAV_MERGE, DAV_UPDATE, DAV_MKWORKSPACE,
                                    DAV_BASELINE_CONTROL, DAV_MKACTIVITY };
    }

    /**
     * Returns <code>true</code> if the request is to create a resource.
     * True for <code>PUT</code>, <code>POST</code>, <code>MKCOL</code>
     * and <code>MKWORKSPACE</code> requests.
     *
     * @return true if request method is to create (or replace) a resource
     */
    public static boolean isCreateRequest(DavServletRequest request) {
        int methodCode = getMethodCode(request.getMethod());
        return ( methodCode == DAV_PUT ||
                 methodCode == DAV_POST ||
                 methodCode == DAV_MKCOL ||
                 methodCode == DAV_MKWORKSPACE);
    }

    /**
     * Returns <code>true</code> if the request is to create a collection resource.
     * True for <code>MKCOL</code> and <code>MKWORKSPACE</code> requests.
     *
     * @return true if request method is to create a new collection resource
     */
    public static boolean isCreateCollectionRequest(DavServletRequest request) {
        int methodCode = getMethodCode(request.getMethod());
        return (methodCode == DAV_MKCOL || methodCode == DAV_MKWORKSPACE);
    }

    /**
     * Returns true, if the specified method is affected by a Label header
     *
     * @param request
     * @return
     */
    public static boolean isMethodAffectedByLabel(DavServletRequest request) {
        int code = getMethodCode(request.getMethod());
        for (int labelMethod : labelMethods) {
            if (code == labelMethod) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if the specified method is defined by RFC 3253
     *
     * @param request
     * @return true, if the specified method is defined by RFC 3253
     */
    public static boolean isDeltaVMethod(DavServletRequest request) {
        int code = getMethodCode(request.getMethod());
        for (int deltaVMethod : deltaVMethods) {
            if (code == deltaVMethod) {
                return true;
            }
        }
        return false;
    }
}
