package org.openlaszlo.schema;

import com.thaiopensource.relaxng.translate.Driver;

import org.apache.xml.resolver.helpers.BootstrapResolver;

public class GenerateXsd {
	
	public static void main(String... args) {
		new GenerateXsd();
	}
	
	public GenerateXsd() {
		
		//Driver driver = new Driver();
		
		String[] args = { "test/lzx.rng", "test/lzx.xsd" };
		
		Driver.main(args);
		
	}

}
