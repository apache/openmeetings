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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.apache.openmeetings.util.StoredFile;
import org.junit.Test;

public class TestStoredFile {

	@Test
	public void testAudio() {
		final String[] exts = {"aif", "aifc", "aiff", "au", "mp3", "flac", "wav"}; //TODO enlarge
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext);
			assertTrue(String.format("Files of type '%s' should be treated as Video", ext), sf.isVideo());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Image", ext), sf.isImage());
		}
	}

	@Test
	public void testVideo() {
		final String[] exts = {"avi", "mov", "flv", "mp4"}; //TODO enlarge
		for (String ext : exts) {
			StoredFile sf = new StoredFile("test", ext);
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
			StoredFile sf = new StoredFile("test", ext);
			assertTrue(String.format("Files of type '%s' should be treated as Image", ext), sf.isImage());
			assertFalse(String.format("Files of type '%s' should NOT be treated as Video", ext), sf.isVideo());
		}
	}
}
