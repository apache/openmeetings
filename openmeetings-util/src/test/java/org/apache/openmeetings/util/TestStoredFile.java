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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class TestStoredFile {
	@Test
	void testAudio() {
		final String[] exts = {"aif", "aifc", "aiff", "au", "mp3", "flac", "wav"};
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(sf.isVideo(), String.format("Files of type '%s' should be treated as Video", ext));
			assertFalse(sf.isImage(), String.format("Files of type '%s' should NOT be treated as Image", ext));
		}
	}

	@Test
	void testVideo() {
		final String[] exts = {"avi", "mov", "flv", "mp4"};
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(sf.isVideo(), String.format("Files of type '%s' should be treated as Video", ext));
			assertFalse(sf.isImage(), String.format("Files of type '%s' should NOT be treated as Image", ext));
		}
	}

	@Test
	void testImage() {
		final String[] exts = {"png", "gif", "svg", "dpx", "exr",
				"pcd", // PhotoCD
				"pcds", // PhotoCD
				"psd", // Adobe Photoshop
				"tiff", // Tagged Image File Format
				"xcf", // GIMP image
				"wpg", // Word Perfect Graphics
				"bmp", "ico", // Microsoft Icon
				"tga", // Truevision Targa
				"jpg", "jpeg"};
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(sf.isImage(), String.format("Files of type '%s' should be treated as Image", ext));
			assertFalse(sf.isVideo(), String.format("Files of type '%s' should NOT be treated as Video", ext));
		}
	}

	@Test
	void testOffice() {
		final String[] exts = {
				"ppt", "odp", "odt", "sxw", "wpd", "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx", "docx", "xlsx"
		};
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext, (InputStream)null);
			assertTrue(sf.isOffice(), String.format("Files of type '%s' should be treated as Convertible", ext));
			assertFalse(sf.isVideo(), String.format("Files of type '%s' should NOT be treated as Video", ext));
		}
	}

	private static void fileOfficeTest(String path) throws IOException {
		try (InputStream is = TestStoredFile.class.getResourceAsStream(path)) {
			StoredFile sf = new StoredFile(path, is);
			assertTrue(sf.isOffice(), String.format("Files of type '%s' should be treated as Convertible", sf.getExt()));
			assertFalse(sf.isVideo(), String.format("Files of type '%s' should NOT be treated as Video", sf.getExt()));
		}
	}

	@Test
	void testOffice1() throws IOException {
		for (String path : new String[] {"/ODFtest.odt", "/ODFtest.ods"}) {
			fileOfficeTest(path);
		}
	}
}
