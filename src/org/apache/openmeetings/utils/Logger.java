/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.utils;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;

public class Logger {
    private org.slf4j.Logger log;

    public Logger() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
        log = Red5LoggerFactory.getLogger(ste.getClass(),
        		OpenmeetingsVariables.webAppRootKey);
    }
    
    private String getMethodMessage() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        return "In the method " + ste.getMethodName();
    }

    public void debug(String s, Object o) {
        log.debug(s, o);
    }

    public void debug(Object o) {
        log.debug(getMethodMessage(), o);
    }

    public void error(Object o) {
        log.error(getMethodMessage(), o);
    }
}
