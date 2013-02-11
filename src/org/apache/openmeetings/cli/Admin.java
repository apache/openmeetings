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
package org.apache.openmeetings.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContextEvent;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.transaction.util.FileHelper;
import org.apache.openjpa.jdbc.meta.MappingTool;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.documents.InstallationDocumentHandler;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.persistence.beans.files.FileExplorerItem;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.servlet.outputhandler.BackupExport;
import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.apache.openmeetings.utils.ImportHelper;
import org.apache.openmeetings.utils.OMContextListener;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.mail.MailUtil;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class Admin {
	private static final Logger log = Red5LoggerFactory.getLogger(Admin.class);
	
	private boolean verbose = false;
	private InstallationConfig cfg = null;
	private Options opts = null;
	private CommandLine cmdl = null;
	private ClassPathXmlApplicationContext ctx = null;
	private final static String PERSISTENCE_NAME = "classes/META-INF/persistence.xml";

	private Admin() {
		cfg = new InstallationConfig();
		opts = buildOptions();
	}
	
	private Options buildOptions() {
		Options options = new Options();
		OptionGroup group = new OptionGroup()
			.addOption(new OmOption("h", 0, "h", "help", false, "prints this message"))
			.addOption(new OmOption("b", 1, "b", "backup", false, "Backups OM"))
			.addOption(new OmOption("r", 2, "r", "restore", false, "Restores OM"))
			.addOption(new OmOption("i", 3, "i", "install", false, "Fill DB table, and make OM usable"))
			.addOption(new OmOption("l", 3, "l", "languages", false, "Reimport All language files into DB"))
			.addOption(new OmOption("f", 4, "f", "files", false, "File operations - statictics/cleanup"));
		group.setRequired(true); 
		options.addOptionGroup(group);
		//general
		options.addOption(new OmOption(null, "v", "verbose", false, "verbose error messages"));
		//backup/restore
		options.addOption(new OmOption("b", null, "exclude-files", false, "should backup exclude files [default: include]", true));
		options.addOption(new OmOption("b,r,i", "file", null, true, "file used for backup/restore/install", "b"));
		//install
		options.addOption(new OmOption("i", "user", null, true, "Login name of the default user, minimum " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " characters (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "email", null, true, "Email of the default user (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "group", null, true, "The name of the default user group (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "tz", null, true, "Default server time zone, and time zone for the selected user (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", null, "password", true, "Password of the default user, minimum " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " characters (will be prompted if not set)", true));
		options.addOption(new OmOption("i", null, "system-email-address", true, "System e-mail address [default: " + cfg.mailReferer + "]", true));
		options.addOption(new OmOption("i", null, "smtp-server", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpServer + "]", true));
		options.addOption(new OmOption("i", null, "smtp-port", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpPort + "]", true));
		options.addOption(new OmOption("i", null, "email-auth-user", true, "Email auth username (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, "email-auth-pass", true, "Email auth password (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, "email-use-tls", false, "Is secure e-mail connection [default: no]", true));
		options.addOption(new OmOption("i", null, "skip-default-rooms", false, "Do not create default rooms [created by default]", true));
		options.addOption(new OmOption("i", null, "disable-frontend-register", false, "Do not allow front end register [allowed by default]", true));

		options.addOption(new OmOption("i", null, "db-type", true, "The type of the DB to be used", true));
		options.addOption(new OmOption("i", null, "db-host", true, "DNS name or IP address of database", true));
		options.addOption(new OmOption("i", null, "db-port", true, "Database port", true));
		options.addOption(new OmOption("i", null, "db-name", true, "The name of Openmeetings database", true));
		options.addOption(new OmOption("i", null, "db-user", true, "User with write access to the DB specified", true));
		options.addOption(new OmOption("i", null, "db-pass", true, "Password of the user with write access to the DB specified", true));
		options.addOption(new OmOption("i", null, "drop", false, "Drop database before installation", true));
		options.addOption(new OmOption("i", null, "force", false, "Install without checking the existence of old data in the database.", true));
		//languages
		options.addOption(new OmOption("l", "lang", "language", true, "Single language to be imported (id or name)", true));
		//files
		options.addOption(new OmOption("f", null, "cleanup", false, "Should intermediate files be clean up", true));
		
		return options;
	}
	
	private enum Command {
		install
		, backup
		, restore
		, languages
		, files
		, usage
	}
	
	private void usage() {
		OmHelpFormatter formatter = new OmHelpFormatter();
		formatter.setWidth(100);
		formatter.printHelp("admin", "Please specify one of the required parameters.", opts, "Examples:\n" +
				"\t./admin.sh -b\n" +
				"\t./admin.sh -i -v -file backup_31_07_2012_12_07_51.zip --drop\n" +
				"\t./admin.sh -i -v -user admin -email someemail@gmail.com -tz \"Asia/Tehran\" -group \"yourgroup\" --db-type mysql --db-host localhost");
	}
	
	private void handleError(String msg, Exception e) {
		handleError(msg, e, false);
	}
	
	private void handleError(String msg, Exception e, boolean printUsage) {
		if (printUsage) {
			usage();
		}
		if (verbose) {
			log.error(msg, e);
		} else {
			log.error(msg + " " + e.getMessage());
		}
		System.exit(1);
	}
	
	private ClassPathXmlApplicationContext getApplicationContext(final String ctxName) {
		if (ctx == null) {
			OMContextListener omcl = new OMContextListener();
			omcl.contextInitialized(new ServletContextEvent(new DummyServletContext(ctxName)));
			try {
				ctx = new ClassPathXmlApplicationContext("openmeetings-applicationContext.xml");
			} catch (Exception e) {
				handleError("Unable to obtain application context", e);
			}
			SchedulerFactoryBean sfb = ctx.getBean(SchedulerFactoryBean.class);
			try {
				sfb.getScheduler().shutdown(false);
			} catch (Exception e) {
				handleError("Unable to shutdown schedulers", e);
			}
		}
		return ctx;
	}
	
	private void process(String[] args) {
		String ctxName = System.getProperty("context", "openmeetings");
		File home = new File(System.getenv("RED5_HOME"));
		OmFileHelper.setOmHome(new File(new File(home, "webapps"), ctxName));
		
		Parser parser = new PosixParser();
		try {
			cmdl = parser.parse(opts, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			usage();
			System.exit(1);
		}
		verbose = cmdl.hasOption('v');

		Command cmd = Command.usage;
		if (cmdl.hasOption('i')) {
			cmd = Command.install;
		} else if (cmdl.hasOption('b')) {
			cmd = Command.backup;
		} else if (cmdl.hasOption('r')) {
			cmd = Command.restore;
		} else if (cmdl.hasOption('l')) {
			cmd = Command.languages;
		} else if (cmdl.hasOption('f')) {
			cmd = Command.files;
		}

		String file = cmdl.getOptionValue("file", "");
		switch(cmd) {
			case install:
				try {
					if (cmdl.hasOption("file") && (cmdl.hasOption("user") || cmdl.hasOption("email") || cmdl.hasOption("group"))) {
						System.out.println("Please specify even 'file' option or 'admin user'.");
						System.exit(1);
					}
					boolean force = cmdl.hasOption("force");
					if (cmdl.hasOption("skip-default-rooms")) {
						cfg.createDefaultRooms = "0";
					}
					if (cmdl.hasOption("disable-frontend-register")) {
						cfg.allowFrontendRegister = "0";
					}
					if (cmdl.hasOption("system-email-address")) {
						cfg.mailReferer = cmdl.getOptionValue("system-email-address");
					}
					if (cmdl.hasOption("smtp-server")) {
						cfg.smtpServer = cmdl.getOptionValue("smtp-server");
					}
					if (cmdl.hasOption("smtp-port")) {
						cfg.smtpPort = cmdl.getOptionValue("smtp-port");
					}
					if (cmdl.hasOption("email-auth-user")) {
						cfg.mailAuthName = cmdl.getOptionValue("email-auth-user");
					}
					if (cmdl.hasOption("email-auth-pass")) {
						cfg.mailAuthPass = cmdl.getOptionValue("email-auth-pass");
					}
					if (cmdl.hasOption("email-use-tls")) {
						cfg.mailUseTls = "1";
					}
					ConnectionProperties connectionProperties = new ConnectionProperties();
					File conf = new File(OmFileHelper.getWebinfDir(), PERSISTENCE_NAME);
					if (!conf.exists() || cmdl.hasOption("db-type") || cmdl.hasOption("db-host") || cmdl.hasOption("db-port") || cmdl.hasOption("db-name") || cmdl.hasOption("db-user") || cmdl.hasOption("db-pass")) {
						String dbType = cmdl.getOptionValue("db-type", "derby");
						File srcConf = new File(OmFileHelper.getWebinfDir(), "classes/META-INF/" + dbType + "_persistence.xml");
						ConnectionPropertiesPatcher.getPatcher(dbType).patch(
								srcConf
								, conf
								, cmdl.getOptionValue("db-host", "localhost")
								, cmdl.getOptionValue("db-port", null)
								, cmdl.getOptionValue("db-name", null)
								, cmdl.getOptionValue("db-user", null)
								, cmdl.getOptionValue("db-pass", null)
								, connectionProperties
								);
					} else {
						//get properties from existent persistence.xml
						connectionProperties = ConnectionPropertiesPatcher.getConnectionProperties(conf);
					}
					if (cmdl.hasOption("file")) {
						File backup = checkRestoreFile(file);
						dropDB(connectionProperties);
						
						ImportInitvalues importInit = getApplicationContext(ctxName).getBean(ImportInitvalues.class);
						importInit.loadSystem(cfg, force); 
						restoreOm(ctxName, backup);
					} else {
						AdminUserDetails admin = checkAdminDetails(ctxName);
						dropDB(connectionProperties);
						
						ImportInitvalues importInit = getApplicationContext(ctxName).getBean(ImportInitvalues.class);
						importInit.loadAll(cfg, admin.login, admin.pass, admin.email, admin.group, admin.tz, force);
					}					
					
					InstallationDocumentHandler.createDocument(3);
				} catch(Exception e) {
					handleError("Install failed", e);
				}
				break;
			case backup:
				try {
					File f;
					if (!cmdl.hasOption("file")) {
						file = "backup_" + CalendarPatterns.getTimeForStreamId(new Date()) + ".zip";
						f = new File(home, file);
						System.out.println("File name was not specified, '" + file + "' will be used");
					} else {
						f = new File(file);
					}
					boolean includeFiles = Boolean.parseBoolean(cmdl.getOptionValue("exclude-files", "true"));
					File backup_dir = new File(OmFileHelper.getUploadTempDir(), "" + System.currentTimeMillis());
					backup_dir.mkdirs();
					
					BackupExport export = getApplicationContext(ctxName).getBean(BackupExport.class);
					export.performExport(f, backup_dir, includeFiles);
					FileHelper.removeRec(backup_dir);
					backup_dir.delete();
				} catch (Exception e) {
					handleError("Backup failed", e);
				}
				break;
			case restore:
				try {
					restoreOm(ctxName, checkRestoreFile(file));
				} catch (Exception e) {
					handleError("Restore failed", e);
				}
				break;
			case languages:
				System.out.println("All language files will be reimported");
				try {
					ImportInitvalues importInit = getApplicationContext(ctxName).getBean(ImportInitvalues.class);
					if (cmdl.hasOption("lang")) {
						String lang = cmdl.getOptionValue("lang");
						System.out.println("Only '" + lang + "' language will be reimported");
						try {
							int id = Integer.parseInt(lang);
							importInit.loadLanguagesFile(id);
						} catch (NumberFormatException e) {
							importInit.loadLanguagesFile(lang);
						}
					} else {
						System.out.println("All language files will be reimported");
						importInit.loadLanguagesFiles();
					}
				} catch (Exception e) {
					handleError("Language reimport failed", e);
				}
				break;
			case files:
				try {
					boolean cleanup = cmdl.hasOption("cleanup");
					if (cleanup) {
						System.out.println("WARNING: all intermadiate files will be clean up!");
					}
					StringBuilder report = new StringBuilder();
					report.append("Temporary files allocates: ").append(OmFileHelper.getHumanSize(OmFileHelper.getUploadTempDir())).append("\n");
					{ //UPLOAD
						long sectionSize = OmFileHelper.getSize(OmFileHelper.getUploadDir());
						report.append("Upload totally allocates: ").append(OmFileHelper.getHumanSize(sectionSize)).append("\n");
						//Profiles
						File profiles = OmFileHelper.getUploadProfilesDir();
						long invalid = 0;
						long deleted = 0;
						ClassPathXmlApplicationContext ctx = getApplicationContext(ctxName);
						UsersDao udao = ctx.getBean(UsersDao.class);
						for (File profile : profiles.listFiles()) {
							long pSize = OmFileHelper.getSize(profile);
							long userId = getUserIdByProfile(profile.getName());
							User u = udao.get(userId);
							if (profile.isFile() || userId < 0 || u == null) {
								if (cleanup) {
									FileHelper.removeRec(profile);
								} else {
									invalid += pSize;
								}
							} else if (u.getDeleted()) {
								if (cleanup) {
									FileHelper.removeRec(profile);
								} else {
									deleted += pSize;
								}
							}
						}
						long missing = 0;
						for (User u : udao.getAllUsersDeleted()) {
							if (!u.getDeleted() && !new File(OmFileHelper.getUploadProfilesUserDir(u.getUser_id()), u.getPictureuri()).exists()) {
								missing++;
							}
						}
						long size = OmFileHelper.getSize(profiles);
						long restSize = sectionSize - size;
						report.append("\t\tprofiles: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						report.append("\t\t\tinvalid: ").append(OmFileHelper.getHumanSize(invalid)).append("\n");
						report.append("\t\t\tdeleted: ").append(OmFileHelper.getHumanSize(deleted)).append("\n");
						report.append("\t\t\tmissing count: ").append(missing).append("\n");
						size = OmFileHelper.getSize(OmFileHelper.getUploadImportDir());
						restSize -= size;
						report.append("\t\timport: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						size = OmFileHelper.getSize(OmFileHelper.getUploadBackupDir());
						restSize -= size;
						report.append("\t\tbackup: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						//Files
						File files = OmFileHelper.getUploadFilesDir();
						size = OmFileHelper.getSize(files);
						restSize -= size;
						FileExplorerItemDao fileDao = ctx.getBean(FileExplorerItemDao.class);
						invalid = 0;
						deleted = 0;
						for (File f : files.listFiles()) {
							long fSize = OmFileHelper.getSize(f);
							FileExplorerItem item = fileDao.getFileExplorerItemsByHash(f.getName());
							if (item == null) {
								if (cleanup) {
									FileHelper.removeRec(f);
								} else {
									invalid += fSize;
								}
							} else if (item.getDeleted()) {
								if (cleanup) {
									FileHelper.removeRec(f);
								} else {
									deleted += fSize;
								}
							}
						}
						missing = 0;
						for (FileExplorerItem item : fileDao.getFileExplorerItems()) {
							if (!item.getDeleted() && !new File(files, item.getFileHash()).exists()) {
								missing++;
							}
						}
						report.append("\t\tfiles: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						report.append("\t\t\tinvalid: ").append(OmFileHelper.getHumanSize(invalid)).append("\n");
						report.append("\t\t\tdeleted: ").append(OmFileHelper.getHumanSize(deleted)).append("\n");
						report.append("\t\t\tmissing count: ").append(missing).append("\n");
						report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
					}
					{ //STREAMS
						File streamsDir = OmFileHelper.getStreamsDir();
						File hibernateDir = OmFileHelper.getStreamsHibernateDir();
						if (cleanup) {
							String hiberPath = hibernateDir.getCanonicalPath();
							for (File f : streamsDir.listFiles()) {
								if (!f.getCanonicalPath().equals(hiberPath)) {
									FileHelper.removeRec(f);
								}
							}
						}
						long sectionSize = OmFileHelper.getSize(streamsDir);
						report.append("Recordings allocates: ").append(OmFileHelper.getHumanSize(sectionSize)).append("\n");
						long size = OmFileHelper.getSize(hibernateDir);
						long restSize = sectionSize - size;
						FlvRecordingDao recordDao = ctx.getBean(FlvRecordingDao.class);
						long[] params = {0, 0}; // [0] == deleted [1] == missing
						for (FlvRecording rec : recordDao.getAllFlvRecordings()) {
							checkRecordingFile(hibernateDir, rec.getFileHash(), rec.getDeleted(), params, cleanup);
							checkRecordingFile(hibernateDir, rec.getAlternateDownload(), rec.getDeleted(), params, cleanup);
							checkRecordingFile(hibernateDir, rec.getPreviewImage(), rec.getDeleted(), params, cleanup);
						}
						long invalid = 0;
						for (File f : hibernateDir.listFiles()) {
							if (f.isFile() && f.getName().endsWith(".flv")) {
								FlvRecording rec = recordDao.getRecordingByHash(f.getName());
								if (rec == null) {
									if (cleanup) {
										FileHelper.removeRec(f);
									} else {
										invalid += f.length();
									}
									String name = f.getName().substring(0, f.getName().length() - 5);
									File rfa = new File(hibernateDir, name + ".avi");
									if (rfa.exists()) {
										if (cleanup) {
											FileHelper.removeRec(rfa);
										} else {
											invalid += rfa.length();
										}
									}
									File rfj = new File(hibernateDir, name + ".jpg");
									if (rfj.exists()) {
										if (cleanup) {
											FileHelper.removeRec(rfj);
										} else {
											invalid += rfj.length();
										}
									}
								}
							}
						}
						report.append("\t\tfinal: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						report.append("\t\t\tinvalid: ").append(OmFileHelper.getHumanSize(invalid)).append("\n");
						report.append("\t\t\tdeleted: ").append(OmFileHelper.getHumanSize(params[0])).append("\n");
						report.append("\t\t\tmissing count: ").append(params[1]).append("\n");
						report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
					}
					System.out.println(report);
				} catch (Exception e) {
					handleError("Files failed", e);
				}
				break;
			case usage:
			default:
				usage();
				break;
		}
		
		System.out.println("... Done");
		System.exit(0);
	}
	
	private void checkRecordingFile(File hibernateDir, String name, boolean deleted, long[] params, boolean cleanup) {
		File flv = name != null ? new File(hibernateDir, name) : null;
		if (flv != null) {
			if (flv.exists() && flv.isFile()) {
				if (deleted) {
					params[0] += flv.length();
					if (cleanup) {
						FileHelper.removeRec(flv);
					}
				}
			} else {
				params[1]++;
			}
		}
	}
	
	private long getUserIdByProfile(String name) {
		long result = -1;
		if (name.startsWith(OmFileHelper.profilesPrefix)) {
			try {
				result = Long.parseLong(name.substring(OmFileHelper.profilesPrefix.length()));
			} catch (Exception e) {
				//noop
			}
		}
		return result;
	}
	
	private class AdminUserDetails {
		String login = null;
		String email = null;
		String group = null;
		String pass = null;
		String tz = null;
	}
	
	private AdminUserDetails checkAdminDetails(String ctxName) throws Exception {
		AdminUserDetails admin = new AdminUserDetails();
		admin.login = cmdl.getOptionValue("user");
		admin.email = cmdl.getOptionValue("email");
		admin.group = cmdl.getOptionValue("group");
		if (admin.login == null || admin.login.length() < InstallationConfig.USER_LOGIN_MINIMUM_LENGTH) {
			System.out.println("User login was not provided, or too short, should be at least " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " character long.");
			System.exit(1);
		}
		
		try {
			if (!MailUtil.matches(admin.email)) {
			    throw new AddressException("Invalid address");
			}
			new InternetAddress(admin.email, true);
		} catch (AddressException ae) {
			System.out.println("Please provide non-empty valid email: '" + admin.email + "' is not valid.");
			System.exit(1);
		}
		if (admin.group == null || admin.group.length() < 1) {
			System.out.println("User group was not provided, or too short, should be at least 1 character long: " + admin.group);
			System.exit(1);
		}
		admin.pass = cmdl.getOptionValue("password");
		if (checkPassword(admin.pass)) {
			System.out.print("Please enter password for the user '" + admin.login + "':");
			admin.pass = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if (checkPassword(admin.pass)) {
				System.out.println("Password was not provided, or too short, should be at least " + InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH + " character long.");
				System.exit(1);
			}
		}
		ImportInitvalues importInit = getApplicationContext(ctxName).getBean(ImportInitvalues.class);
		Map<String, String> tzMap = ImportHelper.getAllTimeZones(importInit.getTimeZones());
		admin.tz = null;
		if (cmdl.hasOption("tz")) {
			admin.tz = cmdl.getOptionValue("tz");
			admin.tz = tzMap.containsKey(admin.tz) ? admin.tz : null;
		}
		if (admin.tz == null) {
			System.out.println("Please enter timezone, Possible timezones are:");
			
			for (String tzIcal : tzMap.keySet()) {
				System.out.println(String.format("%1$-25s%2$s", "\"" + tzIcal + "\"", tzMap.get(tzIcal)));
			}
			System.exit(1);
		}
		return admin;
	}
	
	private boolean checkPassword(String pass) {
		return (pass == null || pass.length() < InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH);
	}
	
	public static void dropDB() throws Exception {
		File conf = new File(OmFileHelper.getWebinfDir(), PERSISTENCE_NAME);
		ConnectionProperties connectionProperties = ConnectionPropertiesPatcher.getConnectionProperties(conf);
		immediateDropDB(connectionProperties);
	}
	
	private void dropDB(ConnectionProperties props) throws Exception {
		if(cmdl.hasOption("drop")) {
			immediateDropDB(props);
		}
	}
	
	private static void immediateDropDB(ConnectionProperties props) throws Exception {
		String[] args = {
				"-schemaAction", "retain,drop"
				, "-properties", new File(OmFileHelper.getWebinfDir(), PERSISTENCE_NAME).getCanonicalPath()
				, "-connectionDriverName", props.getDriver()
				, "-connectionURL", props.getURL()
				, "-connectionUserName", props.getLogin()
				, "-connectionPassword", props.getPassword()
				, "-ignoreErrors", "true"};
		MappingTool.main(args);
	}
	
	private File checkRestoreFile(String file) {
		File backup = new File(file);
		if (!cmdl.hasOption("file") || !backup.exists() || !backup.isFile()) {
			System.out.println("File should be specified, and point the existent zip file");
			usage();
			System.exit(1);
		}
		
		return backup;
	}
	
	private void restoreOm(String ctxName, File backup) {
		try {
			BackupImportController importCtrl = getApplicationContext(ctxName).getBean(BackupImportController.class);
			importCtrl.performImport(new FileInputStream(backup));
		} catch (Exception e) {
			handleError("Restore failed", e);
		}
	}
	
	public static void main(String[] args) {
		new Admin().process(args);
	}
}
