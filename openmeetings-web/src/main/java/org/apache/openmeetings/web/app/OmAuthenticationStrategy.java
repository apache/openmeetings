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

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.util.crypt.AbstractCrypt;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.string.Strings;

public class OmAuthenticationStrategy extends DefaultAuthenticationStrategy {
	private static final String COOKIE_KEY = "LoggedIn";
	private static final int SALT_LENGTH = 20;
	private static final int IV_LENGTH = 12;
	private static final int AUTH_TAG = 128;

	public OmAuthenticationStrategy(String encryptionKey) {
		super(COOKIE_KEY, defaultCrypt(encryptionKey));
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

	private static ICrypt defaultCrypt(String encKey) {
		return new AbstractCrypt() {
			@Override
			protected byte[] crypt(byte[] input, int mode) throws GeneralSecurityException {
				final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA256", "BC");
				final SecureRandom rnd = Application.get().getSecuritySettings().getRandomSupplier().getRandom();
				if (mode == Cipher.ENCRYPT_MODE) {
					byte[] salt = new byte[SALT_LENGTH];
					rnd.nextBytes(salt);
					KeySpec spec = new PBEKeySpec(encKey.toCharArray(), salt, 65536, 256);
					SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
					byte[] iv = new byte[IV_LENGTH];
					rnd.nextBytes(iv);
					Cipher enc = Cipher.getInstance("AES/GCM/NoPadding", "BC");
					enc.init(mode, secret, new GCMParameterSpec(AUTH_TAG, iv));
					byte[] res = enc.doFinal(input);
					byte[] result = new byte[SALT_LENGTH + IV_LENGTH + res.length];
					System.arraycopy(salt, 0, result, 0, SALT_LENGTH);
					System.arraycopy(iv, 0, result, SALT_LENGTH, IV_LENGTH);
					System.arraycopy(res, 0, result, SALT_LENGTH + IV_LENGTH, res.length);
					return result;
				} else {
					if (input.length < SALT_LENGTH + IV_LENGTH + 1) {
						return new byte[0]; // input too short, nothing to decode
					}
					byte[] salt = new byte[SALT_LENGTH];
					System.arraycopy(input, 0, salt, 0, SALT_LENGTH);
					byte[] iv = new byte[IV_LENGTH];
					System.arraycopy(input, SALT_LENGTH, iv, 0, IV_LENGTH);
					byte[] data = new byte[input.length - SALT_LENGTH - IV_LENGTH];
					System.arraycopy(input, SALT_LENGTH + IV_LENGTH, data, 0, data.length);

					KeySpec spec = new PBEKeySpec(encKey.toCharArray(), salt, 65536, 256);
					SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
					Cipher dec = Cipher.getInstance("AES/GCM/NoPadding", "BC");
					dec.init(mode, secret, new GCMParameterSpec(AUTH_TAG, iv));
					return dec.doFinal(data);
				}
			}
		};
	}
}
