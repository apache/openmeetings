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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class StoredFile {
	private static final Set<String> convertExtensions = new HashSet<>(
			Arrays.asList("ppt", "odp", "odt", "sxw", "wpd", "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx", "docx", "xlsx"));

	private static final Set<String> pdfExtensions = new HashSet<>(Arrays.asList("pdf", "ps"));

	private static final Set<String> imageExtensions = new HashSet<>(Arrays.asList("png", "gif", "svg", "dpx", "exr",
			"pcd", // PhotoCD
			"pcds", // PhotoCD
			"psd", // Adobe Photoshop
			"tiff", // Tagged Image File Format
			"ttf", // TrueType font
			"xcf", // GIMP image
			"wpg", // Word Perfect Graphics
			"bmp", "ico", // Microsoft Icon
			"tga", // Truevision Targa
			"jpg", "jpeg"));

	private static final Set<String> chartExtensions = new HashSet<>(Arrays.asList("xchart"));

	private static final Set<String> videoExtensions = new HashSet<>(Arrays.asList("avi", "mov", "flv", "mp4"));

	private static final Set<String> asIsExtensions = new HashSet<>(Arrays.asList("jpg", "xchart"));

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

	public static String[] getExtensions() {
		Set<String> extensions = new HashSet<>();
		extensions.addAll(convertExtensions);
		extensions.addAll(pdfExtensions);
		extensions.addAll(imageExtensions);
		extensions.addAll(chartExtensions);
		extensions.addAll(videoExtensions);
		extensions.addAll(asIsExtensions);

		return extensions.toArray(new String[extensions.size()]);
	}

	public static String getAcceptAttr() {
		Set<String> ext = new LinkedHashSet<>();
		ext.addAll(convertExtensions);
		ext.addAll(pdfExtensions);
		ext.addAll(chartExtensions);
		StringBuilder sb = new StringBuilder("video/*,image/*,."); // TODO add audio/*,
		sb.append(StringUtils.join(ext, ",."));
		// TODO java8 String.join("|.", ext);
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

	public boolean isImage() {
		return imageExtensions.contains(ext);
	}

	public boolean isVideo() {
		return videoExtensions.contains(ext);
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
