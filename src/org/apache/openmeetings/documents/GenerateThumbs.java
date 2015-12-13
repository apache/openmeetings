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
package org.apache.openmeetings.documents;

import java.io.File;
import java.io.IOException;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.flvrecord.converter.BaseConverter;
import org.apache.openmeetings.documents.beans.ConverterProcessResult;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class GenerateThumbs extends BaseConverter {

	private static final Logger log = Red5LoggerFactory
			.getLogger(GenerateThumbs.class, OpenmeetingsVariables.webAppRootKey);

	public ConverterProcessResult generateThumb(String pre, File f, Integer thumbSize) throws IOException {
		// Init variables
		String name = f.getName();
		File parent = f.getParentFile();

		String[] argv = new String[] {
			getPathToImageMagick()
			, "-thumbnail"
			, Integer.toString(thumbSize) + "x" + Integer.toString(thumbSize)
			, f.getCanonicalPath()
			, new File(parent, pre + name).getCanonicalPath()
			};

		log.debug("START generateThumb ################# ");
		for (int i = 0; i < argv.length; i++) {
			log.debug(" i " + i + " argv-i " + argv[i]);
		}
		log.debug("END generateThumb ################# ");

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}
	}

	public ConverterProcessResult decodePDF(String inputfile, String outputfile) {

		String[] argv = new String[] { getPathToImageMagick(),
				inputfile, outputfile };

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}

	}

	public ConverterProcessResult generateBatchThumb(File inputfile, File outputpath, Integer thumbSize, String pre) throws IOException {

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			String[] argv = new String[] {
				getPathToImageMagick()
				, "-thumbnail" // FIXME
				, Integer.toString(thumbSize)
				, inputfile.getCanonicalPath()
				, new File(outputpath, "_" + pre + "_page-%04d.jpg").getCanonicalPath()
				};

			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {

			String[] argv = new String[] {
				getPathToImageMagick()
				, "-thumbnail" // FIXME
				, Integer.toString(thumbSize)
				, inputfile.getCanonicalPath()
				, new File(outputpath, "_" + pre + "_page-%04d.jpg").getCanonicalPath()
				};

			// return GenerateSWF.executeScript("generateBatchThumbByWidth",
			// argv);
			return this.processImageWindows(argv);
		}
	}

	public ConverterProcessResult generateImageBatchByWidth(
			String current_dir, String inputfile, String outputpath,
			Integer thumbWidth, String pre) {

		String[] argv = new String[] { getPathToImageMagick(),
				"-resize", Integer.toString(thumbWidth), inputfile,
				outputpath + "_" + pre + "_page.png" };

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return ProcessHelper.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}
	}

	public ConverterProcessResult processImageWindows(String[] args) {
		return ProcessHelper.executeScriptWindows("processImageWindows", args);
	}
}
