package org.openmeetings.servlet.outputhandler;

import http.utils.multipartrequest.ServletMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateImage;
import org.openmeetings.app.documents.GeneratePDF;
import org.openmeetings.app.documents.GenerateThumbs;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FileExplorerUploadHandler extends HttpServlet {

        private static final Logger log = Red5LoggerFactory.getLogger(FileExplorerUploadHandler.class);
         
        private String filesString[] = null;

        protected HashMap<String, String> fileExtensions = new HashMap<String, String>();

        protected HashMap<String, String> pdfExtensions = new HashMap<String, String>();

        protected HashMap<String, String> imageExtensions = new HashMap<String, String>();

        protected HashMap<String, String> jpgExtensions = new HashMap<String, String>();
        
        protected HashMap<String, String> videoExtensions = new HashMap<String, String>();

        public FileExplorerUploadHandler() {
                fileExtensions.put("ext1", ".ppt");
                fileExtensions.put("ext2", ".odp");
                fileExtensions.put("ext3", ".odt");
                fileExtensions.put("ext4", ".sxw");
                fileExtensions.put("ext5", ".wpd");
                fileExtensions.put("ext6", ".doc");
                fileExtensions.put("ext7", ".rtf");
                fileExtensions.put("ext8", ".txt");
                fileExtensions.put("ext9", ".ods");
                fileExtensions.put("ext10", ".sxc");
                fileExtensions.put("ext11", ".xls");
                fileExtensions.put("ext12", ".sxi");
                fileExtensions.put("ext13", ".pptx");
        		fileExtensions.put("ext14", ".docx");
        		fileExtensions.put("ext15", ".xlsx");

                pdfExtensions.put("ext1", ".pdf");
                pdfExtensions.put("ext2", ".ps"); //PostScript

                jpgExtensions.put("ext1", ".jpg");

                videoExtensions.put("ext1", ".avi");//.avi
                videoExtensions.put("ext2", ".mov");//.mov
                videoExtensions.put("ext3", ".flv");//.flv
                videoExtensions.put("ext4", ".mp4");//.mp4

                imageExtensions.put("ext1", ".png");
                imageExtensions.put("ext2", ".gif");
                imageExtensions.put("ext3", ".svg");
                imageExtensions.put("ext4", ".dpx"); //DPX
                imageExtensions.put("ext5", ".exr"); //EXR
                imageExtensions.put("ext6", ".pcd"); //PhotoCD
                imageExtensions.put("ext7", ".pcds"); //PhotoCD
                imageExtensions.put("ext8", ".psd"); //Adobe Photoshop bitmap file
                imageExtensions.put("ext9", ".tiff"); //Tagged Image File Format
                imageExtensions.put("ext10", ".ttf"); //TrueType font file
                imageExtensions.put("ext11", ".xcf"); //GIMP imag
                imageExtensions.put("ext12", ".wpg"); //Word Perfect Graphics File
                imageExtensions.put("ext13", ".txt"); //Raw text file
                imageExtensions.put("ext14", ".bmp");
                imageExtensions.put("ext15", ".ico"); //Microsoft Icon File
                imageExtensions.put("ext16", ".tga"); //Truevision Targa image
                imageExtensions.put("ext17", ".jpeg");
        }

        /* (non-Javadoc)
         * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        protected void service(HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) throws ServletException,
                        IOException {
                try {
                        
                        log.debug("uploading... ");
      
                        if (httpServletRequest.getContentLength() > 0) {
                                
                                log.debug("uploading........ ");
                                
                                HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();

                                String sid = httpServletRequest.getParameter("sid");
                                if (sid == null) {
                                        throw new ServletException("Missing SID");
                                }

                                log.debug("uploading........ sid: "+sid);
                                
                                Long users_id = Sessionmanagement.getInstance().checkSession(sid);
                                Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
                                
                                String publicSID = httpServletRequest.getParameter("publicSID");
                                if (publicSID == null) {
                                        //Always ask for Public SID
                                        throw new ServletException("Missing publicSID");
                                }

                                if (user_level > 0) {
                                        
                                        log.debug("uploading........ user_level: "+user_level);
                                        
                                        String room_idAsString = httpServletRequest.getParameter("room_id");
                                        if (room_idAsString == null) {
                                                throw new ServletException("Missing Room ID");
                                        }
                                        //String room_id = room_idAsString;
                                        
                                        Long room_id_to_Store = Long.parseLong(room_idAsString);
                                        
                                        String isOwnerAsString = httpServletRequest.getParameter("isOwner");
                                        if (isOwnerAsString == null) {
                                                throw new ServletException("Missing isOwnerAsString");
                                        }
                                        boolean isOwner = false;
                                        if (isOwnerAsString.equals("1")) {
                                        	isOwner = true;
                                        }
                                        
                                        String parentFolderIdAsString = httpServletRequest.getParameter("parentFolderId");
                                        if (parentFolderIdAsString == null) {
                                                throw new ServletException("Missing parentFolderId ID");
                                        }
                                        Long parentFolderId = Long.parseLong(parentFolderIdAsString);
                                                
                                        
//                                        String fileSystemName = httpServletRequest.getParameter("filename");
//                                        if (fileSystemName == null || fileSystemName.length() == 0) {
//                                                throw new Exception("Missing filename");
//                                        }
                                        
                                        //Get the current User-Directory

                                        String current_dir = getServletContext().getRealPath("/");
                                        String working_dir = current_dir + "upload"
                                                        + File.separatorChar + "files"
                                                        + File.separatorChar;
                                        
                                        String working_dirppt = "";

                                        //System.out.println("IS SYSTEM PROFILE");
                                        //Add the Folder for the Room if it does not exist yet
                                        File localFolder = new File(working_dir);
                                        if (!localFolder.exists()) {
                                                localFolder.mkdir();
                                        }

                                        working_dirppt = current_dir + "uploadtemp" 
                                                + File.separatorChar + "files"
                                                + File.separatorChar;

                                        // add Temp Folder Structure
                                        File localFolderppt = new File(working_dirppt);
                                        if (!localFolderppt.exists()) {
                                                localFolderppt.mkdir();
                                        }

                                        log.debug("#### UploadHandler working_dir: "+ working_dir);

                                        

                                        ServletMultipartRequest upload = new ServletMultipartRequest(
                                                        httpServletRequest, 104857600*5, "utf-8"); // max 500 mb
                                        InputStream is = upload.getFileContents("Filedata");

                                        String fileSystemName = upload.getBaseFilename("Filedata");
                                        
                                        int dotidx=fileSystemName.lastIndexOf('.');
                                        
                                        //Generate a random string to prevent any problems with foreign characters and duplicates
                                        Date d = new Date();
                                        String newFileSystemName = MD5.do_checksum("FILE_"+d.getTime());
                                        
                                        String newFileSystemExtName = fileSystemName.substring(dotidx, fileSystemName.length()).toLowerCase();


                                        //Check variable to see if this file is a presentation
                                        //check if this is a a file that can be converted by openoffice-service
                                        boolean canBeConverted = checkForConvertion(newFileSystemExtName);
                                        boolean isPDF = checkForPDF(newFileSystemExtName);
                                        boolean isImage = checkForImage(newFileSystemExtName);
                                        boolean isJpg = checkForJpg(newFileSystemExtName);
                                        boolean isVideo = checkForVideo(newFileSystemExtName);
                                        
                                        String completeName = "";
                                        
                                        //add outputfolders for profiles
                                        //if it is a presenation it will be copied to another place
                                        if (canBeConverted || isPDF || isImage || isVideo) {
                                                completeName = working_dirppt + newFileSystemName;
                                        } else if (isJpg) {
                                                //check if this is a room file or UserProfile
                                                completeName = working_dir + newFileSystemName;
                                        } else {
                                                return;
                                        }

                                        log.debug("##### WRITE FILE TO: "+completeName + newFileSystemExtName);

                                        FileOutputStream fos = new FileOutputStream(completeName + newFileSystemExtName);
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
                                                ownerId = users_id;
                                        }
                                        if (isOwner) {
                                        	ownerId = users_id;
                                        }
                                        
                                        Boolean setImageFlag = false;
                                        if (isImage || isJpg) {
                                                setImageFlag = true;
                                        }
                                        
                                        String fileHashName = newFileSystemName+newFileSystemExtName;
                                        Boolean isPresentation = false;
                                        if (canBeConverted || isPDF) {
                                                //In case of a presentation the hash is a folder-name
                                                fileHashName = newFileSystemName;
                                                isPresentation = true;
                                        }
                                        if (isImage) {
                                                fileHashName = newFileSystemName+".jpg";
                                        }
                                        if (isVideo) {
                                            fileHashName = newFileSystemName+".flv";
                                        }
                                        
                                        
                                        FileExplorerItem fileExplorerItem = FileExplorerItemDaoImpl.getInstance().getFileExplorerItemsById(parentFolderId);
                                        
                                        if (fileExplorerItem != null) {
                                        	if (fileExplorerItem.getIsFolder() == null || !fileExplorerItem.getIsFolder()) {
                                        		parentFolderId = 0L;
                                        	}
                                        }
                                        
                                        Long fileExplorerItemId = FileExplorerItemDaoImpl.getInstance().addFileExplorerItem(
					                                                                fileSystemName, 
					                                                                fileHashName, //The Hashname of the file
					                                                                parentFolderId, 
					                                                                ownerId, room_id_to_Store, users_id, 
					                                                                false, //isFolder
					                                                                setImageFlag, 
					                                                                isPresentation);
                                        

                                        log.debug("canBeConverted: "+canBeConverted);
                                        if (canBeConverted) {
                                                //convert to pdf, thumbs, swf and xml-description
                                                returnError = GeneratePDF.getInstance().convertPDF(current_dir, newFileSystemName , newFileSystemExtName, "files", true, completeName);
                                        } else if (isPDF) {
                                                //convert to thumbs, swf and xml-description
                                                returnError = GeneratePDF.getInstance().convertPDF(current_dir, newFileSystemName , newFileSystemExtName, "files", false, completeName);                                                
                                        } else if (isImage) {
                                                
                                                //convert it to JPG
                                                log.debug("##### convert it to JPG: ");
                                                 returnError = GenerateImage.getInstance().convertImage(current_dir, newFileSystemName, 
                                                                                         newFileSystemExtName, "files",newFileSystemName, false);
                                        } else if (isJpg) {
                                                HashMap<String,Object> processThumb = GenerateThumbs.getInstance().generateThumb("_thumb_", current_dir, completeName, 50);
                                                returnError.put("processThumb", processThumb);
                                                
                                        } else if (isVideo) {
                                        	
                                        	//openmeetings.FlvExplorerConverter
                                        	
//                                        	ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
//                                        	
//                                        	FlvExplorerConverter flvExplorerConverter = (FlvExplorerConverter) context.getBean("openmeetings.FlvExplorerConverter");
//                                        	
//                                        	flvExplorerConverter.startConversion(fileExplorerItemId, completeName + newFileSystemExtName);
                                        	
                                        }
                                        
                                        
                                        //Flash cannot read the response of an upload
                                        //httpServletResponse.getWriter().print(returnError);
                                        LinkedHashMap<String,Object> hs = new LinkedHashMap<String,Object>();
                                        hs.put("user", UsersDaoImpl.getInstance().getUser(users_id));
                                        hs.put("message", "library");
                                        hs.put("action", "newFile");
                                        hs.put("fileExplorerItem", FileExplorerItemDaoImpl.getInstance().getFileExplorerItemsById(fileExplorerItemId));
                                        hs.put("error", returnError);
                                        hs.put("fileName", completeName);        
                                        
                                        ScopeApplicationAdapter.getInstance().sendMessageWithClientByPublicSID(hs,publicSID);
                                                
                                }
                        }
                } catch (Exception e) {
                        System.out.println("ee " + e);
                        log.error("[FileExplorerUploadHandler]",e);
                        e.printStackTrace();
                        throw new ServletException(e);
                }
                return;
        }
        
        private void deleteUserProfileFilesStoreTemp(String current_dir, Long users_id) throws Exception{
                
                String working_imgdir = current_dir + "upload" + File.separatorChar + "profiles" + File.separatorChar + "profile_"+users_id + File.separatorChar;
                File f = new File(working_imgdir);
                if (f.exists() && f.isDirectory()) {
                        this.filesString = f.list();
                }
        }
        
        private void deleteUserProfileFiles(String current_dir, Long users_id) throws Exception{
                
                String working_imgdir = current_dir + "upload" + File.separatorChar + "profiles" + File.separatorChar + "profile_"+users_id + File.separatorChar;

                for (int i=0;i<this.filesString.length;i++) {
                        String fileName = filesString[i];
                        File subf = new File(working_imgdir + fileName);
                        subf.delete();
                }
        }
        
        private boolean checkForConvertion(String fileExtension) throws Exception {
                Iterator<String> extensionIt = fileExtensions.keySet().iterator();
                while (extensionIt.hasNext()) {
                        String fileExt = fileExtensions.get(extensionIt.next());
                        if (fileExtension.equals(fileExt)) {
                                return true;
                        }
                }
                return false;
        }

        private boolean checkForPDF(String fileExtension) throws Exception {
                Iterator<String> extensionIt = pdfExtensions.keySet().iterator();
                while (extensionIt.hasNext()) {
                        String fileExt = pdfExtensions.get(extensionIt.next());
                        if (fileExtension.equals(fileExt)) {
                                return true;
                        }
                }
                return false;
        }

        private boolean checkForJpg(String fileExtension) throws Exception {
                Iterator<String> extensionIt = jpgExtensions.keySet().iterator();
                while (extensionIt.hasNext()) {
                        String fileExt = jpgExtensions.get(extensionIt.next());
                        if (fileExtension.equals(fileExt)) {
                                return true;
                        }
                }
                return false;
        }

        private boolean checkForImage(String fileExtension) throws Exception {
                Iterator<String> extensionIt = imageExtensions.keySet().iterator();
                while (extensionIt.hasNext()) {
                        String fileExt = imageExtensions.get(extensionIt.next());
                        if (fileExtension.equals(fileExt)) {
                                return true;
                        }
                }
                return false;
        }
        
        private boolean checkForVideo(String fileExtension) throws Exception {
            Iterator<String> extensionIt = videoExtensions.keySet().iterator();
            while (extensionIt.hasNext()) {
                    String fileExt = videoExtensions.get(extensionIt.next());
                    if (fileExtension.equals(fileExt)) {
                            return true;
                    }
            }
            return false;
    }

}
