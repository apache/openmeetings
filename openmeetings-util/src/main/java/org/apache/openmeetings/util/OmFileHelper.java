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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OmFileHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(OmFileHelper.class, OpenmeetingsVariables.webAppRootKey);

	/**
	 * This variable needs to point to the openmeetings webapp directory
	 */
	private static File OM_HOME = null;
	private static final String UPLOAD_DIR = "upload";
	private static final String UPLOAD_TEMP_DIR = "uploadtemp";
	private static final String FILES_DIR = "files";
	private static final String PUBLIC_DIR = "public";
	private static final String CLIPARTS_DIR = "cliparts";
	private static final String WEB_INF_DIR = "WEB-INF";
	private static final String PROFILES_DIR = "profiles";
	private static final String STREAMS_DIR = "streams";
	private static final String EMOTIONS_DIR = "emoticons";
	private static final String LANGUAGES_DIR = "languages";
	private static final String IMPORT_DIR = "import";
	private static final String HIBERNATE_DIR = "hibernate";
	private static final String CONF_DIR = "conf";
	private static final String BACKUP_DIR = "backup";
	private static final String DEFAULT_DIR = "default";
	private static final String IMAGES_DIR = "images";
	private static final String WML_DIR = "stored";
	
	private static final String INSTALL_FILE = "install.xml";
	
	public static final String SCREENSHARING_DIR = "screensharing";
	
	public static final String PERSISTENCE_NAME = "classes/META-INF/persistence.xml";
	public static final String DB_PERSISTENCE_NAME = "classes/META-INF/%s_persistence.xml";
	public static final String profilesPrefix = "profile_";
	public static final String nameOfLanguageFile = "languages.xml";
	public static final String nameOfErrorFile = "errorvalues.xml";
	public static final String libraryFileName = "library.xml";
	public static final String defaultProfileImageName = "profile.jpg";
	public static final String profileFileName = "profile";
	public static final String recordingFileName = "flvRecording_";
	public static final String profileImagePrefix = "_profile_";
	public static final String chatImagePrefix = "_chat_";
	public static final String bigImagePrefix = "_big_";
	public static final String thumbImagePrefix = "_thumb_";
	public static final String dashboardFile = "dashboard.xml";
	public static final String FLV_EXTENSION = ".flv";
	public static final String MP4_EXTENSION = ".mp4";
	public static final String OGG_EXTENSION = ".ogg";
	public static final String JPG_EXTENTION = ".jpg";

	public static void setOmHome(File omHome) {
		OmFileHelper.OM_HOME = omHome;
	}
	
	public static void setOmHome(String omHome) {
		OmFileHelper.OM_HOME = new File(omHome);
	}
	
	public static File getRootDir() {
		//FIXME hack !!!!
		return getOmHome().getParentFile().getParentFile();
	}
	
	public static File getOmHome() {
		return OmFileHelper.OM_HOME;
	}
	
	private static File getDir(File parent, String name) {
		File f = new File(parent, name);
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}
	
	public static File getUploadDir() {
		return new File(OmFileHelper.OM_HOME, UPLOAD_DIR);
	}
	
	public static File getUploadFilesDir() {
		return getDir(getUploadDir(), FILES_DIR);
	}
	
	public static File getUploadProfilesDir() {
		return getDir(getUploadDir(), PROFILES_DIR);
	}
	
	public static File getUploadProfilesUserDir(Long userId) {
		return getDir(getUploadProfilesDir(), profilesPrefix + userId);
	}
	
	public static File getUploadProfilesUserDir(String userId) {
		return getDir(getUploadProfilesDir(), profilesPrefix + userId);
	}
	
	public static File getDefaultProfilePicture() {
		return new File(getImagesDir(), defaultProfileImageName);
	}
	
	public static File getUserProfilePicture(Long userId, String uri) {
		File img = new File(getUploadProfilesUserDir(userId), profileImagePrefix + uri);
		if (!img.exists()) {
			img = getDefaultProfilePicture();
		}
		return img;
	}
	
	public static File getUserDashboard(Long userId) {
		return new File(getUploadProfilesUserDir(userId), dashboardFile);
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
	
	public static File getUploadTempDir() {
		return getDir(OmFileHelper.OM_HOME, UPLOAD_TEMP_DIR);
	}
	
	public static File getUploadTempFilesDir() {
		return getDir(getUploadTempDir(), FILES_DIR);
	}
	
	public static File getUploadTempProfilesDir() {
		return getDir(getUploadTempDir(), PROFILES_DIR);
	}
	
	public static File getUploadTempProfilesUserDir(Long userId) {
		return getDir(getUploadTempProfilesDir(), OmFileHelper.profilesPrefix + userId);
	}
	
	public static File getUploadTempRoomDir(String roomName) {
		return getDir(getUploadTempDir(), roomName);
	}
	
	public static File getStreamsDir() {
		return getDir(OmFileHelper.OM_HOME, STREAMS_DIR);
	}
	
	public static File getStreamsHibernateDir() {
		return getDir(getStreamsDir(), HIBERNATE_DIR);
	}
	
	public static File getRecording(String name) {
		return new File(getDir(getStreamsDir(), HIBERNATE_DIR), name);
	}
	
	public static boolean isRecordingExists(String name) {
		try {
			File f = new File(getDir(getStreamsDir(), HIBERNATE_DIR), name);
			return f.exists() && f.isFile();
		} catch (Exception e) {
			//no-op
		}
		return false;
	}
	
	public static File getMp4Recording(String name) {
		return getRecording(name + MP4_EXTENSION);
	}
	
	public static File getOggRecording(String name) {
		return getRecording(name + OGG_EXTENSION);
	}
	
	public static File getStreamsSubDir(Long id) {
		return getDir(getStreamsDir(), id.toString());
	}
	
	public static File getStreamsSubDir(String name) {
		return getDir(getStreamsDir(), name);
	}
	
	public static File getRecordingMetaData(Long roomId, String name) {
		return new File(getStreamsSubDir(roomId), name + FLV_EXTENSION);
	}
	
	public static File getLanguagesDir() {
		return new File(OmFileHelper.OM_HOME, LANGUAGES_DIR);
	}
	
	public static File getPublicDir() {
		return new File(OmFileHelper.OM_HOME, PUBLIC_DIR);
	}
	
	public static File getPublicClipartsDir() {
		return new File(getPublicDir(), CLIPARTS_DIR);
	}
	
	public static File getPublicEmotionsDir() {
		return new File(getPublicDir(), EMOTIONS_DIR);
	}
	
	public static File getWebinfDir() {
		return new File(OmFileHelper.OM_HOME, WEB_INF_DIR);
	}
	
	public static File getPersistence() {
		return getPersistence((DbType)null);
	}
	
	public static File getPersistence(String dbType) {
		return getPersistence(DbType.valueOf(dbType));
	}
	
	public static File getPersistence(DbType dbType) {
		return new File(OmFileHelper.getWebinfDir(), dbType == null ? PERSISTENCE_NAME : String.format(DB_PERSISTENCE_NAME, dbType));
	}
	
	public static File getConfDir() {
		return new File(OmFileHelper.OM_HOME, CONF_DIR);
	}
	
	public static File getInstallFile() {
		return new File(getConfDir(), INSTALL_FILE);
	}
	
	public static File getScreenSharingDir() {
		return new File(OmFileHelper.OM_HOME, SCREENSHARING_DIR);
	}

	public static File getDefaultDir() {
		return new File(OmFileHelper.OM_HOME, DEFAULT_DIR);
	}

	public static File getImagesDir() {
		return new File(OmFileHelper.OM_HOME, IMAGES_DIR);
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
	
	//FIXME need to be generalized
	public static File getNewFile(File dir, String name, String ext) throws IOException {
		File f = new File(dir, name + ext);
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
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static long getSize(File dir) {
		long size = 0;
		if (dir.isFile()) {
			size = dir.length();
		} else {
			File[] subFiles = dir.listFiles();
	
			for (File file : subFiles) {
				if (file.isFile()) {
					size += file.length();
				} else {
					size += getSize(file);
				}
	
			}
		}
		return size;
	}

	public static void copyFile(String sourceFile, String targetFile) throws IOException {
		FileHelper.copy(new File(sourceFile), new File(targetFile));
	}
	
	public static void copyFile(File f1, OutputStream out) throws IOException {
		try (InputStream in = new FileInputStream(f1)) {
			FileHelper.copy(in, out);
			log.debug("File copied.");
		} catch (Exception e) {
			log.error("[copyfile(File, File)]", e);
		}
	}
}
