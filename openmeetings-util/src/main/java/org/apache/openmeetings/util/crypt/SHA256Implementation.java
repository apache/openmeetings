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

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Package private SHA256 implementation to be able to authenticate against
 * passwords created using OM 3.1.x-3.2.x
 */
class SHA256Implementation {
	private static final int KEY_LENGTH = 128 * 8;

	private SHA256Implementation() {}

	private static String hash(String str, byte[] salt, int iter) {
		PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
		gen.init(str.getBytes(StandardCharsets.UTF_8), salt, iter);
		byte[] dk = ((KeyParameter) gen.generateDerivedParameters(KEY_LENGTH)).getKey();
		return Base64.encodeBase64String(dk);
	}

	static boolean verify(String str, String hash) {
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
