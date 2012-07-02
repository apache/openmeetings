package org.openmeetings.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.apache.commons.transaction.util.FileHelper;
import org.openmeetings.app.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OmFileHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(OmFileHelper.class, OpenmeetingsVariables.webAppRootKey);

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
	
	private static final String INSTALL_FILE = "install.xml";
	
	public static final String SCREENSHARING_DIR = "screensharing";
	
	public static final String profilesPrefix = "profile_";
	public static final String nameOfLanguageFile = "languages.xml";
	public static final String nameOfCountriesFile = "countries.xml";
	public static final String nameOfTimeZoneFile = "timezones.xml";
	public static final String nameOfErrorFile = "errorvalues.xml";
	public static final String libraryFileName = "library.xml";

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
	
	public static File getUploadProfilesUserDir(Long users_id) {
		return getDir(getUploadProfilesDir(), OmFileHelper.profilesPrefix + users_id);
	}
	
	public static File getUploadProfilesUserDir(String users_id) {
		return getDir(getUploadProfilesDir(), OmFileHelper.profilesPrefix + users_id);
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
	
	public static File getUploadTempDir() {
		return new File(OmFileHelper.OM_HOME, UPLOAD_TEMP_DIR);
	}
	
	public static File getUploadTempFilesDir() {
		return getDir(getUploadTempDir(), FILES_DIR);
	}
	
	public static File getUploadTempProfilesDir() {
		return getDir(getUploadTempDir(), PROFILES_DIR);
	}
	
	public static File getUploadTempProfilesUserDir(Long users_id) {
		return getDir(getUploadTempProfilesDir(), OmFileHelper.profilesPrefix + users_id);
	}
	
	public static File getUploadTempRoomDir(String roomName) {
		return getDir(getUploadTempDir(), roomName);
	}
	
	public static File getStreamsDir() {
		return new File(OmFileHelper.OM_HOME, STREAMS_DIR);
	}
	
	public static File getStreamsFilesDir() {
		return getDir(getStreamsDir(), FILES_DIR);
	}
	
	public static File getStreamsHibernateDir() {
		return getDir(getStreamsDir(), HIBERNATE_DIR);
	}
	
	public static File getStreamsSubDir(Long id) {
		return getDir(getStreamsDir(), id.toString());
	}
	
	public static File getStreamsSubDir(String name) {
		return getDir(getStreamsDir(), name);
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
		long size = getSize(dir);
	
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
		InputStream in = null;
		try {
			in = new FileInputStream(f1);
			FileHelper.copy(in, out);
			log.debug("File copied.");
		} catch (Exception e) {
			log.error("[copyfile(File, File)]", e);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
