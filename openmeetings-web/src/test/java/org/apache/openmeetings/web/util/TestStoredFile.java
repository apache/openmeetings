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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.util.OmFileHelper.getDefaultProfilePicture;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.util.StoredFile;
import org.junit.jupiter.api.Test;

class TestStoredFile extends AbstractOmServerTest {
	@Test
	void testPng() throws FileNotFoundException, IOException {
		File f = getDefaultProfilePicture();
		for (String ext : new String[] {null, "txt", "jpg"}) {
			StoredFile sf = new StoredFile("test image", ext, f);
			assertTrue(sf.isImage(), "Type should be detected as image");
			assertTrue(sf.isAsIs(), "Type should be detected as image");
		}
	}
}
