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
package org.apache.openmeetings.core.converter;

import static org.apache.openmeetings.util.OmFileHelper.DOC_PAGE_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.getUploadProfilesUserDir;
import static org.apache.openmeetings.util.OmFileHelper.profileFileName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_DPI;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DOCUMENT_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ConverterProcessResultList;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ImageConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(ImageConverter.class, webAppRootKey);
	private static final String PAGE_TMPLT = DOC_PAGE_PREFIX + "-%04d." + EXTENSION_PNG;

	@Autowired
	private UserDao userDao;
	@Autowired
	private ConfigurationDao cfgDao;

	public ConverterProcessResultList convertImage(FileItem f, String ext) throws IOException {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		File jpg = f.getFile(EXTENSION_JPG);
		if (!EXTENSION_JPG.equals(ext)) {
			File img = f.getFile(ext);

			log.debug("##### convertImage destinationFile: " + jpg);
			returnMap.addItem("processJPG", convertSingleJpg(img, jpg));
		}
		returnMap.addItem("get JPG dimensions", initSize(f, jpg));
		return returnMap;
	}

	public ConverterProcessResultList convertImageUserProfile(File file, Long userId, boolean skipConvertion) throws Exception {
		ConverterProcessResultList returnMap = new ConverterProcessResultList();

		// User Profile Update
		File[] files = getUploadProfilesUserDir(userId).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(EXTENSION_JPG);
			}
		});
		if (files != null) {
			for (File f : files) {
				FileUtils.deleteQuietly(f);
			}
		}

		File destinationFile = OmFileHelper.getNewFile(getUploadProfilesUserDir(userId), profileFileName, EXTENSION_JPG);
		if (!skipConvertion) {
			returnMap.addItem("processJPG", convertSingleJpg(file, destinationFile));
		} else {
			FileUtils.copyFile(file, destinationFile);
		}

		if (!skipConvertion) {
			// Delete old one
			file.delete();
		}

		String pictureuri = destinationFile.getName();
		User us = userDao.get(userId);
		us.setUpdated(new java.util.Date());
		us.setPictureuri(pictureuri);
		userDao.update(us, userId);

		//FIXME: After uploading a new picture all other clients should refresh
		//scopeApplicationAdapter.updateUserSessionObject(userId, pictureuri);

		return returnMap;
	}

	private String getDpi() {
		return cfgDao.getConfValue(CONFIG_DOCUMENT_DPI, String.class, "150"); //TODO constant
	}

	private String getQuality() {
		return cfgDao.getConfValue(CONFIG_DOCUMENT_QUALITY, String.class, "90"); //TODO constant
	}

	/**
	 * This method determines and set image size
	 *
	 * @param f - file item to set size
	 * @param img - image file used to determine size
	 * @return result of the operation
	 * @throws IOException in case exception is occured
	 */
	public ConverterProcessResult initSize(FileItem f, File img) throws IOException {
		ConverterProcessResult res = ProcessHelper.executeScript("get image dimensions :: " + f.getId()
				, new String[] {getPathToIdentify(), "-format", "%wx%h", img.getCanonicalPath()});
		Dimension dim = getDimension(res.getOut());
		f.setWidth(dim.width);
		f.setHeight(dim.height);
		return res;
	}

	/**
	 * @param in - input file
	 * @param out - output file
	 * @return - conversion result
	 * @throws IOException
	 *
	 */
	private ConverterProcessResult convertSingleJpg(File in, File out) throws IOException {
		String[] argv = new String[] { getPathToConvert(), in.getCanonicalPath(), out.getCanonicalPath() };

		return ProcessHelper.executeScript("convertSingleJpg", argv);
	}

	public ConverterProcessResult resize(File in, File out, Integer width, Integer height) throws IOException {
		String[] argv = new String[] { getPathToConvert()
				, "-resize", (width == null ? "" : width) + (height == null ? "" : "x" + height)
				, in.getCanonicalPath(), out.getCanonicalPath()
				};
		return ProcessHelper.executeScript("resize", argv);
	}

	/**
	 * Converts PDF document to the series of images
	 *
	 * @param pdf - input PDF document
	 * @return - result of conversion
	 * @throws IOException in case IO exception occured
	 */
	public ConverterProcessResultList convertDocument(ConverterProcessResultList list, FileItem f, File pdf) throws IOException {
		log.debug("convertDocument");
		String[] argv = new String[] {
			getPathToConvert()
			, "-density", getDpi()
			, pdf.getCanonicalPath()
			, "-quality", getQuality()
			, new File(pdf.getParentFile(), PAGE_TMPLT).getCanonicalPath()
			};
		ConverterProcessResult res = ProcessHelper.executeScript("convertDocument", argv);
		list.addItem("convert PDF to images", res);
		if (res.isOk()) {
			File[] pages = pdf.getParentFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isFile() && f.getName().startsWith(DOC_PAGE_PREFIX) && f.getName().endsWith(EXTENSION_PNG);
				}
			});
			if (pages == null || pages.length == 0) {
				f.setCount(0);
			} else {
				f.setCount(pages.length);
			}
			list.addItem("get PNG page dimensions", initSize(f, pages[0]));
		}
		return list;
	}
}
