package org.openlaszlo.generator;


public class RewriteLzxToXSD extends GenerateSchema {
	
	public static void main(String... args) {
		
		RewriteLzxToXSD generateBaseXSD = new RewriteLzxToXSD();
		
		generateBaseXSD.getXsdUtil().setXsdProjectPrefix("project");
		generateBaseXSD.scanFolder("WebContent/src/");
		
		generateBaseXSD.rewriteFolder("WebContent/src/", "WebContent/client/");
		
		
		//generateBaseXSD.getXsdUtil().setXsdProjectPrefix("project");
		//generateBaseXSD.scanFolder("WebContent/src/");
		
		
		
	}
	
}
