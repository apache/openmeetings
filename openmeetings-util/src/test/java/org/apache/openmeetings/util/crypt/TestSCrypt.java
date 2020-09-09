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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestSCrypt extends AbstractCryptTest {
	private static final String TEST_PASS = "12345";
	private static final String MD5_HASH = "827ccb0eea8a706c4c34a16891f84e7b";
	private static final String SHA_HASH = "1000:3C2Sm1yw8NoyEBg8KaJfJMye9GaM8uKDNfUNyPWSbwNI2amKAK10KIrPOQeOV7uLkGCT1Fl5gabBRGjLRSBzi7S8LgaVetiEuCL0d8oVPYT1xtgrmEzx/dIyd7hVaGbol388FVW2Ei7ZxIce8DIOtKmMfrxqoNEZa+ERRAzBGLE=:lbveBdEopW7QuU2jcgv4UeuA1m0eDwfIz+KjWgciF/8TWdLi7utCiy+wm3X2pp0WRffqKEs+wwBh6iJbF2WNPIH06YaB68Q1h34wpxjBdziqAbUiGt2nZiPdKghNNX5j4L0Jp1gGRWpXOrg7V1NqYV6pLmwa+SipQs7MJGCCf+HAcwYW3HNIcp2Rbu9IzH7/t7oJo+FCgL4i1rYVHxrbHhAZCA9hr+dKM6u3S/Ef+EsZfSxCOX2BIRkoqHF4ZlLpwCIf6gmq3m7jenAjz0h2AuO/pM3Mf5d8Oy0LAqgiznU9/S7eEP6QYifF3V/P2ZL6/nX9RprVTTiSf0+GsAygOg==";

	@BeforeAll
	public static void setup() {
		crypt = new SCryptImplementation();
	}

	@Test
	void fallbackTest() {
		assertFalse(crypt.verify(TEST_PASS, MD5_HASH), "MD5 is not valid hash");
		assertFalse(crypt.verify(TEST_PASS, SHA_HASH), "SHA256 is not valid hash");
		assertFalse(crypt.fallback(TEST_PASS, MD5_HASH), "MD5 is valid fallback");
		assertTrue(crypt.fallback(TEST_PASS, SHA_HASH), "SHA256 is valid fallback");
		assertFalse(crypt.fallback(TEST_PASS, "abc"), "Fallback can return false");
	}
}
