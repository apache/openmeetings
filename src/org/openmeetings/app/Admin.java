package org.openmeetings.app;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.openmeetings.servlet.outputhandler.BackupExport;
import org.openmeetings.servlet.outputhandler.BackupImportController;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Admin {
	private static final Logger log = Red5LoggerFactory.getLogger(Admin.class);
	private boolean verbose = false;
	private Options opts = buildOptions();

	@SuppressWarnings("static-access")
	private Options buildOptions() {
		Options options = new Options();
		OptionGroup group = new OptionGroup()
			.addOption(OptionBuilder.withLongOpt("install").withDescription("Fill DB table, and make OM usable").create('i'))
			.addOption(OptionBuilder.withLongOpt("backup").withDescription("Backups OM").create('b'))
			.addOption(OptionBuilder.withLongOpt("restore").withDescription("Restores OM").create('r'))
			.addOption(OptionBuilder.withLongOpt("help").withDescription("print this message").create('h'));
		group.setRequired(true); 
		options.addOptionGroup(group);
		options.addOption(OptionBuilder.withLongOpt("verbose").withDescription("verbose error messages").create('v'));
		options.addOption(OptionBuilder.withLongOpt("file").hasArg().withDescription("file used for backup/restore").create('f'));
		options.addOption(OptionBuilder.withLongOpt("exclude-files").withDescription("should backup exclude files [default: include]").create());
		
		return options;
	}
	
	private enum Command {
		install
		, backup
		, restore
		, usage
	}
	
	private void usage() {
		new HelpFormatter().printHelp("admin [-h|-i|-b|-r] [options]", opts);
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
	
	private ClassPathXmlApplicationContext getApplicationContext() {
		ClassPathXmlApplicationContext applicationContext = null;
		try {
			applicationContext = new ClassPathXmlApplicationContext("openmeetings-applicationContext.xml");
		} catch (Exception e) {
			handleError("Unable to obtain application context", e);
		}
		return applicationContext;
	}
	
	private void process(String[] args) {
		File omHome = new File(System.getenv("RED5_HOME"), "webapps/openmeetings");
		
		Parser parser = new PosixParser();
		CommandLine cmdl = null;
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
		}

		ClassPathXmlApplicationContext applicationContext = null;
		switch(cmd) {
			case install:
			case backup:
			case restore:
				applicationContext = getApplicationContext();
			default:
				//noop
		};
		String file = cmdl.getOptionValue('f', "");
		switch(cmd) {
			case install:
				break;
			case backup:
				try {
					if (!cmdl.hasOption('f')) {
						file = "backup_" + CalendarPatterns.getTimeForStreamId(new Date()) + ".zip";
						System.out.println("File name was not specified, '" + file + "' will be used");
					}
					boolean includeFiles = Boolean.getBoolean(cmdl.getOptionValue("exclude-files", "true"));
					BackupExport export = applicationContext.getBean(BackupExport.class);
					File backup_dir = new File(omHome, "uploadtemp/" + System.currentTimeMillis());
					backup_dir.mkdirs();
					export.performExport(file, backup_dir, includeFiles, omHome.getAbsolutePath());
					export.deleteDirectory(backup_dir);
					backup_dir.delete();
				} catch (Exception e) {
					handleError("Backup failed", e);
				}
				break;
			case restore:
				try {
					File backup = new File(file);
					if (!cmdl.hasOption('f') || !backup.exists() || !backup.isFile()) {
						System.out.println("File should be specified, and point the the existent zip file");
						usage();
						System.exit(1);
					}
					BackupImportController importCtrl = applicationContext.getBean(BackupImportController.class);
					importCtrl.performImport(new FileInputStream(backup), omHome.getAbsolutePath());
				} catch (Exception e) {
					handleError("Restore failed", e);
				}
				break;
			case usage:
			default:
				usage();
				break;
		}
		
		//Exit the shell process
		log.info("... Done");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new Admin().process(args);
	}
}
