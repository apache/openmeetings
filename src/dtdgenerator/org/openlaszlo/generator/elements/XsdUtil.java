package org.openlaszlo.generator.elements;

import java.util.SortedSet;
import java.util.TreeSet;

public class XsdUtil {

	public static String xsdPrefix = "xs";

	public static String xsdProjectPrefix = "lzx";

	public static String nameSpace = "http://localhost/openlaszlo/lzx";

	public static String tabSpace = "  ";
	
	public static String topLevelElementName = "topLevelElements";

	private final SortedSet<ClassElement> allowedElements = new TreeSet<ClassElement>();

	public void registerAllowedSubElement(ClassElement classElement) {
		allowedElements.add(classElement);
	}
	
	public void writeBaseAllowedSubElements(StringBuilder sb) throws Exception {

		sb.append("<" + XsdUtil.xsdPrefix
				+ ":group name=\""+ XsdUtil.topLevelElementName +"\">\n");
		sb.append("<" + XsdUtil.xsdPrefix +":sequence>\n");
		sb.append("<" + XsdUtil.xsdPrefix +":choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		
		sb.append("<" + XsdUtil.xsdPrefix +":any />\n");
		
		sb.append("</" + XsdUtil.xsdPrefix +":choice>\n");
		sb.append("</" + XsdUtil.xsdPrefix +":sequence>\n");
		sb.append("</" + XsdUtil.xsdPrefix +":group>\n");
		

	}

	public void writeAllowedSubElements(StringBuilder sb) throws Exception {

		sb.append("<" + XsdUtil.xsdPrefix
				+ ":group name=\""+ XsdUtil.topLevelElementName +"\">\n");
		sb.append("<" + XsdUtil.xsdPrefix +":sequence>\n");
		sb.append("<" + XsdUtil.xsdPrefix +":choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n");
		
		for(ClassElement cl : allowedElements) {
			
			sb.append("<" + XsdUtil.xsdPrefix +":element ref=\""+XsdUtil.xsdProjectPrefix+":"+cl.getName()+"\"/>\n");
			
		}
		
		sb.append("</" + XsdUtil.xsdPrefix +":choice>\n");
		sb.append("</" + XsdUtil.xsdPrefix +":sequence>\n");
		sb.append("</" + XsdUtil.xsdPrefix +":group>\n");
		

	}

	public void writeXsdHeader(StringBuilder sb) throws Exception {

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		sb.append("<" + XsdUtil.xsdPrefix + ":schema ");
		sb.append("xmlns:" + XsdUtil.xsdPrefix + "=\"http://www.w3.org/2001/XMLSchema\" ");
		sb.append("elementFormDefault=\"qualified\" \n");
		sb.append(XsdUtil.tabSpace + "targetNamespace=\"" + XsdUtil.nameSpace + "\" \n");
		sb.append(XsdUtil.tabSpace + "xmlns:" + XsdUtil.xsdProjectPrefix + "=\"" + XsdUtil.nameSpace + "\">\n");

	}

	public void generateXsdFooter(StringBuilder sb) throws Exception {
		sb.append("</" + XsdUtil.xsdPrefix + ":schema>\n");
	}

	public void writeComplexType(StringBuilder sb, String className,
			ClassElement classElement) throws Exception {

		sb.append("<" + XsdUtil.xsdPrefix + ":element name=\"" + className + "\" ");
		sb.append("type=\"" + xsdProjectPrefix + ":" + className + "\" />\n");
		
		sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix
				+ ":complexType name=\"" + className + "\" mixed=\"true\">\n");

		// Write Attributes to Element
		writeAttributes(sb, classElement);

		sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix
				+ ":complexType>\n");

	}
	
	public static void main(String... args) {
		
		String t = "this.othersb && this.othersb.visible";
		
		String t2 = t.replaceAll("&", "&amp;");
		
		System.out.println(t);
		System.out.println(t2);
		
	}

	private void writeAttributes(StringBuilder sb, ClassElement classElement)
			throws Exception {
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix +":complexContent>\n");
	
			sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix
					+ ":extension base=\"" + xsdProjectPrefix + ":" + classElement.getParentAsString()
					+ "\">\n");
		}
		
		if (classElement.getName().equals("node")) {
			sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix +":group ref=\"" + xsdProjectPrefix + ":"+ XsdUtil.topLevelElementName +"\" />\n");
		}
		
		for (ClassAttribute classAttribute : classElement.getAttributes()) {

			sb.append(XsdUtil.tabSpace + XsdUtil.tabSpace + XsdUtil.tabSpace);

			sb.append("<" + XsdUtil.xsdPrefix + ":attribute name=\"" + classAttribute.getName() + "\" ");
			
			sb.append("type=\"" + XsdUtil.xsdPrefix + ":string\" ");
			
			if (classAttribute.isRequired()) {
				sb.append("use=\"required\" ");
			}
			
			if (classAttribute.getDefaultValue() != null) {
				sb.append("default=\""+classAttribute.getDefaultValue()
						.replaceAll("&", "&amp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						+"\" ");
			}
			
			sb.append("/>\n");

		}
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix + ":extension>\n");
			
			sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix +":complexContent>\n");
		}
		
	}

}
