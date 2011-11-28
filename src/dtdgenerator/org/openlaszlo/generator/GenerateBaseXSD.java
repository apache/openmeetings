package org.openlaszlo.generator;


public class GenerateBaseXSD extends GenerateSchema {
	
	public static void main(String... args) {
		
		GenerateBaseXSD generateBaseXSD = new GenerateBaseXSD();
		
		generateBaseXSD.getXsdUtil().setNameSpace("http://localhost/openlaszlo/lzx");
		generateBaseXSD.getXsdUtil().setXsdProjectPrefix("lzx");
		
		generateBaseXSD.scanFolder("openlaszlo/lps/");
		generateBaseXSD.scanFolder("test/core/lfc/");
		
		generateBaseXSD.printXsd("lzx.xsd","test/core/static_simpleTypes.txt");
		
	}
	
}
