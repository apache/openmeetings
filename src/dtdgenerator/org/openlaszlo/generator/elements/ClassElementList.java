package org.openlaszlo.generator.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClassElementList {

	// Using natural order ?!
	private final Map<String, ClassElement> elementList = new HashMap<String, ClassElement>();

	public void addClassElement(String name, String parent, boolean isRoot) {
		ClassElement element = elementList.get(name);

		if (element == null) {
			element = new ClassElement();
			element.setName(name);
			element.setRoot(isRoot);
		}

		element.setParentAsString(parent);
		elementList.put(name, element);

	}

	public void addClassAttribute(String name, boolean required,
			String className) throws Exception {
		ClassElement element = elementList.get(className);

		if (element == null) {
			throw new Exception("Class not available " + className + " " + name);
		}

		element.getAttributes().add(new ClassAttribute(name, required));
	}

	public void fixParents() {

		for (Entry<String, ClassElement> entry : elementList.entrySet()) {

			ClassElement element = entry.getValue();
			String className = entry.getKey();

			String parentAsString = element.getParentAsString();
			if (parentAsString.length() > 0) {
				ClassElement parent = elementList.get(parentAsString);

				if (parent == null) {
					System.err.println("Could not find parent "
							+ parentAsString + " Classname " + className);
				}

				element.setParent(parent);

			} else {

			}

		}

	}

	private void generateBaseClassTag() {

		ClassElement element = new ClassElement();
		element.setParentAsString("");

		element.getAttributes().add(new ClassAttribute("extends", false));

		for (Entry<String, ClassElement> entry : elementList.entrySet()) {

			ClassElement elementTemp = entry.getValue();
			element.getAttributes().addAll(elementTemp.getAllClassAttributes());
		}

		elementList.put("class", element);

	}

	public final String[] TEXT_OPTION_ENABLED = { "handler", "method", "text" };

	private boolean checkAllowSingleTextNode(String key) {
		for (String textOption : TEXT_OPTION_ENABLED) {
			if (textOption.equals(key)) {
				return true;
			}
		}
		return false;
	}

	public void filePrint(boolean debug, String fileName, String baseDtd) {
		try {
			File f = new File(fileName);
			if (f.exists()) {
				f.delete();
			}

			this.fixParents();

			// this.generateBaseClassTag();

			f.createNewFile();

			OutputStream ou = new FileOutputStream(f);

			// ou.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes());

			if (baseDtd.length() > 0) {

				// Get file and handle download
				RandomAccessFile rf = new RandomAccessFile(baseDtd, "r");

				byte[] buffer = new byte[1024];
				int readed = -1;

				while ((readed = rf.read(buffer, 0, buffer.length)) > -1) {
					ou.write(buffer, 0, readed);
				}

				rf.close();

			}

			for (Entry<String, ClassElement> entry : elementList.entrySet()) {

				String className = entry.getKey();
				ClassElement element = entry.getValue();

				StringBuilder sBuilder = new StringBuilder();

				if (checkAllowSingleTextNode(className)) {
					sBuilder.append("<!ELEMENT " + className
							+ " ( #PCDATA ) > \n");
				} else {
					sBuilder.append("<!ELEMENT " + className + " ANY > \n");
				}

				if (element.getAllClassAttributes().size() > 0) {
					sBuilder.append("<!ATTLIST " + className + " \n");

					for (ClassAttribute attr : element.getAllClassAttributes()) {
						sBuilder.append("    " + attr.getName()
								+ " CDATA  #IMPLIED \n");
					}
					sBuilder.append(">\n");
				}

				if (debug) {
					System.out.print(sBuilder);
				}

				ou.write(sBuilder.toString().getBytes());

			}

			ou.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void xsdPrint(boolean debug, String fileName) {
		try {
			
			this.fixParents();
			
			File f = new File(fileName);
			if (f.exists()) {
				f.delete();
			}

			this.fixParents();

			// this.generateBaseClassTag();

			f.createNewFile();

			OutputStream ou = new FileOutputStream(f);
			
			StringBuilder headerBuilder = new StringBuilder();
			
			XsdUtil xsdUtil = new XsdUtil();
			
			xsdUtil.generateXsdHeader(headerBuilder);
			
			ou.write(headerBuilder.toString().getBytes());
			
			for (Entry<String, ClassElement> entry : elementList.entrySet()) {
				
			}


			for (Entry<String, ClassElement> entry : elementList.entrySet()) {

				String className = entry.getKey();
				ClassElement element = entry.getValue();
				
				element.clearDuplicated();

				StringBuilder sBuilder = new StringBuilder();

				xsdUtil.writeComplexType(sBuilder, className, element);

				if (debug) {
					System.out.print(sBuilder);
				}

				ou.write(sBuilder.toString().getBytes());

			}
			
			StringBuilder footerBuilder = new StringBuilder();
			
			xsdUtil.generateXsdFooter(footerBuilder);
			
			ou.write(footerBuilder.toString().getBytes());

			ou.close();
			
			
			
		} catch (Exception err) {
			
		}
	}
	

//	private DocumentBuilder documentBuilder;
//	private TransformerFactory transformerFactory;
//	private Transformer transformer;
//
//	public void xsdPrint(boolean b, String fileName) {
//		try {
//			this.fixParents();
//
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			documentBuilder = dbf.newDocumentBuilder();
//			transformerFactory = TransformerFactory.newInstance();
//			transformer = transformerFactory.newTransformer();
//			Document schema = documentBuilder.newDocument();
//
//			schema.appendChild(schema.createElement("schema"));
//
//			ClassElement canvas = new ClassElement();
//			canvas.getAllClassAttributes().add(
//					new ClassAttribute("width", false));
//
//			this.addElement(schema, "canvas", canvas);
//			
//			
//			
//			StringWriter sw = new StringWriter();
//			
//			transformer.transform(new DOMSource(schema), new StreamResult(sw));
//			
//			System.out.println(sw);
//			
//			File f = new File(fileName+".xml");
//			if (f.exists()) {
//				f.delete();
//			}
//			f.createNewFile();
//			OutputStream ou = new FileOutputStream(f);
//			ou.write(sw.toString().getBytes());
//			
//			String[] args = { fileName+".xml", fileName+".xsd" };
//			
//			Driver.main(args);
//			
//		} catch (Exception err) {
//			err.printStackTrace();
//		}
//
//	}
//
//	private void addElement(Document document, String name,
//			ClassElement classElement) {
//
//		org.w3c.dom.Element e = document.createElement(name);
//
//		for (ClassAttribute attr : classElement.getAllClassAttributes()) {
//
//			e.setAttribute(attr.getName(), "string");
//
//		}
//
//		document.getDocumentElement().appendChild(e);
//
//	}

}
