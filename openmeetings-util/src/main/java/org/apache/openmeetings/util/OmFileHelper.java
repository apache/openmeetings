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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmFileHelper {
	private static final Logger log = LoggerFactory.getLogger(OmFileHelper.class);
	/**
	 * This variable needs to point to the openmeetings webapp directory
	 */
	private static File omHome = null;
	private static final String UPLOAD_DIR = "upload";
	private static final String PUBLIC_DIR = "public";
	private static final String CLIPARTS_DIR = "cliparts";
	private static final String WEB_INF_DIR = "WEB-INF";
	private static final String GROUP_LOGO_DIR = "grouplogo";
	private static final String STREAMS_DIR = "streams";
	private static final String EMOTIONS_DIR = "emoticons";
	private static final String LANGUAGES_DIR = "languages";
	private static final String CONF_DIR = "conf";
	private static final String IMAGES_DIR = "images";
	private static final String WML_DIR = "stored";

	public static final String FILE_NAME_FMT = "%s.%s";
	public static final String BACKUP_DIR = "backup";
	public static final String IMPORT_DIR = "import";
	public static final String PROFILES_DIR = "profiles";
	public static final String SCREENSHARING_DIR = "screensharing";
	public static final String CSS_DIR = "css";
	public static final String CUSTOM_CSS = "custom.css";
	public static final String FILES_DIR = "files";
	public static final String HIBERNATE = "hibernate";
	public static final String PERSISTENCE_NAME = "classes/META-INF/persistence.xml";
	public static final String DB_PERSISTENCE_NAME = "classes/META-INF/%s_persistence.xml";
	public static final String PROFILES_PREFIX = "profile_";
	public static final String LANG_FILE_NAME = "languages.xml";
	public static final String LIBRARY_FILE_NAME = "library.xml";
	public static final String PROFILE_IMG_NAME = "profile.png";
	public static final String PROFILE_FILE_NAME = "profile";
	public static final String RECORDING_FILE_NAME = "flvRecording_";
	public static final String THUMB_IMG_PREFIX = "_thumb_";
	public static final String DOC_PAGE_PREFIX = "page";
	public static final String TEST_SETUP_PREFIX = "TEST_SETUP_";
	public static final String DASHBOARD_FILE = "dashboard.xml";
	public static final String EXTENSION_WML = "wml";
	public static final String EXTENSION_WEBM = "webm";
	public static final String EXTENSION_MP4 = "mp4";
	public static final String EXTENSION_JPG = "jpg";
	public static final String EXTENSION_PNG = "png";
	public static final String EXTENSION_PDF = "pdf";
	public static final String WB_VIDEO_FILE_PREFIX = "UPLOADFLV_";
	public static final String MP4_MIME_TYPE = "video/" + EXTENSION_MP4;
	public static final String JPG_MIME_TYPE = "image/jpeg";
	public static final String PNG_MIME_TYPE = "image/png";
	public static final String BCKP_ROOM_FILES = "roomFiles";
	public static final String BCKP_RECORD_FILES = "recordingFiles";

	//Sip related stuff
	public static final Long SIP_USER_ID = -1L;
	public static final String SIP_PICTURE_URI = "phone.png";

	private OmFileHelper() {}

	public static void setOmHome(File omHome) {
		OmFileHelper.omHome = omHome;
	}

	public static void setOmHome(String omHome) {
		OmFileHelper.omHome = new File(omHome);
	}

	public static File getRootDir() {
		// hack !!!!
		return getOmHome().getParentFile().getParentFile();
	}

	public static File getOmHome() {
		return OmFileHelper.omHome;
	}

	private static File getDir(File parent, String name) {
		File f = new File(parent, name);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

	public static File getUploadDir() {
		return new File(OmFileHelper.omHome, UPLOAD_DIR);
	}

	public static File getUploadFilesDir() {
		return getDir(getUploadDir(), FILES_DIR);
	}

	public static File getUploadProfilesDir() {
		return getDir(getUploadDir(), PROFILES_DIR);
	}

	public static File getUploadProfilesUserDir(Long userId) {
		return getDir(getUploadProfilesDir(), PROFILES_PREFIX + userId);
	}

	public static File getUploadProfilesUserDir(String userId) {
		return getDir(getUploadProfilesDir(), PROFILES_PREFIX + userId);
	}

	public static File getGroupLogoDir() {
		return getDir(getUploadDir(), GROUP_LOGO_DIR);
	}

	public static File getGroupLogo(Long groupId, boolean check) {
		File logo = new File(getGroupLogoDir(), String.format("logo%s.png", groupId));
		if (groupId == null || (check && !logo.exists())) {
			logo = new File(getImagesDir(), "blank.png");
		}
		return logo;
	}

	public static File getDefaultProfilePicture() {
		return new File(getImagesDir(), PROFILE_IMG_NAME);
	}

	public static File getUserProfilePicture(Long userId, String uri) {
		return getUserProfilePicture(userId, uri, getDefaultProfilePicture());
	}

	public static File getUserProfilePicture(Long userId, String uri, File def) {
		File img = null;
		if (SIP_USER_ID.equals(userId)) {
			img = new File(getImagesDir(), SIP_PICTURE_URI);
		} else if (userId != null) {
			img = new File(getUploadProfilesUserDir(userId), uri == null ? "" : uri);
		}
		if (img == null || !img.exists() || img.isDirectory()) {
			img = def;
		}
		return img;
	}

	public static File getUserDashboard(Long userId) {
		return new File(getUploadProfilesUserDir(userId), DASHBOARD_FILE);
	}

	public static File getUploadImportDir() {
		return getDir(getUploadDir(), IMPORT_DIR);
	}

	public static File getUploadBackupDir() {
		return getDir(getUploadDir(), BACKUP_DIR);
	}

	public static File getUploadRoomDir(String roomName) {
		return getDir(getUploadDir(), roomName);
	}

	public static File getUploadWmlDir() {
		return getDir(getUploadDir(), WML_DIR);
	}

	public static File getStreamsDir() {
		return getDir(OmFileHelper.omHome, STREAMS_DIR);
	}

	public static File getStreamsHibernateDir() {
		return getDir(getStreamsDir(), HIBERNATE);
	}

	public static File getRecording(String name) {
		return new File(getDir(getStreamsDir(), HIBERNATE), name);
	}

	public static File getStreamsSubDir(Long id) {
		return getStreamsSubDir(String.valueOf(id));
	}

	public static File getStreamsSubDir(String name) {
		return getDir(getStreamsDir(), name);
	}

	public static String getRecUri(File f) {
		String furi = f.getAbsolutePath();
		try {
			furi = f.getCanonicalPath();
		} catch (IOException e) {
			log.error("Unexpected error while getting canonical path", e);
		}
		String uri = String.format("file://%s", furi);
		log.info("Configured to record to {}", uri);
		return uri;
	}

	public static String getName(String name, String ext) {
		return String.format(FILE_NAME_FMT, name, ext);
	}

	public static File getRecordingChunk(Long roomId, String name) {
		return new File(getStreamsSubDir(roomId), getName(name, EXTENSION_WEBM));
	}

	public static File getLanguagesDir() {
		return new File(OmFileHelper.omHome, LANGUAGES_DIR);
	}

	public static File getPublicDir() {
		return new File(OmFileHelper.omHome, PUBLIC_DIR);
	}

	public static File getPublicClipartsDir() {
		return new File(getPublicDir(), CLIPARTS_DIR);
	}

	public static File getPublicEmotionsDir() {
		return new File(getPublicDir(), EMOTIONS_DIR);
	}

	public static File getWebinfDir() {
		return new File(OmFileHelper.omHome, WEB_INF_DIR);
	}

	public static File getPersistence() {
		return getPersistence((DbType) null);
	}

	public static File getPersistence(String dbType) {
		return getPersistence(DbType.valueOf(dbType));
	}

	public static File getPersistence(DbType dbType) {
		return new File(OmFileHelper.getWebinfDir(), dbType == null ? PERSISTENCE_NAME : String.format(DB_PERSISTENCE_NAME, dbType));
	}

	public static File getConfDir() {
		return new File(OmFileHelper.omHome, CONF_DIR);
	}

	public static File getScreenSharingDir() {
		return new File(OmFileHelper.omHome, SCREENSHARING_DIR);
	}

	public static File getImagesDir() {
		return new File(OmFileHelper.omHome, IMAGES_DIR);
	}

	public static File getCssDir() {
		return new File(OmFileHelper.omHome, CSS_DIR);
	}

	public static File getCustomCss() {
		return new File(getCssDir(), CUSTOM_CSS);
	}

	public static File appendSuffix(File original, String suffix) {
		File parent = original.getParentFile();
		String name = original.getName();
		String ext = "";
		int idx = name.lastIndexOf('.');
		if (idx > -1) {
			name = name.substring(0, idx);
			ext = name.substring(idx);
		}
		return new File(parent, name + suffix + ext);
	}

	public static File getNewFile(File dir, String name, String ext) throws IOException {
		File f = new File(dir, getName(name, ext));
		int recursiveNumber = 0;
		while (f.exists()) {
			f = new File(dir, name + "_" + (recursiveNumber++) + ext);
		}
		f.createNewFile();
		return f;
	}

	public static File getNewDir(File dir, String name) throws IOException {
		File f = new File(dir, name);
		String baseName = f.getCanonicalPath();

		int recursiveNumber = 0;
		while (f.exists()) {
			f = new File(baseName + "_" + (recursiveNumber++));
		}
		f.mkdir();
		return f;
	}

	public static String getHumanSize(File dir) {
		return getHumanSize(getSize(dir));
	}

	public static String getHumanSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static long getSize(File dir) {
		long size = 0;
		if (dir.isFile()) {
			size = dir.length();
		} else {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					size += file.length();
				} else {
					size += getSize(file);
				}
			}
		}
		return size;
	}

	public static String getFileName(String name) {
		int dotidx = name.lastIndexOf('.');
		return dotidx < 0 ? "" : name.substring(0, dotidx);
	}

	public static String getFileExt(String name) {
		int dotidx = name.lastIndexOf('.');
		return dotidx < 0 ? "" : name.substring(dotidx + 1).toLowerCase();
	}
}
