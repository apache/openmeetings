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
package org.openmeetings.app.documents;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateSWF {

	public static final Logger log = Red5LoggerFactory
			.getLogger(GenerateSWF.class);

	@Autowired
	private Configurationmanagement cfgManagement;

	public final static boolean isPosix = System.getProperty("os.name")
			.toUpperCase().indexOf("WINDOWS") == -1;

	public final static String execExt = isPosix ? "" : ".exe";

	private String getPathToSwfTools() {
		String pathToSWFTools = cfgManagement.getConfValue("swftools_path", String.class, "");
		// If SWFTools Path is not blank a File.separator at the end of the path
		// is needed
		if (!pathToSWFTools.equals("")
				&& !pathToSWFTools.endsWith(File.separator)) {
			pathToSWFTools = pathToSWFTools + File.separator;
		}
		return pathToSWFTools;
	}

	public HashMap<String, String> generateSwf(String current_dir,
			String originalFolder, String destinationFolder, String fileNamePure) {
		
		// Create the Content of the Converter Script (.bat or .sh File)
		String[] argv = new String[] {
				getPathToSwfTools() + "pdf2swf" + execExt, "-s",
				"insertstop", // insert Stop command into every frame
				"-s","poly2bitmap", //http://www.swftools.org/gfx_tutorial.html#Rendering_pages_to_SWF_files
				"-i", // change draw order to reduce pdf complexity
				originalFolder + fileNamePure + ".pdf",
				destinationFolder + fileNamePure + ".swf" };

		return ProcessHelper.executeScript("generateSwf", argv);
	}

	/**
	 * Generates an SWF from the list of files.
	 */
	public HashMap<String, String> generateSwfByImages(List<String> images,
			String outputfile, int fps) {
		List<String> argvList = Arrays.asList(new String[] {
				getPathToSwfTools() + "png2swf" + execExt, "-s", 
				"insertstop", // Insert Stop command into every frame
				"-j", "100", // JPEG Quality to 100
				"-o", outputfile, "-r", Integer.toString(fps), "-z" });

		argvList.addAll(images);
		return ProcessHelper.executeScript("generateSwfByImages",
				argvList.toArray(new String[0]));
	}

	/**
	 * Combines a bunch of SWFs into one SWF by concatenate.
	 */
	public HashMap<String, String> generateSWFByCombine(List<String> swfs,
			String outputswf, int fps) {
		List<String> argvList = Arrays.asList(new String[] {
				getPathToSwfTools() + "swfcombine" + execExt, "-s",
				"insertstop", // Insert Stop command into every frame
				"-o", outputswf, "-r", Integer.toString(fps), "-z", "-a" });

		argvList.addAll(swfs);
		return ProcessHelper.executeScript("generateSwfByImages",
				argvList.toArray(new String[0]));
	}

	public HashMap<String, String> generateSWFByFFMpeg(String inputWildCard,
			String outputswf, int fps, int width, int height) {
		// FIXME: ffmpeg should be on the system path
		String[] argv = new String[] { "ffmpeg" + execExt, "-r",
				Integer.toString(fps), "-i", inputWildCard, "-s",
				width + "x" + height, "-b", "750k", "-ar", "44100", "-y",
				outputswf };

		return ProcessHelper.executeScript("generateSWFByFFMpeg", argv);
	}

}
