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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;

abstract class AbstractCryptTest {
	protected static ICrypt crypt;

	@Test
	void nulltest() {
		String hash = crypt.hash(null);
		assertNull(hash, "Hash for null should be null");

		assertTrue(crypt.verify(null, null), "Hash for null should be null");
		assertFalse(crypt.verify(null, "abc"), "Hash for null should be null");
		assertFalse(crypt.verify("abc", null), "Hash for null should NOT be null");

		assertTrue(crypt.fallback(null, null), "Hash for null should be null");
		assertFalse(crypt.fallback(null, "abc"), "Hash for null should be null");
		assertFalse(crypt.fallback("abc", null), "Hash for null should NOT be null");
	}

	private static List<String> get(int count) {
		Random rnd = new Random();
		List<String> l = new ArrayList<>(count + 1);
		l.add("");
		RandomStringGenerator generator = new RandomStringGenerator.Builder()
				.withinRange('!', '}')
				.usingRandom(rnd::nextInt)
				.get();
		for (int i = 0; i < count; ++i) {
			l.add(generator.generate(rnd.nextInt(256)));
		}
		return l;
	}

	@Test
	void test() {
		for (String str : get(64)) {
			String h1 = crypt.hash(str);
			assertNotNull(h1, "Hash should not be null");
			String h2 = crypt.hash(str);
			assertNotEquals(h1,  h2, "Hashes of same string should NOT be the same");
			assertTrue(crypt.verify(str, h1), "String should be verified successfully");
			assertTrue(crypt.verify(str, h2), "String should be verified successfully");
		}
	}
}
