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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
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

	private static class FlvDimension {
		public FlvDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}
		public int width = 0;
		public int height = 0;
	}

	public List<ConverterProcessResult> convertToMP4(FileExplorerItem f, String ext) {
		List<ConverterProcessResult> logs = new ArrayList<>();
		try {
			File mp4 = f.getFile(EXTENSION_MP4);
			f.setType(Type.Video);
			String input = f.getFile(ext).getCanonicalPath();
			boolean sameExt = EXTENSION_MP4.equals(ext);
			Path tmp = null;
			if (sameExt) {
				//we should do in-place conversion
				tmp = Files.createTempFile("video", "mp4");
				input = Files.move(mp4.toPath(), tmp, StandardCopyOption.ATOMIC_MOVE).toFile().getCanonicalPath();
			}
			String[] args = new String[] { getPathToFFMPEG(), "-y"
					, "-i", input //
					, "-c:v", "h264" //
					, "-c:a", "libfaac" //
					, "-c:a", "libfdk_aac" //
					, "-pix_fmt", "yuv420p" //
					, mp4.getCanonicalPath() };
			ConverterProcessResult res = ProcessHelper.executeScript("uploadVideo ID :: " + f.getId(), args);
			logs.add(res);
			if (sameExt && tmp != null) {
				if (res.isOk()) {
					Files.delete(tmp);
				} else {
					//conversion fails, need to move temp file back
					Files.move(tmp, mp4.toPath(), StandardCopyOption.ATOMIC_MOVE);
				}
			}
			//Parse the width height from the FFMPEG output
			FlvDimension dim = getFlvDimension(res.getError());
			f.setFlvWidth(dim.width);
			f.setFlvHeight(dim.height);
			File jpeg = f.getFile(EXTENSION_JPG);

			args = new String[] { getPathToFFMPEG(), "-y", "-i",
					mp4.getCanonicalPath(), "-codec:v", "mjpeg", "-vframes", "1", "-an",
					"-f", "rawvideo", "-s", dim.width + "x" + dim.height,
					jpeg.getCanonicalPath() };

			logs.add(ProcessHelper.executeScript("previewUpload ID :: " + f.getId(), args));

			fileDao.update(f);
		} catch (Exception err) {
			log.error("[convertToFLV]", err);
			logs.add(new ConverterProcessResult("convertToMP4", err.getMessage(), err));
		}

		return logs;
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
