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
package org.apache.openmeetings.util.crypt;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptProvider {
	private static final Logger log = LoggerFactory.getLogger(CryptProvider.class);
	private static ICrypt crypt;

	private CryptProvider() {}

	public static synchronized ICrypt get() {
		if (crypt == null) {
			crypt = fromClass(getCryptClassName());
		}
		return crypt;
	}

	public static synchronized void reset() {
		crypt = null;
	}

	public static ICrypt fromClass(String className) {
		ICrypt inst = null;
		try {
			Class<?> clazz = Class.forName(className);
			if (ICrypt.class.isAssignableFrom(clazz)) {
				Constructor<?> constr = clazz.getDeclaredConstructor();
				constr.setAccessible(true);
				inst = (ICrypt)constr.newInstance();
			}
		} catch (Exception e) {
			// no-op
		}
		if (inst == null) {
			log.error("Error while attempting to get ICrypt from '" + className + "' as '" + CONFIG_CRYPT + "'");
		}
		return inst;
	}
}
