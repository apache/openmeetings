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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;

import org.apache.openmeetings.util.OmFileHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OmFileHelper.class)
public class FileItemTest {

	@Mock
	private OmFileHelper omFileHelper;

	@InjectMocks
	private FileItem fileItem;

	@Before
	public void setup() {
		initMocks(this);

		// Setup path to be local test resources
		mockStatic(OmFileHelper.class);

		// PDF file tests
		when(OmFileHelper.getFileExt("6594186e-c6bb-49d5-9f66-829e45599aaa.pdf")).thenReturn("pdf");

		// DOCX file tests
		when(OmFileHelper.getFileExt("d44ab2c5-fd5d-4903-8fa7-292286d72a5f.docx")).thenReturn("docx");
		when(OmFileHelper.getFileExt("d44ab2c5-fd5d-4903-8fa7-292286d72a5f.pdf")).thenReturn("pdf");

		// Generic
		when(OmFileHelper.getFileExt("page-0000.png")).thenReturn("png");
		when(OmFileHelper.getFileExt("page-0001.png")).thenReturn("png");

		when(OmFileHelper.getUploadFilesDir()).thenReturn(new File("src/test/resources/org/apache/openmeetings/db/entity/file"));
	}

	@Test
	public void testGetFileShouldReturnFirstSlideWithPDF() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("mypresentation.pdf");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile(null);

		assertTrue(f.getName().endsWith("png"));
		assertEquals(f.getName(), "page-0000.png");
	}

	@Test
	public void testGetOriginalWithPDFWithOriginalName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("mypresentation.pdf");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals(f.getName(), "6594186e-c6bb-49d5-9f66-829e45599aaa.pdf");
	}

	@Test
	public void testGetOriginalWithPDFWithChangedName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("6594186e-c6bb-49d5-9f66-829e45599aaa");
		fileItem.setName("randomName");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals(f.getName(), "6594186e-c6bb-49d5-9f66-829e45599aaa.pdf");
	}

	@Test
	public void testGetOriginalWithDOCXWithOriginalName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("docx"));
		assertEquals(f.getName(), "d44ab2c5-fd5d-4903-8fa7-292286d72a5f.docx");
	}

	@Test
	public void testGetOriginalWithDOCXWithChangedName() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Random Name");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getOriginal();

		assertTrue(f.getName().endsWith("docx"));
		assertEquals(f.getName(), "d44ab2c5-fd5d-4903-8fa7-292286d72a5f.docx");
	}

	@Test
	public void testGetFileShouldReturnFirstSlideWithDOCX() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile(null);

		assertTrue(f.getName().endsWith("png"));
		assertEquals(f.getName(), "page-0000.png");
	}

	@Test
	public void testGetFileShouldReturnPDFWhenRequested() {
		// Setup file
		fileItem.setDeleted(false);
		fileItem.setHash("d44ab2c5-fd5d-4903-8fa7-292286d72a5f");
		fileItem.setName("Sample Document.docx");
		fileItem.setType(BaseFileItem.Type.PRESENTATION);

		File f = fileItem.getFile("pdf");

		assertTrue(f.getName().endsWith("pdf"));
		assertEquals(f.getName(), "d44ab2c5-fd5d-4903-8fa7-292286d72a5f.pdf");
	}

}
