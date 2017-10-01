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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.Test;

public abstract class AbstractCryptTest {
	protected static ICrypt crypt;

	@Test
	public void nulltest() {
		String hash = crypt.hash(null);
		assertEquals("Hash for null should be null", null, hash);

		assertTrue("Hash for null should be null", crypt.verify(null, null));
		assertFalse("Hash for null should be null", crypt.verify(null, "abc"));
		assertFalse("Hash for null should NOT be null", crypt.verify("abc", null));

		assertTrue("Hash for null should be null", crypt.fallback(null, null));
		assertFalse("Hash for null should be null", crypt.fallback(null, "abc"));
		assertFalse("Hash for null should NOT be null", crypt.fallback("abc", null));
	}

	private static List<String> get(int count) {
		Random rnd = new Random();
		List<String> l = new ArrayList<>(count + 1);
		l.add("");
		RandomStringGenerator generator = new RandomStringGenerator.Builder()
				.withinRange('!', '}')
				.usingRandom(rnd::nextInt)
				.build();
		for (int i = 0; i < count; ++i) {
			l.add(generator.generate(rnd.nextInt(256)));
		}
		return l;
	}

	@Test
	public void test() {
		for (String str : get(64)) {
			String h1 = crypt.hash(str);
			assertNotNull("Hash should not be null", h1);
			String h2 = crypt.hash(str);
			assertNotEquals("Hashes of same string should NOT be the same", h1,  h2);
			assertTrue("String should be verified successfully", crypt.verify(str, h1));
			assertTrue("String should be verified successfully", crypt.verify(str, h2));
		}
	}
}
