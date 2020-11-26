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
package org.apache.openmeetings.db.entity.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.io.File;

import org.apache.openmeetings.util.OmFileHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileItemTest {
	private FileItem fileItem;

	@BeforeEach
	void createNewStack() {
		fileItem = new FileItem();
	}

	@Test
	void testGetFileShouldReturnFirstSlideWithPDF() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("mypresentation.pdf");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile(null);

		assertTrue(f.getName().endsWith("png"));
		assertEquals("page-0000.png", f.getName());
	}

	@Test
	void testGetOriginalWithPDFWithOriginalName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("mypresentation.pdf");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals("6594186e-c6bb-49d5-9f66-829e45599aaa.pdf", f.getName());
	}

	@Test
	void testGetOriginalWithPDFWithChangedName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("randomName");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals("6594186e-c6bb-49d5-9f66-829e45599aaa.pdf", f.getName());
	}

	private void wrapper(Runnable r) {
		try (MockedStatic<OmFileHelper> theMock = mockStatic(OmFileHelper.class)) {
			theMock.when(OmFileHelper::getUploadFilesDir).thenReturn(new File("src/test/resources/org/apache/openmeetings/db/entity/file"));
			r.run();
		}
	}

	@Test
	void testGetOriginalWithDOCXWithOriginalName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		wrapper(() -> {
			File f = fileItem.getOriginal();

			assertTrue(f.getName().endsWith("docx"));
			assertEquals("d44ab2c5-fd5d-4903-8fa7-292286d72a5f.docx", f.getName());
		});
	}

	@Test
	void testGetOriginalWithDOCXWithChangedName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Random Name");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);
		wrapper(() -> {
			File f = fileItem.getOriginal();

			assertTrue(f.getName().endsWith("docx"));
			assertEquals("d44ab2c5-fd5d-4903-8fa7-292286d72a5f.docx", f.getName());
		});
	}

	@Test
	void testGetFileShouldReturnFirstSlideWithDOCX() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile(null);

		assertTrue(f.getName().endsWith("png"));
		assertEquals("page-0000.png", f.getName());
	}

	@Test
	void testGetFileShouldReturnPDFWhenRequested() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile("pdf");

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals("d44ab2c5-fd5d-4903-8fa7-292286d72a5f.pdf", f.getName());
	}

}
