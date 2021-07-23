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

import static org.apache.openmeetings.util.OmFileHelper.FILE_NAME_FMT;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;
import static org.apache.tika.mime.MediaType.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoredFile {
	private static final Logger log = LoggerFactory.getLogger(StoredFile.class);
	private static final String MIME_AUDIO = "audio";
	private static final String MIME_VIDEO = "video";
	private static final String MIME_IMAGE = "image";
	private static final String MIME_TEXT = "text";
	private static final String MIME_APP = "application";
	private static final Set<MediaType> CONVERT_TYPES = Set.of(
			application("x-tika-msoffice"), application("x-tika-ooxml"), application("msword")
			, application("vnd.wordperfect"), application("rtf"));

	private static final MediaType MIME_PNG = MediaType.parse(PNG_MIME_TYPE);
	private static final Set<MediaType> PDF_TYPES = Set.of(application("pdf"), application("postscript"));
	private static final Set<MediaType> CHART_TYPES = new HashSet<>();
	private static final Set<MediaType> AS_IS_TYPES = Set.of(MIME_PNG);
	private static final String ACCEPT_STRING;
	private static TikaConfig tika;
	static {
		Set<MediaType> types = new LinkedHashSet<>();
		types.addAll(CONVERT_TYPES);
		types.addAll(PDF_TYPES);
		//TODO Charts need to added
		StringBuilder sb = new StringBuilder("audio/*,video/*,image/*,text/*");
		sb.append(",application/vnd.oasis.opendocument.*");
		sb.append(",application/vnd.sun.xml.*");
		sb.append(",application/vnd.stardivision.*");
		sb.append(",application/x-star*");
		for (MediaType mt : types) {
			sb.append(',').append(mt.toString());
		}
		ACCEPT_STRING = sb.toString();
		try {
			tika = new TikaConfig();
		} catch (IOException | TikaException e) {
			log.error("Unexpected exception while initializing TIKA", e);
			throw new RuntimeException(e);
		}
	}

	private String name;
	private String ext;
	private MediaType mime;

	public StoredFile(String fullname, InputStream is) {
		this(fullname, null, is);
	}

	public StoredFile(String name, String ext, InputStream is) {
		init(name, ext, is);
	}

	public StoredFile(String fullname, File f) throws IOException {
		this(fullname, null, f);
	}

	public StoredFile(String name, String ext, File f) throws IOException {
		try (InputStream fis = new FileInputStream(f)) {
			init(name, ext, fis);
		}
	}

	private void init(String inName, String inExt, InputStream is) {
		if (Strings.isEmpty(inExt)) {
			int idx = inName.lastIndexOf('.');
			name = idx < 0 ? inName : inName.substring(0, idx);
			ext = getFileExt(inName);
		} else {
			name = inName;
			ext = inExt.toLowerCase(Locale.ROOT);
		}
		Metadata md = new Metadata();
		md.add(RESOURCE_NAME_KEY, String.format(FILE_NAME_FMT, name, ext));
		try {
			mime = tika.getDetector().detect(is == null ? null : TikaInputStream.get(is), md);
		} catch (Throwable e) {
			mime = null;
			log.error("Unexpected exception while detecting mime type", e);
		}
	}

	public static String getAcceptAttr() {
		return ACCEPT_STRING;
	}

	public boolean isOffice() {
		if (mime == null) {
			return false;
		}
		return MIME_TEXT.equals(mime.getType())
				|| (MIME_APP.equals(mime.getType()) &&
						(mime.getSubtype().startsWith("vnd.oasis.opendocument")
							|| mime.getSubtype().startsWith("vnd.sun.xml")
							|| mime.getSubtype().startsWith("vnd.stardivision")
							|| mime.getSubtype().startsWith("x-star")
							|| mime.getSubtype().startsWith("vnd.ms-")
							|| mime.getSubtype().startsWith("vnd.openxmlformats-officedocument")
					))
				|| CONVERT_TYPES.contains(mime);
	}

	public boolean isPresentation() {
		return isOffice() || isPdf();
	}

	public boolean isPdf() {
		if (mime == null) {
			return false;
		}
		return PDF_TYPES.contains(mime);
	}

	public boolean isPng() {
		if (mime == null) {
			return false;
		}
		return MIME_PNG.equals(mime);
	}

	public boolean isImage() {
		if (mime == null) {
			return false;
		}
		return MIME_IMAGE.equals(mime.getType());
	}

	public boolean isAudio() {
		if (mime == null) {
			return false;
		}
		return MIME_AUDIO.equals(mime.getType());
	}

	public boolean isVideo() {
		if (mime == null) {
			return false;
		}
		return isAudio() || MIME_VIDEO.equals(mime.getType());
	}

	public boolean isChart() {
		if (mime == null) {
			return false;
		}
		return CHART_TYPES.contains(mime);
	}

	public boolean isAsIs() {
		if (mime == null) {
			return false;
		}
		return AS_IS_TYPES.contains(mime);
	}

	public String getName() {
		return name;
	}

	public String getExt() {
		return ext;
	}

	public MediaType getMime() {
		return mime;
	}
}
