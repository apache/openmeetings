package org.openmeetings.app.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.conference.whiteboard.WhiteboardManagement;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.file.FileUtils;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.file.dto.LibraryPresentation;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.documents.LibraryChartLoader;
import org.openmeetings.app.documents.LibraryDocumentConverter;
import org.openmeetings.app.documents.LibraryWmlLoader;
import org.openmeetings.app.documents.LoadLibraryPresentation;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ConferenceLibrary implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConferenceLibrary.class, ScopeApplicationAdapter.webAppRootKey);

	// Beans, see red5-web.xml
	private ClientListManager clientListManager = null;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private FileExplorerItemDaoImpl fileExplorerItemDao;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private LibraryWmlLoader libraryWmlLoader;
	@Autowired
	private FileUtils fileUtils;

	public ClientListManager getClientListManager() {
		return clientListManager;
	}

	public void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}

	private ConferenceLibrary() {
	}

	public LibraryPresentation getPresentationPreviewFileExplorer(String SID,
			String parentFolder) {

		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("#############users_id : " + users_id);
			log.debug("#############user_level : " + user_level);

			if (authLevelManagement.checkUserLevel(user_level)) {

				String current_dir = ScopeApplicationAdapter.webAppPath
						+ File.separatorChar + "upload";
				String working_dir = current_dir + File.separatorChar + "files"
						+ File.separatorChar + parentFolder;
				log.debug("############# working_dir : " + working_dir);

				File file = new File(working_dir + File.separatorChar
						+ "library.xml");

				if (!file.exists()) {
					throw new Exception("library.xml does not exist "
							+ working_dir + File.separatorChar + "library.xml");
				}

				return LoadLibraryPresentation.getInstance()
						.parseLibraryFileToObject(file.getAbsolutePath());

			} else {
				throw new Exception("not Authenticated");
			}

		} catch (Exception e) {
			log.error("[getListOfFilesByAbsolutePath]", e);
			return null;
		}

	}

	/**
	 * 
	 * Save an Object to the library and returns the file-explorer Id
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileName
	 * @param tObjectRef
	 * @return
	 */
	public Long saveAsObject(String SID, Long room_id, String fileName,
			Object tObjectRef) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {
				// LinkedHashMap tObject = (LinkedHashMap)t;
				// ArrayList tObject = (ArrayList)t;

				log.debug("saveAsObject :1: " + tObjectRef);
				log.debug("saveAsObject :2: " + tObjectRef.getClass().getName());

				@SuppressWarnings("rawtypes")
				ArrayList tObject = (ArrayList) tObjectRef;

				log.debug("saveAsObject" + tObject.size());

				String current_dir = ScopeApplicationAdapter.webAppPath
						+ File.separatorChar + "upload" + File.separatorChar;

				log.debug("### this is my working directory: " + current_dir);

				String localFileName = MD5.do_checksum(new Date().toString())
						+ ".wml";

				LibraryDocumentConverter.getInstance().writeToLocalFolder(
						current_dir, localFileName, tObject);

				// String wmlPath = current_dir + File.separatorChar+fileName
				// +".xml";
				// OwnerID == null
				Long fileExplorerId = fileExplorerItemDao.add(fileName, "", 0L,
						null, room_id, users_id, false, // isFolder
						false, // isImage
						false, // isPresentation
						localFileName, // WML localFileName
						true, // isStoredWML file
						true, 0L, "");

				return fileExplorerId;
			}
		} catch (Exception err) {
			log.error("[saveAsObject] ", err);
		}
		return -1L;
	}

	/**
	 * 
	 * Loads a Object from the library into the whiteboard of all participant of
	 * the current room
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileExplorerItemId
	 * @param whiteboardId
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadWmlObject(String SID, Long room_id,
			Long fileExplorerItemId, Long whiteboardId) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				IConnection current = Red5.getConnectionLocal();
				RoomClient currentClient = this.clientListManager
						.getClientByStreamId(current.getClient().getId());

				if (currentClient == null) {
					return;
				}

				String current_dir = ScopeApplicationAdapter.webAppPath
						+ File.separatorChar + "upload" + File.separatorChar;
				log.debug("### this is my working directory: " + current_dir);

				FileExplorerItem fileExplorerItem = fileExplorerItemDao
						.getFileExplorerItemsById(fileExplorerItemId);

				ArrayList roomItems = libraryWmlLoader.loadWmlFile(current_dir,
						fileExplorerItem.getWmlFilePath());

				Map whiteboardObjClear = new HashMap();
				whiteboardObjClear.put(2, "clear");
				whiteboardObjClear.put(3, null);

				WhiteboardManagement.getInstance().addWhiteBoardObjectById(
						room_id, whiteboardObjClear, whiteboardId);

				for (int k = 0; k < roomItems.size(); k++) {

					ArrayList actionObject = (ArrayList) roomItems.get(k);

					Map whiteboardObj = new HashMap();
					whiteboardObj.put(2, "draw");
					whiteboardObj.put(3, actionObject);

					WhiteboardManagement.getInstance().addWhiteBoardObjectById(
							room_id, whiteboardObj, whiteboardId);

				}

				Map<String, Object> sendObject = new HashMap<String, Object>();
				sendObject.put("id", whiteboardId);
				sendObject.put("roomitems", roomItems);

				// Notify all Clients of that Scope (Room)
				Collection<Set<IConnection>> conCollection = current.getScope()
						.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							if (conn instanceof IServiceCapableConnection) {
								RoomClient rcl = this.clientListManager
										.getClientByStreamId(conn.getClient()
												.getId());
								if ((rcl == null)
										|| (rcl.getIsScreenClient() != null && rcl
												.getIsScreenClient())) {
									continue;
								} else {
									// log.debug("*..*idremote: " +
									// rcl.getStreamid());
									// log.debug("*..* sendVars room_id IS EQUAL: "
									// + currentClient.getStreamid() + " asd " +
									// rcl.getStreamid() + " IS eq? "
									// +currentClient.getStreamid().equals(rcl.getStreamid()));
									((IServiceCapableConnection) conn).invoke(
											"loadWmlToWhiteboardById",
											new Object[] { sendObject }, this);
									// log.debug("sending sendVarsToWhiteboard to "
									// + conn + " rcl " + rcl);
								}
							}
						}
					}
				}

			}
		} catch (Exception err) {
			log.error("[loadWmlObject] ", err);
		}
	}

	/**
	 * 
	 * Loads a chart object
	 * 
	 * @param SID
	 * @param room_id
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList loadChartObject(String SID, Long room_id, String fileName) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkUserLevel(user_level)) {
				String roomName = room_id.toString();
				String current_dir = ScopeApplicationAdapter.webAppPath
						+ File.separatorChar + "upload" + File.separatorChar
						+ roomName + File.separatorChar;
				log.debug("### this is my working directory: " + current_dir);

				return LibraryChartLoader.getInstance().loadChart(current_dir,
						fileName);
			}
		} catch (Exception err) {
			log.error("[loadChartObject] ", err);
		}
		return null;
	}

	/**
	 * 
	 * Load all objects of a conference room
	 * 
	 * @param SID
	 * @param room_id
	 * @return
	 * 
	 *         public FileExplorerObject getFileExplorerByRoom(String SID, Long
	 *         room_id) { try { Long users_id =
	 *         Sessionmanagement.getInstance().checkSession(SID); Long
	 *         user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("room_id " + room_id);
	 * 
	 *         FileExplorerObject fileExplorerObject = new FileExplorerObject();
	 * 
	 *         // Home File List FileExplorerItem[] fList =
	 *         FileExplorerItemDaoImpl .getInstance()
	 *         .getFileExplorerItemsByOwner(users_id, 0L);
	 * 
	 *         long homeFileSize = 0;
	 * 
	 *         for (FileExplorerItem homeChildExplorerItem : fList) {
	 *         log.debug("FileExplorerItem fList " +
	 *         homeChildExplorerItem.getFileName()); homeFileSize +=
	 *         FileUtils.getInstance()
	 *         .getSizeOfDirectoryAndSubs(homeChildExplorerItem); }
	 * 
	 *         fileExplorerObject.setUserHome(fList);
	 *         fileExplorerObject.setUserHomeSize(homeFileSize);
	 * 
	 *         // Public File List FileExplorerItem[] rList =
	 *         FileExplorerItemDaoImpl
	 *         .getInstance().getFileExplorerItemsByRoom(room_id, 0L);
	 * 
	 *         long roomFileSize = 0;
	 * 
	 *         for (FileExplorerItem homeChildExplorerItem : rList) {
	 *         log.debug("FileExplorerItem rList " +
	 *         homeChildExplorerItem.getFileName()); roomFileSize +=
	 *         FileUtils.getInstance()
	 *         .getSizeOfDirectoryAndSubs(homeChildExplorerItem); }
	 * 
	 *         fileExplorerObject.setRoomHome(rList);
	 *         fileExplorerObject.setRoomHomeSize(roomFileSize);
	 * 
	 *         return fileExplorerObject;
	 * 
	 *         } } catch (Exception err) { log.error("[getFileExplorerByRoom] ",
	 *         err); } return null; }
	 * 
	 *         public FileExplorerItem[] getFileExplorerByParent(String SID,
	 *         Long parentFileExplorerItemId, Long room_id, Boolean isOwner) {
	 *         try { Long users_id =
	 *         Sessionmanagement.getInstance().checkSession(SID); Long
	 *         user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("parentFileExplorerItemId " +
	 *         parentFileExplorerItemId);
	 * 
	 *         if (parentFileExplorerItemId == 0) { if (isOwner) { return
	 *         FileExplorerItemDaoImpl.getInstance()
	 *         .getFileExplorerItemsByOwner(users_id, parentFileExplorerItemId);
	 *         } else { return FileExplorerItemDaoImpl.getInstance()
	 *         .getFileExplorerItemsByRoom(room_id, parentFileExplorerItemId); }
	 *         } else { return FileExplorerItemDaoImpl.getInstance()
	 *         .getFileExplorerItemsByParent( parentFileExplorerItemId); }
	 * 
	 *         } } catch (Exception err) {
	 *         log.error("[getFileExplorerByParent] ", err); } return null; }
	 * 
	 *         public Long addFolder(String SID, Long parentFileExplorerItemId,
	 *         String fileName, Long room_id, Boolean isOwner) { try { Long
	 *         users_id = Sessionmanagement.getInstance().checkSession(SID);
	 *         Long user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("addFolder " + parentFileExplorerItemId);
	 * 
	 *         if (parentFileExplorerItemId == 0 && isOwner) { // users_id
	 *         (OwnerID) => only set if its directly root in // Owner Directory,
	 *         // other Folders and Files maybe are also in a Home // directory
	 *         // but just because their parent is return
	 *         FileExplorerItemDaoImpl.getInstance().add(fileName, "",
	 *         parentFileExplorerItemId, users_id, room_id, users_id, true, //
	 *         isFolder false, // isImage false, // isPresentation "", // WML
	 *         Path false, // isStoredWML file false // isXmlFile , 0L, ""); }
	 *         else { return FileExplorerItemDaoImpl.getInstance().add(fileName,
	 *         "", parentFileExplorerItemId, null, room_id, users_id, true, //
	 *         isFolder false, // isImage false, // isPresentation "", // WML
	 *         Paht false, // isStoredWML file false // isXmlFile , 0L, ""); } }
	 *         } catch (Exception err) { log.error("[getFileExplorerByParent] ",
	 *         err); } return null; }
	 * 
	 *         public Long deleteFileOrFolder(String SID, Long
	 *         fileExplorerItemId) { try { Long users_id =
	 *         Sessionmanagement.getInstance().checkSession(SID); Long
	 *         user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("deleteFileOrFolder " + fileExplorerItemId);
	 * 
	 *         FileExplorerItemDaoImpl.getInstance().deleteFileExplorerItem(
	 *         fileExplorerItemId);
	 * 
	 *         } } catch (Exception err) {
	 *         log.error("[getFileExplorerByParent] ", err); } return null; }
	 * 
	 *         public Long updateFileOrFolderName(String SID, Long
	 *         fileExplorerItemId, String fileName) { try { Long users_id =
	 *         Sessionmanagement.getInstance().checkSession(SID); Long
	 *         user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("deleteFileOrFolder " + fileExplorerItemId);
	 * 
	 *         FileExplorerItemDaoImpl.getInstance().updateFileOrFolderName(
	 *         fileExplorerItemId, fileName);
	 * 
	 *         } } catch (Exception err) {
	 *         log.error("[updateFileOrFolderName] ", err); } return null; }
	 * 
	 *         public Long moveFile(String SID, Long fileExplorerItemId, Long
	 *         newParentFileExplorerItemId, Long room_id, Boolean isOwner,
	 *         Boolean moveToHome) { try { Long users_id =
	 *         Sessionmanagement.getInstance().checkSession(SID); Long
	 *         user_level = userManagement.getUserLevelByID( users_id); if
	 *         (authLevelManagement.checkUserLevel(user_level)) {
	 * 
	 *         log.debug("deleteFileOrFolder " + fileExplorerItemId);
	 * 
	 *         FileExplorerItemDaoImpl.getInstance().moveFile(
	 *         fileExplorerItemId, newParentFileExplorerItemId, room_id,
	 *         isOwner, users_id);
	 * 
	 *         FileExplorerItem fileExplorerItem = FileExplorerItemDaoImpl
	 *         .getInstance().getFileExplorerItemsById( fileExplorerItemId);
	 * 
	 *         if (moveToHome) { // set this file and all subfiles and folders
	 *         the ownerId
	 *         FileUtils.getInstance().setFileToOwnerOrRoomByParent(fileExplorerItem
	 *         , users_id, null);
	 * 
	 *         } else { // set this file and all subfiles and folders the
	 *         room_id
	 *         FileUtils.getInstance().setFileToOwnerOrRoomByParent(fileExplorerItem
	 *         , null, room_id);
	 * 
	 *         }
	 * 
	 *         } } catch (Exception err) {
	 *         log.error("[updateFileOrFolderName] ", err); } return null; }
	 */

	public Long copyFileToCurrentRoom(String SID, Long flvFileExplorerId) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				IConnection current = Red5.getConnectionLocal();
				String streamid = current.getClient().getId();

				RoomClient currentClient = this.clientListManager
						.getClientByStreamId(streamid);

				Long room_id = currentClient.getRoom_id();

				if (room_id != null) {

					String streamFolderName = ScopeApplicationAdapter.webAppPath
							+ File.separatorChar
							+ "streams"
							+ File.separatorChar
							+ "hibernate"
							+ File.separatorChar;

					String outputFullFlv = streamFolderName + "UPLOADFLV_"
							+ flvFileExplorerId + ".flv";

					String targetFolderName = ScopeApplicationAdapter.webAppPath
							+ File.separatorChar
							+ "streams"
							+ File.separatorChar + room_id + File.separatorChar;

					File targetFolder = new File(targetFolderName);
					if (!targetFolder.exists()) {
						targetFolder.mkdir();
					}

					String targetFullFlv = targetFolderName + "UPLOADFLV_"
							+ flvFileExplorerId + ".flv";

					File outputFullFlvFile = new File(outputFullFlv);
					File targetFullFlvFile = new File(targetFullFlv);
					if (outputFullFlvFile.exists()) {

						if (!targetFullFlvFile.exists()) {
							fileUtils.copyFile(outputFullFlv, targetFullFlv);
						}

					}

					return 1L;
				}

			}

		} catch (Exception err) {
			log.error("[copyFileToCurrentRoom] ", err);
		}
		return -1L;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub

	}

}
