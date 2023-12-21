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

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Properties;

/**
 * <code>DavException</code> extends the {@link Exception} class in order
 * to simplify handling of exceptional situations occurring during processing
 * of WebDAV requests and provides possibility to retrieve an Xml representation
 * of the error.
 */
public class DavException extends Exception implements XmlSerializable {

    private static Logger log = LoggerFactory.getLogger(DavException.class);
    private static Properties statusPhrases = new Properties();
    static {
        try {
            statusPhrases.load(DavException.class.getResourceAsStream("statuscode.properties"));
        } catch (IOException e) {
            log.error("Failed to load status properties: " + e.getMessage());
        }
    }

    public static final String XML_ERROR = "error";

    private int errorCode = DavServletResponse.SC_INTERNAL_SERVER_ERROR;
    private Element errorCondition;

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     * {@link DavServletResponse}.
     * @param message Human readable error message.
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode, String message) {
        this(errorCode, message, null, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     * {@link DavServletResponse}.
     * @param cause Cause of this DavException
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode, Throwable cause) {
        this(errorCode, null, cause, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     * {@link DavServletResponse}.
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode) {
        this(errorCode, statusPhrases.getProperty(String.valueOf(errorCode)), null, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     * {@link DavServletResponse}.
     * @param message Human readable error message.
     * @param cause Cause of this <code>DavException</code>.
     * @param errorCondition Xml element providing detailed information about
     * the error. If the condition is not <code>null</code>, {@link #toXml(Document)}
     */
    public DavException(int errorCode, String message, Throwable cause, Element errorCondition) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorCondition = errorCondition;
        log.debug("DavException: (" + errorCode + ") " + message);
    }

    /**
     * Return the error code attached to this <code>DavException</code>.
     *
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Return the status phrase corresponding to the error code attached to
     * this <code>DavException</code>.
     *
     * @return status phrase corresponding to the error code.
     * @see #getErrorCode()
     */
    public String getStatusPhrase() {
        return getStatusPhrase(errorCode);
    }

    /**
     * Returns the status phrase for the given error code.
     *
     * @param errorCode
     * @return status phrase corresponding to the given error code.
     */
    public static String getStatusPhrase(int errorCode) {
        return statusPhrases.getProperty(errorCode + "");
    }

    /**
     * @return true if a error condition has been specified, false otherwise.
     */
    public boolean hasErrorCondition() {
        return errorCondition != null;
    }

    /**
     * Return the error condition attached to this <code>DavException</code>.
     *
     * @return errorCondition
     */
    public Element getErrorCondition() {
        return errorCondition;
    }

    /**
     * Returns a DAV:error element containing the error condition or
     * <code>null</code> if no specific condition is available. See
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     * Section 1.6 "Method Preconditions and Postconditions" for additional
     * information.
     *
     * @param document
     * @return A DAV:error element indicating the error cause or <code>null</code>.
     * @see org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        if (hasErrorCondition()) {
            Element error;
            if (DomUtil.matches(errorCondition, XML_ERROR, DavConstants.NAMESPACE)) {
                error = (Element) document.importNode(errorCondition, true);
            } else {
                error = DomUtil.createElement(document, XML_ERROR, DavConstants.NAMESPACE);
                error.appendChild(document.importNode(errorCondition, true));
            }
            return error;
        } else {
            return null;
        }
    }
}
