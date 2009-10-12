package org.openmeetings.batik;

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.openmeetings.servlet.outputhandler.ExportToImage;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestSVGTextExporter extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestSVGTextExporter.class);
	
	public void testGetDiagramList() {
		try {
			
			
			ExportToImage exportToImageTest = new ExportToImage();
			
	        // Get a DOMImplementation.
	        DOMImplementation domImpl =
	            GenericDOMImplementation.getDOMImplementation();

	        // Create an instance of org.w3c.dom.Document.
	        //String svgNS = "http://www.w3.org/2000/svg";
	        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

	        Document document = domImpl.createDocument(svgNS, "svg", null);
	        
	        // Get the root element (the 'svg' element).
	        Element svgRoot = document.getDocumentElement();

	        
	        // Set the width and height attributes on the root 'svg' element.
	        svgRoot.setAttributeNS(null, "width", "2400");
	        svgRoot.setAttributeNS(null, "height", "1600");
	        

	        // Create an instance of the SVG Generator.
	        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

	        SVGGraphics2D svgGenerator8 = new SVGGraphics2D(svgGenerator);
	        //SVGGraphics2D svgGenerator2 = new SVGGraphics2D(document);
	        
//	        //NOTE: Font.ITALIC+Font.BOLD = Font AND Bold !
//	        exportToImageTest.paintTextByWidthHeight(svgGenerator8, 500, 300, 100, 200, "Process", Font.BOLD+Font.ITALIC, 28,
//	        		new Color(255,0,0));
//	        
//	        SVGGraphics2D svgGenerator11 = new SVGGraphics2D(svgGenerator);
//	        
//	        exportToImageTest.paintTextByWidthHeight(svgGenerator11, 100, 300, 100, 200, "Process", Font.BOLD+Font.ITALIC, 111,
//	        		new Color(255,0,0));
	        
	        SVGGraphics2D svgGenerator9 = new SVGGraphics2D(svgGenerator);
	      //NOTE: Font.ITALIC+Font.BOLD = Font AND Bold !
//	        exportToImageTest._paintTextByWidthHeight(svgGenerator9, 500, 300, 100, 200, "Hans", Font.BOLD, 28,
//	        		new Color(255,0,0));
	    
	    
	        // Finally, stream out SVG to the standard output using
	        // UTF-8 encoding.
	        boolean useCSS = true; // we want to use CSS style attributes
	        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
	        
	        String requestedFile = "diagram_xyz_"+new Date().getTime()+".svg";
	        
	        //OutputStream out = httpServletResponse.getOutputStream();
			//httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
			//httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + requestedFile + "\"");
	        Writer out = new OutputStreamWriter(System.out, "UTF-8");

	        //StringWriter out = new StringWriter();
	        
	        svgGenerator.stream(out, useCSS);
	        String xmlDef = out.toString();
	        
	        
	        //log.debug("String: "+xmlDef);
	        
	        
			
		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

}
