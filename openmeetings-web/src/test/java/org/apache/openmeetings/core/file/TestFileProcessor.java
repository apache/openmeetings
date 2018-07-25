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

import static org.apache.openmeetings.util.OmFileHelper.FILE_NAME_FMT;
import static org.apache.openmeetings.util.OmFileHelper.getDefaultProfilePicture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.core.data.file.FileProcessor;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestFileProcessor extends AbstractJUnitDefaults {
	private static final String FILE_NAME = "test_name";

	@Autowired
	protected FileProcessor processor;

	@Test
	public void testProcessPng() throws Exception {
		for (String ext : new String[] {null, "txt", "jpg"}) {
			FileItem f = new FileItemDTO()
					.setName(String.format(FILE_NAME_FMT, FILE_NAME, ext))
					.setHash(UUID.randomUUID().toString())
					.setType(BaseFileItem.Type.Recording).get();
			try (InputStream is = new FileInputStream(getDefaultProfilePicture())) {
				ProcessResultList result = processor.processFile(f, is);
				assertFalse("Conversion should be successful", result.hasError());
				assertEquals("Type should be image", BaseFileItem.Type.Image, f.getType());
			}
		}
	}
}
