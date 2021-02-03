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
package org.apache.openmeetings.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author sebastianwagner
 *
 */
public class XmlExport {
	public static final String LICENSE = ""
			+ "\n"
			+ "  Licensed to the Apache Software Foundation (ASF) under one\n"
			+ "  or more contributor license agreements.  See the NOTICE file\n"
			+ "  distributed with this work for additional information\n"
			+ "  regarding copyright ownership.  The ASF licenses this file\n"
			+ "  to you under the Apache License, Version 2.0 (the\n"
			+ "  \"License\"); you may not use this file except in compliance\n"
			+ "  with the License.  You may obtain a copy of the License at\n"
			+ "\n"
			+ "      http://www.apache.org/licenses/LICENSE-2.0\n"
			+ "\n"
			+ "  Unless required by applicable law or agreed to in writing,\n"
			+ "  software distributed under the License is distributed on an\n"
			+ "  \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
			+ "  KIND, either express or implied.  See the License for the\n"
			+ "  specific language governing permissions and limitations\n"
			+ "  under the License.\n"
			+ "\n";
	public static final String FILE_COMMENT = ""
			+ LICENSE
			+ "\n"
			+ "###############################################\n"
			+ "This File is auto-generated by the LanguageEditor\n"
			+ "to add new Languages or modify/customize it use the LanguageEditor\n"
			+ "see https://openmeetings.apache.org/LanguageEditor.html for Details\n"
			+ "###############################################\n";

	private XmlExport() {}

	public static Document createDocument() {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding(UTF_8.name());
		document.addComment(XmlExport.FILE_COMMENT);
		return document;
	}

	public static Element createRoot(Document document) {
		document.addDocType("properties", null, "http://java.sun.com/dtd/properties.dtd");
		return document.addElement("properties");
	}

	public static Element createRoot(Document document, String root) {
		return document.addElement(root);
	}

	public static void writeOutput(Writer out, Document doc) throws IOException {
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setIndentSize(1);
		outformat.setIndent("\t");
		outformat.setEncoding(UTF_8.name());
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
		out.flush();
		out.close();
	}

	public static void writeOutput(File f, Document doc) throws Exception {
		writeOutput(new FileOutputStream(f), doc);
	}

	public static void writeOutput(OutputStream out, Document doc) throws IOException {
		writeOutput(new OutputStreamWriter(out, UTF_8), doc);
	}
}
