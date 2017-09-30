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
package org.apache.openmeetings.screenshare.util;

import static org.quartz.impl.StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME;

import java.util.Map;
import java.util.Properties;

public class Util {
	private Util() {}

	public static Properties getQurtzProps(String name) {
		final Properties p = new Properties();
		p.put(PROP_SCHED_INSTANCE_NAME, name);
		p.put("org.quartz.threadPool.threadCount", "10");
		return p;
	}

	public static String getString(Map<String, Object> map, String key) {
		return String.valueOf(map.get(key));
	}

	public static Double getDouble(Map<String, Object> map, String key) {
		return Double.valueOf(getString(map, key));
	}

	public static int getInt(Map<String, Object> map, String key) {
		return getDouble(map, key).intValue();
	}

	public static float getFloat(Map<String, Object> map, String key) {
		return getDouble(map, key).floatValue();
	}
}
