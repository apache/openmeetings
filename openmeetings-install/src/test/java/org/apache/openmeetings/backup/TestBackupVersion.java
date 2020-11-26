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
package org.apache.openmeetings.backup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;


class TestBackupVersion {
	@Test
	void testGet() {
		BackupVersion bv = BackupVersion.get();
		assertNotEquals(0, bv.getMajor(), "Major should be set");
	}

	@Test
	void testEquals() {
		assertEquals(BackupVersion.get(), BackupVersion.get());
		assertNotEquals(null, BackupVersion.get());
		assertNotEquals(BackupVersion.get("3.2.1"), BackupVersion.get("3.2.0"));
		assertNotEquals(BackupVersion.get("3.1.1"), BackupVersion.get("3.0.0"));
		assertNotEquals(BackupVersion.get("3.1.1"), BackupVersion.get("2.0.0"));
	}

	@Test
	void testCompare() {
		assertEquals(0, BackupVersion.get().compareTo(BackupVersion.get()));
		assertEquals(1, BackupVersion.get().compareTo(null));
		assertEquals(1, BackupVersion.get("3.2.1").compareTo(BackupVersion.get("3.2.0")));
		assertEquals(-1, BackupVersion.get("3.2.0").compareTo(BackupVersion.get("3.2.1")));
		assertEquals(1, BackupVersion.get("3.1.1").compareTo(BackupVersion.get("3.0.0")));
		assertEquals(1, BackupVersion.get("3.1.1").compareTo(BackupVersion.get("2.0.0")));
	}
}
