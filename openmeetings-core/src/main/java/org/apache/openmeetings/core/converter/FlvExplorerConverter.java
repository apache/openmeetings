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

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.JPG_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.WB_VIDEO_FILE_PREFIX;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingLogDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FlvExplorerConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvExplorerConverter.class, webAppRootKey);
	private static final Pattern p = Pattern.compile("\\d{2,4}(x)\\d{2,4}");

	// Spring loaded Beans
	@Autowired
	private FileExplorerItemDao fileDao;
	@Autowired
	private RecordingLogDao recordingLogDao;
	
	private static class FlvDimension {
		public FlvDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}
		public int width = 0;
		public int height = 0;
	}

	public List<ConverterProcessResult> startConversion(Long fileId, String moviePath) {
		List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
		try {
			FileExplorerItem fileExplorerItem = fileDao.get(fileId);
			if (fileExplorerItem == null) {
				returnLog.add(new ConverterProcessResult("startConversion", "Unable to get FileExplorerItem by ID: " + fileId, null));
			} else {
				log.debug("fileExplorerItem " + fileExplorerItem.getId());
				//  Convert to FLV
				return convertToFLV(fileExplorerItem, moviePath);
			}
		} catch (Exception err) {
			log.error("[startConversion]", err);
			returnLog.add(new ConverterProcessResult("startConversion", err.getMessage(), err));
		}
		return returnLog;
	}

	private List<ConverterProcessResult> convertToFLV(FileExplorerItem fileExplorerItem, String moviePath) {
		List<ConverterProcessResult> returnLog = new ArrayList<ConverterProcessResult>();
		try {
			String name = WB_VIDEO_FILE_PREFIX + fileExplorerItem.getId();
			File outputFullFlv = new File(getStreamsHibernateDir(), name + MP4_EXTENSION);

			fileExplorerItem.setType(Type.Video);

			String[] argv_fullFLV = new String[] { getPathToFFMPEG(), "-y", "-i", moviePath,
					"-c:a", "aac", "-c:v", "h264", "-pix_fmt", "yuv420p", outputFullFlv.getCanonicalPath() };

			ConverterProcessResult returnMapConvertFLV = ProcessHelper.executeScript("uploadFLV ID :: "
					+ fileExplorerItem.getId(), argv_fullFLV);
			
			//Parse the width height from the FFMPEG output
			FlvDimension flvDimension = getFlvDimension(returnMapConvertFLV.getError());
			int flvWidth = flvDimension.width;
			int flvHeight = flvDimension.height;
			
			
			fileExplorerItem.setFlvWidth(flvWidth);
			fileExplorerItem.setFlvHeight(flvHeight);

			returnLog.add(returnMapConvertFLV);

			String hashFileFullNameJPEG = WB_VIDEO_FILE_PREFIX + fileExplorerItem.getId() + JPG_EXTENSION;
			File outPutJpeg = new File(getStreamsHibernateDir(), name + JPG_EXTENSION);

			fileExplorerItem.setPreviewImage(hashFileFullNameJPEG);

			String[] argv_previewFLV = new String[] { getPathToFFMPEG(), "-y", "-i",
					outputFullFlv.getCanonicalPath(), "-codec:v", "mjpeg", "-vframes", "1", "-an",
					"-f", "rawvideo", "-s", flvWidth + "x" + flvHeight,
					outPutJpeg.getCanonicalPath() };

			returnLog.add(ProcessHelper.executeScript("previewUpload ID :: " + fileExplorerItem.getId(), argv_previewFLV));

			fileDao.update(fileExplorerItem);

			for (ConverterProcessResult returnMap : returnLog) {
				recordingLogDao.add("generateFFMPEG", null, returnMap);
			}
		} catch (Exception err) {
			log.error("[convertToFLV]", err);
			returnLog.add(new ConverterProcessResult("convertToFLV", err.getMessage(), err));
		}

		return returnLog;
	}
	
	private static FlvDimension getFlvDimension(String txt) throws Exception {
		Matcher matcher = p.matcher(txt);
		
		while (matcher.find()) {
			String foundResolution = txt.substring(matcher.start(), matcher.end());
			String[] resultions = foundResolution.split("x");
			return new FlvDimension(Integer.valueOf(resultions[0]).intValue(), Integer.valueOf(resultions[1]).intValue());
		}
		
		return new FlvDimension(100, 100); // will return 100x100 for non-video to be able to play
	}
}
