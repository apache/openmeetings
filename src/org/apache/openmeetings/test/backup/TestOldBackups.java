package org.apache.openmeetings.test.backup;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOldBackups extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private BackupImportController backupController;
	
	@Test
	public void importOldVersions() {
		String backupsDir = System.getProperty("backups.dir", ".");
		File backupsHome = new File(backupsDir);
		
		if (!backupsHome.exists() || !backupsHome.isDirectory()) {
			fail("Invalid directory is specified for backup files: " + backupsDir);
		}
		for (File backup : backupsHome.listFiles()) {
			try {
				backupController.performImport(new FileInputStream(backup));
			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception while importing backup: " + backup.getName(), e);
			}
		}
	}
}
