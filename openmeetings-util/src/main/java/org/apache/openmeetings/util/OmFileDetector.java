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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public class OmFileDetector extends FileTypeDetector {
	private final static String[] KNOWN_EXTS = {"dpx", "exr", "pcd", "pcds", "xcf", "wpg", "tga"};
	private final static String OTHER = "image/other";

	@Override
	public String probeContentType(Path path) throws IOException {
		for (String ext : KNOWN_EXTS) {
			if (("" + path).endsWith(ext)) {
				return OTHER;
			}
		}
		return null;
	}
}
