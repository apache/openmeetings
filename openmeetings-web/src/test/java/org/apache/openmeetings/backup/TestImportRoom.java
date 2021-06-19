/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.backup;

import static org.apache.openmeetings.backup.TestImport.BACKUP_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestImportRoom extends AbstractTestImport {
	@Autowired
	private RoomDao roomDao;

	@Test
	void importRooms() throws Exception {
		long roomsCount = roomDao.count();
		File rooms = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "room/rooms.xml").toURI());
		backupImport.importRooms(rooms.getParentFile());
		assertEquals(roomsCount + 1, roomDao.count(), "Room should be added");
	}

	@Test
	void importRoomGroups() throws Exception {
		//TODO need to check it somehow
		File rooms = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "room/rooms.xml").toURI());
		backupImport.importRoomGroups(rooms.getParentFile());
	}
}
