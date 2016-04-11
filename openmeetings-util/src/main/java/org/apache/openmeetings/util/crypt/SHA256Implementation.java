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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SHA256Implementation implements ICrypt {
	private static final Logger log = Red5LoggerFactory.getLogger(SHA256Implementation.class, webAppRootKey);
	private static final String SECURE_RND_ALG = "SHA1PRNG";
	private static final int ITERATIONS = 1000;
	private static final int KEY_LENGTH = 128 * 8;
	private static final int SALT_LENGTH = 256;

	private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance(SECURE_RND_ALG);
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }
	
	private static String hash(String str, byte[] salt, int iter) {
		PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
		gen.init(str.getBytes(StandardCharsets.UTF_8), salt, iter);
		byte[] dk = ((KeyParameter) gen.generateDerivedParameters(KEY_LENGTH)).getKey();
		return Base64.encodeBase64String(dk);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.openmeetings.utils.crypt.ICrypt#hash(java.lang.String)
	 */
	@Override
	public String hash(String str) {
		if (str == null) {
			return null;
		}
		String hash = null;
		try {
			byte[] salt = getSalt();
			String h = hash(str, salt, ITERATIONS);
			hash = String.format("%s:%s:%s", ITERATIONS, h, Base64.encodeBase64String(salt));
		} catch (NoSuchAlgorithmException e) {
			log.error("Error", e);
		}
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.openmeetings.utils.crypt.ICrypt#verify(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean verify(String str, String hash) {
		if (str == null) {
			return hash == null;
		}
		if (hash == null) {
			return false;
		}
		String[] ss = hash.split(":");
		if (ss.length != 3) {
			return false;
		}
		try {
			int iter = Integer.parseInt(ss[0]);
			String h1 = ss[1];
			byte[] salt = Base64.decodeBase64(ss[2]);
			String h2 = hash(str, salt, iter);
			return h2.equals(h1);
		} catch (Exception e) {
			return false;
		}
	}
}
