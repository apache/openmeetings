package org.openmeetings.test.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.hibernate.beans.files.FileExplorerItem;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.test.dao.base.AbstractTestCase;

public class FileExplorerItemDaoImplTest extends AbstractTestCase {

	public FileExplorerItemDaoImplTest(String name) {
		super(name);
	}
	
	final public void testFileExplorerItemDaoImpl() throws Exception {
		
		Long userId = 1L;
		Users user = Usermanagement.getInstance().getUserById(userId);
		assertNotNull("Cann't get default user", user);
		
		List<Rooms> rooms = Roommanagement.getInstance().getPublicRooms(user.getLevel_id());
		assertNotNull("Cann't get public rooms fo default user", rooms);

		Rooms room = null;
		for (Iterator<Rooms> iter = rooms.iterator(); iter.hasNext();){
    		room = iter.next();
    		break;
    	}
		assertNotNull("Cann't get room for default user", room);
		
		FileExplorerItem fileExplorerItem = new FileExplorerItem();
		fileExplorerItem.setFileName("FileExplorerTest");
		fileExplorerItem.setFileHash("");
		fileExplorerItem.setRoom_id(room.getRooms_id());
		fileExplorerItem.setOwnerId(userId);
		fileExplorerItem.setWmlFilePath("");
		fileExplorerItem.setIsFolder(true);
		Long fileExplorerItemId = FileExplorerItemDaoImpl.getInstance().addFileExplorerItem(fileExplorerItem);
		assertNotNull("Cann't add file explorer item", fileExplorerItemId);
		
		fileExplorerItem = FileExplorerItemDaoImpl.getInstance().getFileExplorerItemsById(fileExplorerItemId);
		assertNotNull("Cann't get file explorer item", fileExplorerItem);
		
		FileExplorerItemDaoImpl.getInstance().deleteFileExplorerItem(fileExplorerItemId);
		
	}
}
