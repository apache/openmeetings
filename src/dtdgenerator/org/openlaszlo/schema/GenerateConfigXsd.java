package org.openlaszlo.schema;

import java.io.File;

import com.thaiopensource.relaxng.translate.Driver;

public class GenerateConfigXsd {
	
	public static void main(String... args) {
		new GenerateConfigXsd();
	}
	
	public GenerateConfigXsd() {
		try {
			
			String[] args = { 
						"WebContent"+File.separatorChar+"openmeetings"+File.separatorChar+"config.xml",
						"WebContent"+File.separatorChar+"openmeetings"+File.separatorChar+"openmeetings-config.xsd"
			};
			
			Driver.main(args);
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
