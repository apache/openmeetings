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
package org.apache.openmeetings.core.documents;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class LibraryChartLoader {
	private static final Logger log = Red5LoggerFactory.getLogger(LibraryChartLoader.class, getWebAppRootKey());
	private static final String CHART_EXT = ".xchart";

	private LibraryChartLoader() {}

	@SuppressWarnings("rawtypes")
	public static List loadChart(File dir, String fileName) {
		try {
			File file = new File(dir, fileName + CHART_EXT);

			log.error("filepathComplete: " + file);

			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);

			try (InputStream is = new FileInputStream(file);
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8)))
			{
				return (List) xStream.fromXML(reader);
			}
		} catch (Exception err) {
			log.error("Unexpected error while loading chart", err);
		}
		return new ArrayList<>();
	}
}
