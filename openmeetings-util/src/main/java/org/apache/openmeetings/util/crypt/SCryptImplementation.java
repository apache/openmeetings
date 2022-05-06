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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.generators.SCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SCryptImplementation implements ICrypt {
	private static final Logger log = LoggerFactory.getLogger(SCryptImplementation.class);
	private static final String SECURE_RND_ALG = "SHA1PRNG";
	private static final ThreadLocal<SecureRandom> rnd
			= ThreadLocal.withInitial(() -> {
				SecureRandom sr;
				try {
					sr = SecureRandom.getInstance(SECURE_RND_ALG);
				} catch (NoSuchAlgorithmException e) {
					log.error("Failed to get instance of SecureRandom {}", SECURE_RND_ALG);
					sr = new SecureRandom();
				}
				return sr;
			});
	private static int cost = 1024 * 16;
	private static final int KEY_LENGTH = 512;
	private static final int SALT_LENGTH = 200;

	private static byte[] getSalt(int length) {
		byte[] salt = new byte[length];
		rnd.get().nextBytes(salt);
		return salt;
	}

	SCryptImplementation() {
		try (final InputStream is = getClass().getResourceAsStream("/openmeetings.properties")) {
			Properties props = new Properties();
			props.load(is);
			cost = Integer.valueOf(props.getProperty("scrypt.cost", "" + cost));
		} catch (Exception e) {
			log.error("Failed to initialize the cost {}", e.getMessage());
		}
	}

	private static String hash(String str, byte[] salt) {
		byte[] dk = SCrypt.generate(str.getBytes(UTF_8), salt, cost, 8, 8, KEY_LENGTH);
		return Base64.encodeBase64String(dk);
	}

	@Override
	public String hash(String str) {
		if (str == null) {
			return null;
		}
		byte[] salt = getSalt(SALT_LENGTH);
		String h = hash(str, salt);
		return String.format("%s:%s", h, Base64.encodeBase64String(salt));
	}

	@Override
	public boolean verify(String str, String hash) {
		if (str == null) {
			return hash == null;
		}
		if (hash == null) {
			return false;
		}
		String[] ss = hash.split(":");
		if (ss.length != 2) {
			return false;
		}
		try {
			String h1 = ss[0];
			byte[] salt = Base64.decodeBase64(ss[1]);
			String h2 = hash(str, salt);
			return h2.equals(h1);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean fallback(String str, String hash) {
		return SHA256Implementation.verify(str, hash);
	}

	@Override
	public String randomPassword(int length) {
		return Base64.encodeBase64String(getSalt(length));
	}
}
