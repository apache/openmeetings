package org.apache.openmeetings.test.backup;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOldBackups extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private BackupImportController backupController;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private FlvRecordingDao recordingDao;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;

	@Test
	public void importOldVersions() {
		String backupsDir = System.getProperty("backups.dir", ".");
		File backupsHome = new File(backupsDir);
		
		if (!backupsHome.exists() || !backupsHome.isDirectory()) {
			fail("Invalid directory is specified for backup files: " + backupsDir);
		}
		long userCount = 0;
		long recCount = 0;
		long fileCount = 0;
		for (File backup : backupsHome.listFiles()) {
			try {
				backupController.performImport(new FileInputStream(backup));
				long newUserCount = usersDao.count();
				long newRecCount = recordingDao.getFlvRecordings().size();
				long newFileCount = fileExplorerItemDao.getFileExplorerItems().size();
				assertTrue("Zero users were imported", newUserCount > userCount);
				assertTrue("Zero recordings were imported", newRecCount > recCount);
				assertTrue("Zero files were imported", newFileCount > fileCount);

				userCount = newUserCount;
				recCount = newRecCount;
				fileCount = newFileCount;
			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception while importing backup: " + backup.getName(), e);
			}
		}
	}
}
