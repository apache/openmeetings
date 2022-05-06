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
package org.apache.openmeetings.webservice.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getRestAllowOrigin;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.wicket.util.string.Strings;

public class AllowOriginProvider extends AbstractOutDatabindingInterceptor {
	public AllowOriginProvider() {
		super(Phase.MARSHAL);
	}

	@Override
	public void handleMessage(Message outMessage) {
		final String allowOrigin = getRestAllowOrigin();
		if (!Strings.isEmpty(allowOrigin)) {
			@SuppressWarnings("unchecked")
			Map<String, List<String>> headers =  (Map<String, List<String>>)outMessage.computeIfAbsent(Message.PROTOCOL_HEADERS, key -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
			headers.put("Access-Control-Allow-Origin", List.of(allowOrigin));
		}
	}
}
