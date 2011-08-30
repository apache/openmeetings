package org.openmeetings.test.batik;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestSVGExporter extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestSVGExporter.class);
	
	@Test
	public void testGetDiagramList() {
		try {
			
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
//	        exportToImageTest.paintTextByWidthHeight(svgGenerator8, 500, 300, 100, 200, "Process 1 asd asd as dasas " +
//	        		"	dasdasdasda sdasdad a  das dasdas dasdasdasd Process 1 asd asd as dasas dasdasdasdasdasdad a  das dasd" +
//	        		"	asdasdasdasd Process 1 asd asd as dasasdasdasdasdasdasdad a  das dasdasdasdasdasd", Font.BOLD, 28,
//	        		new Color(255,0,0));
//	        
//	        //Draw a Painting
//	        SVGGraphics2D svgGenerator1 = new SVGGraphics2D(svgGenerator);
//	        //SVGGraphics2D svgGenerator2 = new SVGGraphics2D(document);
//	        
//	        HashMap pointsList = new HashMap();
//	        HashMap point = new HashMap();
//	        point.put(0, "point");
//	        point.put(1, 10);
//	        point.put(2, 20);
//	        point.put(3, 40);
//	        point.put(4, 50);
//	        pointsList.put(0, point);
//	        
//	        HashMap point2 = new HashMap();
//	        point2.put(0, "point");
//	        point2.put(1, 40);
//	        point2.put(2, 50);
//	        point2.put(3, 10);
//	        point2.put(4, 200);
//	        pointsList.put(1, point2);
//	        
//	        exportToImageTest.drawPointsObject(svgGenerator1, pointsList, new Color(255,0,0), 3, 0, 0, new Float(0.2));
//	        
//	        //Draw a Rect without fill
//	        SVGGraphics2D svgGenerator3 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintRect2D(svgGenerator3, 10, 300, 100, 40, new Color(255,0,0),1,null,new Float(0.5));
//	        //Draw rect without fill but with bigger border
//	        SVGGraphics2D svgGenerator4 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintRect2D(svgGenerator4, 10, 350, 100, 40, new Color(0,255,0),5,null,new Float(0.5));
//	        
//	        //Draw rect with fill and thick border
//	        SVGGraphics2D svgGenerator5 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintRect2D(svgGenerator5, 10, 400, 100, 40, new Color(0,255,0),5,new Color(0,0,255),new Float(0.5));
//	        
//	        //Draw rect with fill and without border
//	        SVGGraphics2D svgGenerator6 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintRect2D(svgGenerator6, 10, 450, 100, 40, null,5,new Color(255,0,255),new Float(0.5));
//	        
//	        
//	        //Draw a Ellipse without fill
//	        SVGGraphics2D svgGenerator11 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintEllipse2D(svgGenerator11, 110, 300, 100, 40, new Color(255,0,0),1,null,new Float(0.5));
//	        //Draw Ellipse without fill but with bigger border
//	        SVGGraphics2D svgGenerator12 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintEllipse2D(svgGenerator12, 110, 350, 100, 40, new Color(0,255,0),5,null,new Float(0.5));
//	        
//	        //Draw Ellipse with fill and thick border
//	        SVGGraphics2D svgGenerator13 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintEllipse2D(svgGenerator13, 110, 400, 100, 40, new Color(0,255,0),5,new Color(0,0,255),new Float(0.5));
//	        
//	        //Draw Ellipse with fill and without border
//	        SVGGraphics2D svgGenerator14 = new SVGGraphics2D(svgGenerator);
//	        exportToImageTest.paintEllipse2D(svgGenerator14, 110, 450, 100, 40, null,5,new Color(255,0,255),new Float(0.5));
	        
	        // Finally, stream out SVG to the standard output using
	        // UTF-8 encoding.
	        boolean useCSS = true; // we want to use CSS style attributes
	        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
	        
	        String requestedFile = "diagram_xyz_"+new Date().getTime()+".svg";
	        
	        //OutputStream out = httpServletResponse.getOutputStream();
			//httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
			//httpServletResponse.setHeader("Content-Disposition","attachment; filename=\"" + requestedFile + "\"");
	        Writer out = new OutputStreamWriter(System.out, "UTF-8");

	        
	        svgGenerator.stream(out, useCSS);

			
		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}

}
