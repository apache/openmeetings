package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.StoredFile;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FileExplorerUploadHandler extends UploadHandler {

    private static final Logger log = Red5LoggerFactory.getLogger(
            FileExplorerUploadHandler.class,
            ScopeApplicationAdapter.webAppRootKey);

    private static final long serialVersionUID = 2848421357849982426L;

    protected void fileService(HttpServletRequest httpServletRequest,
            String sid, Long userId, Map<String, Object> hs)
            throws ServletException, Exception {

        HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();

        String room_idAsString = httpServletRequest.getParameter("room_id");
        if (room_idAsString == null) {
            throw new ServletException("Missing Room ID");
        }
        // String room_id = room_idAsString;

        Long room_id_to_Store = Long.parseLong(room_idAsString);

        String isOwnerAsString = httpServletRequest.getParameter("isOwner");
        if (isOwnerAsString == null) {
            throw new ServletException("Missing isOwnerAsString");
        }
        boolean isOwner = false;
        if (isOwnerAsString.equals("1")) {
            isOwner = true;
        }

        String parentFolderIdAsString = httpServletRequest
                .getParameter("parentFolderId");
        if (parentFolderIdAsString == null) {
            throw new ServletException("Missing parentFolderId ID");
        }
        Long parentFolderId = Long.parseLong(parentFolderIdAsString);

        // String fileSystemName =
        // httpServletRequest.getParameter("filename");
        // if (fileSystemName == null || fileSystemName.length() ==
        // 0) {
        // throw new Exception("Missing filename");
        // }

        // Get the current User-Directory

        String current_dir = getServletContext().getRealPath("/");
        String workingDir = current_dir + "upload" + File.separatorChar
                + "files" + File.separatorChar;

        String working_dirppt = "";

        // System.out.println("IS SYSTEM PROFILE");
        // Add the Folder for the Room if it does not exist yet
        File localFolder = new File(workingDir);
        if (!localFolder.exists()) {
            localFolder.mkdir();
        }

        working_dirppt = current_dir + "uploadtemp" + File.separatorChar
                + "files" + File.separatorChar;

        // add Temp Folder Structure
        File localFolderppt = new File(working_dirppt);
        if (!localFolderppt.exists()) {
            localFolderppt.mkdir();
        }

        log.debug("workingDir: " + workingDir);

        ServletMultipartRequest upload = new ServletMultipartRequest(
                httpServletRequest, 104857600 * 5, // max 500 mb
                "utf-8");
        InputStream is = upload.getFileContents("Filedata");
        String fileSystemName = upload.getBaseFilename("Filedata");
        log.debug("fileSystemName: " + fileSystemName);

        int dotidx = fileSystemName.lastIndexOf('.');

        // Generate a random string to prevent any problems with
        // foreign characters and duplicates
        Date d = new Date();
        String newFileSystemName = MD5.do_checksum("FILE_" + d.getTime());

        String newFileExtDot = fileSystemName.substring(dotidx,
                fileSystemName.length()).toLowerCase();
        String newFileExt = newFileExtDot.substring(1);
        log.debug("newFileExt: " + newFileExt);
        StoredFile storedFile = new StoredFile(newFileSystemName, newFileExt); 

        // Check variable to see if this file is a presentation
        // check if this is a a file that can be converted by
        // openoffice-service
        boolean canBeConverted = storedFile.isConvertable();
        boolean isPdf = storedFile.isPdf();
        boolean isImage = storedFile.isImage();
        boolean isChart = storedFile.isChart();
        boolean isAsIs = storedFile.isAsIs();
        boolean isVideo = storedFile.isVideo();

        String completeName = "";
        log.debug("isAsIs: " + isAsIs);

        // add outputfolders for profiles
        // if it is a presenation it will be copied to another place
        if (!(canBeConverted || isPdf || isImage || isVideo || isAsIs)) {
            return;
        }

        if (isAsIs) {
            // check if this is a room file or UserProfile
            completeName = workingDir + newFileSystemName;
        } else {
            completeName = working_dirppt + newFileSystemName;
        }
        log.debug("writing file to: " + completeName + newFileExtDot);

        FileOutputStream fos = new FileOutputStream(completeName
                + newFileExtDot);
        byte[] buffer = new byte[1024];
        int len = 0;

        while (len != (-1)) {
            len = is.read(buffer, 0, 1024);
            if (len != (-1))
                fos.write(buffer, 0, len);
        }

        fos.close();
        is.close();

        Long ownerId = null;
        if (parentFolderId == -2) {
            parentFolderId = 0L;
            ownerId = userId;
        }
        if (isOwner) {
            ownerId = userId;
        }

        String fileHashName = newFileSystemName + newFileExtDot;
        Boolean isPresentation = false;
        if (canBeConverted || isPdf) {
            // In case of a presentation the hash is a folder-name
            fileHashName = newFileSystemName;
            isPresentation = true;
        }
        if (isImage) {
            fileHashName = newFileSystemName + ".jpg";
        }
        if (isVideo) {
            fileHashName = newFileSystemName + ".flv";
        }

        FileExplorerItem fileExplorerItem = FileExplorerItemDaoImpl
                .getInstance().getFileExplorerItemsById(parentFolderId);

        if (fileExplorerItem != null) {
            if (fileExplorerItem.getIsFolder() == null
                    || !fileExplorerItem.getIsFolder()) {
                parentFolderId = 0L;
            }
        }

        Long fileExplorerItemId = FileExplorerItemDaoImpl.getInstance().add(
                fileSystemName, fileHashName, // The Hashname of the file
                parentFolderId, ownerId, room_id_to_Store, userId, false, // isFolder
                isImage, isPresentation, "", false, isChart);
        log.debug("fileExplorerItemId: " + fileExplorerItemId);

        log.debug("canBeConverted: " + canBeConverted);
        if (canBeConverted) {
            // convert to pdf, thumbs, swf and xml-description
            returnError = GeneratePDF.getInstance().convertPDF(current_dir,
                    newFileSystemName, newFileExtDot, "files", true,
                    completeName);
        } else if (isPdf) {
            // convert to thumbs, swf and xml-description
            returnError = GeneratePDF.getInstance().convertPDF(current_dir,
                    newFileSystemName, newFileExtDot, "files", false,
                    completeName);
        } else if (isChart) {
            log.debug("uploaded chart file");
        } else if (isImage && !isAsIs) {
            // convert it to JPG
            log.debug("##### convert it to JPG: ");
            returnError = GenerateImage.getInstance().convertImage(current_dir,
                    newFileSystemName, newFileExtDot, "files",
                    newFileSystemName, false);
        } else if (isAsIs) {
            HashMap<String, Object> processThumb = GenerateThumbs.getInstance()
                    .generateThumb("_thumb_", current_dir, completeName, 50);
            returnError.put("processThumb", processThumb);
        } else if (isVideo) {
            // openmeetings.FlvExplorerConverter

            // ApplicationContext context =
            // WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            //
            // FlvExplorerConverter flvExplorerConverter =
            // (FlvExplorerConverter)
            // context.getBean("openmeetings.FlvExplorerConverter");
            //
            // flvExplorerConverter.startConversion(fileExplorerItemId,
            // completeName + newFileSystemExtName);

        }

        // Flash cannot read the response of an upload
        // httpServletResponse.getWriter().print(returnError);
        hs.put("message", "library");
        hs.put("action", "newFile");
        hs.put("fileExplorerItem", FileExplorerItemDaoImpl.getInstance()
                .getFileExplorerItemsById(fileExplorerItemId));
        hs.put("error", returnError);
        hs.put("fileName", completeName);

    }

}
