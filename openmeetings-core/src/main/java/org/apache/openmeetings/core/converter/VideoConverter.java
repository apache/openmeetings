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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.getCssImagesDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class VideoConverter extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(VideoConverter.class, getWebAppRootKey());

	public void convertVideo(FileItem f, StoredFile sf, ProcessResultList logs) {
		try {
			final File mp4 = f.getFile(EXTENSION_MP4);
			f.setType(Type.Video);
			final String ext = sf.getExt();
			String input = f.getFile(ext).getCanonicalPath();
			boolean sameExt = EXTENSION_MP4.equals(ext);
			Path tmp = null;
			if (sameExt) {
				//we should do in-place conversion
				tmp = Files.createTempFile("video", ".mp4");
				input = Files.move(mp4.toPath(), tmp, REPLACE_EXISTING).toFile().getCanonicalPath();
			}
			List<String> args = new ArrayList<>(Arrays.asList(getPathToFFMPEG(), "-y"));
			if (sf.isAudio()) {
				// need to add background image, it should be jpg since black on transparent will be invisible
				args.addAll(Arrays.asList("-loop", "1"//
						, "-framerate", "24"//
						, "-i", new File(getCssImagesDir(), "audio.jpg").getCanonicalPath()));
			}
			args.addAll(Arrays.asList("-i", input //
					, "-c:v", "h264" //
					, "-c:a", "libfaac" //
					, "-c:a", "libfdk_aac" //
					, "-pix_fmt", "yuv420p"));
			if (sf.isAudio()) {
				args.add("-shortest");
			}
			args.add(mp4.getCanonicalPath());
			ProcessResult res = ProcessHelper.executeScript("convert to MP4 :: " + f.getHash(), args.toArray(new String[0]));
			logs.add(res);
			if (sameExt && tmp != null) {
				if (res.isOk()) {
					Files.delete(tmp);
				} else {
					//conversion fails, need to move temp file back
					Files.move(tmp, mp4.toPath(), REPLACE_EXISTING);
				}
			}
			//Parse the width height from the FFMPEG output
			Dimension dim = getDimension(res.getError());
			f.setWidth(dim.getWidth());
			f.setHeight(dim.getHeight());
			convertToPng(f, mp4.getCanonicalPath(), logs);
		} catch (Exception err) {
			log.error("[convertVideo]", err);
			logs.add(new ProcessResult("convertToMP4", err.getMessage(), err));
		}
	}
}
