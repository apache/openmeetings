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
package org.apache.openmeetings.test.xsd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.thaiopensource.relaxng.translate.Driver;

public class GenerateWebsiteXsd {

	private static String WEBSITE_ROOT_DIR = "xdocs";

	public static void main(String... args) {
		new GenerateWebsiteXsd();
	}

	public GenerateWebsiteXsd() {
		try {

			File curentDir = new File(WEBSITE_ROOT_DIR);

			System.err.println(curentDir.getAbsolutePath());

			// Get all XML Files of this Folder
			FilenameFilter ff = new FilenameFilter() {
				public boolean accept(File b, String name) {
					File f = new File(b, name);
					return f.getName().contains(".xml");
				}
			};

			String[] allfiles = curentDir.list(ff);

			System.err.println("allfiles LENGTH " + allfiles.length);

			List<String> allfilesPlusXSD = new ArrayList<String>();

			for (String fileName : allfiles) {
				allfilesPlusXSD.add(WEBSITE_ROOT_DIR + File.separatorChar
						+ fileName);
			}

			allfilesPlusXSD.add(WEBSITE_ROOT_DIR + File.separatorChar
					+ "website.xsd");

			for (String fileName : allfilesPlusXSD) {
				System.err.println(fileName);
			}

			Driver.main(allfilesPlusXSD.toArray(new String[0]));

		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
