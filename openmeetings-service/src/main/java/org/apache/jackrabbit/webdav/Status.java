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

import java.util.Locale;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>Status</code> encapsulating the 'status' present in multistatus
 * responses.
 */
public class Status implements DavConstants, XmlSerializable{

    private static Logger log = LoggerFactory.getLogger(Status.class);

    private final String version;
    private final int code;
    private final String phrase;

    public Status(int code) {
        version = "HTTP/1.1";
        this.code = code;
        phrase = DavException.getStatusPhrase(code);
    }

    public Status(String version, int code, String phrase) {
        this.version = version;
        this.code = code;
        this.phrase = phrase;
    }

    public int getStatusCode() {
        return code;
    }

    /**
     * @see XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        String statusLine = version + " " + code + " " + phrase;
        Element e = DomUtil.createElement(document, XML_STATUS, NAMESPACE);
        DomUtil.setText(e, statusLine);
        return e;
    }

    /**
     * Parse the given status line and return a new <code>Status</code> object.
     *
     * @param statusLine
     * @return a new <code>Status</code>
     */
    public static Status parse(String statusLine) {
        if (statusLine == null) {
            throw new IllegalArgumentException("Unable to parse status line from null xml element.");
        }
        Status status;

        // code copied from org.apache.commons.httpclient.StatusLine
        int length = statusLine.length();
        int at = 0;
        int start = 0;
        try {
            while (Character.isWhitespace(statusLine.charAt(at))) {
                ++at;
                ++start;
            }
            if (!"HTTP".equals(statusLine.substring(at, at += 4))) {
                log.warn("Status-Line '" + statusLine + "' does not start with HTTP");
            }
            //handle the HTTP-Version
            at = statusLine.indexOf(' ', at);
            if (at <= 0) {
                log.warn("Unable to parse HTTP-Version from the status line: '" + statusLine + "'");
            }
            String version = (statusLine.substring(start, at)).toUpperCase(Locale.ROOT);
            //advance through spaces
            while (statusLine.charAt(at) == ' ') {
                at++;
            }
            //handle the Status-Code
            int code;
            int to = statusLine.indexOf(' ', at);
            if (to < 0) {
                to = length;
            }
            try {
                code = Integer.parseInt(statusLine.substring(at, to));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to parse status code from status line: '" + statusLine + "'");
            }
            //handle the Reason-Phrase
            String phrase = "";
            at = to + 1;
            if (at < length) {
                phrase = statusLine.substring(at).trim();
            }

            status = new Status(version, code, phrase);

        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Status-Line '" + statusLine + "' is not valid");
        }
        return status;
    }
}