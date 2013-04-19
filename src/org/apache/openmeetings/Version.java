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
package org.apache.openmeetings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class Version {
	private static final Logger log = Red5LoggerFactory.getLogger(Version.class, OpenmeetingsVariables.webAppRootKey);
	private static final int startedStringLength = 48;
	private static String version = null;
	private static String revision = null;
	private static String buildDate = null;
	
	private static Attributes getAttributes() throws MalformedURLException, IOException {
		String jarUrl = Version.class.getResource(Version.class.getSimpleName() + ".class").toString();
		return new Manifest(new URL(jarUrl.substring(0, jarUrl.indexOf('!')) + "!/META-INF/MANIFEST.MF").openStream()).getMainAttributes();
	}
	
	public static String getVersion() {
		if (version == null) {
			try {
				version = getAttributes().getValue("Product-Version");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return version;
	}
	
	public static String getRevision() {
		if (revision == null) {
			try {
				revision = getAttributes().getValue("Svn-Revision");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return revision;
	}
	
	public static String getBuildDate() {
		if (buildDate == null) {
			try {
				buildDate = getAttributes().getValue("Built-On");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buildDate;
	}

	private static void getLine(StringBuilder sb, String text, char fill) {
		sb.append('#');
		int l = text.length();
		int headLength = (startedStringLength - l) / 2;
		for (int i = 0; i < headLength; ++i) {
			sb.append(fill);
		}
		sb.append(text);
		for (int i = 0; i < (startedStringLength - l - headLength); ++i) {
			sb.append(fill);
		}
		sb.append("#\n");
	}
	
	public static void logOMStarted() {
		StringBuilder sb = new StringBuilder("\n");
		getLine(sb, "", '#');
		getLine(sb, "Openmeetings is up", ' ');
		getLine(sb, getVersion() + " " + getRevision() + " " + getBuildDate(), ' ');
		getLine(sb, "and ready to use", ' ');
		getLine(sb, "", '#');
		log.debug(sb.toString());
	}
}
