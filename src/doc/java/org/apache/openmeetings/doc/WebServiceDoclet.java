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
package org.apache.openmeetings.doc;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

public class WebServiceDoclet {
	static final String baseTemplatePath = "xdocs";
	static final String basePath = "docs";
	static final String templateName = "ApiMethodsTemplate.vm";
	static final String templateNameIndex = "ApiClassesTemplate.vm";

	public static boolean start(RootDoc root) {

		try {
			
			Velocity.init();
			out("Start WebServiceDoclet templateNameNew " + templateName);
			
			ArrayList<Map<String,String>> classesParsed = new ArrayList<Map<String,String>>(root.classes().length);
			
			// iterate over all classes.
			ClassDoc[] classes = root.classes();
			for (int i = 0; i < classes.length; i++) {
				// iterate over all methods and print their names.
				
				VelocityContext vContext = new VelocityContext();
				vContext.put("className", classes[i].name());
				vContext.put("classComment", classes[i].commentText());
				
				String defaultOutputName = classes[i].name();
				for (Tag tag : classes[i].tags()) {
					if (tag.name().equals("@webservice")) {
						defaultOutputName = tag.text();
					}
				}
				
				vContext.put("webServiceName", defaultOutputName);
				
				HashMap<String,String> classItem = new HashMap<String,String>();
				classItem.put("name",defaultOutputName);
				classItem.put("comment",classes[i].commentText());
				classesParsed.add(classItem);
				
				vContext.put("methods", classes[i].methods());
				
				FileWriter strWriter = new FileWriter(basePath
						+ File.separatorChar + defaultOutputName + ".html");

				Velocity.mergeTemplate(baseTemplatePath + File.separatorChar
						+ templateName, "UTF-8", vContext, strWriter);
				
				strWriter.flush();
				strWriter.close();

			}
			
			
			VelocityContext vIndexContext = new VelocityContext();
			vIndexContext.put("classes", classesParsed);
			
			FileWriter newFileIndexWriter = new FileWriter(basePath
					+ File.separatorChar + "WebserviceIndex.html");

			Velocity.mergeTemplate(baseTemplatePath + File.separatorChar
					+ templateNameIndex, "UTF-8", vIndexContext, newFileIndexWriter);
			
			newFileIndexWriter.flush();
			newFileIndexWriter.close();
			

		} catch (Exception err) {
			err.printStackTrace();
		}

		// No error processing done, simply return true.
		return true;
	}

	private static void out(String msg) {
		System.out.println(msg);
	}

}
