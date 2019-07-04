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
package org.apache.openmeetings.cli;

import java.util.TimeZone;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.string.StringValue;

public class MysqlPatcher extends ConnectionPropertiesPatcher {
	static final String TZ_PARAM = "serverTimezone";

	@Override
	protected String getUrl(String inUrl, String host, String inPort, String inDb) {
		Url url = Url.parse(inUrl);
		url.setHost(host);
		url.setPort((inPort == null) ? 3306 : Integer.valueOf(inPort));
		url.getSegments().set(1, (inDb == null) ? DEFAULT_DB_NAME : inDb);
		PageParameters pp = new PageParametersEncoder().decodePageParameters(url);
		StringValue tz = pp.get(TZ_PARAM);
		if (tz.isEmpty()) {
			url.setQueryParameter(TZ_PARAM, TimeZone.getDefault().getID());
		}
		return url.toString(Url.StringMode.FULL);
	}
}
