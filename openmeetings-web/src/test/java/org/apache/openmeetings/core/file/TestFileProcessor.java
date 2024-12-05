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
package org.apache.openmeetings.core.file;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OmFileHelper.getName;
import static org.apache.openmeetings.util.OmFileHelper.getDefaultProfilePicture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestFileProcessor extends AbstractOmServerTest {
	private static final String FILE_NAME = "test_name";

	@Inject
	protected FileProcessor processor;

	@Test
	void testProcessPng() throws Exception {
		for (String ext : new String[] {null, "txt", "jpg"}) {
			FileItem f = new FileItemDTO()
					.setName(getName(FILE_NAME, ext))
					.setHash(randomUUID().toString())
					.setType(BaseFileItem.Type.RECORDING).get();
			try (InputStream is = new FileInputStream(getDefaultProfilePicture())) {
				ProcessResultList result = processor.processFile(f, is, Optional.empty());
				assertFalse(result.hasError(), "Conversion should be successful");
				assertEquals(BaseFileItem.Type.IMAGE, f.getType(), "Type should be image");
			}
		}
	}
}
