package org.openlaszlo.generator;

public class GenerateProjectXSD extends GenerateSchema {
	
	public static void main(String... args) {
		
		GenerateProjectXSD generateBaseXSD = new GenerateProjectXSD();
		
		generateBaseXSD.getXsdUtil().setNameSpace("http://localhost/openlaszlo/project");
		generateBaseXSD.getXsdUtil().setXsdProjectPrefix("project");
		
		generateBaseXSD.scanFolder("WebContent/src/");
		
		generateBaseXSD.getXsdUtil().setImport("http://localhost/openlaszlo/lzx","lzx", "lzx.xsd");
		
		generateBaseXSD.printXsd("project.xsd","");
		
	}
	
}
