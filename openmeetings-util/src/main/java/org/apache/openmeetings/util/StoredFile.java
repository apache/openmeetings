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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.util.string.Strings;

public class StoredFile {
	private final static MimetypesFileTypeMap MIMES_MAP = new MimetypesFileTypeMap();
	private final static String MIME_AUDIO = "audio";
	private final static String MIME_VIDEO = "video";
	private final static String MIME_IMAGE = "image";
	private static final Set<String> convertExtensions = new HashSet<>(
			Arrays.asList("ppt", "odp", "odt", "sxw", "wpd", "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx", "docx", "xlsx"));

	private static final Set<String> pdfExtensions = new HashSet<>(Arrays.asList("pdf", "ps"));

	private static final Set<String> chartExtensions = new HashSet<>(Arrays.asList("xchart"));

	private static final Set<String> asIsExtensions = new HashSet<>(Arrays.asList(EXTENSION_MP4, EXTENSION_JPG, "xchart"));

	private final String name;
	private final String ext;

	public StoredFile(String fullname) {
		int idx = fullname.lastIndexOf('.');
		name = idx < 0 ? fullname : fullname.substring(0, idx);
		ext = idx < 0 ? "" : fullname.substring(idx + 1).toLowerCase();
	}

	public StoredFile(String name, String ext) {
		this.name = name;
		this.ext = ext != null ? ext.toLowerCase() : "";
	}

	public static String getAcceptAttr() {
		Set<String> ext = new LinkedHashSet<>();
		ext.addAll(convertExtensions);
		ext.addAll(pdfExtensions);
		ext.addAll(chartExtensions);
		StringBuilder sb = new StringBuilder("audio/*,video/*,image/*,.");
		sb.append(StringUtils.join(ext, ",.")); // TODO java8 String.join("|.", ext);
		return sb.toString();
	}

	public boolean isConvertable() {
		return convertExtensions.contains(ext);
	}

	public boolean isPresentation() {
		return isConvertable() || isPdf();
	}

	public boolean isPdf() {
		return pdfExtensions.contains(ext);
	}

	private static String getMimeType(StoredFile f) {
		String filename = String.format("%s%s%s", f.name, Strings.isEmpty(f.ext) ? "" : ".", f.ext);
		String type = "";
		try {
			type = Files.probeContentType(new File(filename).toPath());
		} catch (IOException e) {
			//no-op
		}
		if (Strings.isEmpty(type)) {
			type = MIMES_MAP.getContentType(filename);
		}
		String[] mime = type.split("/");
		return mime[0];
	}
	
	public boolean isImage() {
		String mime = getMimeType(this);
		return MIME_IMAGE.equals(mime);
	}

	public boolean isVideo() {
		String mime = getMimeType(this);
		return MIME_AUDIO.equals(mime) || MIME_VIDEO.equals(mime);
	}

	public boolean isChart() {
		return chartExtensions.contains(ext);
	}

	public boolean isAsIs() {
		return asIsExtensions.contains(ext);
	}

	public String getName() {
		return name;
	}
}
