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
package org.apache.openmeetings.test.asf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class AsfCopyRightReplace {
	private static final Logger log = Red5LoggerFactory.getLogger(AsfCopyRightReplace.class, webAppRootKey);

	String asf_copyright = "/*\n"
			+ " * Licensed to the Apache Software Foundation (ASF) under one\n"
			+ " * or more contributor license agreements.  See the NOTICE file\n"
			+ " * distributed with this work for additional information\n"
			+ " * regarding copyright ownership.  The ASF licenses this file\n"
			+ " * to you under the Apache License, Version 2.0 (the\n"
			+ " * \"License\") +  you may not use this file except in compliance\n"
			+ " * with the License.  You may obtain a copy of the License at\n"
			+ " *\n"
			+ " *   http://www.apache.org/licenses/LICENSE-2.0\n"
			+ " *\n"
			+ " * Unless required by applicable law or agreed to in writing,\n"
			+ " * software distributed under the License is distributed on an\n"
			+ " * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
			+ " * KIND, either express or implied.  See the License for the\n"
			+ " * specific language governing permissions and limitations\n"
			+ " * under the License.\n" + " */\n";

	String asf_xml_copyright = "<!--\n"
			+ "  Licensed to the Apache Software Foundation (ASF) under one\n"
			+ "  or more contributor license agreements.  See the NOTICE file\n"
			+ "  distributed with this work for additional information\n"
			+ "  regarding copyright ownership.  The ASF licenses this file\n"
			+ "  to you under the Apache License, Version 2.0 (the\n"
			+ "  \"License\"); you may not use this file except in compliance\n"
			+ "  with the License.  You may obtain a copy of the License at\n"
			+ "  \n" + "      http://www.apache.org/licenses/LICENSE-2.0\n"
			+ "    	  \n"
			+ "  Unless required by applicable law or agreed to in writing,\n"
			+ "  software distributed under the License is distributed on an\n"
			+ "  \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
			+ "  KIND, either express or implied.  See the License for the\n"
			+ "  specific language governing permissions and limitations\n"
			+ "  under the License.\n" + "  \n" + "-->\n";
	
	String asf_vm_copyright = "#\n"
			+ "# Licensed to the Apache Software Foundation (ASF) under one\n"
			+ "# or more contributor license agreements.  See the NOTICE file\n"
			+ "# distributed with this work for additional information\n"
			+ "# regarding copyright ownership.  The ASF licenses this file\n"
			+ "# to you under the Apache License, Version 2.0 (the\n"
			+ "# \"License\") +  you may not use this file except in compliance\n"
			+ "# with the License.  You may obtain a copy of the License at\n"
			+ "#\n"
			+ "#   http://www.apache.org/licenses/LICENSE-2.0\n"
			+ "#\n"
			+ "# Unless required by applicable law or agreed to in writing,\n"
			+ "# software distributed under the License is distributed on an\n"
			+ "# \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
			+ "# KIND, either express or implied.  See the License for the\n"
			+ "# specific language governing permissions and limitations\n"
			+ "# under the License.\n" + "#/\n";

	public static void main(String... args) {
		new AsfCopyRightReplace();
	}

	public AsfCopyRightReplace() {
		scanFolder(new File("./src/main"));
	}

	private void scanFolder(File folder) {

		for (File javaFile : folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File b, String name) {
				return name.endsWith(".java") || name.endsWith(".vm");
			}
		})) {
			scanAndWriteJavaFile(javaFile);
		}

		for (File javaFile : folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File b, String name) {
				return name.endsWith(".xml") || name.endsWith(".lzx");
			}
		})) {
			scanAndWriteXMLFile(javaFile);
		}

		for (File folderFile : folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File b, String name) {
				File f = new File(b, name);
				return f.isDirectory();
			}
		})) {
			scanFolder(folderFile);
		}
	}

	private void scanAndWriteXMLFile(File javaFile) {
		StringWriter strWriter = new StringWriter();
		try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(javaFile), UTF_8))) {
			log.debug("Processing " + javaFile.getCanonicalPath());

			String line;
			String firstline = "";
			int i = 0;

			while ((line = is.readLine()) != null) {
				if (i == 0) {
					firstline = line;
					
					if (firstline.startsWith("<canvas")) {
						strWriter.append(asf_xml_copyright);
					} else if (firstline.startsWith("<library")) {
						strWriter.append(asf_xml_copyright);
					}
				} else if (i == 1) {
					if (firstline.startsWith("<?xml ")
							&& !line.startsWith("<!--")) {
						strWriter.append(asf_xml_copyright);
					} else if (firstline.startsWith("<?xml ")
							&& !line.startsWith("<library")) {
						strWriter.append(asf_xml_copyright);
					} else if (firstline.startsWith("<?xml ")
							&& !line.startsWith("<canvas")) {
						strWriter.append(asf_xml_copyright);
					}
				}
				strWriter.append(line + "\n");
				i++;
			}

		} catch (Exception err) {
			log.error("Error while scanAndWriteXMLFile", err);
		}
		try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(javaFile), UTF_8)) {
			out.write(strWriter.toString());
			out.flush();
		} catch (Exception err) {
			log.error("Error while scanAndWriteXMLFile", err);
		}
	}

	private void scanAndWriteJavaFile(File javaFile) {
		StringWriter strWriter = new StringWriter();
		try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(javaFile), UTF_8))) {
			log.debug("Processing " + javaFile.getCanonicalPath());

			String line;
			int i = 0;

			while ((line = is.readLine()) != null) {
				if (i == 0) {
					if (line.startsWith("package ")) {
						strWriter.append(asf_copyright);
					} else if (line.startsWith("## OpenMeetings") 
							&& javaFile.getName().endsWith(".vm")) {
						strWriter.append(asf_vm_copyright);
					}
				}
				strWriter.append(line + "\n");
				i++;
			}
		} catch (Exception err) {
			log.error("Error while scanAndWriteJavaFile", err);
		}
		try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(javaFile), UTF_8)) {
			out.write(strWriter.toString());
			out.flush();
		} catch (Exception err) {
			log.error("Error while scanAndWriteJavaFile", err);
		}
	}
}
