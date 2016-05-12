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

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.db.util.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.db.util.UserHelper.invalidPassword;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.transaction.util.FileHelper;
import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
import org.apache.openjpa.jdbc.schema.SchemaTool;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.lib.log.LogFactoryImpl.LogImpl;
import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.ProgressHolder;
import org.apache.openmeetings.core.ldap.LdapLoginManagement;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.installation.InstallationDocumentHandler;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.ImportHelper;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.mail.MailUtil;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.string.StringValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class Admin {
	private static final Logger log = Red5LoggerFactory.getLogger(Admin.class);
	
	private boolean verbose = false;
	private InstallationConfig cfg = null;
	private Options opts = null;
	private CommandLine cmdl = null;
	private WebApplicationContext ctx = null;

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
			.addOption(new OmOption("l", 3, "l", "LDAP", false, "Import LDAP users into DB"))
			.addOption(new OmOption("f", 4, "f", "files", false, "File operations - statictics/cleanup"));
		group.setRequired(true); 
		options.addOptionGroup(group);
		//general
		options.addOption(new OmOption(null, "v", "verbose", false, "verbose error messages"));
		//backup/restore
		options.addOption(new OmOption("b", null, "exclude-files", false, "should backup exclude files [default: include]", true));
		options.addOption(new OmOption("b,r,i", "file", null, true, "file used for backup/restore/install", "b"));
		//install
		options.addOption(new OmOption("i", "user", null, true, "Login name of the default user, minimum " + USER_LOGIN_MINIMUM_LENGTH + " characters (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "email", null, true, "Email of the default user (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "group", null, true, "The name of the default user group (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", "tz", null, true, "Default server time zone, and time zone for the selected user (mutually exclusive with 'file')"));
		options.addOption(new OmOption("i", null, "password", true, "Password of the default user, minimum " + USER_PASSWORD_MINIMUM_LENGTH + " characters (will be prompted if not set)", true));
		options.addOption(new OmOption("i", null, "system-email-address", true, "System e-mail address [default: " + cfg.mailReferer + "]", true));
		options.addOption(new OmOption("i", null, "smtp-server", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpServer + "]", true));
		options.addOption(new OmOption("i", null, "smtp-port", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpPort + "]", true));
		options.addOption(new OmOption("i", null, "email-auth-user", true, "Email auth username (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, "email-auth-pass", true, "Email auth password (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, "email-use-tls", false, "Is secure e-mail connection [default: no]", true));
		options.addOption(new OmOption("i", null, "skip-default-rooms", false, "Do not create default rooms [created by default]", true));
		options.addOption(new OmOption("i", null, "disable-frontend-register", false, "Do not allow front end register [allowed by default]", true));
		options.addOption(new OmOption("i", null, "default-language", true, "Default system language as int [1 by default]", true));

		options.addOption(new OmOption("i", null, "db-type", true, "The type of the DB to be used", true));
		options.addOption(new OmOption("i", null, "db-host", true, "DNS name or IP address of database", true));
		options.addOption(new OmOption("i", null, "db-port", true, "Database port", true));
		options.addOption(new OmOption("i", null, "db-name", true, "The name of Openmeetings database", true));
		options.addOption(new OmOption("i", null, "db-user", true, "User with write access to the DB specified", true));
		options.addOption(new OmOption("i", null, "db-pass", true, "Password of the user with write access to the DB specified", true));
		options.addOption(new OmOption("i", null, "drop", false, "Drop database before installation", true));
		options.addOption(new OmOption("i", null, "force", false, "Install without checking the existence of old data in the database.", true));
		//files
		options.addOption(new OmOption("f", null, "cleanup", false, "Should intermediate files be clean up", true));
		//ldap
		options.addOption(new OmOption("l", "d", "domain-id", true, "LDAP domain Id", false));
		options.addOption(new OmOption("l", null, "print-only", false, "Print users found instead of importing", true));
		return options;
	}
	
	private enum Command {
		install
		, backup
		, restore
		, files
		, ldap
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
	
	private WebApplicationContext getApplicationContext() {
		if (ctx == null) {
			Long lngId = StringValue.valueOf(cfg.defaultLangId).toLong(1L);
			ctx = getWebApplicationContext(((WebApplication)ensureApplication(lngId)).getServletContext());
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
		OpenmeetingsVariables.wicketApplicationName = ctxName;
		File home = new File(System.getenv("RED5_HOME"));
		OmFileHelper.setOmHome(new File(new File(home, "webapps"), ctxName));
		
		CommandLineParser parser = new DefaultParser();
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
		} else if (cmdl.hasOption('f')) {
			cmd = Command.files;
		} else if (cmdl.hasOption('l')) {
			cmd = Command.ldap;
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
						cfg.smtpPort = Integer.valueOf(cmdl.getOptionValue("smtp-port"));
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
					if (cmdl.hasOption("default-language")) {
						cfg.defaultLangId = cmdl.getOptionValue("default-language");
					}
					ConnectionProperties connectionProperties = new ConnectionProperties();
					File conf = OmFileHelper.getPersistence();
					if (!conf.exists() || cmdl.hasOption("db-type") || cmdl.hasOption("db-host") || cmdl.hasOption("db-port") || cmdl.hasOption("db-name") || cmdl.hasOption("db-user") || cmdl.hasOption("db-pass")) {
						String dbType = cmdl.getOptionValue("db-type", "derby");
						connectionProperties = ConnectionPropertiesPatcher.patch(dbType
								, cmdl.getOptionValue("db-host", "localhost")
								, cmdl.getOptionValue("db-port", null)
								, cmdl.getOptionValue("db-name", null)
								, cmdl.getOptionValue("db-user", null)
								, cmdl.getOptionValue("db-pass", null)
								);
					} else {
						//get properties from existent persistence.xml
						connectionProperties = ConnectionPropertiesPatcher.getConnectionProperties(conf);
					}
					if (cmdl.hasOption("file")) {
						File backup = checkRestoreFile(file);
						dropDB(connectionProperties);
						
						ImportInitvalues importInit = getApplicationContext().getBean(ImportInitvalues.class);
						importInit.loadSystem(cfg, force); 
						restoreOm(backup);
					} else {
						checkAdminDetails();
						dropDB(connectionProperties);
						
						ImportInitvalues importInit = getApplicationContext().getBean(ImportInitvalues.class);
						importInit.loadAll(cfg, force);
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
					boolean includeFiles = !cmdl.hasOption("exclude-files");
					File backup_dir = new File(OmFileHelper.getUploadTempDir(), "" + System.currentTimeMillis());
					backup_dir.mkdirs();
					
					BackupExport export = getApplicationContext().getBean(BackupExport.class);
					export.performExport(f, backup_dir, includeFiles, new ProgressHolder());
					FileHelper.removeRec(backup_dir);
					backup_dir.delete();
				} catch (Exception e) {
					handleError("Backup failed", e);
				}
				break;
			case restore:
				try {
					restoreOm(checkRestoreFile(file));
				} catch (Exception e) {
					handleError("Restore failed", e);
				}
				break;
			case files:
				try {
					boolean cleanup = cmdl.hasOption("cleanup");
					if (cleanup) {
						System.out.println("WARNING: all intermadiate files will be clean up!");
					}
					StringBuilder report = new StringBuilder();
					CleanupUnit temp = CleanupHelper.getTempUnit();
					if (cleanup) {
						temp.cleanup();
					}
					report.append("Temporary files allocates: ").append(temp.getHumanTotal()).append("\n");
					{ //UPLOAD
						long sectionSize = OmFileHelper.getSize(OmFileHelper.getUploadDir());
						report.append("Upload totally allocates: ").append(OmFileHelper.getHumanSize(sectionSize)).append("\n");
						//Profiles
						WebApplicationContext ctx = getApplicationContext();
						UserDao udao = ctx.getBean(UserDao.class);
						CleanupEntityUnit profile = CleanupHelper.getProfileUnit(udao);
						long restSize = sectionSize - profile.getSizeTotal();
						report.append("\t\tprofiles: ").append(profile.getHumanTotal()).append("\n");
						report.append("\t\t\tinvalid: ").append(profile.getHumanInvalid()).append("\n");
						report.append("\t\t\tdeleted: ").append(profile.getHumanDeleted()).append("\n");
						report.append("\t\t\tmissing count: ").append(profile.getMissing()).append("\n");
						if (cleanup) {
							profile.cleanup();
						}
						CleanupUnit imp = CleanupHelper.getImportUnit();
						restSize -= imp.getSizeTotal();
						report.append("\t\timport: ").append(OmFileHelper.getHumanSize(imp.getSizeTotal())).append("\n");
						if (cleanup) {
							imp.cleanup();
						}
						CleanupUnit back = CleanupHelper.getBackupUnit();
						restSize -= back.getSizeTotal();
						report.append("\t\tbackup: ").append(OmFileHelper.getHumanSize(back.getSizeTotal())).append("\n");
						if (cleanup) {
							back.cleanup();
						}
						//Files
						FileExplorerItemDao fileDao = ctx.getBean(FileExplorerItemDao.class);
						CleanupEntityUnit files = CleanupHelper.getFileUnit(fileDao);
						restSize -= files.getSizeTotal();
						report.append("\t\tfiles: ").append(files.getHumanTotal()).append("\n");
						report.append("\t\t\tinvalid: ").append(files.getHumanInvalid()).append("\n");
						report.append("\t\t\tdeleted: ").append(files.getHumanDeleted()).append("\n");
						report.append("\t\t\tmissing count: ").append(files.getMissing()).append("\n");
						report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
						if (cleanup) {
							files.cleanup();
						}
					}
					{ //STREAMS
						RecordingDao recordDao = getApplicationContext().getBean(RecordingDao.class);
						CleanupEntityUnit rec = CleanupHelper.getRecUnit(recordDao);
						File hibernateDir = OmFileHelper.getStreamsHibernateDir();
						report.append("Recordings allocates: ").append(rec.getHumanTotal()).append("\n");
						long size = OmFileHelper.getSize(hibernateDir);
						long restSize = rec.getSizeTotal() - size;
						report.append("\t\tfinal: ").append(OmFileHelper.getHumanSize(size)).append("\n");
						report.append("\t\t\tinvalid: ").append(rec.getHumanInvalid()).append("\n");
						report.append("\t\t\tdeleted: ").append(rec.getHumanDeleted()).append("\n");
						report.append("\t\t\tmissing count: ").append(rec.getMissing()).append("\n");
						report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
						if (cleanup) {
							rec.cleanup();
						}
					}
					System.out.println(report);
				} catch (Exception e) {
					handleError("Files failed", e);
				}
				break;
			case ldap:
				if (!cmdl.hasOption("d")) {
					System.out.println("Please specify LDAP domain Id.");
					System.exit(1);
				}
				Long domainId = Long.valueOf(cmdl.getOptionValue('d'));
				try {
					getApplicationContext().getBean(LdapLoginManagement.class).importUsers(domainId, cmdl.hasOption("print-only"));
				} catch (Exception e) {
					handleError("LDAP import failed", e);
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
	
	private void checkAdminDetails() throws Exception {
		cfg.username = cmdl.getOptionValue("user");
		cfg.email = cmdl.getOptionValue("email");
		cfg.group = cmdl.getOptionValue("group");
		if (cfg.username == null || cfg.username.length() < USER_LOGIN_MINIMUM_LENGTH) {
			System.out.println("User login was not provided, or too short, should be at least " + USER_LOGIN_MINIMUM_LENGTH + " character long.");
			System.exit(1);
		}
		
		try {
			if (cfg.email == null || !MailUtil.matches(cfg.email)) {
			    throw new AddressException("Invalid address");
			}
			new InternetAddress(cfg.email, true);
		} catch (AddressException ae) {
			System.out.println("Please provide non-empty valid email: '" + cfg.email + "' is not valid.");
			System.exit(1);
		}
		if (cfg.group == null || cfg.group.length() < 1) {
			System.out.println("User group was not provided, or too short, should be at least 1 character long: " + cfg.group);
			System.exit(1);
		}
		cfg.password = cmdl.getOptionValue("password");
		ConfigurationDao cfgDao = getApplicationContext().getBean(ConfigurationDao.class);
		if (invalidPassword(cfg.password, cfgDao)) {
			System.out.print("Please enter password for the user '" + cfg.username + "':");
			cfg.password = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).readLine();
			if (invalidPassword(cfg.password, cfgDao)) {
				System.out.println("Password was not provided, or too short, should be at least " + getMinPasswdLength(cfgDao) + " character long.");
				System.exit(1);
			}
		}
		Map<String, String> tzMap = ImportHelper.getAllTimeZones(TimeZone.getAvailableIDs());
		cfg.ical_timeZone = null;
		if (cmdl.hasOption("tz")) {
			cfg.ical_timeZone = cmdl.getOptionValue("tz");
			cfg.ical_timeZone = tzMap.containsKey(cfg.ical_timeZone) ? cfg.ical_timeZone : null;
		}
		if (cfg.ical_timeZone == null) {
			System.out.println("Please enter timezone, Possible timezones are:");
			
			for (Map.Entry<String,String> me : tzMap.entrySet()) {
				System.out.println(String.format("%1$-25s%2$s", "\"" + me.getKey() + "\"", me.getValue()));
			}
			System.exit(1);
		}
	}
	
	public void dropDB() throws Exception {
		File conf = OmFileHelper.getPersistence();
		ConnectionProperties connectionProperties = ConnectionPropertiesPatcher.getConnectionProperties(conf);
		immediateDropDB(connectionProperties);
	}
	
	private void dropDB(ConnectionProperties props) throws Exception {
		if(cmdl.hasOption("drop")) {
			immediateDropDB(props);
		}
	}
	
	private static LogImpl getLogImpl(JDBCConfiguration conf) {
		return (LogImpl)conf.getLog(JDBCConfiguration.LOG_SCHEMA);
	}

	private static void runSchemaTool(JDBCConfigurationImpl conf, String action) throws Exception {
		SchemaTool st = new SchemaTool(conf, action);
		st.setIgnoreErrors(true);
		st.setOpenJPATables(true);
		st.setIndexes(false);
		st.setPrimaryKeys(false);
		if (!SchemaTool.ACTION_DROPDB.equals(action)) {
			st.setSchemaGroup(st.getDBSchemaGroup());
		}
		st.run();
	}
	
	private void immediateDropDB(ConnectionProperties props) throws Exception {
		if (ctx != null) {
			((XmlWebApplicationContext)ctx).destroy();
			ctx = null;
		}
		JDBCConfigurationImpl conf = new JDBCConfigurationImpl();
		try {
			conf.setPropertiesFile(OmFileHelper.getPersistence());
			conf.setConnectionDriverName(props.getDriver());
			conf.setConnectionURL(props.getURL());
			conf.setConnectionUserName(props.getLogin());
			conf.setConnectionPassword(props.getPassword());
			//HACK to suppress all warnings
			getLogImpl(conf).setLevel(Log.INFO);
			runSchemaTool(conf, SchemaTool.ACTION_DROPDB);
			runSchemaTool(conf, SchemaTool.ACTION_CREATEDB);
		} finally {
			conf.close();
		}
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
	
	private void restoreOm(File backup) {
		try (InputStream is = new FileInputStream(backup)) {
			BackupImport importCtrl = getApplicationContext().getBean(BackupImport.class);
			importCtrl.performImport(is);
		} catch (Exception e) {
			handleError("Restore failed", e);
		}
	}
	
	public static void main(String[] args) {
		new Admin().process(args);
	}
}
