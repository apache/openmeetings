package org.openlaszlo.generator.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;

public class ElementList {

	// Using natural order ?!
	private final Map<String, Element> elementList = new HashMap<String, Element>();

	public void addElement(String name, Attributes attributes, String memoryLastElement) {
		
		if (memoryLastElement.length() > 0) {
			Element parentElement = elementList.get(memoryLastElement);
			if (parentElement == null) {
				new Exception("parentElement missing "+memoryLastElement);
				return;
			}
			if (parentElement.getChildelements() == null) {
				new Exception("getChildelements missing ");
			}
			parentElement.getChildelements().add(name);
		}

		Element element = elementList.get(name);
		
		if (element == null) {
			element = new Element();
		}
		
		for (int i = 0; i < attributes.getLength(); i++) {
			element.getAttributes().add(attributes.getQName(i));
		}

		elementList.put(name, element);

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

	public void filePrint(boolean debug) {
		try {
			File f = new File("project.dtd");
			if (f.exists()){
				f.delete();
			}
			f.createNewFile();
			
			OutputStream ou = new FileOutputStream(f);
			
			for (Entry<String, Element> entry : elementList
					.entrySet()) {
				
				String key = entry.getKey();
				Element element = entry.getValue();
				
				StringBuilder sBuilder = new StringBuilder();

				if (element.getChildelements().size()>0) {
					sBuilder.append("<!ELEMENT " + key + " ( ");
					int i = 0;
					for (String child : element.getChildelements()) {
						if (i!=0) {
							sBuilder.append(" |");
						}
						sBuilder.append(" "+child+"");
						i++;
					}
					sBuilder.append(" )* >\n");
				} else {
					
					if (checkAllowSingleTextNode(key)) {
						sBuilder.append("<!ELEMENT " + key + " ( #PCDATA ) > \n");
					} else {
						sBuilder.append("<!ELEMENT " + key + " EMPTY > \n");
					}
					
				}
				
				if (element.getAttributes().size() > 0) {
					sBuilder.append("<!ATTLIST " + key + " \n");
		
					for (String attribute : element.getAttributes()) {
						sBuilder.append("    " + attribute + " CDATA  #IMPLIED \n");
					}
					sBuilder.append(">\n");
				}
				
				if (debug) {
					System.out.print(sBuilder);
				}
				
				ou.write(sBuilder.toString().getBytes());

			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
