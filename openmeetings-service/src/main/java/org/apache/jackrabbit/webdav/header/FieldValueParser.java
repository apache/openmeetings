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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldValueParser {

    /**
     * Tokenize lists of token and quoted-url
     * @param list field value
     */
    public static List<String> tokenizeList(String list) {

        String[] split = list.split(",");
        if (split.length == 1) {
            return Collections.singletonList(split[0].trim());
        } else {
            List<String> result = new ArrayList<String>();
            String inCodedUrl = null;
            for (String t : split) {
                String trimmed = t.trim();
                // handle quoted-url containing ","
                if (trimmed.startsWith("<") && !trimmed.endsWith(">")) {
                    inCodedUrl = trimmed + ",";
                } else if (inCodedUrl != null && trimmed.endsWith(">")) {
                    inCodedUrl += trimmed;
                    result.add(inCodedUrl);
                    inCodedUrl = null;
                } else {
                    if (trimmed.length() != 0) {
                        result.add(trimmed);
                    }
                }
            }
            return Collections.unmodifiableList(result);
        }
    }
}
