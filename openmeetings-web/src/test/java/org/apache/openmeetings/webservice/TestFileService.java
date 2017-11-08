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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.file.FileExplorerObject;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.test.NonJenkinsTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class TestFileService extends AbstractWebServiceTest {

	@Test
	@Category(NonJenkinsTests.class)
	public void addFileTest() throws IOException {
		File img = null;
		try {
			img = File.createTempFile("omtest", ".jpg");
			final Integer width = 150;
			final Integer height = 100;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.drawString("Hello World!!!", 10, 20);
			ImageIO.write(image, "jpg", img);
			CallResult<FileItemDTO> cr = createVerifiedFile(img, "test.txt", BaseFileItem.Type.Presentation);
			assertEquals("Type should be Image", BaseFileItem.Type.Image, cr.getObj().getType());
			assertEquals("Width should be determined", width, cr.getObj().getWidth());
			assertEquals("Height should be Image", height, cr.getObj().getHeight());
		} finally {
			if (img != null && img.exists()) {
				img.delete();
			}
		}
	}

	@Test
	public void testGetRoom() {
		ServiceResult r = login();
		FileExplorerObject fo = getClient(getFileUrl())
				.path("/room/5")
				.query("sid", r.getMessage())
				.get(FileExplorerObject.class);
		assertNotNull(fo);
	}
}
