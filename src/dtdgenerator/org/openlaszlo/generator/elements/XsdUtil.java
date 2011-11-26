package org.openlaszlo.generator.elements;

import java.util.SortedSet;
import java.util.TreeSet;

public class XsdUtil {

	public static String xsdPrefix = "xs";

	public static String xsdProjectPrefix = "lzx";

	public static String nameSpace = "http://www.laszlosystems.com/2003/05/lzx";

	public static String tabSpace = "  ";

	private final SortedSet<ClassElement> rootElements = new TreeSet<ClassElement>();

	public void registerRootElements(ClassElement classElement) {
		rootElements.add(classElement);
	}

	public void writeTopLevelElements(StringBuilder sb) throws Exception {

		sb.append("<" + XsdUtil.xsdPrefix
				+ ":group name=\"toplevelElements\">\n");

	}

	public void generateXsdHeader(StringBuilder sb) throws Exception {

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		sb.append("<" + XsdUtil.xsdPrefix + ":schema ");
		sb.append("xmlns:" + XsdUtil.xsdPrefix
				+ "=\"http://www.w3.org/2001/XMLSchema\" ");
		sb.append("elementFormDefault=\"qualified\" \n");
		sb.append(XsdUtil.tabSpace + "targetNamespace=\"" + XsdUtil.nameSpace
				+ "\" \n");
		sb.append(XsdUtil.tabSpace + "xmlns:" + XsdUtil.xsdProjectPrefix
				+ "=\"" + XsdUtil.nameSpace + "\">\n");

	}

	public void generateXsdFooter(StringBuilder sb) throws Exception {
		sb.append("</" + XsdUtil.xsdPrefix + ":schema>\n");
	}

	public void writeComplexType(StringBuilder sb, String className,
			ClassElement classElement) throws Exception {

		sb.append("<" + XsdUtil.xsdPrefix + ":element name=\"" + className + "\" ");
		sb.append("type=\"" + xsdProjectPrefix + ":" + className + "\" />\n");
		
		sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix
				+ ":complexType name=\"" + className + "\">\n");

		// Write Attributes to Element
		writeAttributes(sb, classElement);

		sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix
				+ ":complexType>\n");

	}

	private void writeAttributes(StringBuilder sb, ClassElement classElement)
			throws Exception {
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix +":complexContent>\n");
	
			sb.append(XsdUtil.tabSpace + "<" + XsdUtil.xsdPrefix
					+ ":extension base=\"" + xsdProjectPrefix + ":" + classElement.getParentAsString()
					+ "\">\n");
		}
		
		for (ClassAttribute classAttribute : classElement.getAttributes()) {

			sb.append(XsdUtil.tabSpace + XsdUtil.tabSpace + XsdUtil.tabSpace);

			sb.append("<" + XsdUtil.xsdPrefix + ":attribute name=\""
					+ classAttribute.getName() + "\" type=\"xs:string\"/>\n");

		}
		
		if (!classElement.getParentAsString().equals("")) {
			sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix + ":extension>\n");
			
			sb.append(XsdUtil.tabSpace + "</" + XsdUtil.xsdPrefix +":complexContent>\n");
		}
		
	}

}
