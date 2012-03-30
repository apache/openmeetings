package org.openmeetings.app;

import java.io.File;

import org.openmeetings.servlet.outputhandler.BackupExport;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Admin {
	private static final Logger log = Red5LoggerFactory.getLogger(Admin.class);
	private boolean verbose = false;

	private enum Command {
		install
		, backup
		, restore
	}
	
	private void handleError(String msg, Exception e) {
		if (verbose) {
			log.error(msg, e);
		} else {
			log.error(msg + e.getMessage());
		}
		System.exit(1);
	}
	
	private String getFile(String[] args) {
		String result = null;
		for (int i = 0; i < args.length; ++i) {
			String a = args[i];
			if ("-file".equals(a)) {
				result = args[i + 1];
				break;
			}
		}
		return result;
	}
	
	private void process(String[] args) {
		String red5Home = "webapps/openmeetings/";
		for (String a : args) {
			if ("-v".equals(a)) {
				verbose = true;
				break;
			}
		}
		ClassPathXmlApplicationContext applicationContext = null;
		try {
			applicationContext = new ClassPathXmlApplicationContext("openmeetings-applicationContext.xml");
		} catch (Exception e) {
			handleError("Unable to obtain application context", e);
		}

		Command cmd = null;
		try {
			cmd = Command.valueOf(args[0]);
		} catch (Exception e) {
			handleError("Failed to get command ", e);
		}
		switch(cmd) {
			case install:
				break;
			case backup:
				try {
					//FIXME !!!! cleanup
					//FIXME hardcoded paths
					BackupExport export = (BackupExport)applicationContext.getBean("backupExport");
					String backup_dir = red5Home + "uploadtemp/" + System.currentTimeMillis() + "/";
					new File(backup_dir).mkdirs();
					export.performExport(getFile(args), backup_dir, true, red5Home);
					export.deleteDirectory(new File(backup_dir));
				} catch (Exception e) {
					handleError("Backup failed", e);
				}
				break;
			case restore:
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
