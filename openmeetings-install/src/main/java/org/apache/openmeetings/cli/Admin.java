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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.db.util.ApplicationHelper.destroyApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CONTEXT_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_LOGIN_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.USER_PASSWORD_MINIMUM_LENGTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
import org.apache.openjpa.jdbc.schema.SchemaTool;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.lib.log.LogFactoryImpl.LogImpl;
import org.apache.openmeetings.backup.BackupExport;
import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.ProgressHolder;
import org.apache.openmeetings.core.ldap.LdapLoginManager;
import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ApplicationHelper;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.openmeetings.util.ImportHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.mail.MailUtil;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.context.WebApplicationContext;

public class Admin {
	private static final Logger _log = Red5LoggerFactory.getLogger(Admin.class);
	private static final String OPTION_DB_TYPE = "db-type";
	private static final String OPTION_DB_HOST = "db-host";
	private static final String OPTION_DB_PORT = "db-port";
	private static final String OPTION_DB_NAME = "db-name";
	private static final String OPTION_DB_USER = "db-user";
	private static final String OPTION_DB_PASS = "db-pass";
	private static final String OPTION_MAIL_REFERRER = "system-email-address";
	private static final String OPTION_MAIL_SERVER = "smtp-server";
	private static final String OPTION_MAIL_PORT = "smtp-port";
	private static final String OPTION_MAIL_USER = "email-auth-user";
	private static final String OPTION_MAIL_PASS = "email-auth-pass";
	private static final String REP_INVALID = "\t\t\tinvalid: ";
	private static final String REP_DELETED = "\t\t\tdeleted: ";
	private static final String REP_MISSING = "\t\t\tmissing count: ";

	public static final String RED5_HOME = "red5_home";

	private boolean verbose = false;
	private InstallationConfig cfg = null;
	private Options opts = null;
	private CommandLine cmdl = null;
	private WebApplicationContext context = null;
	private File home;
	private String step;

	Admin() {
		cfg = new InstallationConfig();
		opts = buildOptions();
		step = "Initialization";
		if (!System.getProperties().containsKey("context")) {
			log(String.format("System.property 'context' is not set, defaulting to %s", DEFAULT_CONTEXT_NAME));
		}
		String ctxName = System.getProperty("context", DEFAULT_CONTEXT_NAME);
		setWicketApplicationName(ctxName);
		home = new File(System.getProperty(RED5_HOME));
		OmFileHelper.setOmHome(new File(new File(home, "webapps"), ctxName));
	}

	private static void log(CharSequence msg) {
		_log.info(msg.toString());
		System.out.println(msg);
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
		options.addOption(new OmOption("i", null, OPTION_MAIL_REFERRER, true, String.format("System e-mail address [default: %s]", cfg.getMailReferer()), true));
		options.addOption(new OmOption("i", null, OPTION_MAIL_SERVER, true, String.format("SMTP server for outgoing e-mails [default: %s]", cfg.getSmtpServer()), true));
		options.addOption(new OmOption("i", null, OPTION_MAIL_PORT, true, String.format("SMTP server for outgoing e-mails [default: %s]", cfg.getSmtpPort()), true));
		options.addOption(new OmOption("i", null, OPTION_MAIL_USER, true, "Email auth username (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, OPTION_MAIL_PASS, true, "Email auth password (anonymous connection will be used if not set)", true));
		options.addOption(new OmOption("i", null, "email-use-tls", false, "Is secure e-mail connection [default: no]", true));
		options.addOption(new OmOption("i", null, "skip-default-objects", false, "Do not create default rooms and OAuth servers [created by default]", true));
		options.addOption(new OmOption("i", null, "disable-frontend-register", false, "Do not allow front end register [allowed by default]", true));
		options.addOption(new OmOption("i", null, "default-language", true, "Default system language as int [1 by default]", true));

		options.addOption(new OmOption("i", null, OPTION_DB_TYPE, true, "The type of the DB to be used", true));
		options.addOption(new OmOption("i", null, OPTION_DB_HOST, true, "DNS name or IP address of database", true));
		options.addOption(new OmOption("i", null, OPTION_DB_PORT, true, "Database port", true));
		options.addOption(new OmOption("i", null, OPTION_DB_NAME, true, "The name of Openmeetings database", true));
		options.addOption(new OmOption("i", null, OPTION_DB_USER, true, "User with write access to the DB specified", true));
		options.addOption(new OmOption("i", null, OPTION_DB_PASS, true, "Password of the user with write access to the DB specified", true));
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

	private void handleError(Exception e) {
		handleError(e, false, true);
	}

	private void handleError(Exception e, boolean printUsage, boolean _throw) {
		if (printUsage) {
			usage();
		}
		if (verbose) {
			String msg = String.format("%s failed", step);
			_log.error(msg, e);
		} else {
			_log.error("{} failed: {}", step, e.getMessage());
		}
		if (_throw) {
			throw new ExitException();
		}
	}

	private WebApplicationContext getApplicationContext() {
		if (context == null) {
			String _step = step; //preserve step
			step = "Shutdown schedulers";
			Long lngId = (long)cfg.getDefaultLangId();
			context = ApplicationHelper.getApplicationContext(lngId);
			SchedulerFactoryBean sfb = context.getBean(SchedulerFactoryBean.class);
			try {
				sfb.getScheduler().shutdown(false);
				step = _step; //restore
			} catch (Exception e) {
				handleError(e);
			}
		}
		return context;
	}

	//package private wrapper for testing
	void process(String... args) throws Exception {
		CommandLineParser parser = new DefaultParser();
		try {
			cmdl = parser.parse(opts, args);
		} catch (ParseException e) {
			log(e.getMessage());
			usage();
			throw new ExitException();
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
				step = "Install";
				processInstall(file);
				break;
			case backup:
				step = "Backup";
				processBackup(file);
				break;
			case restore:
				step = "Restore";
				processRestore(checkRestoreFile(file));
				break;
			case files:
				step = "Files";
				processFiles();
				break;
			case ldap:
				step = "LDAP import";
				processLdap();
				break;
			case usage:
			default:
				usage();
				break;
		}
	}

	private void processInstall(String file) throws Exception {
		if (cmdl.hasOption("file") && (cmdl.hasOption("user") || cmdl.hasOption("email") || cmdl.hasOption("group"))) {
			log("Please specify even 'file' option or 'admin user'.");
			throw new ExitException();
		}
		boolean force = cmdl.hasOption("force");
		if (cmdl.hasOption("skip-default-objects")) {
			cfg.setCreateDefaultObjects(false);
		}
		if (cmdl.hasOption("disable-frontend-register")) {
			cfg.setAllowFrontendRegister(false);
		}
		if (cmdl.hasOption(OPTION_MAIL_REFERRER)) {
			cfg.setMailReferer(cmdl.getOptionValue(OPTION_MAIL_REFERRER));
		}
		if (cmdl.hasOption(OPTION_MAIL_SERVER)) {
			cfg.setSmtpServer(cmdl.getOptionValue(OPTION_MAIL_SERVER));
		}
		if (cmdl.hasOption(OPTION_MAIL_PORT)) {
			cfg.setSmtpPort(Integer.valueOf(cmdl.getOptionValue(OPTION_MAIL_PORT)));
		}
		if (cmdl.hasOption(OPTION_MAIL_USER)) {
			cfg.setMailAuthName(cmdl.getOptionValue(OPTION_MAIL_USER));
		}
		if (cmdl.hasOption(OPTION_MAIL_PASS)) {
			cfg.setMailAuthPass(cmdl.getOptionValue(OPTION_MAIL_PASS));
		}
		if (cmdl.hasOption("email-use-tls")) {
			cfg.setMailUseTls(true);
		}
		if (cmdl.hasOption("default-language")) {
			cfg.setDefaultLangId(Integer.parseInt(cmdl.getOptionValue("default-language")));
		}
		ConnectionProperties connectionProperties;
		File conf = OmFileHelper.getPersistence();
		if (!conf.exists() || cmdl.hasOption(OPTION_DB_TYPE) || cmdl.hasOption(OPTION_DB_HOST)
				|| cmdl.hasOption(OPTION_DB_PORT) || cmdl.hasOption(OPTION_DB_NAME) || cmdl.hasOption(OPTION_DB_USER)
				|| cmdl.hasOption(OPTION_DB_PASS))
		{
			String dbType = cmdl.getOptionValue(OPTION_DB_TYPE, DbType.derby.name());
			connectionProperties = ConnectionPropertiesPatcher.patch(dbType
					, cmdl.getOptionValue(OPTION_DB_HOST, "localhost")
					, cmdl.getOptionValue(OPTION_DB_PORT, null)
					, cmdl.getOptionValue(OPTION_DB_NAME, null)
					, cmdl.getOptionValue(OPTION_DB_USER, null)
					, cmdl.getOptionValue(OPTION_DB_PASS, null)
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
			processRestore(backup);
		} else {
			checkAdminDetails();
			dropDB(connectionProperties);

			ImportInitvalues importInit = getApplicationContext().getBean(ImportInitvalues.class);
			importInit.loadAll(cfg, force);
		}
	}

	private void processBackup(String file) throws Exception {
		File f;
		if (!cmdl.hasOption("file")) {
			String fn = "backup_" + CalendarPatterns.getTimeForStreamId(new Date()) + ".zip";
			f = new File(home, fn);
			log("File name was not specified, '" + fn + "' will be used");
		} else {
			f = new File(file);
		}
		boolean includeFiles = !cmdl.hasOption("exclude-files");
		File backupDir = new File(OmFileHelper.getUploadBackupDir(), String.valueOf(System.currentTimeMillis()));
		backupDir.mkdirs();

		BackupExport export = getApplicationContext().getBean(BackupExport.class);
		export.performExport(f, includeFiles, new ProgressHolder());
		FileUtils.deleteDirectory(backupDir);
		backupDir.delete();
	}

	private void processFiles() throws IOException {
		boolean cleanup = cmdl.hasOption("cleanup");
		if (cleanup) {
			log("WARNING: all intermediate files will be clean up!");
		}
		StringBuilder report = new StringBuilder();
		reportUploads(report, cleanup);
		reportStreams(report, cleanup);
		log(report);
	}

	private void processLdap() throws OmException {
		if (!cmdl.hasOption("d")) {
			log("Please specify LDAP domain Id.");
			throw new ExitException();
		}
		Long domainId = Long.valueOf(cmdl.getOptionValue('d'));
		getApplicationContext().getBean(LdapLoginManager.class).importUsers(domainId, cmdl.hasOption("print-only"));
	}

	private void reportUploads(StringBuilder report, boolean cleanup) throws IOException {
		long sectionSize = OmFileHelper.getSize(OmFileHelper.getUploadDir());
		report.append("Upload totally allocates: ").append(OmFileHelper.getHumanSize(sectionSize)).append("\n");
		//Profiles
		WebApplicationContext ctx = getApplicationContext();
		UserDao udao = ctx.getBean(UserDao.class);
		CleanupEntityUnit profile = CleanupHelper.getProfileUnit(udao);
		long restSize = sectionSize - profile.getSizeTotal();
		report.append("\t\tprofiles: ").append(profile.getHumanTotal()).append("\n");
		report.append(REP_INVALID).append(profile.getHumanInvalid()).append("\n");
		report.append(REP_DELETED).append(profile.getHumanDeleted()).append("\n");
		report.append(REP_MISSING).append(profile.getMissing()).append("\n");
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
		FileItemDao fileDao = ctx.getBean(FileItemDao.class);
		CleanupEntityUnit files = CleanupHelper.getFileUnit(fileDao);
		restSize -= files.getSizeTotal();
		report.append("\t\tfiles: ").append(files.getHumanTotal()).append("\n");
		report.append(REP_INVALID).append(files.getHumanInvalid()).append("\n");
		report.append(REP_DELETED).append(files.getHumanDeleted()).append("\n");
		report.append(REP_MISSING).append(files.getMissing()).append("\n");
		report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
		if (cleanup) {
			files.cleanup();
		}
	}

	private void reportStreams(StringBuilder report, boolean cleanup) throws IOException {
		RecordingDao recordDao = getApplicationContext().getBean(RecordingDao.class);
		CleanupEntityUnit rec = CleanupHelper.getRecUnit(recordDao);
		File hibernateDir = OmFileHelper.getStreamsHibernateDir();
		report.append("Recordings allocates: ").append(rec.getHumanTotal()).append("\n");
		long size = OmFileHelper.getSize(hibernateDir);
		long restSize = rec.getSizeTotal() - size;
		report.append("\t\tfinal: ").append(OmFileHelper.getHumanSize(size)).append("\n");
		report.append(REP_INVALID).append(rec.getHumanInvalid()).append("\n");
		report.append(REP_DELETED).append(rec.getHumanDeleted()).append("\n");
		report.append(REP_MISSING).append(rec.getMissing()).append("\n");
		report.append("\t\trest: ").append(OmFileHelper.getHumanSize(restSize)).append("\n");
		if (cleanup) {
			rec.cleanup();
		}
	}

	private void checkAdminDetails() throws Exception {
		cfg.setUsername(cmdl.getOptionValue("user"));
		cfg.setEmail(cmdl.getOptionValue("email"));
		cfg.setGroup(cmdl.getOptionValue("group"));
		if (cfg.getUsername() == null || cfg.getUsername().length() < USER_LOGIN_MINIMUM_LENGTH) {
			log("User login was not provided, or too short, should be at least " + USER_LOGIN_MINIMUM_LENGTH + " character long.");
			throw new ExitException();
		}

		if (!MailUtil.isValid(cfg.getEmail())) {
			log(String.format("Please provide non-empty valid email: '%s' is not valid.", cfg.getEmail()));
			throw new ExitException();
		}
		if (Strings.isEmpty(cfg.getGroup())) {
			log(String.format("User group was not provided, or too short, should be at least 1 character long: %s", cfg.getGroup()));
			throw new ExitException();
		}
		if (cmdl.hasOption("password")) {
			cfg.setPassword(cmdl.getOptionValue("password"));
		}
		IValidator<String> passValidator = new StrongPasswordValidator(false, new User());
		Validatable<String> passVal;
		do {
			passVal = new Validatable<>(cfg.getPassword());
			passValidator.validate(passVal);
			if (!passVal.isValid()) {
				log(String.format("Please enter password for the user '%s':", cfg.getUsername()));
				cfg.setPassword(new BufferedReader(new InputStreamReader(System.in, UTF_8)).readLine());
			}
		} while (!passVal.isValid());
		Map<String, String> tzMap = ImportHelper.getAllTimeZones(TimeZone.getAvailableIDs());
		cfg.setTimeZone(null);
		if (cmdl.hasOption("tz")) {
			String tz = cmdl.getOptionValue("tz");
			cfg.setTimeZone(tzMap.containsKey(tz) ? tz : null);
		}
		if (cfg.getTimeZone() == null) {
			log("Please enter timezone, Possible timezones are:");

			for (Map.Entry<String,String> me : tzMap.entrySet()) {
				log(String.format("%1$-25s%2$s", "\"" + me.getKey() + "\"", me.getValue()));
			}
			throw new ExitException();
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
		if (context != null) {
			destroyApplication();
			context = null;
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
			log("File should be specified, and point the existent zip file");
			usage();
			throw new ExitException();
		}
		return backup;
	}

	private void processRestore(File backup) throws Exception {
		try (InputStream is = new FileInputStream(backup)) {
			BackupImport importCtrl = getApplicationContext().getBean(BackupImport.class);
			importCtrl.performImport(is);
		}
	}

	public static void main(String[] args) {
		Admin a = new Admin();
		try {
			a.process(args);

			log("... Done");
			System.exit(0);
		} catch (ExitException e) {
			a.handleError(e, false, false);
			System.exit(1);
		} catch (Exception e) {
			_log.error("Unexpected error", e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
