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
package org.openmeetings.app.backup;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;

public abstract class OmConverter<T> implements Converter<T> {
	static long getlongValue(InputNode node) throws Exception {
		return getlongValue(node.getValue());
	}

	static long getlongValue(String value) {
		return getlongValue(value, 0);
	}
	
	static long getlongValue(String value, long def) {
		long result = def;
		try {
			result = Long.valueOf(value).longValue();
		} catch (Exception e) {
			//no op
		}
		return result;
	}

	static int getintValue(String value, int def) {
		int result = def;
		try {
			result = Integer.valueOf(value).intValue();
		} catch (Exception e) {
			//no op
		}
		return result;
	}
}
