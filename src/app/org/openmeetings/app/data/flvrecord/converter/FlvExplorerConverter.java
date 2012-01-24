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
package org.openmeetings.app.data.flvrecord.converter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.data.file.dao.FileExplorerItemDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingLogDaoImpl;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.persistence.beans.files.FileExplorerItem;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvExplorerConverter extends BaseConverter {

	private static final Logger log = Red5LoggerFactory.getLogger(
			FlvExplorerConverter.class, ScopeApplicationAdapter.webAppRootKey);

	// Spring loaded Beans
	@Autowired
	private FileExplorerItemDaoImpl fileExplorerItemDaoImpl;
	@Autowired
	private FlvRecordingLogDaoImpl flvRecordingLogDaoImpl;

	public void startConversion(Long fileExplorerItemId, String moviePath) {
		try {

			FileExplorerItem fileExplorerItem = this.fileExplorerItemDaoImpl
					.getFileExplorerItemsById(fileExplorerItemId);

			log.debug("fileExplorerItem "
					+ fileExplorerItem.getFileExplorerItemId());

			// Strip Audio out of all Audio-FLVs
			this.convertToFLV(fileExplorerItem, moviePath);

			// Add empty pieces at the beginning and end of the wav

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}

	}

	private void convertToFLV(FileExplorerItem fileExplorerItem,
			String moviePath) {
		try {

			List<HashMap<String, String>> returnLog = new LinkedList<HashMap<String, String>>();

			String streamFolderName = getStreamFolderName("hibernate");

			String outputFullFlv = streamFolderName + "UPLOADFLV_"
					+ fileExplorerItem.getFileExplorerItemId() + ".flv";

			int flvWidth = 300;
			int flvHeight = 240;

			fileExplorerItem.setFlvWidth(flvWidth);
			fileExplorerItem.setFlvHeight(flvHeight);
			fileExplorerItem.setIsVideo(true);

			String[] argv_fullFLV = null;

			argv_fullFLV = new String[] { getPathToFFMPEG(), "-i", moviePath,
					"-ar", "22050", "-acodec", "libmp3lame", "-ab", "32k",
					"-s", flvWidth + "x" + flvHeight, "-vcodec", "flv",
					outputFullFlv };

			log.debug("START generateFullFLV ################# ");
			String tString = "";
			for (int i = 0; i < argv_fullFLV.length; i++) {
				tString += argv_fullFLV[i] + " ";
				// log.debug(" i " + i + " argv-i " + argv_fullFLV[i]);
			}
			log.debug(tString);
			log.debug("END generateFullFLV ################# ");

			returnLog.add(GenerateSWF.executeScript("uploadFLV ID :: "
					+ fileExplorerItem.getFileExplorerItemId(), argv_fullFLV));

			String hashFileFullNameJPEG = "UPLOADFLV_"
					+ fileExplorerItem.getFileExplorerItemId() + ".jpg";
			String outPutJpeg = streamFolderName + hashFileFullNameJPEG;

			fileExplorerItem.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { getPathToFFMPEG(), "-i",
					outputFullFlv, "-vcodec", "mjpeg", "-vframes", "1", "-an",
					"-f", "rawvideo", "-s", flvWidth + "x" + flvHeight,
					outPutJpeg };

			log.debug("START previewFullFLV ################# ");
			log.debug(argv_previewFLV.toString());
			String kString = "";
			for (int i = 0; i < argv_previewFLV.length; i++) {
				kString += argv_previewFLV[i] + " ";
				// log.debug(" i " + i + " argv-i " + argv_previewFLV[i]);
			}
			log.debug(kString);
			log.debug("END previewFullFLV ################# ");

			returnLog
					.add(GenerateSWF.executeScript("previewUpload ID :: "
							+ fileExplorerItem.getFileExplorerItemId(),
							argv_previewFLV));

			this.fileExplorerItemDaoImpl.updateFileOrFolder(fileExplorerItem);

			for (HashMap<String, String> returnMap : returnLog) {
				this.flvRecordingLogDaoImpl.addFLVRecordingLog(
						"generateFFMPEG", null, returnMap);
			}

		} catch (Exception err) {
			log.error("[convertToFLV]", err);
		}

	}

}
