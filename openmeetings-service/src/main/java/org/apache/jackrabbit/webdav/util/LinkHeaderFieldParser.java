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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple parser for HTTP Link header fields, as defined in RFC 5988.
 */
public class LinkHeaderFieldParser {

    /**
     * the default logger
     */
    private static Logger log = LoggerFactory.getLogger(LinkHeaderFieldParser.class);

    private final List<LinkRelation> relations;

    public LinkHeaderFieldParser(List<String> fieldValues) {
        List<LinkRelation> tmp = new ArrayList<LinkRelation>();
        if (fieldValues != null) {
            for (String value : fieldValues) {
                addFields(tmp, value);
            }
        }
        relations = Collections.unmodifiableList(tmp);
    }

    public LinkHeaderFieldParser(Enumeration<?> en) {
        if (en != null && en.hasMoreElements()) {
            List<LinkRelation> tmp = new ArrayList<LinkRelation>();

            while (en.hasMoreElements()) {
                addFields(tmp, en.nextElement().toString());
            }
            relations = Collections.unmodifiableList(tmp);
        } else {
            // optimize case of no Link headers
            relations = Collections.emptyList();
        }
    }

    public String getFirstTargetForRelation(String relationType) {

        for (LinkRelation lr : relations) {

            String relationNames = lr.getParameters().get("rel");
            if (relationNames != null) {

                // split rel value on whitespace
                for (String rn : relationNames.toLowerCase(Locale.ENGLISH)
                        .split("\\s")) {
                    if (relationType.equals(rn)) {
                        return lr.getTarget();
                    }
                }
            }
        }

        return null;
    }

    // A single header field instance can contain multiple, comma-separated
    // fields.
    private void addFields(List<LinkRelation> l, String fieldValue) {

        boolean insideAngleBrackets = false;
        boolean insideDoubleQuotes = false;

        for (int i = 0; i < fieldValue.length(); i++) {

            char c = fieldValue.charAt(i);

            if (insideAngleBrackets) {
                insideAngleBrackets = c != '>';
            } else if (insideDoubleQuotes) {
                insideDoubleQuotes = c != '"';
                if (c == '\\' && i < fieldValue.length() - 1) {
                    // skip over next character
                    c = fieldValue.charAt(++i);
                }
            } else {
                insideAngleBrackets = c == '<';
                insideDoubleQuotes = c == '"';

                if (c == ',') {
                    String v = fieldValue.substring(0, i);
                    if (v.length() > 0) {
                        try {
                            l.add(new LinkRelation(v));
                        } catch (Exception ex) {
                            log.warn("parse error in Link Header field value",
                                    ex);
                        }
                    }
                    addFields(l, fieldValue.substring(i + 1));
                    return;
                }
            }
        }

        if (fieldValue.length() > 0) {
            try {
                l.add(new LinkRelation(fieldValue));
            } catch (Exception ex) {
                log.warn("parse error in Link Header field value", ex);
            }
        }
    }

    private static class LinkRelation {

        private static Pattern P = Pattern.compile("\\s*<(.*)>\\s*(.*)");

        private String target;
        private Map<String, String> parameters;

        /**
         * Parses a single link relation, consisting of <URI> and optional
         * parameters.
         * 
         * @param field
         *            field value
         * @throws Exception
         */
        public LinkRelation(String field) throws Exception {

            // find the link target using a regexp
            Matcher m = P.matcher(field);
            if (!m.matches()) {
                throw new Exception("illegal Link header field value:" + field);
            }

            target = m.group(1);

            // pass the remainder to the generic parameter parser
            NameValuePair[] params = BasicHeaderValueParser.parseParameters(m.group(2), null);

            if (params.length == 0) {
                parameters = Collections.emptyMap();
            } else if (params.length == 1) {
                NameValuePair nvp = params[0];
                parameters = Collections.singletonMap(nvp.getName()
                        .toLowerCase(Locale.ENGLISH), nvp.getValue());
            } else {
                parameters = new HashMap<String, String>();
                for (NameValuePair p : params) {
                    if (null != parameters.put(
                            p.getName().toLowerCase(Locale.ENGLISH),
                            p.getValue())) {
                        throw new Exception("duplicate parameter + "
                                + p.getName() + " field ignored");
                    }
                }
            }
        }

        public String getTarget() {
            return target;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public String toString() {
            return target + " " + parameters;
        }
    }
}
