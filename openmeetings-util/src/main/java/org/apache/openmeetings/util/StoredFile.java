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
import static org.apache.openmeetings.util.OmFileHelper.getFileExt;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY;
import static org.apache.tika.mime.MediaType.application;
import static org.apache.tika.mime.MediaType.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class StoredFile {
	private static final Logger log = Red5LoggerFactory.getLogger(StoredFile.class, webAppRootKey);
	private final static String MIME_AUDIO = "audio";
	private final static String MIME_VIDEO = "video";
	private final static String MIME_IMAGE = "image";
	private final static String MIME_TEXT = "text";
	private final static String MIME_APP = "application";
	private static final Set<MediaType> CONVERT_TYPES = new HashSet<>(Arrays.asList(
			application("x-tika-msoffice"), application("x-tika-ooxml"), application("msword")
			, application("vnd.wordperfect"), application("rtf")));

	private static final MediaType MIME_JPG = image(EXTENSION_JPG);
	private static final Set<MediaType> PDF_TYPES = new HashSet<>(Arrays.asList(application("pdf"), application("postscript")));
	private static final Set<MediaType> CHART_TYPES = new HashSet<>(/* TODO have to be tested and re-added Arrays.asList("xchart")*/);
	private static final Set<MediaType> AS_IS_TYPES = new HashSet<>(Arrays.asList(MIME_JPG/* TODO have to be tested and re-added, "xchart"*/));
	private static final String ACCEPT_STRING;
	static {
		Set<MediaType> types = new LinkedHashSet<>();
		types.addAll(CONVERT_TYPES);
		types.addAll(PDF_TYPES);
		//TODO have to be tested and re-added ext.addAll(chartExtensions);
		StringBuilder sb = new StringBuilder("audio/*,video/*,image/*,text/*");
		sb.append(",application/vnd.oasis.opendocument.*");
		sb.append(",application/vnd.sun.xml.*");
		sb.append(",application/vnd.stardivision.*");
		sb.append(",application/x-star*");
		for (MediaType mt : types) {
			sb.append(',').append(mt.toString());
		}
		ACCEPT_STRING = sb.toString();
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

	public StoredFile(String fullname, File f) throws FileNotFoundException, IOException {
		this(fullname, null, f);
	}

	public StoredFile(String name, String ext, File f) throws FileNotFoundException, IOException {
		try (InputStream fis = new FileInputStream(f)) {
			init(name, ext, fis);
		}
	}

	private void init(String name, String ext, InputStream is) {
		if (Strings.isEmpty(ext)) {
			int idx = name.lastIndexOf('.');
			this.name = idx < 0 ? name : name.substring(0, idx);
			this.ext = getFileExt(name);
		} else {
			this.name = name;
			this.ext = ext.toLowerCase();
		}
		Tika tika = new Tika();
		Metadata md = new Metadata();
		md.add(RESOURCE_NAME_KEY, String.format("%s.%s", name, ext));
		try {
			mime = MediaType.parse(tika.detect(is, md));
		} catch (Exception e) {
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

	public boolean isJpg() {
		if (mime == null) {
			return false;
		}
		return MIME_JPG.equals(mime);
	}

	public boolean isImage() {
		if (mime == null) {
			return false;
		}
		return MIME_IMAGE.equals(mime.getType());
	}

	public boolean isVideo() {
		if (mime == null) {
			return false;
		}
		return MIME_AUDIO.equals(mime.getType()) || MIME_VIDEO.equals(mime.getType());
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
