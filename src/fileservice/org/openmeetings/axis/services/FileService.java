package org.openmeetings.axis.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.red5.logging.Red5LoggerFactory;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.files.*;
import org.openmeetings.app.data.file.FileProcessor;
import org.openmeetings.app.data.file.FileUtils;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.file.dto.FileExplorerObject;
import org.openmeetings.app.data.file.dto.LibraryPresentation;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.LoadLibraryPresentation;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.ConferenceLibrary;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.StoredFile;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;

public class FileService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FileService.class, ScopeApplicationAdapter.webAppRootKey);
	
	public ServletContext getServletContext()
	{
		MessageContext mc = MessageContext.getCurrentMessageContext();
		return (ServletContext) mc.getProperty(HTTPConstants.MC_HTTP_SERVLETCONTEXT);
	}
	
	/**
	 * 
	 * Import file from external source
	 * 
	 * to upload a file to a room-drive you specify:
	 * externalUserId, user if of openmeetings user for which we upload the file
	 * room_id = openmeetings room id
	 * isOwner = 0
	 * parentFolderId = 0
	 * 
	 * to upload a file to a private-drive you specify:
	 * externalUserId, user if of openmeetings user for which we upload the file
	 * room_id = openmeetings room id
	 * isOwner = 1
	 * parentFolderId = -2
	 * 
	 * @param SID
	 * @param externalUserId the external user id => If the file should goto a private section of any user, this number needs to be set
	 * @param externalFileId the external file-type to identify the file later
	 * @param externalType the name of the external system
	 * @param room_id the room Id, if the file goes to the private folder of an user, you can set a random number here
	 * @param isOwner specify a 1/true AND parentFolderId==-2 to make the file goto the private section
	 * @param path http-path where we can grab the file from, the file has to be accessible from the OpenMeetings server
	 * @param parentFolderId specify a parentFolderId==-2 AND isOwner == 1/true AND to make the file goto the private section
	 * @param fileSystemName the filename => Important WITH file extension!
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFile(String SID, Long externalUserId, Long externalFileId, 
					String externalType, Long room_id, boolean isOwner, String path, 
					Long parentFolderId, String fileSystemName) throws AxisFault{
		try {
		
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
				String current_dir = getServletContext().getRealPath("/");
				
				ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		        FileProcessor fileProcessor = (FileProcessor) context.getBean("openmeetings.FileProcessor");
				
		        URL url = new URL(path);
		        URLConnection uc = url.openConnection();
		        InputStream inputstream = new BufferedInputStream(uc.getInputStream());
		        
		        Users externalUser = Usermanagement.getInstance().getUserByExternalIdAndType(externalUserId, externalType);
		        
		        LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
				hs.put("user", externalUser);
		        
				HashMap<String, HashMap<String, Object>> returnError = fileProcessor.processFile(externalUser.getUser_id(), room_id, isOwner, inputstream, parentFolderId, fileSystemName, current_dir, hs, externalFileId, externalType);
		
				HashMap<String, Object> returnAttributes = returnError.get("returnAttributes");
		        
		        // Flash cannot read the response of an upload
		        // httpServletResponse.getWriter().print(returnError);
		        hs.put("message", "library");
		        hs.put("action", "newFile");
		        hs.put("fileExplorerItem", FileExplorerItemDaoImpl.getInstance()
		                .getFileExplorerItemsById(Long.parseLong(returnAttributes.get("fileExplorerItemId").toString())));
		        hs.put("error", returnError);
		        hs.put("fileName", returnAttributes.get("completeName"));
				
		        FileImportError[] fileImportErrors = new FileImportError[returnError.size()];
		        
		        int i = 0;
				//Axis need Objects or array of objects, Map won't work
				for (Iterator<String> iter = returnError.keySet().iterator();iter.hasNext();) {
					
					HashMap<String, Object> returnAttribute = returnError.get(iter.next());
					
					fileImportErrors[i] = new FileImportError();
					fileImportErrors[i].setCommand((returnAttribute.get("command")!=null) ? returnAttribute.get("command").toString() : "");
					fileImportErrors[i].setError((returnAttribute.get("error")!=null) ? returnAttribute.get("error").toString() : "");
					fileImportErrors[i].setExitValue((returnAttribute.get("exitValue")!=null) ? Integer.valueOf(returnAttribute.get("exitValue").toString()).intValue() : 0);
					fileImportErrors[i].setProcess((returnAttribute.get("process")!=null) ? returnAttribute.get("process").toString() : "");
					
					i++;
				}
				
				return fileImportErrors;
				
	        }
		} catch (Exception err) {
			log.error("[importFile]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * Import file from external source
	 * 
	 * to upload a file to a room-drive you specify:
	 * internalUserId, user if of openmeetings user for which we upload the file
	 * room_id = openmeetings room id
	 * isOwner = 0
	 * parentFolderId = 0
	 * 
	 * to upload a file to a private-drive you specify:
	 * internalUserId, user if of openmeetings user for which we upload the file
	 * room_id = openmeetings room id
	 * isOwner = 1
	 * parentFolderId = -2
	 * 
	 * @param SID
	 * @param internalUserId the openmeetings user id => If the file should goto a private section of any user, this number needs to be set
	 * @param externalFileId the external file-type to identify the file later
	 * @param externalType the name of the external system
	 * @param room_id the room Id, if the file goes to the private folder of an user, you can set a random number here
	 * @param isOwner specify a 1/true AND parentFolderId==-2 to make the file goto the private section
	 * @param path http-path where we can grab the file from, the file has to be accessible from the OpenMeetings server
	 * @param parentFolderId specify a parentFolderId==-2 AND isOwner == 1/true AND to make the file goto the private section
	 * @param fileSystemName the filename => Important WITH file extension!
	 * @return
	 * @throws AxisFault
	 */
	public FileImportError[] importFileByInternalUserId(String SID, Long internalUserId, Long externalFileId, 
					String externalType, Long room_id, boolean isOwner, String path, 
					Long parentFolderId, String fileSystemName) throws AxisFault{
		try {
		
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
				String current_dir = getServletContext().getRealPath("/");
				
				ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		        FileProcessor fileProcessor = (FileProcessor) context.getBean("openmeetings.FileProcessor");
				
		        URL url = new URL(path);
		        URLConnection uc = url.openConnection();
		        InputStream inputstream = new BufferedInputStream(uc.getInputStream());
		        
		        Users internalUser = Usermanagement.getInstance().getUserById(internalUserId);
		        
		        LinkedHashMap<String, Object> hs = new LinkedHashMap<String, Object>();
				hs.put("user", internalUser);
		        
				HashMap<String, HashMap<String, Object>> returnError = fileProcessor.processFile(internalUser.getUser_id(), room_id, isOwner, inputstream, parentFolderId, fileSystemName, current_dir, hs, externalFileId, externalType);
		
				HashMap<String, Object> returnAttributes = returnError.get("returnAttributes");
		        
		        // Flash cannot read the response of an upload
		        // httpServletResponse.getWriter().print(returnError);
		        hs.put("message", "library");
		        hs.put("action", "newFile");
		        hs.put("fileExplorerItem", FileExplorerItemDaoImpl.getInstance()
		                .getFileExplorerItemsById(Long.parseLong(returnAttributes.get("fileExplorerItemId").toString())));
		        hs.put("error", returnError);
		        hs.put("fileName", returnAttributes.get("completeName"));
				
		        FileImportError[] fileImportErrors = new FileImportError[returnError.size()];
		        
		        int i = 0;
				//Axis need Objects or array of objects, Map won't work
				for (Iterator<String> iter = returnError.keySet().iterator();iter.hasNext();) {
					
					HashMap<String, Object> returnAttribute = returnError.get(iter.next());
					
					fileImportErrors[i] = new FileImportError();
					fileImportErrors[i].setCommand((returnAttribute.get("command")!=null) ? returnAttribute.get("command").toString() : "");
					fileImportErrors[i].setError((returnAttribute.get("error")!=null) ? returnAttribute.get("error").toString() : "");
					fileImportErrors[i].setExitValue((returnAttribute.get("exitValue")!=null) ? Integer.valueOf(returnAttribute.get("exitValue").toString()).intValue() : 0);
					fileImportErrors[i].setProcess((returnAttribute.get("process")!=null) ? returnAttribute.get("process").toString() : "");
					
					i++;
				}
				
				return fileImportErrors;
				
	        }
		} catch (Exception err) {
			log.error("[importFile]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and externalUserId/externalUserType to a valid user
	 * 
	 * @param SID
	 * @param externalUserId
	 * @param externalUserType
	 * @param parentFileExplorerItemId
	 * @param fileName
	 * @param room_id
	 * @param isOwner
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 * @throws AxisFault
	 */
	public Long addFolderByExternalUserIdAndType(String SID, Long externalUserId, 
			Long parentFileExplorerItemId, String folderName, Long room_id, Boolean isOwner,
			Long externalFilesid, String externalType) throws AxisFault{
		try {
		
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
	        	Users userExternal = Usermanagement.getInstance().getUserByExternalIdAndType(externalUserId, externalType);
				
				Long userId = userExternal.getUser_id();
	        	
	        	log.debug("addFolder " + parentFileExplorerItemId);

	        	if (parentFileExplorerItemId == -2 && isOwner) {
                    // users_id (OwnerID) => only set if its directly root in
                    // Owner Directory,
                    // other Folders and Files maybe are also in a Home
                    // directory
                    // but just because their parent is
                    return FileExplorerItemDaoImpl.getInstance().add(folderName,
                            "", 0L, userId, room_id,
                            userId, true, // isFolder
                            false, // isImage
                            false, // isPresentation
                            "", // WML Path
                            false, // isStoredWML file
                            false, // isXmlFile
                            externalFilesid, externalType);
                } else {
                    return FileExplorerItemDaoImpl.getInstance().add(folderName,
                            "", parentFileExplorerItemId, null, room_id,
                            userId, true, // isFolder
                            false, // isImage
                            false, // isPresentation
                            "", // WML Path
                            false, // isStoredWML file
                            false, // isXmlFile
                            externalFilesid, externalType);
                }
	        }
	        
		} catch (Exception err) {
			log.error("[addFolderByExternalUserIdAndType]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * to add a folder to the private drive, set parentFileExplorerItemId = 0
	 * and isOwner to 1/true and userId to a valid user
	 * 
	 * @param SID
	 * @param userId
	 * @param parentFileExplorerItemId
	 * @param fileName
	 * @param room_id
	 * @param isOwner
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 * @throws AxisFault
	 */
	public Long addFolderByUserId(String SID, Long userId, 
			Long parentFileExplorerItemId, String folderName, Long room_id, Boolean isOwner,
			Long externalFilesid, String externalType) throws AxisFault{
		try {
		
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
	        	log.debug("addFolder " + parentFileExplorerItemId);

                if (parentFileExplorerItemId == -2 && isOwner) {
                    // users_id (OwnerID) => only set if its directly root in
                    // Owner Directory,
                    // other Folders and Files maybe are also in a Home
                    // directory
                    // but just because their parent is
                    return FileExplorerItemDaoImpl.getInstance().add(folderName,
                            "", 0L, userId, room_id,
                            userId, true, // isFolder
                            false, // isImage
                            false, // isPresentation
                            "", // WML Path
                            false, // isStoredWML file
                            false, // isXmlFile
                            externalFilesid, externalType);
                } else {
                    return FileExplorerItemDaoImpl.getInstance().add(folderName,
                            "", parentFileExplorerItemId, null, room_id,
                            userId, true, // isFolder
                            false, // isImage
                            false, // isPresentation
                            "", // WML Path
                            false, // isStoredWML file
                            false, // isXmlFile
                            externalFilesid, externalType);
                }
	        }
	        
		} catch (Exception err) {
			log.error("[addFolderByUserId]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * deletes a file by its external Id and type
	 * 
	 * @param SID
	 * @param externalFilesid
	 * @param externalType
	 * @return
	 */
	public Long deleteFileOrFolderByExternalIdAndType(String SID, Long externalFilesid, String externalType) throws AxisFault {
		
		try {
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
	        	FileExplorerItemDaoImpl.getInstance().deleteFileExplorerItemByExternalIdAndType(
	        			externalFilesid, externalType);
	        	
	        }
		
		} catch (Exception err) {
			log.error("[deleteFileOrFolderByExternalIdAndType]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * deletes files or folders based on it id
	 * 
	 * @param SID
	 * @param fileExplorerItemId
	 * @return
	 */
	public Long deleteFileOrFolder(String SID, Long fileExplorerItemId) throws AxisFault {
		
		try {
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long User_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(User_level)){
	        	
	        	FileExplorerItemDaoImpl.getInstance().deleteFileExplorerItem(
                        fileExplorerItemId);
	        	
	        }
		
		} catch (Exception err) {
			log.error("[deleteFileOrFolder]",err);
		}
		return null;
	}
   
	public String[] getImportFileExtensions() throws AxisFault {
		try {
			
			return StoredFile.getExtensions();
		
		} catch (Exception err) {
			log.error("[getImportFileExtensions]",err);
		}
		return null;
	}
	
	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) throws AxisFault {

	    try {
	
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(
	                users_id);
	
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(user_level)) {
	
	            String current_dir = ScopeApplicationAdapter.webAppPath
	                    + File.separatorChar + "upload";
	            String working_dir = current_dir + File.separatorChar + "files"
	            		+ File.separatorChar+ parentFolder;
	            log.debug("############# working_dir : " + working_dir);
	
	            File file = new File(working_dir + File.separatorChar + "library.xml");
	
	            if (!file.exists()) {
	            	throw new Exception("library.xml does not exist "+working_dir + File.separatorChar + "library.xml");
	            }
	            
	            return LoadLibraryPresentation.getInstance()
	                                    .parseLibraryFileToObject(
	                                            file.getAbsolutePath());
	           
	        } else {
	        	
	            throw new Exception("not Authenticated");
	            
	        }
	
	    } catch (Exception e) {
	        log.error("[getListOfFilesByAbsolutePath]", e);
	        return null;
	    }
	
	}
	
	public FileExplorerObject getFileExplorerByRoom(String SID, Long room_id, Long owner_id) throws AxisFault {

	    try {
	
	        Long webservice_users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(
	        		webservice_users_id);
	
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(user_level)) {
	        	
	        	log.debug("room_id " + room_id);

                FileExplorerObject fileExplorerObject = new FileExplorerObject();

                // Home File List
                FileExplorerItem[] fList = FileExplorerItemDaoImpl
                        .getInstance()
                        .getFileExplorerItemsByOwner(owner_id, 0L);

                long homeFileSize = 0;

                for (FileExplorerItem homeChildExplorerItem : fList) {
                    log.debug("FileExplorerItem fList "
                            + homeChildExplorerItem.getFileName());
                    homeFileSize += FileUtils.getInstance()
                            .getSizeOfDirectoryAndSubs(homeChildExplorerItem);
                }

                fileExplorerObject.setUserHome(fList);
                fileExplorerObject.setUserHomeSize(homeFileSize);

                // Public File List
                FileExplorerItem[] rList = FileExplorerItemDaoImpl
                        .getInstance().getFileExplorerItemsByRoom(room_id, 0L);

                long roomFileSize = 0;

                for (FileExplorerItem homeChildExplorerItem : rList) {
                    log.debug("FileExplorerItem rList "
                            + homeChildExplorerItem.getFileName());
                    roomFileSize += FileUtils.getInstance()
                            .getSizeOfDirectoryAndSubs(homeChildExplorerItem);
                }

                fileExplorerObject.setRoomHome(rList);
                fileExplorerObject.setRoomHomeSize(roomFileSize);

                return fileExplorerObject;
	        	
	        } else {
	        	
	            throw new Exception("not Authenticated");
	            
	        }
	        
	    } catch (Exception e) {
	        log.error("[getListOfFilesByAbsolutePath]", e);
	        return null;
	    }	        
	}
	
	public FileExplorerItem[] getFileExplorerByParent(String SID,
            Long parentFileExplorerItemId, Long room_id, Boolean isOwner, Long owner_id) throws AxisFault {
		
		try {
			
	        Long webservice_users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(
	        		webservice_users_id);
	
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(user_level)) {

                log.debug("parentFileExplorerItemId "
                        + parentFileExplorerItemId);

                if (parentFileExplorerItemId == 0) {
                    if (isOwner) {
                        return FileExplorerItemDaoImpl.getInstance()
                                .getFileExplorerItemsByOwner(owner_id,
                                        parentFileExplorerItemId);
                    } else {
                        return FileExplorerItemDaoImpl.getInstance()
                                .getFileExplorerItemsByRoom(room_id,
                                        parentFileExplorerItemId);
                    }
                } else {
                    return FileExplorerItemDaoImpl.getInstance()
                            .getFileExplorerItemsByParent(
                                    parentFileExplorerItemId);
                }

            }
        } catch (Exception err) {
            log.error("[getFileExplorerByParent] ", err);
        }
        return null;
    }
	
	public Long updateFileOrFolderName(String SID, Long fileExplorerItemId,
            String fileName) throws AxisFault {
        		
		try {
			
	        Long webservice_users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(
	        		webservice_users_id);
	
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(user_level)) {

                log.debug("deleteFileOrFolder " + fileExplorerItemId);

                FileExplorerItemDaoImpl.getInstance().updateFileOrFolderName(
                        fileExplorerItemId, fileName);

            }
        } catch (Exception err) {
            log.error("[updateFileOrFolderName] ", err);
        }
        return null;
    }
	
	public Long moveFile(String SID, Long fileExplorerItemId,
            Long newParentFileExplorerItemId, Long room_id, Boolean isOwner,
            Boolean moveToHome, Long owner_id) throws AxisFault {
        		
		try {
			
	        Long webservice_users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(
	        		webservice_users_id);
	
	        if (AuthLevelmanagement.getInstance().checkWebServiceLevel(user_level)) {

                log.debug("deleteFileOrFolder " + fileExplorerItemId);

                FileExplorerItemDaoImpl.getInstance().moveFile(
                        fileExplorerItemId, newParentFileExplorerItemId,
                        room_id, isOwner, owner_id);

                FileExplorerItem fileExplorerItem = FileExplorerItemDaoImpl
                        .getInstance().getFileExplorerItemsById(
                                fileExplorerItemId);

                if (moveToHome) {
                    // set this file and all subfiles and folders the ownerId
                	FileUtils.getInstance().setFileToOwnerOrRoomByParent(fileExplorerItem,
                			owner_id, null);

                } else {
                    // set this file and all subfiles and folders the room_id
                	FileUtils.getInstance().setFileToOwnerOrRoomByParent(fileExplorerItem, null,
                            room_id);

                }

            }
        } catch (Exception err) {
            log.error("[moveFile] ", err);
        }
        return null;
    }
	
	public TestObject getTestObject(){
		TestObject textO = new TestObject();
		textO.setList1(new LinkedList<String>());
		textO.setList2(new LinkedList<String>());
		return new TestObject();
	}
	
    public OMElement echo(OMElement element) throws XMLStreamException {
        //Praparing the OMElement so that it can be attached to another OM Tree.
        //First the OMElement should be completely build in case it is not fully built and still
        //some of the xml is in the stream.
        element.build();
        //Secondly the OMElement should be detached from the current OMTree so that it can be attached
        //some other OM Tree. Once detached the OmTree will remove its connections to this OMElement.
        element.detach();
        return element;
    }

    public void ping(OMElement element) throws XMLStreamException {
        //Do some processing
    	System.out.println("PING PING 1");
    	Long ch = Sessionmanagement.getInstance().checkSession("12312312");
    	System.out.println("PING PING 1 ch: "+ch);
    }
    
    public void pingF(OMElement element) throws AxisFault{
        throw new AxisFault("Fault being thrown");
    }
    
}
