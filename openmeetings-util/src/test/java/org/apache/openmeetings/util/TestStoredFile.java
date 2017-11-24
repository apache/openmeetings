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
package org.apache.openmeetings.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class TestStoredFile {
	@Test
	public void testAudio() {
		final String[] exts = {"aif", "aifc", "aiff", "au", "mp3", "flac", "wav"}; //TODO enlarge
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(String.format("Files of type '%s' should be treated as Video", ext), sf.isVideo());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Image", ext), sf.isImage());
		}
	}

	@Test
	public void testVideo() {
		final String[] exts = {"avi", "mov", "flv", "mp4"}; //TODO enlarge
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(String.format("Files of type '%s' should be treated as Video", ext), sf.isVideo());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Image", ext), sf.isImage());
		}
	}

	@Test
	public void testImage() {
		final String[] exts = {"png", "gif", "svg", "dpx", "exr",
				"pcd", // PhotoCD
				"pcds", // PhotoCD
				"psd", // Adobe Photoshop
				"tiff", // Tagged Image File Format
				"xcf", // GIMP image
				"wpg", // Word Perfect Graphics
				"bmp", "ico", // Microsoft Icon
				"tga", // Truevision Targa
				"jpg", "jpeg"}; //TODO enlarge
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(String.format("Files of type '%s' should be treated as Image", ext), sf.isImage());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Video", ext), sf.isVideo());
		}
	}

	@Test
	public void testOffice() {
		final String[] exts = {
				"ppt", "odp", "odt", "sxw", "wpd", "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx", "docx", "xlsx"
		};
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(String.format("Files of type '%s' should be treated as Convertible", ext), sf.isOffice());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Video", ext), sf.isVideo());
		}
	}

	private static void fileOfficeTest(String path) throws IOException {
		try (InputStream is = TestStoredFile.class.getResourceAsStream(path)) {
			StoredFile sf = new StoredFile(path, is);
			assertTrue(String.format("Files of type '%s' should be treated as Convertible", sf.getExt()), sf.isOffice());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Video", sf.getExt()), sf.isVideo());
		}
	}

	@Test
	public void testOffice1() throws IOException {
		for (String path : new String[] {"/ODFtest.odt", "/ODFtest.ods"}) {
			fileOfficeTest(path);
		}
	}
}
