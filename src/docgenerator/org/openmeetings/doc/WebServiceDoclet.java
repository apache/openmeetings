package org.openmeetings.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.EscapeTool;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

public class WebServiceDoclet {

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

				Velocity.mergeTemplate(basePath + File.separatorChar
						+ templateName, "UTF-8", vContext, strWriter);
				
				strWriter.flush();
				strWriter.close();

			}
			
			
			VelocityContext vIndexContext = new VelocityContext();
			vIndexContext.put("classes", classesParsed);
			
			FileWriter newFileIndexWriter = new FileWriter(basePath
					+ File.separatorChar + "WebserviceIndex.html");

			Velocity.mergeTemplate(basePath + File.separatorChar
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
