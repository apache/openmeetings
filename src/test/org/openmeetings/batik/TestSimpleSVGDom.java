package org.openmeetings.batik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class TestSimpleSVGDom extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestSVGTextExporter.class);
	
	public void testGetDiagramList() {
		try {
			
			DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
			String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
			Document doc = impl.createDocument(svgNS, "svg", null);

			// Get the root element (the 'svg' element).
			Element svgRoot = doc.getDocumentElement(); 
			
			
			
			// Set the width and height attributes on the root 'svg' element.
			svgRoot.setAttributeNS(svgNS, "width", "400");
			svgRoot.setAttributeNS(svgNS, "height", "450");

			// Create the rectangle.
			Element rectangle = doc.createElementNS(svgNS, "text");
			rectangle.setAttributeNS(svgNS, "x", "10");
			rectangle.setAttributeNS(svgNS, "y", "20");
			rectangle.setAttributeNS(svgNS, "text", "20");
			rectangle.setAttributeNS(svgNS, "width", "100");
			rectangle.setAttributeNS(svgNS, "height", "50");
			rectangle.setAttributeNS(svgNS, "fill", "red");
			//rectangle.setTextContent("textFF");

			// Attach the rectangle to the root 'svg' element.
			svgRoot.appendChild(rectangle);
			
			
			// Finally, stream out SVG to the standard output using
	        // UTF-8 encoding.
	        boolean useCSS = true; // we want to use CSS style attributes
	        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
	        
	        //OutputStream out = httpServletResponse.getOutputStream();
			//httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
			//httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + requestedFile + "\"");
	        Writer out = new OutputStreamWriter(System.out, "UTF-8");

	        //Create an instance of the SVG Generator.
	        SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);
	        svgGenerator.stream(out, useCSS);
	        

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}
	

}
