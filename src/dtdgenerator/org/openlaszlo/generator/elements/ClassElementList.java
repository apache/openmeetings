package org.openlaszlo.generator.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.xml.sax.Attributes;

public class ClassElementList {

	// Using natural order ?!
	private Map<String, ClassElement> elementList = new HashMap<String, ClassElement>();
	
	public void addClassElement(String name, String parent) {
		ClassElement element = elementList.get(name);
		
		if (element == null) {
			element = new ClassElement();
		}
		
		element.setParentAsString(parent);
		elementList.put(name, element);
		
	}
	
	public void addClassAttribute(String name, boolean required, String className) throws Exception {
		ClassElement element = elementList.get(className);
		
		if (element == null) {
			throw new Exception("Class not available "+className+ " "+name);
		}
		
		element.getAttributes().add(new ClassAttribute(name, required));
	}
	
	private void debugPrint() {
		
		for (Entry<String, ClassElement> entry : elementList
				.entrySet()) {
			
			String className = entry.getKey();
			ClassElement element = entry.getValue();
			
			System.out.println("TAG "+className+ " " + element.getParentAsString());
			
			String tString = "   -> Attributes: ";
			for (ClassAttribute attr : element.getAllClassAttributes()) {
				tString += " "+attr.getName();
			}
			
			System.out.println(tString);
			
		}
	}

	public void fixParents() {
		
		for (Entry<String, ClassElement> entry : elementList
				.entrySet()) {
			
			ClassElement element = entry.getValue();
			String className = entry.getKey();
			
			String parentAsString = element.getParentAsString();
			if (parentAsString.length() > 0) {
				ClassElement parent = elementList.get(parentAsString);
				
				if (parent == null) {
					System.err.println("Could not find parent "+parentAsString+ " Classname " +className);
				}
				
				element.setParent(parent);
				
			} else {
				
			}
			
		}

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
			File f = new File("lzx.dtd");
			if (f.exists()){
				f.delete();
			}
			
			this.fixParents();
			
			f.createNewFile();
			
			OutputStream ou = new FileOutputStream(f);
			
			for (Entry<String, ClassElement> entry : elementList
					.entrySet()) {
				
				String className = entry.getKey();
				ClassElement element = entry.getValue();
				
				StringBuilder sBuilder = new StringBuilder();
				
				if (checkAllowSingleTextNode(className)) {
					sBuilder.append("<!ELEMENT " + className + " ( #PCDATA ) > \n");
				} else {
					sBuilder.append("<!ELEMENT " + className + " ANY > \n");
				}
				
				
				if (element.getAllClassAttributes().size() > 0) {
					sBuilder.append("<!ATTLIST " + className + " \n");
		
					for (ClassAttribute attr : element.getAllClassAttributes()) {
						sBuilder.append("    " + attr.getName() + " CDATA  #IMPLIED \n");
					}
					sBuilder.append(">\n");
				}
				
				if (debug) {
					System.out.print(sBuilder);
				}
				
				ou.write(sBuilder.toString().getBytes());
				
			}
			
//			for (Entry<String, Element> entry : elementList
//					.entrySet()) {
//				
//				String key = entry.getKey();
//				Element element = entry.getValue();
//				
//				StringBuilder sBuilder = new StringBuilder();
//
//				if (element.getChildelements().size()>0) {
//					sBuilder.append("<!ELEMENT " + key + " ( ");
//					int i = 0;
//					for (String child : element.getChildelements()) {
//						if (i!=0) {
//							sBuilder.append(" |");
//						}
//						sBuilder.append(" "+child+"");
//						i++;
//					}
//					sBuilder.append(" )* >\n");
//				} else {
//					
//					if (checkAllowSingleTextNode(key)) {
//						sBuilder.append("<!ELEMENT " + key + " ( #PCDATA ) > \n");
//					} else {
//						sBuilder.append("<!ELEMENT " + key + " EMPTY > \n");
//					}
//					
//				}
//				
//				if (element.getAttributes().size() > 0) {
//					sBuilder.append("<!ATTLIST " + key + " \n");
//		
//					for (String attribute : element.getAttributes()) {
//						sBuilder.append("    " + attribute + " CDATA  #IMPLIED \n");
//					}
//					sBuilder.append(">\n");
//				}
//				
//				if (debug) {
//					System.out.print(sBuilder);
//				}
//				
//				ou.write(sBuilder.toString().getBytes());
//
//			}
			
			ou.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
