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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.IOException;

import org.apache.openmeetings.util.process.ConverterProcessResult;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class GenerateThumbs extends BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(GenerateThumbs.class, webAppRootKey);

	public ConverterProcessResult generateThumb(String pre, File f, Integer thumbSize) throws IOException {
		log.debug("generateThumb");
		// Init variables
		String name = f.getName();
		File parent = f.getParentFile();

		String[] argv = new String[] {
			getPathToImageMagick()
			, "-thumbnail"
			, thumbSize + "x" + thumbSize
			, f.getCanonicalPath()
			, new File(parent, pre + name).getCanonicalPath()
			};

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}

	public ConverterProcessResult decodePDF(String inputfile, String outputfile) {
		log.debug("decodePDF");
		String[] argv = new String[] { getPathToImageMagick(), inputfile, outputfile };

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}

	public ConverterProcessResult generateBatchThumb(File in, File outDir, Integer thumbSize, String pre) throws IOException {
		log.debug("generateBatchThumbByWidth");
		String[] argv = new String[] {
			getPathToImageMagick()
			, "-thumbnail" // FIXME
			, "" + thumbSize
			, in.getCanonicalPath()
			, new File(outDir, "_" + pre + "_page-%04d.jpg").getCanonicalPath()
			};

		return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
	}
}
