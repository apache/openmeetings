package org.openmeetings.app.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FileUtils {

	private static final Logger log = Red5LoggerFactory.getLogger(FileProcessor.class, ScopeApplicationAdapter.webAppRootKey);

	private static FileUtils instance;
	
	private FileUtils() {}

	public static synchronized FileUtils getInstance() {
		if (instance == null) {
			instance = new FileUtils();
		}
		return instance;
	}
	
	public long getSizeOfDirectoryAndSubs(FileExplorerItem fileExplorerItem) {
        try {

            long fileSize = 0;

            if (fileExplorerItem.getIsImage()) {

                File tFile = new File(ScopeApplicationAdapter.webAppPath
                        + File.separatorChar + "upload" + File.separatorChar
                        + "files" + File.separatorChar
                        + fileExplorerItem.getFileHash());
                if (tFile.exists()) {
                    fileSize += tFile.length();
                }

                File thumbFile = new File(ScopeApplicationAdapter.webAppPath
                        + File.separatorChar + "upload" + File.separatorChar
                        + "files" + File.separatorChar + "_thumb_"
                        + fileExplorerItem.getFileHash());
                if (thumbFile.exists()) {
                    fileSize += thumbFile.length();
                }

            }

            if (fileExplorerItem.getIsPresentation()) {

                File tFolder = new File(ScopeApplicationAdapter.webAppPath
                        + File.separatorChar + "upload" + File.separatorChar
                        + "files" + File.separatorChar
                        + fileExplorerItem.getFileHash());

                if (tFolder.exists()) {
                    fileSize += this.getDirSize(tFolder);
                }

            }

            log.debug("calling [1] FileExplorerItemDaoImpl.updateFileOrFolder()");
            FileExplorerItemDaoImpl.getInstance().updateFileOrFolder(
                    fileExplorerItem);

            FileExplorerItem[] childElements = FileExplorerItemDaoImpl
                    .getInstance().getFileExplorerItemsByParent(
                            fileExplorerItem.getFileExplorerItemId());

            for (FileExplorerItem childExplorerItem : childElements) {

                fileSize += this.getSizeOfDirectoryAndSubs(childExplorerItem);

            }

            return fileSize;

        } catch (Exception err) {
            log.error("[getSizeOfDirectoryAndSubs] ", err);
        }
        return 0;
    }

	public long getDirSize(File dir) {
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();

            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += this.getDirSize(file);
                }

            }
        }

        return size;
    }
    
	public void setFileToOwnerOrRoomByParent(
            FileExplorerItem fileExplorerItem, Long users_id, Long room_id) {
        try {

            fileExplorerItem.setOwnerId(users_id);
            fileExplorerItem.setRoom_id(room_id);

            log.debug("calling [2] FileExplorerItemDaoImpl.updateFileOrFolder()");
            FileExplorerItemDaoImpl.getInstance().updateFileOrFolder(
                    fileExplorerItem);

            FileExplorerItem[] childElements = FileExplorerItemDaoImpl
                    .getInstance().getFileExplorerItemsByParent(
                            fileExplorerItem.getFileExplorerItemId());

            for (FileExplorerItem childExplorerItem : childElements) {

                this.setFileToOwnerOrRoomByParent(childExplorerItem, users_id,
                        room_id);

            }

        } catch (Exception err) {
            log.error("[setFileToOwnerOrRoomByParent] ", err);
        }
    }

	public String formatDate(Date date) {
        SimpleDateFormat formatter;
        String pattern = "dd/MM/yy H:mm:ss";
        Locale locale = new Locale("en", "US");
        formatter = new SimpleDateFormat(pattern, locale);
        return formatter.format(date);
    }
    
	public void copyFile(String sourceFile, String targetFile) {
        try {
            File f1 = new File(sourceFile);
            File f2 = new File(targetFile);
            InputStream in = new FileInputStream(f1);

            // For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (Exception e) {
            log.error("[copyfile]", e);
        }
    }
}
