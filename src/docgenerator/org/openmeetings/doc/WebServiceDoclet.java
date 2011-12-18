package org.openmeetings.doc;

import java.io.File;
import java.io.FileWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

public class WebServiceDoclet {

	static final String basePath = "docs";
	static final String templateName = "ApiMethodsTemplate.vm";

	public static boolean start(RootDoc root) {

		try {
			out("Start WebServiceDoclet templateNameNew " + templateName);

			// iterate over all classes.
			ClassDoc[] classes = root.classes();
			for (int i = 0; i < classes.length; i++) {
				// iterate over all methods and print their names.
				MethodDoc[] methods = classes[i].methods();

				Velocity.init();
				VelocityContext vContext = new VelocityContext();
				vContext.put("className", classes[i].name());
				vContext.put("classComment", classes[i].commentText());
				
				String defaultOutputName = classes[i].name();
				for (Tag tag : classes[i].tags()) {
					out("Class Tag "+tag.name() + " || "+tag.text());
					if (tag.name().equals("webservice")) {
						defaultOutputName = tag.text();
					}
				}
				
				vContext.put("webServiceName", defaultOutputName);
				
				vContext.put("methods", classes[i].methods());

				FileWriter strWriter = new FileWriter(basePath
						+ File.separatorChar + defaultOutputName + ".html");

				Velocity.mergeTemplate(basePath + File.separatorChar
						+ templateName, "UTF-8", vContext, strWriter);

				out("Methods");
				out("-------");
				for (int j = 0; j < methods.length; j++) {
					out("Method: name = " + methods[j].name() + " Comment "
							+ methods[j].commentText() + " Return Type "
							+ methods[j].returnType().simpleTypeName());
					int k = 0;
					for (ParamTag parameter : methods[j].paramTags()) {
						out("Param : "
								+ parameter.parameterName()
								+ " Type: "
								+ methods[j].parameters()[k].type()
										.simpleTypeName() + " "
								+ parameter.parameterComment());
						k++;
					}
				}

			}

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
