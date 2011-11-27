package org.openlaszlo.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openlaszlo.generator.elements.ClassAttribute;
import org.openlaszlo.generator.elements.ClassElement;
import org.w3c.dom.Document;

import com.thaiopensource.relaxng.translate.Driver;

public class GenerateXsd {
	
	public static void main(String... args) {
		new GenerateXsd();
	}
	
	public GenerateXsd() {
		
		//Driver driver = new Driver();
		
		String[] args = { "test/lzx.rnc", "test/lzx_by_rnc.xsd" };
		
		Driver.main(args);
		
	}
	
	private DocumentBuilder documentBuilder;
	private TransformerFactory transformerFactory;
	private Transformer transformer;

	public void xsdPrint(String fileName) {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			documentBuilder = dbf.newDocumentBuilder();
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			Document schema = documentBuilder.newDocument();

			schema.appendChild(schema.createElement("schema"));

			ClassElement canvas = new ClassElement();
			canvas.getAllClassAttributes().add(
					new ClassAttribute("width", false, "", null, ""));

			this.addElement(schema, "canvas", canvas);
			
			
			
			StringWriter sw = new StringWriter();
			
			transformer.transform(new DOMSource(schema), new StreamResult(sw));
			
			System.out.println(sw);
			
			File f = new File(fileName+".xml");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			OutputStream ou = new FileOutputStream(f);
			ou.write(sw.toString().getBytes());
			
			String[] args = { fileName+".xml", fileName+".xsd" };
			
			Driver.main(args);
			
		} catch (Exception err) {
			err.printStackTrace();
		}

	}

	private void addElement(Document document, String name,
			ClassElement classElement) {

		org.w3c.dom.Element e = document.createElement(name);

		for (ClassAttribute attr : classElement.getAllClassAttributes()) {

			e.setAttribute(attr.getName(), "string");

		}

		document.getDocumentElement().appendChild(e);

	}

}
