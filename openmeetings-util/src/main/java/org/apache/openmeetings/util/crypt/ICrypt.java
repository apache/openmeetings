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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for Encryption-Class see:
 * https://openmeetings.apache.org/CustomCryptMechanism.html see:
 * https://crackstation.net/hashing-security.htm
 *
 * @author sebastianwagner
 *
 */

public interface ICrypt {
	public static final Logger log = LoggerFactory.getLogger(SCryptImplementation.class);
	public static final String SECURE_RND_ALG = "SHA1PRNG";
	public static final ThreadLocal<SecureRandom> rnd
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
	/**
	 * Creates hash of given string
	 *
	 * @param str
	 *            - string to calculate hash for
	 * @return hash of passed string
	 */
	String hash(String str);

	/**
	 * Verify string passed is matches given hash
	 *
	 * @param str
	 *            - string to check hash for
	 * @param hash
	 *            - hash to compare
	 * @return <code>true</code> in case string matches hash, <code>false</code> otherwise
	 */
	boolean verify(String str, String hash);

	/**
	 * Verify string passed is matches given hash (using fallback crypt mechanism)
	 *
	 * @param str
	 *            - string to check hash for
	 * @param hash
	 *            - hash to compare
	 * @return <code>true</code> in case string matches hash, <code>false</code> otherwise
	 */
	default boolean fallback(String str, String hash) {
		return false;
	}

	String randomPassword(int length);
}
