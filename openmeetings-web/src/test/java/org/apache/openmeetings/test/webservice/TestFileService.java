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
package org.apache.openmeetings.test.webservice;

import static org.apache.openmeetings.db.util.ApplicationHelper.getWicketTester;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.file.FileExplorerItemDTO;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Test;

public class TestFileService extends AbstractWebServiceTest {
	public final static String FILE_SERVICE_URL = BASE_SERVICES_URL + "/file";
	protected WicketTester tester;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		tester = getWicketTester();
		assertNotNull("Web session should not be null", WebSession.get());
	}

	@Test
	public void addFileTest() throws IOException {
		ServiceResult r = login();

		File img = null;
		try {
			img = File.createTempFile("omtest", ".jpg");
			final Integer width = 150;
			final Integer height = 100;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.drawString("Hello World!!!", 10, 20);
			ImageIO.write(image, "jpg", img);
			try (InputStream is = new FileInputStream(img)) {
				FileExplorerItemDTO file = new FileExplorerItemDTO();
				file.setName("test.txt");
				file.setHash(UUID.randomUUID().toString());
				file.setType(FileItem.Type.Presentation);
				List<Attachment> atts = new ArrayList<>();
				atts.add(new Attachment("file", MediaType.APPLICATION_JSON, file));
				atts.add(new Attachment("stream", MediaType.APPLICATION_OCTET_STREAM, is));
				FileExplorerItemDTO f1 = getClient(FILE_SERVICE_URL)
						.path("/")
						.query("sid", r.getMessage())
						.type(MediaType.MULTIPART_FORM_DATA_TYPE).postCollection(atts, Attachment.class, FileExplorerItemDTO.class);
				assertNotNull("Valid FileItem should be returned", f1);
				assertNotNull("Valid FileItem should be returned", f1.getId());
				assertEquals("Type should be Image", FileItem.Type.Image, f1.getType());
				assertEquals("Width should be determined", width, f1.getWidth());
				assertEquals("Height should be Image", height, f1.getHeight());
			}
		} finally {
			if (img != null && img.exists()) {
				img.delete();
			}
		}
	}

	@After
	public void tearDown() {
		if (tester != null) {
			//can be null in case exception on initialization
			tester.destroy();
		}
	}
}
