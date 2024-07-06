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
package org.apache.openmeetings.webservice;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.AbstractOmServerTest.UNIT_TEST_ARAB_EXT_TYPE;
import static org.apache.openmeetings.web.AbstractOmServerTest.createUser;
import static org.apache.openmeetings.web.AbstractOmServerTest.getUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.file.FileExplorerObject;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.NonJenkinsTest;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.junit.jupiter.api.Test;

class TestFileService extends AbstractWebServiceTest {

	@NonJenkinsTest
	void addFileTest() throws IOException {
		File img = null;
		try {
			img = File.createTempFile("omtest", ".jpg");
			final Integer width = 150;
			final Integer height = 100;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.drawString("Hello World!!!", 10, 20);
			ImageIO.write(image, "jpg", img);
			CallResult<FileItemDTO> cr = createVerifiedFile(img, "test.txt", BaseFileItem.Type.PRESENTATION);
			assertEquals(BaseFileItem.Type.IMAGE, cr.getObj().getType(), "Type should be Image");
			assertEquals(width, cr.getObj().getWidth(), "Width should be determined");
			assertEquals(height, cr.getObj().getHeight(), "Height should be Image");
		} finally {
			if (img != null) {
				Files.deleteIfExists(img.toPath());
			}
		}
	}

	@Test
	void testGetRoom() {
		ServiceResult r = login();
		FileExplorerObject fo = getClient(getFileUrl())
				.path("/room/5")
				.query("sid", r.getMessage())
				.get(FileExplorerObject.class);
		assertNotNull(fo);
	}

	@Test
	void testGetExternal() throws Exception {
		Group g = new Group();
		g.setExternal(true);
		g.setName(UNIT_TEST_ARAB_EXT_TYPE);
		g = getBean(GroupDao.class).update(g, null);
		User u = getUser(randomUUID().toString());
		u.addGroup(g);
		u = createUser(getBean(UserDao.class), u);

		FileItem f = new FileItem();
		f.setName("Arab external test");
		f.setType(BaseFileItem.Type.IMAGE);
		f.setInsertedBy(u.getId());
		getBean(FileItemDao.class).update(f);

		ServiceResult r = login();
		Collection<? extends FileItemDTO> list = getClient(getFileUrl())
				.path("/" + UrlEncoder.PATH_INSTANCE.encode(UNIT_TEST_ARAB_EXT_TYPE, UTF_8))
				.query("sid", r.getMessage())
				.getCollection(FileItemDTO.class);
		assertNotNull(list);
		assertFalse(list.isEmpty());
	}
}
