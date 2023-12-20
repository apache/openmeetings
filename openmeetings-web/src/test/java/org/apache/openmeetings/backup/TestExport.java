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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.bind.Constants.FILE_LIST_NODE;
import static org.apache.openmeetings.db.bind.Constants.USER_LIST_NODE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.openmeetings.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestExport extends AbstractOmServerTest {
	@Autowired
	private FileItemDao fileItemDao;

	@Test
	void exportMain() throws Exception {
		String backupPath = File.createTempFile("gereral", "cfg").getCanonicalPath();
		BackupExport.main(new String[] {backupPath});
		assertTrue(Paths.get(backupPath).toFile().exists(), "Backup should be created");
	}

	@Test
	void exportUser() throws Exception {
		User u = createUser();
		u.setAge(LocalDate.of(1977, 11, 13));
		Group g = groupDao.get(1L);
		u.addGroup(g);
		Class<User> eClazz = User.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		/* FIXME TODO
		marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
			@Override
			public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
				writer.write(ac, i, j);
			}
		});
		*/
		StringWriter writer = new StringWriter();
		marshaller.marshal(u, writer);
		Assertions.assertNotNull(writer.getBuffer());
	}

	@Test
	void exportUsers() throws Exception {
		ByteArrayOutputStream baos = BackupExport.stream(USER_LIST_NODE, userDao.getAllBackupUsers());
		assertNotNull(baos);
	}

	@Test
	void exportFiles() throws Exception {
		FileItem fld = new FileItem();
		fld.setName("folder");
		fld.setHash(randomUUID().toString());
		fld.setType(BaseFileItem.Type.FOLDER);
		fileItemDao.update(fld);
		FileItem f = new FileItem();
		f.setName("file");
		f.setHash(randomUUID().toString());
		f.setParentId(fld.getId());
		f.setType(BaseFileItem.Type.PRESENTATION);
		fileItemDao.update(f);
		ByteArrayOutputStream baos = BackupExport.stream(FILE_LIST_NODE, fileItemDao.get());
		assertNotNull(baos);
	}
}
