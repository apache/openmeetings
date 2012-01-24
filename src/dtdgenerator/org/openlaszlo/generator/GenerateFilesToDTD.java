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
package org.openlaszlo.generator;

import java.io.File;
import java.io.FilenameFilter;

import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class GenerateFilesToDTD {

	FilenameFilter folderFilter = new FilenameFilter() {
		public boolean accept(File b, String name) {
			// We do not scan folder that start with a "."
			if (name.startsWith(".")) {
				return false;
			}
			String absPath = b.getAbsolutePath() + File.separatorChar + name;
			File f = new File(absPath);
			return f.isDirectory();
		}
	};

	FilenameFilter lzxFilter = new FilenameFilter() {
		public boolean accept(File b, String name) {
			return name.endsWith(".lzx");
		}
	};

	public static void main(String... args) {
		new GenerateFilesToDTD("WebContent/src/");
	}

	public GenerateFilesToDTD(String basePath) {
		this.scanFolder(basePath);

	}

	public void scanFolder(String filePath) {
		try {

			File baseFolder = new File(filePath);

			if (!baseFolder.exists()) {
				throw new Exception("Base path does not exist " + filePath);
			}

			if (baseFolder.isDirectory()) {
				for (String folder : baseFolder.list(folderFilter)) {
					scanFolder(filePath + File.separatorChar + folder);
				}
				for (String file : baseFolder.list(lzxFilter)) {
					scanFile(filePath + File.separatorChar + file);
				}
			}

		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		}
	}

	EntityResolver resolver = new EntityResolver() {
	    public InputSource resolveEntity(String publicId, String systemId) {
	    	System.out.println("resolveEntity "+publicId+ " " +systemId);
	    	
	    	return new InputSource("/project.dtd");
		}
	};
	
	public void scanFile(String filePath) {
		try {
			
			 SAXReader saxReader = new SAXReader();
			 //saxReader.setEntityResolver( resolver );
			 Document document = saxReader.read(filePath);
			 
			 DocumentType dt = document.getDocType();
			 
			 if (dt != null) {
				 System.out.println(""+document.getDocType());
			 } else {
				 System.out.println("DTD missing "+filePath);
			 }
			 
			 
            
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println(err.getMessage());
		}
	}
}
