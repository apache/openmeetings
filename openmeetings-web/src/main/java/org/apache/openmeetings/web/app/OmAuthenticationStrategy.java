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
package org.apache.openmeetings.web.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmAuthenticationStrategy extends DefaultAuthenticationStrategy {
	private static final Logger log = LoggerFactory.getLogger(OmAuthenticationStrategy.class);
	private static final String COOKIE_KEY = "LoggedIn";

	public OmAuthenticationStrategy(String encryptionKey, String salt) {
		super(COOKIE_KEY, defaultCrypt(encryptionKey, salt));
	}

	/**
	 * @see DefaultAuthenticationStrategy#decode(String value)
	 * Additionally decodes stored login type and domain
	 */
	@Override
	protected String[] decode(String value) {
		if (!Strings.isEmpty(value)) {
			String username = null;
			String password = null;
			String type = null;
			String domainId = null;

			String[] values = value.split(VALUE_SEPARATOR);
			if (values.length > 0 && !Strings.isEmpty(values[0])) {
				username = values[0];
			}
			if (values.length > 1 && !Strings.isEmpty(values[1])) {
				password = values[1];
			}
			if (values.length > 2 && !Strings.isEmpty(values[2])) {
				type = values[2];
			}
			if (values.length > 3 && !Strings.isEmpty(values[3])) {
				domainId = values[3];
			}

			return new String[] { username, password, type, domainId };
		}
		return new String[] {};
	}

	public void save(final String username, final String password, final Type type, final Long domainId) {
		if (type != Type.OAUTH) {
			super.save(username, password, type.name(), String.valueOf(domainId));
		}
	}

	private static ICrypt defaultCrypt(String encryptionKey, String saltStr) {
		SunJceCrypt crypt = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);)
		{
			ps.append(saltStr).append("om_secret");
			byte[] salt = Arrays.copyOfRange(baos.toByteArray(), 0, 8);

			crypt = new SunJceCrypt(salt, 1000);
			crypt.setKey(encryptionKey);
		} catch (IOException e) {
			log.error("Enxpected error while creating crypt", e);
		}
		return crypt;
	}
}
