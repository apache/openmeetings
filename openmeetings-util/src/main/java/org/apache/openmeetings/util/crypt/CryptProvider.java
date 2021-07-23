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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptProvider {
	private static final Logger log = LoggerFactory.getLogger(CryptProvider.class);
	private static ICrypt crypt;

	private CryptProvider() {}

	public static synchronized ICrypt get() {
		if (crypt == null) {
			String clazz = getCryptClassName();
			try {
				log.debug("get:: configKeyCryptClassName: {}", clazz);

				crypt = clazz == null ? null : (ICrypt) Class.forName(clazz).getDeclaredConstructor().newInstance();
			} catch (Exception err) {
				log.error("[get]", err);
			}
		}
		return crypt;
	}

	public static synchronized void reset() {
		crypt = null;
	}
}
